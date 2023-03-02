/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/19/2021
 *
 * This is a subclass of the GameWindow. It translate user input into in-game actions. If the window loses focus, the
 * game is paused automatically.
 */

package FinalProject;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public final class PlayableGameWindow extends GameWindow {
    private static final int MAX_DIRECTION_CHANGES_QUEUED = 3;
    private static final Map<Integer, Direction> keyActionMap = Map.of(KeyEvent.VK_W, Direction.UP,
                                                                       KeyEvent.VK_UP, Direction.UP,
                                                                       KeyEvent.VK_S, Direction.DOWN,
                                                                       KeyEvent.VK_DOWN, Direction.DOWN,
                                                                       KeyEvent.VK_A, Direction.LEFT,
                                                                       KeyEvent.VK_LEFT, Direction.LEFT,
                                                                       KeyEvent.VK_D, Direction.RIGHT,
                                                                       KeyEvent.VK_RIGHT, Direction.RIGHT);

    private final Deque<Direction> directionChangeQueue; // Queues a limited number of consecutive direction changes.
    private boolean manualPaused;

    public PlayableGameWindow(Game game, JFrame owner) {
        super(game, owner);
        this.directionChangeQueue = new ArrayDeque<>(MAX_DIRECTION_CHANGES_QUEUED);
    }

    private void autoPauseGame() {
        if (this.isRunning()) {
            this.directionChangeQueue.clear(); // Pausing should clear pending direction changes.
            this.pauseGame();
            this.setMessage(null);
        }
    }

    private void autoResumeGame() {
        // Only unpause if not manually paused by the player.
        if (this.isPaused() && !manualPaused)
            this.resumeGame();
    }

    // Update the game state and render the new state.
    @Override
    protected void render() {
        // If there are any direction changes queued up, submit the first one. The next render will submit the next one.
        Direction direction = this.directionChangeQueue.poll();
        if (direction != null && this.game.getState() == GameState.RUNNING) // Important to check game state here!
            this.game.changeDirection(direction);

        super.render();

        if (this.game.getState() == GameState.STOPPED)
            this.stopRendering();
    }

    // Handles key input
    @Override
    protected void handleKeyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_SPACE) {
            if (this.isInitial()) {
                this.startGame();
            } else if (this.isRunning() && !this.isPaused()) { // Pause.
                this.directionChangeQueue.clear(); // Pausing should clear pending direction changes.
                this.manualPaused = true;
                this.pauseGame();
            } else if (!this.isRunning() && this.isPaused()) { // Resume.
                this.resumeGame();
            }
        } else if (keyCode == KeyEvent.VK_P) {
            this.takeScreenshot();
        } else if (this.isRunning() && PlayableGameWindow.keyActionMap.containsKey(keyCode)) {
            Direction requestedDirection = PlayableGameWindow.keyActionMap.get(keyCode);
            Direction queuedDirection = this.directionChangeQueue.peekLast();

            // If this direction is the opposite of the one just queued, clear the queue.
            if (queuedDirection == Direction.UP && requestedDirection == Direction.DOWN
                    || queuedDirection == Direction.DOWN && requestedDirection == Direction.UP
                    || queuedDirection == Direction.LEFT && requestedDirection == Direction.RIGHT
                    || queuedDirection == Direction.RIGHT && requestedDirection == Direction.LEFT)
                this.directionChangeQueue.clear();

            // Add it to the direction change queue. This provides a better feel for players executing complex moves.
            if (this.directionChangeQueue.size() < MAX_DIRECTION_CHANGES_QUEUED
             && requestedDirection != queuedDirection)
                this.directionChangeQueue.add(requestedDirection);
        }
    }

    @Override
    protected void handleWindowIconified(WindowEvent e) {
        this.autoPauseGame();
    }

    @Override
    protected void handleWindowDeactivated(WindowEvent e) {
        this.autoPauseGame();
    }

    @Override
    protected void handleWindowActivate(WindowEvent e) {
        this.autoResumeGame();
    }

    @Override
    protected void handleWindowDeiconified(WindowEvent e) {
        this.autoResumeGame();
    }
}
