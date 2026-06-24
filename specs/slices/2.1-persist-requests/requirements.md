# Slice 2.1 — Persistencia de solicitudes

**Estado:** hecho (PASO 6).  
**Historia de usuario:** primera capacidad de **Fase 2** — el anfitrión no pierde solicitudes válidas al reiniciar la app o si falla el email.  
**Prerrequisito:** Fase 1 API **completa** (slices 1.2–1.6).  
**Referencias:** [`api-contract.md`](../../api-contract.md), [`techstack.md`](../../techstack.md), [`roadmap.md`](../../roadmap.md), [`slice-post-reserva-experiencia.md`](../../slice-post-reserva-experiencia.md).

---

## Historia

Como anfitrión, quiero que cada **solicitud válida** quede **guardada** en el servidor, para poder consultarla después aunque reinicie la aplicación o falle el envío de email.

---

## Alcance de este slice

| Incluido | Fuera |
|----------|-------|
| Guardar solicitudes válidas tras validación | UI / panel de administración (Fase 1b) |
| Puerto de persistencia en dominio/aplicación | Autenticación / autorización |
| Adaptador con **H2** en fichero | Postgres / cloud |
| `GET /api/requests` + `POST` con `id` en `201` | Calendario, más experiencias, admin |
| Tests TDD alineados con `validation.md` | Docker |
| Smoke JAR en CI (ex 1.7) | — |

**Idioma público API:** inglés (sin cambio).

---

## Comportamiento observable

### `POST /api/requests` — sin cambio en validación

La validación existente (`400` con `errors`) **no cambia**.

### Cuándo se persiste

| Caso | ¿Se guarda? | HTTP |
|------|-------------|------|
| Payload inválido | **No** | `400` |
| Payload válido, email enviado (o log en perfil A) | **Sí** | `201` |
| Payload válido, fallo al notificar | **Sí** | `503` |

La solicitud se **guarda antes** de notificar. Un `503` no implica pérdida de datos.

### Respuesta `201`

```json
{
  "ok": true,
  "id": 1
}
```

- `id`: entero positivo, único y estable.
- `503` y `400`: **sin** `id`.

### `GET /api/requests`

Listar solicitudes guardadas (sin auth en este slice).

#### `200 OK`

```json
[
  {
    "id": 1,
    "experienceId": 1,
    "experienceTitle": "Cinema",
    "visitorEmail": "visitor@example.com",
    "comment": "Saturday afternoon would work best for me",
    "nativeEnglishSpeaker": true,
    "createdAt": "2026-06-24T10:15:30Z"
  }
]
```

- Orden: **más recientes primero** (`createdAt` descendente).
- `experienceTitle` resuelto desde el catálogo.
- `createdAt`: instante UTC ISO-8601.

#### `405 Method Not Allowed`

Solo `GET` y `POST` (p. ej. `PUT` / `DELETE` → `405`).

### Datos almacenados

| Campo | Origen |
|-------|--------|
| `id` | Generado al guardar |
| `experienceId` | Payload |
| `visitorEmail` | Payload |
| `comment` | Payload |
| `nativeEnglishSpeaker` | Payload |
| `createdAt` | Servidor al guardar |

### Arranque y JAR

- H2 en fichero bajo `./data/`: las solicitudes **sobreviven** a reinicio de `java -jar`.
- Perfil A (sin vars email): sigue arrancando; persistencia activa por defecto.

---

## Acuerdos cerrados (PASO 1)

| Tema | Decisión |
|------|----------|
| ID slice | `2.1-persist-requests` |
| Motor | **H2** file-based |
| `GET /api/requests` | **Sí** en este slice |
| `503` + persistencia | **Sí** guardar igualmente |
| `201` devuelve `id` | **Sí** |
| Auth en `GET` | **No** |
| Ubicación fichero H2 | `./data/` relativo al cwd (documentar en README) |
| Slice 1.7 CI JAR | **Descartada** |

---

## Criterios de éxito (requirements)

- [x] Solicitud válida queda almacenada y obtiene `id` único.
- [x] Tras reinicio de la app, las solicitudes anteriores siguen listables.
- [x] `400` no crea registros.
- [x] Comportamiento de email/notificación sin regresión (1.4 / 1.5).
- [x] `mvn test` en verde; contrato actualizado en `api-contract.md` al cerrar slice.
- [x] `techstack.md` actualizado: persistencia ya no es «ninguna».

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-06-24 | Borrador PASO 1: opción A (persistencia); 1.7 CI JAR descartada. |
| 2026-06-24 | PASO 1 cerrado: H2, GET, persistir en 503, `id` en 201. |
