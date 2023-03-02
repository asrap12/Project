/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/17/2021
 *
 * This is the main entry point of the application (hence the name). It is responsible for composing other classes
 * necessary to make the game work.
 *
 * Notes: This class is not intended for public consumption. Thus, only the entry method (main) is exposed.
 */

package FinalProject;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.time.*;

public final class Application extends JFrame {
    static final String NAME = "Snake";
    static final String FILES_PATH = Path.of(System.getProperty("user.home"), ".snake") // Application folder
                                         .toAbsolutePath()
                                         .toString();
    static final String RECORDINGS_PATH = Path.of(FILES_PATH, "recordings").toString(); // Recorded games folder
    static final String RECORDING_FILE_NAME_EXTENSION = "sr";
    static final String RECORDING_FILE_NAME_FORMAT = "%tY-%<tm-%<td %<tH-%<tM-%<tS.%<tL" // Format string for recordings
                                                   + '.'
                                                   + Application.RECORDING_FILE_NAME_EXTENSION;
    static final String SCREENSHOTS_PATH = Path.of(FILES_PATH, "screenshots").toString(); // Recorded games folder
    static final String SCREENSHOT_IMAGE_FORMAT = "png";
    static final String SCREENSHOT_FILE_NAME_FORMAT = "%tY-%<tm-%<td %<tH-%<tM-%<tS.%<tL.png";

    private final JList<String> recordedGamesList;
    private final JTable highScoreTable;
    private final JFormattedTextField widthTextField, heightTextField;
    private final JButton browseRecordedGameButton, replayRecordedGameButton, startGameButton;

    // Only the entry method can instantiate this class.
    private Application() {
        super(Application.NAME);

        // Initialize resources.
        // TODO: !!

        GridBagConstraints gbc; // Reused throughout this constructor to reference new grid bag constraint objects

        // Set up the root components of the user interface.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setPreferredSize(new Dimension(360, 360));
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setTabPlacement(2);
        mainPanel.add(tabPane, BorderLayout.CENTER);

        // Set up the new game tab.
        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new GridBagLayout());
        tabPane.addTab("New Game", newGamePanel);
        JPanel newGameInnerPanel = new JPanel();
        newGameInnerPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        newGamePanel.add(newGameInnerPanel, gbc);
        JLabel widthLabel = new JLabel();
        widthLabel.setHorizontalAlignment(2);
        widthLabel.setText("Width:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        newGameInnerPanel.add(widthLabel, gbc);
        this.widthTextField = new JFormattedTextField();
        this.widthTextField.setFormatterFactory( // Restricts the field to only integers.
            new DefaultFormatterFactory(
                new NumberFormatter(
                    NumberFormat.getIntegerInstance()
                )
            )
        );
        this.widthTextField.setHorizontalAlignment(2);
        this.widthTextField.setText("20"); // TODO: Set the value here based on the application settings.
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        newGameInnerPanel.add(widthTextField, gbc);
        JLabel heightLabel = new JLabel();
        heightLabel.setHorizontalAlignment(2);
        heightLabel.setText("Height:");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        newGameInnerPanel.add(heightLabel, gbc);
        this.heightTextField = new JFormattedTextField();
        this.heightTextField.setFormatterFactory(
            new DefaultFormatterFactory(
                new NumberFormatter(
                    NumberFormat.getIntegerInstance()
                )
            )
        );
        this.heightTextField.setHorizontalAlignment(2);
        this.heightTextField.setText("20"); // TODO: Set the value here based on the application settings.
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        newGameInnerPanel.add(heightTextField, gbc);
        JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        newGameInnerPanel.add(spacer1, gbc);
        JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        newGameInnerPanel.add(spacer2, gbc);
        JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        newGameInnerPanel.add(spacer3, gbc);
        JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        newGameInnerPanel.add(spacer4, gbc);
        this.startGameButton = new JButton();
        this.startGameButton.setText("Start New Game");
        this.startGameButton.addActionListener(e -> this.openNewGame());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 7;
        gbc.anchor = GridBagConstraints.EAST;
        newGameInnerPanel.add(startGameButton, gbc);
        JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        newGamePanel.add(spacer5, gbc);
        JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        newGamePanel.add(spacer6, gbc);

        // Set up the recorded games tab.
        JPanel recordedGamesPanel = new JPanel();
        recordedGamesPanel.setLayout(new GridBagLayout());
        tabPane.addTab("Recorded Games", recordedGamesPanel);
        this.recordedGamesList = new JList<>();
        this.recordedGamesList.setSelectionMode(0);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        try { // Attempt to populate the list with files names from the recordings directory.
            Application.createDirectory(Application.RECORDINGS_PATH); // Ensure that the recordings directory exists.
            Files.list(Path.of(Application.RECORDINGS_PATH))
                 .map(Path::toFile)
                 .filter(File::isFile)
                 .map(File::getName)
                 .forEach(listModel::addElement);
        } catch (IOException ex) {
            this.recordedGamesList.setEnabled(false); // Since we cannot list the contents, disable the list component.
            ex.printStackTrace();
        }
        this.recordedGamesList.setModel(listModel);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        recordedGamesPanel.add(recordedGamesList, gbc);
        this.browseRecordedGameButton = new JButton();
        this.browseRecordedGameButton.setText("Browse…");
        this.browseRecordedGameButton.addActionListener(e -> this.replayRecordedGame());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        recordedGamesPanel.add(this.browseRecordedGameButton, gbc);
        this.replayRecordedGameButton = new JButton();
        this.replayRecordedGameButton.setText("Replay Game");
        this.replayRecordedGameButton.addActionListener(e -> {
            String fileName = this.recordedGamesList.getSelectedValue();
            this.replayRecordedGame(fileName);
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        recordedGamesPanel.add(this.replayRecordedGameButton, gbc);

        // Set up the high scores panel.
        JPanel highScoresPanel = new JPanel();
        highScoresPanel.setLayout(new BorderLayout(0, 0));
        //tabPane.addTab("High Scores", highScoresPanel); // TODO: Uncomment this when ready; no logic to add high score
        JScrollPane scrollPane = new JScrollPane();
        highScoresPanel.add(scrollPane, BorderLayout.CENTER);
        this.highScoreTable = new JTable();
        this.highScoreTable.setAutoResizeMode(3);
        this.highScoreTable.setRowSelectionAllowed(false);
        scrollPane.setViewportView(highScoreTable);

        // Wrap up the user interface; add everything into the frame and fix its size.
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setMinimumSize(this.getSize()); // Not really necessary since we disabled resizing
        this.setLocationRelativeTo(null);
    }

    // Create a directory if it does not exist.
    static void createDirectory(String path) {
        File directory = new File(path);
        directory.mkdirs();
    }

    static FileOutputStream createFile(Path path) throws IOException {
        return Application.createFile(new File(path.toString()));
    }

    // Create a file if it does not exist.
    static FileOutputStream createFile(File file) throws IOException {
        file = file.getCanonicalFile();
        File containingDirectory = file.getParentFile();
        if (containingDirectory != null)
            containingDirectory.mkdirs();
        return new FileOutputStream(file);
    }

    public static void main(String[] args) {
        // Show the main application window.
        Application application = new Application();
        application.setVisible(true);
    }

    private void openNewGame() {
        int gameWidth = Byte.parseByte(this.widthTextField.getText());
        int gameHeight = Byte.parseByte(this.heightTextField.getText());
        if (gameWidth < 9 || gameHeight < 9) { // Prevent the game size from being too small.
            JOptionPane.showMessageDialog(this,
                                          "%d \u00d7 %d is too small for a game.".formatted(gameWidth, gameHeight),
                                          "Invalid Input",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gameWidth > 99 || gameHeight > 99) { // Prevent the game size from being too big.
            JOptionPane.showMessageDialog(this,
                                          "%d \u00d7 %d is too big for a game.".formatted(gameWidth, gameHeight),
                                          "Invalid Input",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set up the game, record, and show the game window.
        GameRecording recording = new GameRecording();
        Game game = new Game(gameWidth, gameHeight);
        game.addEventListener(recording::addEvent);
        recording.start();
        PlayableGameWindow gameWindow = new PlayableGameWindow(game, this);
        gameWindow.setVisible(true);
        recording.stop();

        // Check for a new high score and add it to the list if it beats the previous high score.
        int score = recording.getFinalScore(); // TODO: maintain high scores list

        // Save the game recording to the application's directory.
        if (recording.isFinalized()) {
            String fileName = Application.RECORDING_FILE_NAME_FORMAT.formatted(LocalDateTime.now());
            Path savePath = Path.of(Application.RECORDINGS_PATH, fileName);
            try (FileOutputStream fileOutputStream = Application.createFile(savePath)) {
                // Save the recording.
                recording.save(fileOutputStream);

                // If the save operation did not throw an exception, also add it to the list of recordings.
                ((DefaultListModel<String>)this.recordedGamesList.getModel()).addElement(fileName);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                                              "Could not save the recording to " + savePath,
                                              "I/O Error",
                                              JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Lets the user choose a recording to load and replay
    private void replayRecordedGame() {
        // Get the user's file selection using a file selection dialog.
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Snake Game Recordings",
                Application.RECORDING_FILE_NAME_EXTENSION));

        // Replay the selected file.
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            this.replayRecordedGame(fileChooser.getSelectedFile());
    }

    // Plays a recording of the supplied file name from the application's recording's folder
    private void replayRecordedGame(String fileName) {
        this.replayRecordedGame(Path.of(Application.RECORDINGS_PATH, fileName).toFile());
    }

    private void replayRecordedGame(File file) {
        // Try to open the file selected by the user.
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Show the game window to replay the recording.
            GameRecording recording = GameRecording.load(fileInputStream);

            // TODO: Remove after debugging. This prints the events in the game recording as text to the error stream.
            for (GameEvent event: recording.getEvents())
                System.err.println(event.toString());

            GameReplayWindow gameWindow = new GameReplayWindow(recording, this);
            gameWindow.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not open the file " + file,
                                          "I/O Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Could not load the data from " + file,
                                          "File Format Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
