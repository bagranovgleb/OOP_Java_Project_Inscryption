package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;
import com.inscryption.model.Tribe;

/**
 * Hidden metadata marking a card as belonging to a tribe. Tribe identity is
 * represented by the {@link Tribe} enum rather than display-name strings, so
 * totems and other rules can query it safely without depending on UI text.
 * One reusable class is sufficient because the tribe varies as data, not as
 * behavior.
 */
public class TribeSigil implements Sigil {

    private final Tribe tribe;

    public TribeSigil(Tribe tribe) {
        this.tribe = tribe;
    }

    public Tribe getTribe() {
        return tribe;
    }

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        // No active effect - identity only.
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public String getName() {
        return tribe.getDisplayName();
    }

    @Override
    public boolean sameKindAs(Sigil other) {
        return other instanceof TribeSigil tribeSigil && tribeSigil.tribe == tribe;
    }
}
