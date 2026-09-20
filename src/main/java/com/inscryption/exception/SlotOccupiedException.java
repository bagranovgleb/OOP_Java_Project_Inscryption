package com.inscryption.exception;

/** Thrown when a card is placed on a board slot that's already occupied. */
public class SlotOccupiedException extends RuntimeException {
    public SlotOccupiedException(String message) {
        super(message);
    }
}
