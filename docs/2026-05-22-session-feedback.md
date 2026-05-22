# Sesión 2026-05-22 — Feedback

Plan y acuerdos: `specs/next-steps.md` · Hitos: `changelog.md`

---

## Feedback sobre cómo trabajas con el agente

**Lo que funciona muy bien**

- Construiste la **constitución paso a paso** (misión → tech → roadmap) sin saltarte debates; el proyecto queda legible para retomar meses después.
- **Recortas y reorientas** con criterio (“solo API”, “Java porque es mi trabajo”, “el feedback no va en next-steps”) — eso evita documentación duplicada y confusiones.
- **Revisas y aceptas** cambios con ojo crítico (preguntaste si `agents.md` era coherente antes de cerrar); detectaste bien que el stack no debía vivir duplicado en dos sitios.
- Trato **claro y amable** al cerrar; facilita iterar sin fricción.

**Un matiz a vigilar**

- Cuando algo en una spec antigua no encaje con `mission.md` (p. ej. `yearsInMadrid` vs visitante nativo), conviene un OK explícito en una frase al inicio de la sesión de tests para no escribir tests sobre el campo equivocado.

---

## Consejos para sacar más partido al agente

1. **Al abrir:** la frase de `next-steps.md` + “PASO N” — suficiente para arrancar sin re-explicar el proyecto.
2. **Al cerrar:** “cierro sesión” + pedir los tres archivos (`next-steps`, `docs/…-feedback`, `changelog`) — como hoy.
3. Si cambias una regla de documentación (como separar feedback de next-steps), dilo una vez; el agente lo deja fijado en `agents.md`.
4. Mantén **una fuente de verdad por tema** (tech en `techstack.md`, plan en `next-steps.md`); cuando notes duplicación, corrígelo pronto como hiciste hoy.
5. Para PASO 4, un “solo tests, sin código de producción” evita que el agente adelante implementación.
