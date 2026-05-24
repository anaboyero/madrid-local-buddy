# Slice 1.5 — Plan técnico (email HTTP / Resend)

**Estado:** hecho (PASO 6).  
**Requirements:** [`requirements.md`](requirements.md) (acordado).  
**Prerrequisito:** 1.4 en `main` — `LogEmailSender`, `EmailHostNotifier`, `503` en fallo de notificación.

---

## Objetivo técnico

Añadir un adaptador **`HttpEmailSender`** que envíe correo real vía **Resend** (API REST), seleccionable con `EMAIL_SENDER_MODE=log|http`, con **fail-fast** al arrancar si falta config en modo `http`. Sin cambios en contrato HTTP ni en la lógica de validación del `POST`.

---

## Proveedor: Resend

| Aspecto | Valor |
|---------|--------|
| Endpoint default | `POST https://api.resend.com/emails` (`EMAIL_API_URL` override opcional) |
| Auth | Header `Authorization: Bearer {EMAIL_API_KEY}` |
| Content-Type | `application/json` |
| User-Agent | **Obligatorio** en Resend — peticiones sin él → `403` |
| Cuerpo mínimo | `{ "from", "to", "subject", "text" }` — usamos **`text`** (plain), según formato en [`requirements.md`](requirements.md) |
| `to` | Array de strings: `["boyeromail@gmail.com"]` |
| Éxito | HTTP `200` + JSON `{ "id": "..." }` |
| Fallo | `4xx` / `5xx` / timeout / red → excepción en `HttpEmailSender` → `503` vía `EmailHostNotifier` (sin cambio) |

### Valores sandbox (smoke manual)

| Variable | Valor de prueba |
|----------|-----------------|
| `EMAIL_SENDER_MODE` | `http` |
| `HOST_NOTIFICATION_EMAIL` | `boyeromail@gmail.com` |
| `EMAIL_FROM` | `onboarding@resend.dev` (o formato `"Madrid Local Buddy <onboarding@resend.dev>"`) |
| `EMAIL_API_KEY` | Clave de cuenta Resend (solo entorno local / IDE — **nunca** en git) |

Documentar en `specs/techstack.md` al implementar (no duplicar secretos en repo).

---

## Diseño

### Diagrama

```text
POST /api/requests  →  RequestsController  (sin cambios)
                              ↓
              HostNotifier.notify(request)
                              ↓
              EmailHostNotifier  — asunto y cuerpo según requirements
                              ↓
              CompositeEmailSender.send(to, subject, body)
                    │
                    ├─→ LogEmailSender  (SIEMPRE → SLF4J)
                    │
                    └─→ deliverySender
                           ├─ mode=log  → NoOpEmailSender (no envía)
                           └─ mode=http → HttpEmailSender → POST Resend /emails
```

### Piezas

| Pieza | Paquete | Acción |
|-------|---------|--------|
| `EmailSender` | `infrastructure` | **Sin cambios** — `void send(String to, String subject, String body)` |
| `LogEmailSender` | `infrastructure` | **Sin cambios** — siempre usado vía composite |
| `NoOpEmailSender` | `infrastructure` | **Nueva** — `send` sin efecto (modo `log`) |
| `CompositeEmailSender` | `infrastructure` | **Nueva** — log + delegado de entrega |
| `HttpEmailSender` | `infrastructure` | **Nueva** — REST a Resend; lanza `RuntimeException` si falla |
| `EmailHostNotifier` | `application` | **Refactor menor** — destinatario inyectado (no constante hardcodeada) |
| `EmailSenderProperties` | `config` | **Nueva** — lee env vars; valida fail-fast en modo `http` |
| `AppConfig` | `config` | **Refactor** — elige impl según mode; construye `EmailHostNotifier` con destinatario correcto |
| Controladores / validador / mapper | — | **Sin cambios** |

### Refactor `EmailHostNotifier`

Hoy usa constante `host@localhost`. Cambio:

```java
public EmailHostNotifier(EmailSender emailSender, String hostNotificationEmail)
```

| Modo | Valor de `hostNotificationEmail` |
|------|-----------------------------------|
| `log` | `"host@localhost"` (fijo en wiring, aunque exista `HOST_NOTIFICATION_EMAIL`) |
| `http` | Valor de `HOST_NOTIFICATION_EMAIL` |

### Contenido del email (`EmailHostNotifier`)

Actualizar en este slice (modos `log` y `http`):

| Parte | Formato |
|-------|---------|
| **Asunto** | `You got a new experience request: ` + `request.experience().title()` |
| **Cuerpo** | Plain text en inglés con **todos** los campos de `ExperienceRequest` (ver [`requirements.md`](requirements.md)) |

```text
Experience: {title}
Description: {description}
Visitor email: {email}
Native English speaker: {yes|no}
Preferred date or time: {comment}
```

El título de la experiencia puede aparecer en asunto y en cuerpo.

Ejemplo asunto (Cinema): `You got a new experience request: Cinema`

Los tests de `EmailHostNotifierTest` se actualizan en PASO 4 para reflejar este formato.

### `HttpEmailSender`

Responsabilidades:

- Recibir en constructor: `apiUrl`, `apiKey`, `from` (ya resueltos desde properties).
- En `send(to, subject, body)`: POST JSON a Resend con `from`, `to: [to]`, `subject`, `text: body`.
- Headers: `Authorization`, `Content-Type`, `User-Agent` (p. ej. `madrid-local-buddy/0.0.1`).
- Si status no es 2xx → lanzar excepción (mensaje útil en log, sin filtrar API key).

**Cliente HTTP:** `RestClient` (incluido en Spring Boot 3 / `spring-boot-starter-web`) — **sin SDK Resend**, **sin dependencia Maven nueva**.

### `EmailSenderProperties` + fail-fast

Clase `@Configuration` o `@ConfigurationProperties` en `config` que lee:

| Env var | Campo | Default |
|---------|-------|---------|
| `EMAIL_SENDER_MODE` | `mode` | `log` |
| `HOST_NOTIFICATION_EMAIL` | `hostNotificationEmail` | — |
| `EMAIL_FROM` | `from` | — |
| `EMAIL_API_KEY` | `apiKey` | — |
| `EMAIL_API_URL` | `apiUrl` | `https://api.resend.com/emails` |

Método `validateForStartup()` (llamado desde `@Bean` o `@PostConstruct`):

- Si `mode` es `log` → OK (ignorar resto).
- Si `mode` es `http` → comprobar que `hostNotificationEmail`, `from`, `apiKey` no están blank → si falta alguna, **`IllegalStateException`** con nombre de variable claro → Spring no arranca.

**Sin** `spring-boot-starter-validation` — validación manual para no añadir dependencia (salvo que prefieras Bean Validation).

### `CompositeEmailSender` + `NoOpEmailSender`

```java
// Siempre: log primero, luego entrega
void send(to, subject, body) {
    logSender.send(to, subject, body);
    deliverySender.send(to, subject, body);
}
```

| Modo | `deliverySender` |
|------|------------------|
| `log` | `NoOpEmailSender` |
| `http` | `HttpEmailSender` |

### Wiring en `AppConfig`

```text
1. EmailSenderProperties (bean) — lee EMAIL_SENDER_MODE y vars HTTP
2. properties.validateForStartup()
3. EmailSender bean:
     logSender = new LogEmailSender()
     delivery = log ? new NoOpEmailSender() : new HttpEmailSender(...)
     → new CompositeEmailSender(logSender, delivery)
4. HostNotifier bean:
     recipient = log ? "host@localhost" : properties.hostNotificationEmail()
     → new EmailHostNotifier(emailSender, recipient)
```

---

## Dependencias Maven

| Dependencia | ¿Nueva? | Notas |
|-------------|---------|--------|
| `spring-boot-starter-web` | No | Ya presente; incluye `RestClient` |
| SDK Resend | **No** | Solo HTTP genérico + JSON manual o record DTO interno |
| WireMock / Testcontainers | **No** en MVP | Tests con `MockRestServiceServer` + `RestClient` (spring-test) |

---

## Orden de construcción (implementación PASO 6)

1. `EmailSenderProperties` + fail-fast.
2. `CompositeEmailSender` + `NoOpEmailSender`.
3. Refactor `EmailHostNotifier` (destinatario inyectado + **nuevo asunto/cuerpo**).
4. `HttpEmailSender` + `ResendEmailRequest` record + tests unitarios con servidor HTTP mock.
5. `AppConfig` — composite + selección de `deliverySender` (`log` / `http`).
6. Test de contexto: `EMAIL_SENDER_MODE=http` sin API key → contexto **no** carga.
7. Regresión suite completa en modo `log` (default tests).
8. Actualizar `specs/techstack.md` (Resend concreto, env vars, log siempre).
9. Smoke manual según [`validation.md`](validation.md).

---

## Tests previstos (detalle en `validation.md`)

| Test | Capa | Enfoque |
|------|------|---------|
| `CompositeEmailSenderTest` | unit | siempre log; delivery solo si no es NoOp |
| `HttpEmailSenderTest` | unit | Mock HTTP: 200 → OK; 401/500 → excepción |
| `EmailSenderPropertiesTest` | unit | fail-fast si `http` + falta key |
| `EmailHostNotifierTest` | unit | destinatario al `send()`; asunto y cuerpo según requirements |
| `EmailSenderStartupTest` | integration | `@SpringBootTest` + env `http` incompleto → contexto no arranca |
| Suite existente | — | Sin regresión en `log` (MockMvc sigue con `@MockBean HostNotifier`) |

**Sin** correo real en CI ni en `mvn test`.

---

## Smoke manual (referencia rápida)

1. Exportar env vars (`http`, API key, From sandbox, `boyeromail@gmail.com`).
2. `mvn spring-boot:run` — debe arrancar.
3. `curl -X POST .../api/requests` con payload válido → `201`.
4. Comprobar bandeja: asunto `You got a new experience request: {title}`; cuerpo con experience (título + descripción), visitor email, native English speaker y preferred date/time.

---

## Fuera de este plan

- Cambios en `api-contract.md` (no hay).
- Persistencia, UI, dominio verificado en Resend.
- Idempotency-Key de Resend (opcional futuro).
- Retry / cola de emails.

---

## Acuerdos técnicos cerrados (PASO 2–3)

| Tema | Decisión |
|------|----------|
| Cliente HTTP | **`RestClient`** |
| Fail-fast config | Validación **manual** en `EmailSenderProperties` (sin `spring-boot-starter-validation`) |
| DTO Resend | **Record** `ResendEmailRequest` en `infrastructure` |
| Test arranque | **`EmailSenderStartupTest`** — contexto no carga con `http` + config incompleta |
| Deps Maven | Sin nuevas (solo `spring-boot-starter-web`) |

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-24 | Borrador inicial PASO 2–3; Resend, HttpEmailSender, fail-fast, refactor destinatario en EmailHostNotifier. |
| 2026-05-24 | PASO 2–3 cerrado: RestClient, fail-fast manual, record DTO, test de contexto; asunto/cuerpo email actualizados. |
| 2026-05-24 | Cuerpo email: todos los datos de dominio (repetición título en asunto/cuerpo aceptada). |
| 2026-05-24 | CompositeEmailSender: log siempre; NoOp / Http según modo. |
