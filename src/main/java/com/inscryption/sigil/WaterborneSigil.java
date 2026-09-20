package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;

/**
 * On the opponent's turn, creatures attacking this card's space attack
 * directly instead - this card never blocks, permanently, from the
 * moment it's placed until it dies. Uses the preventsTargeting hook,
 * always returning true.
 */
public class WaterborneSigil implements Sigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        // No reactive effect - the pass-through behavior is a passive check
        // (preventsTargeting), not something triggered by an event.
    }

    @Override
    public boolean preventsTargeting(Card owner) {
        return true;
    }

    @Override
    public String getName() {
        return "Waterborne";
    }
}
