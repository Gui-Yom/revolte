module revolte {

    requires org.slf4j;
    requires logback.classic;

    requires spark.core;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires spark.template.thymeleaf;

    requires lombok;
    requires com.google.gson;
    requires okhttp3;

    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.flywaydb.core;

    opens dev.plus1.messenger.webhook to com.google.gson;
}