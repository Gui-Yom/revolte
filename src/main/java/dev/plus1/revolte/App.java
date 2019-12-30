package dev.plus1.revolte;

import com.google.gson.Gson;
import dev.plus1.messenger.Messenger;
import dev.plus1.messenger.webhook.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(Integer.parseInt(System.getenv("PORT")));
        //useRequestLoggingJettyServer();

        staticFileLocation("/public");
        staticFiles.expireTime(300);

        get("/", (q, a) -> {
            Map<Object, Object> model = new HashMap<>();
            model.put("ip", q.ip());
            return new ModelAndView(model, "index");
        }, new ThymeleafTemplateEngine());

        get("/messenger-wh", (q, a) -> {

            final String mode = q.queryParams("hub.mode");
            final String token = q.queryParams("hub.verify_token");
            final String challenge = q.queryParams("hub.challenge");
            if (Messenger.verifyWebhook(mode, token, challenge)) {
                log.info("Webhook has been verified !");
                a.status(200);
                return challenge;
            }
            halt(403);
            return "";
        });

        post("/messenger-wh", (q, a) -> {

            log.info(q.body());
            a.status(200);
            for (Event e : Messenger.handleWebhook(q.body()).getEntry()) {

                log.info(e.toString());
                // If this is the start of a conversation
                if (e.getEventType() == Event.EventType.POSTBACK && e.getPostback().getPayload().equals("Commencer")) {
                    Messenger.postMessage("Invoque moi dans une conversation de groupe pour commencer !", e.getSender());
                }
                Messenger.postSenderAction(Messenger.SenderAction.MARK_SEEN, e.getSender());
            }
            return "";
        });
    }
}
