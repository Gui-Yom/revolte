package dev.plus1.revolte;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Player {

    private String psid;
    private int health;
    private Role role = null;
    /**
     * The player that is voted to die by this player
     */
    private String vote;
    private boolean markedForDeath = false;
    private boolean isProtected = false;
    private boolean isRolePublic = false;

    public Player(String psid) {
        this.psid = psid;
        this.health = 1;
    }

    public void addHealth(int q) {
        this.health += q;
    }

    public boolean isDead() {
        return health == 0;
    }
}
