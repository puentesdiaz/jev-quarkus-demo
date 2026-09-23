package ai.jev.demo.triage;

import ai.jev.demo.jev.Question;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The judgments Jev makes about a support ticket. They are independent, so they all go
 * in one request and run in parallel. Question ids are for code only; each question
 * carries its full meaning and points at the state with backticked paths.
 */
final class TriageQuestions {

    static final String DEPARTMENT = "department";
    static final String IS_URGENT = "isUrgent";
    static final String WANTS_HUMAN = "wantsHuman";
    static final String FRUSTRATION = "frustration";
    static final String IS_SPAM = "isSpam";

    private TriageQuestions() {
    }

    static Map<String, Question> all() {
        Map<String, Question> questions = new LinkedHashMap<>();

        Map<String, String> departments = new LinkedHashMap<>();
        departments.put("tesoreria", "facturas, reembolsos, pagos, cobros.");
        departments.put("soporte", "Bugs, errores, performance o problemas de integracion.");
        departments.put("cuenta", "Login, password, acceso, perfil o seguridad de la cuenta.");
        departments.put("ventas", "Preguntas sobre precios, presupuestos, financiacion.");
        departments.put("other", "The main request in `ticket.message` fits none of the other teams.");

        
        questions.put(DEPARTMENT, Question.choice(
                "Which team should handle the main request in `ticket.message`?", departments));

        questions.put(IS_URGENT, Question.noul(
                "Does `ticket.message` describe a problem that needs attention today?",
                "Service is down, money is being lost, a deadline is imminent, or security is at risk.",
                "The request can wait for the normal queue."));

        questions.put(WANTS_HUMAN, Question.noul(
                "Does `ticket.message` explicitly ask to talk to a human person or a manager?"));

        questions.put(FRUSTRATION, Question.score(
                "How much frustration does the customer express in `ticket.message`?",
                List.of(
                        "Neutral or positive tone; makes a request without complaint.",
                        "Expresses dissatisfaction or impatience without strong anger.",
                        "Expresses strong anger, threatens to cancel, or uses hostile language.")));

        questions.put(IS_SPAM, Question.noul(
                "Is `ticket.message` spam, advertising or unrelated to being a customer of the product?"));

        return questions;
    }
}
