package ui;

import model.AlienEnemy;
import model.GalaxyGameState;
import model.GameMode;
import model.HomingMissile;
import model.Laser;
import model.PlayerShip;
import model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.List;

/**
 * SpaceCombatCanvas - High-Fidelity 2D Sci-Fi Arcade Combat Renderer (60 FPS).
 * Academic Visualization Layer for KDU IT22013 Data Structures and Algorithms Project.
 * 
 * Visualization Features:
 * - 2D Parallax Starfield Background (Lesson 2: Multidimensional Arrays double[150][3])
 * - Dynamic Laser Projectile Node Rendering (Lesson 3: Singly Linked List)
 * - Round Queue Wave Status & Alien Spawn Tracking (Lesson 4: Circular Queue)
 * - Action-Driven Temporal Undo Ring Gauge (Lesson 5: LIFO Stack capacity 30)
 * - Real-Time In-Place Fleet Formation Reordering Visualizer (Lesson 9: Insertion Sort)
 * - Divide & Conquer Logarithmic Radar Sweep & Reticle Lock (Lesson 1 & 2: Binary Search)
 * - Multi-Branch Electric Energy Discharge Arcs (Lesson 1: Recursion)
 * - Holographic Right-Side Action Dock with Embedded Hotkey Icons & Radial Cooldowns
 */
public class SpaceCombatCanvas extends JPanel {
    private GalaxyGameState gameState;
    private long animationTick = 0;

    // 3-Second Round Announcement Timer (180 ticks at 60 FPS)
    private int roundBannerTimer = 180;
    private int lastObservedRound = 1;

    // Floating DSA Action Notification
    private String hudNotification = "";
    private int hudNotificationTimer = 0;

    // Futuristic Sci-Fi Palette
    private final Color COLOR_SPACE_BG_TOP = new Color(10, 8, 26);
    private final Color COLOR_SPACE_BG_BOT = new Color(3, 4, 12);
    private final Color COLOR_NEON_CYAN = new Color(0, 235, 255);
    private final Color COLOR_NEON_MAGENTA = new Color(255, 0, 140);
    private final Color COLOR_NEON_RED = new Color(255, 45, 75);
    private final Color COLOR_NEON_GOLD = new Color(255, 215, 0);
    private final Color COLOR_NEON_LIME = new Color(50, 255, 130);

    // Operational mode & Parent frame link
    private GalaxyDefenderFrame frame;
    private GameMode gameMode = GameMode.MENU;

    // Interactive button hitboxes
    private final Rectangle rectStartGame = new Rectangle();
    private final Rectangle rectContinueGame = new Rectangle();
    private final Rectangle rectResumeGame = new Rectangle();
    private final Rectangle rectReturnMenu = new Rectangle();
    private final Rectangle rectRestartRound = new Rectangle();

    // Hover button identifiers
    public static final int HOVER_NONE = 0;
    public static final int HOVER_START = 1;
    public static final int HOVER_CONTINUE = 2;
    public static final int HOVER_RESUME = 3;
    public static final int HOVER_MENU = 4;
    public static final int HOVER_RESTART = 5;
    private int hoveredButton = HOVER_NONE;

    public SpaceCombatCanvas(GalaxyGameState gameState, GalaxyDefenderFrame frame) {
        this.gameState = gameState;
        this.frame = frame;
        setPreferredSize(new Dimension(GalaxyGameState.CANVAS_WIDTH, GalaxyGameState.CANVAS_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setupMouseListeners();
    }

    public SpaceCombatCanvas(GalaxyGameState gameState) {
        this(gameState, null);
    }

    public void setFrame(GalaxyDefenderFrame frame) {
        this.frame = frame;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
        this.hoveredButton = HOVER_NONE;
        repaint();
    }

    public void setGameState(GalaxyGameState gameState) {
        this.gameState = gameState;
        this.roundBannerTimer = 180;
        this.lastObservedRound = gameState.getCurrentRound();
    }

    public void showNotification(String text) {
        this.hudNotification = text;
        this.hudNotificationTimer = 150; // Show for 2.5 seconds
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        animationTick++;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. Draw Deep Space Background & Starfield (2D Array - Lesson 2)
        drawNebulaAndStars(g2);

        // Render Start Screen if in MENU mode
        if (gameMode == GameMode.MENU) {
            drawStartMenu(g2);
            if (hudNotificationTimer > 0) {
                hudNotificationTimer--;
                drawFloatingNotification(g2);
            }
            return;
        }

        // Check if round changed to show 3-second banner
        if (gameState.getCurrentRound() != lastObservedRound) {
            lastObservedRound = gameState.getCurrentRound();
            roundBannerTimer = 180; // Reset to 3 seconds
        }

        // 2. Draw Active Alien Invaders (with Floating Health Bars)
        for (AlienEnemy enemy : gameState.getActiveEnemies()) {
            if (enemy.isActive()) {
                drawAdvancedAlienShip(g2, enemy);
            }
        }

        // 3. Draw Recursive Chain Lightning Bolts (Recursion - Lesson 1)
        drawLightningArcs(g2);

        // 4. Draw Player Lasers with Neon Glow (CustomLinkedList - Lesson 3)
        for (Laser laser : gameState.getPlayer().getLaserList()) {
            if (laser.isActive()) {
                drawPlayerLaser(g2, laser);
            }
        }

        // 5. Draw Smart Homing Missiles (Binary Search guidance - Lesson 1 & 2)
        drawHomingMissiles(g2);

        // 6. Draw Enemy Plasma Projectiles
        for (Laser el : gameState.getEnemyLasers()) {
            drawEnemyPlasma(g2, el);
        }

        // 7. Draw Player Starship
        drawAdvancedPlayerShip(g2, gameState.getPlayer());

        // 6. Draw 3-Second Round Banner (Disappears after 3 seconds)
        if (roundBannerTimer > 0) {
            roundBannerTimer--;
            drawRoundBanner(g2, gameState.getCurrentRound());
        }

        // 7. Draw Floating DSA Operation Notification (if active)
        if (hudNotificationTimer > 0) {
            hudNotificationTimer--;
            drawFloatingNotification(g2);
        }

        // 8. Draw Vertical Circular DSA Action Dock (Right Side)
        drawVerticalActionDock(g2);

        // 9. Overlay Pause Menu if paused
        if (gameMode == GameMode.PAUSED) {
            drawPauseMenu(g2);
            return;
        }

        // 10. Overlay Game Over / Victory
        if (gameState.isGameOver()) {
            drawGameOverMenu(g2);
        } else if (gameState.isVictory()) {
            drawVictoryMenu(g2);
        }
    }

    /**
     * 3-Second Temporary Round Announcement Banner that fades out without blocking combat vision.
     */
    private void drawRoundBanner(Graphics2D g2, int round) {
        float alpha = 1.0f;
        if (roundBannerTimer < 45) {
            alpha = roundBannerTimer / 45.0f;
        }

        int bannerY = getHeight() / 2 - 60;
        int bannerW = 340;
        int bannerH = 55;
        int bannerX = (getWidth() - bannerW) / 2;

        // Subtle semi-transparent rounded pill (not full-width screen blockage)
        g2.setColor(new Color(10, 16, 32, (int) (130 * alpha)));
        g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, 14, 14);

        g2.setColor(new Color(0, 235, 255, (int) (170 * alpha)));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bannerX, bannerY, bannerW, bannerH, 14, 14);

        // Banner Text
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String title = "⚔ ROUND " + round + " ⚔";
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(title)) / 2;
        g2.setColor(new Color(255, 255, 255, (int) (255 * alpha)));
        g2.drawString(title, tx, bannerY + 28);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String sub = "INVASION FLEET ARRIVING";
        FontMetrics fmSub = g2.getFontMetrics();
        g2.setColor(new Color(0, 230, 255, (int) (240 * alpha)));
        g2.drawString(sub, (getWidth() - fmSub.stringWidth(sub)) / 2, bannerY + 46);
    }

    /**
     * In-game floating notification for DSA actions.
     * Positioned slightly above the player's ship with a sleek semi-transparent glassmorphic look.
     */
    private void drawFloatingNotification(Graphics2D g2) {
        if (hudNotification == null || hudNotification.isEmpty()) return;

        PlayerShip player = gameState.getPlayer();
        if (player == null) return;

        // Smooth fade-out alpha for the last 40 frames
        float alpha = 1.0f;
        if (hudNotificationTimer < 40) {
            alpha = hudNotificationTimer / 40.0f;
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(hudNotification);
        int boxW = textW + 28;
        int boxH = 26;

        // Centered horizontally on screen (fixed center, not following ship position)
        int boxX = (getWidth() - boxW) / 2;

        // Positioned slightly above the player ship area
        int boxY = getHeight() - 135;

        // Semi-transparent glassmorphic pill background (translucent alpha ~105)
        g2.setColor(new Color(10, 16, 28, (int) (105 * alpha)));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 13, 13);

        // Soft glowing neon cyan border
        g2.setColor(new Color(0, 235, 255, (int) (140 * alpha)));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 13, 13);

        // Crisp luminous text
        g2.setColor(new Color(255, 235, 120, (int) (235 * alpha)));
        g2.drawString(hudNotification, boxX + 14, boxY + 18);
    }

    private void drawNebulaAndStars(Graphics2D g2) {
        // Space Gradient
        GradientPaint gp = new GradientPaint(0, 0, COLOR_SPACE_BG_TOP, 0, getHeight(), COLOR_SPACE_BG_BOT);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Ambient Nebula Glow
        RadialGradientPaint nebula = new RadialGradientPaint(
                new Point2D.Float(getWidth() * 0.7f, getHeight() * 0.35f),
                280.0f,
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{new Color(110, 20, 160, 45), new Color(40, 10, 100, 25), new Color(0, 0, 0, 0)}
        );
        g2.setPaint(nebula);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Starfield (2D Array - Lesson 2)
        double[][] stars = gameState.getStars();
        for (int i = 0; i < stars.length; i++) {
            int sx = (int) stars[i][0];
            int sy = (int) stars[i][1];
            double spd = stars[i][2];
            int size = (spd > 1.8) ? 3 : ((spd > 1.0) ? 2 : 1);

            if (size == 3) {
                g2.setColor(new Color(180, 230, 255, 220));
                g2.fillOval(sx, sy, 3, 3);
            } else if (size == 2) {
                g2.setColor(new Color(230, 230, 250, 180));
                g2.fillOval(sx, sy, 2, 2);
            } else {
                g2.setColor(new Color(190, 200, 220, 130));
                g2.fillRect(sx, sy, 1, 1);
            }
        }
    }

    private void drawAdvancedAlienShip(Graphics2D g2, AlienEnemy enemy) {
        int ex = (int) enemy.getX();
        int ey = (int) enemy.getY();
        int w = enemy.getWidth();
        int h = enemy.getHeight();

        String type = enemy.getType();

        if (type.equalsIgnoreCase("Boss")) {
            drawBossDreadnought(g2, ex, ey, w, h, enemy);
        } else if (type.equalsIgnoreCase("Heavy")) {
            drawHeavyCruiser(g2, ex, ey, w, h, enemy);
        } else if (type.equalsIgnoreCase("Swarm")) {
            drawSwarmHornet(g2, ex, ey, w, h, enemy);
        } else {
            drawGruntStalker(g2, ex, ey, w, h, enemy);
        }

        // Radar Target Lock Reticle if selected via Binary Search
        if (enemy.isTargeted()) {
            drawTargetReticle(g2, ex, ey, w, h, enemy);
        }
    }

    /**
     * Floating Health Bar above alien ships (Clean, no numbers).
     */
    private void drawMiniHealthBar(Graphics2D g2, int x, int y, int w, AlienEnemy enemy) {
        int barH = 4;
        int barY = y - 7;

        // Background
        g2.setColor(new Color(20, 20, 30));
        g2.fillRect(x, barY, w, barH);

        // Fill based on HP
        double pct = (double) enemy.getHp() / enemy.getMaxHp();
        Color hpColor = (pct > 0.5) ? COLOR_NEON_LIME : ((pct > 0.25) ? Color.ORANGE : Color.RED);
        g2.setColor(hpColor);
        g2.fillRect(x, barY, (int) (w * pct), barH);

        // Frame
        g2.setColor(new Color(60, 80, 110));
        g2.drawRect(x, barY, w, barH);
    }

    /**
     * 1. GRUNT STALKER: Crimson Bio-Mechanical Scout with predatory curved mandibles.
     */
    private void drawGruntStalker(Graphics2D g2, int x, int y, int w, int h, AlienEnemy enemy) {
        drawMiniHealthBar(g2, x, y, w, enemy);

        Path2D.Double pincerLeft = new Path2D.Double();
        pincerLeft.moveTo(x + w * 0.25, y + h * 0.4);
        pincerLeft.quadTo(x - 2, y + h * 0.7, x + w * 0.2, y + h);
        pincerLeft.lineTo(x + w * 0.35, y + h * 0.5);
        pincerLeft.closePath();

        Path2D.Double pincerRight = new Path2D.Double();
        pincerRight.moveTo(x + w * 0.75, y + h * 0.4);
        pincerRight.quadTo(x + w + 2, y + h * 0.7, x + w * 0.8, y + h);
        pincerRight.lineTo(x + w * 0.65, y + h * 0.5);
        pincerRight.closePath();

        g2.setColor(new Color(255, 60, 40));
        g2.fill(pincerLeft);
        g2.fill(pincerRight);

        Path2D.Double hull = new Path2D.Double();
        hull.moveTo(x + w * 0.5, y);
        hull.lineTo(x + w, y + h * 0.45);
        hull.lineTo(x + w * 0.7, y + h * 0.75);
        hull.lineTo(x + w * 0.5, y + h * 0.95);
        hull.lineTo(x + w * 0.3, y + h * 0.75);
        hull.lineTo(x, y + h * 0.45);
        hull.closePath();

        GradientPaint hullGrad = new GradientPaint(x, y, new Color(180, 20, 40), x, y + h, new Color(60, 10, 20));
        g2.setPaint(hullGrad);
        g2.fill(hull);

        g2.setColor(COLOR_NEON_RED);
        g2.setStroke(new BasicStroke(1.4f));
        g2.draw(hull);

        float pulse = (float) (0.7 + 0.3 * Math.sin(animationTick * 0.15));
        int eyeSize = 8;
        g2.setColor(new Color(255, 230, 80, (int) (pulse * 255)));
        g2.fillOval(x + (w - eyeSize) / 2, y + h / 2 - 4, eyeSize, eyeSize);
        g2.setColor(Color.WHITE);
        g2.fillOval(x + (w - eyeSize) / 2 + 2, y + h / 2 - 2, 3, 3);
    }

    /**
     * 2. HEAVY CRUISER: Imposing armored battlecruiser in royal purple & titanium.
     */
    private void drawHeavyCruiser(Graphics2D g2, int x, int y, int w, int h, AlienEnemy enemy) {
        drawMiniHealthBar(g2, x, y, w, enemy);

        g2.setColor(new Color(30, 20, 50));
        g2.fillRoundRect(x - 2, y + 4, 10, h - 8, 4, 4);
        g2.fillRoundRect(x + w - 8, y + 4, 10, h - 8, 4, 4);

        g2.setColor(COLOR_NEON_CYAN);
        g2.fillRect(x, y + 8, 6, 4);
        g2.fillRect(x + w - 6, y + 8, 6, 4);

        Path2D.Double hull = new Path2D.Double();
        hull.moveTo(x + w * 0.5, y);
        hull.lineTo(x + w * 0.85, y + h * 0.3);
        hull.lineTo(x + w * 0.85, y + h * 0.85);
        hull.lineTo(x + w * 0.5, y + h);
        hull.lineTo(x + w * 0.15, y + h * 0.85);
        hull.lineTo(x + w * 0.15, y + h * 0.3);
        hull.closePath();

        GradientPaint gp = new GradientPaint(x, y, new Color(130, 60, 180), x, y + h, new Color(40, 15, 65));
        g2.setPaint(gp);
        g2.fill(hull);

        g2.setColor(new Color(200, 120, 255));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(hull);

        int coreW = 12, coreH = 10;
        int cx = x + (w - coreW) / 2;
        int cy = y + (h - coreH) / 2;
        g2.setColor(COLOR_NEON_CYAN);
        g2.fillRoundRect(cx, cy, coreW, coreH, 4, 4);
        g2.setColor(Color.WHITE);
        g2.fillRect(cx + 3, cy + 2, 6, 6);
    }

    /**
     * 3. SWARM HORNET: Ultra-fast venomous drone in neon emerald green.
     */
    private void drawSwarmHornet(Graphics2D g2, int x, int y, int w, int h, AlienEnemy enemy) {
        drawMiniHealthBar(g2, x, y, w, enemy);

        Path2D.Double wings = new Path2D.Double();
        wings.moveTo(x + w * 0.5, y);
        wings.lineTo(x + w, y + h * 0.6);
        wings.lineTo(x + w * 0.7, y + h * 0.7);
        wings.lineTo(x + w * 0.5, y + h);
        wings.lineTo(x + w * 0.3, y + h * 0.7);
        wings.lineTo(x, y + h * 0.6);
        wings.closePath();

        GradientPaint gp = new GradientPaint(x, y, new Color(30, 220, 100), x, y + h, new Color(10, 80, 40));
        g2.setPaint(gp);
        g2.fill(wings);

        g2.setColor(COLOR_NEON_LIME);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(wings);

        g2.setColor(Color.WHITE);
        g2.fillRect(x + w / 2 - 1, y + h - 2, 2, 6);
    }

    /**
     * 4. DREADNOUGHT BOSS: Massive flagship with layered armor & antimatter core.
     */
    private void drawBossDreadnought(Graphics2D g2, int x, int y, int w, int h, AlienEnemy boss) {
        float shieldAlpha = (float) (0.25 + 0.15 * Math.sin(animationTick * 0.1));
        g2.setColor(new Color(0, 200, 255, (int) (shieldAlpha * 255)));
        g2.fillOval(x - 8, y - 8, w + 16, h + 16);
        g2.setColor(COLOR_NEON_CYAN);
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(x - 8, y - 8, w + 16, h + 16);

        g2.setColor(new Color(40, 45, 60));
        g2.fillRoundRect(x - 6, y + 10, 14, h - 16, 4, 4);
        g2.fillRoundRect(x + w - 8, y + 10, 14, h - 16, 4, 4);
        g2.setColor(COLOR_NEON_RED);
        g2.fillRect(x - 2, y + h - 10, 6, 10);
        g2.fillRect(x + w - 4, y + h - 10, 6, 10);

        Path2D.Double hull = new Path2D.Double();
        hull.moveTo(x + w * 0.5, y);
        hull.lineTo(x + w * 0.9, y + h * 0.35);
        hull.lineTo(x + w * 0.75, y + h * 0.85);
        hull.lineTo(x + w * 0.5, y + h);
        hull.lineTo(x + w * 0.25, y + h * 0.85);
        hull.lineTo(x + w * 0.1, y + h * 0.35);
        hull.closePath();

        GradientPaint bossGrad = new GradientPaint(x, y, new Color(180, 20, 120), x, y + h, new Color(40, 10, 35));
        g2.setPaint(bossGrad);
        g2.fill(hull);

        g2.setColor(COLOR_NEON_MAGENTA);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(hull);

        int coreRadius = 16;
        int cx = x + w / 2;
        int cy = y + h / 2;

        RadialGradientPaint coreGlow = new RadialGradientPaint(
                new Point2D.Float(cx, cy), coreRadius,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{Color.WHITE, new Color(255, 0, 120), new Color(40, 0, 50, 0)}
        );
        g2.setPaint(coreGlow);
        g2.fillOval(cx - coreRadius, cy - coreRadius, coreRadius * 2, coreRadius * 2);

        drawBossHealthBar(g2, x, y, w, boss);
    }

    private void drawBossHealthBar(Graphics2D g2, int x, int y, int w, AlienEnemy boss) {
        int barW = w + 20;
        int barX = x - 10;
        int barY = y - 16;
        int barH = 7;

        g2.setColor(new Color(20, 10, 30));
        g2.fillRoundRect(barX, barY, barW, barH, 4, 4);

        double pct = (double) boss.getHp() / boss.getMaxHp();
        GradientPaint hpGrad = new GradientPaint(barX, barY, Color.RED, barX + barW, barY, COLOR_NEON_MAGENTA);
        g2.setPaint(hpGrad);
        g2.fillRoundRect(barX, barY, (int) (barW * pct), barH, 4, 4);

        g2.setColor(COLOR_NEON_GOLD);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(barX, barY, barW, barH, 4, 4);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("👑 DREADNOUGHT BOSS " + (int) (pct * 100) + "%", barX + 6, barY - 3);
    }

    private void drawTargetReticle(Graphics2D g2, int x, int y, int w, int h, AlienEnemy enemy) {
        g2.setColor(COLOR_NEON_GOLD);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 3}, 0));
        g2.drawRoundRect(x - 8, y - 8, w + 16, h + 16, 8, 8);

        int cross = 6;
        g2.drawLine(x - 8, y - 8 + cross, x - 8, y - 8);
        g2.drawLine(x - 8, y - 8, x - 8 + cross, y - 8);
        g2.drawLine(x + w + 8 - cross, y - 8, x + w + 8, y - 8);
        g2.drawLine(x + w + 8, y - 8, x + w + 8, y - 8 + cross);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("🎯 TARGET LOCKED", x - 4, y - 18);
        g2.setStroke(new BasicStroke(1.0f));
    }

    private void drawAdvancedPlayerShip(Graphics2D g2, PlayerShip ship) {
        int px = (int) ship.getX();
        int py = (int) ship.getY();
        int w = ship.getWidth();
        int h = ship.getHeight();

        float flameLength = (float) (14 + Math.random() * 8);
        GradientPaint flameGrad = new GradientPaint(px + 20, py + h - 6, new Color(0, 240, 255, 230), px + 20, py + h + flameLength, new Color(0, 80, 255, 0));
        g2.setPaint(flameGrad);
        g2.fillOval(px + 14, py + h - 8, 8, (int) flameLength);
        g2.fillOval(px + w - 22, py + h - 8, 8, (int) flameLength);

        Path2D.Double body = new Path2D.Double();
        body.moveTo(px + w * 0.5, py);
        body.lineTo(px + w * 0.72, py + h * 0.45);
        body.lineTo(px + w, py + h * 0.78);
        body.lineTo(px + w * 0.88, py + h * 0.95);
        body.lineTo(px + w * 0.65, py + h * 0.82);
        body.lineTo(px + w * 0.35, py + h * 0.82);
        body.lineTo(px + w * 0.12, py + h * 0.95);
        body.lineTo(px, py + h * 0.78);
        body.lineTo(px + w * 0.28, py + h * 0.45);
        body.closePath();

        GradientPaint hullGrad = new GradientPaint(px, py, new Color(28, 44, 68), px, py + h, new Color(10, 16, 28));
        g2.setPaint(hullGrad);
        g2.fill(body);

        g2.setColor(COLOR_NEON_CYAN);
        g2.setStroke(new BasicStroke(1.8f));
        g2.draw(body);

        g2.setColor(COLOR_NEON_CYAN);
        g2.fillRect(px + 2, py + (int) (h * 0.6), 4, 12);
        g2.fillRect(px + w - 6, py + (int) (h * 0.6), 4, 12);

        Path2D.Double canopy = new Path2D.Double();
        canopy.moveTo(px + w * 0.5, py + 8);
        canopy.lineTo(px + w * 0.58, py + 26);
        canopy.lineTo(px + w * 0.42, py + 26);
        canopy.closePath();

        g2.setColor(new Color(0, 200, 255, 180));
        g2.fill(canopy);
        g2.setColor(Color.WHITE);
        g2.drawLine(px + (int) (w * 0.48), py + 12, px + (int) (w * 0.48), py + 22);

        g2.setColor(new Color(0, 210, 255, 160));
        g2.drawLine(px + w / 2 - 8, py + h - 14, px + w / 2 + 8, py + h - 14);
    }

    /**
     * High-energy Player Plasma Lance (Dynamic Singly Linked List - Lesson 3).
     */
    private void drawPlayerLaser(Graphics2D g2, Laser laser) {
        int lx = (int) laser.getX();
        int ly = (int) laser.getY();

        // 1. Ion Exhaust Trail (trailing downwards from bullet moving up)
        GradientPaint trailGrad = new GradientPaint(
                lx, ly + 2, new Color(0, 235, 255, 200),
                lx, ly + 22, new Color(0, 100, 255, 0)
        );
        g2.setPaint(trailGrad);
        g2.fillRoundRect(lx - 2, ly + 2, 7, 20, 3, 3);

        // 2. Outer Neon Cyan Glow Halo
        g2.setColor(new Color(0, 240, 255, 90));
        g2.fillRoundRect(lx - 3, ly - 3, 9, 18, 5, 5);

        // 3. Piercing Aerodynamic Plasma Blade
        Path2D.Double bolt = new Path2D.Double();
        bolt.moveTo(lx + 1.5, ly - 4); // Sharp energy tip
        bolt.lineTo(lx + 4.5, ly + 4);
        bolt.lineTo(lx + 3.0, ly + 14);
        bolt.lineTo(lx, ly + 14);
        bolt.lineTo(lx - 1.5, ly + 4);
        bolt.closePath();

        GradientPaint boltGrad = new GradientPaint(
                lx, ly - 4, Color.WHITE,
                lx, ly + 14, COLOR_NEON_CYAN
        );
        g2.setPaint(boltGrad);
        g2.fill(bolt);

        // 4. White-Hot Energy Core Beam
        g2.setColor(Color.WHITE);
        g2.drawLine(lx + 1, ly - 2, lx + 1, ly + 10);
    }

    /**
     * Alien Bio-Mechanical Plasma Orb / Comet with fiery energy trail and pulsing corona.
     */
    private void drawEnemyPlasma(Graphics2D g2, Laser el) {
        int ex = (int) el.getX();
        int ey = (int) el.getY();

        // Subtle animation pulse
        double pulse = 1.0 + 0.15 * Math.sin(animationTick * 0.35 + el.getId());
        int radius = (int) (7 * pulse);

        // 1. Fiery Plasma Tail (trailing UP from projectile moving down)
        int tailLength = 18;
        Path2D.Double tail = new Path2D.Double();
        tail.moveTo(ex - radius + 2, ey);
        tail.lineTo(ex + radius - 2, ey);
        tail.lineTo(ex, ey - tailLength); // Tail apex pointing upwards
        tail.closePath();

        GradientPaint tailGrad = new GradientPaint(
                ex, ey, new Color(255, 60, 40, 220),
                ex, ey - tailLength, new Color(180, 0, 120, 0)
        );
        g2.setPaint(tailGrad);
        g2.fill(tail);

        // 2. Outer Expanding Energy Corona (Soft glow)
        g2.setColor(new Color(255, 30, 80, 55));
        g2.fillOval(ex - radius - 4, ey - radius - 4, (radius + 4) * 2, (radius + 4) * 2);

        // 3. Mid Plasma Fireball (Crimson - Orange gradient)
        RadialGradientPaint plasmaGlow = new RadialGradientPaint(
                new Point2D.Float(ex, ey), Math.max(1, radius + 2),
                new float[]{0.0f, 0.55f, 1.0f},
                new Color[]{
                    new Color(255, 240, 160),
                    new Color(255, 50, 40, 230),
                    new Color(180, 0, 100, 40)
                }
        );
        g2.setPaint(plasmaGlow);
        g2.fillOval(ex - radius, ey - radius, radius * 2, radius * 2);

        // 4. Blinding Fusion White-Hot Center
        g2.setColor(Color.WHITE);
        int coreR = Math.max(2, (int) (radius * 0.4));
        g2.fillOval(ex - coreR, ey - coreR, coreR * 2, coreR * 2);

        // 5. Orbiting Micro Plasma Sparks
        float angle = (animationTick * 0.25f) + el.getId();
        int sparkX = (int) (ex + Math.cos(angle) * (radius + 2));
        int sparkY = (int) (ey + Math.sin(angle) * (radius + 2));
        g2.setColor(new Color(255, 230, 100, 200));
        g2.fillOval(sparkX, sparkY, 2, 2);
    }

    private void drawOverlayMessage(Graphics2D g2, String title, String sub, Color color) {
        g2.setColor(new Color(5, 7, 14, 220));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(color);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - fm1.stringWidth(title)) / 2, getHeight() / 2 - 15);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(sub, (getWidth() - fm2.stringWidth(sub)) / 2, getHeight() / 2 + 25);
    }

    /**
     * Renders Recursive Chain Lightning bolts arcing between enemies.
     */
    private void drawLightningArcs(Graphics2D g2) {
        List<Point2D[]> arcs = gameState.getActiveLightningArcs();
        if (arcs == null || arcs.isEmpty()) return;

        for (Point2D[] arc : arcs) {
            Point2D from = arc[0];
            Point2D to = arc[1];

            // Outer Electric Halo
            g2.setColor(new Color(0, 230, 255, 140));
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawJaggedLightning(g2, from.getX(), from.getY(), to.getX(), to.getY());

            // Inner Core Lightning
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawJaggedLightning(g2, from.getX(), from.getY(), to.getX(), to.getY());
        }
        g2.setStroke(new BasicStroke(1.0f));
    }

    private void drawJaggedLightning(Graphics2D g2, double x1, double y1, double x2, double y2) {
        int segments = 4;
        double dx = (x2 - x1) / segments;
        double dy = (y2 - y1) / segments;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(x1, y1);

        for (int i = 1; i < segments; i++) {
            double jitter = (Math.random() - 0.5) * 16.0;
            path.lineTo(x1 + dx * i + jitter, y1 + dy * i + jitter);
        }
        path.lineTo(x2, y2);
        g2.draw(path);
    }

    /**
     * Renders Smart Homing Missiles with rocket flame trails.
     */
    private void drawHomingMissiles(Graphics2D g2) {
        for (HomingMissile m : gameState.getActiveMissiles()) {
            if (m.isActive()) {
                int mx = (int) m.getX();
                int my = (int) m.getY();

                // Rocket Exhaust Trail
                g2.setColor(new Color(255, 140, 0, 180));
                g2.fillOval(mx - 1, my + 6, 6, 10);
                g2.setColor(Color.YELLOW);
                g2.fillOval(mx, my + 4, 4, 6);

                // Golden Rocket Hull
                g2.setColor(COLOR_NEON_GOLD);
                g2.fillRoundRect(mx - 2, my, 8, 14, 4, 4);
                g2.setColor(Color.WHITE);
                g2.fillOval(mx, my, 4, 4);
            }
        }
    }

    /**
     * Vertical Circular Ability Action Dock on the right margin.
     * Displays DSA capabilities with 360-degree radial sweeping circular cooldowns.
     * Hotkey letters (E, X, T, U) are prominently embedded inside each circle.
     */
    private void drawVerticalActionDock(Graphics2D g2) {
        int dockW = 52;
        int dockX = getWidth() - dockW - 14;
        int dockY = 85;
        int slotH = 58;
        int dockH = 4 * slotH + 12;

        // Semi-transparent Sci-Fi pill frame
        g2.setColor(new Color(8, 14, 26, 150));
        g2.fillRoundRect(dockX, dockY, dockW, dockH, 18, 18);
        g2.setColor(new Color(25, 45, 75, 170));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(dockX, dockY, dockW, dockH, 18, 18);

        int cx = dockX + dockW / 2;

        // 1. [E] ⚡ Recursive Chain Lightning (Lesson 1)
        int cdLightning = gameState.getLightningCooldown();
        boolean readyLightning = (cdLightning == 0);
        double pctLightning = cdLightning / 150.0;
        String cdTxtLightning = String.format("%.1f", cdLightning / 60.0 + 0.1) + "s";
        drawActionBadge(g2, cx, dockY + 24, "E", COLOR_NEON_CYAN, pctLightning, cdTxtLightning, readyLightning, null);

        // 2. [X] 🎯 Smart Homing Missile - Binary Search (Lesson 1 & 2)
        int cdMissile = gameState.getMissileCooldown();
        boolean readyMissile = (cdMissile == 0);
        double pctMissile = cdMissile / 180.0;
        String cdTxtMissile = String.format("%.1f", cdMissile / 60.0 + 0.1) + "s";
        drawActionBadge(g2, cx, dockY + 24 + slotH, "X", COLOR_NEON_GOLD, pctMissile, cdTxtMissile, readyMissile, null);

        // 3. [T] 📊 Fleet Formation - Insertion Sort (Lesson 9)
        boolean hasEnemies = !gameState.getActiveEnemies().isEmpty();
        drawActionBadge(g2, cx, dockY + 24 + slotH * 2, "T", COLOR_NEON_LIME, 0.0, "", hasEnemies, null);

        // 4. [U] ⏪ Time Rewind - LIFO Stack (Capacity 30 circular fill gauge)
        int stackDepth = gameState.getPlayer().getRewindStack().size();
        double fillPct = Math.min(1.0, (double) stackDepth / 30.0);
        drawStackGaugeBadge(g2, cx, dockY + 24 + slotH * 3, "U", new Color(195, 115, 255), fillPct, stackDepth);
    }

    /**
     * Renders a circular holographic ability badge with the hotkey letter inside the circle.
     */
    private void drawActionBadge(Graphics2D g2, int cx, int cy, String letter, Color themeColor, double cdPct, String cdText, boolean ready, String subLabel) {
        int r = 19; // radius

        // 1. Base dark circular body
        g2.setColor(new Color(12, 18, 32, 230));
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        if (!ready) {
            // Rotating Circular Cooldown Arc (fills clockwise)
            g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 55));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            g2.setColor(themeColor);
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int arcAngle = (int) (360.0 * (1.0 - cdPct));
            g2.drawArc(cx - r, cy - r, r * 2, r * 2, 90, -arcAngle);

            // Letter inside circle (upper portion)
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fmKey = g2.getFontMetrics();
            int kx = cx - fmKey.stringWidth(letter) / 2;
            g2.setColor(new Color(170, 185, 205));
            g2.drawString(letter, kx, cy - 2);

            // Cooldown seconds inside circle (lower portion)
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            FontMetrics fmSec = g2.getFontMetrics();
            int sx = cx - fmSec.stringWidth(cdText) / 2;
            g2.setColor(new Color(255, 225, 120));
            g2.drawString(cdText, sx, cy + 10);
        } else {
            // Pulsing Ready Halo around circle
            float pulse = (float) (0.7 + 0.3 * Math.sin(animationTick * 0.15));
            g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), (int) (pulse * 255)));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            // Inner soft ambient glow
            g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 45));
            g2.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

            // Prominent Hotkey Letter inside the center of circle!
            g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx - fm.stringWidth(letter) / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(letter, tx, cy + 6);
        }
    }

    /**
     * Renders the [U] Time Rewind badge as a circular gauge that fills up from 0 to 30.
     * No sublabel below the circle.
     */
    private void drawStackGaugeBadge(Graphics2D g2, int cx, int cy, String letter, Color themeColor, double fillPct, int count) {
        int r = 19; // radius

        // 1. Dark circular body
        g2.setColor(new Color(12, 18, 32, 230));
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        // 2. Track background ring
        g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 45));
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);

        if (count > 0) {
            // Circular Progress Arc filling clockwise as actions are pushed up to 30
            g2.setColor(themeColor);
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int arcAngle = (int) (360.0 * fillPct);
            g2.drawArc(cx - r, cy - r, r * 2, r * 2, 90, -arcAngle);

            // If completely full (30/30), subtle pulsing outer aura
            if (count >= 30) {
                float pulse = (float) (0.7 + 0.3 * Math.sin(animationTick * 0.15));
                g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), (int) (pulse * 200)));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - r - 2, cy - r - 2, (r + 2) * 2, (r + 2) * 2);
            }

            // Inner soft ambient glow
            g2.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), (int) (40 * fillPct)));
            g2.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);

            // Bold 'U' Letter inside center of circle
            g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx - fm.stringWidth(letter) / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(letter, tx, cy + 6);
        } else {
            // Empty stack: dim grey 'U'
            g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx - fm.stringWidth(letter) / 2;
            g2.setColor(new Color(100, 110, 130));
            g2.drawString(letter, tx, cy + 6);
        }
    }

    /**
     * Mouse listener for interactive menu buttons with smooth cursor states and clicks.
     */
    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int prevHover = hoveredButton;
                hoveredButton = HOVER_NONE;

                if (gameMode == GameMode.MENU) {
                    if (rectStartGame.contains(p)) {
                        hoveredButton = HOVER_START;
                    } else if (rectContinueGame.contains(p)) {
                        hoveredButton = HOVER_CONTINUE;
                    }
                } else if (gameMode == GameMode.PAUSED) {
                    if (rectResumeGame.contains(p)) {
                        hoveredButton = HOVER_RESUME;
                    } else if (rectReturnMenu.contains(p)) {
                        hoveredButton = HOVER_MENU;
                    } else if (rectRestartRound.contains(p)) {
                        hoveredButton = HOVER_RESTART;
                    }
                } else if (gameState != null && (gameState.isGameOver() || gameState.isVictory())) {
                    if (rectReturnMenu.contains(p)) {
                        hoveredButton = HOVER_MENU;
                    } else if (rectRestartRound.contains(p)) {
                        hoveredButton = HOVER_RESTART;
                    }
                }

                if (hoveredButton != HOVER_NONE) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }

                if (hoveredButton != prevHover) {
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                if (frame == null) return;

                if (gameMode == GameMode.MENU) {
                    if (rectStartGame.contains(p)) {
                        frame.startNewGame();
                    } else if (rectContinueGame.contains(p)) {
                        if (SaveManager.hasSave()) {
                            frame.continueGame();
                        } else {
                            showNotification("No saved progress found! Click 'START NEW GAME' to begin.");
                        }
                    }
                } else if (gameMode == GameMode.PAUSED) {
                    if (rectResumeGame.contains(p)) {
                        frame.resumeGame();
                    } else if (rectReturnMenu.contains(p)) {
                        frame.returnToMenu();
                    } else if (rectRestartRound.contains(p)) {
                        frame.restartGame();
                    }
                } else if (gameState != null && (gameState.isGameOver() || gameState.isVictory())) {
                    if (rectReturnMenu.contains(p)) {
                        frame.returnToMenu();
                    } else if (rectRestartRound.contains(p)) {
                        frame.restartGame();
                    }
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    /**
     * Renders the futuristic Start Screen Menu with glowing title, glassmorphic card,
     * START button, CONTINUE button, and live save info.
     */
    private void drawStartMenu(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0) w = GalaxyGameState.CANVAS_WIDTH;
        if (h <= 0) h = GalaxyGameState.CANVAS_HEIGHT;

        int cardW = Math.min(540, w - 40);
        int cardH = 380;
        int cardX = (w - cardW) / 2;
        int cardY = Math.max(120, (h - cardH) / 2 + 15);
        int titleY = cardY - 60;

        // 1. Neon Glowing Futuristic Title
        String title = "GALAXY DEFENDER";
        g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
        FontMetrics fmTitle = g2.getFontMetrics();
        int tx = (w - fmTitle.stringWidth(title)) / 2;

        // Multi-layer glowing drop shadows
        float pulse = (float) (0.65 + 0.35 * Math.sin(animationTick * 0.08));
        int glowAlpha = (int) (60 * pulse);
        g2.setColor(new Color(0, 220, 255, glowAlpha));
        g2.drawString(title, tx - 2, titleY);
        g2.drawString(title, tx + 2, titleY);
        g2.drawString(title, tx, titleY - 2);
        g2.drawString(title, tx, titleY + 2);

        // Core Title Text Gradient
        GradientPaint titleGrad = new GradientPaint(
                tx, titleY - 30, Color.WHITE,
                tx, titleY, new Color(0, 230, 255)
        );
        g2.setPaint(titleGrad);
        g2.drawString(title, tx, titleY);

        // Subtitle & Academic Tag
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String sub = "✦ DATA STRUCTURES & ALGORITHMS • SPACE COMBAT SIMULATOR ✦";
        FontMetrics fmSub = g2.getFontMetrics();
        int subX = (w - fmSub.stringWidth(sub)) / 2;
        g2.setColor(new Color(0, 210, 255, 230));
        g2.drawString(sub, subX, titleY + 24);

        // 2. Central Glassmorphic Command Card
        g2.setColor(new Color(8, 14, 28, 230));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Neon Border with Corner Accents
        g2.setColor(new Color(0, 190, 255, 175));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Header Label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String head = "✦ FLEET COMMAND TERMINAL • MISSION LAUNCH ✦";
        FontMetrics fmHead = g2.getFontMetrics();
        g2.setColor(new Color(140, 180, 220));
        g2.drawString(head, (w - fmHead.stringWidth(head)) / 2, cardY + 28);

        // Horizontal separator line
        g2.setColor(new Color(0, 190, 255, 60));
        g2.drawLine(cardX + 25, cardY + 38, cardX + cardW - 25, cardY + 38);

        // 3. START GAME BUTTON
        int btnW = cardW - 60;
        int btnH = 62;
        int btnX = cardX + 30;
        int startY = cardY + 54;
        rectStartGame.setBounds(btnX, startY, btnW, btnH);

        boolean hoverStart = (hoveredButton == HOVER_START);
        if (hoverStart) {
            // Glowing cyan button
            g2.setColor(new Color(0, 235, 255, 60));
            g2.fillRoundRect(btnX - 3, startY - 3, btnW + 6, btnH + 6, 18, 18);

            GradientPaint startGrad = new GradientPaint(btnX, startY, new Color(0, 185, 255), btnX, startY + btnH, new Color(0, 240, 255));
            g2.setPaint(startGrad);
            g2.fillRoundRect(btnX, startY, btnW, btnH, 14, 14);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(btnX, startY, btnW, btnH, 14, 14);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.setColor(new Color(5, 14, 28));
            String b1Text = "🚀  START NEW GAME";
            FontMetrics fmB1 = g2.getFontMetrics();
            g2.drawString(b1Text, btnX + (btnW - fmB1.stringWidth(b1Text)) / 2, startY + 28);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(8, 32, 54));
            String b1Sub = "Begin fresh expedition from Level 1 • Press [Enter] or [S]";
            FontMetrics fmB1s = g2.getFontMetrics();
            g2.drawString(b1Sub, btnX + (btnW - fmB1s.stringWidth(b1Sub)) / 2, startY + 48);
        } else {
            GradientPaint startGrad = new GradientPaint(btnX, startY, new Color(10, 36, 68, 220), btnX, startY + btnH, new Color(16, 56, 102, 240));
            g2.setPaint(startGrad);
            g2.fillRoundRect(btnX, startY, btnW, btnH, 14, 14);

            g2.setColor(new Color(0, 200, 255, 190));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(btnX, startY, btnW, btnH, 14, 14);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
            g2.setColor(Color.WHITE);
            String b1Text = "🚀  START NEW GAME";
            FontMetrics fmB1 = g2.getFontMetrics();
            g2.drawString(b1Text, btnX + (btnW - fmB1.stringWidth(b1Text)) / 2, startY + 28);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(0, 220, 255, 220));
            String b1Sub = "Begin fresh expedition from Level 1 • Press [Enter] or [S]";
            FontMetrics fmB1s = g2.getFontMetrics();
            g2.drawString(b1Sub, btnX + (btnW - fmB1s.stringWidth(b1Sub)) / 2, startY + 48);
        }

        // 4. CONTINUE GAME BUTTON
        int continueY = cardY + 130;
        rectContinueGame.setBounds(btnX, continueY, btnW, btnH);

        SaveManager.SaveData save = SaveManager.load();
        boolean hasSave = (save != null && save.round >= 1);
        boolean hoverContinue = (hoveredButton == HOVER_CONTINUE);

        if (hasSave) {
            if (hoverContinue) {
                // Glowing emerald button
                g2.setColor(new Color(50, 255, 140, 60));
                g2.fillRoundRect(btnX - 3, continueY - 3, btnW + 6, btnH + 6, 18, 18);

                GradientPaint contGrad = new GradientPaint(btnX, continueY, new Color(35, 220, 125), btnX, continueY + btnH, new Color(60, 255, 160));
                g2.setPaint(contGrad);
                g2.fillRoundRect(btnX, continueY, btnW, btnH, 14, 14);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(btnX, continueY, btnW, btnH, 14, 14);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new Color(4, 24, 14));
                String b2Text = "▶  CONTINUE MISSION (LEVEL " + save.round + ")";
                FontMetrics fmB2 = g2.getFontMetrics();
                g2.drawString(b2Text, btnX + (btnW - fmB2.stringWidth(b2Text)) / 2, continueY + 28);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(10, 44, 24));
                String b2Sub = "Resume Round " + save.round + " • Score: " + String.format("%,d", save.score) + " • Lives: x" + save.lives + " • Press [C]";
                FontMetrics fmB2s = g2.getFontMetrics();
                g2.drawString(b2Sub, btnX + (btnW - fmB2s.stringWidth(b2Sub)) / 2, continueY + 48);
            } else {
                GradientPaint contGrad = new GradientPaint(btnX, continueY, new Color(14, 52, 36, 220), btnX, continueY + btnH, new Color(20, 80, 52, 240));
                g2.setPaint(contGrad);
                g2.fillRoundRect(btnX, continueY, btnW, btnH, 14, 14);

                g2.setColor(new Color(50, 255, 130, 190));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(btnX, continueY, btnW, btnH, 14, 14);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
                g2.setColor(Color.WHITE);
                String b2Text = "▶  CONTINUE MISSION (LEVEL " + save.round + ")";
                FontMetrics fmB2 = g2.getFontMetrics();
                g2.drawString(b2Text, btnX + (btnW - fmB2.stringWidth(b2Text)) / 2, continueY + 28);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(60, 255, 150, 220));
                String b2Sub = "Resume Round " + save.round + " • Score: " + String.format("%,d", save.score) + " • Lives: x" + save.lives + " • Press [C]";
                FontMetrics fmB2s = g2.getFontMetrics();
                g2.drawString(b2Sub, btnX + (btnW - fmB2s.stringWidth(b2Sub)) / 2, continueY + 48);
            }
        } else {
            // Disabled style when no saved game exists
            GradientPaint contGrad = new GradientPaint(btnX, continueY, new Color(16, 22, 34, 180), btnX, continueY + btnH, new Color(22, 30, 44, 200));
            g2.setPaint(contGrad);
            g2.fillRoundRect(btnX, continueY, btnW, btnH, 14, 14);

            g2.setColor(new Color(70, 82, 102, 130));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(btnX, continueY, btnW, btnH, 14, 14);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 17));
            g2.setColor(new Color(110, 125, 145));
            String b2Text = "▶  CONTINUE  (NO SAVE FOUND)";
            FontMetrics fmB2 = g2.getFontMetrics();
            g2.drawString(b2Text, btnX + (btnW - fmB2.stringWidth(b2Text)) / 2, continueY + 28);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(80, 95, 115));
            String b2Sub = "Start a mission to auto-save level progression";
            FontMetrics fmB2s = g2.getFontMetrics();
            g2.drawString(b2Sub, btnX + (btnW - fmB2s.stringWidth(b2Sub)) / 2, continueY + 48);
        }

        // 5. ALL-TIME HIGH SCORE BADGE
        int highY = cardY + 210;
        int highH = 36;
        g2.setColor(new Color(32, 26, 12, 210));
        g2.fillRoundRect(btnX, highY, btnW, highH, 12, 12);
        g2.setColor(new Color(255, 215, 0, 160));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(btnX, highY, btnW, highH, 12, 12);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        String highText = "🏆 ALL-TIME HIGH SCORE: " + String.format("%,d", SaveManager.getHighScore());
        FontMetrics fmHigh = g2.getFontMetrics();
        g2.setColor(new Color(255, 225, 110));
        g2.drawString(highText, btnX + (btnW - fmHigh.stringWidth(highText)) / 2, highY + 23);

        // 6. DSA CAPABILITIES PREVIEW
        int dsaY = cardY + 264;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        String dsaLabel = "INTEGRATED DATA STRUCTURES & ALGORITHMS (KDU IT22013):";
        FontMetrics fmDsa = g2.getFontMetrics();
        g2.setColor(new Color(130, 160, 200));
        g2.drawString(dsaLabel, btnX + (btnW - fmDsa.stringWidth(dsaLabel)) / 2, dsaY);

        // Badges Bar
        int badgeH = 26;
        int badgeW = (btnW - 15) / 4;
        String[] badges = {"⚡ [E] Recursion", "🎯 [X] Binary Search", "📊 [T] Insertion Sort", "⏪ [U] Stack Rewind"};
        Color[] badgeColors = {COLOR_NEON_CYAN, COLOR_NEON_GOLD, COLOR_NEON_LIME, new Color(195, 115, 255)};

        for (int i = 0; i < 4; i++) {
            int bx = btnX + i * (badgeW + 5);
            int by = dsaY + 12;

            g2.setColor(new Color(12, 18, 32, 210));
            g2.fillRoundRect(bx, by, badgeW, badgeH, 8, 8);
            g2.setColor(new Color(badgeColors[i].getRed(), badgeColors[i].getGreen(), badgeColors[i].getBlue(), 120));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(bx, by, badgeW, badgeH, 8, 8);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            FontMetrics fmB = g2.getFontMetrics();
            g2.setColor(badgeColors[i]);
            g2.drawString(badges[i], bx + (badgeW - fmB.stringWidth(badges[i])) / 2, by + 17);
        }

        // 7. FOOTER CONTROLS REFERENCE
        int footY = cardY + cardH + 34;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String footer = "Controls: [W A S D / Arrows] Fly • [SPACE] Plasma Lasers • [ESC / P] Pause & Auto-Save • [F11] Fullscreen";
        FontMetrics fmFoot = g2.getFontMetrics();
        g2.setColor(new Color(140, 175, 215));
        g2.drawString(footer, (w - fmFoot.stringWidth(footer)) / 2, footY);
    }

    /**
     * Renders the Pause Dialog with resume, return to menu, and restart round options.
     */
    private void drawPauseMenu(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        // Darkened glassmorphic backdrop
        g2.setColor(new Color(4, 7, 16, 215));
        g2.fillRect(0, 0, w, h);

        int cardW = Math.min(480, w - 40);
        int cardH = 320;
        int cardX = (w - cardW) / 2;
        int cardY = (h - cardH) / 2;

        // Card body
        g2.setColor(new Color(12, 18, 34, 240));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g2.setColor(new Color(255, 195, 0, 180));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        // Header Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        String pTitle = "⏸  MISSION SUSPENDED";
        FontMetrics fmP = g2.getFontMetrics();
        g2.setColor(new Color(255, 215, 50));
        g2.drawString(pTitle, (w - fmP.stringWidth(pTitle)) / 2, cardY + 45);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String pSub = "Sector Round " + gameState.getCurrentRound() + " • Progress Auto-Saved to Local Storage!";
        FontMetrics fmPs = g2.getFontMetrics();
        g2.setColor(new Color(200, 225, 255));
        g2.drawString(pSub, (w - fmPs.stringWidth(pSub)) / 2, cardY + 70);

        // Buttons
        int btnW = cardW - 70;
        int btnH = 48;
        int btnX = cardX + 35;

        // 1. Resume
        int resumeY = cardY + 95;
        rectResumeGame.setBounds(btnX, resumeY, btnW, btnH);
        drawMenuButton(g2, rectResumeGame, "▶  RESUME COMBAT  [ESC / P]", COLOR_NEON_CYAN, hoveredButton == HOVER_RESUME);

        // 2. Return to Menu
        int menuY = cardY + 155;
        rectReturnMenu.setBounds(btnX, menuY, btnW, btnH);
        drawMenuButton(g2, rectReturnMenu, "🏠  SAVE & RETURN TO TITLE MENU  [M]", new Color(255, 160, 40), hoveredButton == HOVER_MENU);

        // 3. Restart Round
        int restartY = cardY + 215;
        rectRestartRound.setBounds(btnX, restartY, btnW, btnH);
        drawMenuButton(g2, rectRestartRound, "🔄  RESTART ROUND  [R]", new Color(195, 115, 255), hoveredButton == HOVER_RESTART);
    }

    private void drawGameOverMenu(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(16, 4, 8, 225));
        g2.fillRect(0, 0, w, h);

        int cardW = Math.min(480, w - 40);
        int cardH = 310;
        int cardX = (w - cardW) / 2;
        int cardY = (h - cardH) / 2;

        g2.setColor(new Color(24, 10, 16, 240));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g2.setColor(COLOR_NEON_RED);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
        String title = "MISSION FAILED";
        FontMetrics fmT = g2.getFontMetrics();
        g2.setColor(COLOR_NEON_RED);
        g2.drawString(title, (w - fmT.stringWidth(title)) / 2, cardY + 45);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        String sub = "Round " + gameState.getCurrentRound() + " • Final Score: " + String.format("%,d", gameState.getPlayer().getScore());
        FontMetrics fmS = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.drawString(sub, (w - fmS.stringWidth(sub)) / 2, cardY + 72);

        int btnW = cardW - 70;
        int btnH = 48;
        int btnX = cardX + 35;

        // Restart Round
        int restartY = cardY + 105;
        rectRestartRound.setBounds(btnX, restartY, btnW, btnH);
        drawMenuButton(g2, rectRestartRound, "🔄  RESTART MISSION  [R]", COLOR_NEON_CYAN, hoveredButton == HOVER_RESTART);

        // Return to Menu
        int menuY = cardY + 165;
        rectReturnMenu.setBounds(btnX, menuY, btnW, btnH);
        drawMenuButton(g2, rectReturnMenu, "🏠  RETURN TO TITLE MENU  [M]", new Color(255, 160, 40), hoveredButton == HOVER_MENU);

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        String tip = "Tip: Press 'U' to Time-Rewind (LIFO Stack Temporal Damage Reversal)";
        FontMetrics fmTip = g2.getFontMetrics();
        g2.setColor(new Color(200, 180, 220));
        g2.drawString(tip, (w - fmTip.stringWidth(tip)) / 2, cardY + 245);
    }

    private void drawVictoryMenu(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(4, 16, 10, 225));
        g2.fillRect(0, 0, w, h);

        int cardW = Math.min(480, w - 40);
        int cardH = 300;
        int cardX = (w - cardW) / 2;
        int cardY = (h - cardH) / 2;

        g2.setColor(new Color(10, 24, 16, 240));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g2.setColor(COLOR_NEON_LIME);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
        String title = "GALAXY CONQUERED!";
        FontMetrics fmT = g2.getFontMetrics();
        g2.setColor(COLOR_NEON_LIME);
        g2.drawString(title, (w - fmT.stringWidth(title)) / 2, cardY + 45);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        String sub = "All Alien Invasions Eliminated! Final Score: " + String.format("%,d", gameState.getPlayer().getScore());
        FontMetrics fmS = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.drawString(sub, (w - fmS.stringWidth(sub)) / 2, cardY + 72);

        int btnW = cardW - 70;
        int btnH = 48;
        int btnX = cardX + 35;

        int menuY = cardY + 110;
        rectReturnMenu.setBounds(btnX, menuY, btnW, btnH);
        drawMenuButton(g2, rectReturnMenu, "🏠  RETURN TO TITLE MENU  [M]", COLOR_NEON_CYAN, hoveredButton == HOVER_MENU);

        int restartY = cardY + 170;
        rectRestartRound.setBounds(btnX, restartY, btnW, btnH);
        drawMenuButton(g2, rectRestartRound, "🚀  PLAY AGAIN FROM ROUND 1  [R]", COLOR_NEON_LIME, hoveredButton == HOVER_RESTART);
    }

    private void drawMenuButton(Graphics2D g2, Rectangle r, String text, Color themeColor, boolean hovered) {
        if (hovered) {
            GradientPaint grad = new GradientPaint(r.x, r.y, themeColor, r.x, r.y + r.height, Color.WHITE);
            g2.setPaint(grad);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(new Color(6, 14, 24));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2, r.y + 30);
        } else {
            g2.setColor(new Color(themeColor.getRed() / 4, themeColor.getGreen() / 4, themeColor.getBlue() / 4, 190));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            g2.setColor(themeColor);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2, r.y + 29);
        }
    }
}
