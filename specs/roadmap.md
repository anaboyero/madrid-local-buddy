# Roadmap — Madrid Local Buddy

**Estado:** borrador acordado (2026-05-22). Pasos: `debate` | `acordado` | `hecho`.

Ver [`mission.md`](mission.md), [`techstack.md`](techstack.md), [`AGENTS.md`](../AGENTS.md).

---

## Fase 0 — Constitución

| Paso | Entregable | Estado |
|------|------------|--------|
| 0.1 | `mission.md` | acordado |
| 0.2 | `techstack.md` | acordado |
| 0.3 | `roadmap.md` | acordado |
| 0.4 | Contrato API mínimo | acordado — [`api-contract.md`](api-contract.md) |

---

## Fase 1 — Primera historia (API primero, sin UI)

Referencia funcional: [`primera-historia-especificacion.md`](primera-historia-especificacion.md).  
La UI y criterios de pantalla quedan en **Fase 1b**.

| Paso | Entregable | Estado |
|------|------------|--------|
| 1.1 | Proyecto Spring Boot 3 + Maven + JUnit 5 + estructura por capas | acordado |
| 1.2 | Catálogo estático de 2 experiencias + `GET /api/experiences` | hecho |
| 1.3 | Reserva: validar `ExperienceRequest` + `POST /api/requests` (`201`/`400`) | hecho |
| 1.4 | Notificación email al anfitrión (`HostNotifier`, `EmailHostNotifier`, `503` si falla) | hecho — [`slice-host-notification.md`](slice-host-notification.md) |
| 1.5 | Adaptador de email real (HTTP REST; Resend) | hecho — [`slices/1.5-resend-email/`](slices/1.5-resend-email/) |
| 1.6 | Ejecutable JAR local documentado (`java -jar`) | hecho — [`slices/1.6-jar-local/`](slices/1.6-jar-local/) |

**Criterio de éxito slice GET (1.2):** hecho. **Slice reserva (1.3):** [`slice-post-reserva-experiencia.md`](slice-post-reserva-experiencia.md). Email: paso 1.4.

---

## Fase 1b — UI de la primera historia (aplazada)

| Paso | Entregable | Estado |
|------|------------|--------|
| 1b.1 | Landing en inglés + 2 experiencias + formulario | debate |
| 1b.2 | Consumo de `GET /api/experiences` y `POST /api/requests` | debate |
| 1b.3 | Tests RTL + E2E Playwright (opcional) | debate |
| 1b.4 | Deploy web (TBD) | debate |

---

## Fase 2+ (visión — sin orden cerrado)

- Más experiencias en catálogo  
- Persistencia de solicitudes  
- Dominio propio y mejor deliverability de email  
- Calendario / disponibilidad  
- Contenido dinámico / admin  

Detalle según `readme.md` y prioridad del anfitrión cuando Fase 1 esté hecha.

---

## Historial

| Fecha | Cambio |
|-------|--------|
| 2026-05-22 | Roadmap inicial: API-first Java, UI en Fase 1b, JAR local. |
| 2026-05-23 | Paso 1.2 incluye `GET /api/experiences`; contrato en `api-contract.md`. |
| 2026-05-23 | Slice reserva (solo validación); email aplazado a 1.4. |
| 2026-05-23 | Slice GET cerrado; rama `feature/slice-post-requests`. |
| 2026-05-24 | Spec slice notificación (1.4): `HostNotifier`, payload/dominio, `Visitor`; rama `feature/slice-host-notification`. |
| 2026-06-14 | Slice 1.6 hecho: JAR documentado en README, `.env.example`, smoke manual OK. |
