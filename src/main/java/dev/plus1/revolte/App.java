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

import static dev.plus1.revolte.SparkUtils.useRequestLoggingJettyServer;
import static spark.Spark.*;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(Integer.parseInt(System.getenv("PORT")));
        useRequestLoggingJettyServer();

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
            if (mode != null && token != null) {
                final String VERIFY_TOKEN = System.getenv("WEBHOOK_VERIFY_TOKEN");

                if (mode.equals("subscribe") && token.equals(VERIFY_TOKEN)) {

                    log.info("Webhook has been verified !");
                    a.status(200);
                    return q.queryParams("hub.challenge");
                }
            }
            halt(403);
            return null;
        });

        post("/messenger-wh", (q, a) -> {

            a.status(200);
            for (Event e : Messenger.handleWebhook(q.body())) {

                log.info("{}", e);
            }
            return null;
        });
    }
}
