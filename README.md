# Jev + Quarkus: triaje de tickets de soporte

Demo de **Java 17 + Quarkus 3.39** que usa **Jev** (modelo System One de TypeSafe) para
clasificar mensajes de clientes. Jev aporta los juicios semánticos; el código Java decide.

```
Mensaje ──► Jev (1 petición, 5 preguntas en paralelo) ──► TriagePolicy (reglas en Java) ──► cola, prioridad, escalado
```

| Pregunta (`TriageQuestions`) | Primitiva | Para qué |
| --- | --- | --- |
| `department` | Choice | billing / technical / account / sales / other |
| `isUrgent` | Noul | ¿Requiere atención hoy? |
| `wantsHuman` | Noul | ¿Pide hablar con una persona? |
| `frustration` | Score (0–2) | Nivel de enfado |
| `isSpam` | Noul | ¿Es spam? |

`TriagePolicy` aplica umbrales configurables (`application.properties`):
spam → cola `spam`; confianza de Choice baja → `manual-review`; urgente o muy enfadado
→ prioridad `HIGH`; pide humano (o urgente + enfadado) → escalar. Los umbrales son
ilustrativos: evalúalos con tus propios datos.

## Ejecutar

```sh
cp .env.example .env        # y pon tu TYPESAFE_API_KEY
mvn quarkus:dev             # http://localhost:8080
```

Abre http://localhost:8080 para la UI, o usa la API:

```sh
curl -s localhost:8080/api/triage -H 'Content-Type: application/json' \
  -d '{"message":"I was charged twice for my September invoice. Can you refund the duplicate?"}'
```

Tests (usan Jev simulado, no necesitan clave ni red):

```sh
mvn test
```

Empaquetado: `mvn package` y luego `TYPESAFE_API_KEY=... java -jar target/quarkus-app/quarkus-run.jar`.

## Estructura

```
src/main/java/ai/jev/demo/
  jev/      Cliente HTTP de Jev: JevApi (REST Client), JevClient (auth + reintentos 429/529), DTOs tipados
  triage/   TriageQuestions (preguntas), TriagePolicy (reglas), TriageService, TriageResource (/api/triage)
src/main/resources/META-INF/resources/index.html   UI de la demo
```

No existe SDK Java de TypeSafe, así que se llama directamente a `POST https://api.typesafe.ai/v1/systemone`.

Docs: https://docs.typesafe.ai/api
