# Primera historia — slice vertical mínimo



**Estado:** slice GET **hecho** (2026-05-23); slice POST en [`slice-post-reserva-experiencia.md`](slice-post-reserva-experiencia.md).  

**Contrato HTTP:** [`api-contract.md`](api-contract.md). **Stack:** [`techstack.md`](techstack.md).



---



## Historia



Como visitante que busca un plan auténtico en Madrid, quiero **ver las experiencias disponibles** para poder elegir una y **enviar una solicitud al anfitrión** (esta segunda parte: slice POST).



---



## Slice 1 — catálogo (hecho)



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



## Modelo de solicitud (dominio preparado)



`ExperienceRequest` — usado en el slice POST:



| Campo | Tipo | Notas |

|-------|------|--------|

| `experienceId` | integer | Id del catálogo (`1`, `2`, …). |

| `visitorEmail` | string | Email del visitante. |

| `comment` | string | Obligatorio: preferencia de fecha/horario (no vacío). |

| `nativeEnglishSpeaker` | boolean | Alineado con visitante nativo en [`mission.md`](mission.md). |



Validación y `POST /api/requests`: ver [`slice-post-reserva-experiencia.md`](slice-post-reserva-experiencia.md).



---



## Aplazado (después de la reserva)



- Notificación por email al anfitrión (roadmap 1.4).

- UI (Fase 1b): landing, formulario, privacidad, confirmación en pantalla.

- Adaptador de email HTTP real (roadmap 1.5).



---



## Criterios de hecho — slice 1 (GET)



- [x] `GET /api/experiences` devuelve **exactamente** las dos experiencias con `id`, `title` y `description` en inglés.

- [x] `POST` en `/api/experiences` → `405`.

- [x] Tests de catálogo y MockMvc en verde.

- [x] Ids de experiencia son **enteros** (`1`, `2`).



---



## Historial



| Fecha | Notas |

|-------|--------|

| 2026-05-23 | Slice GET cerrado; spec POST en archivo dedicado. |

| 2026-05-23 | Slice reducido: solo GET catálogo; `nativeEnglishSpeaker` en `ExperienceRequest`. |

| 2026-05-23 | Versión anterior incluía POST, validación y email (aplazado). |

| 2026-05-23 | `id` de experiencias y `experienceId` pasan de string a integer (`1` = cinema, `2` = casa de campo). |


