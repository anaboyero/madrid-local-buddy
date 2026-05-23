# Primera historia — slice vertical mínimo

**Estado:** acordado (2026-05-23).  
**Contrato HTTP:** [`api-contract.md`](api-contract.md). **Stack:** [`techstack.md`](techstack.md).

---

## Historia

Como visitante que busca un plan auténtico en Madrid, quiero **ver las experiencias disponibles** para poder elegir una y, más adelante, enviar una solicitud al anfitrión.

---

## Slice actual (Fase 1 — en curso)

| Capacidad | Entrega |
|-----------|---------|
| Catálogo estático de 2 experiencias | Dominio `ExperienceCatalog` |
| Exponer catálogo por API | `GET /api/experiences` |

**Idioma público:** inglés (`title`, `description`).

---

## Catálogo

| `id` | `title` | `description` (provisional) |
|------|---------|----------------------------|
| `1` | Cinema | An evening at a local cinema — film and conversation in English. |
| `2` | Casa de Campo walk | A relaxed walk in Casa de Campo — green Madrid away from the tourist centre. |

---

## Modelo de solicitud (preparado, sin API aún)

`ExperienceRequest` en dominio — para el futuro `POST /api/requests`:

| Campo | Tipo | Notas |
|-------|------|--------|
| `experienceId` | integer | Id del catálogo (`1`, `2`, …). |
| `visitorEmail` | string | Email del visitante. |
| `comment` | string | Opcional. |
| `nativeEnglishSpeaker` | boolean | Alineado con visitante nativo en [`mission.md`](mission.md). |

Sin validación ni email al anfitrión en este slice.

---

## Aplazado (siguientes iteraciones)

- `POST /api/requests`, validación, errores `400`, email al anfitrión.
- UI (Fase 1b): landing, formulario, privacidad, confirmación en pantalla.

---

## Criterios de hecho — slice actual

- [x] `GET /api/experiences` devuelve **exactamente** las dos experiencias con `id`, `title` y `description` en inglés.
- [x] `POST` en `/api/experiences` → `405`.
- [x] Tests de catálogo y MockMvc en verde.
- [ ] `curl` documentado en el contrato funciona contra el JAR local (probar con `mvn spring-boot:run`).

---

## Historial

| Fecha | Notas |
|-------|--------|
| 2026-05-23 | Slice reducido: solo GET catálogo; `nativeEnglishSpeaker` en `ExperienceRequest`. |
| 2026-05-23 | Versión anterior incluía POST, validación y email (aplazado). |
| 2026-05-23 | `id` de experiencias y `experienceId` pasan de string a integer (`1` = cinema, `2` = casa de campo). |
