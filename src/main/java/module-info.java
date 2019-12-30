module revolte {

    requires spark.core;
    requires org.slf4j;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires com.google.gson;
    requires lombok;
    requires spark.template.thymeleaf;
    requires okhttp3;

    opens dev.plus1.messenger.webhook to com.google.gson;
}