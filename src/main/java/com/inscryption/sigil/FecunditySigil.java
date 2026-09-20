package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.CardType;
import com.inscryption.model.GameEvent;
import com.inscryption.model.GameEventType;
import com.inscryption.player.Player;

/**
 * When this card is played, a copy of it enters the owner's hand. Uses
 * Card.getSourceType() to build the copy - same technique as Unkillable.
 * Tagged with ConjuredMarker, same as Bees Within and The Smoke - conjured
 * for this fight, not something that should linger in the deck afterward.
 */
public class FecunditySigil implements Sigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        if (event.getType() != GameEventType.PLACE || event.getSource() != owner) {
            return;
        }
        CardType sourceType = owner.getSourceType();
        if (sourceType == null) {
            return;
        }
        Player controllingPlayer = ownerOf(owner, context);
        if (controllingPlayer != null) {
            Card copy = sourceType.create();
            copy.addSigil(new ConjuredMarker());
            controllingPlayer.addToHand(copy);
        }
    }

    @Override
    public String getName() {
        return "Fecundity";
    }
}
