# Auditoría de seguridad y cadena de suministro de Beloz

Fecha: 5 de noviembre de 2026. Alcance: copia local de Android, backend experimental Python e inspección estática de la carpeta iOS. El repositorio separado del servidor Node y los servicios desplegados no se han auditado ni modificado. No se ha hecho commit, push, despliegue ni operación en cuentas externas.

## Conclusión

Se han corregido dos dependencias vulnerables del runtime de Android y las dependencias Python afectadas en un entorno nuevo. Se ha cerrado temporalmente el acceso a pedidos y pagos del backend Python, que carecía de autenticación y autorización. Persisten avisos en herramientas de compilación Android y el intérprete local Python es antiguo. No se ha encontrado evidencia de compromiso en las comprobaciones descritas; esto no demuestra que el proyecto o el equipo estén libres de malware.

## Hallazgos prioritarios y estado

### Alta: operaciones de pedidos y pagos sin autorización en el backend Python

En `backend_python/app/main.py`, seis rutas permitían acceder o escribir información de pedidos y pagos sin validar una sesión ni la propiedad del recurso. El inicio de sesión existente comprueba la contraseña, pero **no emite ni valida un token de sesión**. Se ha aplicado una barrera que devuelve HTTP 503 en estas rutas, incluso si se presenta un token arbitrario; esto desactiva esas operaciones en esta copia local hasta implementar autenticación y autorización reales. No protege una versión que ya esté desplegada.

El registro y login siguen siendo experimentales y no se han validado controles de limitación de intentos, sesiones, caducidad ni recuperación. No usar este backend para datos personales reales ni pagos. Las operaciones de pago requieren, además, un proveedor adecuado y evitar que el servicio almacene datos de tarjeta directamente. El catálogo y el chat no dependen de las rutas bloqueadas en esta implementación; sus pruebas usan datos simulados, no una base real.

La respuesta de error de registro ya no devuelve excepciones de base de datos. La configuración CORS ya no permite credenciales con origen comodín, pero CORS no sustituye la autorización ni impide por sí solo el acceso de clientes no navegador.

### Alta: posibles credenciales compartidas y políticas del servicio externo

En esta conversación se compartió anteriormente una URL de PostgreSQL que incluía credenciales. Si esa contraseña sigue vigente, trátala como expuesta y rótala desde el proveedor; actualiza después la configuración de los servicios afectados. No se ha verificado su vigencia ni cambiado ninguna cuenta. No se reproducen valores.

El proyecto contiene una clave de Supabase de tipo cliente público. Su presencia no demuestra por sí sola una fuga de una clave privilegiada; la protección depende, entre otros factores, de las políticas de autorización del servidor. No se han comprobado las políticas RLS ni las credenciales de producción. [Tipos de claves de Supabase](https://supabase.com/docs/guides/getting-started/api-keys).

### Alta: intérprete Python y entorno anterior

`backend_python/.venv` continúa intacto, con las versiones antiguas detalladas más abajo; no se recomienda su uso con datos reales. El nuevo `backend_python/.venv-seguro` actualiza las dependencias, pero fue creado con Python 3.12.1, anterior a correcciones de seguridad posteriores. Su nombre **no** supone una garantía de seguridad.

[Python 3.12.16](https://www.python.org/downloads/release/python-31216/) incorpora correcciones posteriores, pero python.org ya no ofrece instaladores binarios oficiales de Windows para las últimas versiones 3.12. Existe una [distribución independiente mantenida por Astral](https://github.com/astral-sh/python-build-standalone), que no se ha instalado ni auditado íntegramente. Antes de usar datos reales, selecciona un intérprete soportado y verifica compatibilidad; cambiar a Python 3.13 o superior puede exigir actualizar NumPy y otras bibliotecas.

### Alta: herramientas de compilación Android desactualizadas

El análisis OSV de los grafos de herramientas y pruebas encontró **16 coordenadas Maven afectadas y 66 avisos GHSA únicos** en el classpath de compilación, incluyendo avisos críticos o altos. Estar en el classpath no demuestra que la funcionalidad afectada se invoque ni que sea explotable aquí. Estas coordenadas no aparecieron en los grafos de runtime Android revisados. El detalle, rutas, versiones, gravedad y fuentes está en el apéndice.

No se ha forzado ninguna sustitución transversal: Android Gradle Plugin 8.2.2, Gradle y Kotlin necesitan una actualización coordinada y pruebas de compatibilidad. En particular, [CVE-2026-53914](https://github.com/advisories/GHSA-r937-wjx7-w2jp) afecta a la deserialización de metadatos de caché de compilación bajo sus condiciones descritas; las pruebas de esta revisión se ejecutaron con `--no-build-cache`, no con cachés remotas de terceros.

### Media: contexto Docker y verificación de descargas

Antes de esta revisión, el Dockerfile copiaba el directorio del backend y no había `.dockerignore`: un `.env` local podía entrar en el contexto y en la imagen. Se ha añadido exclusión de secretos y entornos virtuales. Esto reduce el riesgo futuro, pero no demuestra que imágenes anteriores no contengan secretos. El daemon Docker no estaba disponible y no se ha construido ni inspeccionado una imagen. [Contexto de compilación de Docker](https://docs.docker.com/build/concepts/context/).

Se ha añadido el SHA-256 oficial de la distribución Gradle 8.2 al wrapper. El JAR existente coincide con el JAR oficial de versiones anteriores de Gradle; esa diferencia no demuestra manipulación. La distribución Gradle local se comparó con una copia cuya SHA-256 coincide con la publicada por Gradle. La comprobación del wrapper se aplica al descargar, no vuelve a autenticar una distribución ya extraída. [Checksums oficiales](https://gradle.org/release-checksums/) y [verificación del wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

Faltan un lockfile integral de dependencias transitivas Python con hashes y una verificación integral de dependencias Gradle. Las dependencias pueden cambiar al resolver o actualizar; no se ha probado una instalación reproducible en macOS.

### Media: dos dependencias Android corregidas

- **Gson 2.8.5 → 2.8.9**, ruta `converter-gson:2.9.0 → gson:2.8.5`. [GHSA-4jrv-ppp4-jm57 / CVE-2022-25647](https://github.com/advisories/GHSA-4jrv-ppp4-jm57), gravedad alta: deserialización de ciertos tipos internos a partir de datos no confiables. No se ha demostrado que la app alcance la vía vulnerable.
- **play-services-basement 18.0.0 → 18.0.2**, presente mediante Play Services y ML Kit. [GHSA-cm6r-892j-jv2g / CVE-2022-2390](https://github.com/advisories/GHSA-cm6r-892j-jv2g), gravedad media: indicadores de mutabilidad incorrectos en determinados PendingIntent. No se ha demostrado que la app use la operación afectada.

Se han aplicado restricciones mínimas en `app/build.gradle.kts`. Las pruebas unitarias y la compilación debug pasaron; las pruebas de integración en dispositivo y una compilación release quedan pendientes.

### Baja: datos de ubicación en registros de desarrollo

Se ha retirado el interceptor HTTP BASIC de `ProveedorClimaRemoto.kt`, que podía registrar URL y coordenadas incluidas en ellas. La implementación examinada usa por defecto las coordenadas de Reus; no hay evidencia de que se registraran ubicaciones personales reales.

## Alcance, método y resultados

- Manifiestos y ejecución: revisados `settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`, Gradle Wrapper, `backend_python/requirements.txt`, Dockerfile, `render.yaml` y el proyecto iOS disponible. No se encontró un lockfile integral de Python ni un `Package.resolved` iOS en el material revisado. La carpeta iOS es un cambio local preexistente y no se compiló en Windows.
- Android: OSV consultado sobre 167 coordenadas del runtime debug y 178 coordenadas combinadas de runtime release e instrumentación; tras las correcciones no hubo coincidencias conocidas en esos grafos consultados. Los grafos no equivalen a ejecutar todas las funcionalidades. El análisis adicional de herramientas y pruebas encontró los avisos pendientes del apéndice.
- Python: OSV consultado sobre 38 paquetes instalados en el entorno nuevo; no hubo coincidencias conocidas en la fecha indicada. El entorno antiguo conserva los ocho componentes afectados descritos más abajo. Un resultado vacío no descarta malware ni fallos desconocidos.
- La consulta OSV incluye sus avisos de paquetes maliciosos conocidos; no aparecieron identificadores `MAL` en las versiones Python consultadas ni en los avisos conservados del classpath Maven. Esto no es un análisis exhaustivo de malware ni demuestra la integridad de todo paquete o ejecutable.
- Durante la preparación se descargaron ruedas para Windows/Python 3.12, se contrastaron sus 40 hashes frente a los publicados por PyPI y se revisaron los elementos de instalación relevantes; esto prueba coincidencia con los artefactos consultados, no una auditoría de su código o del registro.
- `pip check`: correcto en el entorno nuevo. Siete pruebas de seguridad y arranque Python y una prueba básica NumPy/pandas/scikit-learn: correctas. Las pruebas Python simularon sesiones de base de datos; no se probó PostgreSQL real.
- `:app:testDebugUnitTest :app:assembleDebug` con JDK 17, distribución verificada, `--offline --no-daemon --no-build-cache`: correctos. Siete tests en tres suites sin fallos. No se ejecutaron pruebas instrumentadas, pruebas de aceptación ni release. No se instaló la app ni se desplegaron servicios.
- Secretos: búsqueda parcial por patrones en código e historial accesible, con revisión adicional de archivos de configuración relevantes, sin mostrar valores. No sustituye un escáner completo del historial, archivos binarios, SDK, modelos ni una inspección forense. No se ha hallado evidencia real de infección; tampoco se puede descartar.
- No hay verificación de políticas RLS, base de datos real, servicio Node separado, Render, SDK de Android completo, modelos, ejecutables ajenos ni el archivo ZIP iOS preexistente. No se eliminó el entorno anterior, el ZIP, modelos ni datos del usuario.

## Cambios realizados y pasos siguientes

Cambios de esta auditoría: `.gitignore`, `backend_python/.dockerignore`, `backend_python/README.md`, `backend_python/app/main.py`, `backend_python/requirements.txt`, `backend_python/tests/test_security.py`, `app/build.gradle.kts`, `app/src/main/java/com/app/beloz/innovacion/contexto/datos/ProveedorClimaRemoto.kt`, `gradle/wrapper/gradle-wrapper.properties` y este informe. También se creó `backend_python/.venv-seguro`, ignorado por Git y Docker. Había numerosos cambios y archivos sin seguimiento antes de esta revisión; se han preservado.

1. Si la credencial compartida sigue activa, rótala y actualiza los servicios que la usan.
2. Actualiza el intérprete Python mediante una distribución de confianza, crea un entorno independiente y vuelve a ejecutar pruebas y auditoría; no reutilices el entorno antiguo.
3. Mantén desactivados pedidos y pagos del backend Python hasta disponer de autenticación, autorización por propiedad, limitación de intentos y pruebas de integración.
4. Actualiza de manera coordinada Gradle, Android Gradle Plugin y Kotlin, revisa sus notas de compatibilidad y vuelve a ejecutar OSV, pruebas y compilaciones sin cachés ajenas.
5. Genera lockfiles multiplataforma con hashes verificados, activa verificación de dependencias Gradle y configura una revisión periódica de avisos.
6. Antes de distribuir, prueba Android e iOS en dispositivos o simuladores, revisa el servidor Node y las políticas reales de PostgreSQL/Supabase, y comprueba imágenes Docker antiguas si existen.

Para pruebas locales de Android, desde la raíz y con JDK 17:

```powershell
.\gradlew.bat --offline --no-daemon --no-build-cache :app:testDebugUnitTest :app:assembleDebug
```

Las instrucciones de creación de entorno Python están en `backend_python/README.md`. No introduzcas contraseñas en archivos versionados. Comprueba `git status --short` antes de cualquier commit: este repositorio ya tenía cambios previos y esta revisión no los ha revertido.

## Anexo A: avisos Python del entorno anterior

Los avisos se han contrastado con OSV. `.venv` no se modificó. Las versiones nuevas están en el entorno separado y declaradas cuando corresponde en `backend_python/requirements.txt`. Se muestran gravedad, resumen y primer arreglo de la rama indicado por OSV. Consulta cada fuente para las condiciones completas.

### anyio: 4.14.0 → 4.14.2

Ruta: `FastAPI → Starlette → AnyIO`. El código revisado no usa directamente TLSStream, extra_groups ni el pool de procesos afectados; no se ha demostrado alcanzabilidad.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-82r6-8w77-94w6 / CVE-2026-63374](https://github.com/advisories/GHSA-82r6-8w77-94w6) | Crítica | AnyIO: TLSStream IDNA 2003 host name encoding enables potential TLS certificate spoofing | 4.14.2 |
| [GHSA-3w57-8xmc-8v26 / CVE-2026-63349](https://github.com/advisories/GHSA-3w57-8xmc-8v26) | Alta | AnyIO run_process/open_process ignores extra_groups and can retain parent supplementary groups | 4.14.2 |
| [GHSA-5p39-cfhj-2xmp / CVE-2026-64847](https://github.com/advisories/GHSA-5p39-cfhj-2xmp) | Media | AnyIO process-pool workers can block indefinitely on undrained stderr | 4.14.2 |

### certifi: 2026.6.17 → 2026.10.23

Ruta: `requests → certifi`. Las consultas HTTPS del scraper pueden usar el almacén de certificados; la explotación requiere intercepción o un certificado indebido. No hay evidencia de ello.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-6q48-6c7w-9gw2](https://github.com/advisories/GHSA-6q48-6c7w-9gw2) | Crítica | certifi bundles emSign root CAs with compromised intermediate CA keys | 2026.10.18 |

### urllib3: 2.7.0 → 2.8.0

Ruta: `requests → urllib3`. Las solicitudes a servidores externos pueden emplear urllib3; no se ha demostrado la configuración de proxy o flujo concreto afectado.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-8988-9cw3-xx77 / CVE-2026-97687](https://github.com/advisories/GHSA-8988-9cw3-xx77) | Alta | urllib3: HTTPS proxy TLS configuration may be ignored or overridden | 2.8.0 |
| [GHSA-vxq7-64xx-v4gw / CVE-2026-97689](https://github.com/advisories/GHSA-vxq7-64xx-v4gw) | Alta | urllib3: HTTPResponse.stream()/read_chunked() buffers an unbounded chunk-size line into memory | 2.8.0 |
| [GHSA-gh4c-6fx4-qh6g / CVE-2026-97688](https://github.com/advisories/GHSA-gh4c-6fx4-qh6g) | Media | urllib3: Chunked Deflate streaming can enter an infinite loop | 2.8.0 |

### starlette: 0.38.6 → 1.3.1

Ruta: `fastapi → starlette`. Los avisos afectan a formularios, validación de host, StaticFiles en Windows o HTTPEndpoint bajo condiciones específicas. No se ha encontrado el uso directo de esos componentes afectados. Se actualizó FastAPI de 0.115.0 a 0.140.0 por compatibilidad; requiere pruebas funcionales ampliadas antes de despliegue.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-82w8-qh3p-5jfq / CVE-2026-54283](https://github.com/advisories/GHSA-82w8-qh3p-5jfq) | Alta | Starlette: request.form() limits silently ignored for application/x-www-form-urlencoded enable DoS | 1.3.1 |
| [GHSA-f96h-pmfr-66vw / CVE-2024-47874](https://github.com/advisories/GHSA-f96h-pmfr-66vw) | Alta | Starlette Denial of service (DoS) via multipart/form-data | 0.40.0 |
| [GHSA-wqp7-x3pw-xc5r / CVE-2026-48818](https://github.com/advisories/GHSA-wqp7-x3pw-xc5r) | Alta | Starlette: SSRF and NTLM credential theft via UNC paths in StaticFiles on Windows | 1.1.0 |
| [GHSA-2c2j-9gv5-cj73 / CVE-2025-54121](https://github.com/advisories/GHSA-2c2j-9gv5-cj73) | Media | Starlette has possible denial-of-service vector when parsing large files in multipart forms | 0.47.2 |
| [GHSA-86qp-5c8j-p5mr / CVE-2026-48710](https://github.com/advisories/GHSA-86qp-5c8j-p5mr) | Media | Starlette has missing Host header validation that poisons request.url.path, bypassing path-based security checks | 1.0.1 |
| [GHSA-x746-7m8f-x49c / CVE-2026-48817](https://github.com/advisories/GHSA-x746-7m8f-x49c) | Media | Starlette: Arbitrary HTTP method dispatched to `HTTPEndpoint` attributes via `getattr` | 1.1.0 |
| [GHSA-jp82-jpqv-5vv3 / CVE-2026-54282](https://github.com/advisories/GHSA-jp82-jpqv-5vv3) | Baja | Starlette: Unvalidated request path concatenated into authority poisons request.url.hostname | 1.3.0 |

### requests: 2.32.3 → 2.33.0

Ruta: `dependencia directa de requirements.txt`. El scraper acepta URL; la filtración de .netrc requiere una URL maliciosa y credenciales locales aplicables. No se ha visto uso directo de extract_zipped_paths.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-9hjg-9r4m-mvj7 / CVE-2024-47081](https://github.com/advisories/GHSA-9hjg-9r4m-mvj7) | Media | Requests vulnerable to .netrc credentials leak via malicious URLs | 2.32.4 |
| [GHSA-gc5v-m9x4-r6x2 / CVE-2026-25645](https://github.com/advisories/GHSA-gc5v-m9x4-r6x2) | Media | Requests has Insecure Temp File Reuse in its extract_zipped_paths() utility function | 2.33.0 |

### python-dotenv: 1.0.1 → 1.2.2

Ruta: `dependencia directa de requirements.txt`. El aviso requiere operaciones de escritura set_key/unset_key bajo condiciones de enlaces; el proyecto usa load_dotenv.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-mf9w-mj56-hr94 / CVE-2026-28684](https://github.com/advisories/GHSA-mf9w-mj56-hr94) | Media | python-dotenv: Symlink following in set_key allows arbitrary file overwrite via cross-device rename fallback | 1.2.2 |

### soupsieve: 2.8.4 → 2.9.0

Ruta: `beautifulsoup4 → soupsieve`. Los avisos necesitan selectores CSS especialmente preparados; los selectores revisados son constantes.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-gjv8-xp57-g29c / CVE-2026-86000](https://github.com/advisories/GHSA-gjv8-xp57-g29c) | Media | Soup Sieve: Polynomial-time ReDoS (O(n²)) in the `IDENTIFIER` / `VALUE` selector sub-patterns | 2.9.0 |
| [GHSA-j934-xhv5-fg8f / CVE-2026-85999](https://github.com/advisories/GHSA-j934-xhv5-fg8f) | Media | Soup Sieve: Polynomial-time ReDoS (O(n²)) in the whitespace/comment trimming regex `RE_WS_END` (triggers on VALID selectors) | 2.9.0 |

### pip: 26.1.2 → 26.2.0

Ruta: `herramienta de instalación del entorno virtual`. El aviso afecta a URL de paquetes doblemente codificadas suministradas por un índice; el entorno nuevo utiliza el índice oficial y ruedas verificadas durante esta revisión.

| Aviso y fuente | Gravedad | Resumen del aviso | Corregido según OSV |
| --- | --- | --- | --- |
| [GHSA-qwm4-qh6w-59xr / CVE-2026-13346](https://github.com/advisories/GHSA-qwm4-qh6w-59xr) | Media | pip would incorrectly handle doubly-encoded package URLs from indexes | 26.2.0 |

Además, `passlib 1.7.4` no funcionaba con `bcrypt 5.0.0` en la prueba de hash/verificación. Se fijó `bcrypt 4.0.1` para compatibilidad y la prueba pasó. Esto es un problema funcional, no una afirmación de vulnerabilidad de bcrypt.

## Anexo B: avisos del classpath de compilación Android

Datos de OSV/GitHub Advisory Database consultados el 5 de noviembre de 2026. En cada apartado se muestra **una** ruta representativa del grafo; puede haber otras. El título resume la condición pero no prueba que se cumpla en Beloz. Las versiones corregidas pueden pertenecer a ramas diferentes: no deben imponerse sin comprobar rangos y compatibilidad. La gravedad es la publicada en el registro consultado y puede diferir de la valoración del mantenedor.

### io.netty:netty-handler 4.1.72.Final

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.utp:android-test-plugin-result-listener-gradle-proto:31.2.2 → io.grpc:grpc-netty:1.45.1 → io.netty:netty-codec-http2:4.1.72.Final → io.netty:netty-handler:4.1.72.Final`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-c4c3-7fpv-j4q5 / CVE-2026-75595](https://github.com/advisories/GHSA-c4c3-7fpv-j4q5) | Crítica | Netty: SNI Routing Bypass via Fragmented TLS ClientHello Causing Fallback to Default SslContext | 4.2.17.Final, 4.1.137.Final |
| [GHSA-3qp7-7mw8-wx86 / CVE-2026-44249](https://github.com/advisories/GHSA-3qp7-7mw8-wx86) | Alta | Netty has an IPv6 Subnet Filter Bypass via Incorrect Comparator Masking | 4.2.15.Final, 4.1.135.Final |
| [GHSA-c653-97m9-rcg9 / CVE-2026-50010](https://github.com/advisories/GHSA-c653-97m9-rcg9) | Alta | Netty: Wrapping plain trust manager silently disables hostname verification | 4.2.15.Final, 4.1.135.Final |
| [GHSA-x4gw-5cx5-pgmh / CVE-2026-45416](https://github.com/advisories/GHSA-x4gw-5cx5-pgmh) | Alta | Netty: SNI handler pre-allocates up to 16 MiB from nine attacker bytes | 4.2.15.Final, 4.1.135.Final |
| [GHSA-6mjq-h674-j845 / CVE-2023-34462](https://github.com/advisories/GHSA-6mjq-h674-j845) | Media | netty-handler SniHandler 16MB allocation | 4.1.94.Final |
| [GHSA-fccg-mwvh-qqg4 / CVE-2026-75596](https://github.com/advisories/GHSA-fccg-mwvh-qqg4) | Media | Netty: Fragmented ClientHello records trigger quadratic pre-handshake reassembly in default SNI parsing | 4.2.17.Final, 4.1.137.Final |

### com.google.guava:guava 31.1-jre

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools:sdk-common:31.2.2 → com.android.tools.analytics-library:shared:31.2.2 → com.android.tools:common:31.2.2 → com.google.guava:guava:31.1-jre`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-xxph-c9ww-hj94 / CVE-2026-102554](https://github.com/advisories/GHSA-xxph-c9ww-hj94) | Alta | Google Guava: CompactHashMap/CompactHashSet/MapMakerInternalMap deserialization triggers uncontrolled memory allocation | 33.7.2 |
| [GHSA-7g45-4rm6-3mm3 / CVE-2023-2976](https://github.com/advisories/GHSA-7g45-4rm6-3mm3) | Media | Guava vulnerable to insecure use of temporary directory | 32.0.0-android |
| [GHSA-5mg8-w23w-74h3 / CVE-2020-8908](https://github.com/advisories/GHSA-5mg8-w23w-74h3) | Baja | Information Disclosure in Guava | 32.0.0-android |

### com.google.protobuf:protobuf-java 3.19.3

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools:sdk-common:31.2.2 → com.android.tools.analytics-library:shared:31.2.2 → com.android.tools.analytics-library:protos:31.2.2 → com.google.protobuf:protobuf-java:3.19.3`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-4gg5-vx3j-xwc7 / CVE-2022-3510](https://github.com/advisories/GHSA-4gg5-vx3j-xwc7) | Alta | Protobuf Java vulnerable to Uncontrolled Resource Consumption | 3.16.3, 3.19.6, 3.20.3, 3.21.7 |
| [GHSA-735f-pc8j-v9w8 / CVE-2024-7254](https://github.com/advisories/GHSA-735f-pc8j-v9w8) | Alta | protobuf-java has potential Denial of Service issue | 3.25.5, 4.27.5, 4.28.2 |
| [GHSA-g5ww-5jh7-63cx / CVE-2022-3509](https://github.com/advisories/GHSA-g5ww-5jh7-63cx) | Alta | Protobuf Java vulnerable to Uncontrolled Resource Consumption | 3.16.3, 3.19.6, 3.20.3, 3.21.7 |
| [GHSA-h4h5-3hr4-j3g2 / CVE-2022-3171](https://github.com/advisories/GHSA-h4h5-3hr4-j3g2) | Media | protobuf-java has a potential Denial of Service issue | 3.21.7, 3.20.3, 3.19.6, 3.16.3 |

### commons-io:commons-io 2.4

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → androidx.databinding:databinding-compiler-common:8.2.2 → commons-io:commons-io:2.4`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-78wr-2p64-hpwj / CVE-2024-47554](https://github.com/advisories/GHSA-78wr-2p64-hpwj) | Alta | Apache Commons IO: Possible denial of service attack on untrusted input to XmlStreamReader | 2.14.0 |
| [GHSA-gwrp-pvrq-jmwv / CVE-2021-29425](https://github.com/advisories/GHSA-gwrp-pvrq-jmwv) | Media | Path Traversal and Improper Input Validation in Apache Commons IO | 2.7 |

### io.netty:netty-codec 4.1.72.Final

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.utp:android-test-plugin-result-listener-gradle-proto:31.2.2 → io.grpc:grpc-netty:1.45.1 → io.netty:netty-codec-http2:4.1.72.Final → io.netty:netty-codec:4.1.72.Final`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-558v-64gr-wgg4 / CVE-2026-59901](https://github.com/advisories/GHSA-558v-64gr-wgg4) | Alta | Netty: [Bzip2Decoder] Infinite Loop in RLE State Machine Leads to Event-Loop Thread Hang | 4.1.136.Final |
| [GHSA-mj4r-2hfc-f8p6 / CVE-2026-42583](https://github.com/advisories/GHSA-mj4r-2hfc-f8p6) | Alta | Netty Lz4FrameDecoder is vulnerable to resource exhaustion  | 4.1.133.Final |
| [GHSA-3p8m-j85q-pgmj / CVE-2025-58057](https://github.com/advisories/GHSA-3p8m-j85q-pgmj) | Media | Netty's decoders vulnerable to DoS via zip bomb style attack | 4.1.125.Final |

### io.netty:netty-codec-http 4.1.72.Final

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.utp:android-test-plugin-result-listener-gradle-proto:31.2.2 → io.grpc:grpc-netty:1.45.1 → io.netty:netty-codec-http2:4.1.72.Final → io.netty:netty-codec-http:4.1.72.Final`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-57rv-r2g8-2cj3 / CVE-2026-42584](https://github.com/advisories/GHSA-57rv-r2g8-2cj3) | Alta | Netty has HttpClientCodec response desynchronization | 4.2.13.Final, 4.1.133.Final |
| [GHSA-6jqx-86gh-f27w / CVE-2026-55831](https://github.com/advisories/GHSA-6jqx-86gh-f27w) | Alta | Netty SPDY SETTINGS frame count materializes unbounded settings map | 4.2.16.Final, 4.1.136.Final |
| [GHSA-f6hv-jmp6-3vwv / CVE-2026-42587](https://github.com/advisories/GHSA-f6hv-jmp6-3vwv) | Alta | Netty: HttpContentDecompressor maxAllocation bypass when Content-Encoding set to br/zstd/snappy leads to decompression bomb DoS | 4.2.13.Final, 4.1.133.Final |
| [GHSA-jppx-w49h-x2qq / CVE-2026-56745](https://github.com/advisories/GHSA-jppx-w49h-x2qq) | Alta | Netty: [SpdyHttpDecoder] ByteBuf Reference Leak on RST_STREAM Leads to Native Memory Exhaustion | 4.2.16.Final, 4.1.136.Final |
| [GHSA-mvh2-crg5-v77c / CVE-2026-55833](https://github.com/advisories/GHSA-mvh2-crg5-v77c) | Alta | Netty SPDY zlib header block continues decoded expansion after maxHeaderSize truncation | 4.2.16.Final, 4.1.136.Final |
| [GHSA-pwqr-wmgm-9rr8 / CVE-2026-33870](https://github.com/advisories/GHSA-pwqr-wmgm-9rr8) | Alta | Netty: HTTP Request Smuggling via Chunked Extension Quoted-String Parsing | 4.1.132.Final, 4.2.10.Final |
| [GHSA-269q-hmxg-m83q / CVE-2022-24823](https://github.com/advisories/GHSA-269q-hmxg-m83q) | Media | Local Information Disclosure Vulnerability in io.netty:netty-codec-http | 4.1.77.Final |
| [GHSA-38f8-5428-x5cv / CVE-2026-42585](https://github.com/advisories/GHSA-38f8-5428-x5cv) | Media | Netty vulnerable to HTTP Request Smuggling due to malformed Transfer-Encoding | 4.2.13.Final, 4.1.133.Final |
| [GHSA-4mp9-239f-g9hg / CVE-2026-59898](https://github.com/advisories/GHSA-4mp9-239f-g9hg) | Media | Netty: WebSockets V07/V08 handshaker missing Connection/Upgrade validation | 4.2.16.Final, 4.1.136.Final |
| [GHSA-5jpm-x58v-624v / CVE-2024-29025](https://github.com/advisories/GHSA-5jpm-x58v-624v) | Media | Netty's HttpPostRequestDecoder can OOM | 4.1.108.Final |
| [GHSA-6cqp-g7gg-8hr5 / CVE-2026-56746](https://github.com/advisories/GHSA-6cqp-g7gg-8hr5) | Media | Netty: Security Control Bypass via CORS Short-Circuit Failure | 4.2.16.Final, 4.1.136.Final |
| [GHSA-84h7-rjj3-6jx4 / CVE-2025-67735](https://github.com/advisories/GHSA-84h7-rjj3-6jx4) | Media | Netty has a CRLF Injection vulnerability in io.netty.handler.codec.http.HttpRequestEncoder | 4.2.8.Final, 4.1.129.Final |
| [GHSA-8c42-7qj2-3j46 / CVE-2026-59903](https://github.com/advisories/GHSA-8c42-7qj2-3j46) | Media | Netty Vulnerable to Cache Poisoning and Information Disclosure via CORS Vary Header Overwrite | 4.2.17.Final, 4.1.137.Final |
| [GHSA-gcjf-9mgh-3p7g / CVE-2026-59921](https://github.com/advisories/GHSA-gcjf-9mgh-3p7g) | Media | Netty: CRLF Injection via Multipart Filename in Netty HttpPostRequestEncoder | 4.2.16.Final, 4.1.136.Final |
| [GHSA-hvcg-qmg6-jm4c / CVE-2026-50020](https://github.com/advisories/GHSA-hvcg-qmg6-jm4c) | Media | Netty: HttpObjectDecoder skips arbitrary initial control characters when only initial CRLF characters are permitted | 4.2.15.Final, 4.1.135.Final |
| [GHSA-m4cv-j2px-7723 / CVE-2026-42580](https://github.com/advisories/GHSA-m4cv-j2px-7723) | Media | Netty vulnerable to HTTP Request Smuggling due to incorrect chunk size parsing | 4.2.13.Final, 4.1.133.Final |
| [GHSA-q4f6-jm68-57ww / CVE-2026-59899](https://github.com/advisories/GHSA-q4f6-jm68-57ww) | Media | Netty: [HttpContentEncoder] Unbounded Per-Connection Queue Growth via HTTP/1.1 Pipelining Leads to Denial of Service | 4.2.16.Final, 4.1.136.Final |
| [GHSA-v8h7-rr48-vmmv / CVE-2026-41417](https://github.com/advisories/GHSA-v8h7-rr48-vmmv) | Media | Netty: Start-Line Injection in DefaultHttpRequest.setUri() Allows HTTP Request Smuggling and RTSP Request Injection | 4.1.133.Final, 4.2.13.Final |
| [GHSA-xxqh-mfjm-7mv9 / CVE-2026-42581](https://github.com/advisories/GHSA-xxqh-mfjm-7mv9) | Media | Netty HTTP/1.0 TE+CL Coexistence Bypasses Smuggling Sanitization | 4.2.13.Final, 4.1.133.Final |
| [GHSA-fghv-69vj-qj49 / CVE-2025-58056](https://github.com/advisories/GHSA-fghv-69vj-qj49) | Baja | Netty vulnerable to request smuggling due to incorrect parsing of chunk extensions | 4.1.125.Final, 4.2.5.Final |

### io.netty:netty-codec-http2 4.1.72.Final

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.utp:android-test-plugin-result-listener-gradle-proto:31.2.2 → io.grpc:grpc-netty:1.45.1 → io.netty:netty-codec-http2:4.1.72.Final`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-93wv-jw9v-4972 / CVE-2026-56819](https://github.com/advisories/GHSA-93wv-jw9v-4972) | Alta | Netty: HTTP/2 decompression leaks ByteBuf reference count when the decompressor channel is already closed (Direct memory leak / OOM DoS) | 4.2.16.Final, 4.1.136.Final |
| [GHSA-f6hv-jmp6-3vwv / CVE-2026-42587](https://github.com/advisories/GHSA-f6hv-jmp6-3vwv) | Alta | Netty: HttpContentDecompressor maxAllocation bypass when Content-Encoding set to br/zstd/snappy leads to decompression bomb DoS | 4.2.13.Final, 4.1.133.Final |
| [GHSA-prj3-ccx8-p6x4 / CVE-2025-55163](https://github.com/advisories/GHSA-prj3-ccx8-p6x4) | Alta | Netty affected by MadeYouReset HTTP/2 DDoS vulnerability | 4.2.4.Final, 4.1.124.Final |
| [GHSA-w9fj-cfpg-grvv / CVE-2026-33871](https://github.com/advisories/GHSA-w9fj-cfpg-grvv) | Alta | Netty HTTP/2 CONTINUATION Frame Flood DoS via Zero-Byte Frame Bypass | 4.1.132.Final, 4.2.11.Final |
| [GHSA-xpw8-rcwv-8f8p](https://github.com/advisories/GHSA-xpw8-rcwv-8f8p) | Alta | io.netty:netty-codec-http2 vulnerable to HTTP/2 Rapid Reset Attack | 4.1.100.Final |
| [GHSA-563q-j3cm-6jxm / CVE-2026-50560](https://github.com/advisories/GHSA-563q-j3cm-6jxm) | Media | Netty susceptible to HTTP/2 Reset Attack with different on-the-wire signature | 4.2.15.Final, 4.1.135.Final |
| [GHSA-5x3r-wrvg-rp6q / CVE-2026-47244](https://github.com/advisories/GHSA-5x3r-wrvg-rp6q) | Media | Netty HTTP/2: Advertised MAX_CONCURRENT_STREAMS are not enforced | 4.2.15.Final, 4.1.135.Final |
| [GHSA-c2gf-v879-257j / CVE-2026-48043](https://github.com/advisories/GHSA-c2gf-v879-257j) | Media | netty-codec-http2: ByteBuf Reference-Count Leak in DelegatingDecompressorFrameListener Leads to Memory Exhaustion | 4.1.135.Final, 4.2.15.Final |
| [GHSA-c69g-56f8-xwqj / CVE-2026-59900](https://github.com/advisories/GHSA-c69g-56f8-xwqj) | Media | Netty: [codec-http2] Lack of Host Header Deduplication in HTTP/2→HTTP/1.x Translation Leads to Request Routing Bypass | 4.2.16.Final, 4.1.136.Final |

### org.bitbucket.b_c:jose4j 0.7.0

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.build:bundletool:1.15.2 → org.bitbucket.b_c:jose4j:0.7.0`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-3677-xxcr-wjqv / CVE-2024-29371](https://github.com/advisories/GHSA-3677-xxcr-wjqv) | Alta | jose4j is vulnerable to DoS via compressed JWE content | 0.9.6 |
| [GHSA-7g24-qg88-p43q / CVE-2023-31582](https://github.com/advisories/GHSA-7g24-qg88-p43q) | Alta | jose4j uses weak cryptographic algorithm | 0.9.3 |
| [GHSA-6qvw-249j-h44c / CVE-2023-51775](https://github.com/advisories/GHSA-6qvw-249j-h44c) | Media | jose4j denial of service via specifically crafted JWE | 0.9.4 |
| [GHSA-jgvc-jfgh-rjvv](https://github.com/advisories/GHSA-jgvc-jfgh-rjvv) | Media | Chosen Ciphertext Attack in Jose4j | 0.9.3 |

### org.jdom:jdom2 2.0.6

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.build.jetifier:jetifier-processor:1.0.0-beta10 → org.jdom:jdom2:2.0.6`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-2363-cqg2-863c / CVE-2021-33813](https://github.com/advisories/GHSA-2363-cqg2-863c) | Alta | XML External Entity (XXE) Injection in JDOM | 2.0.6.1 |

### io.netty:netty-common 4.1.72.Final

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.utp:android-test-plugin-result-listener-gradle-proto:31.2.2 → io.grpc:grpc-netty:1.45.1 → io.netty:netty-codec-http2:4.1.72.Final → io.netty:netty-common:4.1.72.Final`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-389x-839f-4rhx / CVE-2025-25193](https://github.com/advisories/GHSA-389x-839f-4rhx) | Media | Denial of Service attack on windows app using Netty | 4.1.118.Final |
| [GHSA-xq3w-v528-46rv / CVE-2024-47535](https://github.com/advisories/GHSA-xq3w-v528-46rv) | Media | Denial of Service attack on windows app using netty | 4.1.115.Final |

### org.apache.commons:commons-compress 1.21

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools:sdk-common:31.2.2 → com.android.tools:sdklib:31.2.2 → com.android.tools:repository:31.2.2 → org.apache.commons:commons-compress:1.21`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-4265-ccf5-phj5 / CVE-2024-26308](https://github.com/advisories/GHSA-4265-ccf5-phj5) | Media | Apache Commons Compress: OutOfMemoryError unpacking broken Pack200 file | 1.26.0 |
| [GHSA-4g9r-vxhx-9pgx / CVE-2024-25710](https://github.com/advisories/GHSA-4g9r-vxhx-9pgx) | Media | Apache Commons Compress: Denial of service caused by an infinite loop for a corrupted DUMP file | 1.26.0 |

### org.bouncycastle:bcpkix-jdk15on 1.67

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools:sdk-common:31.2.2 → org.bouncycastle:bcpkix-jdk15on:1.67`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-4cx2-fc23-5wg6 / CVE-2025-8916](https://github.com/advisories/GHSA-4cx2-fc23-5wg6) | Media | Bouncy Castle for Java bcpkix, bcprov, bcpkix-fips on All (API modules) allows Excessive Allocation | 1.79 |
| [GHSA-wg6q-6289-32hp / CVE-2026-5588](https://github.com/advisories/GHSA-wg6q-6289-32hp) | Media | Bouncy Castle Crypto Package For Java: Use of a Broken or Risky Cryptographic Algorithm vulnerability in bcpkix modules | 1.84 |

### org.bouncycastle:bcprov-jdk15on 1.67

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools:sdk-common:31.2.2 → org.bouncycastle:bcpkix-jdk15on:1.67 → org.bouncycastle:bcprov-jdk15on:1.67`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-4h8f-2wvx-gg5w / CVE-2024-34447](https://github.com/advisories/GHSA-4h8f-2wvx-gg5w) | Media | Bouncy Castle Java Cryptography API vulnerable to DNS poisoning | 1.78 |
| [GHSA-8xfc-gm6g-vgpv / CVE-2024-29857](https://github.com/advisories/GHSA-8xfc-gm6g-vgpv) | Media | Bouncy Castle certificate parsing issues cause high CPU usage during parameter evaluation. | 1.78 |
| [GHSA-hr8g-6v94-x4m9 / CVE-2023-33201](https://github.com/advisories/GHSA-hr8g-6v94-x4m9) | Media | Bouncy Castle For Java LDAP injection vulnerability | No indicada |
| [GHSA-v435-xc8x-wvr9 / CVE-2024-30171](https://github.com/advisories/GHSA-v435-xc8x-wvr9) | Media | Bouncy Castle affected by timing side-channel for RSA key exchange ("The Marvin Attack") | 1.78 |
| [GHSA-wjxj-5m7g-mg7q / CVE-2023-33202](https://github.com/advisories/GHSA-wjxj-5m7g-mg7q) | Media | Bouncy Castle Denial of Service (DoS) | No indicada |

### org.jetbrains.kotlin:kotlin-gradle-plugin 1.9.0

Ruta: `org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:1.9.0 → org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-r937-wjx7-w2jp / CVE-2026-53914](https://github.com/advisories/GHSA-r937-wjx7-w2jp) | Media | JetBrains Kotlin: Unsafe Deserialization in Kotlin Build Cache Enables Code Execution | 2.4.20-Beta1 |

### xerces:xercesImpl 2.12.0

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools:sdk-common:31.2.2 → xerces:xercesImpl:2.12.0`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-h65f-jvqw-m9fj / CVE-2022-23437](https://github.com/advisories/GHSA-h65f-jvqw-m9fj) | Media | Infinite Loop in Apache Xerces Java | 2.12.2 |
| [GHSA-w4jq-qh47-hvjq / CVE-2020-14338](https://github.com/advisories/GHSA-w4jq-qh47-hvjq) | Media | Improper Input Validation in Xerces | 2.12.0.sp3 |

### io.netty:netty-handler-proxy 4.1.72.Final

Ruta: `com.android.application:com.android.application.gradle.plugin:8.2.2 → com.android.tools.build:gradle:8.2.2 → com.android.tools.utp:android-test-plugin-result-listener-gradle-proto:31.2.2 → io.grpc:grpc-netty:1.45.1 → io.netty:netty-handler-proxy:4.1.72.Final`.

| Aviso y fuente | Gravedad | Resumen del aviso | Versiones corregidas según OSV |
| --- | --- | --- | --- |
| [GHSA-45q3-82m4-75jr / CVE-2026-42578](https://github.com/advisories/GHSA-45q3-82m4-75jr) | Baja | Netty has HTTP Header Injection via HttpProxyHandler Disabled Validation (Incomplete Fix CVE-2025-67735) | 4.1.133.Final, 4.2.13.Final |
