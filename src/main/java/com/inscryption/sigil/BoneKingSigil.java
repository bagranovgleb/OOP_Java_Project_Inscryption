package com.inscryption.sigil;


/**
 * When this card dies, 4 bones are awarded instead of 1. A passive marker:
 * GameEngine's bone-crediting step queries its type rather than this
 * class doing anything reactively.
 */
public class BoneKingSigil extends PassiveMarkerSigil {

    @Override
    public String getName() {
        return "Bone King";
    }
}
