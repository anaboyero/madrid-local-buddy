# Slice 1.6 — Plan técnico (JAR local documentado)

**Estado:** hecho (PASO 6).  
**Requirements:** [`requirements.md`](requirements.md) (acordado).  
**Prerrequisito:** slices 1.2–1.5 en `main` — API, notificación, email Resend.

---

## Objetivo técnico

Dejar la Fase 1 **ejecutable fuera del IDE** con `mvn package` + `java -jar`, documentado en README y `.env.example`. **Sin cambios** en código Java, contrato HTTP ni dependencias Maven.

---

## Situación actual

| Aspecto | Estado |
|---------|--------|
| `spring-boot-maven-plugin` | Ya en `pom.xml` — repackage a fat JAR |
| Artefacto | `target/madrid-local-buddy-0.0.1-SNAPSHOT.jar` |
| Java compile | 17 (`java.version` en POM) |
| Arranque default | Puerto 8080; `EMAIL_SENDER_MODE=log` sin vars |
| `.gitignore` | `.env` ya ignorado |
| CI | `mvn clean test` en push/PR a `main` — **sin cambio** en este slice |

Verificación local previa (2026-06-14): `mvn package` → BUILD SUCCESS + JAR repackageado.

---

## Diseño

### Qué cambia (solo repo / docs)

```text
mvn package
      ↓
target/madrid-local-buddy-0.0.1-SNAPSHOT.jar  (fat JAR, sin cambios en POM)
      ↓
java -jar …  (+ env vars opcionales)
      ↓
Spring Boot arranca → mismos controladores / config que en IDE
      ↓
GET /api/experiences  |  POST /api/requests  (sin cambios)
```

### Piezas

| Pieza | Acción |
|-------|--------|
| `pom.xml` | **Sin cambios** — plugin ya correcto |
| Código Java (`src/main`, `src/test`) | **Sin cambios** |
| [`readme.md`](../../../readme.md) | **Nueva sección** «Running locally (JAR)» |
| [`.env.example`](../../../.env.example) | **Nuevo** — plantilla perfiles A/B |
| [`specs/techstack.md`](../../techstack.md) | **Enlace menor** a README para arranque JAR (1 línea en tabla Deploy) |
| [`specs/roadmap.md`](../../roadmap.md) | Marcar 1.6 **hecho** al cerrar PASO 6 |
| CI (`.github/workflows/ci.yaml`) | **Sin cambios** |

### README — contenido de la sección

Estructura acordada (inglés, coherente con README actual):

1. **Prerequisites** — Java 17+, Maven 3.x  
2. **Build** — `mvn package` (tests deben pasar)  
3. **Run** — `java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar`  
4. **Environment variables** — perfiles A (default log) y B (http + Resend); enlace a `specs/techstack.md` y `.env.example`  
5. **Optional port** — `SERVER_PORT` (ejemplos bash + PowerShell)  
6. **Quick check** — `curl` a `GET /api/experiences`  

No duplicar la tabla completa de variables de email (vive en `techstack.md`).

### `.env.example`

- Raíz del repo, versionado.  
- Valores placeholder / comentados — **sin** API keys reales.  
- Comentarios que distingan perfil A vs B (ver [`requirements.md`](requirements.md)).

### Perfiles de arranque (wiring existente — solo documentar)

| Perfil | Variables | Comportamiento |
|--------|-----------|----------------|
| **A** (default) | Ninguna o `EMAIL_SENDER_MODE=log` | Arranca; email solo en log |
| **B** | `EMAIL_SENDER_MODE=http` + vars obligatorias 1.5 | Arranca; log + Resend; fail-fast si falta config |

La lógica ya está en `EmailSenderProperties` + `AppConfig` (slice 1.5).

---

## Dependencias Maven

| Dependencia | ¿Nueva? |
|-------------|---------|
| Ninguna | — |

---

## Tests automatizados

| Enfoque | Decisión |
|---------|----------|
| Esta slice | **Sin tests nuevos** — regresión `mvn test` + smoke manual ([`validation.md`](validation.md)) |
| Futuro (B) | Test subproceso `java -jar` + assert GET — aplazado Fase 2+ / CI |

**PASO 4 omitido** para 1.6: no hay clases de test nuevas que escribir en rojo.

---

## Orden de construcción (PASO 6)

1. Crear `.env.example` en raíz.  
2. Añadir sección «Running locally (JAR)» en `readme.md`.  
3. Añadir en `specs/techstack.md` referencia cruzada al README (deploy JAR).  
4. Ejecutar `mvn test` → suite verde (regresión).  
5. Ejecutar `mvn package` → confirmar JAR en `target/`.  
6. Smoke manual **M-01** a **M-04** ([`validation.md`](validation.md)).  
7. Actualizar `specs/roadmap.md` (1.6 → hecho).  
8. Actualizar `changelog.md` al commitear (regla `AGENTS.md`).

---

## Smoke manual (referencia rápida)

1. `mvn package`  
2. `java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar` (sin env)  
3. `curl http://localhost:8080/api/experiences` → `200`, 2 experiencias  
4. *(Opcional)* `SERVER_PORT=9090` + curl al puerto 9090  
5. *(Opcional)* Perfil B — reutiliza smoke 1.5 con JAR en lugar de `spring-boot:run`

---

## Fuera de este plan

- Docker, systemd, scripts de arranque.  
- Test automatizado de subproceso JAR (aplazado).  
- Cambios en `api-contract.md`.  
- Añadir `mvn package` a CI (valorar en slice futuro).  
- UI (Fase 1b).

---

## Acuerdos técnicos cerrados (PASO 2–3)

| Tema | Decisión |
|------|----------|
| Código Java | **Sin cambios** |
| `pom.xml` | **Sin cambios** |
| CI | **Sin cambios** |
| PASO 4 | **Omitido** — slice documentación + smoke manual |
| Entregables | `readme.md`, `.env.example`, enlace en `techstack.md` |
| Validación | Smoke M-01–M-04 + `mvn test` verde |

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-06-14 | PASO 2–3 cerrado: plan docs-only; PASO 4 omitido; orden PASO 6. |
| 2026-06-14 | PASO 6 cerrado: entregables docs + smoke OK. |
