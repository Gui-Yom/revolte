package dev.plus1.revolte;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLogWriter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.embeddedserver.jetty.JettyServerFactory;

public final class SparkUtils {

    public static void useRequestLoggingJettyServer() {
        CustomRequestLog requestLog = new CustomRequestLog(new Slf4jRequestLogWriter(), CustomRequestLog.EXTENDED_NCSA_FORMAT);

        EmbeddedJettyFactory factory = new EmbeddedJettyFactory(new JettyServerFactory() {
            @Override
            public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
                Server server;
                if (maxThreads > 0) {
                    int min = minThreads > 0 ? minThreads : 8;
                    int idleTimeout = threadTimeoutMillis > 0 ? threadTimeoutMillis : '\uea60';
                    server = new Server(new QueuedThreadPool(maxThreads, min, idleTimeout));
                } else {
                    server = new Server();
                }
                server.setRequestLog(requestLog);
                return server;
            }

            @Override
            public Server create(ThreadPool threadPool) {
                Server server = threadPool != null ? new Server(threadPool) : new Server();
                server.setRequestLog(requestLog);
                return server;
            }
        });

        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, factory);
    }
}
