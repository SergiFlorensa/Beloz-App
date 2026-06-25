import asyncio
import os
import sys
from datetime import datetime
from pathlib import Path

from dotenv import load_dotenv
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from sqlmodel import SQLModel, select

BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from app.models import AIKnowledgeChunk


load_dotenv()


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
    raise RuntimeError("Define SEED_DATABASE_URL, EXTERNAL_DATABASE_URL o DATABASE_URL para insertar el RAG.")


CHUNKS = [
    {
        "restaurant_name": "La Piemontesa Reus",
        "category": "restaurante",
        "title": "Descripcion oficial del local",
        "content": (
            "La Piemontesa Reus se presenta como un restaurante ambientado en una fabrica de harina, "
            "cerca del casco antiguo de Reus, con estilo rustico y encanto. La web oficial destaca que "
            "el local esta reconvertido en restaurante y sorprende por su decoracion e historia."
        ),
        "source_url": "https://www.lapiemontesa.com/restaurantes-comida-italiana/la-piemontesa-reus",
        "source_type": "web",
        "tags": "la piemontesa,reus,italiana,pasta,pizza,restaurante con encanto,casco antiguo",
    },
    {
        "restaurant_name": "La Piemontesa Reus",
        "category": "carta",
        "title": "Carta oficial 2025",
        "content": (
            "La carta oficial 2025 de La Piemontesa esta publicada en formato digital. "
            "Incluye carta principal, postres e informacion de alergenos en documentos oficiales enlazados "
            "desde la propia web del restaurante."
        ),
        "source_url": "https://carta.lapiemontesa.com/cartalapiemontesa/2025V1/",
        "source_type": "web",
        "tags": "carta,menu,2025,postres,alergenos,pasta,pizza,italiana",
    },
    {
        "restaurant_name": "La Piemontesa Reus",
        "category": "carta fusion",
        "title": "Carta La Piemontesa Fusion 2025",
        "content": (
            "La Piemontesa ofrece una carta Fusion 2025 en castellano con acceso a carta principal, "
            "postres y documento de alergenos. La carta visual esta disponible como imagen/PDF oficial, "
            "por lo que para indexar plato por plato haria falta OCR o una fuente textual del restaurante."
        ),
        "source_url": "https://carta.lapiemontesa.com/cartalapiemontesa/fusion2025V1/",
        "source_type": "web",
        "tags": "fusion,carta,menu,postres,alergenos,italiana",
    },
    {
        "restaurant_name": "La Piemontesa Reus",
        "category": "alergenos",
        "title": "Informacion oficial de alergenos",
        "content": (
            "El manual oficial de alergenos indica que La Piemontesa actualiza la informacion alergena "
            "mediante revisiones constantes y que todos sus restaurantes disponen de un sistema de gestion "
            "y evaluacion de productos que pueden producir alergias o intolerancias. Tambien advierte que "
            "no puede asegurar ausencia total de trazas por existir una unica linea de produccion."
        ),
        "source_url": "https://carta.lapiemontesa.com/cartalapiemontesa/fusion2025V1/PDF%20Docs/La%20Piemontesa%20fusion%20Alergenos%20ES.pdf",
        "source_type": "pdf",
        "tags": "alergenos,intolerancias,trazas,gluten,huevo,leche,pescado,moluscos,crustaceos,soja,frutos de cascara",
    },
    {
        "restaurant_name": "La Piemontesa Reus",
        "category": "promociones",
        "title": "Promociones y actualidad",
        "content": (
            "La seccion oficial de actualidad de La Piemontesa publica promociones, sorteos y novedades. "
            "En el historico aparecen acciones como pizzas a precio unico los miercoles, festival de la pasta "
            "los lunes, sorteos y regalos vinculados a fechas especiales. Debe verificarse vigencia antes de "
            "mostrar una promocion como activa."
        ),
        "source_url": "https://www.lapiemontesa.com/actualidad-la-piemontesa",
        "source_type": "web",
        "tags": "promociones,ofertas,sorteos,pizza,pasta,actualidad,vigencia",
    },
    {
        "restaurant_name": "La Piemontesa Reus",
        "category": "uso_chat",
        "title": "Como debe responder Beloz AI",
        "content": (
            "Cuando un usuario pregunte por La Piemontesa, Beloz AI debe responder con cautela: puede informar "
            "que existe carta oficial, carta fusion, postres y alergenos, enlazar la fuente y recomendar verificar "
            "precios/promociones vigentes. No debe inventar precios ni platos no extraidos de fuente textual."
        ),
        "source_url": "https://www.lapiemontesa.com/restaurantes-comida-italiana/la-piemontesa-reus",
        "source_type": "policy",
        "tags": "rag,chat,fuentes,precios,verificar,no inventar",
    },
]


async def upsert_chunk(session: AsyncSession, payload: dict) -> str:
    statement = select(AIKnowledgeChunk).where(
        AIKnowledgeChunk.restaurant_name == payload["restaurant_name"],
        AIKnowledgeChunk.title == payload["title"],
        AIKnowledgeChunk.source_url == payload["source_url"],
    )
    result = await session.execute(statement)
    chunk = result.scalar_one_or_none()

    if chunk:
        for key, value in payload.items():
            setattr(chunk, key, value)
        chunk.updated_at = datetime.utcnow()
        return "updated"

    session.add(AIKnowledgeChunk(**payload))
    return "inserted"


async def main() -> None:
    connect_args = {"ssl": True} if "render.com" in DATABASE_URL else {}
    engine = create_async_engine(DATABASE_URL, echo=False, connect_args=connect_args)
    async with engine.begin() as conn:
        await conn.run_sync(SQLModel.metadata.create_all)

    async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    counts = {"inserted": 0, "updated": 0}
    async with async_session() as session:
        for payload in CHUNKS:
            status = await upsert_chunk(session, payload)
            counts[status] += 1
        await session.commit()
    await engine.dispose()
    print(f"AI knowledge chunks La Piemontesa: {counts['inserted']} insertados, {counts['updated']} actualizados.")


if __name__ == "__main__":
    asyncio.run(main())
