package com.inscryption.sigil;

import com.inscryption.engine.GameContext;
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
import com.inscryption.model.Card;
import com.inscryption.model.GameEvent;
import com.inscryption.model.GameEventType;
import com.inscryption.player.Player;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * When this card is played, its owner receives a random item if they have
 * room (max 3 held at once - see Player.MAX_ITEMS). Does nothing if the
 * inventory is already full - unlike the Gain Consumables run event, the
 * reference sheet doesn't mention a fallback reward (like a Pack Rat) for
 * Trinket Bearer specifically, so this one is silent when there's no room.
 * <p>
 * Hoggy Bank and Fish Hook are deliberately excluded - both have their own
 * specific acquisition method (a campfire destruction, and a boss fight
 * this project doesn't model, respectively), not general item draws. Plain
 * Boulder is also excluded - it remains a real, placeable item, but only
 * Boulder in a Bottle should ever come from a random item draw.
 */
public class TrinketBearerSigil implements Sigil {

    private static final List<Supplier<Item>> ITEM_POOL = List.of(
        MagnifyingGlassItem::new, ScissorsItem::new, PliersItem::new,
        BoulderInABottleItem::new, SquirrelInABottleItem::new, BlackGoatInABottleItem::new,
        FrozenOpossumInABottleItem::new, HourglassItem::new, HarpiesBirdlegFanItem::new);

    private final Random random = new Random();

    @Override
    public void apply(GameEvent event, Card owner, GameContext context) {
        if (event.getType() != GameEventType.PLACE || event.getSource() != owner) {
            return;
        }
        Player controllingPlayer = ownerOf(owner, context);
        if (controllingPlayer == null || !controllingPlayer.hasRoomForItem()) {
            return;
        }
        Item item = ITEM_POOL.get(random.nextInt(ITEM_POOL.size())).get();
        controllingPlayer.addItem(item);
    }

    @Override
    public String getName() {
        return "Trinket Bearer";
    }
}
