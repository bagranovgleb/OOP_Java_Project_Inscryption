package com.inscryption.board;

import com.inscryption.exception.SlotOccupiedException;
import com.inscryption.model.Card;

public class Slot implements ReadOnlySlot {

    private Card occupant;

    @Override
    public boolean isEmpty() {
        return occupant == null;
    }

    @Override
    public Card getOccupant() {
        return occupant;
    }

    public void place(Card card) {
        if (!isEmpty()) {
            throw new SlotOccupiedException("Slot is already occupied by " + occupant.getName());
        }
        occupant = card;
    }

    public void clear() {
        occupant = null;
    }
}
