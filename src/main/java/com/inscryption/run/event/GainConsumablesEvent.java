package com.inscryption.run.event;

import com.inscryption.item.BlackGoatInABottleItem;
import com.inscryption.item.BoulderInABottleItem;
import com.inscryption.item.FrozenOpossumInABottleItem;
import com.inscryption.item.HarpiesBirdlegFanItem;
import com.inscryption.item.HourglassItem;
import com.inscryption.item.Item;
import com.inscryption.item.MagnifyingGlassItem;
import com.inscryption.item.PliersItem;
import com.inscryption.item.ScissorsItem;
import com.inscryption.item.SquirrelInABottleItem;
import com.inscryption.model.CardType;
import com.inscryption.player.Player;
import com.inscryption.run.NodeContent;
import com.inscryption.run.RunContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.inscryption.util.GameRandom;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Gain Consumables: choose a first item from 3 random, distinct options -
 * then, if there's still room afterward, choose a second item from a
 * freshly-rolled set of 3 more options. If the inventory is already full
 * at the start of the visit, a Pack Rat card is given instead of any
 * choice at all (which, fittingly, carries Trinket Bearer, so there's
 * still a shot at a future item once room opens up).
 * <p>
 * Hoggy Bank and Fish Hook are deliberately excluded from this pool - both
 * have their own specific acquisition method described in the reference
 * sheet (a campfire destruction, and defeating a specific boss this
 * project doesn't model, respectively) rather than being generally
 * obtainable. Plain Boulder is also excluded - it remains a real,
 * placeable item, but only Boulder in a Bottle should ever come from a
 * random item draw.
 */
public class GainConsumablesEvent implements NodeContent {

    private static final List<Supplier<Item>> ITEM_POOL = List.of(
        MagnifyingGlassItem::new, ScissorsItem::new, PliersItem::new,
        BoulderInABottleItem::new, SquirrelInABottleItem::new, BlackGoatInABottleItem::new,
        FrozenOpossumInABottleItem::new, HourglassItem::new, HarpiesBirdlegFanItem::new);

    private final Random random;

    public GainConsumablesEvent() {
        this(GameRandom.create());
    }

    /** Public so tests (including Main.java's own scenarios) can inject a seeded Random for deterministic outcomes. */
    public GainConsumablesEvent(Random random) {
        this.random = random;
    }

    @Override
    public String describe() {
        return "Gain Consumables: choose up to two items, one at a time, while you have room";
    }

    @Override
    public boolean resolve(RunContext context) {
        Player player = context.getPlayer();

        boolean firstPickHappened = offerOneItem(context, player);
        if (firstPickHappened && player.hasRoomForItem()) {
            offerOneItem(context, player);
        }
        return true;
    }

    /**
     * Offers one round of up to 3 random distinct items, if there's room.
     * Grants a Pack Rat instead and returns false if there wasn't. Returns
     * true if an item was actually offered and added.
     */
    private boolean offerOneItem(RunContext context, Player player) {
        if (!player.hasRoomForItem()) {
            context.getUI().show("Your item inventory is already full - here's a Pack Rat instead.");
            player.addToAnimalDeck(CardType.PACK_RAT.create());
            return false;
        }

        List<Supplier<Item>> options = pickDistinct(3);
        List<Item> previews = options.stream().map(Supplier::get).toList();
        List<String> optionTexts = previews.stream()
            .map(item -> item.getName() + " - " + item.getDescription())
            .toList();
        int choice = context.getUI().askChoice("Choose an item:", optionTexts);
        Item chosen = previews.get(choice);
        player.addItem(chosen);
        context.getUI().show(chosen.getName() + " added to your inventory.");
        return true;
    }

    private List<Supplier<Item>> pickDistinct(int count) {
        List<Supplier<Item>> pool = new ArrayList<>(ITEM_POOL);
        Collections.shuffle(pool, random);
        return pool.subList(0, Math.min(count, pool.size()));
    }
}
