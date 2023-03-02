/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * An event that represents a request to change directions, typically as a result of activating any of the directional
 * controls on a device such as a keyboard.
 *
 * Notes: This class is immutable.
 */

package FinalProject;

import java.util.*;

public final class ChangeDirectionEvent extends GameEvent {
    private static Map<Direction, ChangeDirectionEvent> singletons;

    private final Direction direction;

    private ChangeDirectionEvent(Direction direction) {
        super(EventType.MOVE);
        this.direction = direction;
    }

    // Get a singleton instance for the moving up event.
    public static ChangeDirectionEvent getInstance(Direction direction) {
        if (ChangeDirectionEvent.singletons == null)
            ChangeDirectionEvent.singletons = new HashMap<>(Direction.values().length);
        return ChangeDirectionEvent.singletons.computeIfAbsent(direction, ChangeDirectionEvent::new);
    }

    @Override
    public String toString() {
        return "%s(%s)".formatted(super.toString(), this.direction);
    }

    public Direction getDirection() {
        return this.direction;
    }
}
