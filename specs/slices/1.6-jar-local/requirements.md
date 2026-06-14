# Slice 1.6 — JAR local documentado

**Estado:** hecho (PASO 6).  
**Historia de usuario:** continúa [`primera-historia-especificacion.md`](../../primera-historia-especificacion.md).  
**Prerrequisito:** slices 1.2–1.5 **hechas** en `main` (API + notificación + email Resend).  
**Referencias:** [`techstack.md`](../../techstack.md), [`api-contract.md`](../../api-contract.md), [`roadmap.md`](../../roadmap.md).

---

## Historia

Como anfitrión/desarrolladora, quiero **construir un JAR y arrancar la API con `java -jar`**, con instrucciones claras de requisitos y variables de entorno, para ejecutar Madrid Local Buddy en local **sin depender del IDE**.

---

## Alcance de este slice

| Incluido | Fuera (slice posterior) |
|----------|-------------------------|
| `mvn package` → JAR ejecutable (fat JAR Spring Boot) | Docker / contenedores |
| `java -jar …` arranca la app | Deploy en cloud |
| Sección **Running locally (JAR)** en [`readme.md`](../../../readme.md) | Scripts `.bat` / `.sh` de arranque |
| Archivo **`.env.example`** (sin secretos) | Cambios en contrato HTTP |
| Smoke manual: JAR arranca + `GET /api/experiences` | UI (Fase 1b) |
| Documentar `SERVER_PORT` (override opcional) | Test automatizado `java -jar` en CI *(aplazado — ver abajo)* |

**Idioma:** documentación operativa en el README en **inglés** (coherente con el README actual del repo). Specs del slice en **español**.

---

## Comportamiento observable

### Build

| Acción | Resultado esperado |
|--------|-------------------|
| `mvn package` (suite en verde) | Genera JAR ejecutable en `target/madrid-local-buddy-0.0.1-SNAPSHOT.jar` |
| JAR generado | Fat JAR repackageado por Spring Boot (`java -jar` arranca la app completa) |

El plugin `spring-boot-maven-plugin` ya está en el proyecto; este slice **no añade dependencias** salvo acuerdo explícito en `plan.md`.

### Arranque con `java -jar`

**Requisito:** Java **17+** (alineado con `pom.xml`).

Comando mínimo:

```bash
java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
```

Por defecto la API escucha en **puerto 8080**.

**Puerto configurable** (sin código nuevo — convención Spring Boot):

```bash
# Linux / macOS
SERVER_PORT=9090 java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar

# Windows PowerShell
$env:SERVER_PORT=9090; java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
```

### Perfiles de arranque documentados

#### Perfil A — Local / desarrollo (default)

Sin variables de entorno (o solo las opcionales):

- `EMAIL_SENDER_MODE` por defecto **`log`**: la app **arranca** sin API key ni credenciales de email.
- Notificaciones solo en log (comportamiento 1.5).
- Uso: desarrollo, pruebas manuales de catálogo y reservas sin envío real.

#### Perfil B — Envío real de email

Variables según [`techstack.md`](../../techstack.md):

- `EMAIL_SENDER_MODE=http`
- `HOST_NOTIFICATION_EMAIL`, `EMAIL_FROM`, `EMAIL_API_KEY` (obligatorias)
- `EMAIL_API_URL` (opcional)

Si `EMAIL_SENDER_MODE=http` y falta alguna variable obligatoria, la app **no arranca** (fail-fast ya acordado en 1.5).

El README y `.env.example` deben dejar claro **A vs B**; el detalle de cada variable sigue en `techstack.md` (no duplicar tablas largas — enlace + ejemplo mínimo).

### API — sin cambios de contrato

Tras arrancar el JAR, los endpoints existentes responden igual que en desarrollo con IDE:

| Endpoint | Comportamiento |
|----------|----------------|
| `GET /api/experiences` | `200` + catálogo de 2 experiencias |
| `POST /api/requests` | Sin cambios respecto a [`api-contract.md`](../../api-contract.md) |

---

## Documentación entregable

### README — sección «Running locally (JAR)»

Debe incluir, como mínimo:

1. **Prerequisites:** Java 17+, Maven 3.x.
2. **Build:** `mvn package`.
3. **Run:** comando `java -jar` con ruta al artefacto en `target/`.
4. **Environment:** enlace a `specs/techstack.md` (variables de email) + referencia a `.env.example`.
5. **Perfiles A y B** (local log vs envío real).
6. **Optional:** `SERVER_PORT`.
7. **Quick check:** ejemplo `curl` a `GET /api/experiences` (puerto por defecto 8080).

### `.env.example`

Archivo en la raíz del repo, **sin secretos**, versionado en git. Ejemplo orientativo:

```text
# Profile A (default) — log only, no real email
EMAIL_SENDER_MODE=log

# Profile B — uncomment and set for real email (see specs/techstack.md)
# EMAIL_SENDER_MODE=http
# HOST_NOTIFICATION_EMAIL=host@example.com
# EMAIL_FROM=onboarding@resend.dev
# EMAIL_API_KEY=re_xxxxxxxx
# EMAIL_API_URL=https://api.resend.com/emails
# SERVER_PORT=8080
```

El anfitrión copia a `.env` local (ignorado por git) y carga variables en el IDE o en la shell; **no** commitear `.env` con valores reales.

---

## Smoke manual

Tras `mvn package`:

1. Arrancar con **perfil A** (sin env vars): `java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar`.
2. Comprobar que el proceso **no falla** al startup.
3. `curl http://localhost:8080/api/experiences` → `200` y JSON con **2** experiencias.
4. *(Opcional en la misma sesión)* Perfil B con credenciales reales + `POST /api/requests` — reutiliza smoke 1.5; no es obligatorio para cerrar 1.6 si el perfil A está verde.

Escenarios detallados en [`validation.md`](validation.md) (PASO 3).

---

## Tests automatizados

| Enfoque | Decisión |
|---------|----------|
| **A (esta slice)** | Smoke **manual** en `validation.md`; suite `mvn test` existente sin regresiones. |
| **B (futuro)** | Test que lanza `java -jar` en subproceso y assert de health/GET — **aplazado** (Fase 2+ o CI más adelante). |

No se exige test de subproceso JAR en PASO 4–6 de 1.6.

---

## Criterios de éxito (requirements)

- [x] `mvn package` produce JAR ejecutable en `target/`.
- [x] `java -jar …` arranca en **perfil A** (sin variables) sin error.
- [x] `GET /api/experiences` responde correctamente contra el JAR en marcha.
- [x] README incluye sección «Running locally (JAR)» con prerequisitos, build, run, perfiles A/B, `SERVER_PORT`, curl de prueba.
- [x] `.env.example` versionado; `.env` real sigue fuera de git.
- [x] `mvn test` en verde; contrato API sin cambios.

---

## Acuerdos cerrados (PASO 1)

| Tema | Decisión |
|------|----------|
| Documentación | Sección en **`readme.md`** (no doc separada). |
| `.env.example` | **Sí**, en raíz, sin secretos. |
| Java | **17+** documentado como requisito de ejecución. |
| Tests JAR | Smoke **manual** (A); test subproceso **aplazado** (B). |
| Perfiles arranque | **A:** sin vars, modo log. **B:** vars Resend + `EMAIL_SENDER_MODE=http`. |
| Puerto | **`SERVER_PORT`** documentado; default 8080. |
| Contrato API | **Sin cambios**. |

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-06-14 | PASO 1 cerrado: debate acuerdos anfitrión; requirements redactado. |
| 2026-06-14 | PASO 6 cerrado: README, `.env.example`, smoke M-01–M-04, 36 tests verdes. |
