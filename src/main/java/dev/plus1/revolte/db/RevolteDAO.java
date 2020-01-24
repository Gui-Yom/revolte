package dev.plus1.revolte.db;

import dev.plus1.revolte.Player;
import dev.plus1.revolte.Revolte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RevolteDAO {

    private static final Logger log = LoggerFactory.getLogger(RevolteDAO.class);

    public static List<Revolte> getAll() {
        try (Connection conn = DBEnv.getConnection()) {

            List<Revolte> games = new ArrayList<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("select * from games")) {
                while (rs.next()) {
                    Revolte game = new Revolte();
                    game.setThreadId(rs.getString(1));
                    game.setDay(rs.getInt(2));
                    games.add(game);
                }
            }
            if (!games.isEmpty())
                try (PreparedStatement pst = conn.prepareStatement("select * from players where gameid = ?")) {
                    for (Revolte game : games) {
                        Map<String, Player> players = new ConcurrentHashMap<>();
                        game.setPlayers(players);
                        //pst.setLong(1, game.getGameId());
                        try (ResultSet rs = pst.executeQuery()) {
                            while (rs.next()) {
                                Player p = PlayerDAO.parseFromQueryResult(rs);
                                players.put(p.getPsid(), p);
                            }
                        }
                    }
                }

            return games;
        } catch (SQLException e) {
            log.error("Cannot open connection to database.", e);
        }
        return null;
    }

    public static long save(Revolte game) {
        try {
            Connection conn = DBEnv.getConnection();
            try (PreparedStatement pst = conn.prepareStatement("insert into games(threadid, turn) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, game.getThreadId());
                pst.setInt(2, game.getDay());
                pst.executeQuery();
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Cannot open connection to database.", e);
        }
        return -1;
    }
}
