/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * An immutable class representing a location in the game. Location coordinates are independent of the size of the game
 * in pixels.
 *
 * Note: This class is immutable and does not reference any mutable types.
 */

package FinalProject;

import java.io.*;

public class Location implements Serializable {
    public final int row, column;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // Checks if this location is next to another (a.k.a. touching at the edges)
    public boolean isNextTo(Location location) {
        return this.row == location.row && Math.abs(this.column - location.column) == 1
            || this.column == location.column && Math.abs(this.row - location.row) == 1;
    }

    // Gives a location one position away in one of the directions.
    public Location offset(Direction direction) {
        switch (direction) { // TODO: Should be replace with Java 14's enhanced switch statement for compactness
            case UP:
                return new Location(row - 1, column);
            case DOWN:
                return new Location(row + 1, column);
            case LEFT:
                return new Location(row, column - 1);
            case RIGHT:
                return new Location(row, column + 1);
            default:
                throw new IllegalArgumentException(); // This should never be thrown but better than returning null.
        }
    }

    @Override
    public boolean equals(Object other) {
        return this.equals((Location)other);
    }

    public boolean equals(Location other) {
        return this.row == other.row && this.column == other.column;
    }

    @Override
    public String toString() {
        return "(%d, %d)".formatted(this.row, this.column);
    }
}
