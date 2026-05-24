# Slice 1.5 — Email real vía HTTP (REST)

**Estado:** acordado (PASO 1 cerrado).  
**Historia de usuario:** continúa [`primera-historia-especificacion.md`](../../primera-historia-especificacion.md).  
**Prerrequisito:** slice notificación (1.4) **hecha** en `main` — [`slice-host-notification.md`](../../slice-host-notification.md).  
**Referencias:** [`api-contract.md`](../../api-contract.md), [`techstack.md`](../../techstack.md), [`mission.md`](../../mission.md).

---

## Historia

Como anfitrión, quiero **recibir en mi bandeja real** el email de notificación cuando un visitante hace una reserva válida, para poder responder sin depender de logs del servidor.

Como desarrolladora, quiero poder **elegir entre modo log y modo HTTP** para no enviar correo real en tests ni en local por defecto.

---

## Alcance de este slice

| Incluido | Fuera (slice posterior) |
|----------|-------------------------|
| Envío de email **real** cuando el modo de transporte es HTTP | Cambios en el contrato HTTP (`POST /api/requests`) |
| Adaptador **HTTP REST** genérico (proveedor concreto en `plan.md`) | Otros canales (`WhatsAppHostNotifier`, etc.) |
| Modo **`log`** (default) — comportamiento actual de 1.4 | Persistencia de solicitudes |
| Modo **`http`** — envío real con variables de entorno | UI (Fase 1b) |
| Destinatario configurable (`HOST_NOTIFICATION_EMAIL`) en envío real | Verificación de dominio propio del remitente |
| **Formato de asunto y cuerpo** del email de notificación (ver abajo) | CI en la nube |
| `503` si falla el envío HTTP (igual que 1.4) | |

**Idioma del email y de la API:** inglés (sin cambios respecto a 1.4).

---

## Comportamiento observable

### API — sin cambios de contrato

El endpoint `POST /api/requests` mantiene exactamente las respuestas acordadas en [`api-contract.md`](../../api-contract.md):

| Código | Cuándo |
|--------|--------|
| `201` | Reserva válida **y** notificación enviada correctamente |
| `400` | Datos inválidos — **no** se intenta notificar |
| `503` | Reserva válida pero **fallo al notificar** al anfitrión |
| `405` | Método distinto de `POST` |

Flujo HTTP 1.4: validar → mapear a dominio → notificar → responder.

### Contenido del email de notificación

Aplica en modo **`log`** y **`http`** (mismo texto; solo cambia si se envía correo real o se escribe en log).

**Asunto**

```text
You got a new experience request: {experience.title}
```

Ejemplo (experiencia Cinema): `You got a new experience request: Cinema`

**Cuerpo** (plain text, inglés) — **todos** los datos de la reserva disponibles en dominio. Puede repetir el título de la experiencia aunque ya figure en el asunto.

```text
Experience: {experience.title}
Description: {experience.description}
Visitor email: {visitor.email}
Native English speaker: {yes|no}
Preferred date or time: {comment}
```

Ejemplo (Cinema):

```text
Experience: Cinema
Description: An evening at a local cinema — film and conversation in English.
Visitor email: visitor@example.com
Native English speaker: yes
Preferred date or time: Saturday afternoon would work best for me
```

### Log siempre + envío real opcional

Cada notificación **siempre** se escribe en log (destinatario, asunto, cuerpo). El envío de correo **real** es opcional y se controla con `EMAIL_SENDER_MODE`.

| `EMAIL_SENDER_MODE` | Default | Log (SLF4J) | Envío real (HTTP) |
|---------------------|---------|-------------|-------------------|
| `log` | **Sí** (si no se configura) | **Sí** | **No** |
| `http` | — | **Sí** | **Sí** (API REST del proveedor) |

**Dónde se configura:** variable de entorno `EMAIL_SENDER_MODE` al arrancar la app (IDE, PowerShell, despliegue). El código la lee en `EmailSenderProperties` y `AppConfig` elige el adaptador de entrega (ver [`plan.md`](plan.md)).

### Modo `log` (solo log, sin envío real)

Cuando `EMAIL_SENDER_MODE=log` (o sin variable):

- **Siempre** aparece una línea de log con el email (formato acordado arriba).
- **No** se llama al proveedor HTTP.
- Destinatario pasado al log: **dummy** (`host@localhost`), aunque exista `HOST_NOTIFICATION_EMAIL` — sin side effects.
- Tests automatizados y desarrollo local usan este modo por defecto.

### Modo `http` (log + envío real)

Cuando `EMAIL_SENDER_MODE=http`:

1. **Siempre** se escribe el mismo contenido en log (con destinatario real `HOST_NOTIFICATION_EMAIL`).
2. Además se envía correo real vía API REST; el anfitrión lo recibe en su bandeja.
3. El **remitente** es `EMAIL_FROM` (autorizado en el proveedor; sandbox hasta dominio propio).
4. Si la llamada HTTP **falla**, la API responde **`503`** con `{ "ok": false, "message": "Unable to notify host" }`.
5. Si tiene **éxito**, **`201`** con `{ "ok": true }`.

### Variables de entorno (comportamiento, no implementación)

| Variable | Obligatoria | Modo | Notas |
|----------|-------------|------|--------|
| `EMAIL_SENDER_MODE` | No | ambos | `log` (default) \| `http` |
| `HOST_NOTIFICATION_EMAIL` | Sí | `http` | Bandeja del anfitrión. **Pruebas manuales:** `boyeromail@gmail.com` |
| `EMAIL_FROM` | Sí | `http` | Remitente autorizado en el proveedor |
| `EMAIL_API_KEY` | Sí | `http` | Clave API — **nunca** en git |
| `EMAIL_API_URL` | No | `http` | URL del endpoint REST; default acordado en `plan.md` |

### Config incompleta en modo `http` — fail-fast

Si `EMAIL_SENDER_MODE=http` y falta alguna variable obligatoria (`HOST_NOTIFICATION_EMAIL`, `EMAIL_FROM`, `EMAIL_API_KEY`), la aplicación **no arranca** y muestra un error claro en log. No se aceptan reservas hasta corregir la configuración.

---

## Smoke manual (modo `http`)

Tras levantar la app con credenciales reales y hacer un `POST /api/requests` válido:

1. El email **llega** a la bandeja configurada en `HOST_NOTIFICATION_EMAIL`.
2. Se **comprueba en el cliente de correo** el **asunto** y el **cuerpo** completos (experiencia con título y descripción, visitante, native English speaker, preferred date/time).

*(El proveedor concreto y valores sandbox de prueba van en `plan.md`.)*

---

## Criterios de éxito (requirements)

- [ ] Con `EMAIL_SENDER_MODE=http` y credenciales válidas, una reserva válida produce un email **real** en `HOST_NOTIFICATION_EMAIL`.
- [ ] Con `EMAIL_SENDER_MODE=log`, no hay regresión respecto a 1.4 (tests y local; destinatario dummy en log).
- [ ] Fallo de envío HTTP → `503` con mensaje acordado; reserva inválida → `400` sin envío.
- [ ] Contrato HTTP sin cambios; documentación de variables de entorno actualizada.
- [ ] Smoke manual: recepción + asunto + cuerpo verificados en cliente de correo.

---

## Acuerdos cerrados (PASO 1)

| Tema | Decisión |
|------|----------|
| Log siempre | Cada notificación se escribe en log en **ambos** modos (`log` y `http`). |
| Modo `log` | Solo log; destinatario dummy en log; sin envío real. |
| Modo `http` | Log + envío real a `HOST_NOTIFICATION_EMAIL`. |
| Smoke manual | Verificar llegada **y** contenido (asunto + cuerpo). |
| Proveedor | Requirements **agnóstico** (HTTP REST); proveedor concreto solo en `plan.md`. |
| Formato email | Asunto: `You got a new experience request: {title}`; cuerpo: experiencia (título + descripción), visitante, native speaker, comment (repetición en asunto aceptada) |
| Config incompleta (`http`) | **Fail-fast al arrancar** si faltan variables obligatorias. |

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-24 | Borrador inicial PASO 1; formato tres documentos. |
| 2026-05-24 | Cierre parcial: log con dummy, smoke con asunto/cuerpo, proveedor agnóstico en requirements. |
| 2026-05-24 | PASO 1 cerrado: fail-fast al arrancar si config `http` incompleta. |
| 2026-05-24 | Asunto/cuerpo email acordados (asunto con título; cuerpo con todos los campos de dominio). |
| 2026-05-24 | Cuerpo incluye información completa (incl. experiencia en cuerpo aunque esté en asunto). |
| 2026-05-24 | Log siempre; envío real opcional (`log` vs `http`). |
