package com.inscryption.run.event;

import com.inscryption.model.Card;
import com.inscryption.model.CardType;
import com.inscryption.model.ResourceType;
import com.inscryption.pelt.PeltKind;
import com.inscryption.player.Player;
import com.inscryption.run.NodeContent;
import com.inscryption.run.RunContext;
import com.inscryption.sigil.RandomizableSigils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.inscryption.util.GameRandom;
import java.util.Random;

/**
 * Trade Pelts: spend pelts to buy cards from the Trader's market. Works
 * through pelt kinds automatically in increasing order - Rabbit, then
 * Wolf, then Sheep, then their Fused counterparts - letting you spend as
 * many pelts of one kind as you hold before moving to the next, rather
 * than limiting you to a single purchase per visit. A "Leave" option is
 * always available if you want to stop early.
 * <p>
 * Each kind's market is generated once and shrinks as you buy from it
 * (bought cards are removed, not re-rolled), so you're working through one
 * real, fixed list per kind rather than a fresh random draw every
 * purchase. The market shown depends on the kind: Rabbit and Wolf offer up
 * to 8 eligible cards, Sheep up to 4 (matching that kind's cost filter -
 * see PeltKind). A fused kind's market shows fused (double-stat, via
 * Card.stitch on two copies of the same catalog entry) versions of the
 * same eligible cards instead of plain ones - "same rules for market" as
 * its base kind, just fused cards.
 * <p>
 * The Wolf market's cost filter is a maximum, not an exact match - any
 * card up to 3 Blood or up to 4 Bones qualifies, not just cards at exactly
 * that cost. Any eligible card genuinely under that ceiling gets a random
 * bonus sigil applied, to make up for being a lower tier than the pelt
 * would normally command - a card already sitting exactly at the ceiling
 * gets no bonus, since it's already a "true" Wolf-tier cost.
 * <p>
 * FINAL, CONFIRMED RULE STATEMENT (do not reintroduce the earlier, reverted
 * "free pelt purchase" behavior here - that belongs to BuyPeltsEvent, the
 * Trapper, which already grants one free Rabbit Pelt unconditionally on
 * every visit and needs no changes):
 * <ul>
 *   <li>Trapper (BuyPeltsEvent): gives 1 free Rabbit Pelt, always. No
 *   other special behavior. Already correct as-is.</li>
 *   <li>Trader (this class): gives 5 golden teeth if the player has no
 *   pelts of any kind to exchange - nothing else special.</li>
 * </ul>
 */
public class TradePeltsEvent implements NodeContent {

    private static final int NO_PELTS_TEETH_BONUS = 5;

    private final Random random;

    public TradePeltsEvent() {
        this(GameRandom.create());
    }

    /** Public so tests can inject a seeded Random for deterministic outcomes. */
    public TradePeltsEvent(Random random) {
        this.random = random;
    }

    @Override
    public String describe() {
        return "Trade Pelts: spend your pelts for cards from the Trader's market";
    }

    @Override
    public boolean resolve(RunContext context) {
        Player player = context.getPlayer();
        boolean hasAnyPelts = Arrays.stream(PeltKind.values()).anyMatch(k -> player.getPeltCount(k) > 0);
        if (!hasAnyPelts) {
            context.getUI().show("You have no pelts to trade - the Trader tosses you "
                + NO_PELTS_TEETH_BONUS + " golden teeth instead.");
            player.gainTeeth(NO_PELTS_TEETH_BONUS);
            return true;
        }

        for (PeltKind kind : PeltKind.values()) {
            if (player.getPeltCount(kind) == 0) {
                continue;
            }

            List<Card> market = generateMarket(kind);
            if (market.isEmpty()) {
                context.getUI().show("No eligible cards for the " + kind.getDisplayName() + " market - skipping.");
                continue;
            }

            while (player.getPeltCount(kind) > 0 && !market.isEmpty()) {
                List<String> optionTexts = new ArrayList<>(market.stream().map(Card::toString).toList());
                optionTexts.add("Leave");
                int choice = context.getUI().askChoice(
                    "You hold " + player.getPeltCount(kind) + " " + kind.getDisplayName() + "(s). The Trader's market:",
                    optionTexts);
                if (choice == market.size()) {
                    return true;
                }
                player.spendPelt(kind);
                Card bought = market.remove(choice);
                player.addToAnimalDeck(bought);
                context.getUI().show("Bought: " + bought);
            }
        }
        context.getUI().show("The Trader has nothing left you can afford.");
        return true;
    }

    private List<Card> generateMarket(PeltKind kind) {
        List<CardType> eligible = new ArrayList<>(Arrays.stream(CardType.values())
            .filter(t -> t != CardType.DAM && t != CardType.SQUIRREL)
            .filter(t -> kind.matchesCostFilter(t.create()))
            .toList());
        Collections.shuffle(eligible, random);

        List<Card> market = new ArrayList<>();
        int size = Math.min(kind.getMarketSize(), eligible.size());
        for (int i = 0; i < size; i++) {
            CardType type = eligible.get(i);
            Card card = kind.isFused() ? Card.stitch(type.create(), type.create()) : type.create();
            if (kind.baseKind() == PeltKind.WOLF && isBelowWolfCeiling(card)) {
                // Wolf's filter is a maximum, not an exact match - anything
                // genuinely under the ceiling (3 Blood / 4 Bones) gets a
                // random bonus sigil to make up for being a lower tier than
                // the pelt would normally command.
                card.addSigil(RandomizableSigils.randomExcluding(random, card));
            }
            market.add(card);
        }
        return market;
    }

    /** Whether a card's cost sits strictly below Wolf tier's ceiling (3 Blood or 4 Bones) - exactly at the ceiling gets no bonus, matching the original "true Wolf tier" cost. */
    private boolean isBelowWolfCeiling(Card card) {
        if (card.getCostType() == ResourceType.BLOOD) {
            return card.getCost() < 3;
        }
        return card.getCost() < 4;
    }
}
