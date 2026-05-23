# API contract — Madrid Local Buddy (Fase 1)

**Estado:** acordado (2026-05-23).  
**Alcance actual:** `GET /api/experiences` y `POST /api/requests` (reserva, sin email). Notificación: roadmap 1.4.

---

## Catálogo de referencia

| `id` | `title` |
|------|---------|
| `1` | Cinema |
| `2` | Casa de Campo walk |

---

## `GET /api/experiences`

Devuelve las experiencias disponibles (contenido estático en servidor).

### Response

#### `200 OK`

```json
[
  {
    "id": 1,
    "title": "Cinema",
    "description": "An evening at a local cinema — film and conversation in English."
  },
  {
    "id": 2,
    "title": "Casa de Campo walk",
    "description": "A relaxed walk in Casa de Campo — green Madrid away from the tourist centre."
  }
]
```

- Array ordenado de forma estable (mismo orden en cada petición).
- Exactamente **dos** elementos en Fase 1.
- Textos en **inglés**.

#### `405 Method Not Allowed`

Solo se admite `GET` en esta ruta.

### Ejemplo `curl`

```bash
curl -s http://localhost:8080/api/experiences
```

---

## `POST /api/requests` (reserva — acordado)

Acepta o rechaza una **reserva** según validez del payload. **Sin envío de email** en este slice (ver roadmap 1.4).

Detalle: [`slice-post-reserva-experiencia.md`](slice-post-reserva-experiencia.md).

### Request

```json
{
  "experienceId": 1,
  "visitorEmail": "visitor@example.com",
  "comment": "Saturday afternoon would work best for me",
  "nativeEnglishSpeaker": true
}
```

### Responses

| Código | Cuándo | Cuerpo |
|--------|--------|--------|
| `201` | Reserva válida | `{ "ok": true }` |
| `400` | Datos inválidos | `{ "ok": false, "errors": [...] }` |
| `405` | Método no `POST` | — |

Email al anfitrión: **aplazado** (slice posterior).

---

## Historial

| Fecha | Cambio |
|-------|--------|
| 2026-05-22 | Contrato inicial: `POST /api/requests`. |
| 2026-05-23 | Añadido `GET /api/experiences`. |
| 2026-05-23 | Slice actual = solo GET; POST aplazado; `nativeEnglishSpeaker` sustituye `yearsInMadrid`. |
| 2026-05-23 | `id` de experiencias y `experienceId` pasan de string a integer (`1` = cinema, `2` = casa de campo). |
