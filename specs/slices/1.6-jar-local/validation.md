# Slice 1.6 — Validación (smoke manual y regresión)

**Estado:** hecho (PASO 6).  
**Requirements:** [`requirements.md`](requirements.md) (acordado).  
**Plan:** [`plan.md`](plan.md) (acordado).  
**Contrato API:** [`api-contract.md`](../../api-contract.md) — sin cambios.

---

## Criterio de salida PASO 4

**No aplica** — slice sin tests automatizados nuevos (acuerdo PASO 1: enfoque A).

- No crear clases de test adicionales para el JAR en este slice.  
- **Regresión:** `mvn test` debe seguir en verde tras los cambios de documentación.  
- **Validación principal:** smoke manual **M-01** a **M-04** (anfitrión o agente con terminal).

---

## Tests automatizados — regresión

La suite existente (36 tests, slices 1.2–1.5) debe permanecer **verde** sin modificaciones de test:

| Clase | Comportamiento |
|-------|----------------|
| `ExperiencesControllerMvcTest` | `GET /api/experiences` |
| `RequestsControllerMvcTest` | `POST /api/requests` — 201/400/503/405 |
| `ExperienceRequestValidatorTest` | reglas de validación |
| `ExperienceRequestMapperTest` | payload → dominio |
| `EmailHostNotifierTest` | asunto/cuerpo email |
| `EmailSenderPropertiesTest` | fail-fast modo `http` |
| `CompositeEmailSenderTest` | log + delivery |
| `HttpEmailSenderTest` | REST Resend mock |
| `EmailSenderStartupTest` | contexto no arranca sin API key en `http` |

**Comando:** `mvn test` desde la raíz del proyecto.

---

## Aplazado — test subproceso JAR (enfoque B)

| ID | Escenario | Notas |
|----|-----------|--------|
| F-01 | `mvn package` + `ProcessBuilder` / subproceso `java -jar` | Esperar log «Started» o poll `GET /api/experiences` → 200 |
| F-02 | Mismo test en CI | Requiere puerto libre, timeout, kill del proceso |

**Fuera de PASO 4–6 de 1.6.** Valorar cuando CI ejecute también empaquetado o en Fase 2+.

---

## Datos de referencia (smoke GET)

Fuente: [`ContractCatalog.java`](../../../src/test/java/com/madridlocalbuddy/support/ContractCatalog.java).

Tras `GET /api/experiences` se esperan **2** experiencias en JSON, incluyendo al menos:

| Campo | Experiencia 1 (Cinema) |
|-------|------------------------|
| `id` | `1` |
| `title` | `Cinema` |

*(No hace falta assert byte-a-byte del JSON en manual; basta comprobar `200`, array de 2 elementos y presencia de `"Cinema"`.)*

---

## Smoke manual (perfil A — obligatorio)

Prueba con JAR empaquetado. **No** sustituye `mvn test`; complementa la documentación.

### Antes de empezar

| Requisito | Detalle |
|-----------|---------|
| Build | `mvn package` completado sin errores |
| Perfil | **A** — sin variables de entorno (o solo `EMAIL_SENDER_MODE=log`) |
| Puerto | Default **8080** (cerrar otras instancias en ese puerto) |
| Terminal | Servidor en una ventana; `curl` en otra |

### Pasos detallados

#### M-01 — Build produce JAR ejecutable

1. En la raíz: `mvn package`
2. **Esperado:** BUILD SUCCESS.
3. Comprobar que existe `target/madrid-local-buddy-0.0.1-SNAPSHOT.jar` (fat JAR, tamaño >> jar “delgado”).

#### M-02 — Arranque con `java -jar` (perfil A)

1. Sin exportar variables de email:
   ```powershell
   java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
   ```
2. **Esperado:** log Spring Boot con arranque completado (p. ej. «Started MadridLocalBuddyApplication»); **sin** error de variables faltantes.
3. Proceso sigue en ejecución escuchando en 8080.

#### M-03 — Quick check API

Con la app del JAR en marcha:

```powershell
curl.exe -s -w "`nHTTP %{http_code}`n" http://localhost:8080/api/experiences
```

**Esperado:**

- HTTP **200**
- Cuerpo JSON con **2** experiencias; incluye `"title":"Cinema"` (o equivalente con espaciado JSON).

#### M-04 — Puerto configurable (opcional recomendado)

1. Detener el JAR (Ctrl+C).
2. Arrancar con puerto distinto:
   ```powershell
   $env:SERVER_PORT = "9090"
   java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
   ```
3. `curl http://localhost:9090/api/experiences` → **200**.
4. `curl http://localhost:8080/api/experiences` → **no** debe responder (conexión rechazada o timeout).

Si M-04 se omite, M-01–M-03 bastan para cerrar la slice.

---

## Smoke manual — perfil B (opcional)

Reutiliza variables y expectativas del smoke 1.5 ([`validation.md` de 1.5](../1.5-resend-email/validation.md) M-01–M-07), cambiando solo el arranque:

```powershell
$env:EMAIL_SENDER_MODE = "http"
$env:HOST_NOTIFICATION_EMAIL = "…"
$env:EMAIL_FROM = "…"
$env:EMAIL_API_KEY = "…"
java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
```

**No obligatorio** para cerrar 1.6 si M-01–M-03 están OK.

---

## Checklist documentación (PASO 6)

| ID | Comprobación | ¿OK? |
|----|--------------|------|
| D-01 | README tiene sección «Running locally (JAR)» con prereqs, build, run, env, `SERVER_PORT`, curl | ☑ |
| D-02 | `.env.example` en raíz, sin secretos, perfiles A/B comentados | ☑ |
| D-03 | `.env` real **no** trackeado (sigue en `.gitignore`) | ☑ |
| D-04 | `specs/techstack.md` enlaza o menciona arranque JAR vía README | ☑ |

---

## Resumen checklist smoke

| Paso | ¿OK? | Notas |
|------|------|-------|
| M-01 `mvn package` + JAR en target | ☑ | |
| M-02 `java -jar` perfil A arranca | ☑ | |
| M-03 GET experiences → 200, 2 items | ☑ | Cinema + Casa de Campo |
| M-04 `SERVER_PORT` (opcional) | ☑ | 9090 OK; 8080 sin listener |
| Perfil B + POST (opcional) | ☐ | Reutiliza smoke 1.5 |

---

## Matriz requisito → verificación

| Requisito | Verificación |
|-----------|--------------|
| JAR ejecutable | M-01 |
| Arranque sin IDE / sin vars | M-02 |
| API operativa vía JAR | M-03 |
| `SERVER_PORT` documentado y funcional | M-04 + README |
| Perfiles A/B documentados | README + `.env.example` + D-01/D-02 |
| Sin regresión tests | `mvn test` verde |
| Contrato API sin cambios | Regresión suite MockMvc |
| Test subproceso JAR | **Aplazado** (F-01/F-02) |

---

## Criterios de hecho (slice 1.6)

- [x] `.env.example` y README actualizados.
- [x] `mvn test` verde.
- [x] Smoke **M-01** a **M-03** ejecutado al menos una vez.
- [x] Checklist documentación **D-01** a **D-04** OK.
- [x] `specs/roadmap.md` — paso 1.6 marcado **hecho**.
- [x] Sin secretos en git.

---

## PASO 4 — checklist para el agente

1. **No** añadir tests JAR automatizados.
2. Pasar directamente a **PASO 6** (implementar docs + smoke).
3. Ejecutar `mvn test` tras cambios → informar resultado.

---

## PASO 6 — checklist para el agente

1. Crear `.env.example`.
2. Actualizar `readme.md` (sección JAR).
3. Actualizar enlace en `specs/techstack.md`.
4. `mvn test` → verde.
5. `mvn package` + smoke M-01–M-03 (M-04 si posible).
6. Marcar roadmap 1.6 hecho; actualizar `changelog.md` antes del commit.

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-06-14 | Borrador PASO 3: smoke M-01–M-04, regresión suite, PASO 4 omitido, F-01/F-02 aplazados. |
| 2026-06-14 | PASO 6 cerrado: smoke M-01–M-04 ejecutado; checklists D y criterios de hecho OK. |
