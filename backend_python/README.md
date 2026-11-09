# Backend Python de Innovación

Este directorio aloja una API en Python (FastAPI) pensada para experimentar con IA y servicios de valor añadido para Beloz.

**Seguridad:** este backend experimental no emite ni valida sesiones de usuario.
Las rutas `/api/pedidos` y `/api/pagos` responden temporalmente con HTTP 503 para
evitar acceso sin autorizacion. No deben reactivarse hasta implementar y probar
autenticacion, control de propiedad y un sistema de pagos adecuado. El catalogo,
las recomendaciones y el chat no dependen de esas rutas. El servicio Node es
un repositorio distinto y necesita una revision independiente.

Con `CORS_ORIGINS=*` no se permiten credenciales CORS. Si se necesitan cookies
entre origenes, configura una lista explicita de origenes de confianza.

## Estructura inicial
- `app/main.py`: punto de entrada FastAPI con endpoints de ejemplo.
- `requirements.txt`: dependencias.
- `notebooks/` y `datos/` (opcional) para explorar modelos.

## Puesta en marcha rápida
```bash
cd backend_python
python -m venv .venv-seguro
.\.venv-seguro\Scripts\activate
python -m pip --isolated install --only-binary=:all: --index-url https://pypi.org/simple pip==26.2.0
python -m pip --isolated install --only-binary=:all: --index-url https://pypi.org/simple -r requirements.txt
uvicorn app.main:app --reload
```

En macOS activa el entorno con `source .venv-seguro/bin/activate`. Crea el
entorno en cada ordenador; no copies una carpeta de entorno virtual entre
Windows y macOS.

Un entorno virtual conserva la version del interprete con la que se creo. El
entorno `.venv-seguro` preparado durante la revision del 5 de noviembre de
2026 utiliza Python 3.12.1, una version antigua sin todas las correcciones de
seguridad posteriores. Su nombre no garantiza que el entorno sea seguro. Antes
de usarlo con datos reales, instala y verifica un interprete compatible y
actualizado, crea otro entorno y vuelve a ejecutar las pruebas. Las versiones
fijadas en `requirements.txt` pueden requerir ajustes para Python 3.13 o mayor.
El entorno anterior `.venv` no se ha actualizado y conserva sus dependencias.

Las dependencias directas estan fijadas, pero `requirements.txt` no es un
lockfile completo con hashes de todos los paquetes transitivos. Para un
despliegue reproducible debe generarse y revisarse uno para cada plataforma.

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

Los cambios de `requirements.txt` no actualizan un entorno virtual ya existente.
Para probar las versiones nuevas, crea un entorno virtual nuevo y ejecuta el
comando de instalacion anterior. No copies tu `.env` dentro de la imagen Docker;
facilita la configuracion al contenedor mediante variables de entorno.

