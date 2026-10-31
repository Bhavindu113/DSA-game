package model;

import datastructures.CustomLinkedList;
import datastructures.CustomStack;

/**
 * PlayerShip - The user-controlled flagship starship (GDS-1 Defender).
 * Directly embeds CustomStack for Action-Driven Undo / Temporal Damage Reversal (KDU IT22013 Lesson 5)
 * and CustomLinkedList for active Laser projectiles (KDU IT22013 Lesson 3).
 * 
 * @author Member B (ID: IT/IS/42/002) - Individual Contribution: 20%
 * Module: Action-Driven LIFO Undo & Temporal Damage Reversal Stack (Co-developed with Member A on Projectiles)
 */
public class PlayerShip {
    private double x;
    private double y;
    private int width = 54;
    private int height = 48;
    private double speed = 6.0;

    private int shield;     // 0 to 100%
    private int lives;      // 3 lives
    private int ammo;       // e.g. 100 max
    private int maxAmmo = 100;
    private int score;
    private int comboStreak;

    // DSA Integration
    private CustomStack<RewindFrame> rewindStack;
    private CustomLinkedList<Laser> laserList;

    public PlayerShip(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.shield = 100;
        this.lives = 3;
        this.ammo = 100;
        this.score = 0;
        this.comboStreak = 0;

        // Custom Stack for Undo / Time Rewind (Lesson 5) - Capacity 30
        this.rewindStack = new CustomStack<>(30);

        // Custom Linked List for active projectiles (Lesson 3)
        this.laserList = new CustomLinkedList<>();

        this.lastRecordedX = startX;
        this.lastRecordedY = startY;
        recordStateForRewind(0); // Initial baseline state
    }

    private double lastRecordedX;
    private double lastRecordedY;
    private int shotBurstCounter = 0;

    /**
     * Action-Driven Undo: Records state on weapon fire action.
     */
    public Laser fireLaser() {
        if (ammo <= 0) return null;

        // Snapshot state at start of firing salvo so shots can be undone
        if (shotBurstCounter == 0) {
            recordStateForRewind(0);
        }
        shotBurstCounter = (shotBurstCounter + 1) % 3;

        ammo--;

        // Spawn laser projectile from front cannons
        Laser laser = new Laser(x + width / 2.0 - 2.5, y - 10, 9.5, 25, true, "Pulse");
        laserList.insertLast(laser); // Lesson 3: Linked list insertion
        return laser;
    }

    /**
     * Action-Driven Undo: Records tactical flight maneuver if ship moved significantly.
     */
    public void recordManeuverIfMoved(int currentEnemyCount) {
        double dx = x - lastRecordedX;
        double dy = y - lastRecordedY;
        if (dx * dx + dy * dy >= 65 * 65) {
            recordStateForRewind(currentEnemyCount);
        }
    }

    public void recordStateForRewind(int currentEnemyCount) {
        // Push state snapshot onto LIFO Stack (Lesson 5)
        rewindStack.push(new RewindFrame(x, y, shield, score, ammo, currentEnemyCount));
        this.lastRecordedX = x;
        this.lastRecordedY = y;
    }

    public boolean executeTimeRewind() {
        RewindFrame frame = rewindStack.pop(); // Lesson 5: Stack pop()
        if (frame != null) {
            this.x = frame.playerX;
            this.y = frame.playerY;
            this.lastRecordedX = frame.playerX;
            this.lastRecordedY = frame.playerY;
            this.shield = frame.shield;
            this.score = frame.score;
            this.ammo = frame.ammo;
            return true;
        }
        return false;
    }

    /**
     * Action-Driven Undo: Records safe state BEFORE damage is received!
     */
    public void takeDamage(int amount) {
        // Snapshot safe state before damage is incurred so 'U' reverses damage
        recordStateForRewind(0);

        this.shield -= amount;
        this.comboStreak = 0; // Combo breaks on hit
        if (this.shield <= 0) {
            this.shield = 0;
            this.lives--;
            if (this.lives > 0) {
                this.shield = 100; // Shield recharge with lost life
            }
        }
    }

    public void rechargeAmmo(int amount) {
        this.ammo = Math.min(maxAmmo, this.ammo + amount);
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void incrementCombo() {
        this.comboStreak++;
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public void setShield(int shield) { this.shield = shield; }
    public void setLives(int lives) { this.lives = lives; }
    public void setAmmo(int ammo) { this.ammo = ammo; }
    public void setScore(int score) { this.score = score; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getSpeed() { return speed; }
    public int getShield() { return shield; }
    public int getLives() { return lives; }
    public int getAmmo() { return ammo; }
    public int getMaxAmmo() { return maxAmmo; }
    public int getScore() { return score; }
    public int getComboStreak() { return comboStreak; }
    public CustomStack<RewindFrame> getRewindStack() { return rewindStack; }
    public CustomLinkedList<Laser> getLaserList() { return laserList; }
}
