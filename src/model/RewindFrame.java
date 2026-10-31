package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RewindFrame - Temporal Snapshot of player starship & battlefield telemetry.
 * Pushed and popped from CustomStack (KDU IT22013 Lesson 5: Stack Data Structure).
 * 
 * @author Member B (ID: IT/IS/42/002) - Individual Contribution: 20%
 * Module: Action-Driven LIFO Undo & Temporal Damage Reversal Stack
 * 
 * Encapsulates immutable state fields (spatial coordinates, shield integrity, score, ammunition)
 * restored in LIFO order upon pressing 'U'.
 */
public class RewindFrame {
    private static long frameCounter = 1000;

    public final long frameId;
    public final double playerX;
    public final double playerY;
    public final int shield;
    public final int score;
    public final int ammo;
    public final int enemyCount;
    public final String timestamp;

    public RewindFrame(double playerX, double playerY, int shield, int score, int ammo, int enemyCount) {
        this.frameId = ++frameCounter;
        this.playerX = playerX;
        this.playerY = playerY;
        this.shield = shield;
        this.score = score;
        this.ammo = ammo;
        this.enemyCount = enemyCount;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    @Override
    public String toString() {
        return "Frame #" + frameId + " [Shield: " + shield + "% | " + timestamp + "]";
    }
}
