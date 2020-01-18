package dev.plus1.revolte;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBEnv {

    private static final Logger log = LoggerFactory.getLogger(DBEnv.class);
    private static final String DATABASE_URL = System.getenv("DATABASE_URL");
    private static final HikariDataSource ds;
    private static final Flyway flyway;

    static {
        URI dbUri = null;
        try {
            dbUri = new URI(DATABASE_URL);
        } catch (URISyntaxException e) {
            log.error("Malformated URI", e);
            System.exit(-1);
        }

        String[] userinfo = dbUri.getUserInfo().split(":");
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(userinfo[0]);
        config.setPassword(userinfo[1]);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        flyway = Flyway.configure()
                         .dataSource(ds)
                         .locations("classpath:db")
                         .load();
    }

    public static void setup() {
        flyway.migrate();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
