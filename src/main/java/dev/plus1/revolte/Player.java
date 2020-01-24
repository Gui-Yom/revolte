package dev.plus1.revolte;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Player {

    private String psid;
    private int health;
    private Role role;
    private long vote;

    public Player(String psid) {
        this.psid = psid;
        this.health = 1;
    }

    public void addHealth(int q) {
        this.health += q;
    }
}
