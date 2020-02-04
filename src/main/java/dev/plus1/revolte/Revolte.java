package dev.plus1.revolte;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
public final class Revolte {

    private static final Logger log = LoggerFactory.getLogger(Revolte.class);

    /**
     * Serve as key
     */
    private String threadId;
    private Map<String, Player> players;
    private int day;
    private Phase phase;
    private Map<Phase, Duration> phasesDuration;
    private Instant phaseEnd;
    private transient Timer timer;

    /**
     * Create a new game and start it.
     *
     * @param threadId
     * @param phasesDuration
     */
    public Revolte(String threadId, Map<Phase, Duration> phasesDuration) {
        this.threadId = threadId;
        this.day = 0;
        this.phase = null;
        this.phasesDuration = phasesDuration;
        this.players = new ConcurrentHashMap<>();
        this.timer = new Timer();

        // TODO game functionnalities are currently disabled
        //startJoinPhase();
    }

    public void nextPhase(Phase phase) {
        this.phase = phase;
        this.phaseEnd = Instant.now().plus(phasesDuration.get(phase));
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // End of phase
                nextPhase(phase.next());
            }
        }, Date.from(phaseEnd));
    }

    public void startJoinPhase() {
        this.phase = Phase.JOIN;
        this.phaseEnd = Instant.now().plus(phasesDuration.get(phase));
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // End of enroll phase
                distributeRoles();
                nextPhase(Phase.JOIN.next());
            }
        }, Date.from(phaseEnd));
    }

    public void addPlayer(Player player) {
        this.players.put(player.getPsid(), player);
    }

    public void distributeRoles() {
        int size = players.size();
        List<Role> roles = Arrays.asList(
                Role.KING, Role.QUEEN, Role.DRUID, Role.JUDGE, Role.CITIZEN,
                Role.ASSASSIN,
                Role.REBEL, Role.REBEL, Role.REBEL);
        switch (size) {
            case 9:
                break;
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
        for (Player p : players.values()) {
            p.setRole(roles.remove(rng.nextInt(max.getAndDecrement())));

            if (p.getRole() == Role.KING) {
                p.addHealth(size < 14 ? 2 : 3);
            } else if (p.getRole() == Role.QUEEN && size >= 18) {
                p.addHealth(1);
            }
        }
        log.info("Distributed roles !");
    }
}
