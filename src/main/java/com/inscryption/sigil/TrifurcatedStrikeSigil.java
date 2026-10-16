package com.inscryption.sigil;


/**
 * This card deals damage to the opposing spaces left, right, and directly
 * across from it - three lanes total. A passive marker:
 * GameEngine.resolveLaneAttack queries its type to pick target lanes.
 */
public class TrifurcatedStrikeSigil extends PassiveMarkerSigil {

    @Override
    public String getName() {
        return "Trifurcated Strike";
    }
}
