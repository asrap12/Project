/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * An event representing the spawning of an apple in the game.
 *
 * Note: This class is immutable and does not reference any mutable types.
 */

package FinalProject;

public final class SpawnAppleGameEvent extends GameEvent {
    private final Location location;

    public SpawnAppleGameEvent(Location location) {
        super(EventType.SPAWN_APPLE);
        this.location = location;
    }

    @Override
    public String toString() {
        return "%s(%d, %d)".formatted(super.toString(), this.location.row, this.location.column);
    }

    public Location getLocation() {
        return this.location;
    }
}
