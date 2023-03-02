/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * An abstract superclass representing all of the entities that exist within a game.
 */

package FinalProject;

abstract class Entity {
    // All game entities need access to the game instance to query and manipulate the game space.
    protected final Game game;

    protected Entity(Game game) {
        this.game = game;
    }

    public abstract EntityType getEntityType();

    public abstract boolean isEdible();
}
