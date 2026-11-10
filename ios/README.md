# Beloz para iOS

Proyecto nativo SwiftUI que consume el mismo servicio y catálogo que la app Android. No copia ni migra la base de datos: restaurantes, platos, imágenes, pedidos y chat se consultan por HTTPS al backend configurado.

## Abrir y ejecutar

1. En un Mac con Xcode 15 o posterior, abre `ios/Beloz.xcodeproj`.
2. Selecciona el esquema **Beloz** y un simulador iPhone con iOS 17 o posterior.
3. Pulsa **Run**. La URL inicial ya apunta a `https://beloz.onrender.com/`.
4. Para instalarlo en un iPhone, selecciona tu equipo en **Signing & Capabilities** y establece un identificador de paquete propio si Xcode lo solicita.

El host de desarrollo actual es Windows, por lo que aquí no se puede ejecutar Xcode, compilar el binario iOS, firmarlo ni lanzarlo en un simulador. La validación final del proyecto debe hacerse desde macOS.

El paquete está preparado para pruebas en simulador, no es todavía un envío a App Store: antes de distribuirlo añade el icono final de 1024×1024, revisa la ficha de privacidad con las prácticas reales del backend y configura la firma de distribución.

## Configurar otro backend

La URL no contiene credenciales. Para cambiarla sin tocar el proyecto compartido, crea `ios/Beloz/Config/Local.xcconfig` a partir del ejemplo `ios/Beloz/Config/Local.example.xcconfig` y cambia `BELOZ_API_BASE_URL`. En los archivos `.xcconfig`, conserva el formato `https:/$()/` del ejemplo: Xcode interpreta `//` como el comienzo de un comentario. Ese archivo local está ignorado por Git. El backend debe ofrecer los endpoints `/api/restaurantes`, `/api/restaurantes/{id}/platos`, `/api/auth/*`, `/api/pedidos` y `/api/chat` usados por Android.

La app exige HTTPS también en Debug, sin excepciones ATS. Para probar un backend que corre en tu Mac, expón un endpoint HTTPS accesible desde el simulador o el iPhone; el `localhost` del Mac no es accesible directamente desde un teléfono físico.

## Funciones incluidas

- Inicio con menú, búsqueda por prefijo, filtros, tres recomendaciones comparables y restaurantes destacados.
- Filtros por varios tipos de cocina, precio orientativo y orden por nombre o espera.
- Mapa centrado en Reus, sin acceso a la ubicación personal ni marcadores de restaurantes inventados.
- Carta remota agrupada por categoría e imágenes de platos, con cesta persistente en el dispositivo.
- Chat Beloz AI conectado a `/api/chat`, historial breve durante la sesión, sugerencias y fuentes devueltas por el servicio.
- Registro, inicio/cierre de sesión, cambios de correo, teléfono y contraseña, eliminación de cuenta e historial y detalles de pedidos.
- Gustos opcionales: con consentimiento se guardan localmente los tipos de cocina visitados y el chat recibe un resumen; al desactivarlo se borra el historial.
- Envío del pedido según el contrato usado por Android. El backend actual no ofrece un cobro online integrado en este flujo: la app no guarda tarjetas ni afirma que haya cobrado.

## Seguridad y límites conocidos

- Sesión y token guardados en Keychain, con protección `WhenUnlockedThisDeviceOnly`.
- Sin SDKs externos, claves de proveedor, secretos de base de datos, datos personales de ejemplo ni copia del backup PostgreSQL.
- Incluye un manifiesto de privacidad para el uso de `UserDefaults` y los datos que este cliente envía al backend; antes de publicar, contrástalo con la retención y los usos reales configurados en el servidor.
- Solicitudes de API limitadas al host configurado; no se siguen redirecciones HTTP que pudieran reenviar el token.
- Imágenes relativas se resuelven contra el backend y las URL absolutas solo se cargan con HTTPS.
- No se solicitaron permisos de ubicación, contactos, cámara ni fotos.
- El mapa solo señala Reus: el catálogo no proporciona coordenadas verificadas de restaurantes. Pagos con tarjeta, notificaciones push y seguimiento en tiempo real no están disponibles en esta versión.

## Lista de prueba en Xcode

Antes de abrir el simulador, desde la raiz del repositorio ejecuta
`bash ios/verify-macos.sh`. Comprueba los archivos de proyecto, ejecuta pruebas
de búsqueda, filtros, precios y datos, y compila para
simulador sin firma. Requiere la aplicacion completa de Xcode y su SDK de iOS,
no solo Command Line Tools. No instala paquetes, no arranca el servidor y no
modifica la base de datos. Esta comprobacion aun no se ha ejecutado en macOS.

Para trabajar con iOS no necesitas instalar Gradle, dependencias Android ni
el backend experimental Python. No hay paquetes externos de Swift ni CocoaPods
declarados en este proyecto. Node solo hace falta si ejecutas el servidor en
tu Mac; para usar Render no es necesario arrancarlo localmente.

- Compilar para simulador iPhone con iOS 17+.
- Confirmar carga de restaurantes, cartas categorizadas e imágenes.
- Probar búsqueda, filtros y orden por tiempo.
- Añadir platos, cerrar y reabrir la app, y verificar la cesta.
- Enviar una pregunta al chat y abrir una sugerencia de restaurante.
- Registrar/iniciar sesión, revisar cuenta e historial, editar teléfono/correo y cerrar sesión.
- Enviar un pedido de prueba solo si se acepta que se creará un registro real en el backend.

Render puede tardar en responder tras un periodo sin actividad. La app permite hasta 90 segundos para las primeras peticiones; en desarrollo conviene esperar a que el servicio despierte antes de repetir operaciones que creen pedidos.
