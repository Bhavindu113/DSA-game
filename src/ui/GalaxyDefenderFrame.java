package ui;

import model.GalaxyGameState;
import model.GameMode;
import model.PlayerShip;
import model.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GalaxyDefenderFrame - Main Game Frame for Galaxy Defender: DSA Edition.
 * Fully integrates Data Structures & Algorithms directly into real-time arcade gameplay
 * without artificial pop-up boxes or typing interruptions:
 * 
 * - [E] Key: Recursive Chain Lightning Weapon (Lesson 1: Recursion)
 * - [X] Key: Smart Homing Missile Target Lock (Lesson 1 & 2: Binary Search O(log N))
 * - [T] Key: Alien Formation Sorting (Lesson 9: Insertion Sort)
 * - [U] Key: Action-Driven Time-Rewind Temporal Undo (Lesson 5: LIFO Stack)
 * - [SPACE]: Continuous Laser Projectiles (Lesson 3: Dynamic Singly Linked List)
 * - [F11]:   Toggle Borderless Fullscreen / Windowed Mode
 */
public class GalaxyDefenderFrame extends JFrame {
    private GalaxyGameState gameState;
    private SpaceCombatCanvas canvas;

    // HUD Labels
    private JLabel lblScore;
    private JLabel lblShield;
    private JLabel lblAmmo;
    private JLabel lblRound;

    // Key press flags
    private boolean keyLeft = false;
    private boolean keyRight = false;
    private boolean keyUp = false;
    private boolean keyDown = false;
    private boolean keyShoot = false;
    private long lastShotTime = 0;
    private boolean isExclusiveFullscreen = false;

    private Timer gameLoopTimer;

    public GalaxyDefenderFrame() {
        setTitle("Galaxy Defender: DSA Edition (KDU IT22013)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(10, 14, 22));

        this.gameState = new GalaxyGameState();

        initMenuBar();
        initTopHUD();

        // Canvas is the clean, full-screen center element
        canvas = new SpaceCombatCanvas(gameState, this);
        canvas.setGameMode(GameMode.MENU);
        add(canvas, BorderLayout.CENTER);

        // Auto-save on window exit if playing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (canvas != null && canvas.getGameMode() == GameMode.PLAYING && gameState != null && !gameState.isGameOver()) {
                    gameState.saveCurrentState();
                }
            }
        });

        setupKeyListeners();
        startGameLoop();

        pack();
        setSize(980, 780);
        setMinimumSize(new Dimension(800, 650));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);

        canvas.requestFocusInWindow();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(20, 25, 38));

        JMenu gameMenu = new JMenu("System");
        gameMenu.setForeground(Color.WHITE);

        JMenuItem itemStart = new JMenuItem("🚀 Start New Game (S / Enter)");
        itemStart.addActionListener(e -> startNewGame());

        JMenuItem itemContinue = new JMenuItem("▶ Continue Saved Game (C)");
        itemContinue.addActionListener(e -> continueGame());

        JMenuItem itemSave = new JMenuItem("💾 Save Current Progress (P)");
        itemSave.addActionListener(e -> {
            if (gameState != null) {
                gameState.saveCurrentState();
                canvas.showNotification("💾 Progress safely auto-saved to local disk!");
            }
        });

        JMenuItem itemMenu = new JMenuItem("🏠 Return to Title Menu (ESC / M)");
        itemMenu.addActionListener(e -> returnToMenu());

        JMenuItem itemFullscreen = new JMenuItem("Toggle Fullscreen (F11)");
        itemFullscreen.addActionListener(e -> toggleFullscreen());

        JMenuItem itemRestart = new JMenuItem("Restart Round (R)");
        itemRestart.addActionListener(e -> restartGame());

        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(e -> {
            if (canvas != null && canvas.getGameMode() == GameMode.PLAYING && gameState != null && !gameState.isGameOver()) {
                gameState.saveCurrentState();
            }
            System.exit(0);
        });

        gameMenu.add(itemStart);
        gameMenu.add(itemContinue);
        gameMenu.add(itemSave);
        gameMenu.add(itemMenu);
        gameMenu.addSeparator();
        gameMenu.add(itemFullscreen);
        gameMenu.add(itemRestart);
        gameMenu.addSeparator();
        gameMenu.add(itemExit);

        // DSA Operations Menu (Direct trigger without typing or popups)
        JMenu dsaMenu = new JMenu("DSA Weapons & Actions");
        dsaMenu.setForeground(Color.WHITE);

        JMenuItem itemLightning = new JMenuItem("⚡ Recursive Chain Lightning (E)");
        itemLightning.addActionListener(e -> triggerChainLightning());

        JMenuItem itemMissile = new JMenuItem("🎯 Smart Missile - Binary Search (X)");
        itemMissile.addActionListener(e -> triggerSmartMissile());

        JMenuItem itemSort = new JMenuItem("📊 Fleet Formation - Insertion Sort (T)");
        itemSort.addActionListener(e -> triggerFormationSort());

        JMenuItem itemRewind = new JMenuItem("⏪ Time Rewind - LIFO Stack (U)");
        itemRewind.addActionListener(e -> triggerTimeRewind());

        JMenuItem itemReportGuide = new JMenuItem("📋 View Assignment DSA Mapping (Viva)");
        itemReportGuide.addActionListener(e -> showDsaMappingDialog());

        dsaMenu.add(itemLightning);
        dsaMenu.add(itemMissile);
        dsaMenu.add(itemSort);
        dsaMenu.add(itemRewind);
        dsaMenu.addSeparator();
        dsaMenu.add(itemReportGuide);

        menuBar.add(gameMenu);
        menuBar.add(dsaMenu);
        setJMenuBar(menuBar);
    }

    private void initTopHUD() {
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        topPanel.setBackground(new Color(14, 18, 28));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 180, 255)));
        topPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 48));

        lblScore = createHudLabel("SCORE", "0", new Color(0, 230, 255));
        lblShield = createHudLabel("SHIELD", "100%", new Color(52, 152, 219));
        lblAmmo = createHudLabel("PLASMA AMMO", "100 / 100", new Color(46, 204, 113));
        lblRound = createHudLabel("ROUND", "1", new Color(241, 196, 15));

        topPanel.add(lblScore);
        topPanel.add(lblShield);
        topPanel.add(lblAmmo);
        topPanel.add(lblRound);

        add(topPanel, BorderLayout.NORTH);
    }

    private void setupKeyListeners() {
        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                GameMode mode = canvas.getGameMode();

                // 1. Menu Controls
                if (mode == GameMode.MENU) {
                    if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_S) startNewGame();
                    if (code == KeyEvent.VK_C) continueGame();
                    if (code == KeyEvent.VK_F11) toggleFullscreen();
                    return;
                }

                // 2. Paused Controls
                if (mode == GameMode.PAUSED) {
                    if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_P) resumeGame();
                    if (code == KeyEvent.VK_M) returnToMenu();
                    if (code == KeyEvent.VK_R) restartGame();
                    if (code == KeyEvent.VK_F11) toggleFullscreen();
                    return;
                }

                // 3. Game Over / Victory Controls
                if (gameState.isGameOver() || gameState.isVictory()) {
                    if (code == KeyEvent.VK_M) returnToMenu();
                    if (code == KeyEvent.VK_R) restartGame();
                    if (code == KeyEvent.VK_U || code == KeyEvent.VK_Z) triggerTimeRewind();
                    if (code == KeyEvent.VK_F11) toggleFullscreen();
                    return;
                }

                // 4. In-Combat Pause Toggle
                if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_P) {
                    pauseGame();
                    return;
                }

                // 4-Way Smooth Navigation: Left, Right, Forward (Up), Backward (Down)
                if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) keyLeft = true;
                if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) keyRight = true;
                if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) keyUp = true;
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) keyDown = true;
                if (code == KeyEvent.VK_SPACE) keyShoot = true;

                // Natural In-Game DSA Weapon & Action Triggers
                if (code == KeyEvent.VK_E) triggerChainLightning();
                if (code == KeyEvent.VK_X || code == KeyEvent.VK_F) triggerSmartMissile();
                if (code == KeyEvent.VK_T || code == KeyEvent.VK_Q) triggerFormationSort();
                if (code == KeyEvent.VK_U || code == KeyEvent.VK_Z) triggerTimeRewind();
                if (code == KeyEvent.VK_R) restartGame();
                if (code == KeyEvent.VK_F11) toggleFullscreen();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) keyLeft = false;
                if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) keyRight = false;
                if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) keyUp = false;
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) keyDown = false;
                if (code == KeyEvent.VK_SPACE) keyShoot = false;
            }
        };

        canvas.addKeyListener(adapter);
        this.addKeyListener(adapter);
    }

    private void startGameLoop() {
        gameLoopTimer = new Timer(16, e -> {
            GameMode mode = canvas.getGameMode();

            // When in Title Menu, softly drift starfield in background
            if (mode == GameMode.MENU) {
                double[][] stars = gameState.getStars();
                if (stars != null) {
                    int ch = canvas.getHeight();
                    int cw = canvas.getWidth();
                    if (ch <= 0) ch = GalaxyGameState.CANVAS_HEIGHT;
                    if (cw <= 0) cw = GalaxyGameState.CANVAS_WIDTH;
                    for (int i = 0; i < stars.length; i++) {
                        stars[i][1] += stars[i][2] * 0.7;
                        if (stars[i][1] > ch) {
                            stars[i][1] = 0;
                            stars[i][0] = Math.random() * cw;
                        }
                    }
                }
                canvas.repaint();
                refreshHUD();
                return;
            }

            // When Paused, freeze combat simulation
            if (mode == GameMode.PAUSED) {
                canvas.repaint();
                return;
            }

            PlayerShip player = gameState.getPlayer();
            int cw = canvas.getWidth();
            int ch = canvas.getHeight();
            if (cw <= 0) cw = GalaxyGameState.CANVAS_WIDTH;
            if (ch <= 0) ch = GalaxyGameState.CANVAS_HEIGHT;

            double spd = player.getSpeed();
            double px = player.getX();
            double py = player.getY();

            // 4-Way Steering: Forward, Backward, Left, Right
            if (keyLeft) px -= spd;
            if (keyRight) px += spd;
            if (keyUp) py -= spd;   // Move forward towards alien fleet
            if (keyDown) py += spd; // Move backward towards lower sector

            // Keep player safely within visible combat zone (leaving clearance for hotkey dock on right)
            px = Math.max(10, Math.min(cw - player.getWidth() - 80, px));
            py = Math.max(90, Math.min(ch - player.getHeight() - 15, py));
            player.setPosition(px, py);

            if (keyShoot && System.currentTimeMillis() - lastShotTime > 180) {
                player.fireLaser();
                lastShotTime = System.currentTimeMillis();
            }

            gameState.update(cw, ch);
            canvas.repaint();
            refreshHUD();
        });
        gameLoopTimer.start();
    }

    public void refreshHUD() {
        GameMode mode = canvas.getGameMode();
        if (mode == GameMode.MENU) {
            updateHudValue(lblScore, String.format("%,d", SaveManager.getHighScore()));
            SaveManager.SaveData save = SaveManager.load();
            String savedText = (save != null) ? ("Round " + save.round) : "None";
            updateHudValue(lblShield, savedText);
            lblAmmo.setText("<html><center><font color='#8899aa' size='2'>SYSTEM</font><br><font color='#00e6ff' size='3'><b>READY TO LAUNCH</b></font></center></html>");
            updateHudValue(lblRound, "MENU");
            return;
        }

        PlayerShip p = gameState.getPlayer();
        updateHudValue(lblScore, String.format("%,d", p.getScore()));
        updateHudValue(lblShield, p.getShield() + "% (x" + p.getLives() + ")");

        // Dynamic Plasma Ammo Status & Color Warnings
        int ammo = p.getAmmo();
        int maxAmmo = p.getMaxAmmo();
        String ammoColor = (ammo > 40) ? "#2ecc71" : ((ammo > 15) ? "#f39c12" : "#e74c3c");
        lblAmmo.setText("<html><center><font color='#8899aa' size='2'>PLASMA AMMO</font><br><font color='" 
                + ammoColor + "' size='3'><b>" + ammo + " / " + maxAmmo + "</b></font></center></html>");

        updateHudValue(lblRound, String.valueOf(gameState.getCurrentRound()));
    }

    /**
     * Practical Recursive Weapon: Fires Chain Lightning arcing across up to 4 enemies!
     */
    private void triggerChainLightning() {
        boolean fired = gameState.fireRecursiveChainLightning();
        if (fired) {
            canvas.showNotification("⚡ RECURSIVE CHAIN LIGHTNING FIRED! Jumps across multiple enemies.");
        } else if (gameState.getLightningCooldown() > 0) {
            canvas.showNotification("Lightning EMP is recharging...");
        } else {
            canvas.showNotification("No targets in firing vector!");
        }
    }

    /**
     * Practical Binary Search Weapon: Smart Homing Missile locks closest target in O(log N)!
     */
    private void triggerSmartMissile() {
        boolean fired = gameState.fireSmartHomingMissile();
        if (fired) {
            canvas.showNotification("🎯 SMART MISSILE: Target Locked via Binary Search O(log N)!");
        } else if (gameState.getMissileCooldown() > 0) {
            canvas.showNotification("Missile launcher is reloading...");
        } else {
            canvas.showNotification("No targets detected on radar.");
        }
    }

    /**
     * Practical Sorting: Organizes alien fleet formation dynamically via Insertion Sort!
     */
    private void triggerFormationSort() {
        boolean sorted = gameState.sortFormationByThreat();
        if (sorted) {
            canvas.showNotification("📊 INSERTION SORT: Alien Fleet Re-aligned by Threat Level!");
        } else {
            canvas.showNotification("No alien ships active to sort.");
        }
    }

    /**
     * Practical Stack: Pops LIFO stack frame and rewinds time!
     */
    private void triggerTimeRewind() {
        boolean success = gameState.executeTimeRewind();
        if (success) {
            canvas.showNotification("⏪ TIME REWIND: LIFO Stack Popped! Shield Restored.");
        } else {
            canvas.showNotification("Rewind Stack is empty! Fly for a few seconds first.");
        }
    }

    public void startNewGame() {
        resetKeyStates();
        this.gameState = new GalaxyGameState(1, 0, 100, 100, 3);
        canvas.setGameState(gameState);
        canvas.setGameMode(GameMode.PLAYING);
        SaveManager.save(1, 0, 100, 100, 3);
        canvas.showNotification("🚀 MISSION STARTED: Round 1 | Defend the Sector!");
        canvas.requestFocusInWindow();
    }

    public void continueGame() {
        resetKeyStates();
        SaveManager.SaveData save = SaveManager.load();
        if (save != null && save.round >= 1) {
            this.gameState = new GalaxyGameState(save.round, save.score, save.shield, save.ammo, save.lives);
            canvas.setGameState(gameState);
            canvas.setGameMode(GameMode.PLAYING);
            canvas.showNotification("▶ MISSION RESUMED: Round " + save.round + " | Score: " + String.format("%,d", save.score));
        } else {
            startNewGame();
        }
        canvas.requestFocusInWindow();
    }

    public void pauseGame() {
        if (canvas.getGameMode() == GameMode.PLAYING && !gameState.isGameOver() && !gameState.isVictory()) {
            resetKeyStates();
            gameState.saveCurrentState();
            canvas.setGameMode(GameMode.PAUSED);
        }
    }

    public void resumeGame() {
        resetKeyStates();
        canvas.setGameMode(GameMode.PLAYING);
        canvas.requestFocusInWindow();
    }

    public void returnToMenu() {
        resetKeyStates();
        if (gameState != null && !gameState.isGameOver() && !gameState.isVictory()) {
            gameState.saveCurrentState();
        }
        canvas.setGameMode(GameMode.MENU);
        canvas.requestFocusInWindow();
    }

    public void restartGame() {
        resetKeyStates();
        int targetRound = (gameState != null) ? gameState.getCurrentRound() : 1;
        this.gameState = new GalaxyGameState(targetRound, 0, 100, 100, 3);
        canvas.setGameState(gameState);
        canvas.setGameMode(GameMode.PLAYING);
        canvas.requestFocusInWindow();
    }

    private void resetKeyStates() {
        keyLeft = false;
        keyRight = false;
        keyUp = false;
        keyDown = false;
        keyShoot = false;
    }

    /**
     * Toggles between borderless true Fullscreen and Standard Windowed mode.
     */
    private void toggleFullscreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        dispose();
        if (!isExclusiveFullscreen) {
            setUndecorated(true);
            try {
                gd.setFullScreenWindow(this);
            } catch (Exception ex) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            isExclusiveFullscreen = true;
        } else {
            try {
                gd.setFullScreenWindow(null);
            } catch (Exception ignored) {}
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            setSize(980, 780);
            setLocationRelativeTo(null);
            isExclusiveFullscreen = false;
        }
        setVisible(true);

        // Keep all ships strictly inside canvas bounds after resizing
        int cw = canvas.getWidth();
        int ch = canvas.getHeight();
        if (cw > 0 && ch > 0) {
            gameState.update(cw, ch);
        }

        canvas.requestFocusInWindow();
    }

    private JLabel createHudLabel(String title, String val, Color valColor) {
        JLabel lbl = new JLabel("<html><center><font color='#8899aa' size='2'>" + title + "</font><br><font color='" 
                + toHex(valColor) + "' size='3'><b>" + val + "</b></font></center></html>", JLabel.CENTER);
        return lbl;
    }

    private void updateHudValue(JLabel lbl, String val) {
        String html = lbl.getText();
        int bStart = html.indexOf("<b>");
        int bEnd = html.indexOf("</b>");
        if (bStart != -1 && bEnd != -1) {
            String newHtml = html.substring(0, bStart + 3) + val + html.substring(bEnd);
            lbl.setText(newHtml);
        }
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private void showDsaMappingDialog() {
        String msg = "KDU IT22013 DSA Group Assignment Implementation Details:\n\n"
                + "1. Round Queue (Lesson 4 - FIFO Circular Queue):\n"
                + "   - Alien waves are scheduled and deployed round-by-round from CustomCircularQueue.\n\n"
                + "2. Time Rewind Stack (Lesson 5 - LIFO Stack):\n"
                + "   - Starship state snapshots are pushed to CustomStack and popped on 'U' (Time Rewind).\n\n"
                + "3. Laser Projectiles (Lesson 3 - Singly Linked List):\n"
                + "   - Lasers are dynamically inserted into CustomLinkedList and deleted on impact.\n\n"
                + "4. Fleet Formation Sorting (Lesson 9 - Insertion Sort):\n"
                + "   - Aliens dynamically re-order their battle ranks descending by threat (HP * Attack).\n\n"
                + "5. Smart Missile Radar (Lesson 1 & 2 - Binary Search):\n"
                + "   - When firing homing missiles (X), target coordinate is found via Binary Search in O(log N).\n\n"
                + "6. Recursive Chain Lightning Weapon (Lesson 1 - Recursion):\n"
                + "   - Special EMP weapon (E) recursively jumps across up to 4 nearby enemies.";
        JOptionPane.showMessageDialog(this, msg, "DSA Concept Mapping", JOptionPane.INFORMATION_MESSAGE);
    }
}
