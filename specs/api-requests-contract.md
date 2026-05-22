# API contract — Experience requests

**Estado:** acordado (mínimo para PASO 4 — tests).  
**Implementación:** Spring Boot 3, Maven. Ver [`techstack.md`](techstack.md).

---

## `POST /api/requests`

Crea una solicitud de experiencia y notifica al anfitrión por correo (cuando el adaptador de email está configurado).

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
| `experienceId` | string | Sí | Debe ser un id conocido del catálogo (ver abajo). |
| `visitorEmail` | string | Sí | Formato de email válido. |
| `comment` | string | No | Libre; si falta o está vacío, el email al host indica `not provided`. |
| `yearsInMadrid` | string | No | Contexto del visitante (sustituye “nivel de inglés” alineado con `mission.md`). Si falta o está vacío: `not provided`. |

**Experiencias válidas (`experienceId`)**

| `experienceId` | Título (email / logs) |
|----------------|------------------------|
| `cinema` | Cinema |
| `casa-de-campo-walk` | Casa de Campo walk |

---

### Responses

#### `201 Created`

Solicitud aceptada y notificación encolada/enviada según configuración de email.

```json
{
  "ok": true
}
```

#### `400 Bad Request`

Error de validación (payload inválido o experiencia desconocida).

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

- `errors` es una lista; puede haber varios campos.
- Mensajes en **inglés** (API pública).

#### `405 Method Not Allowed`

Solo se admite `POST` en esta ruta.

#### `502 Bad Gateway` (o `503 Service Unavailable`)

Validación correcta pero fallo al enviar el correo (proveedor caído, API key inválida, etc.).

```json
{
  "ok": false,
  "message": "Unable to send notification email"
}
```

---

## Email al anfitrión (comportamiento esperado)

Generado en el servidor; no es respuesta HTTP al cliente.

**Asunto (ejemplo):** `New Madrid Local Buddy request: Cinema`

**Cuerpo (ejemplo, inglés):**

```text
Experience: Cinema (cinema)
Visitor email: visitor@example.com
Comment: I'd like Saturday afternoon
Years in Madrid: about 2 years
Submitted at: 2026-05-22T14:30:00+02:00
```

Si `comment` o `yearsInMadrid` faltan o están en blanco, usar la línea `not provided` para ese campo.

---

## Ejemplos `curl`

```bash
curl -s -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d "{\"experienceId\":\"cinema\",\"visitorEmail\":\"visitor@example.com\",\"comment\":\"Saturday afternoon\",\"yearsInMadrid\":\"1 year\"}"
```

---

## Historial

| Fecha | Cambio |
|-------|--------|
| 2026-05-22 | Contrato mínimo acordado; `yearsInMadrid` opcional en lugar de `englishLevel`. |
