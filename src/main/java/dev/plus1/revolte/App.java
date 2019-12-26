package dev.plus1.revolte;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.plus1.revolte.data.MessageEvent;
import dev.plus1.revolte.data.WebhookPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.plus1.revolte.SparkUtils.useRequestLoggingJettyServer;
import static spark.Spark.*;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(Integer.parseInt(System.getenv("PORT")));
        useRequestLoggingJettyServer();

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

            if (q.body() != null) {
                WebhookPost<MessageEvent> body = gson.fromJson(q.body(), new TypeToken<WebhookPost<MessageEvent>>() {}.getType());
                if (body.getObject().equals("page")) {
                    for (MessageEvent e : body.getEntry()) {
                        for (MessageEvent.MessageUnit unit : e.getMessaging())
                            log.info("Message from {} : {}", unit.getSender().getId(), unit.getMessage().getText());
                    }
                    a.status(200);
                    return "EVENT_RECEIVED";
                }
            }
            halt(404);
            return null;
        });
    }
}
