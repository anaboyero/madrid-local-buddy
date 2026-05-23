# API contract — Madrid Local Buddy (Fase 1)

**Estado:** acordado (PASO 3, 2026-05-23).  
**Implementación:** Spring Boot 3, Maven. Ver [`techstack.md`](techstack.md).

---

## Catálogo de referencia

| `id` | `title` | Uso |
|------|---------|-----|
| `cinema` | Cinema | GET + POST + email |
| `casa-de-campo-walk` | Casa de Campo walk | GET + POST + email |

---

## `GET /api/experiences`

Devuelve las experiencias disponibles (contenido estático en servidor).

### Response

#### `200 OK`

```json
[
  {
    "id": "cinema",
    "title": "Cinema",
    "description": "An evening at a local cinema — film and conversation in English."
  },
  {
    "id": "casa-de-campo-walk",
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

## `POST /api/requests`

Crea una solicitud y notifica al anfitrión por correo (según configuración de `EmailSender`).

### Request

**Headers**

```http
Content-Type: application/json
```

**Body**

```json
{
  "experienceId": "cinema",
  "visitorEmail": "visitor@example.com",
  "comment": "I'd like Saturday afternoon",
  "yearsInMadrid": "about 2 years"
}
```

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| `experienceId` | string | Sí | Id del catálogo (tabla arriba). |
| `visitorEmail` | string | Sí | Formato de email válido. |
| `comment` | string | No | Si falta o está vacío → `not provided` en el email. |
| `yearsInMadrid` | string | No | Si falta o está vacío → `not provided`. |

### Responses

#### `201 Created`

```json
{
  "ok": true
}
```

#### `400 Bad Request`

```json
{
  "ok": false,
  "errors": [
    {
      "field": "visitorEmail",
      "message": "Invalid email address"
    }
  ]
}
```

- `errors` puede incluir varios campos.
- Mensajes en **inglés**.

#### `405 Method Not Allowed`

Solo se admite `POST` en esta ruta.

#### `502 Bad Gateway` (o `503 Service Unavailable`)

Validación correcta pero fallo al enviar el correo.

```json
{
  "ok": false,
  "message": "Unable to send notification email"
}
```

### Ejemplo `curl`

```bash
curl -s -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d "{\"experienceId\":\"cinema\",\"visitorEmail\":\"visitor@example.com\",\"comment\":\"Saturday afternoon\",\"yearsInMadrid\":\"1 year\"}"
```

---

## Email al anfitrión (comportamiento esperado)

No es respuesta HTTP; lo genera el servidor tras un `POST` válido.

**Asunto (ejemplo):** `New Madrid Local Buddy request: Cinema`

**Cuerpo (ejemplo):**

```text
Experience: Cinema (cinema)
Visitor email: visitor@example.com
Comment: I'd like Saturday afternoon
Years in Madrid: about 2 years
Submitted at: 2026-05-22T14:30:00+02:00
```

Si `comment` o `yearsInMadrid` faltan o están en blanco → línea `not provided` para ese campo.

---

## Historial

| Fecha | Cambio |
|-------|--------|
| 2026-05-22 | Contrato inicial (`api-requests-contract.md`): solo `POST /api/requests`. |
| 2026-05-23 | Renombrado a `api-contract.md`; añadido `GET /api/experiences`; copy en inglés en catálogo. |
