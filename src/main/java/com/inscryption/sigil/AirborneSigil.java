package com.inscryption.sigil;


/**
 * This card will ignore opposing cards and strike the defending player
 * directly - unless the defender carries Mighty Leap. A passive marker: no
 * reactive behavior of its own. GameEngine.resolveLaneAttack queries its type when resolving an attack.
 */
public class AirborneSigil extends PassiveMarkerSigil {

    @Override
    public String getName() {
        return "Airborne";
    }

    @Override
    public boolean sameKindAs(Sigil other) {
        return other instanceof AirborneSigil;
    }
}
