package dev.plus1.revolte;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Player {

    private static final Logger log = LoggerFactory.getLogger(Revolte.class);

    private long playerId;
    private String psid;
    private int health;
    private Role role;
    private long vote;

    public Player(String psid) {
        this.psid = psid;
        this.health = 1;
        this.playerId = save();
    }

    private Player() {

    }

    static Player fromQueryResult(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setPlayerId(rs.getLong(1));
        player.setPsid(rs.getString(2));
        player.setHealth(rs.getInt(3));
        player.setRole(Role.valueOf(rs.getString(4)));
        player.setVote(rs.getLong(6));
        return player;
    }

    public void addHealth(int q) {
        this.health += q;
    }

    long save() {
        try {
            Connection conn = DBEnv.getConnection();
            try (PreparedStatement pst = conn.prepareStatement("insert into players(psid, health, role) values (?, ?, ?)", new String[] { "playerid" })) {
                pst.setString(1, psid);
                pst.setInt(2, health);
                pst.setString(3, role.name());
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
}
