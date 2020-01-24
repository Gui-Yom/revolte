package dev.plus1.revolte.db;

import dev.plus1.revolte.Player;
import dev.plus1.revolte.Revolte;
import dev.plus1.revolte.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {

    private static final Logger log = LoggerFactory.getLogger(Revolte.class);

    public static long insertPlayer(Player player) {
        try {
            Connection conn = DBEnv.getConnection();
            try (PreparedStatement pst = conn.prepareStatement("insert into players(psid, health, role) values (?, ?, ?)", new String[] { "playerid" })) {
                pst.setString(1, player.getPsid());
                pst.setInt(2, player.getHealth());
                pst.setString(3, player.getRole().name());
                if (pst.execute()) {
                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to persist Revolte object into games table.", e);
        }
        return -1;
    }

    public static Player parseFromQueryResult(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setPsid(rs.getString(2));
        player.setHealth(rs.getInt(3));
        player.setRole(Role.valueOf(rs.getString(4)));
        player.setVote(rs.getLong(6));
        return player;
    }
}
