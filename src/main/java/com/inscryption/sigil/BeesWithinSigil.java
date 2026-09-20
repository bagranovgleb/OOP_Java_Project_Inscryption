package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.CardType;
import com.inscryption.model.GameEvent;
import com.inscryption.model.GameEventType;
import com.inscryption.player.Player;

/**
 * When this card is struck (takes a direct hit in combat), a Bee is
 * created in the ATTACKER's hand - a reward for popping the hive, not a
 * consolation prize for the hive's own owner. Reacts to ATTACK where this
 * card is the target - only fires for an attack that actually connects,
 * not one that missed entirely due to a bypass like Airborne. Whichever
 * side controls the attacking creature gets the Bee, regardless of which
 * side this card (the one being struck) itself belongs to. Tagged with
 * ConjuredMarker, same as the "in a bottle" items and The Smoke - it's
 * conjured for this fight specifically, not something that should linger
 * in the deck afterward.
 */
public class BeesWithinSigil implements Sigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        if (event.getType() != GameEventType.ATTACK || event.getTarget() != owner) {
            return;
        }
        Player attackingPlayer = ownerOf(event.getSource(), context);
        if (attackingPlayer != null) {
            Card bee = CardType.BEE.create();
            bee.addSigil(new ConjuredMarker());
            attackingPlayer.addToHand(bee);
        }
    }

    @Override
    public String getName() {
        return "Bees Within";
    }
}
