package com.inscryption.model;

/**
 * Stable domain identifiers for the card tribes used by deck trials and
 * totems. Display text is kept separate from the programmatic identity so
 * renaming a label cannot silently break game rules.
 */
public enum Tribe {
    AVIAN("Avian"),
    CANINE("Canine"),
    HOOVED("Hooved"),
    INSECT("Insect"),
    REPTILE("Reptile"),
    SQUIRREL("Squirrel"),
    ANT("Ant");

    private final String displayName;

    Tribe(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
