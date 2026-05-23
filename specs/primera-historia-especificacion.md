# Primera historia — slice vertical mínimo

**Estado:** acordado (PASO 3, 2026-05-23).  
**Contrato HTTP:** [`api-contract.md`](api-contract.md). **Stack:** [`techstack.md`](techstack.md). **UI:** Fase 1b en [`roadmap.md`](roadmap.md).

---

## Historia

Como visitante que busca un plan auténtico en Madrid, quiero **ver las experiencias disponibles**, **elegir una**, **dejar mis datos** y **enviar una solicitud**, para que el anfitrión reciba un correo y pueda responder fuera de la web.

---

## Slice vertical (qué entra en cada fase)

| Capacidad | Fase 1 — API | Fase 1b — UI |
|-----------|--------------|--------------|
| Listar experiencias (id, título, descripción en inglés) | `GET /api/experiences` | Landing consume el GET |
| Enviar solicitud | `POST /api/requests` | Formulario consume el POST |
| Validación (experiencia + email) | API `400` en inglés | Mismos errores en pantalla |
| Notificar al anfitrión | Email vía puerto `EmailSender` | — |
| Confirmación al visitante | `201` + `{ "ok": true }` | Mensaje en pantalla |
| Aviso de privacidad | — | Texto visible en la web |

**Idioma público:** inglés (respuestas API, descripciones del catálogo, email al anfitrión, UI en 1b).

---

## Catálogo (estático, sin administración)

Exactamente **dos** experiencias:

| `id` | `title` (inglés) | `description` (inglés, copy provisional) |
|------|------------------|----------------------------------------|
| `cinema` | Cinema | An evening at a local cinema — film and conversation in English. |
| `casa-de-campo-walk` | Casa de Campo walk | A relaxed walk in Casa de Campo — green Madrid away from the tourist centre. |

El copy se puede pulir en Fase 1b; Fase 1 debe devolver texto legible en inglés para ambas.

---

## Solicitud (`POST /api/requests`)

| Campo | Obligatorio | Notas |
|-------|-------------|--------|
| `experienceId` | Sí | Debe existir en el catálogo. |
| `visitorEmail` | Sí | Formato de email válido. |
| `comment` | No | Si falta o vacío → `not provided` en el email. |
| `yearsInMadrid` | No | Contexto del visitante ([`mission.md`](mission.md)). Si falta o vacío → `not provided`. |

Detalle de respuestas y ejemplos: [`api-contract.md`](api-contract.md).

---

## Email al anfitrión

Tras un `POST` válido, el servidor envía (o registra en modo `log`) un correo en inglés con: experiencia, email del visitante, comentario, años en Madrid, **marca temporal**. Ver formato en el contrato.

Proveedor concreto y envío real: paso **1.5** del roadmap (no bloquea Fase 1 con mock/log).

---

## Fuera de alcance (esta historia)

- Más de dos experiencias o catálogo editable.
- Persistencia de solicitudes, login, pagos, calendario, anti-spam avanzado.
- Correo automático al visitante.
- Cualquier cosa de `readme.md` no listada arriba.

---

## Criterios de hecho

### Fase 1 (API)

- [ ] `GET /api/experiences` devuelve **exactamente** las dos experiencias con `id`, `title` y `description` en inglés.
- [ ] `POST /api/requests` con payload válido → `201` y notificación al anfitrión (email real, mock en tests o `log` en local).
- [ ] Payload inválido o `experienceId` desconocido → `400` con `errors` en inglés.
- [ ] Fallo del adaptador de email tras validación correcta → `502`/`503` según contrato.
- [ ] Prueba manual documentada: `curl` al GET y al POST.

### Fase 1b (UI — aplazada)

- [ ] Landing en inglés: lista desde `GET /api/experiences`, formulario → `POST /api/requests`.
- [ ] No enviar sin experiencia + email válido; errores claros sin borrar el formulario.
- [ ] Confirmación en pantalla tras envío correcto.
- [ ] Aviso de privacidad visible.

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-21 | PASO 1 inicial: 2 experiencias, formulario, email al anfitrión. |
| 2026-05-22 | API-first; `yearsInMadrid`; UI en Fase 1b. |
| 2026-05-23 | Spec simplificada; slice vertical con `GET /api/experiences` + `POST /api/requests`; criterios separados Fase 1 / 1b. |
