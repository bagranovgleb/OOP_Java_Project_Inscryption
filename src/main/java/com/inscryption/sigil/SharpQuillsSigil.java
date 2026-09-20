package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;
import com.inscryption.model.GameEventType;

/** Damages whoever attacks the card carrying this sigil. */
public class SharpQuillsSigil implements Sigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        if (event.getType() == GameEventType.ATTACK && event.getTarget() == owner) {
            Card attacker = event.getSource();
            if (attacker != null) {
                attacker.takeDamage(owner.getAttack());
            }
        }
    }

    @Override
    public String getName() {
        return "Sharp Quills";
    }
}
