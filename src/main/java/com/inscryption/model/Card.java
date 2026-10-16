package com.inscryption.model;

import com.inscryption.engine.GameContext;
import com.inscryption.sigil.Sigil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for every creature card in the game.
 * <p>
 * Encapsulation: card state is private and can only be observed or changed
 * through the methods below (getters, takeDamage, heal, sacrifice, move, buffs) -
 * nothing outside this class can mutate health directly.
 * <p>
 * Abstraction: callers (the engine, sigils) never need to know which concrete
 * subclass or which sigils a card has - they just call applySigils()/onAttack()
 * and the right behavior happens.
 */
public class Card {

    private final String name;
    private int attack;
    private int health;
    private int maxHealth;
    private boolean exhausted;
    private final int cost;
    private final ResourceType costType;

    /**
     * Which catalog entry this card was built from, if any - set directly by
     * CardType.create() (same package). Lets a sigil like Unkillable make a
     * fresh copy of this exact card via sourceType.create(). Null for cards
     * built ad hoc outside the catalog (e.g. in tests), which simply can't
     * support "make a copy of me" sigils.
     */
    private CardType sourceType;

    private final List<Sigil> sigils = new ArrayList<>();

    protected Card(String name, int attack, int health, int cost, ResourceType costType) {
        if (attack < 0 || health <= 0) {
            throw new IllegalArgumentException("Attack must be >= 0 and health > 0");
        }
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.maxHealth = health;
        this.cost = cost;
        this.costType = costType;
    }

    public void addSigil(Sigil sigil) {
        sigils.add(sigil);
    }

    /**
     * Removes a specific sigil instance - used by sigils that expire
     * themselves (e.g. a temporary Airborne grant removing itself once its
     * owner's next turn starts). Removes by reference, not by name, so a
     * card with several sigils of the same kind only loses the exact one
     * that asked to be removed.
     */
    public void removeSigil(Sigil sigil) {
        sigils.remove(sigil);
    }

    /** Returns a read-only view; callers cannot add/remove sigils from outside. */
    public List<Sigil> getSigils() {
        return List.copyOf(sigils);
    }

    /** Same as getSigils(), but excludes hidden identity sigils - for UI display. */
    public List<Sigil> getVisibleSigils() {
        return sigils.stream().filter(sigil -> !sigil.isHidden()).toList();
    }

    /**
     * Type-safe ability query. Subclasses count as the same ability family,
     * which is important for temporary variants such as TemporaryAirborneSigil.
     */
    public boolean hasSigil(Class<? extends Sigil> sigilType) {
        for (Sigil sigil : sigils) {
            if (sigilType.isInstance(sigil)) {
                return true;
            }
        }
        return false;
    }

    /** Returns whether this card carries the given hidden tribe tag. */
    public boolean hasTribe(Tribe tribe) {
        for (Sigil sigil : sigils) {
            if (sigil instanceof com.inscryption.sigil.TribeSigil tribeSigil
                && tribeSigil.getTribe() == tribe) {
                return true;
            }
        }
        return false;
    }

    /**
     * Semantic duplicate check used when composing cards or granting a totem
     * sigil. It avoids coupling identity to the user-facing sigil name.
     */
    public boolean hasSameSigilKind(Sigil candidate) {
        for (Sigil sigil : sigils) {
            if (sigil.sameKindAs(candidate) || candidate.sameKindAs(sigil)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /** How much of costType this card takes to place on the board. 0 means free. */
    public int getCost() {
        return cost;
    }

    /** Which resource pays for this card. Irrelevant when cost is 0. */
    public ResourceType getCostType() {
        return costType;
    }

    /** Which CardType this card was built from, or null if built ad hoc. */
    public CardType getSourceType() {
        return sourceType;
    }

    /** Package-level construction hook used only by CardType.create(). */
    void setSourceType(CardType sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public boolean isAlive() {
        return health > 0;
    }

    /** True unless a sigil (e.g. a submerged Diver) is actively blocking targeting. */
    public boolean canBeTargeted() {
        for (Sigil sigil : sigils) {
            if (sigil.preventsTargeting(this)) {
                return false;
            }
        }
        return true;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        health = Math.max(0, health - amount);
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative");
        }
        health = Math.min(maxHealth, health + amount);
    }

    /** Permanently increases attack (e.g. a totem buffing every card of a species). */
    public void buffAttack(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Attack buff cannot be negative");
        }
        attack += amount;
    }

    /**
     * Permanently raises this card's health cap (e.g. Fledgling's generic
     * growth for a card with no distinct "grown form" in the catalog).
     * Also raises current health by the same amount, not just headroom -
     * growing the cap should make the card tougher right now, not just
     * later once healed.
     */
    public void buffMaxHealth(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Health buff cannot be negative");
        }
        maxHealth += amount;
        health += amount;
    }

    /** Kills the card outright; used when the player sacrifices it for resources. */
    public void sacrifice() {
        health = 0;
    }

    public void move() {
        exhausted = true;
    }

    public void resetExhaustion() {
        exhausted = false;
    }

    /**
     * Default attack behavior: deal this card's attack as damage to the target.
     * Polymorphism: subclasses (e.g. a boss with a multi-hit attack) can override
     * this instead of the engine special-casing card types.
     * <p>
     * Takes the amount to deal as a parameter rather than always using
     * this.attack, because the engine may need to adjust it first based on
     * board position (e.g. Stinky reducing an attacker's power while it
     * faces the Stinky card) - a subclass overriding this still gets to add
     * its own bonus on top of whatever the engine determined the attack is
     * actually worth this turn, rather than the engine bypassing it.
     */
    public void onAttack(Card target, int attackAmount) {
        target.takeDamage(attackAmount);
    }

    /**
     * Applies every sigil this card carries for the given event. The engine calls
     * this without knowing which (if any) sigils are attached - that's the point
     * of routing everything through the Sigil interface.
     */
    public final void applySigils(GameEvent event, GameContext context) {
        // Iterate a defensive copy: a sigil can legitimately remove itself
        // during its own apply() (e.g. a temporary grant expiring), and
        // mutating the live list mid-iteration would throw
        // ConcurrentModificationException otherwise.
        for (Sigil sigil : List.copyOf(sigils)) {
            sigil.apply(event, this, context);
        }
    }

    @Override
    public String toString() {
        return describe(attack);
    }

    /**
     * Same formatting as toString(), but with a caller-supplied attack value
     * instead of the stored one - used by GameEngine.getDisplayAttack() so a
     * UI can show a card's real, current attack (e.g. an Ant's swarm-scaled
     * power) without this class needing any awareness of the board itself.
     * <p>
     * Also lists the card's visible sigils - not just its original ones,
     * but anything added later too (e.g. a sigil gained via Mysterious
     * Stones), so a power-up is actually visible during a fight, not just
     * at the moment the event granted it.
     */
    public String describe(int displayAttack) {
        String costPart = cost > 0
            ? String.format(" {cost: %d %s}", cost, costType.name().toLowerCase())
            : "";
        String sigilPart = visibleSigilsSuffix();
        String exhaustedPart = exhausted ? " (exhausted)" : "";
        return String.format("%s [%d/%d]%s%s%s", name, displayAttack, health, costPart, sigilPart, exhaustedPart);
    }

    /** A short bracketed list of this card's real, player-visible abilities (e.g. " [Sharp Quills]"), or "" if it has none worth showing. */
    private String visibleSigilsSuffix() {
        List<String> names = getVisibleSigils().stream()
            .map(Sigil::getName)
            .filter(n -> !n.equals("Extra Sigil"))
            .toList();
        return names.isEmpty() ? "" : " [" + String.join(", ", names) + "]";
    }

    /**
     * Fuses two cards into one: combined attack, combined health, and the
     * union of their sigils - not a concatenation. Fusing two copies of the
     * same card (e.g. two Mantis Gods) is a real, common case, and their
     * sigils genuinely are the same thing, not two separate copies of it -
     * a fused card should never end up with a sigil listed twice.
     */
    public static Card stitch(Card a, Card b) {
        int combinedAttack = a.getAttack() + b.getAttack();
        int combinedHealth = a.getMaxHealth() + b.getMaxHealth();
        Card result = new Card(a.getName(), combinedAttack, combinedHealth, a.getCost(), a.getCostType());
        for (Sigil sigil : a.getSigils()) {
            if (!result.hasSameSigilKind(sigil)) {
                result.addSigil(sigil);
            }
        }
        for (Sigil sigil : b.getSigils()) {
            if (!result.hasSameSigilKind(sigil)) {
                result.addSigil(sigil);
            }
        }
        return result;
    }
}
