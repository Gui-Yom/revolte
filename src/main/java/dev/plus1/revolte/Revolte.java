package dev.plus1.revolte;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public final class Revolte {

    private static final Logger log = LoggerFactory.getLogger(Revolte.class);

    private long gameId;
    private String threadId;
    private List<Player> players;
    private int turn;

    public Revolte(String threadId) {
        this.threadId = threadId;
        this.turn = 0;
        this.players = new ArrayList<>();
        this.gameId = persist();
    }

    private Revolte() {
        this.players = new ArrayList<>();
    }

    static List<Revolte> retrieveAll() {
        try (Connection conn = DBEnv.getConnection()) {

            List<Revolte> games = new ArrayList<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("select * from games")) {
                while (rs.next()) {
                    Revolte game = new Revolte();
                    game.gameId = rs.getLong(1);
                    game.threadId = rs.getString(2);
                    game.turn = rs.getInt(3);
                    games.add(game);
                }
            }
            if (!games.isEmpty())
                try (PreparedStatement pst = conn.prepareStatement("select * from players where gameid = ?")) {
                    for (Revolte game : games) {
                        pst.setLong(1, game.gameId);
                        try (ResultSet rs = pst.executeQuery()) {
                            while (rs.next()) {
                                game.players.add(Player.fromQueryResult(rs));
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

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void distributeRoles() {
        int size = players.size();
        List<Role> roles = Arrays.asList(
                Role.KING, Role.QUEEN, Role.DRUID, Role.JUDGE, Role.CITIZEN,
                Role.ASSASSIN,
                Role.REBEL, Role.REBEL, Role.REBEL);
        switch (size) {
            case 10:
                roles.remove(Role.CITIZEN);
                roles.add(Role.REBEL);
                roles.add(Role.WIZARD);
                break;
            case 11:
                roles.remove(Role.WIZARD);
                roles.add(Role.TWIN);
                roles.add(Role.TWIN);
                break;
            case 12:
                roles.add(Role.JESTER);
                break;
            case 13:
                roles.add(Role.FARSEER);
                break;
            case 14:
                roles.remove(Role.TWIN);
                roles.remove(Role.TWIN);
                roles.add(Role.REBEL);
                roles.add(Role.WIZARD);
                roles.add(Role.HONEST_CITIZEN);
                break;
            case 15:
                roles.remove(Role.WIZARD);
                roles.add(Role.TWIN);
                roles.add(Role.TWIN);
                break;
            case 16:
                roles.remove(Role.HONEST_CITIZEN);
                roles.add(Role.REBEL);
                break;
            case 17:
                roles.add(Role.HONEST_CITIZEN);
                break;
            case 18:
                roles.remove(Role.HONEST_CITIZEN);
                roles.add(Role.LOVER);
                roles.add(Role.LOVER);
                break;
            case 19:
                roles.add(Role.HONEST_CITIZEN);
                break;
            case 20:
                roles.add(Role.CITIZEN);
                break;
            default:
                log.error("No role distribution policy has been set for {} players !", size);
                break;
        }

        Random rng = new Random();
        Collections.shuffle(roles, rng);
        AtomicInteger max = new AtomicInteger(roles.size());
        for (Player p : players) {
            p.setRole(roles.remove(rng.nextInt(max.get())));
            max.decrementAndGet();

            if (p.getRole() == Role.KING) {
                p.addHealth(size < 14 ? 2 : 3);
            } else if (p.getRole() == Role.QUEEN && size >= 18) {
                p.addHealth(1);
            }
        }
        log.info("Distributed roles !");
    }

    long persist() {
        try {
            Connection conn = DBEnv.getConnection();
            try (PreparedStatement pst = conn.prepareStatement("insert into games(threadid, turn) values (?, ?)", new String[] { "gameid" })) {
                pst.setString(1, threadId);
                pst.setInt(2, turn);
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
