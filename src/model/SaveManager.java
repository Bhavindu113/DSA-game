package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * SaveManager - Handles game progress persistence and cross-session continuation.
 * Allows players to stop playing and resume later from their exact reached level/round,
 * score, shield, ammo, and life reserves.
 */
public class SaveManager {
    private static final String SAVE_FILE_NAME = "galaxy_savedata.properties";
    private static final File SAVE_FILE = new File(SAVE_FILE_NAME);

    public static class SaveData {
        public final int round;
        public final int score;
        public final int shield;
        public final int ammo;
        public final int lives;
        public final int highScore;
        public final String timestamp;
        public final String dateFormatted;

        public SaveData(int round, int score, int shield, int ammo, int lives, int highScore, String timestamp, String dateFormatted) {
            this.round = round;
            this.score = score;
            this.shield = shield;
            this.ammo = ammo;
            this.lives = lives;
            this.highScore = highScore;
            this.timestamp = timestamp;
            this.dateFormatted = dateFormatted;
        }

        public String getSummary() {
            return "Round " + round + " • Score: " + String.format("%,d", score) + " • Shield: " + shield + "% • Lives: x" + lives;
        }
    }

    public static boolean hasSave() {
        if (!SAVE_FILE.exists()) return false;
        SaveData data = load();
        return data != null && data.round >= 1;
    }

    public static synchronized void save(int round, int score, int shield, int ammo, int lives) {
        try {
            int currentHigh = getHighScore();
            if (score > currentHigh) {
                currentHigh = score;
            }

            Properties props = new Properties();
            props.setProperty("round", String.valueOf(round));
            props.setProperty("score", String.valueOf(score));
            props.setProperty("shield", String.valueOf(shield));
            props.setProperty("ammo", String.valueOf(ammo));
            props.setProperty("lives", String.valueOf(lives));
            props.setProperty("highScore", String.valueOf(currentHigh));
            props.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            props.setProperty("dateFormatted", sdf.format(new Date()));

            try (FileOutputStream fos = new FileOutputStream(SAVE_FILE)) {
                props.store(fos, "Galaxy Defender Saved Progress");
            }
        } catch (Exception e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    public static synchronized SaveData load() {
        if (!SAVE_FILE.exists()) return null;
        try (FileInputStream fis = new FileInputStream(SAVE_FILE)) {
            Properties props = new Properties();
            props.load(fis);

            int round = Integer.parseInt(props.getProperty("round", "1"));
            int score = Integer.parseInt(props.getProperty("score", "0"));
            int shield = Integer.parseInt(props.getProperty("shield", "100"));
            int ammo = Integer.parseInt(props.getProperty("ammo", "100"));
            int lives = Integer.parseInt(props.getProperty("lives", "3"));
            int highScore = Integer.parseInt(props.getProperty("highScore", "0"));
            String timestamp = props.getProperty("timestamp", "0");
            String dateFormatted = props.getProperty("dateFormatted", "Recent Mission");

            return new SaveData(round, score, shield, ammo, lives, highScore, timestamp, dateFormatted);
        } catch (Exception e) {
            System.err.println("Failed to load game save: " + e.getMessage());
            return null;
        }
    }

    public static synchronized int getHighScore() {
        if (!SAVE_FILE.exists()) return 0;
        try (FileInputStream fis = new FileInputStream(SAVE_FILE)) {
            Properties props = new Properties();
            props.load(fis);
            return Integer.parseInt(props.getProperty("highScore", "0"));
        } catch (Exception e) {
            return 0;
        }
    }

    public static synchronized void updateHighScore(int score) {
        if (score <= 0) return;
        int curHigh = getHighScore();
        if (score > curHigh) {
            try {
                Properties props = new Properties();
                if (SAVE_FILE.exists()) {
                    try (FileInputStream fis = new FileInputStream(SAVE_FILE)) {
                        props.load(fis);
                    }
                }
                props.setProperty("highScore", String.valueOf(score));
                try (FileOutputStream fos = new FileOutputStream(SAVE_FILE)) {
                    props.store(fos, "Galaxy Defender High Score Update");
                }
            } catch (Exception ignored) {}
        }
    }

    public static synchronized void deleteSave() {
        if (SAVE_FILE.exists()) {
            SAVE_FILE.delete();
        }
    }
}
