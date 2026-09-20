package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;
import com.inscryption.model.GameEventType;
import com.inscryption.model.SpecialCardType;
import com.inscryption.player.Player;

/** When this card is played, a Rabbit is created in the owner's hand. Tagged with ConjuredMarker, same as Bees Within and The Smoke - conjured for this fight, not something that should linger in the deck afterward. */
public class RabbitHoleSigil implements Sigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        if (event.getType() != GameEventType.PLACE || event.getSource() != owner) {
            return;
        }
        Player controllingPlayer = ownerOf(owner, context);
        if (controllingPlayer != null) {
            Card rabbit = SpecialCardType.RABBIT.create();
            rabbit.addSigil(new ConjuredMarker());
            controllingPlayer.addToHand(rabbit);
        }
    }

    @Override
    public String getName() {
        return "Rabbit Hole";
    }
}
