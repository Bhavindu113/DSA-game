package model;

/**
 * Laser - High-velocity projectile fired by Starship or Alien Enemies.
 * Stored and managed inside CustomLinkedList (KDU IT22013 Lesson 3).
 * 
 * @author Member A (ID: IT/IS/42/001) - Individual Contribution: 20%
 * Module: Dynamic Projectile Management & Linked Node Allocations
 * 
 * Demonstrates dynamic node allocation and O(1) unlinking as lasers travel and collide,
 * preventing GC hitches during intense space dogfights.
 */
public class Laser {
    private static int idCounter = 1;

    private int id;
    private double x;
    private double y;
    private double speed; // Pixels per tick
    private int damage;
    private boolean playerLaser;
    private String type; // "Pulse", "Plasma", "Ion"
    private boolean active;

    public Laser(double x, double y, double speed, int damage, boolean playerLaser, String type) {
        this.id = idCounter++;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.playerLaser = playerLaser;
        this.type = type;
        this.active = true;
    }

    public void update() {
        if (playerLaser) {
            y -= speed; // Moves upwards towards alien fleet
        } else {
            y += speed; // Alien projectile moves downwards
        }
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSpeed() { return speed; }
    public int getDamage() { return damage; }
    public boolean isPlayerLaser() { return playerLaser; }
    public String getType() { return type; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }

    @Override
    public String toString() {
        return "#" + id + " [" + type + " DMG:" + damage + " @ (" + (int) x + "," + (int) y + ")]";
    }
}
