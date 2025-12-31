from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime

app = FastAPI(title="Backend IA Beloz", version="0.1.0")

class ContextoEntrada(BaseModel):
    momento_del_dia: str
    tipo_de_dia: str
    clima: str | None = None

class Sugerencia(BaseModel):
    titulo: str
    descripcion: str
    etiquetas: list[str]

@app.get("/salud")
def estado_servidor():
    return {"estado": "ok", "timestamp": datetime.utcnow().isoformat()}

@app.post("/recomendaciones", response_model=list[Sugerencia])
def generar_recomendaciones(contexto: ContextoEntrada):
    recomendaciones: list[Sugerencia] = []

    if contexto.momento_del_dia.lower() == "noche" and contexto.tipo_de_dia == "fin_de_semana":
        recomendaciones.append(
            Sugerencia(
                titulo="Plan nocturno sin estrés",
                descripcion="Comparte pizzas y postres con tu grupo. Añade bebidas frías y playlists relajantes.",
                etiquetas=["compartir", "confort"],
            )
        )

    if contexto.momento_del_dia.lower() == "mediodia" and contexto.tipo_de_dia == "laborable":
        recomendaciones.append(
            Sugerencia(
                titulo="Energía en pausa corta",
                descripcion="Menús ejecutivos con entrega <15 min, acompáñalos con jugos verdes.",
                etiquetas=["rápido", "nutritivo"],
            )
        )

    if contexto.clima and "lluvia" in contexto.clima.lower():
        recomendaciones.append(
            Sugerencia(
                titulo="Clima lluvioso",
                descripcion="Sopas asiáticas, ramen y postres calientes para reconfortar.",
                etiquetas=["calor", "sopa"],
            )
        )

    if not recomendaciones:
        recomendaciones.append(
            Sugerencia(
                titulo="Explora algo nuevo",
                descripcion="Prueba restaurantes emergentes con experiencia sensorial personalizada.",
                etiquetas=["explorar", "sensory"],
            )
        )

    return recomendaciones
