# Roadmap IA - Beloz (Kotlin + Backend Python)

Objetivo: evolucionar la app a una experiencia predictiva y contextual, con IA on-device para privacidad y backend Python para mejoras potentes.

## Principios
- On-device primero: modelos y reglas en el movil cuando sea posible.
- Open source y gratuito: sin dependencias cerradas para ML.
- UX explicable: el usuario entiende por que se sugiere algo.
- Datos minimizados: solo lo necesario y con control del usuario.

## Mapa de componentes
### Kotlin (movil)
- Context Engine: senales de momento del dia, dia de semana, clima, historico.
- Taste Profile: perfil dinamico local con preferencias (cocina, precio, picante, horarios).
- Ranking local: reordenar catalogo y banners por probabilidad de conversion.
- Scan to Crave: ML Kit para ingredientes y busqueda guiada.
- Notificaciones inteligentes: triggers por probabilidad de conversion.
- Audio y wearables: TTS y notificaciones glanceable (Wear OS).

### Backend Python (FastAPI)
- ETA inteligente: prediccion con banda de confianza y explicacion.
- Tracking granular: estados finos (emplatado, empaquetado, pickup, entrega).
- Anomalias y retrasos: deteccion proactiva y compensaciones.
- Asistente LLM open source: soporte y cambios de pedido.

## Hitos (orden sugerido)
### M0 - Fundaciones y contratos
- Definir eventos de producto (view, click, add-to-cart, checkout, pedido).
- Crear contratos API base para IA (recomendaciones, eta, tracking).
- Persistencia local de perfil (DataStore/Room).
- Telemetria local y opt-out.

### M1 - Recomendaciones contextuales v2 (on-device)
- Vector de contexto (momento, dia, clima, historico).
- Reglas + scoring simple (ponderaciones ajustables).
- UI: seccion de recomendaciones con explicacion breve.
- Tests unitarios del motor.

### M2 - Perfil de sabor y ranking local
- Perfil incremental (conteos + decay temporal).
- Ranking del feed segun contexto + perfil.
- Experimento A/B local (bandit simple).

### M3 - Scan to Crave (vision)
- ML Kit Object Detection (on-device).
- Pipeline ingrediente -> categorias -> platos.
- UI: modo camara + resultados por cercania.

### M4 - ETA y tracking obsesivo (backend)
- Endpoint /eta con banda y causa.
- Endpoint /tracking con hitos detallados.
- Integracion Kotlin: polling + UI timeline.

### M5 - Audio + wearables
- TTS para eventos criticos.
- Wear OS tiles y notificaciones rapidas.

## Contratos API (borrador)
### POST /ia/recomendaciones
Request:
```
{
  "user_id": "u123",
  "momento": "noche",
  "dia": "domingo",
  "clima": "lluvia",
  "categoria_preferida": "italiana"
}
```
Response:
```
{
  "items": [
    {
      "titulo": "Comfort food nocturno",
      "motivo": "Domingo + lluvia",
      "tags": ["comfort", "caliente"]
    }
  ]
}
```

### GET /ia/eta?pedido_id=123
Response:
```
{
  "min": 18,
  "max": 24,
  "razon": "alta demanda + trafico"
}
```

### GET /ia/tracking?pedido_id=123
Response:
```
{
  "estado": "en_camino",
  "hitos": ["cocina", "empaque", "pickup", "en_camino", "llegando"]
}
```

## Checklist para empezar a aplicar codigo (sprint 1)
- Kotlin: crear modulo `innovacion/perfil` con modelos `PerfilSabor`, `EventoUso`.
- Kotlin: DataStore para persistir preferencias y recencia.
- Kotlin: agregar eventos de producto en pantallas clave.
- Kotlin: integrar perfil con `MotorRecomendacionesContextuales`.
- Python: agregar endpoint /ia/recomendaciones con scoring simple.
- Python: definir esquema de eventos para futura ingesta.

## Testing y calidad
- Unit tests para reglas y ranking local.
- Simulaciones con data sintetica para ETA (backend).
- Validar tiempos y uso de bateria (on-device).

## Riesgos y mitigacion
- Datos pobres: usar cold-start con reglas + popularidad.
- Privacidad: opt-out y explicaciones visibles.
- Mantenimiento: separar capas (dominio, datos, UI).
