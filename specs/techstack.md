# Tech stack — Madrid Local Buddy

**Estado:** acordado (2026-05-22). Revisar con el anfitrión antes de cambiar dependencias o proveedores.

**Principio rector:** **API-first** — construir y validar el backend antes de cualquier interfaz.

---

## Relación con otros documentos

| Documento | Contenido |
|-----------|-----------|
| [`AGENTS.md`](../AGENTS.md) | Cómo trabajamos (TDD, pasos, conducta) |
| [`mission.md`](mission.md) | Producto y límites |
| [`roadmap.md`](roadmap.md) | Orden de entregas |
| [`api-contract.md`](api-contract.md) | Contrato HTTP Fase 1 (GET catálogo + POST solicitud) |
| [`primera-historia-especificacion.md`](primera-historia-especificacion.md) | Alcance funcional de la primera historia |

Los principios de ingeniería (TDD, capas, calidad) están en `AGENTS.md`. Este archivo fija **decisiones técnicas concretas**.

---

## Backend (Fase 1 — acordado)

| Decisión | Valor | Estado |
|----------|--------|--------|
| Lenguaje | **Java** | acordado |
| Framework | **Spring Boot 3** | acordado |
| Build | **Maven** | acordado |
| Estilo API | REST JSON | acordado |
| Persistencia Fase 1 | **Ninguna** (sin BD) | acordado |
| Deploy Fase 1 | **JAR local** (`java -jar`) | acordado |
| Cloud / contenedores | Fuera de Fase 1 | aplazado |

### Estructura de paquetes (orientativa)

```text
src/main/java/.../
  domain/           # catálogo, ExperienceRequest (futuro POST)
  api/              # controlador REST GET /api/experiences
src/test/java/.../  # espejo para tests
```

### Tests (Fase 1)

| Herramienta | Uso |
|-------------|-----|
| **JUnit 5** | Tests unitarios (dominio, aplicación) |
| **Mockito** | Mocks del puerto `EmailSender` cuando aplique |
| **Spring Boot Test + MockMvc** | Tests de integración del endpoint |
| **AssertJ** (opcional) | Aserciones legibles — añadir solo si se acuerda al crear el proyecto |

No usar Vitest ni RTL en Fase 1 (ver `AGENTS.md`).

---

## API

| Decisión | Valor |
|----------|--------|
| Endpoint slice actual | `GET /api/experiences` |
| Endpoints aplazados | `POST /api/requests` |
| Contrato | [`api-contract.md`](api-contract.md) |
| Prueba manual | `curl`, Bruno o Postman |

---

## Email (opción B — sin acoplar proveedor en el dominio)

| Decisión | Valor | Estado |
|----------|--------|--------|
| Patrón | **Puerto** `EmailSender` en infraestructura; dominio sin SDK de terceros | acordado |
| Tests | Implementación **mock** / fake que no envía correo real | acordado |
| Desarrollo local | Implementación **log** (escribe el cuerpo del mail en logs) si no hay API key | acordado |
| Producción | Implementación **HTTP REST** genérica (URL + API key por variables de entorno) | acordado |
| Proveedor concreto | **TBD** — no fijar SDK de vendor en el núcleo | TBD |

**Recomendación inicial (cuando se implemente el paso 1.5):** [Resend](https://resend.com) vía API REST (un `RestTemplate` o `WebClient`, JSON simple, free tier suficiente para el MVP). Sustituible por SendGrid/Postmark cambiando solo el adaptador y la configuración.

### Variables de entorno (documentadas; no commitear valores)

| Variable | Obligatoria | Notas |
|----------|-------------|--------|
| `HOST_NOTIFICATION_EMAIL` | Sí (prod / envío real) | Bandeja del anfitrión |
| `EMAIL_FROM` | Sí (envío real) | Remitente verificado en el proveedor |
| `EMAIL_API_KEY` | Sí (envío real) | Clave del proveedor |
| `EMAIL_API_URL` | No | Por defecto URL de Resend; override para otro proveedor |
| `EMAIL_SENDER_MODE` | No | `log` \| `http` — default `log` en local |

---

## Frontend (aplazado)

| Decisión | Valor |
|----------|--------|
| Fase 1 | **Sin UI** — solo API |
| Stack UI | **TBD** en fase posterior (candidatos: HTML+fetch, React+TS) |
| Tests UI (futuro) | Vitest, React Testing Library, Playwright — ver `AGENTS.md` |

La UI pública será **100 % en inglés** (ver `mission.md`).

---

## Calidad y dependencias

- **Nuevas dependencias Maven:** solo con explicación (por qué, alternativas, tradeoffs) y OK del anfitrión.
- **Sin** Redis, Docker obligatorio, GraphQL, BD ni colas en Fase 1.
- **Rate limiting / CAPTCHA:** aplazado (ver spec de primera historia).
- **Analytics:** ninguno en Fase 1.
- **CI en la nube:** aplazado; tests en local y al integrar se valorará GitHub Actions.

---

## Convenciones

- Código del producto (mensajes de API, cuerpo de email al host): **inglés**.
- Documentación del repo (`specs/`, debates): **español**.
- Secretos: solo variables de entorno o gestor del IDE; **nunca** en git.

---

## Historial

| Fecha | Cambio |
|-------|--------|
| 2026-05-22 | Acuerdo: Java, Spring Boot 3, Maven, API-first, POST /api/requests, JUnit/MockMvc, email por puerto + REST TBD (Resend recomendado), deploy JAR local, UI aplazada. |
