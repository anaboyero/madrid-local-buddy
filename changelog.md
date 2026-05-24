# Changelog — Madrid Local Buddy

Avances del proyecto (hitos acordados o entregados). Actualizar al cerrar cada sesion (ver `agents.md`).

---

## 2026-05-24

- PASO 1 slice notificación (1.4): spec `slice-host-notification.md` — `HostNotifier`, `EmailHostNotifier`, payload/dominio, `Visitor`, `503`.
- Contrato API y `techstack.md` actualizados; rama acordada `feature/slice-host-notification`.

## 2026-05-23

- **Sesión cerrada** (feedback anfitrión): refuerzo TDD y nueva-slice en `AGENTS.md`; bugfix experiencia errónea en `next-steps`.
- **Sesión cerrada:** slice reserva entregada; PR #1 mergeado a `main`; CI GitHub Actions; siguiente slice email (1.4).
- PASO 6 slice reserva: `ExperienceRequestValidator`, `POST /api/requests`; 18 tests verdes.
- Slice POST simplificada: solo validación de reserva; tests sin email; notificación aplazada (roadmap 1.4).
- Slice GET cerrado; rama `feature/slice-post-requests`; spec `slice-post-reserva-experiencia.md`.
- Ids de experiencia: string → integer (`1`, `2`); `findById(int)`.
- TDD slice GET: tests aprobados (PASO 5), `ExperienceCatalog` implementado (PASO 6), 7 tests verdes.
- Test `findById_returnsCasaDeCampoWhenIdMatches` añadido.
- Slice reducido: solo `GET /api/experiences`; eliminados POST/validación/email del código; `ExperienceRequest.nativeEnglishSpeaker`.
- Catálogo implementado; tests de catálogo y MockMvc.
- `mission.md` marcado **acordado** (OK del anfitrión).
- `primera-historia-especificacion.md` simplificada: slice vertical Fase 1 (GET + POST) / Fase 1b (UI).
- Contrato API unificado en `specs/api-contract.md` (`GET /api/experiences`, `POST /api/requests`); `api-requests-contract.md` → redirect.
- `roadmap.md` y `techstack.md` alineados con el GET de catálogo.

## 2026-05-22

- Sesion cerrada: constitucion lista; siguiente PASO 4 (tests API).
- `agents.md` simplificado — stack detallado solo en `specs/techstack.md`; aceptado por el anfitrion.
- Revision de `agents.md` aceptada por el anfitrion (Fase 1/1b, API-first, cierre de sesion, changelog).
- Constitucion de producto: `specs/mission.md` (borrador acordado).
- Primera historia especificada: `specs/primera-historia-especificacion.md`.
- Tech stack acordado: Java, Spring Boot 3, Maven, API-first, JAR local (`specs/techstack.md`).
- Roadmap: Fase 1 API, Fase 1b UI (`specs/roadmap.md`).
- Contrato API minimo: `POST /api/requests` (`specs/api-requests-contract.md`).
- `agents.md` revisado: Fase 1/1b, API-first, PASO 7 vs feedback en `docs/`, cierre de sesion.
- `changelog.md` creado.
- `docs/2026-05-22-session-feedback.md` — solo feedback y consejos (sin resumen).
- `specs/next-steps.md` — siguiente: PASO 4 (tests 1.2–1.4).

## 2026-05-21

- Cierre PASO 1 primera historia: 2 experiencias, formulario, UI en ingles (alcance; UI aplazada tras decision API-first).
