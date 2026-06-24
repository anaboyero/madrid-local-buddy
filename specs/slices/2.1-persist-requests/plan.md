# Slice 2.1 — Plan técnico (persistencia de solicitudes)

**Estado:** hecho (PASO 6).  
**Requirements:** [`requirements.md`](requirements.md) (acordado).  
**Prerrequisito:** Fase 1 en `main` — API, notificación, email Resend, JAR documentado.

---

## Objetivo técnico

Persistir cada `ExperienceRequest` válido en **H2 file-based**, exponer **`GET /api/requests`** y devolver **`id`** en `201`. La notificación al anfitrión **no cambia** (1.4 / 1.5). Guardar **antes** de `HostNotifier.notify` para que un `503` no borre el registro.

---

## Dependencias Maven (nuevas — acordadas)

| Dependencia | Por qué | Alternativa descartada |
|-------------|---------|------------------------|
| `spring-boot-starter-data-jpa` | Adaptador estándar Spring; menos boilerplate que JDBC puro | JDBC manual |
| `h2` (runtime) | BD embebida, fichero local, encaja con JAR sin infra externa | Postgres (cloud), SQLite |

Sin Flyway/Liquibase en este slice — `spring.jpa.hibernate.ddl-auto=update` suficiente para MVP.

---

## Diseño

### Diagrama

```text
POST /api/requests
      ↓
RequestsController
      ├─ validate → 400
      ├─ mapper.toDomain
      ├─ RequestRepository.save(domain) → id     ← commit propio (antes de notify)
      ├─ HostNotifier.notify(domain) → 503 si falla
      └─ 201 { ok, id }

GET /api/requests
      ↓
RequestsController
      ├─ RequestRepository.findAllNewestFirst()
      └─ enriquecer experienceTitle desde ExperienceCatalog → 200 JSON array
```

### Capas

| Pieza | Paquete | Acción |
|-------|---------|--------|
| `StoredExperienceRequest` | `domain` | **Nueva** — record con `id`, campos persistidos, `createdAt` |
| `RequestRepository` | `application` | **Nueva** — puerto: `long save(ExperienceRequest)`, `List<StoredExperienceRequest> findAllNewestFirst()` |
| `ExperienceRequestEntity` | `infrastructure` | **Nueva** — entidad JPA |
| `SpringDataExperienceRequestRepository` | `infrastructure` | **Nueva** — `JpaRepository` |
| `JpaRequestRepository` | `infrastructure` | **Nueva** — implementa `RequestRepository` |
| `RequestsController` | `api` | **Refactor** — save + GET; `RequestResponse.Success` con `id` |
| `StoredRequestResponse` | `api` | **Nueva** — DTO JSON para ítem de `GET` |
| `RequestResponse` | `api` | **Refactor** — `Success(boolean ok, long id)` |
| Validador / mapper / `HostNotifier` / email | — | **Sin cambios de comportamiento** |
| `AppConfig` | `config` | **Sin beans nuevos** si Spring Data auto-configura el adaptador (`@Repository` en infra) |

### Flujo `POST` (orden crítico)

```java
// Pseudocódigo — sin @Transactional en el controlador
long id = requestRepository.save(domainRequest);
try {
    hostNotifier.notify(domainRequest);
} catch (HostNotificationException ex) {
    return 503;  // el id ya está persistido
}
return 201 with id;
```

`JpaRequestRepository.save` usa transacción de Spring Data por llamada → el registro queda confirmado antes del `notify`.

### Entidad JPA (orientativa)

| Columna | Tipo |
|---------|------|
| `id` | `BIGINT`, autogenerado |
| `experience_id` | `INT`, not null |
| `visitor_email` | `VARCHAR`, not null |
| `comment` | `VARCHAR`, not null |
| `native_english_speaker` | `BOOLEAN`, not null |
| `created_at` | `TIMESTAMP` UTC, not null |

No se persiste `description` ni `title` de la experiencia — solo `experience_id`.

### Configuración

**`application.properties` (producción / JAR local)**

```properties
spring.datasource.url=jdbc:h2:file:./data/madrid-local-buddy
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
```

**`src/test/resources/application.properties` (tests)**

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=create-drop
```

### Repo / git

- Añadir `data/` a `.gitignore` (ficheros H2 locales).
- README: una línea sobre carpeta `./data/` y ejemplo `curl` para `GET /api/requests`.

### Contrato y docs (PASO 6)

- Actualizar [`api-contract.md`](../../api-contract.md): `GET /api/requests`, `201` con `id`.
- Actualizar [`techstack.md`](../../techstack.md): persistencia H2 file-based.

---

## Tests (PASO 4 — solo rojo)

Ver [`validation.md`](validation.md). Resumen:

| Clase | Tipo |
|-------|------|
| `RequestsControllerMvcTest` | Ampliar / ajustar (`@SpringBootTest` + H2 memoria; `@MockBean HostNotifier`) |
| `JpaRequestRepositoryTest` | `@DataJpaTest` — orden y persistencia básica (opcional si MVC cubre todo) |

**Regresión:** suite 1.2–1.6 sigue verde salvo tests que **deben** cambiar (p. ej. `getRequests_returns405` → `getRequests_returns200`).

**Prohibido en PASO 4:** implementación en `src/main/java` que haga pasar los tests nuevos (solo esqueletos que fallen si hace falta compilar).

---

## Orden de construcción (PASO 6)

1. Dependencias JPA + H2 en `pom.xml`.
2. `application.properties` + test properties + `.gitignore`.
3. Dominio `StoredExperienceRequest`, puerto `RequestRepository`, adaptador JPA.
4. Refactor `RequestsController` + DTOs respuesta.
5. Actualizar tests existentes que rompan contrato (`201` sin `id`, `GET` 405).
6. `mvn test` verde.
7. Smoke manual opcional: `POST` + reinicio JAR + `GET` (M-01 en validation).
8. `api-contract.md`, `techstack.md`, `roadmap.md`, `readme.md`, `changelog.md`.

---

## Fuera de este plan

- Auth, admin UI, Postgres, migraciones versionadas.
- Cambios en formato de email.
- Docker / CI JAR.

---

## Acuerdos técnicos cerrados (PASO 2–3)

| Tema | Decisión |
|------|----------|
| ORM | Spring Data JPA |
| BD | H2 file `./data/madrid-local-buddy` |
| Tests | H2 in-memory |
| Save antes de notify | Sí — transacción independiente por `save` |
| `JpaRequestRepositoryTest` | Opcional; MVC tests son fuente principal |

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-06-24 | PASO 2–3: plan JPA/H2, capas, orden POST, config test/prod. |
