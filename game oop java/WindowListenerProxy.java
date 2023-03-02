/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/19/2021
 *
 * This class exists to reduce code clutter and to hide event handling methods from public access. Classes interested in
 * handling just one kind of window event can supply a lambda method to this class' constructor instead of implementing
 * the entire WindowListener interface.
 *
 * Note: This class is immutable. Event handlers cannot be added nor removed once instantiated.
 */

package FinalProject;

import java.awt.event.*;
import java.util.function.*;

public final class WindowListenerProxy implements WindowListener {
    private final Consumer<WindowEvent> windowOpenedHandler;
    private final Consumer<WindowEvent> windowClosingHandler;
    private final Consumer<WindowEvent> windowClosedHandler;
    private final Consumer<WindowEvent> windowIconifiedHandler;
    private final Consumer<WindowEvent> windowDeiconifiedHandler;
    private final Consumer<WindowEvent> windowActivatedHandler;
    private final Consumer<WindowEvent> windowDeactivatedHandler;

    public WindowListenerProxy(Consumer<WindowEvent> windowOpenedHandler,
                               Consumer<WindowEvent> windowClosingHandler,
                               Consumer<WindowEvent> windowClosedHandler,
                               Consumer<WindowEvent> windowIconifiedHandler,
                               Consumer<WindowEvent> windowDeiconifiedHandler,
                               Consumer<WindowEvent> windowActivatedHandler,
                               Consumer<WindowEvent> windowDeactivatedHandler) {
        this.windowOpenedHandler = windowOpenedHandler;
        this.windowClosingHandler = windowClosingHandler;
        this.windowClosedHandler = windowClosedHandler;
        this.windowIconifiedHandler = windowIconifiedHandler;
        this.windowDeiconifiedHandler = windowDeiconifiedHandler;
        this.windowActivatedHandler = windowActivatedHandler;
        this.windowDeactivatedHandler = windowDeactivatedHandler;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (this.windowOpenedHandler != null)
            this.windowOpenedHandler.accept(e);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (this.windowClosingHandler != null)
            this.windowClosingHandler.accept(e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (this.windowClosedHandler != null)
            this.windowClosedHandler.accept(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (this.windowIconifiedHandler != null)
            this.windowIconifiedHandler.accept(e);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (this.windowDeiconifiedHandler != null)
            this.windowDeiconifiedHandler.accept(e);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (this.windowActivatedHandler != null)
            this.windowActivatedHandler.accept(e);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (this.windowDeactivatedHandler != null)
            this.windowDeactivatedHandler.accept(e);
    }
}
