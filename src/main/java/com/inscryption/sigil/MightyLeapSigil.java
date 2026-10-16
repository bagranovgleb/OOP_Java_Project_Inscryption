package com.inscryption.sigil;


/**
 * This card blocks opposing Airborne creatures - normally Airborne skips
 * the defender entirely, but a card with Mighty Leap still intercepts it.
 * A passive marker: GameEngine.resolveLaneAttack queries its type.
 */
public class MightyLeapSigil extends PassiveMarkerSigil {

    @Override
    public String getName() {
        return "Mighty Leap";
    }
}
