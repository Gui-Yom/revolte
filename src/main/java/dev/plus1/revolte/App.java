package dev.plus1.revolte;

import static dev.plus1.revolte.SparkUtils.useRequestLoggingJettyServer;
import static spark.Spark.get;
import static spark.Spark.port;

public final class App {

    //private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        port(Integer.parseInt(System.getenv("PORT")));
        useRequestLoggingJettyServer();

        get("/", (q, a) -> {
            return "Henlo " + q.ip() + ", page_access_token=" + System.getenv("PAGE_ACCESS_TOKEN");
        });
    }
}
