/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * This class represents a snake, an in-game avatar of a player. It handles its own movement logic within the game.
 */

package FinalProject;

import java.util.*;

class Snake extends Entity {

    // This is a doubly-linked lists for keeping track of the snake's parts. The first item represents the head.
    // It's a very efficient data structure to manage the snake's body because only the head and tail need to be
    // manipulated.
    private final LinkedList<Location> segments;

    // The current direction of the snake's movement (when the snake moves).
    private Direction direction;

    public Snake(Game game, List<Location> segments) {
        super(game);

        if (segments.size() < Game.MINIMUM_SNAKE_LENGTH)
            throw new IllegalArgumentException();

        // Check that all the segments are connected.
        Location previousSegment = segments.get(0);
        for (int i = 1; i < segments.size(); i++) {
            Location currentSegment = segments.get(i);
            if (!currentSegment.isNextTo(previousSegment))
                throw new IllegalArgumentException();
            previousSegment = currentSegment;
        }

        // The first two segments determine the direction.
        if (segments.get(0).column == segments.get(1).column) // One of the vertical directions
            this.direction = segments.get(0).row < segments.get(1).row ? Direction.UP : Direction.DOWN;
        else if (segments.get(0).row == segments.get(1).row) // One of the horizontal directions
            this.direction = segments.get(0).column < segments.get(1).column ? Direction.LEFT : Direction.RIGHT;

        this.segments = new LinkedList<>(segments);
    }

    // Move the snake in the specified direction.
    void move() {
        // Find out where the snake's head currently is and get its new location based on the direction.
        Location headLocation = segments.getFirst();
        Location newHeadLocation = headLocation.offset(this.direction);

        // Try to take ownership of the tile where the head's new location is. If something is in the way, we will find
        // out what.
        Entity collidedWithEntity = this.game.tryOwnTile(newHeadLocation, this);
        if (collidedWithEntity != null) { // Uh-oh! What did we run into?
            if (collidedWithEntity.isEdible()) { // Running into food makes the snake grow.
                this.game.eatApple((Apple)collidedWithEntity);
                this.game.tryOwnTile(newHeadLocation, this);
            } else {
                this.game.stop(); // Running into anything else is a hard collision, which ends the game.
            }
        } else {
            // Remove the tail segment and release ownership of its associated tile.
            this.game.releaseTile(segments.removeLast());
        }

        this.segments.push(newHeadLocation); // Update the segments list.
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SNAKE;
    }

    public int getLength() {
        return this.segments.size();
    }

    // Warning: although the consumer cannot modify the returned list, the list is nonetheless live. It can change!
    public List<Location> getSegments() {
        return Collections.unmodifiableList(this.segments);
    }

    // Change the direction of movement. (This does not move the snake.)
    void setDirection(Direction direction) {
        this.direction = direction;
    }
}
