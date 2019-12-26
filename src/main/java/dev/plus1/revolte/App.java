package dev.plus1.revolte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.plus1.revolte.SparkUtils.useRequestLoggingJettyServer;
import static spark.Spark.get;
import static spark.Spark.port;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

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
            a.status(403);
            return null;
        });
    }
}
