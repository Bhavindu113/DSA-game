package algorithms;

import model.AlienEnemy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * SearchingAlgorithms - Implementation of Binary Search and Linear Search.
 * Corresponds to General Sir John Kotelawala Defence University (KDU) IT22013 Lesson 1 (Divide & Conquer) & Lesson 2 (Arrays).
 * 
 * @author Member E (ID: IT/IS/42/005) - Individual Contribution: 20%
 * Module: Logarithmic Radar Missile Targeting & Recursive Chain Lightning
 * 
 * Theoretical Complexities:
 * - Binary Search: Best O(1), Worst O(log N) Time, O(1) Auxiliary Space
 * - Linear Search: Best O(1), Worst O(N) Time, O(1) Auxiliary Space
 * 
 * In-Game Application:
 * 1. Smart Missile Radar Binary Search: Finds the optimal target closest to player's
 *    firing vector in O(log N) comparisons using Divide & Conquer.
 * 2. Binary Search by Unique ID: Instantaneous query resolution for targeting telemetry.
 */
public class SearchingAlgorithms {

    /**
     * Binary Search Target Acquisition for Smart Homing Missile.
     * Takes active alien enemies, sorts them by X coordinate, and uses Binary Search
     * to locate the enemy closest to the player's horizontal launch position in O(log N) time.
     * 
     * @param enemies List of active alien targets
     * @param targetX The player's X coordinate aiming vector
     * @return The closest AlienEnemy locked by Binary Search
     */
    public static AlienEnemy findClosestTargetByBinarySearch(List<AlienEnemy> enemies, double targetX) {
        if (enemies == null || enemies.isEmpty()) {
            return null;
        }

        // 1. Prepare sorted array by X coordinate (Pre-requisite for Binary Search)
        AlienEnemy[] sorted = enemies.toArray(new AlienEnemy[0]);
        Arrays.sort(sorted, Comparator.comparingDouble(AlienEnemy::getX));

        int low = 0;
        int high = sorted.length - 1;

        // Binary Search Divide & Conquer
        while (low <= high) {
            int mid = low + (high - low) / 2;
            double midX = sorted[mid].getX() + sorted[mid].getWidth() / 2.0;

            if (Math.abs(midX - targetX) < 20.0) {
                return sorted[mid]; // Direct lock on target
            } else if (midX < targetX) {
                low = mid + 1; // Search right half
            } else {
                high = mid - 1; // Search left half
            }
        }

        // Boundary check: select closest between low and high
        if (low >= sorted.length) low = sorted.length - 1;
        if (high < 0) high = 0;

        double d1 = Math.abs((sorted[low].getX() + sorted[low].getWidth() / 2.0) - targetX);
        double d2 = Math.abs((sorted[high].getX() + sorted[high].getWidth() / 2.0) - targetX);

        return (d1 <= d2) ? sorted[low] : sorted[high];
    }

    /**
     * Classic Binary Search by unique Target ID.
     * Time Complexity: O(log N)
     */
    public static AlienEnemy binarySearchById(AlienEnemy[] sortedEnemies, int targetId) {
        int low = 0;
        int high = sortedEnemies.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (sortedEnemies[mid].getId() == targetId) {
                return sortedEnemies[mid];
            } else if (sortedEnemies[mid].getId() < targetId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }
}
