from datetime import datetime
from typing import Optional
from sqlmodel import Field, SQLModel

class User(SQLModel, table=True):
    __tablename__ = "users"
    id: Optional[int] = Field(default=None, primary_key=True)
    name: str = ""
    surname: str = ""
    email: str = Field(unique=True, index=True)
    password_hash: str
    num_telefono: Optional[str] = None
    created_at: datetime = Field(default_factory=datetime.utcnow)

class Restaurante(SQLModel, table=True):
    __tablename__ = "restaurantes"
    id: Optional[int] = Field(default=None, primary_key=True)
    name: str
    image_path: Optional[str] = None
    wait_time: int
    price_level: str
    type_of_food: str
    country: str
    es_popular: bool = False
    logo_restaurante: Optional[str] = None
    relevancia: int = 0
    valoracion: float = 0.0

class Plato(SQLModel, table=True):
    __tablename__ = "platos"
    id: Optional[int] = Field(default=None, primary_key=True)
    restaurante_id: int = Field(foreign_key="restaurantes.id")
    name: str
    description: str
    price: float
    image_path: Optional[str] = None

class Pedido(SQLModel, table=True):
    __tablename__ = "pedidos"
    id: Optional[int] = Field(default=None, primary_key=True)
    user_id: int = Field(foreign_key="users.id")
    restaurant_id: int = Field(foreign_key="restaurantes.id")
    total: float
    status: str = "pendiente"
    fecha: datetime = Field(default_factory=datetime.utcnow)

class DetallePedido(SQLModel, table=True):
    __tablename__ = "detalle_pedido"
    id_detalle: Optional[int] = Field(default=None, primary_key=True)
    pedido_id: Optional[int] = Field(default=None, foreign_key="pedidos.id")
    plato_id: int = Field(foreign_key="platos.id")
    cantidad: int
    precio: float

class PaymentInfo(SQLModel, table=True):
    __tablename__ = "datos_bancarios"
    id: Optional[int] = Field(default=None, primary_key=True)
    user_id: int = Field(foreign_key="users.id", unique=True, index=True)
    nombre_titular: Optional[str] = None
    numero_tarjeta_encriptado: Optional[str] = None
    iv: Optional[str] = None
    fecha_expiracion: Optional[str] = None
    tipo_tarjeta: Optional[str] = None
    metodo_pago_predeterminado: bool = True

class AIKnowledgeChunk(SQLModel, table=True):
    __tablename__ = "ai_knowledge_chunks"
    id: Optional[int] = Field(default=None, primary_key=True)
    restaurant_name: str = Field(index=True)
    category: str = Field(index=True)
    title: str
    content: str
    source_url: str
    source_type: str = "web"
    tags: str = ""
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)

class AIVisualDish(SQLModel, table=True):
    __tablename__ = "ai_visual_dishes"
    id: Optional[int] = Field(default=None, primary_key=True)
    restaurant_name: str = Field(index=True)
    dish_name: str = Field(index=True)
    description: str = ""
    category: str = Field(default="", index=True)
    image_url: str
    dish_url: str
    source_url: str
    extraction_method: str = "dom-proximity"
    confidence: float = 0.0
    tags: str = ""
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: datetime = Field(default_factory=datetime.utcnow)
