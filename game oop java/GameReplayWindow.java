/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/19/2021
 *
 * This class is responsible for replaying recorded games.
 */

package FinalProject;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public final class GameReplayWindow extends GameWindow {
    private final Queue<GameEvent> events;

    public GameReplayWindow(GameRecording gameRecording, JFrame owner) {
        super(new Game(gameRecording.getInitializationInformation().getSize(),
                       gameRecording.getInitializationInformation().getSnakeSegments(),
                       gameRecording.getInitializationInformation().getAppleLocation(),
                       gameRecording.getAppleSpawnLocations()),
              owner);

        // Initialize the fields with the arguments and create the user interface.
        this.events = new LinkedList<>(gameRecording.getEvents());
    }

    // Update the game state and render the new state
    @Override
    protected void render() {
        // Feed the recorded events into the game until the next render event. Only movement/direction events supported.
        GameEvent eventToReplay = this.events.poll();
        while (eventToReplay != null && eventToReplay.type != EventType.RENDER) {
            if (eventToReplay.type == EventType.MOVE)
                this.game.changeDirection(((ChangeDirectionEvent)eventToReplay).getDirection());
            eventToReplay = this.events.poll();
        }

        super.render();

        if (this.game.getState() == GameState.STOPPED || this.events.isEmpty())
            this.stopRendering();
    }

    // Handles key input
    @Override
    protected void handleKeyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_SPACE) {
            if (this.isInitial())
                this.startGame();
            else if (this.isRunning() && !this.isPaused())
                this.pauseGame();
            else if (!this.isRunning() && this.isPaused())
                this.resumeGame();
        }
    }
}
