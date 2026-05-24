# Tercera slice vertical — notificación al anfitrión (`POST /api/requests` + email)

**Estado:** hecho (2026-05-24).  
**Historia de usuario:** continúa [`primera-historia-especificacion.md`](primera-historia-especificacion.md).  
**Prerrequisito:** slice reserva (`POST /api/requests`, validación) **hecha** en `main`.  
**Referencias:** [`api-contract.md`](api-contract.md), [`techstack.md`](techstack.md), [`mission.md`](mission.md).  
**Rama:** `feature/slice-host-notification`.

---

## Historia

Como visitante con una reserva válida, quiero que el anfitrión **reciba una notificación** con los datos de mi solicitud. Si la notificación no puede enviarse, quiero saber que el servidor no pudo completar la operación.

---

## Alcance de este slice

| Incluido | Fuera (slice posterior) |
|----------|-------------------------|
| Refactor: payload HTTP vs `ExperienceRequest` de dominio | Adaptador HTTP real (Resend, etc.) — roadmap **1.5** |
| Record `Visitor` | Otros canales (`WhatsAppHostNotifier`, etc.) — diseño preparado, impl futura |
| Puerto `HostNotifier` + `EmailHostNotifier` | Persistencia de solicitudes |
| Puerto `EmailSender` + impl **log/fake** (tests y local) | UI (Fase 1b) |
| `POST /api/requests` válido → notificar → `201` o `503` | |
| Validación inválida → `400` (sin notificar) | |

**Idioma público de la API y del cuerpo del email:** inglés.

---

## Modelo — dos tipos + mapper

### Entrada HTTP — `ExperienceRequestPayload` (capa `api`)

Deserializa el JSON **plano** del contrato (sin cambios respecto al slice reserva):

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| `experienceId` | integer | Sí | Debe existir en el catálogo (`1` o `2`). |
| `visitorEmail` | string | Sí | Formato de email válido. |
| `comment` | string | Sí | Preferencia de **fecha/horario**; no `null`, vacío ni solo espacios. |
| `nativeEnglishSpeaker` | boolean | Sí | Presente en el JSON (`true` / `false`). |

**Mensajes de error (inglés)** — sin cambios:

| Campo | Mensaje |
|-------|---------|
| `visitorEmail` | `Invalid email address` |
| `experienceId` | `Unknown experience` |
| `comment` | `Preferred date or time is required` |

### Dominio — `ExperienceRequest` (enriquecido, post-validación)

Solo se construye cuando el payload es válido. **No** se deserializa del JSON.

| Campo | Tipo | Origen |
|-------|------|--------|
| `experience` | `Experience` | Catálogo (`findById(experienceId)`) — única fuente de verdad |
| `visitor` | `Visitor` | Email y flag del payload |
| `comment` | string | Del payload |

### `Visitor`

| Campo | Tipo |
|-------|------|
| `email` | string |
| `nativeEnglishSpeaker` | boolean |

### Mapper

Tras validación exitosa del payload:

```text
ExperienceRequestPayload + ExperienceCatalog
  → ExperienceRequestMapper.toDomain(payload, catalog)
  → ExperienceRequest
```

El catálogo **no** se inyecta en `EmailHostNotifier`; la experiencia ya viene resuelta en el dominio.

---

## Notificación al anfitrión

### Puerto `HostNotifier` (aplicación)

```text
void notify(ExperienceRequest request)
```

Contrato: comunicar al anfitrión que hay una reserva válida. Canal agnóstico (email hoy; WhatsApp u otros mañana).

**Excepción / resultado de fallo:** si el canal no puede notificar, la capa API responde `503` (ver HTTP abajo). La interfaz puede lanzar una excepción de aplicación o devolver un tipo de resultado — decisión de implementación en PASO 6; el comportamiento HTTP es el criterio de aceptación.

### `EmailHostNotifier` (aplicación / infra)

Única implementación en este slice. Construye asunto y cuerpo del email en inglés a partir de:

- `request.experience().title()` (y opcionalmente `description` si aporta contexto)
- `request.visitor().email()`
- `request.visitor().nativeEnglishSpeaker()`
- `request.comment()`

Delega el envío en `EmailSender`.

> **Formato vigente (slice 1.5):** asunto y cuerpo actualizados en [`slices/1.5-resend-email/requirements.md`](slices/1.5-resend-email/requirements.md) — asunto `You got a new experience request: {title}`; cuerpo con experiencia (título + descripción), visitante y comment. Lo implementado en 1.4 usaba otro asunto hasta aplicar 1.5.

### Puerto `EmailSender` (infraestructura)

Transporte de email desacoplado del proveedor:

```text
void send(String to, String subject, String body)
```

(o firma equivalente acordada en implementación).

| Implementación | Uso |
|----------------|-----|
| **Log / fake** | Tests y desarrollo local (`EMAIL_SENDER_MODE=log`, default) — escribe destinatario, asunto y cuerpo en log; no envía correo real |
| **HTTP REST** | Roadmap **1.5** — no en este slice |

En modo `log`, destinatario dummy aceptable (`host@localhost`) si no hay `HOST_NOTIFICATION_EMAIL` configurada.

---

## HTTP — `POST /api/requests` (actualizado)

**Body válido** — sin cambios:

```json
{
  "experienceId": 1,
  "visitorEmail": "visitor@example.com",
  "comment": "Saturday afternoon would work best for me",
  "nativeEnglishSpeaker": true
}
```

### Flujo

```text
POST /api/requests
  → deserializar ExperienceRequestPayload
  → ExperienceRequestValidator.validate(payload, catalog)
  → si errores: 400 (no se llama a HostNotifier)
  → mapper → ExperienceRequest
  → HostNotifier.notify(request)
  → si OK: 201
  → si fallo de notificación: 503
```

### `201 Created`

Reserva válida **y** notificación enviada correctamente.

```json
{ "ok": true }
```

### `400 Bad Request`

Sin cambios. No se intenta notificar.

```json
{
  "ok": false,
  "errors": [
    { "field": "visitorEmail", "message": "Invalid email address" }
  ]
}
```

### `503 Service Unavailable`

Reserva válida pero **fallo al notificar** al anfitrión (p. ej. `EmailSender` lanza o devuelve error).

```json
{
  "ok": false,
  "message": "Unable to notify host"
}
```

### `405 Method Not Allowed`

Solo `POST` en esta ruta.

---

## Diseño (PASO 3)

```text
POST /api/requests  →  RequestsController
                              ↓
              ExperienceRequestValidator ← ExperienceCatalog
                              ↓ (si válido)
              ExperienceRequestMapper → ExperienceRequest
                              ↓
              HostNotifier.notify(request)
                              ↓
              EmailHostNotifier → EmailSender (log/fake)
```

| Pieza | Paquete | Responsabilidad |
|-------|---------|-------------------|
| `ExperienceRequestPayload` | `api` | JSON plano de entrada |
| `ExperienceRequest` | `domain` | Reserva validada enriquecida |
| `Visitor` | `domain` | Datos del visitante |
| `ExperienceRequestValidator` | `domain` | Validar payload frente al catálogo |
| `ExperienceRequestMapper` | `application` o `api` | Payload + catálogo → dominio |
| `HostNotifier` | `application` | Puerto: notificar al anfitrión |
| `EmailHostNotifier` | `application` / `infra` | Notificación por email |
| `EmailSender` | `infra` | Transporte email (log en 1.4) |
| `RequestsController` | `api` | Orquestación HTTP; `201` / `400` / `503` / `405` |

---

## Tests (PASO 4)

Comportamiento acordado (TDD estricto — rojo antes de PASO 6):

1. **`ExperienceRequestValidatorTest`** — adaptar a validación sobre **payload** (mismos casos de error).
2. **`ExperienceRequestMapperTest`** (opcional) — payload válido + catálogo → `ExperienceRequest` con `Experience` y `Visitor` correctos.
3. **`EmailHostNotifierTest`** — con `EmailSender` mockeado: verifica asunto/cuerpo/destinatario con datos del dominio.
4. **`RequestsControllerMvcTest`** — ampliar:
   - `POST` válido + notifier OK → `201`
   - `POST` válido + notifier falla → `503` + cuerpo acordado
   - Casos `400` y `405` siguen pasando
5. Mock de `HostNotifier` o `EmailSender` según capa bajo test; **sin** correo real.

---

## Criterios de hecho

- [x] Payload HTTP plano sin cambios de contrato; dominio enriquecido tras validación.
- [x] `Visitor` y mapper documentados e implementados.
- [x] `HostNotifier` + `EmailHostNotifier` + `EmailSender` (log) en verde.
- [x] `POST` válido + notificación OK → `201`.
- [x] `POST` válido + fallo notificación → `503` + `{ "ok": false, "message": "Unable to notify host" }`.
- [x] `POST` inválido → `400`; no se invoca notificación.
- [x] Tests en verde; CI pendiente de PR.
- [x] Contrato actualizado en [`api-contract.md`](api-contract.md).

---

## Slice siguiente (aplazado)

**1.5 — Adaptador email HTTP real:** `HttpEmailSender`, variables de entorno, `HOST_NOTIFICATION_EMAIL` obligatoria en envío real.

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-24 | Spec acordada: `HostNotifier`, payload/dominio, `Visitor`, `503`, rama `feature/slice-host-notification`. |
| 2026-05-23 | Aplazado desde slice reserva (solo validación). |
