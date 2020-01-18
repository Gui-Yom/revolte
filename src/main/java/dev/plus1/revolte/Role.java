package dev.plus1.revolte;

public enum Role {
    KING("Roi"),
    QUEEN("Reine"),
    WIZARD("Sorcier"),
    JESTER("Bouffon"),
    ASSASSIN("Assassin"),
    TWIN("Jumelle"),
    REBEL("Révolté"),
    REBEL_CHIEF("Chef des révoltés"),
    DRUID("Druide"),
    FARSEER("Clairvoyant"),
    JUDGE("Juge"),
    HONEST_CITIZEN("Honnête citoyen"),
    CITIZEN("Citoyen"),
    LOVER("Amant");

    private String text;

    Role(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
