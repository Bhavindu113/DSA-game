package model;

/**
 * GameMode - High-level operational state for Galaxy Defender.
 */
public enum GameMode {
    MENU,       // Starting Screen / Main Menu (Awaiting user to Start or Continue)
    PLAYING,    // Active Combat Simulation
    PAUSED      // Combat Paused (Auto-saved, allows resume or return to menu)
}
