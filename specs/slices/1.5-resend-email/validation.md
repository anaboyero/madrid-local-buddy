# Slice 1.5 — Validación (tests y smoke)

**Estado:** hecho (PASO 6 — 36 tests verdes).  
**Requirements:** [`requirements.md`](requirements.md) (acordado).  
**Plan:** [`plan.md`](plan.md) (acordado).  
**Contrato API:** [`api-contract.md`](../../api-contract.md) — sin cambios en este slice.

---

## Criterio de salida PASO 4

- Tests nuevos en **rojo** (`mvn test` falla por comportamiento no implementado).
- Cada escenario automatizado abajo tiene **clase de test** y **nombre de método** acordados.
- **Sin** correo real ni llamadas a Resend en `mvn test`.
- Suite existente (1.2–1.4) sigue pasando en modo `log` por defecto tras añadir tests (puede fallar solo lo nuevo hasta PASO 6).

---

## Datos de referencia (contrato de pruebas)

Fuente: [`ContractRequests.java`](../../../src/test/java/com/madridlocalbuddy/support/ContractRequests.java), [`ContractCatalog.java`](../../../src/test/java/com/madridlocalbuddy/support/ContractCatalog.java).

### Reserva válida (Cinema)

| Campo | Valor |
|-------|--------|
| `experienceId` | `1` |
| `visitorEmail` | `visitor@example.com` |
| `comment` | `Saturday afternoon would work best for me` |
| `nativeEnglishSpeaker` | `true` |

### Email esperado (`EmailHostNotifier`)

**Asunto**

```text
You got a new experience request: Cinema
```

**Cuerpo** (orden y etiquetas exactas)

```text
Experience: Cinema
Description: An evening at a local cinema — film and conversation in English.
Visitor email: visitor@example.com
Native English speaker: yes
Preferred date or time: Saturday afternoon would work best for me
```

**Variante** `nativeEnglishSpeaker: false` → línea `Native English speaker: no` (resto igual con experiencia correspondiente).

### Destinatario según modo

| Modo | `EmailSender.send(to, …)` — `to` |
|------|----------------------------------|
| `log` | `host@localhost` |
| `http` | valor de `HOST_NOTIFICATION_EMAIL` (p. ej. `host@example.com` en tests) |

---

## Escenarios automatizados

### 1. `EmailHostNotifierTest` (unit)

Prueba la clase que **monta el asunto y el cuerpo** del email y llama a `EmailSender`. No envía correo real: el `EmailSender` va mockeado.

| ID | Escenario | Given | When | Then |
|----|-----------|-------|------|------|
| N-01 | Reserva estándar (visitante **sí** es native speaker) | `EmailSender` mock; destinatario de prueba `host@example.com`; reserva válida Cinema con `nativeEnglishSpeaker: **true**` (`ContractRequests.VALID_DOMAIN`) | `notify(request)` | Se llama `send(to, subject, body)` con `to = host@example.com`; `subject` = `You got a new experience request: Cinema`; `body` incluye las 5 líneas del contrato (Experience, Description, Visitor email, **Native English speaker: yes**, Preferred date or time) |
| N-02 | Visitante **no** es native English speaker | Igual que N-01, pero la reserva tiene `nativeEnglishSpeaker: **false**` (mismo cine; email/comment pueden ser los mismos) | `notify(request)` | En el cuerpo aparece la línea exacta **`Native English speaker: no`** (no `yes`) |
| N-03 | Fallo al enviar | `EmailSender` mock que lanza `RuntimeException` al hacer `send` | `notify(request)` | Se lanza `HostNotificationException` con mensaje `Unable to notify host` |

**Qué es «native speaker no» (N-02):** en el formulario de reserva el visitante indica si habla inglés como lengua materna (`nativeEnglishSpeaker` en el JSON). Si es `false`, el email al anfitrión debe decir `Native English speaker: no`. El test comprueba que esa línea se genera bien; no es un escenario distinto de producto, solo un **caso borde** del mismo campo booleano.

**Métodos sugeridos:** `notify_sendsFullSubjectAndBodyToConfiguredRecipient`, `notify_whenVisitorIsNotNativeEnglishSpeaker_bodyContainsNo`, `notify_whenEmailSenderFails_throwsHostNotificationException`.

---

### 2. `EmailSenderPropertiesTest` (unit)

| ID | Escenario | Given | When | Then |
|----|-----------|-------|------|------|
| P-01 | Modo log válido | `EMAIL_SENDER_MODE=log` (resto vacío) | `validateForStartup()` | no lanza |
| P-02 | Modo http completo | `http` + email host + from + apiKey | `validateForStartup()` | no lanza |
| P-03 | Falta API key | `http` sin `EMAIL_API_KEY` | `validateForStartup()` | `IllegalStateException`; mensaje menciona `EMAIL_API_KEY` |
| P-04 | Falta host email | `http` sin `HOST_NOTIFICATION_EMAIL` | `validateForStartup()` | `IllegalStateException`; mensaje menciona `HOST_NOTIFICATION_EMAIL` |
| P-05 | Falta from | `http` sin `EMAIL_FROM` | `validateForStartup()` | `IllegalStateException`; mensaje menciona `EMAIL_FROM` |
| P-06 | Default mode | sin `EMAIL_SENDER_MODE` | leer mode | `log` |

**Métodos sugeridos:** `validateForStartup_logMode_succeeds`, `validateForStartup_httpModeWithAllVars_succeeds`, `validateForStartup_httpModeMissingApiKey_throws`, etc.

---

### 3. `CompositeEmailSenderTest` (unit)

Verifica que **log y entrega** están separados: el log se invoca siempre; el delegado de entrega depende del modo.

| ID | Escenario | Given | When | Then |
|----|-----------|-------|------|------|
| C-01 | Modo solo log | `logSender` mock + `NoOpEmailSender` como delivery | `send(to, subject, body)` | `logSender.send` una vez con mismos args; `delivery` (NoOp) no lanza |
| C-02 | Modo log + HTTP | `logSender` mock + `deliverySender` mock | `send(to, subject, body)` | **ambos** reciben la misma llamada `send(to, subject, body)` |

**Métodos sugeridos:** `send_alwaysInvokesLogSender`, `send_withHttpDelivery_invokesBothLogAndDelivery`.

---

### 4. `HttpEmailSenderTest` (unit, `MockRestServiceServer`)

| ID | Escenario | Given | When | Then |
|----|-----------|-------|------|------|
| H-01 | Envío OK | Mock `POST /emails` → `200` + `{ "id": "…" }` | `send(to, subject, body)` | petición con `Authorization: Bearer …`, header `User-Agent`, JSON `from`, `to: [to]`, `subject`, `text`; sin excepción |
| H-02 | API 401 | Mock → `401` | `send(…)` | lanza `RuntimeException` (o subtipo acordado en impl) |
| H-03 | API 500 | Mock → `500` | `send(…)` | lanza excepción |
| H-04 | URL custom | `EMAIL_API_URL` distinta | `send(…)` | POST a la URL configurada |

**Métodos sugeridos:** `send_whenResendReturns200_completesWithoutException`, `send_whenResendReturns401_throws`, `send_whenResendReturns500_throws`, `send_usesConfiguredApiUrl`.

---

### 5. `EmailSenderStartupTest` (integration)

| ID | Escenario | Given | When | Then |
|----|-----------|-------|------|------|
| S-01 | Fail-fast arranque | `@SpringBootTest`; `EMAIL_SENDER_MODE=http`; sin `EMAIL_API_KEY` (y sin otras obligatorias si aplica) | cargar contexto | `ApplicationContext` **no** arranca (`IllegalStateException` o failure cause con variable faltante) |

**Método sugerido:** `contextLoads_httpModeWithoutApiKey_fails`.

Usar `@SpringBootTest` + `@TestPropertySource` o variables de entorno de test; **no** commitear API keys.

---

### 6. Regresión API (sin cambios — suite existente)

Estos escenarios **ya** están cubiertos; deben seguir en verde en PASO 6 (modo `log` por defecto en tests):

| Clase | Comportamiento |
|-------|----------------|
| `RequestsControllerMvcTest` | `POST` válido → `201`; notifier falla → `503`; inválidos → `400`; `GET` → `405` |
| `ExperienceRequestValidatorTest` | reglas de validación |
| `ExperienceRequestMapperTest` | payload → dominio |
| `ExperiencesControllerMvcTest` | catálogo GET |

**Nota:** `RequestsControllerMvcTest` usa `@MockBean HostNotifier` — no ejercita `HttpEmailSender` ni email real.

---

### 7. Opcional (no obligatorio en PASO 4)

| ID | Escenario | Notas |
|----|-----------|--------|
| O-01 | `LogEmailSenderTest` | Verifica log con `to`, `subject`, `body` recibidos — solo si aporta valor |
| O-02 | `POST` integración con `http` + mock server | End-to-end sin mock de `HostNotifier`; valorar en refactor posterior |

---

## Matriz requisito → test

| Requisito | Tests |
|-----------|--------|
| Asunto/cuerpo email completos | N-01, N-02 |
| `503` si falla envío | N-03 (+ regresión `RequestsControllerMvcTest`) |
| Log siempre + entrega opcional | C-01, C-02 |
| Fail-fast config `http` | P-03–P-05, S-01 |
| `HttpEmailSender` REST Resend | H-01–H-04 |
| Modo `log` default | P-01, P-06 |
| API sin cambios | `RequestsControllerMvcTest` + validador/mapper |
| Destinatario log vs http | N-01 (recipient inject); wiring en PASO 6 / smoke |

---

## Smoke manual (modo `http`)

Prueba **real** con Resend y tu bandeja. No forma parte de `mvn test`. Hazla **después** de PASO 6 (implementación verde).

### Antes de empezar

| Requisito | Detalle |
|-----------|---------|
| Cuenta Resend | API key creada en el panel de Resend |
| Secretos | Solo en variables de entorno del IDE o terminal — **nunca** en git ni en el chat con el agente |
| Destino | Bandeja a la que tienes acceso (acordado: `boyeromail@gmail.com`) |
| Remitente | Sandbox Resend, p. ej. `onboarding@resend.dev` — no uses un `@gmail.com` como From |

### Variables de entorno (valores de ejemplo)

Configúralas **antes** de arrancar la app (Run Configuration en Cursor/IDE, o sesión de terminal):

| Variable | Valor ejemplo |
|----------|----------------|
| `EMAIL_SENDER_MODE` | `http` |
| `HOST_NOTIFICATION_EMAIL` | `boyeromail@gmail.com` |
| `EMAIL_FROM` | `onboarding@resend.dev` |
| `EMAIL_API_KEY` | `re_…` (tu clave real) |

Opcional: `EMAIL_API_URL` solo si pruebas otro endpoint; por defecto Resend usa `https://api.resend.com/emails`.

**PowerShell (solo para la sesión actual):**

```powershell
$env:EMAIL_SENDER_MODE = "http"
$env:HOST_NOTIFICATION_EMAIL = "boyeromail@gmail.com"
$env:EMAIL_FROM = "onboarding@resend.dev"
$env:EMAIL_API_KEY = "re_TU_CLAVE_AQUI"
```

### Pasos detallados

#### M-01 — Arranque con configuración correcta

1. Define las cuatro variables de la tabla anterior.
2. En la raíz del proyecto: `mvn spring-boot:run`
3. **Esperado:** la app levanta en el puerto 8080 (o el configurado); en log **no** aparece error de variable faltante.
4. Si falla al arrancar: revisa nombres exactos de variables y que `EMAIL_API_KEY` no esté vacía.

#### M-02 — Fail-fast (config incompleta)

1. **Para** la app (Ctrl+C).
2. Quita solo `EMAIL_API_KEY` (o déjala vacía): `Remove-Item Env:EMAIL_API_KEY` en PowerShell.
3. Vuelve a ejecutar `mvn spring-boot:run`.
4. **Esperado:** la aplicación **no** arranca; en consola un mensaje claro (p. ej. menciona `EMAIL_API_KEY`).
5. **No** debe ser posible hacer un `POST` hasta corregir la config.

#### M-03 — Restaurar y arrancar de nuevo

1. Vuelve a poner `EMAIL_API_KEY` con tu clave válida.
2. `mvn spring-boot:run`
3. **Esperado:** arranque normal (como M-01).

#### M-04 — Reserva válida por API

Con la app en marcha, en **otra** terminal (el servidor debe tener las env vars; el cliente curl no):

**Esperado en consola del servidor:** línea de log del email (destinatario, asunto, cuerpo) **antes o aunque falle** el envío HTTP.

```powershell
curl.exe -s -w "`nHTTP %{http_code}`n" -X POST http://localhost:8080/api/requests `
  -H "Content-Type: application/json" `
  -d '{\"experienceId\":1,\"visitorEmail\":\"visitor@example.com\",\"comment\":\"Saturday afternoon would work best for me\",\"nativeEnglishSpeaker\":true}'
```

**Esperado:**

- Código HTTP **201**
- Cuerpo JSON: `{"ok":true}`

Si obtienes **503**: Resend rechazó el envío (revisa API key, From sandbox, logs del servidor).  
Si obtienes **400**: payload inválido; compara con el JSON de arriba.

#### M-05 — Email recibido

1. Abre el cliente de correo de **`HOST_NOTIFICATION_EMAIL`** (Gmail: `boyeromail@gmail.com`).
2. Espera unos segundos; revisa **Recibidos** y, si no aparece, **Spam**.
3. **Esperado:** un mensaje nuevo tras el POST de M-04 (remitente coherente con Resend / `EMAIL_FROM`).

#### M-06 — Comprobar asunto

Abre el email y lee la línea de asunto.

**Esperado (texto exacto para esta reserva Cinema):**

```text
You got a new experience request: Cinema
```

#### M-07 — Comprobar cuerpo

Abre el cuerpo del mensaje (texto plano). Debe contener **todas** estas líneas (pueden ir separadas por saltos de línea):

```text
Experience: Cinema
Description: An evening at a local cinema — film and conversation in English.
Visitor email: visitor@example.com
Native English speaker: yes
Preferred date or time: Saturday afternoon would work best for me
```

Marca en checklist: asunto OK / cuerpo OK. Si falta alguna línea o el texto difiere, anótalo antes de dar por cerrada la slice.

### Resumen checklist smoke

| Paso | ¿OK? | Notas |
|------|------|-------|
| M-01 Arranque con `http` + vars | ☐ | |
| M-02 Sin API key → no arranca | ☐ | |
| M-03 Arranque restaurado | ☐ | |
| M-04 POST → 201 | ☐ | |
| M-05 Email en bandeja | ☐ | |
| M-06 Asunto correcto | ☐ | |
| M-07 Cuerpo completo | ☐ | |

---

## Criterios de hecho (slice 1.5)

- [x] Todos los escenarios **N**, **C**, **P**, **H**, **S** implementados y verdes (PASO 6).
- [x] Regresión suite 1.2–1.4 verde.
- [x] `specs/techstack.md` actualizado (Resend, env vars, modo log/http).
- [x] Smoke **M-01** a **M-07** ejecutado al menos una vez por el anfitrión.
- [x] Sin secretos en git.

---

## PASO 4 — checklist para el agente

1. Crear/actualizar clases de test listadas arriba.
2. Actualizar `EmailHostNotifierTest` (asunto/cuerpo nuevos; captor en `to`).
3. **No** implementar `HttpEmailSender`, `EmailSenderProperties` ni wiring `http` en producción.
4. Ejecutar `mvn test` → informar **cuántos fallan** y por qué (rojo esperado).
5. Esperar revisión del anfitrión (PASO 5) antes de PASO 6.

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-24 | Borrador PASO 3: escenarios N/P/H/S, smoke manual, datos de referencia Cinema. |
| 2026-05-24 | Aclarado N-02 (nativeEnglishSpeaker false); smoke manual paso a paso (PowerShell, checklist). |
| 2026-05-24 | CompositeEmailSender: escenarios C-01/C-02; log siempre. |
