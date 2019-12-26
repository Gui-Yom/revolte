package dev.plus1.revolte;

import static dev.plus1.revolte.SparkUtils.useRequestLoggingJettyServer;
import static spark.Spark.get;

public final class App {

    //private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        useRequestLoggingJettyServer();

        get("/", (q, a) -> {
            return "Henlo " + q.ip();
        });
    }
}
