# Feedback de sesión — 2026-05-24

## Cómo interactúas (lo que funciona muy bien)

- **Debate antes de codificar:** planteas dudas de diseño (payload vs dominio, `User` vs `Visitor`, `AppConfig`) y esperas argumentos antes de cerrar. Eso evita implementar en la dirección equivocada.
- **Revisión con criterio en PASO 5:** no apruebas tests a ciegas; pides explicación cuando algo no entiendes (`ArgumentCaptor`). Eso mejora tu aprendizaje y la calidad del contrato de tests.
- **Reglas de equipo claras:** cuando fijas una norma (“no commitear en rojo”), la comunicas explícita para que quede en el repo. Muy útil.
- **Cierre de sesión ordenado:** separas debate de siguiente paso, merge, pruebas locales y cierre con documentación.

## Consejos para sacarme mejor partido

1. **En PASO 4 (rojo):** pide “tests sin commitear” o “solo en working tree” si quieres revisar antes; así alineamos TDD rojo con tu regla de commits solo en verde.
2. **Preguntas “¿por qué existe X?”** (como con `AppConfig`): úsalas en cualquier momento; son ideales para anclar conceptos Spring/capas antes de la siguiente slice.
3. **Al cerrar slice:** una frase del tipo “¿actualizamos roadmap y next-steps?” evita que el cierre quede solo en el chat.
4. **Nueva slice = nuevo chat:** ya lo haces bien al arrancar; mantenerlo en 1.5 (Resend) reducirá mezclar contexto de 1.4.
5. **Secrets:** cuando llegue Resend, ten la API key lista en variables de entorno del IDE; no hace falta pegarla en el chat.
