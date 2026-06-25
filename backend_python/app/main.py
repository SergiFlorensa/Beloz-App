import os
import re
from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.ext.asyncio import AsyncSession
from pydantic import Field as PydanticField
from sqlmodel import select
from typing import List, Optional
from datetime import datetime
from passlib.context import CryptContext

from .database import get_session, engine
from .models import (
    AIKnowledgeChunk,
    AIVisualDish,
    DetallePedido,
    PaymentInfo,
    Pedido,
    Plato,
    Restaurante,
    SQLModel,
    User,
)

# Security
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

app = FastAPI(title="Beloz Unified Backend", version="1.0.0")

allowed_origins = [
    origin.strip()
    for origin in os.getenv("CORS_ORIGINS", "*").split(",")
    if origin.strip()
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.on_event("startup")
async def on_startup():
    if os.getenv("AUTO_CREATE_TABLES", "true").lower() == "true":
        async with engine.begin() as conn:
            await conn.run_sync(SQLModel.metadata.create_all)

# Helper functions
def hash_password(password: str):
    return pwd_context.hash(password)

def verify_password(plain_password: str, hashed_password: str):
    return pwd_context.verify(plain_password, hashed_password)

# --- AUTH ENDPOINTS ---

@app.post("/api/auth/register")
async def register(user: User, session: AsyncSession = Depends(get_session)):
    user.password_hash = hash_password(user.password_hash)
    session.add(user)
    try:
        await session.commit()
        await session.refresh(user)
        return {
            "id": user.id,
            "email": user.email,
            "name": user.name,
            "surname": user.surname,
            "num_telefono": user.num_telefono,
        }
    except Exception as e:
        await session.rollback()
        raise HTTPException(status_code=400, detail="Error al registrar usuario: " + str(e))

@app.post("/api/auth/login")
async def login(credentials: dict, session: AsyncSession = Depends(get_session)):
    email = credentials.get("email")
    password = credentials.get("password")

    statement = select(User).where(User.email == email)
    result = await session.execute(statement)
    user = result.scalar_one_or_none()

    if not user or not verify_password(password, user.password_hash):
        raise HTTPException(status_code=401, detail="Credenciales inválidas")

    return {
        "message": "Login exitoso",
        "user_id": user.id,
        "id": user.id,
        "email": user.email,
        "name": user.name,
        "surname": user.surname,
        "num_telefono": user.num_telefono,
    }

# --- RESTAURANT ENDPOINTS ---

@app.get("/api/restaurantes", response_model=List[Restaurante])
async def get_restaurantes(country: Optional[str] = None, session: AsyncSession = Depends(get_session)):
    statement = select(Restaurante)
    if country:
        statement = statement.where(Restaurante.country == country)
    result = await session.execute(statement)
    return result.scalars().all()

@app.get("/api/restaurantes/populares", response_model=List[Restaurante])
async def get_populares(session: AsyncSession = Depends(get_session)):
    statement = select(Restaurante).where(Restaurante.es_popular == True).order_by(Restaurante.relevancia.desc())
    result = await session.execute(statement)
    return result.scalars().all()

@app.get("/api/restaurantes/{restaurante_id}/platos", response_model=List[Plato])
async def get_platos_por_restaurante(restaurante_id: int, session: AsyncSession = Depends(get_session)):
    statement = select(Plato).where(Plato.restaurante_id == restaurante_id)
    result = await session.execute(statement)
    return result.scalars().all()

@app.get("/api/platos", response_model=List[Plato])
async def get_all_platos(restaurante_id: Optional[int] = None, session: AsyncSession = Depends(get_session)):
    statement = select(Plato)
    if restaurante_id:
        statement = statement.where(Plato.restaurante_id == restaurante_id)
    result = await session.execute(statement)
    return result.scalars().all()

# --- ORDERS ---

@app.post("/api/pedidos/crear")
async def crear_pedido(pedido: Pedido, session: AsyncSession = Depends(get_session)):
    session.add(pedido)
    await session.commit()
    await session.refresh(pedido)
    return pedido

@app.post("/api/pedidos/{pedido_id}/detalles", response_model=List[DetallePedido])
async def crear_detalles_pedido(
    pedido_id: int,
    detalles: List[DetallePedido],
    session: AsyncSession = Depends(get_session),
):
    detalles_guardados = []
    for detalle in detalles:
        detalle.pedido_id = pedido_id
        session.add(detalle)
        detalles_guardados.append(detalle)

    await session.commit()
    for detalle in detalles_guardados:
        await session.refresh(detalle)
    return detalles_guardados

@app.get("/api/pedidos", response_model=List[Pedido])
async def get_pedidos_por_usuario(user_id: int, session: AsyncSession = Depends(get_session)):
    statement = select(Pedido).where(Pedido.user_id == user_id).order_by(Pedido.fecha.desc())
    result = await session.execute(statement)
    return result.scalars().all()

@app.get("/api/pedidos/{pedido_id}/detalles")
async def get_detalles_pedido(pedido_id: int, session: AsyncSession = Depends(get_session)):
    statement = (
        select(DetallePedido, Plato, Restaurante)
        .join(Plato, DetallePedido.plato_id == Plato.id)
        .join(Restaurante, Plato.restaurante_id == Restaurante.id)
        .where(DetallePedido.pedido_id == pedido_id)
    )
    result = await session.execute(statement)
    return [
        {
            "id_detalle": detalle.id_detalle,
            "pedido_id": detalle.pedido_id,
            "plato_id": detalle.plato_id,
            "cantidad": detalle.cantidad,
            "precio": detalle.precio,
            "plato_nombre": plato.name,
            "restaurante_nombre": restaurante.name,
        }
        for detalle, plato, restaurante in result.all()
    ]

@app.get("/api/pagos/{user_id}", response_model=Optional[PaymentInfo])
async def get_pago_usuario(user_id: int, session: AsyncSession = Depends(get_session)):
    statement = select(PaymentInfo).where(PaymentInfo.user_id == user_id)
    result = await session.execute(statement)
    return result.scalar_one_or_none()

@app.put("/api/pagos/{user_id}", response_model=PaymentInfo)
async def guardar_pago_usuario(
    user_id: int,
    datos_pago: PaymentInfo,
    session: AsyncSession = Depends(get_session),
):
    statement = select(PaymentInfo).where(PaymentInfo.user_id == user_id)
    result = await session.execute(statement)
    existente = result.scalar_one_or_none()

    if existente:
        existente.nombre_titular = datos_pago.nombre_titular
        existente.numero_tarjeta_encriptado = datos_pago.numero_tarjeta_encriptado
        existente.iv = datos_pago.iv
        existente.fecha_expiracion = datos_pago.fecha_expiracion
        existente.tipo_tarjeta = datos_pago.tipo_tarjeta
        existente.metodo_pago_predeterminado = datos_pago.metodo_pago_predeterminado
        pago = existente
    else:
        datos_pago.user_id = user_id
        session.add(datos_pago)
        pago = datos_pago

    await session.commit()
    await session.refresh(pago)
    return pago

# --- IA RECOMMENDATIONS (Enhanced) ---

class ContextoEntrada(SQLModel):
    momento_del_dia: str
    tipo_de_dia: str
    clima: Optional[str] = None
    user_id: Optional[int] = None

class Sugerencia(SQLModel):
    titulo: str
    descripcion: str
    etiquetas: List[str]
    motivo: Optional[str] = None

class ChatRequest(SQLModel):
    message: str
    perfil_sabor: Optional[dict] = None

class ChatSuggestion(SQLModel):
    restaurante_id: int
    restaurante_nombre: str
    image_path: Optional[str] = None
    plato: Optional[str] = None
    price: Optional[float] = None
    wait_time: Optional[int] = None
    type_of_food: Optional[str] = None
    motivo: Optional[str] = None

class ChatResponse(SQLModel):
    provider: str
    respuesta: str
    accion: str = "responder"
    sugerencias: List[ChatSuggestion] = PydanticField(default_factory=list)

@app.post("/api/recomendaciones", response_model=List[Sugerencia])
async def generar_recomendaciones(contexto: ContextoEntrada, session: AsyncSession = Depends(get_session)):
    recomendaciones = []

    # 1. Inteligencia basada en Historial (Si hay user_id)
    if contexto.user_id:
        # Aquí podríamos consultar los últimos pedidos del usuario para personalizar
        pass

    # 2. Inteligencia basada en Contexto (Radar Emocional)
    if contexto.momento_del_dia.lower() == "noche" and contexto.tipo_de_dia == "fin_de_semana":
        recomendaciones.append(Sugerencia(
            titulo="Plan nocturno sin estrés",
            descripcion="Comparte pizzas y postres con tu grupo. Añade bebidas frías.",
            etiquetas=["compartir", "confort"],
            motivo="Es fin de semana por la noche, ideal para desconectar."
        ))

    # Fallback si no hay nada específico
    if not recomendaciones:
        recomendaciones.append(Sugerencia(
            titulo="Explora algo nuevo",
            descripcion="Prueba restaurantes con alta valoración cerca de ti.",
            etiquetas=["explorar"],
            motivo="Siempre es buen momento para descubrir nuevos sabores."
        ))

    return recomendaciones

STOPWORDS = {
    "para", "pero", "como", "quiero", "tengo", "algo", "dame", "sobre",
    "esta", "este", "esto", "con", "los", "las", "una", "uno", "del",
    "por", "que", "hay", "tiene", "cual", "cuales", "info", "informacion",
}

def normalizar_tokens(texto: str) -> set[str]:
    tokens = re.findall(r"[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9]+", texto.lower())
    return {token for token in tokens if len(token) > 2 and token not in STOPWORDS}

def puntuar_chunk(query_tokens: set[str], chunk: AIKnowledgeChunk) -> int:
    haystack = " ".join([
        chunk.restaurant_name,
        chunk.category,
        chunk.title,
        chunk.content,
        chunk.tags,
    ]).lower()
    score = sum(3 if token in chunk.title.lower() else 1 for token in query_tokens if token in haystack)
    if "piemontesa" in query_tokens and "piemontesa" in chunk.restaurant_name.lower():
        score += 6
    return score

async def buscar_chunks_rag(message: str, session: AsyncSession, limit: int = 4) -> List[AIKnowledgeChunk]:
    query_tokens = normalizar_tokens(message)
    result = await session.execute(select(AIKnowledgeChunk))
    chunks = result.scalars().all()
    scored = [
        (puntuar_chunk(query_tokens, chunk), chunk)
        for chunk in chunks
    ]
    return [chunk for score, chunk in sorted(scored, key=lambda item: item[0], reverse=True) if score > 0][:limit]

async def buscar_platos_visuales(message: str, session: AsyncSession, limit: int = 3) -> List[AIVisualDish]:
    query_tokens = normalizar_tokens(message)
    result = await session.execute(select(AIVisualDish))
    dishes = result.scalars().all()
    scored = []
    for dish in dishes:
        name = dish.dish_name.lower()
        description = dish.description.lower()
        category = dish.category.lower()
        tags = dish.tags.lower()
        restaurant = dish.restaurant_name.lower()
        score = 0
        for token in query_tokens:
            if token in name:
                score += 8
            if token in description:
                score += 5
            if token in category:
                score += 3
            if token in tags:
                score += 2
            if token in restaurant:
                score += 1
        if "udon" in query_tokens and "udon" in dish.restaurant_name.lower():
            score += 2
        if score > 0:
            scored.append((score, dish))
    return [dish for score, dish in sorted(scored, key=lambda item: item[0], reverse=True)[:limit]]

async def sugerencia_restaurante(nombre: str, motivo: str, session: AsyncSession) -> List[ChatSuggestion]:
    statement = select(Restaurante).where(Restaurante.name.ilike(f"%{nombre}%"))
    result = await session.execute(statement)
    restaurante = result.scalar_one_or_none()
    if not restaurante or restaurante.id is None:
        return []
    return [
        ChatSuggestion(
            restaurante_id=restaurante.id,
            restaurante_nombre=restaurante.name,
            image_path=restaurante.image_path,
            wait_time=restaurante.wait_time,
            type_of_food=restaurante.type_of_food,
            motivo=motivo,
        )
    ]

@app.post("/api/chat", response_model=ChatResponse)
async def chat_beloz(request: ChatRequest, session: AsyncSession = Depends(get_session)):
    message = request.message.strip()
    if not message:
        return ChatResponse(
            provider="beloz-rag",
            respuesta="Dime que restaurante, plato o tipo de comida quieres consultar.",
            accion="pedir_contexto",
        )

    chunks = await buscar_chunks_rag(message, session)
    visual_dishes = await buscar_platos_visuales(message, session)
    if chunks:
        principales = chunks[:3]
        lineas = [
            f"- {chunk.title}: {chunk.content}"
            for chunk in principales
        ]
        fuentes = sorted({chunk.source_url for chunk in principales})
        fuente_texto = "\n".join(f"Fuente: {url}" for url in fuentes[:2])
        restaurante_nombre = principales[0].restaurant_name
        sugerencias = await sugerencia_restaurante(
            restaurante_nombre,
            "Informacion enriquecida desde fuentes oficiales del restaurante.",
            session,
        )
        visual_suggestions = [
            ChatSuggestion(
                restaurante_id=0,
                restaurante_nombre=dish.restaurant_name,
                image_path=dish.image_url,
                plato=dish.dish_name,
                motivo=dish.description[:180] if dish.description else "Imagen y plato extraidos de la carta oficial.",
            )
            for dish in visual_dishes
        ]
        return ChatResponse(
            provider="beloz-rag",
            respuesta=(
                f"He encontrado informacion oficial sobre {restaurante_nombre}:\n"
                + "\n".join(lineas)
                + ("\n" + fuente_texto if fuente_texto else "")
            ),
            accion="mostrar_contexto",
            sugerencias=visual_suggestions or sugerencias,
        )

    if visual_dishes:
        fuentes = sorted({dish.dish_url for dish in visual_dishes})
        lineas = [
            f"- {dish.dish_name}: {dish.description}"
            for dish in visual_dishes
        ]
        return ChatResponse(
            provider="beloz-visual-rag",
            respuesta=(
                "He encontrado platos con imagen enlazada desde la carta oficial:\n"
                + "\n".join(lineas)
                + "\n"
                + "\n".join(f"Fuente: {url}" for url in fuentes[:2])
            ),
            accion="mostrar_platos_visuales",
            sugerencias=[
                ChatSuggestion(
                    restaurante_id=0,
                    restaurante_nombre=dish.restaurant_name,
                    image_path=dish.image_url,
                    plato=dish.dish_name,
                    motivo=dish.description[:180] if dish.description else "Plato extraido de la carta oficial.",
                )
                for dish in visual_dishes
            ],
        )

    statement = select(Restaurante).order_by(Restaurante.relevancia.desc()).limit(3)
    result = await session.execute(statement)
    restaurantes = result.scalars().all()
    sugerencias = [
        ChatSuggestion(
            restaurante_id=restaurante.id or 0,
            restaurante_nombre=restaurante.name,
            image_path=restaurante.image_path,
            wait_time=restaurante.wait_time,
            type_of_food=restaurante.type_of_food,
            motivo="Restaurante destacado en Beloz.",
        )
        for restaurante in restaurantes
        if restaurante.id is not None
    ]
    return ChatResponse(
        provider="beloz-local",
        respuesta="No tengo un documento especifico para esa pregunta todavia. Te dejo opciones destacadas de Beloz y puedo afinar por presupuesto, antojo o prisa.",
        accion="recomendar",
        sugerencias=sugerencias,
    )

@app.get("/salud")
def estado_servidor():
    return {"estado": "ok", "timestamp": datetime.utcnow().isoformat(), "unified": True}
