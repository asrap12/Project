/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * This enumeration represents the type of event in the game.
 */

package FinalProject;

public enum EventType {
    INITIALIZE,
    START, // When the game starts
    MOVE, // When a player changes direction
    SPAWN_APPLE, // When a new apple is spawned
    RENDER, // After a frame is updated and rendered
    STOP // When the game stops
}
