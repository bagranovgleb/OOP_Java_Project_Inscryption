package com.inscryption.sigil;

/**
 * This card counts as 3 blood rather than 1 when sacrificed. A passive
 * marker: Player.sacrificeCard queries its type rather than this class
 * doing anything reactively.
 */
public class WorthySacrificeSigil extends PassiveMarkerSigil {

    @Override
    public String getName() {
        return "Worthy Sacrifice";
    }
}
