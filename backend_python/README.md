# Backend Python de Innovación

Este directorio aloja una API en Python (FastAPI) pensada para experimentar con IA y servicios de valor añadido para Beloz.

## Estructura inicial
- `app/main.py`: punto de entrada FastAPI con endpoints de ejemplo.
- `requirements.txt`: dependencias.
- `notebooks/` y `datos/` (opcional) para explorar modelos.

## Puesta en marcha rápida
```bash
cd backend_python
python -m venv .venv
.\.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## Configuracion
1. Copia `.env.example` a `.env`.
2. Define `DATABASE_URL` con la URL PostgreSQL de Render, Railway u otro proveedor.
3. Si la URL empieza por `postgres://` o `postgresql://`, la app la adapta automaticamente a `postgresql+asyncpg://`.
4. `AUTO_CREATE_TABLES=true` crea las tablas al arrancar. Para produccion avanzada, cambia a migraciones con Alembic.

## Render
El archivo `../render.yaml` declara:
- servicio web `beloz-backend` con Docker;
- base de datos PostgreSQL `belozdb`;
- variable `DATABASE_URL` conectada al `connectionString` interno de PostgreSQL;
- health check en `/salud`.

Tras desplegar el backend en Render, configura Android con la URL publica del servicio:
```properties
BELOZ_API_BASE_URL=https://TU-SERVICIO.onrender.com/
```

Puedes poner esa variable en `local.properties` para desarrollo local o pasarla a Gradle como `-PBELOZ_API_BASE_URL=...`.

