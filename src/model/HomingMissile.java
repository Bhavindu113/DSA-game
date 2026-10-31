package model;

/**
 * HomingMissile - Smart guided missile targeted via Divide & Conquer Binary Search.
 * Corresponds to KDU IT22013 Lesson 1 & Lesson 2 (Logarithmic Target Acquisition).
 * 
 * @author Member E (ID: IT/IS/42/005) - Individual Contribution: 20%
 * Module: Logarithmic Radar Missile Targeting & Recursive Chain Lightning
 * 
 * Steers dynamically toward the enemy craft locked by SearchingAlgorithms.findClosestTargetByBinarySearch().
 */
public class HomingMissile {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double speed = 7.5;
    private AlienEnemy target;
    private int damage = 120;
    private boolean active = true;

    public HomingMissile(double startX, double startY, AlienEnemy target) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.vx = 0;
        this.vy = -speed;
    }

    public void update() {
        if (!active) return;

        if (target != null && target.isActive()) {
            double targetCenterX = target.getX() + target.getWidth() / 2.0;
            double targetCenterY = target.getY() + target.getHeight() / 2.0;

            double dx = targetCenterX - x;
            double dy = targetCenterY - y;
            double dist = Math.hypot(dx, dy);

            if (dist > 1.0) {
                // Steer towards target
                vx = (dx / dist) * speed;
                vy = (dy / dist) * speed;
            }

            x += vx;
            y += vy;

            // Check collision with target
            if (dist < 22.0) {
                target.takeDamage(damage);
                active = false;
            }
        } else {
            // Target destroyed or lost, fly straight up
            y -= speed;
            if (y < -30) active = false;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getDamage() { return damage; }
    public boolean isActive() { return active; }
    public AlienEnemy getTarget() { return target; }
    public void deactivate() { this.active = false; }
}
