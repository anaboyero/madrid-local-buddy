# Primera historia — Especificación acordada

**Estado:** acuerdos cerrados (PASO 1). Implementación por fases: **API primero** (Fase 1 en [`roadmap.md`](roadmap.md)); **UI en Fase 1b**.

**Contexto:** El contenido de `readme.md` describe la **visión del proyecto final**. Esta historia es un **MVP mínimo**: landing + formulario + notificación por correo al anfitrión.

---

## Objetivo de la historia

Como visitante interesado en un plan auténtico en Madrid, quiero ver tipos de experiencia, elegir una, dejar mis datos y enviar una solicitud, para que el anfitrión reciba un correo con la propuesta y pueda responder fuera de la web.

---

## Alcance incluido

- Una web con **descripciones** de tipos de experiencia (contenido **estático** al inicio; sin panel de administración).
- **Dos** experiencias únicamente:
  1. **Cine** (cinema / film-related outing — copy en inglés por definir).
  2. **Paseo por la Casa de Campo** (walk — copy en inglés por definir).
- Formulario de solicitud con los campos acordados (ver abajo).
- **Interfaz íntegramente en inglés** (textos de UI, etiquetas, mensajes de error y confirmación).
- Tras un envío correcto: **mensaje de confirmación en pantalla** (sin correo automático al visitante en esta historia).
- Validación antes de enviar:
  - Experiencia seleccionada obligatoria.
  - Email del visitante obligatorio y con **formato válido**.
  - Mensajes de error claros; no perder lo que el usuario ya escribió si falla el envío.
- Texto legal mínimo de **privacidad** (uso del email solo para responder a esta solicitud).
- Envío al anfitrión mediante **API propia + servicio de email** (opción B). Detalle de proveedor, hosting y variables de entorno: **debate e implementación en sesión posterior**.

---

## Alcance excluido (esta historia)

- Más de dos experiencias o inventario editable desde la app.
- Agenda, calendario, disponibilidad, filtros de perfil.
- Login, cuentas de usuario, reseñas.
- Pagos, chat in-app, punto de encuentro automático.
- Copia por correo al visitante tras enviar.
- Anti-spam avanzado (rate limit, CAPTCHA, etc.) — **revisar solo si aparece abuso**; acordado posponer en v1.
- Cualquier funcionalidad descrita en `readme.md` que no esté listada arriba en “Alcance incluido”.

---

## Formulario — campos

| Campo | Obligatorio | Notas |
|--------|-------------|--------|
| Experiencia seleccionada | Sí | Identificador (`id`) o nombre estable acordado en implementación (p. ej. `cinema`, `casa-de-campo-walk`). |
| Email del visitante | Sí | Validación de formato. |
| Comentario libre | No* | Ej.: “I’d like Saturday afternoon”. |
| Años en Madrid (`yearsInMadrid`) | No* | Sustituye “nivel de inglés” (alineado con visitante nativo en [`mission.md`](mission.md)). Ver contrato API. |

\*Obligatoriedad de comentario y años en Madrid: no exigidos; si faltan, el email indica `not provided`.

---

## Contenido del correo al anfitrión

El email que recibe el anfitrión debe incluir como mínimo:

- Experiencia seleccionada (id o nombre legible).
- Email del visitante.
- Comentario libre del visitante.
- Años en Madrid (`yearsInMadrid`) si el visitante lo indica.
- **Marca temporal** de envío (recomendado en acuerdo; incluir en implementación).

Asunto y cuerpo en un estilo claro (preferiblemente inglés en el cuerpo del mail alineado con la experiencia del producto).

---

## Flujo feliz

1. El visitante abre la web y ve las dos experiencias con descripción.
2. Selecciona una experiencia.
3. Completa email, comentario (opcional) y nivel de inglés (opcional según diseño).
4. Envía la solicitud.
5. Ve un mensaje de confirmación en pantalla.
6. El anfitrión recibe un correo con todos los datos acordados.

---

## Criterios de “hecho”

- [ ] Se muestran exactamente las dos experiencias con descripción legible en inglés.
- [ ] No se puede enviar sin experiencia + email válido.
- [ ] Errores de validación y de envío son comprensibles y no borran el formulario sin necesidad.
- [ ] Confirmación en pantalla tras envío correcto.
- [ ] El anfitrión recibe el correo con todos los campos acordados + timestamp.
- [ ] Aviso de privacidad visible donde corresponda (footer o junto al formulario).

---

## Implementación acordada (constitución técnica)

- **Fase 1:** solo API — Spring Boot 3, Maven, `POST /api/requests`, JUnit/MockMvc. Ver [`techstack.md`](techstack.md) y [`api-requests-contract.md`](api-requests-contract.md).
- **Fase 1b:** UI (landing, formulario, confirmación en pantalla).
- **Email:** puerto `EmailSender`; proveedor concreto TBD (recomendación Resend vía REST cuando se implemente 1.5).
- **Deploy Fase 1:** JAR local.

## Pendiente menor

- Copy exacto en inglés de las dos descripciones (sobre todo para Fase 1b).

---

## Referencia al flujo de trabajo

Según `AGENTS.md`:

1. ~~PASO 1 — Especificaciones~~ (este documento).
2. PASO 2 — Diseño genérico del MVP.
3. PASO 3 — Primera historia en formato slice vertical.
4. PASO 4 — Tests sin código de producción.
5. PASO 5 — Revisión de tests por el anfitrión.
6. PASO 6 — Implementación hasta pasar tests.
7. PASO 7 — Feedback.

---

## Historial de acuerdos

| Fecha | Notas |
|-------|--------|
| 2026-05-21 | Cierre PASO 1: 2 experiencias, formulario, UI en inglés, confirmación en pantalla, opción B para email (implementación al día siguiente). |
| 2026-05-22 | API-first; Java/Spring Boot; UI Fase 1b; campo `yearsInMadrid`; contrato en api-requests-contract.md. |
