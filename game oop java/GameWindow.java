/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/19/2021
 *
 * Sets up all the basic elements of a game window save for the logic. Derived classes should handle keyboard input,
 * window events, and call this class' render method to drive the game.
 */

package FinalProject;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.function.*;
import javax.imageio.*;
import javax.swing.*;

public abstract class GameWindow extends JDialog {
    private static final int FRAMES_PER_SECOND = 8;
    private static final int FRAME_INTERVAL = 1000 / GameWindow.FRAMES_PER_SECOND;
    private static final Color TRANSLUCENT_BLACK = new Color(2130706432, true);

    protected final Game game; // The game state manager
    private final Timer timer; // Provides a "clock signal" to drive the update and rendering at regular intervals
    private final EntityType[][] tileBuffer; // An array of game entities by position on the screen
    private String message; // The current message to overlay and center on the screen

    public GameWindow(Game game, JFrame owner) {
        super(owner);

        // Create the graphics component.
        Size gameSize = game.getSize();
        this.tileBuffer = new EntityType[gameSize.height][gameSize.width];
        BiConsumer<Graphics2D, Rectangle> paintComponent = this::paintComponent; // Needed to access instance method
        var graphicsPanel = new JPanel() { // Create an anonymous class instance extending JPanel and overriding paint.
            @Override
            public void paint(Graphics g) {
                paintComponent.accept((Graphics2D)g, this.getBounds()); // Accesses the instance method of outer class
            }
        };
        graphicsPanel.setFocusable(true);
        graphicsPanel.setFocusTraversalKeysEnabled(false);
        graphicsPanel.addKeyListener(new KeyListenerProxy(this::handleKeyTyped, // Enables keyboard input
                                                          this::handleKeyPressed,
                                                          this::handleKeyReleased));
        this.add(graphicsPanel);

        // Set up the rest of the user interface and assign the remaining field variables.
        Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        screenSize.y += (screenSize.height = screenSize.height * 3 / 4);
        graphicsPanel.setPreferredSize(GameWindow.fitDimension(screenSize.getSize(), gameSize.toDimension()));
        this.pack();
        this.setMinimumSize(new Dimension(this.getSize()));
        this.setModal(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("%s (%s)".formatted(Application.NAME, gameSize));
        this.addWindowListener(new WindowListenerProxy(this::handleWindowOpened, // Enable window events
                                                       this::handleWindowClosing,
                                                       this::handleWindowClosed,
                                                       this::handleWindowIconified,
                                                       this::handleWindowDeiconified,
                                                       this::handleWindowActivate,
                                                       this::handleWindowDeactivated));
        this.timer = new Timer(GameWindow.FRAME_INTERVAL, e -> this.render());
        this.game = game;
    }

    // Centers a rectangle within another
    private static Rectangle centerDimension(Rectangle container, Dimension object) {
        return new Rectangle((int)Math.round(container.x + (double)(container.width - object.width) / 2),
                             (int)Math.round(container.y + (double)(container.height - object.height) / 2),
                             object.width,
                             object.height);
    }

    // Computes the biggest dimension that will fill another dimension while maintaining the object's aspect ratio
    private static Dimension fitDimension(Dimension container, Dimension object) {
        assert object.width <= container.width && object.height <= container.height;
        if (container.getWidth() / container.getHeight() > object.getWidth() / object.getHeight())
            return new Dimension((int)Math.round(container.getHeight() * object.getWidth() / object.getHeight()),
                                 (int)Math.round(container.getHeight()));
        else
            return new Dimension((int)Math.round(container.getWidth()),
                                 (int)Math.round(container.getWidth() * object.getHeight() / object.getWidth()));
    }

    /* This functionality is largely unnecessary for the simple squares which we are drawing.
    // Takes a graphics object's scaling transformation and undoes it; then scales the bounds to pixel-perfect size
    private static void makePixelPerfect(Graphics2D g, Rectangle bounds) {
        AffineTransform transform = g.getTransform();
        double scaleX = transform.getScaleX(), scaleY = transform.getScaleY(); // X and Y scales ought to be the same.

        // Reset the transform scale to 1 in both dimensions.
        transform.setToScale(1, 1);
        g.setTransform(transform);

        // Scale the bounds' width and height by the same amount we unscaled the transform to get the true pixel count.
        bounds.width *= scaleX;
        bounds.height *= scaleY;
    }*/

    protected void startGame() {
        assert this.game.getState() == GameState.INITIAL && !this.timer.isRunning();
        this.message = null;
        this.game.start();
        this.timer.start();
    }

    protected void pauseGame() {
        assert this.game.getState() == GameState.RUNNING && this.timer.isRunning();
        this.timer.stop();
        this.message = "Press space to resume.";
        this.repaint();
    }

    protected void resumeGame() {
        assert this.game.getState() == GameState.RUNNING && !this.timer.isRunning();
        this.message = null;
        this.timer.start();
        this.repaint();
    }

    protected void stopRendering() {
        assert this.game.getState() == GameState.STOPPED && this.timer.isRunning();
        this.message = "Score: %d".formatted(this.game.getApplesConsumed());
        this.timer.setRepeats(false);
    }

    // Update the game state and render the new state
    protected void render() {
        this.game.render(this.tileBuffer);
        this.repaint();
    }

    protected void takeScreenshot() {
        // Paint a the rendering onto our own buffer instead of the Swing component.
        Dimension imageSize = GameWindow.fitDimension(this.getBounds().getSize(), this.game.getSize().toDimension());
        BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
        this.paintGameArea(image.createGraphics(), new Rectangle(imageSize));

        // Try to save it into a file in the screenshots directory.
        String fileName = Application.SCREENSHOT_FILE_NAME_FORMAT.formatted(LocalDateTime.now());
        try (FileOutputStream file = Application.createFile(Path.of(Application.SCREENSHOTS_PATH, fileName))) {
            ImageIO.write(image, Application.SCREENSHOT_IMAGE_FORMAT, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void paintComponent(Graphics2D g, Rectangle bounds) {
        // Paint the background.
        g.setColor(Color.black);
        g.fill(bounds); // Technically, we should get the bounds of the JPanel, but it is the same region.

        /*
        // We want to handle drawing our own pixels instead of automatically transforming our coordinates in high-DPI.
        GameWindow.makePixelPerfect(g, bounds);*/

        // Get the paintable region of this window and fit the game into it such that all the tiles are square.
        Rectangle gameAreaBounds = GameWindow.centerDimension(
            bounds,
            GameWindow.fitDimension(
                bounds.getSize(),
                this.game.getSize().toDimension()
            )
        );

        // Paint the game area, leaving the letterbox areas black.
        this.paintGameArea(g, gameAreaBounds);
    }

    private void paintGameArea(Graphics2D g, Rectangle bounds){
        // Paint the background.
        g.setColor(Color.darkGray);
        g.fill(bounds);

        // Calculate all the row and column boundaries.
        int[] rowBoundaries = new int[this.game.getSize().height + 1];
        for (int row = 0; row < rowBoundaries.length; row++)
            rowBoundaries[row] = bounds.y + row * bounds.height / this.game.getSize().height;
        int[] columnBoundaries = new int[this.game.getSize().width + 1];
        for (int column = 0; column < columnBoundaries.length; column++)
            columnBoundaries[column] = bounds.x + column * bounds.width / this.game.getSize().width;

        // Paint the game entities tile by tile.
        for (int row = 0; row < this.tileBuffer.length; row++)
            for (int column = 0; column < this.tileBuffer[row].length; column++)
                // Check if there is anything to paint at this location.
                if (this.tileBuffer[row][column] != null) {
                    switch (this.tileBuffer[row][column]) { // TODO: Replace with Java 14's enhanced switch statement.
                        case SNAKE:
                            g.setColor(Color.green); // Snakes are green.
                            break;
                        case APPLE:
                            g.setColor(Color.red); // Apples are red.
                            break;
                    }

                    // Paint the tile.
                    g.fillRect(columnBoundaries[column],
                               rowBoundaries[row],
                               columnBoundaries[column + 1] - columnBoundaries[column],
                               rowBoundaries[row + 1] - rowBoundaries[row]);
                }

        if (this.message != null) {
            Font font = new Font(Font.MONOSPACED, Font.BOLD, bounds.width / 20);
            FontMetrics fontMetrics = g.getFontMetrics(font);
            g.setFont(font);
            g.setColor(Color.white);
            g.drawString(message,
                         bounds.x + (bounds.width - fontMetrics.stringWidth(message)) / 2,
                         bounds.y + fontMetrics.getAscent() + ((bounds.height - fontMetrics.getHeight()) / 2));
        }

        // Paint faux scan lines. That gives it a look and feel reminiscent of a video game from the late 1900s.
        g.setColor(GameWindow.TRANSLUCENT_BLACK);
        for (int row = bounds.y; row < bounds.y + bounds.height; row += 2)
            g.drawLine(bounds.x, row, bounds.x + bounds.width, row);
    }

    protected void handleWindowOpened(WindowEvent e) {
        this.game.initialize();
        this.setLocationRelativeTo(this.getOwner());
        this.message = "Press space to start.";
        this.render(); // Render the initial graphics.
    }

    protected void handleWindowClosing(WindowEvent e) {
        this.timer.stop();
    }

    // The event handlers below do nothing by default. Override in a derived class to do something when triggered.
    protected void handleKeyTyped(KeyEvent e) {}
    protected void handleKeyPressed(KeyEvent e) {}
    protected void handleKeyReleased(KeyEvent e) {}
    protected void handleWindowClosed(WindowEvent e) {}
    protected void handleWindowIconified(WindowEvent e) {}
    protected void handleWindowDeiconified(WindowEvent e) {}
    protected void handleWindowActivate(WindowEvent e) {}
    protected void handleWindowDeactivated(WindowEvent e) {}

    protected boolean isInitial() {
        return this.game.getState() == GameState.INITIAL && !this.timer.isRunning();
    }

    protected boolean isPaused() {
        return this.game.getState() == GameState.RUNNING && !this.timer.isRunning(); // Game's running, but timer isn't
    }

    protected boolean isRunning() {
        return this.game.getState() == GameState.RUNNING && this.timer.isRunning(); // Game and timer are both running
    }

    protected boolean isStopped() {
        return this.game.getState() == GameState.STOPPED && !this.timer.isRunning();
    }

    protected String getMessage() {
        return this.message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }
}
