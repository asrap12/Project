/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * A base class for all game event notifications. A listener can use a sequence of these events to replay a game frame
 * by frame.
 *
 * Notes: Derived classes are expected to be immutable.
 */

package FinalProject;

import java.io.*;

public class GameEvent implements Serializable {
    private static GameEvent startGameEvent;
    private static GameEvent renderGameEvent;

    protected final EventType type;

    protected GameEvent(EventType type) {
        this.type = type;
    }

    // Get a singleton instance for the start game event.
    public static GameEvent getStartEvent() {
        if (GameEvent.startGameEvent == null)
            return GameEvent.startGameEvent = new GameEvent(EventType.START);
        return GameEvent.startGameEvent;
    }

    // Get a singleton instance for the render game event.
    public static GameEvent getRenderEvent() {
        if (GameEvent.renderGameEvent == null)
            return GameEvent.renderGameEvent = new GameEvent(EventType.RENDER);
        return GameEvent.renderGameEvent;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
