package model;

/**
 * AlienEnemy - Invading hostile extraterrestrial spaceship.
 * Staged inside CustomCircularQueue (KDU IT22013 Lesson 4: Queue),
 * sorted by Threat Level (Lesson 9: Insertion Sort),
 * and targeted via Logarithmic Radar Search (Lesson 1 & 2: Binary Search).
 * 
 * @author Member C (ID: IT/IS/42/003) - Individual Contribution: 20%
 * Module: FIFO Round Scheduling & Alien Wave Staging (Co-developed with Member D for Fleet Sorting)
 */
public class AlienEnemy implements Comparable<AlienEnemy> {
    private int id;
    private String type; // "Grunt", "Heavy", "Swarm", "Dreadnought Boss"
    private double x;
    private double y;
    private double vx; // Horizontal patrol velocity
    private int hp;
    private int maxHp;
    private int attackPower;
    private int threatLevel; // Calculated as: hp * attackPower (for sorting)
    private int scoreReward;
    private int width;
    private int height;
    private boolean active;
    private boolean targeted; // True if locked on by Radar Binary Search

    public AlienEnemy(int id, String type, double x, double y, int hp, int attackPower, int scoreReward) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.vx = 1.8;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.threatLevel = hp * attackPower;
        this.scoreReward = scoreReward;
        this.active = true;
        this.targeted = false;

        // Size based on enemy category
        if (type.equalsIgnoreCase("Boss")) {
            this.width = 64;
            this.height = 48;
            this.vx = 1.0;
        } else if (type.equalsIgnoreCase("Heavy")) {
            this.width = 44;
            this.height = 36;
            this.vx = 1.4;
        } else if (type.equalsIgnoreCase("Swarm")) {
            this.width = 28;
            this.height = 24;
            this.vx = 2.6;
        } else { // Grunt
            this.width = 36;
            this.height = 30;
            this.vx = 1.8;
        }
    }

    public void update(int canvasWidth) {
        x += vx;
        // Bounce off canvas boundaries and keep strictly clear of the hotkey dock on right
        int rightLimit = canvasWidth - 85;
        if (x + width >= rightLimit) {
            x = rightLimit - width;
            vx = -Math.abs(vx);
            y += 6; // Advance downwards
        } else if (x <= 15) {
            x = 15;
            vx = Math.abs(vx);
            y += 6; // Advance downwards
        }
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
        this.threatLevel = Math.max(0, this.hp * this.attackPower);
        if (this.hp <= 0) {
            this.hp = 0;
            this.active = false;
        }
    }

    @Override
    public int compareTo(AlienEnemy other) {
        // Natural ordering by ID for Binary Search
        return Integer.compare(this.id, other.id);
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public int getThreatLevel() { return threatLevel; }
    public int getScoreReward() { return scoreReward; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isActive() { return active; }
    public boolean isTargeted() { return targeted; }
    public void setTargeted(boolean targeted) { this.targeted = targeted; }

    @Override
    public String toString() {
        return "[" + id + "] " + type + " (HP:" + hp + "/" + maxHp + ", Threat:" + threatLevel + ")";
    }
}
