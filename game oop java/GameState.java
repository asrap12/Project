/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * An enumeration of all the possible states of a game. ("Pause" is not included because that is implemented by the user
 * interface. All that needs to be done to pause a game is to stop calling the render method.)
 */

package FinalProject;

public enum GameState {
    INITIAL,
    RUNNING,
    STOPPED
}
