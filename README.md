# Galaxy Defender: DSA Edition
**General Sir John Kotelawala Defence University (KDU)**  
**Faculty of Computing | Department of Information Technology**  
**IT22013: Data Structures and Algorithms — Group Assignment**  
**Intake 42**

---

## 🎮 How to Play the Game

### Method 1: Double-Click Batch Files (Windows)
1. Double-click `compile.bat` to compile the Java source files into `bin/`.
2. Double-click `run.bat` to launch the game window!

### Method 2: Command Line (CMD / PowerShell)
```bash
# Compile
javac -d bin src\datastructures\*.java src\model\*.java src\algorithms\*.java src\ui\*.java src\Main.java

# Run
java -cp bin Main
```

---

## 🕹️ Controls & In-Game Practical DSA Weapons
- **◄ / ► / ▲ / ▼** or **W / A / S / D**: Full 4-Way Starship Navigation (Forward, Backward, Left, Right).
- **SPACEBAR**: Fire rapid plasma lasers (Dynamic Singly Linked List - Lesson 3).
- **E Key**: **⚡ Recursive Chain Lightning Weapon (Recursion - Lesson 1)**
  - Fires an electric EMP bolt that strikes an enemy and **recursively arcs to up to 4 adjacent enemies**!
- **X Key**: **🎯 Smart Homing Missile (Binary Search - Lesson 1 & 2)**
  - Uses **Binary Search ($O(\log N)$)** across sorted enemy vectors to acquire the closest target, launching a guided rocket that curves and destroys it!
- **T Key**: **📊 Alien Fleet Formation Re-alignment (Insertion Sort - Lesson 9)**
  - The alien fleet dynamically organizes its flight ranks descending by Threat Level ($HP \times Attack$) using **Insertion Sort**!
- **U Key**: **⏪ Time Rewind / Action-Driven Undo (LIFO Stack - Lesson 5)**
  - Operates like Ctrl+Z in software! Pops the last recorded player action (Damage taken, weapon fired, or tactical maneuver) from `CustomStack` to restore shield integrity and position!
- **F11 Key**: **🖥️ Toggle Fullscreen / Windowed Mode**.
- **R Key**: Restart current round.

---

## 📂 Source Code & DSA Mapping

| Package & File | Role & Lecture Alignment |
|---|---|
| `datastructures.CustomStack` | **Lesson 5: Stack (LIFO)** — Action-Driven Undo Stack history (`push`, `pop`, `peek`) |
| `datastructures.CustomCircularQueue` | **Lesson 4: Circular Queue (FIFO)** — Alien round invasion queue (`(rear+1)%size`) |
| `datastructures.CustomLinkedList` | **Lesson 3: Singly Linked List** — Dynamic laser projectile node chain |
| `algorithms.RecursionHelper` | **Lesson 1: Recursion** — Recursive Chain Lightning weapon propagation algorithm |
| `algorithms.SearchingAlgorithms` | **Lesson 1 & 2: Searching** — Smart Missile Target Guidance via **Binary Search** ($O(\log N)$) |
| `algorithms.SortingAlgorithms` | **Lesson 9: Sorting Techniques** — Alien fleet battle formation sorting via **Insertion Sort** |
| `model.HomingMissile` | Smart guided rocket tracked towards binary-search acquired target |
| `model.PlayerShip` | Starship model containing embedded stack and linked list |
| `model.AlienEnemy` | Invader model with threat level metrics and radar lock state |
| `model.Laser` | Projectile node model |
| `model.RewindFrame` | State snapshot record for Stack Undo |
| `model.GalaxyGameState` | Physics, weapons, and 2D starfield array (Lesson 2) |
| `ui.SpaceCombatCanvas` | High-performance 2D arcade rendering engine with lightning arcs and rocket trails |
| `ui.GalaxyDefenderFrame` | Main Swing window coordinating HUD, weapon cooldowns, and 60 FPS game loop |
| `PROJECT_REPORT_DRAFT.md` | Complete assignment report formatted according to the guidelines |
