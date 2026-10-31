package algorithms;

import model.AlienEnemy;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;

/**
 * RecursionHelper - Practical In-Game Recursive Algorithms.
 * Corresponds to General Sir John Kotelawala Defence University (KDU) IT22013 Lesson 1 (Recursion & Divide and Conquer).
 * 
 * @author Member E (ID: IT/IS/42/005) - Individual Contribution: 20%
 * Module: Logarithmic Radar Missile Targeting & Recursive Chain Lightning
 * 
 * Theoretical Complexities:
 * - Recursive Chain Lightning: O(K * N) Time where K is jump limit (4) and N is enemy count
 * - Call Stack Depth: O(K) Auxiliary Space (bounded to prevent java.lang.StackOverflowError)
 * - Recursive Combo Multiplier: O(C) Time where C is combo streak, O(C) Stack Frames
 * 
 * In-Game Application:
 * 1. Recursive Chain Lightning: Strikes primary target and recursively leaps to nearest unvisited
 *    hostile craft, dynamically generating branching electrical energy arcs.
 * 2. Recursive Score Combo: Calculates geometric score escalation based on consecutive hits.
 */
public class RecursionHelper {

    /**
     * 1. Recursive Chain Lightning Weapon Propagation.
     * Jumps from target to the nearest unvisited enemy, zapping them with electric arcs.
     * 
     * Base Cases:
     * - jumpsRemaining <= 0
     * - No more valid unvisited targets within jump range
     * 
     * Recursive Step:
     * - Deals damage to current target
     * - Finds closest unvisited enemy in sector
     * - Calls propagateChainLightning(nearest, ..., jumpsRemaining - 1)
     */
    public static void propagateChainLightning(
            AlienEnemy current,
            List<AlienEnemy> allEnemies,
            Set<AlienEnemy> visited,
            int jumpsRemaining,
            int damage,
            List<Point2D[]> lightningSegments) {

        // Base Case 1: Reached maximum chain jumps or invalid node
        if (current == null || jumpsRemaining <= 0) {
            return;
        }

        visited.add(current);
        current.takeDamage(damage);

        // Find nearest unvisited enemy
        AlienEnemy nearest = null;
        double minDistance = 260.0; // Jump radius in pixels

        double curCenterX = current.getX() + current.getWidth() / 2.0;
        double curCenterY = current.getY() + current.getHeight() / 2.0;

        for (AlienEnemy candidate : allEnemies) {
            if (candidate.isActive() && !visited.contains(candidate)) {
                double candCenterX = candidate.getX() + candidate.getWidth() / 2.0;
                double candCenterY = candidate.getY() + candidate.getHeight() / 2.0;
                double dist = Math.hypot(candCenterX - curCenterX, candCenterY - curCenterY);

                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = candidate;
                }
            }
        }

        // Base Case 2: No more reachable enemies within lightning arc range
        if (nearest != null) {
            // Record visual lightning bolt segment
            Point2D from = new Point2D.Double(curCenterX, curCenterY);
            Point2D to = new Point2D.Double(nearest.getX() + nearest.getWidth() / 2.0, nearest.getY() + nearest.getHeight() / 2.0);
            lightningSegments.add(new Point2D[]{from, to});

            // Recursive Step: Jump to next target with (jumpsRemaining - 1)
            propagateChainLightning(nearest, allEnemies, visited, jumpsRemaining - 1, (int) (damage * 0.85), lightningSegments);
        }
    }

    /**
     * 2. Recursive Score Combo Multiplier.
     * Computes killstreak bonuses: f(n) = n * base + f(n - 1).
     */
    public static int calculateComboScore(int streak, int basePoints) {
        if (streak <= 1) {
            return basePoints; // Base case
        }
        return (streak * basePoints) + calculateComboScore(streak - 1, basePoints); // Recursive case
    }
}
