import ui.GalaxyDefenderFrame;

import javax.swing.*;

/**
 * Main - Application Entry Point for KDU IT22013 Data Structures and Algorithms Group Assignment.
 * Project: Galaxy Defender (DSA Edition) - 2D Space Arcade Combat Simulator.
 * 
 * General Sir John Kotelawala Defence University (KDU) - Department of Information Technology.
 * Intake 42 - Group Assignment (Academic Year 2026).
 * 
 * Group Members (Equal 20% Contribution):
 * - Member A (ID: IT/IS/42/001) - Custom Singly Linked List & Laser Projectile Pipeline (Lesson 3)
 * - Member B (ID: IT/IS/42/002) - Custom LIFO Undo & Damage Reversal Stack (Lesson 5)
 * - Member C (ID: IT/IS/42/003) - Custom FIFO Circular Queue & Round Scheduling (Lesson 4)
 * - Member D (ID: IT/IS/42/004) - Tactical Fleet Threat Formation & Insertion Sorting (Lesson 9)
 * - Member E (ID: IT/IS/42/005) - Radar Binary Search O(log N) & Recursive Chain Lightning (Lesson 1 & 2)
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new GalaxyDefenderFrame();
        });
    }
}
