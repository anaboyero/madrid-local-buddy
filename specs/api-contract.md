# API contract — Madrid Local Buddy (Fase 1)

**Estado:** acordado (2026-05-23).  
**Alcance actual:** solo `GET /api/experiences`. `POST /api/requests` aplazado (borrador abajo).

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

## `POST /api/requests` (aplazado)

No implementar en el slice actual. Borrador de cuerpo para cuando se retome:

```json
{
  "experienceId": 1,
  "visitorEmail": "visitor@example.com",
  "comment": "I'd like Saturday afternoon",
  "nativeEnglishSpeaker": true
}
```

| Campo | Tipo | Notas |
|-------|------|--------|
| `experienceId` | integer | Id del catálogo (`1`, `2`, …). |
| `visitorEmail` | string | Email del visitante. |
| `comment` | string | Opcional. |
| `nativeEnglishSpeaker` | boolean | Si el visitante es hablante nativo de inglés ([`mission.md`](mission.md)). |

Validación, errores HTTP y email al anfitrión: se definirán al retomar este endpoint.

---

## Historial

| Fecha | Cambio |
|-------|--------|
| 2026-05-22 | Contrato inicial: `POST /api/requests`. |
| 2026-05-23 | Añadido `GET /api/experiences`. |
| 2026-05-23 | Slice actual = solo GET; POST aplazado; `nativeEnglishSpeaker` sustituye `yearsInMadrid`. |
| 2026-05-23 | `id` de experiencias y `experienceId` pasan de string a integer (`1` = cinema, `2` = casa de campo). |
