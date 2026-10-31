package model;

import algorithms.RecursionHelper;
import algorithms.SearchingAlgorithms;
import algorithms.SortingAlgorithms;
import datastructures.CustomCircularQueue;
import datastructures.CustomLinkedList;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * GalaxyGameState - Coordinates the Space Combat simulation and DSA structures.
 * Architectural backbone for General Sir John Kotelawala Defence University (KDU)
 * IT22013 Data Structures & Algorithms Group Assignment (Intake 42).
 * 
 * Group Member Allocation & Integration (Equal 20% Contribution):
 * - Member A (ID: IT/IS/42/001): CustomLinkedList<Laser> dynamic projectile tracking (Lesson 3)
 * - Member B (ID: IT/IS/42/002): CustomStack<RewindFrame> action-driven LIFO undo (Lesson 5)
 * - Member C (ID: IT/IS/42/003): CustomCircularQueue<AlienEnemy> FIFO round scheduling (Lesson 4)
 * - Member D (ID: IT/IS/42/004): SortingAlgorithms (Insertion Sort) fleet threat hierarchy (Lesson 9)
 * - Member E (ID: IT/IS/42/005): SearchingAlgorithms (Binary Search O(log N)) & RecursionHelper (Lesson 1 & 2)
 * 
 * Multidimensional Arrays:
 * - double[150][3] Starfield parallax depth simulation (Lesson 2)
 */
public class GalaxyGameState {
    public static final int CANVAS_WIDTH = 960;
    public static final int CANVAS_HEIGHT = 800;

    public int canvasWidth = CANVAS_WIDTH;
    public int canvasHeight = CANVAS_HEIGHT;

    private PlayerShip player;
    private CustomCircularQueue<AlienEnemy> roundQueue;
    private List<AlienEnemy> activeEnemies;
    private List<Laser> enemyLasers;
    private List<HomingMissile> activeMissiles;
    private List<Point2D[]> activeLightningArcs;
    private int lightningTimer = 0;

    // Cooldown ticks (60 ticks = 1 sec)
    private int lightningCooldown = 0;
    private int missileCooldown = 0;

    private int currentRound;
    private boolean gameOver;
    private boolean victory;
    private List<String> eventLogs;
    private long tickCounter;

    // 2D Array for Starfield (Lesson 2) - 150 stars for immersive Fullscreen
    private double[][] stars;

    public GalaxyGameState() {
        this(1, 0, 100, 100, 3);
    }

    public GalaxyGameState(int startingRound, int score, int shield, int ammo, int lives) {
        this.eventLogs = new ArrayList<>();
        this.activeEnemies = new ArrayList<>();
        this.enemyLasers = new ArrayList<>();
        this.activeMissiles = new ArrayList<>();
        this.activeLightningArcs = new ArrayList<>();
        this.roundQueue = new CustomCircularQueue<>(25);
        this.currentRound = Math.max(1, Math.min(5, startingRound));
        this.gameOver = false;
        this.victory = false;
        this.tickCounter = 0;

        initStars();
        initPlayer();
        if (player != null) {
            player.setScore(score);
            player.setShield(shield);
            player.setAmmo(ammo);
            player.setLives(lives);
        }
        prepareRoundQueue(this.currentRound);
        deployRound();
    }

    private void initStars() {
        stars = new double[150][3];
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = Math.random() * 1920;
            stars[i][1] = Math.random() * 1080;
            stars[i][2] = 0.5 + Math.random() * 2.5;
        }
    }

    public void initPlayer() {
        player = new PlayerShip(canvasWidth / 2.0 - 27, canvasHeight - 100);
    }

    public void prepareRoundQueue(int round) {
        roundQueue.clear();
        this.currentRound = round;

        int idBase = round * 100;
        roundQueue.enqueue(new AlienEnemy(idBase + 10, "Grunt", 70, 55, 40, 10, 100));
        roundQueue.enqueue(new AlienEnemy(idBase + 20, "Heavy", 170, 55, 90, 20, 200));
        roundQueue.enqueue(new AlienEnemy(idBase + 30, "Grunt", 270, 55, 40, 10, 100));
        roundQueue.enqueue(new AlienEnemy(idBase + 40, "Heavy", 370, 55, 90, 20, 200));
        roundQueue.enqueue(new AlienEnemy(idBase + 50, "Swarm", 470, 55, 30, 8, 120));

        if (round >= 2) {
            roundQueue.enqueue(new AlienEnemy(idBase + 99, "Boss", 240, 35, 250, 35, 1000));
        }

        log("Round " + round + " queued with " + roundQueue.size() + " invaders.");
    }

    public void deployRound() {
        while (!roundQueue.isEmpty()) {
            AlienEnemy enemy = roundQueue.dequeue();
            if (enemy != null) {
                activeEnemies.add(enemy);
            }
        }
        // Organize formation initially via Insertion Sort
        sortFormationByThreat();
    }

    /**
     * Practical Insertion Sort (Lesson 9):
     * Dynamically organizes the alien fleet formation based on Threat Level.
     */
    public boolean sortFormationByThreat() {
        if (activeEnemies.isEmpty()) return false;

        AlienEnemy[] arr = activeEnemies.toArray(new AlienEnemy[0]);
        // Sort descending by Threat Level using Insertion Sort
        SortingAlgorithms.insertionSort(arr, (a, b) -> Integer.compare(b.getThreatLevel(), a.getThreatLevel()));

        activeEnemies.clear();
        activeEnemies.addAll(Arrays.asList(arr));

        // Smoothly position them in neat formation ranks (clear of hotkey dock on right)
        int startX = 60;
        int availableW = canvasWidth - 170;
        int spacing = availableW / Math.max(1, arr.length);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getType().equalsIgnoreCase("Boss")) {
                arr[i].setPosition((availableW + startX) / 2.0 - arr[i].getWidth() / 2.0, 40);
            } else {
                arr[i].setPosition(startX + i * spacing, 60 + (i % 2) * 50);
            }
        }
        log("Formation dynamically re-ordered via Insertion Sort.");
        return true;
    }

    /**
     * Practical Recursive Weapon (Lesson 1):
     * Fires Chain Lightning that recursively zaps through multiple adjacent targets!
     */
    public boolean fireRecursiveChainLightning() {
        if (lightningCooldown > 0 || activeEnemies.isEmpty()) {
            return false;
        }

        lightningCooldown = 150; // 2.5 seconds cooldown
        activeLightningArcs.clear();
        lightningTimer = 25; // Visible for ~25 frames

        // Action-Driven Undo: Record state before special weapon release
        player.recordStateForRewind(activeEnemies.size());

        // Find initial enemy closest to center vector
        AlienEnemy first = SearchingAlgorithms.findClosestTargetByBinarySearch(activeEnemies, player.getX() + player.getWidth() / 2.0);
        if (first != null) {
            // First lightning bolt from starship to first enemy
            Point2D starshipNose = new Point2D.Double(player.getX() + player.getWidth() / 2.0, player.getY());
            Point2D firstEnemyCenter = new Point2D.Double(first.getX() + first.getWidth() / 2.0, first.getY() + first.getHeight() / 2.0);
            activeLightningArcs.add(new Point2D[]{starshipNose, firstEnemyCenter});

            // Trigger Recursive Chain Lightning propagation (Jumps through 4 enemies recursively)
            Set<AlienEnemy> visited = new HashSet<>();
            RecursionHelper.propagateChainLightning(first, activeEnemies, visited, 4, 75, activeLightningArcs);

            player.addScore(visited.size() * 150);
            log("Recursive Chain Lightning zapped " + visited.size() + " alien targets!");
            return true;
        }
        return false;
    }

    /**
     * Practical Binary Search Weapon (Lesson 1 & 2):
     * Launches a Smart Homing Missile that locates its target in O(log N) via Binary Search!
     */
    public boolean fireSmartHomingMissile() {
        if (missileCooldown > 0 || activeEnemies.isEmpty()) {
            return false;
        }

        missileCooldown = 90; // 1.5 seconds cooldown

        // Action-Driven Undo: Record state before missile launch
        player.recordStateForRewind(activeEnemies.size());

        // Find target closest to player's aiming vector using Binary Search (O(log N))
        AlienEnemy lockedTarget = SearchingAlgorithms.findClosestTargetByBinarySearch(activeEnemies, player.getX() + player.getWidth() / 2.0);

        if (lockedTarget != null) {
            // Un-target others, target this one
            for (AlienEnemy e : activeEnemies) e.setTargeted(false);
            lockedTarget.setTargeted(true);

            // Spawn homing missile from starship
            HomingMissile missile = new HomingMissile(player.getX() + player.getWidth() / 2.0 - 4, player.getY() - 15, lockedTarget);
            activeMissiles.add(missile);

            log("Smart Missile launched! Target acquired via Binary Search in O(log N).");
            return true;
        }
        return false;
    }

    /**
     * Main 60 FPS simulation loop with responsive canvas bounds.
     */
    public void update() {
        update(canvasWidth, canvasHeight);
    }

    public void update(int w, int h) {
        if (w > 100 && h > 100) {
            int oldWidth = this.canvasWidth;
            this.canvasWidth = w;
            this.canvasHeight = h;

            // Keep player safely within combat bounds (strictly clear of hotkey dock on right)
            double px = Math.max(10, Math.min(canvasWidth - player.getWidth() - 80, player.getX()));
            double py = Math.max(90, Math.min(canvasHeight - player.getHeight() - 15, player.getY()));
            player.setPosition(px, py);

            // Keep alien enemies safely within combat bounds (strictly clear of hotkey dock)
            int enemyRightLimit = canvasWidth - 85;
            if (oldWidth != w && oldWidth > 0) {
                for (AlienEnemy enemy : activeEnemies) {
                    if (enemy.getX() > enemyRightLimit - enemy.getWidth()) {
                        enemy.setPosition(enemyRightLimit - enemy.getWidth(), enemy.getY());
                    }
                    if (enemy.getX() < 15) {
                        enemy.setPosition(15, enemy.getY());
                    }
                }
            }
        }

        if (gameOver || victory) return;
        tickCounter++;

        // Cooldown tick decrements
        if (lightningCooldown > 0) lightningCooldown--;
        if (missileCooldown > 0) missileCooldown--;
        if (lightningTimer > 0) {
            lightningTimer--;
            if (lightningTimer == 0) activeLightningArcs.clear();
        }

        // Passive plasma ammo recharge trickle (1 ammo per 25 ticks)
        if (tickCounter % 25 == 0 && player.getAmmo() < player.getMaxAmmo()) {
            player.rechargeAmmo(1);
        }

        // 1. Starfield across responsive canvas
        for (int i = 0; i < stars.length; i++) {
            stars[i][1] += stars[i][2];
            if (stars[i][1] > canvasHeight) {
                stars[i][1] = 0;
                stars[i][0] = Math.random() * canvasWidth;
            }
        }

        // 2. Action-Driven Undo: Record tactical flight maneuvers (Lesson 5)
        player.recordManeuverIfMoved(activeEnemies.size());

        // 3. Update Player Lasers (CustomLinkedList - Lesson 3)
        CustomLinkedList<Laser> lasers = player.getLaserList();
        CustomLinkedList.Node<Laser> prev = null;
        CustomLinkedList.Node<Laser> curr = lasers.getHead();

        while (curr != null) {
            Laser laser = curr.data;
            laser.update();

            boolean hit = false;
            for (AlienEnemy enemy : activeEnemies) {
                if (enemy.isActive() && checkCollision(laser, enemy)) {
                    enemy.takeDamage(laser.getDamage());
                    laser.deactivate();
                    hit = true;
                    player.incrementCombo();
                    int comboBonus = RecursionHelper.calculateComboScore(player.getComboStreak(), 20);
                    player.addScore(comboBonus);

                    if (!enemy.isActive()) {
                        player.addScore(enemy.getScoreReward());
                    }
                    break;
                }
            }

            if (laser.getY() < -20 || !laser.isActive()) {
                if (prev == null) {
                    lasers.deleteAt(0);
                    curr = lasers.getHead();
                } else {
                    prev.next = curr.next;
                    curr = curr.next;
                }
            } else {
                prev = curr;
                curr = curr.next;
            }
        }

        // 4. Update Smart Homing Missiles (Binary Search guidance)
        Iterator<HomingMissile> misIt = activeMissiles.iterator();
        while (misIt.hasNext()) {
            HomingMissile m = misIt.next();
            m.update();
            if (!m.isActive()) {
                misIt.remove();
                player.addScore(200);
            }
        }

        // 5. Update Alien Invaders
        Iterator<AlienEnemy> enemyIt = activeEnemies.iterator();
        while (enemyIt.hasNext()) {
            AlienEnemy enemy = enemyIt.next();
            if (!enemy.isActive()) {
                enemyIt.remove();
                continue;
            }

            enemy.update(canvasWidth);

            // Alien firing chance
            if (Math.random() < 0.012) {
                enemyLasers.add(new Laser(enemy.getX() + enemy.getWidth() / 2.0, enemy.getY() + enemy.getHeight(), 5.5, enemy.getAttackPower(), false, "Plasma"));
            }

            if (enemy.getY() + enemy.getHeight() >= player.getY()) {
                player.takeDamage(35);
                enemy.takeDamage(999);
            }
        }

        // 6. Update Enemy Lasers
        Iterator<Laser> elIt = enemyLasers.iterator();
        while (elIt.hasNext()) {
            Laser el = elIt.next();
            el.update();
            if (checkPlayerCollision(el, player)) {
                player.takeDamage(el.getDamage());
                elIt.remove();
                if (player.getLives() <= 0) {
                    gameOver = true;
                    SaveManager.updateHighScore(player.getScore());
                }
            } else if (el.getY() > canvasHeight + 20) {
                elIt.remove();
            }
        }

        // 7. Check Round Completion
        if (activeEnemies.isEmpty()) {
            if (currentRound >= 5) {
                victory = true;
                SaveManager.updateHighScore(player.getScore());
            } else {
                currentRound++;
                player.rechargeAmmo(40);
                prepareRoundQueue(currentRound);
                deployRound();
                // Auto-save progression to next round
                SaveManager.save(currentRound, player.getScore(), player.getShield(), player.getAmmo(), player.getLives());
            }
        }
    }

    private boolean checkCollision(Laser laser, AlienEnemy enemy) {
        return laser.getX() >= enemy.getX() &&
               laser.getX() <= enemy.getX() + enemy.getWidth() &&
               laser.getY() >= enemy.getY() &&
               laser.getY() <= enemy.getY() + enemy.getHeight();
    }

    private boolean checkPlayerCollision(Laser laser, PlayerShip ship) {
        return laser.getX() >= ship.getX() &&
               laser.getX() <= ship.getX() + ship.getWidth() &&
               laser.getY() >= ship.getY() &&
               laser.getY() <= ship.getY() + ship.getHeight();
    }

    public boolean executeTimeRewind() {
        boolean success = player.executeTimeRewind(); // Lesson 5: Stack pop()
        if (success) {
            enemyLasers.clear();
            gameOver = false;
        }
        return success;
    }

    public void log(String msg) {
        eventLogs.add(msg);
        if (eventLogs.size() > 100) eventLogs.remove(0);
    }

    public void saveCurrentState() {
        if (player != null && currentRound >= 1) {
            SaveManager.save(currentRound, player.getScore(), player.getShield(), player.getAmmo(), player.getLives());
        }
    }

    // Getters
    public PlayerShip getPlayer() { return player; }
    public CustomCircularQueue<AlienEnemy> getRoundQueue() { return roundQueue; }
    public List<AlienEnemy> getActiveEnemies() { return activeEnemies; }
    public List<Laser> getEnemyLasers() { return enemyLasers; }
    public List<HomingMissile> getActiveMissiles() { return activeMissiles; }
    public List<Point2D[]> getActiveLightningArcs() { return activeLightningArcs; }
    public int getLightningCooldown() { return lightningCooldown; }
    public int getMissileCooldown() { return missileCooldown; }
    public double[][] getStars() { return stars; }
    public int getCurrentRound() { return currentRound; }
    public boolean isGameOver() { return gameOver; }
    public boolean isVictory() { return victory; }
    public List<String> getEventLogs() { return eventLogs; }
}
