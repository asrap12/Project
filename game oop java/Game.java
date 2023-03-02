/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * This class handles most of the logic of the game mechanics.
 *
 * Note: This class contains a lot of mutable state and is most definitely not thread-safe.
 */

package FinalProject;

import java.util.*;
import java.util.function.*;

// Acts as the go-between for all of the game's entities
// Keeps track of the game's running state and which entity is present at all locations in the game space.
public class Game {
    static final int NEW_SNAKE_LENGTH = 3;
    static final int MINIMUM_SNAKE_LENGTH = 2;
    public static final int MINIMUM_WIDTH = NEW_SNAKE_LENGTH * 2;
    public static final int MINIMUM_HEIGHT = 2; // The game would not really be usable, but this is technically doable.

    // The "character" to be controlled by the player of the game
    private final Snake player;

    // A predefined ordered collection of spawn locations of apples; if empty, random locations will be generated
    private final Queue<Location> applesSpawnLocations;

    // Stores the status of all the tiles--whether they are free (null) or owned by an entity
    private final Entity[][] tileOwners;

    // The wall surrounding the space--basically anywhere not on the tiles
    private final Wall wall;

    // A list of event listeners to notify of happenings within the game--for external subscribers
    private final Collection<Consumer<GameEvent>> eventListeners;

    // The game's current state--defaults to initial on creation of the game
    private GameState state;

    // Direction to change to on update--cleared after every update
    private Direction pendingDirectionChange;

    // The current apple instance
    private Apple apple;

    // A counter to track how many apples were eaten
    private int applesConsumed;

    public Game(Size size) {
        this(size, null, null, null);
    }

    public Game(int width, int height) {
        this(new Size(width, height));
    }

    public Game(Size size,
                List<Location> snakeSegments,
                Location appleLocation,
                List<Location> applesSpawnLocations) {
        if (size.width < Game.MINIMUM_WIDTH || size.height < Game.MINIMUM_HEIGHT)
            throw new IllegalArgumentException();
        this.eventListeners = new HashSet<>();
        this.applesSpawnLocations = new LinkedList<>(applesSpawnLocations == null
                                                   ? Collections.emptyList()
                                                   : applesSpawnLocations);

        // Create the game entities.
        this.wall = new Wall(this);
        this.tileOwners = new Entity[size.height][size.width];
        Snake newSnake = this.createSnake(snakeSegments); // Must be crated and added before the apple!
        for (Location location : newSnake.getSegments())
            if (this.tryOwnTile(location, newSnake) != null) // Try taking ownership of the tiles which the snake is on.
                throw new IllegalArgumentException();
        this.player = newSnake;
        this.spawnApple(this.createApple(appleLocation)); // Must be created after the snake is added!
    }

    public void initialize() {
        // Set the game's state to "initial."
        this.state = GameState.INITIAL;

        // Collect all the initialization information needed to start the game again in the same exact state.
        InitializeGameEvent initializationInformation = new InitializeGameEvent(this.getSize(),
                                                                                this.player.getSegments(),
                                                                                this.apple.getLocation());

        // Notify all the event listeners of the initialize event.
        this.dispatchEvent(initializationInformation);
    }

    // Sets the game's state to "running." This is not supposed to be called after the game stops.
    public void start() {
        if (this.state != GameState.INITIAL)
            throw new IllegalStateException();
        this.state = GameState.RUNNING;

        // Notify all the event listeners of the start event.
        this.dispatchEvent(GameEvent.getStartEvent());
    }

    // Changes the direction of the snake, but does not take effect until render. So it's possible to "cancel" a change.
    public void changeDirection(Direction direction) {
        if (this.state != GameState.RUNNING)
            throw new IllegalStateException();
        this.pendingDirectionChange = direction;
    }

    // Immediately causes the game to transition to a "stop" state.
    public void stop() {
        if (this.state != GameState.RUNNING)
            throw new IllegalStateException();
        this.state = GameState.STOPPED;

        // Notify all the event listeners of the stop event.
        this.dispatchEvent(new StopGameEvent(this.applesConsumed));
    }

    // Renders the complete state of the game to a buffer of entity types. Each one represents a type of game entity at
    // a particular location in the game space. A null indicates that the space is empty.
    public void render(EntityType[][] buffer) {
        // Update the game's state if running.
        if (this.state == GameState.RUNNING) {
            if (this.pendingDirectionChange != null) {
                // Commit the directional change.
                this.player.setDirection(this.pendingDirectionChange);

                // Notify all the event listeners of the direction change event.
                this.dispatchEvent(ChangeDirectionEvent.getInstance(this.pendingDirectionChange));
            }
            this.player.move(); // Movement and possible consumption of apples
            if (this.apple == null && this.state == GameState.RUNNING) { // Spawning of a new apple if previous eaten
                Apple newApple = this.createApple(this.applesSpawnLocations.poll());
                this.spawnApple(newApple);
                this.dispatchEvent(new SpawnAppleGameEvent(newApple.getLocation())); // Notify event listeners;
            }
        }

        // Clear the intraframe state.
        this.pendingDirectionChange = null;

        // Write the current game's state to the buffer--just enough information to render the graphics.
        for (int row = 0; row < this.tileOwners.length; row++) {
            for (int column = 0; column < this.tileOwners[row].length; column++) {
                Entity entity = this.tileOwners[row][column];
                buffer[row][column] = entity == null ? null : entity.getEntityType();
            }
        }

        // Notify all the event listeners of the render event.
        this.dispatchEvent(GameEvent.getRenderEvent());
    }

    // Adds a listener which will receive notifications from the game.
    public void addEventListener(Consumer<GameEvent> listener) {
        this.eventListeners.add(listener);
    }

    // Adds a listener which has been receiving notifications from the game.
    public void removeEventListener(Consumer<GameEvent> listener) {
        this.eventListeners.remove(listener);
    }

    // Notify all the event listeners of an event.
    private void dispatchEvent(GameEvent eventArgs) {
        for (Consumer<GameEvent> eventListener : this.eventListeners)
            eventListener.accept(eventArgs);
    }

    // Call this internal method from other methods to ensure that the game has not ended.
    private void checkForStoppedState() {
        if (this.state == GameState.STOPPED)
            throw new IllegalStateException(); // This should not happen if the game was programmed correctly.
    }

    // This creates a new snake located somewhere near the middle if the snake segments are unspecified.
    private Snake createSnake(List<Location> snakeSegments) {
        if (snakeSegments == null) { // Create the snake at the standard location if unspecified.
            LinkedList<Location> newSnakeSegments = new LinkedList<>();
            newSnakeSegments.addLast(this.getSize().getCenter()); // Starting at the center
            do newSnakeSegments.addLast(newSnakeSegments.getLast().offset(Direction.LEFT));
            while (newSnakeSegments.size() < NEW_SNAKE_LENGTH);
            snakeSegments = newSnakeSegments; // New snake is as long as the predefined length for new snakes.
        }
        return new Snake(this, snakeSegments);
    }

    private Apple createApple(Location location) {
        // Check if the Snake has already been created. This method should not be called if the Snake is not present.
        assert this.player != null;

        // If no location was provided, create the apple at a random location.
        if (location == null) {
            // Get a list of locations where the new apple can be placed.
            List<Location> freeTiles = this.getFreeTiles();

            // Create the apple at a random location.
            return new Apple(this, freeTiles.get((int)(Math.random() * freeTiles.size())));
        }
        return new Apple(this, location);
    }

    // This adds an apple to the game.
    private void spawnApple(Apple apple) {
        // There can only be one apple. And the game must be running first.
        assert this.apple == null;

        // Try to have the apple own the tile at that location.
        if (this.tryOwnTile(apple.getLocation(), apple) != null)
            throw new IllegalArgumentException(); // If the apple is being spawned at a location already owned

        // Save the reference to the new apple.
        this.apple = apple;
    }

    private Apple findApple() {
        for (Entity[] row: this.tileOwners)
            for (Entity entity: row)
                if (entity instanceof Apple)
                    return (Apple)entity;
        return null;
    }

    // This method helps with collision detection. If a tile is owned by another entity, it returns that entity.
    Entity tryOwnTile(Location location, Entity entity) {
        this.checkForStoppedState();
        if (location.row < 0 || location.row >= this.tileOwners.length
         || location.column < 0 || location.column >= this.tileOwners[location.row].length)
            return this.wall;
        Entity existingEntity = this.tileOwners[location.row][location.column];
        if (existingEntity == null)
            this.tileOwners[location.row][location.column] = entity; // Marks tile as owned by the supplied entity.
        return existingEntity;
    }

    // Releases a previously owned tile.
    void releaseTile(Location location) {
        this.checkForStoppedState();
        assert this.tileOwners[location.row][location.column] != null;
        this.tileOwners[location.row][location.column] = null; // Marks the tile as free.
    }

    void eatApple(Apple apple) {
        if (apple == null) // If the location is not given, now we have to find the tile with the apple.
            apple = this.findApple();
        apple.removeFromGame(); // The variable should not be null at this point; that indicates logic errors elsewhere.
        this.apple = null;
        this.applesConsumed++;
    }

    Entity getTileOwner(Location location) {
        if (location.row < 0 || location.row >= this.tileOwners.length
         || location.column < 0 || location.column >= this.tileOwners[location.row].length)
            return this.wall;
        return this.tileOwners[location.row][location.column];
    }

    // Get a list of currently free tiles (those that are not owned by any entity).
    List<Location> getFreeTiles() {
        ArrayList<Location> freeTiles = new ArrayList<>();
        for (int row = 0; row < this.tileOwners.length; row++)
            for (int column = 0; column < this.tileOwners[row].length; column++)
                if (this.tileOwners[row][column] == null)
                    freeTiles.add(new Location(row, column));
        return freeTiles;
    }

    public Size getSize() {
        return new Size(this.tileOwners[0].length, this.tileOwners.length);
    }

    public GameState getState() {
        return this.state;
    }

    public int getApplesConsumed() {
        return this.applesConsumed;
    }
}
