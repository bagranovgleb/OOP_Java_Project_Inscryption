package com.inscryption.modifier;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.Tribe;

/**
 * Blueprint modifier that buffs every player card belonging to one tribe.
 * Tribe identity is typed, so the rule does not depend on display-name text.
 */
public class SigilTotemModifier implements ChallengeModifier {

    private final Tribe targetTribe;
    private final int attackBonus;
    private final int healthBonus;

    public SigilTotemModifier(Tribe targetTribe, int attackBonus, int healthBonus) {
        this.targetTribe = targetTribe;
        this.attackBonus = attackBonus;
        this.healthBonus = healthBonus;
    }

    @Override
    public void apply(GameContext context) {
        for (var slot : context.getBoard().getPlayerSlots()) {
            Card occupant = slot.getOccupant();
            if (occupant != null && occupant.hasTribe(targetTribe)) {
                if (attackBonus > 0) {
                    occupant.buffAttack(attackBonus);
                }
                if (healthBonus > 0) {
                    occupant.heal(healthBonus);
                }
            }
        }
    }

    @Override
    public boolean isCompatibleWith(ChallengeModifier other) {
        return true;
    }

    @Override
    public String getName() {
        return "Totem: " + targetTribe.getDisplayName();
    }
}
