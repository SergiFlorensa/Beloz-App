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

