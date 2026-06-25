import os
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from dotenv import load_dotenv

load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL") or os.getenv("EXTERNAL_DATABASE_URL")

if DATABASE_URL and DATABASE_URL.startswith("postgres://"):
    DATABASE_URL = DATABASE_URL.replace("postgres://", "postgresql+asyncpg://", 1)
elif DATABASE_URL and DATABASE_URL.startswith("postgresql://"):
    DATABASE_URL = DATABASE_URL.replace("postgresql://", "postgresql+asyncpg://", 1)

if not DATABASE_URL:
    raise RuntimeError(
        "DATABASE_URL no esta configurada. Define DATABASE_URL o EXTERNAL_DATABASE_URL en backend_python/.env."
    )

connect_args = {"ssl": True} if "render.com" in DATABASE_URL else {}

engine = create_async_engine(
    DATABASE_URL,
    echo=os.getenv("SQL_ECHO") == "true",
    connect_args=connect_args,
)

async_session = sessionmaker(
    engine, class_=AsyncSession, expire_on_commit=False
)

async def get_session() -> AsyncSession:
    async with async_session() as session:
        yield session
