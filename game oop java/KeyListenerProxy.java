/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/19/2021
 *
 * This class exists to reduce code clutter and to hide event handling methods from public access. Classes interested in
 * handling just one kind of keyboard event can supply a lambda method to this class' constructor instead of
 * implementing the entire KeyListener interface.
 *
 * Note: This class is immutable. Event handlers cannot be added nor removed once instantiated.
 */

package FinalProject;

import java.awt.event.*;
import java.util.function.*;

public final class KeyListenerProxy implements KeyListener {
    private final Consumer<KeyEvent> keyTypedHandler;
    private final Consumer<KeyEvent> keyPressedHandler;
    private final Consumer<KeyEvent> keyReleasedHandler;

    public KeyListenerProxy(Consumer<KeyEvent> keyTypedHandler,
                            Consumer<KeyEvent> keyPressedHandler,
                            Consumer<KeyEvent> keyReleasedHandler) {
        this.keyTypedHandler = keyTypedHandler;
        this.keyPressedHandler = keyPressedHandler;
        this.keyReleasedHandler = keyReleasedHandler;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (this.keyTypedHandler != null)
            this.keyTypedHandler.accept(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (this.keyPressedHandler != null)
            this.keyPressedHandler.accept(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.keyReleasedHandler != null)
            this.keyReleasedHandler.accept(e);
    }
}
