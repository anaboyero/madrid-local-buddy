# Feedback de sesión — 2026-05-23

Solo interacción con el agente (acuerdos y plan: `specs/next-steps.md`).

---

## Feedback del anfitrión al agente

**Positivo**

- Muy buena sesión: explicas y argumentas las decisiones.
- Muy bien debatido y explicado el tema **400 vs 503** (el anfitrión escribió «500»; se refería al debate de códigos HTTP en errores de entrada vs fallo de envío).

**A mejorar (agente)**

- No avisó de **cambiar la ventana de contexto** al abrir una slice nueva (rama/spec distinta).
- **Segunda vez** que se implementó antes del TDD (código en `src/main` antes de tests en rojo y revisión). El anfitrión seguirá las frases de refuerzo del feedback; valorar refuerzo en `AGENTS.md`.
- Bug pendiente: experiencia errónea sin mensaje de error útil en API → `next-steps.md`.
- El feedback es **bidireccional**: el agente debe comentar sin reservas lo que le parezca raro o inconsistente.

---

## Qué funcionó bien en cómo trabajas conmigo

- **Corriges el ritmo TDD** cuando el agente se adelanta. Eso mantiene el proceso de `AGENTS.md`.
- **Recortas alcance con criterio** (email en otra slice).
- **Debate técnico concreto** antes de codificar.
- **Nombras las cosas** como las quieres en las specs.

---

## Consejos para sacar más partido al agente

1. Al empezar TDD: *«PASO 4: solo tests, sin `src/main`»* o *«no implementes hasta que apruebe los tests»*.
2. *«Muéstrame los tests y no toques producción»* si quieres revisar antes de compilar.
3. Al cerrar slice: **modo cierre** (`next-steps`, `changelog`, `docs/`).
4. *«Solo X en esta slice»* temprano si el agente mezcla validación + email + HTTP.
5. Nueva slice: recordar **contexto nuevo** en Cursor si la conversación viene muy cargada del slice anterior.

---

## Feedback del agente al anfitrión (bidireccional)

- **Aciertas** al frenar implementación prematura; sin eso el repo habría mezclado email y reserva y el TDD sería teatro.
- **Recortar la slice** a solo validación fue una decisión de producto madura; conviene repetir ese filtro al inicio de cada slice.
- Si algo te parece raro en una propuesta mía (tecnología, alcance, orden TDD), dilo en el momento — encaja con el manifiesto de `AGENTS.md` y evita rework.
- Para el bug de experiencia errónea: traer el `curl` exacto mañana ahorrará adivinar (los tests actuales no cubren el caso que viste).
