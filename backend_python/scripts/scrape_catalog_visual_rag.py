import argparse
import asyncio
import os
import sys
from datetime import datetime
from pathlib import Path
from urllib.parse import urljoin

import requests
from bs4 import BeautifulSoup
from dotenv import load_dotenv
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker
from sqlmodel import SQLModel, select

BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from app.models import AIKnowledgeChunk, AIVisualDish, Restaurante

USER_AGENT = "Mozilla/5.0 (compatible; BelozVisualRAG/0.1; +https://beloz.onrender.com)"

load_dotenv(BACKEND_ROOT / ".env")


def normalize_database_url(url: str) -> str:
    if url.startswith("postgres://"):
        return url.replace("postgres://", "postgresql+asyncpg://", 1)
    if url.startswith("postgresql://"):
        return url.replace("postgresql://", "postgresql+asyncpg://", 1)
    return url


DATABASE_URL = normalize_database_url(
    os.getenv("SEED_DATABASE_URL")
    or os.getenv("EXTERNAL_DATABASE_URL")
    or os.getenv("DATABASE_URL")
    or ""
)

if not DATABASE_URL:
    raise RuntimeError("Define SEED_DATABASE_URL, EXTERNAL_DATABASE_URL o DATABASE_URL.")


def clean_text(value: str) -> str:
    return " ".join((value or "").split())


def is_food_image(src: str) -> bool:
    lowered = src.lower()
    if not src or lowered.endswith(".svg"):
        return False
    banned = ("logo", "icon", "google-play", "app-store", "facebook", "instagram")
    return not any(token in lowered for token in banned)


def extract_cards(source_url: str, restaurant_name: str, category: str, limit: int) -> list[dict]:
    response = requests.get(source_url, headers={"User-Agent": USER_AGENT}, timeout=30)
    response.raise_for_status()
    soup = BeautifulSoup(response.text, "html.parser")
    dishes = []
    selectors = "article, li, .card, [class*=product], [class*=dish], [class*=item], [class*=carta]"

    for element in soup.select(selectors):
        image = element.find("img")
        image_url = ""
        if image:
            image_url = image.get("src") or image.get("data-src") or image.get("data-lazy-src") or ""
            image_url = urljoin(source_url, image_url)
        if not is_food_image(image_url):
            continue

        title_node = element.find(["h2", "h3", "h4", "strong"])
        title = clean_text(title_node.get_text(" ", strip=True) if title_node else image.get("alt", ""))
        text = clean_text(element.get_text(" ", strip=True)).replace(" Ver más", "")
        if title and text.lower().startswith(title.lower()):
            description = clean_text(text[len(title) :])
        else:
            description = text

        if not title or len(description) < 12:
            continue

        payload = {
            "restaurant_name": restaurant_name,
            "dish_name": title,
            "description": description,
            "category": category,
            "image_url": image_url,
            "dish_url": source_url,
            "source_url": source_url,
            "extraction_method": "catalog-dom-proximity",
            "confidence": 0.9,
            "tags": ",".join([restaurant_name, category, "reus", "visual rag", "catalog verified"]),
        }
        key = (payload["dish_name"], payload["image_url"])
        if key not in {(dish["dish_name"], dish["image_url"]) for dish in dishes}:
            dishes.append(payload)
        if len(dishes) >= limit:
            break
    return dishes


async def require_catalog_restaurant(session: AsyncSession, restaurant_name: str) -> Restaurante:
    result = await session.execute(
        select(Restaurante).where(Restaurante.name.ilike(f"%{restaurant_name}%"))
    )
    restaurant = result.scalar_one_or_none()
    if not restaurant:
        raise RuntimeError(
            f"No guardo RAG visual: '{restaurant_name}' no existe en la tabla restaurantes."
        )
    return restaurant


async def upsert_visual_dish(session: AsyncSession, payload: dict) -> str:
    result = await session.execute(
        select(AIVisualDish).where(
            AIVisualDish.restaurant_name == payload["restaurant_name"],
            AIVisualDish.dish_name == payload["dish_name"],
            AIVisualDish.source_url == payload["source_url"],
        )
    )
    row = result.scalar_one_or_none()
    if row:
        for key, value in payload.items():
            setattr(row, key, value)
        row.updated_at = datetime.utcnow()
        return "updated"
    session.add(AIVisualDish(**payload))
    return "inserted"


async def upsert_chunk(session: AsyncSession, payload: dict) -> str:
    result = await session.execute(
        select(AIKnowledgeChunk).where(
            AIKnowledgeChunk.restaurant_name == payload["restaurant_name"],
            AIKnowledgeChunk.title == payload["title"],
            AIKnowledgeChunk.source_url == payload["source_url"],
        )
    )
    row = result.scalar_one_or_none()
    if row:
        for key, value in payload.items():
            setattr(row, key, value)
        row.updated_at = datetime.utcnow()
        return "updated"
    session.add(AIKnowledgeChunk(**payload))
    return "inserted"


async def main() -> None:
    parser = argparse.ArgumentParser(description="Scrapea platos visuales solo para restaurantes del catalogo Beloz.")
    parser.add_argument("--restaurant", required=True, help="Nombre del restaurante existente en Beloz.")
    parser.add_argument("--url", required=True, help="URL oficial de carta o pagina de platos.")
    parser.add_argument("--category", default="carta", help="Categoria de carta.")
    parser.add_argument("--limit", type=int, default=30)
    args = parser.parse_args()

    dishes = extract_cards(args.url, args.restaurant, args.category, args.limit)
    if not dishes:
        raise RuntimeError("No se han encontrado tarjetas fiables con plato, descripcion e imagen.")

    connect_args = {"ssl": True} if "render.com" in DATABASE_URL else {}
    engine = create_async_engine(DATABASE_URL, echo=False, connect_args=connect_args)
    async with engine.begin() as conn:
        await conn.run_sync(SQLModel.metadata.create_all)

    async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    counts = {"inserted": 0, "updated": 0, "chunk_inserted": 0, "chunk_updated": 0}
    async with async_session() as session:
        restaurant = await require_catalog_restaurant(session, args.restaurant)
        catalog_name = restaurant.name
        for dish in dishes:
            dish["restaurant_name"] = catalog_name
            status = await upsert_visual_dish(session, dish)
            counts[status] += 1

        sample = "; ".join(f"{dish['dish_name']}: {dish['description']}" for dish in dishes[:6])
        chunk = {
            "restaurant_name": catalog_name,
            "category": "carta_visual",
            "title": f"Carta visual oficial de {catalog_name}",
            "content": (
                "Beloz ha extraido tarjetas de carta con nombre, descripcion e imagen desde una fuente oficial. "
                f"Ejemplos: {sample}"
            ),
            "source_url": args.url,
            "source_type": "web",
            "tags": ",".join([catalog_name, args.category, "reus", "visual rag", "pixel rag"]),
        }
        chunk_status = await upsert_chunk(session, chunk)
        counts[f"chunk_{chunk_status}"] += 1
        await session.commit()

    await engine.dispose()
    print(
        f"{catalog_name} visual RAG: {counts['inserted']} platos insertados, "
        f"{counts['updated']} actualizados; {counts['chunk_inserted']} chunks insertados, "
        f"{counts['chunk_updated']} actualizados."
    )


if __name__ == "__main__":
    asyncio.run(main())
