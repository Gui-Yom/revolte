package dev.plus1.revolte;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.plus1.messenger.Messenger;
import dev.plus1.messenger.webhook.Event;
import dev.plus1.revolte.data.DurationGsonAdapter;
import dev.plus1.revolte.data.InstantGsonAdapter;
import dev.plus1.revolte.db.DBEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

public final class App {

    public static final boolean USE_DB = false;
    static final Gson gson = new GsonBuilder()
                                     .registerTypeAdapter(Duration.class, new DurationGsonAdapter())
                                     .registerTypeAdapter(Instant.class, new InstantGsonAdapter())
                                     .create();
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static Map<String, Revolte> games;

    public static void main(String[] args) {

        // Set log level to info
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ROOT")).setLevel(Level.INFO);

        if (USE_DB) {
            DBEnv.setup();
            // TODO reimplement db persistence
            //games = RevolteDAO.getAll();
        } else {
            games = new ConcurrentHashMap<>();
        }

        port(Integer.parseInt(System.getenv("PORT")));
        //useRequestLoggingJettyServer();

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

        webSocket("/game", GameWebSocket.class);
    }

    static String error(Response a, int code, String message) {
        a.status(code);
        a.type("text/plain");
        return code + ": " + message;
    }

    static Map<String, Revolte> getGames() {
        return games;
    }
}
