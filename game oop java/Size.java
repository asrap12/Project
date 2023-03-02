/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * Represents the size of a game.
 *
 * Note: This class is immutable and does not reference any mutable types.
 */

package FinalProject;

import java.awt.*;
import java.io.*;

public class Size implements Serializable {
    public final int width, height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension toDimension() {
        return new Dimension(this.width, this.height);
    }

    @Override
    public boolean equals(Object other) {
        return this.equals((Size)other);
    }

    public boolean equals(Size other) {
        return this.width == other.width && this.height == other.height;
    }

    @Override
    public String toString() {
        return "%d \u00d7 %d".formatted(this.width, this.height);
    }

    public Location getCenter() {
        return new Location((this.height - 1) / 2, (this.width - 1) / 2);
    }
}
