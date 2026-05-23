# Segunda slice vertical — reserva de experiencia (`POST /api/requests`)

**Estado:** acordado (2026-05-23).  
**Historia de usuario:** continúa [`primera-historia-especificacion.md`](primera-historia-especificacion.md).  
**Prerrequisito:** slice GET catálogo **hecho**.  
**Referencias:** [`api-contract.md`](api-contract.md), [`techstack.md`](techstack.md), [`mission.md`](mission.md).

---

## Historia

Como visitante que ya ha visto las experiencias disponibles, quiero **enviar una reserva** (solicitud con fecha/horario preferido) y saber si el servidor la **acepta o rechaza** por datos inválidos.

---

## Alcance de este slice

| Incluido | Fuera (slice posterior) |
|----------|-------------------------|
| Construir / validar `ExperienceRequest` | Envío de email al anfitrión |
| `ExperienceRequestValidator` + catálogo | `EmailSender`, cuerpo de email, `503` por fallo de envío |
| `POST /api/requests` → `201` o `400` | Servicio de notificación (nuevo en slice 3) |
| `GET /api/requests` → `405` | Persistencia en BD |

**Idioma público de la API:** inglés (mensajes de error).

---

## Modelo — `ExperienceRequest`

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| `experienceId` | integer | Sí | Debe existir en el catálogo (`1` o `2`). |
| `visitorEmail` | string | Sí | Formato de email válido. |
| `comment` | string | Sí | Preferencia de **fecha/horario**; no `null`, vacío ni solo espacios. |
| `nativeEnglishSpeaker` | boolean | Sí | Presente en el JSON (`true` / `false`). |

**Mensajes de error (inglés)**

| Campo | Mensaje |
|-------|---------|
| `visitorEmail` | `Invalid email address` |
| `experienceId` | `Unknown experience` |
| `comment` | `Preferred date or time is required` |

---

## HTTP — `POST /api/requests`

**Body válido (ejemplo)**

```json
{
  "experienceId": 1,
  "visitorEmail": "visitor@example.com",
  "comment": "Saturday afternoon would work best for me",
  "nativeEnglishSpeaker": true
}
```

### `201 Created`

Reserva **válida** aceptada (sin efectos secundarios de email en este slice).

```json
{ "ok": true }
```

### `400 Bad Request`

```json
{
  "ok": false,
  "errors": [
    { "field": "visitorEmail", "message": "Invalid email address" }
  ]
}
```

### `405 Method Not Allowed`

Solo `POST` en esta ruta.

---

## Diseño (PASO 3)

```
POST /api/requests  →  RequestsController
                              ↓
              ExperienceRequestValidator ← ExperienceCatalog
```

| Pieza | Paquete | Responsabilidad |
|-------|---------|-------------------|
| `ValidationError` | `domain` | `field` + `message` |
| `ExperienceRequestValidator` | `domain` | ¿`ExperienceRequest` válida frente al catálogo? |
| `RequestsController` | `api` | JSON ↔ dominio; `201` / `400` / `405` |

---

## Tests (PASO 4)

Solo comportamiento de **reserva / validación**:

1. `ExperienceRequestValidatorTest` — dominio.
2. `RequestsControllerMvcTest` — HTTP (`201`, `400`, `405`).

Sin tests de email ni de servicio de notificación.

---

## Criterios de hecho

- [x] Validador: payload válido → sin errores; casos inválidos → errores acordados.
- [x] `POST` válido → `201` + `{ "ok": true }`.
- [x] `POST` inválido → `400` + `errors` en inglés.
- [x] `GET /api/requests` → `405`.
- [x] Tests en verde (18 total con slice GET).
- [x] Contrato POST en [`api-contract.md`](api-contract.md).

---

## Slice siguiente (aplazado)

**Notificación al anfitrión:** servicio de aplicación + `EmailSender` + cuerpo del email + `503` si falla el envío. Spec propia cuando toque (roadmap 1.4+).

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-23 | Slice reducida: solo validación de reserva; email en slice aparte. |
| 2026-05-23 | `comment` obligatorio; ids enteros; `503` y email aplazados. |
| 2026-05-23 | Borrador inicial. |
