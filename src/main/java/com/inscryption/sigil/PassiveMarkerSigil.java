package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;

/**
 * Base class for sigils that are pure markers - they don't react to any
 * game event themselves. Instead, GameEngine (or another rule) checks for
 * their typed presence at the exact moment it matters (for example, whether
 * an attacker carries Airborne or a dying card carries Bone King). Extending
 * this instead of implementing Sigil directly means each marker inherits the
 * same no-op event behavior rather than duplicating it.
 */
public abstract class PassiveMarkerSigil implements Sigil {

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        // Passive - queried by game rules when relevant, not event-reactive.
    }
}
