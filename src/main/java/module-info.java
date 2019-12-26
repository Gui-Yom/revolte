module revolte {

    requires spark.core;
    requires org.slf4j;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires com.google.gson;
    requires lombok;

    opens dev.plus1.revolte.data to com.google.gson;
}