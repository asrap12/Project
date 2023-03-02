/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * An event representing the game being initialized. If the snake or apple were randomly placed, the respective fields
 * would also have that information, allowing a consumer to deterministically replay the event.
 *
 * Note: This class is immutable and does not reference any mutable types.
 */

package FinalProject;

import java.util.*;

public class InitializeGameEvent extends GameEvent {
    private final Size size;
    private final List<Location> snakeSegments;
    private final Location appleLocation;

    public InitializeGameEvent(Size size, List<Location> snakeSegments, Location appleLocation) {
        super(EventType.INITIALIZE);
        this.size = size;
        this.snakeSegments = List.copyOf(snakeSegments); // Make a defensive immutable copy; the original could change.
        this.appleLocation = appleLocation;
    }

    @Override
    public String toString() {
        return "%s(%s, %s, %s)".formatted(super.toString(), this.size, this.snakeSegments, this.appleLocation);
    }

    public Size getSize() {
        return this.size;
    }

    public List<Location> getSnakeSegments() {
        return this.snakeSegments;
    }

    public Location getAppleLocation() {
        return this.appleLocation;
    }
}
