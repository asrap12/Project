/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/20/2021
 *
 * Represents a game stop event.
 *
 * Note: This class is immutable and does not reference any mutable types.
 */

package FinalProject;

public class StopGameEvent extends GameEvent {
    private final int score;

    public StopGameEvent(int score) {
        super(EventType.STOP);
        this.score = score;
    }

    @Override
    public String toString() {
        return "%s(%d)".formatted(super.toString(), this.score);
    }

    public int getScore() {
        return this.score;
    }
}
