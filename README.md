# Inscryption

A Java/JavaFX card battler inspired by *Inscryption*, built as an OOP course project. Players draft a deck, fight through three locations of escalating enemies, and face a boss at the end of each — all governed by a sigil system where individual card abilities compose independently rather than being hardcoded per-card.

## Features

- **A real, sealed deck** — cards can only be drawn one at a time from the front, or peeked at with specific items; nothing can search, reorder, or reach into a deck's contents directly.
- **~50 sigils** covering combat modifiers (Airborne, Waterborne, Bifurcated/Trifurcated Strike, Burrower, Loose Tail, Hefty), resource generation (Bone King, Ant Spawner, Rabbit Hole, Bees Within), and growth/transformation (Fledgling).
- **A full combat engine** — lane-based board, reserve rows with turn-delayed promotion, sacrifice-based resource payment, piercing overkill, and health-scale win/loss tracking.
- **3 locations** (Woodlands, Wetlands, Snowlands), each with regular fights, run events (Trader, Trapper, Woodcarver, Bone Altar, Mysterious Stones, Deck Trial, and more), and a boss fight requiring two round wins.
- **A candle/lives system** — losing a fight costs a candle rather than ending the run outright, with boss fights capping the player to their last one.
- **Two front ends sharing identical game logic** — a console version and a JavaFX GUI — both driven through the same `GameUI` interface, so the rules are never duplicated between them.
- **A growing regression suite** (138 checks and counting) exercising sigil interactions, event outcomes, and full battle sequences.

## Tech stack

- Java 17
- Maven
- JavaFX 21 (GUI)
- JUnit 5 (declared for future use; the primary regression suite is a standalone assertion battery — see [Testing](#testing))

## Running it

```bash
# Console version — play a full run in the terminal
mvn exec:java

# JavaFX GUI
mvn javafx:run
```

## Testing

```bash
mvn compile
java -cp target/classes com.inscryption.Main
```

`Main` is a self-contained battery of scripted scenarios and assertions covering sigil mechanics, event outcomes, and full battles — not a game to play, but the project's regression safety net. It prints a running log of each check and a final pass/fail summary.

## Project structure

```
src/main/java/com/inscryption/
├── ai/         opponent decision-making
├── board/      lane/slot board state
├── cli/        console entry point and the core Battle loop
├── deck/       the sealed Deck abstraction
├── engine/     combat resolution, sigil dispatch, turn sequencing
├── gui/        JavaFX front end
├── item/       consumable items (Boulder, Magnifying Glass, "in a bottle" items, ...)
├── model/      Card, CardType catalog, GameEvent
├── modifier/   challenge modifiers
├── pelt/       the Trapper/Trader pelt economy
├── player/     player state — hand, decks, bones, teeth, totems
├── run/        the run's path structure, nodes, and events (Trader, Woodcarver, ...)
├── sigil/      every card ability, each an independent, composable unit
├── totem/      totem heads/bodies and active-totem assembly
├── ui/         the GameUI interface shared by both front ends
└── util/       shared utilities
```

## Design notes

- **Sigils are independent and composable.** A card's abilities are a list of `Sigil` implementations rather than per-card `if` branches — new mechanics are added by writing a new `Sigil`, not by touching existing card logic.
- **Cards conjured mid-fight don't pollute the deck.** Anything created by an item or sigil during a battle (Bees Within's Bee, a bottled Boulder, Fledgling's grown form) is tagged and excluded when the deck reshuffles after the fight — only genuinely earned cards persist.
- **The deck's seal is never bypassed**, even by mechanics that need to interact with its full contents (like drawing an entire deck for an event) — those work by repeatedly using the deck's own narrow interface, never a bulk read.
