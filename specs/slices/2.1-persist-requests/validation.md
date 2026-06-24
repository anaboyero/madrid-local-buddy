# Slice 2.1 — Validación (tests y smoke)

**Estado:** hecho (PASO 6 — 40 tests verdes).  
**Requirements:** [`requirements.md`](requirements.md) (acordado).  
**Plan:** [`plan.md`](plan.md) (acordado).  
**Contrato API:** [`api-contract.md`](../../api-contract.md) — **se ampliará** al cerrar PASO 6 (`GET`, `id` en `201`).

---

## Criterio de salida PASO 4

- Tests nuevos o actualizados en **rojo** (`mvn test` falla por persistencia / `GET` / `id` no implementados).
- Cada escenario automatizado tiene **clase** y **nombre de método** acordados.
- **Sin** H2 en fichero en tests automatizados — solo `jdbc:h2:mem:testdb`.
- `@MockBean HostNotifier` se mantiene en MVC tests (email fuera de alcance).
- Suite existente pasa salvo tests que **deben** evolucionar con el nuevo contrato.

---

## Datos de referencia

Fuente: [`ContractRequests.java`](../../../src/test/java/com/madridlocalbuddy/support/ContractRequests.java), [`ContractCatalog.java`](../../../src/test/java/com/madridlocalbuddy/support/ContractCatalog.java).

### POST válido (sin cambio de body)

| Campo | Valor |
|-------|--------|
| `experienceId` | `1` |
| `visitorEmail` | `visitor@example.com` |
| `comment` | `Saturday afternoon would work best for me` |
| `nativeEnglishSpeaker` | `true` |

### GET — ítem esperado (tras un POST válido)

| Campo | Valor esperado |
|-------|----------------|
| `id` | `1` (primer registro; enteros consecutivos en tests aislados) |
| `experienceId` | `1` |
| `experienceTitle` | `Cinema` |
| `visitorEmail` | `visitor@example.com` |
| `comment` | `Saturday afternoon would work best for me` |
| `nativeEnglishSpeaker` | `true` |
| `createdAt` | Presente, formato ISO-8601 parseable (no assert de valor exacto de reloj) |

---

## Escenarios automatizados

### 1. `RequestsControllerMvcTest` (`@SpringBootTest`, H2 memoria)

| ID | Escenario | Given | When | Then |
|----|-----------|-------|------|------|
| R-01 | POST válido persiste y devuelve id | `HostNotifier` mock; BD vacía | `POST /api/requests` con `ContractRequests.VALID_JSON` | `201`; `$.ok` = `true`; `$.id` = `1`; `notify` invocado una vez |
| R-02 | GET lista solicitud guardada | Tras R-01 (mismo test o `@BeforeEach` limpio + un POST) | `GET /api/requests` | `200`; array tamaño `1`; `$[0].id` = `1`; `$[0].experienceTitle` = `Cinema`; campos según tabla referencia |
| R-03 | POST inválido no persiste | BD vacía | `POST` email inválido (test existente) | `400`; luego `GET /api/requests` → `200`, array **vacío** |
| R-04 | 503 pero solicitud guardada | `HostNotifier` lanza `HostNotificationException` | `POST` válido | `503`; `$.message` = `Unable to notify host`; **sin** `$.id`; `GET /api/requests` → array tamaño `1` |
| R-05 | Orden más reciente primero | Dos POST válidos distintos (p. ej. `experienceId` 1 y 2, o comments distintos) | `GET /api/requests` | `200`; tamaño `2`; `$[0].experienceId` = del **segundo** POST; `$[1].experienceId` = del primero |
| R-06 | GET con lista vacía | Sin POST previo | `GET /api/requests` | `200`; `[]` |
| R-07 | Métodos no permitidos | — | `PUT /api/requests` | `405` |
| R-08 | Regresión notificación OK | Mock `HostNotifier` | `POST` válido | `verify(hostNotifier).notify(...)` — igual que hoy |

**Cambios respecto a tests actuales:**

| Test actual | Acción |
|-------------|--------|
| `postRequests_withValidPayload_returns201AndOkTrue` | Añadir assert `$.id` |
| `getRequests_returns405` | **Sustituir** por R-02 / R-06 (GET → `200`) |

**Métodos sugeridos:** `postRequests_withValidPayload_returns201WithId`, `getRequests_afterPost_returnsStoredRequest`, `postRequests_withInvalidPayload_doesNotPersist`, `postRequests_whenNotificationFails_stillPersistsRequest`, `getRequests_returnsNewestFirst`, `getRequests_whenEmpty_returnsEmptyArray`, `putRequests_returns405`, `postRequests_withValidPayload_notifiesHost`.

**Estilo de aserciones (acordado PASO 5):** comparar **objetos de respuesta completos** (`ContractApiResponses` + AssertJ `isEqualTo`), no `jsonPath` campo a campo. Fuente: [`ContractApiResponses.java`](../../../src/test/java/com/madridlocalbuddy/support/ContractApiResponses.java), [`MvcTestJson.java`](../../../src/test/java/com/madridlocalbuddy/support/MvcTestJson.java). Excepción: en listados `StoredRequest`, `createdAt` se ignora en la igualdad (reloj del servidor) pero debe estar presente.

**Aislamiento:** `@BeforeEach` limpia tabla y reinicia secuencia `id` en tests MVC (`JdbcTemplate`).

---

### 2. `JpaRequestRepositoryTest` (opcional — `@DataJpaTest`)

Solo si MVC no cubre orden/persistencia con suficiente confianza.

| ID | Escenario | When | Then |
|----|-----------|------|------|
| J-01 | Save devuelve id | `save(domainRequest)` | id `>= 1` |
| J-02 | Orden descendente | Dos saves | `findAllNewestFirst()` — el segundo aparece primero |

**Métodos sugeridos:** `save_assignsId`, `findAllNewestFirst_ordersByCreatedAtDesc`.

---

## Smoke manual (post PASO 6)

No sustituye `mvn test`.

| ID | Pasos | Resultado |
|----|-------|-----------|
| M-01 | `mvn package` → `java -jar …` (perfil A) → `POST` válido → parar JAR → arrancar de nuevo → `GET /api/requests` | Tras reinicio, `GET` sigue devolviendo la solicitud |
| M-02 | Comprobar que existe `./data/` con ficheros H2 tras al menos un POST | Datos en disco |

---

## Matriz requisito → verificación

| Requisito | Verificación |
|-----------|----------------|
| Guardar solicitud válida + `id` | R-01 |
| `GET` listado | R-02, R-06 |
| `400` no persiste | R-03 |
| `503` persiste | R-04 |
| Orden recientes primero | R-05 |
| Email sin regresión | R-08 |
| Supervivencia reinicio | M-01 (manual) |
| Fichero H2 local | M-02 (manual) |

---

## Criterio de «hecho» (slice completa — PASO 6)

- [x] Escenarios R-01 a R-08 en verde.
- [x] `mvn test` verde.
- [x] Smoke M-01 OK (recomendado).
- [x] `api-contract.md` y `techstack.md` actualizados.
- [x] README: `./data/` + `curl GET`.

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-06-24 | PASO 6: implementación JPA/H2; 40 tests verdes. |
