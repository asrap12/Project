/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * A game entity that is a catch-all for any location not within the bounds of the game space.
 */

package FinalProject;

class Wall extends Entity {
    public Wall(Game game) {
        super(game);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WALL;
    }

    @Override
    public boolean isEdible() {
        return false;
    }
}
