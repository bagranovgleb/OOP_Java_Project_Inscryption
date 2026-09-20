package com.inscryption.item;

import com.inscryption.model.Card;
import com.inscryption.model.CardType;
import com.inscryption.sigil.ConjuredMarker;

/** Black Goat in a Bottle: creates a Black Goat (0 power, 1 health, Worthy Sacrifice) in the user's hand. */
public class BlackGoatInABottleItem implements Item {

    @Override
    public String getName() {
        return "Black Goat in a Bottle";
    }

    @Override
    public String getDescription() {
        return "Creates a Black Goat in your hand.";
    }

    @Override
    public boolean use(ItemContext context) {
        Card goat = CardType.BLACK_GOAT.create();
        goat.addSigil(new ConjuredMarker()); // conjured, not earned - shouldn't return to the deck after the fight
        context.getPlayer().addToHand(goat);
        context.getUI().show("A Black Goat steps out of the bottle.");
        return true;
    }
}
