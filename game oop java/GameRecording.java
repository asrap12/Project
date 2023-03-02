/*
 * CST 3513 Final Project: Snake Game
 * Group 1 (Asra Pervaiz, Kevin Li, Naheed Reyhad, Martin Liang) @6/18/2021
 *
 * Stores a record of the game events for later replay.
 */

package FinalProject;

import java.io.*;
import java.util.*;

public class GameRecording implements Serializable {
    private final List<Location> appleSpawnLocations; // A list of apple spawn locations to override random locations
    private final List<GameEvent> events; // All of the events
    private InitializeGameEvent initializationInformation; // Needed to start a replay with the same exact state
    private int finalScore;
    private boolean started;
    private boolean finalized; // A flag that indicate whether the recording is completed

    public GameRecording() {
        this.appleSpawnLocations = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    // Loads a recording from a stream (usually a FileInputStream)
    public static GameRecording load(InputStream inputStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream deserializer = new ObjectInputStream(inputStream)) {
            GameRecording recording = (GameRecording)deserializer.readObject();
            if (!recording.finalized)
                throw new InvalidObjectException("Data not finalized");
            return recording;
        }
    }

    // Saves this recording to a stream (usually a FileOutputStream)
    public void save(OutputStream outputStream) throws IOException {
        if (!this.started || !this.finalized)
            throw new IllegalStateException(); // Cannot save a recording before it's been finalized.
        try (ObjectOutputStream serializer = new ObjectOutputStream(outputStream)) {
            serializer.writeObject(this);
        }
    }

    // Initializes the recording
    public void start() {
        if (this.started || this.finalized)
            throw new IllegalStateException(); // Cannot start again after starting or after finalizing
        this.started = true;
    }

    // Adds an event to the recording
    public void addEvent(GameEvent e) {
        if (!this.started || this.finalized)
            throw new IllegalStateException(); // Cannot add events before starting or after stopping.
        switch (e.type) { // Handle by type of event--some types have special handling.
            case INITIALIZE:
                this.initializationInformation = (InitializeGameEvent)e;
                break;
            case SPAWN_APPLE:
                this.appleSpawnLocations.add(((SpawnAppleGameEvent) e).getLocation()); // Save location to special list
                break;
            case STOP:
                this.finalScore = ((StopGameEvent)e).getScore();
                break;
            case START: // The start event can be ignored.
                return;
        }
        this.events.add(e);
    }

    // Finalizes the recording
    public void stop() {
        if (!this.started || this.finalized)
            throw new IllegalStateException(); // Cannot stop before starting or stop again after stopping.
        this.finalized = true;
    }

    public boolean isFinalized() {
        return this.finalized;
    }

    public boolean isStarted() {
        return this.started;
    }

    List<Location> getAppleSpawnLocations() {
        if (!this.finalized)
            throw new IllegalStateException(); // The recorded locations can only be access once finalized.
        return Collections.unmodifiableList(this.appleSpawnLocations);
    }

    public List<GameEvent> getEvents() {
        if (!this.finalized)
            throw new IllegalStateException(); // The recorded events can only be access once finalized.
        return Collections.unmodifiableList(this.events);
    }

    public int getFinalScore() {
        if (!this.finalized)
            throw new IllegalStateException(); // The final score can only be access once finalized--obviously.
        return this.finalScore;
    }

    public InitializeGameEvent getInitializationInformation() {
        if (!this.finalized)
            throw new IllegalStateException(); // The recorded events can only be access once finalized.
        return this.initializationInformation;
    }
}
