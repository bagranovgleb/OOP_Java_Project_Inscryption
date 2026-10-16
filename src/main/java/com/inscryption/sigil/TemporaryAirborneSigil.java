package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;
import com.inscryption.model.GameEventType;

/**
 * A temporary grant of Airborne, lasting only until the owner's next
 * TURN_START - used by the Harpie's Birdleg Fan item, which affects only
 * creatures already on the board at the moment it's used, for the rest of
 * that turn only. It subclasses AirborneSigil, so every typed Airborne
 * query treats it exactly like the permanent ability, while this subclass
 * adds only the expiration behavior.
 * <p>
 * This relies on the same timing guarantee used elsewhere (e.g. Fledgling):
 * a card already on the board mid-turn won't receive another TURN_START
 * until its owner's side begins its next turn, so "the next TURN_START this
 * card sees" reliably means "the turn after the one it was granted in."
 */
public class TemporaryAirborneSigil extends AirborneSigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        if (event.getType() == GameEventType.TURN_START && event.getSource() == owner) {
            owner.removeSigil(this);
        }
    }

}
