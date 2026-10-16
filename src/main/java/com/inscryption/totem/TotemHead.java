package com.inscryption.totem;

import com.inscryption.model.Tribe;

/** A totem head - defines which tribe an assembled totem applies to. */
public class TotemHead implements TotemPiece {

    private final Tribe tribe;

    public TotemHead(Tribe tribe) {
        this.tribe = tribe;
    }

    public Tribe getTribe() {
        return tribe;
    }

    @Override
    public String describe() {
        return "Head (" + tribe.getDisplayName() + ")";
    }
}
