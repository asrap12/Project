/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * This class represents an apple--the favorite food of snakes.
 *
 * Notes: This class is immutable.
 */

package FinalProject;

class Apple extends Entity {
    private final Location location;

    public Apple(Game game, Location location) {
        super(game);
        this.location = location;
    }

    public void removeFromGame() {
        if (this != this.game.getTileOwner(this.location))
            throw new IllegalStateException();
        this.game.releaseTile(location);
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.APPLE;
    }

    @Override
    public boolean isEdible() {
        return true;
    }
}
