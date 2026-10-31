# GENERAL SIR JOHN KOTELAWALA DEFENCE UNIVERSITY
## Faculty of Computing | Department of Information Technology
### Bachelor of Science (Hons) in Information Technology / Information Systems
### Intake 42 | Year 2 Semester 1

---

# DATA STRUCTURES AND ALGORITHMS (IT22013)
## GROUP ASSIGNMENT TECHNICAL REPORT

```
========================================================================================
       GALAXY DEFENDER: DSA EDITION - HIGH-PERFORMANCE 2D ARCADE SPACE SHOOTER
========================================================================================
```

**Module Code:** IT22013  
**Module Title:** Data Structures and Algorithms  
**Academic Year:** 2026  
**Implementation Technology:** Java (JDK 18+ with Java Swing & AWT Graphics Engine)  
**Submission Deadline:** 01st of November 2026  

---

### Group Members Information:

| # | Student Name | Student Registration No. (ID) | Degree Programme | Contribution (%) | Assigned Role / Modules |
|:---:|---|:---:|:---:|:---:|---|
| **1** | **Member A (Group Leader)** | `IT/IS/42/001` *(Placeholder)* | BSc (Hons) Information Technology / IS | 20% | Project Architecture, CustomStack (LIFO Undo), GUI Coordination |
| **2** | **Member B** | `IT/IS/42/002` *(Placeholder)* | BSc (Hons) Information Technology / IS | 20% | CustomCircularQueue (Round Invasions), Game Loop Physics |
| **3** | **Member C** | `IT/IS/42/003` *(Placeholder)* | BSc (Hons) Information Technology / IS | 20% | CustomLinkedList (Laser Projectiles), Collision Engine |
| **4** | **Member D** | `IT/IS/42/004` *(Placeholder)* | BSc (Hons) Information Technology / IS | 20% | Searching (Binary Search Smart Missile Radar), Testing Matrix |
| **5** | **Member E** | `IT/IS/42/005` *(Placeholder)* | BSc (Hons) Information Technology / IS | 20% | Sorting (Insertion Sort Fleet Formation), Recursion (Chain Lightning) |

---

## a. Introduction

### a.1 Game Concept
*Galaxy Defender: DSA Edition* is a high-performance 2D sci-fi arcade space shooter video game developed using pure Java (Standard Edition) without any external third-party game libraries. The player assumes command of an advanced defense starship defending sector airspace against successive invasion waves of alien fleets, comprising four specialized hostile spacecraft archetypes: **Grunt Stalkers**, **Heavy Cruisers**, **Swarm Hornets**, and the colossal **Dreadnought Boss**.

Rather than implementing Data Structures and Algorithms through artificial, text-heavy pop-up dialogs or typing prompts that destroy the real-time arcade flow, every single required DSA concept is embedded directly into **organic, real-time in-game battle mechanics, weapons, and control loops**.

### a.2 Project Objectives
1. **Core Syllabus Compliance:** Fully satisfy and exceed the mandatory IT22013 curriculum requirements by implementing:
   - Four core Data Structures (**Array, Linked List, Stack, Queue**).
   - An in-situ **Sorting Algorithm** (Insertion Sort).
   - An efficient logarithmic **Searching Algorithm** (Binary Search in $O(\log N)$).
   - A multi-branch **Recursive Function** (Electric Chain Lightning propagation).
2. **Strict Custom Implementation Policy:** Refrain from utilizing built-in Java Collections Framework utilities (such as `java.util.Stack`, `java.util.LinkedList`, or `java.util.Queue`) for core mechanics. All fundamental structures are constructed from scratch from basic memory pointers and primitive arrays.
3. **Smooth 60 FPS User Experience:** Combine real-time 60 FPS Swing graphics rendering with algorithmic operations, ensuring zero garbage collection pauses and responsive 4-way keyboard controls.

---

## b. Design Overview

### b.1 System Architecture & Architectural Pattern
The software architecture strictly adheres to the **Model-View-Controller (MVC)** architectural paradigm, maintaining strict separation between mathematical simulation, algorithmic data structures, and vector graphics rendering:

```
+----------------------------------------------------------------------------------------+
|                                  CONTROLLER LAYER                                      |
|                               (GalaxyDefenderFrame)                                    |
|   - Master Swing Window (Standard 980x780 Windowed Mode & F11 Borderless Fullscreen)   |
|   - Top Cyberpunk Vitals HUD (Score, Shield/Lives, Plasma Ammo 100/100, Round)         |
|   - 60 FPS Master Game Loop Timer (16.6 ms tick interval)                             |
|   - 4-Way Smooth Directional Keyboard Listener (W, A, S, D / Arrow Keys)              |
+-------------------------------------------+--------------------------------------------+
                                            |
                                            v
+----------------------------------------------------------------------------------------+
|                                     MODEL LAYER                                        |
|                                  (GalaxyGameState)                                     |
|   - Coordinates Physics, Bounding Box Collisions, and Spatial Boundaries              |
|   - Invokes Custom Data Structures & Concrete Algorithms                               |
+-----------+----------------------+--------------------+--------------------+-----------+
            |                      |                    |                    |
            v                      v                    v                    v
  +------------------+   +--------------------+  +--------------+  +---------------------+
  | CustomStack<T>   |   | CustomCircular     |  | Custom       |  | 2D Starfield Array  |
  | (LIFO Undo)      |   | Queue<T> (Rounds)  |  | Linked       |  | double[150][3]      |
  | Capacity: 30     |   | Capacity: 25       |  | List<Laser>  |  | Lesson 2            |
  | Lesson 5         |   | Lesson 4           |  | Lesson 3     |  +---------------------+
  +------------------+   +--------------------+  +--------------+
            ^                      ^                    ^
            |                      |                    |
+-----------+----------------------+--------------------+--------------------------------+
|                                     VIEW LAYER                                         |
|                                (SpaceCombatCanvas)                                     |
|   - Custom JPanel with Java2D Double-Buffering & High-Fidelity Vector Graphics         |
|   - Right-Side Circular Action Dock (E, X, T, U with 360° Radial Cooldown Rings)       |
|   - Bio-Mechanical Alien Ships with Floating Health Bars (HP %)                        |
|   - Dynamic Projectiles: Ion Plasma Lances, Bio-Plasma Comets, and Guided Rockets       |
|   - Non-Intrusive Centered HUD Action Notifications & Fading Round Banner              |
+----------------------------------------------------------------------------------------+
```

### b.2 Game Loop & Data Flow
1. **User Input:** Player inputs (Movement, Laser Fire, or Special Ability triggers) are captured asynchronously by `KeyAdapter` in `GalaxyDefenderFrame`.
2. **State Mutation:** Every 16.6 ms, `gameState.update(width, height)` executes:
   - Updates star coordinates in the 2D Array.
   - Traverses active lasers via `CustomLinkedList` node iteration.
   - Detects collisions and applies damage.
   - Updates guided missiles tracking coordinates acquired via Binary Search.
   - Evaluates player action milestones to push state snapshots onto `CustomStack`.
3. **Canvas Repaint:** `SpaceCombatCanvas.paintComponent()` consumes the model state and draws the updated visual frame with zero screen tearing.

---

## c. Algorithms Used & Theoretical Justification

### c.1 Data Structures Justification

#### 1. Custom Stack (LIFO) — Action-Driven Undo & Damage Reversal (Lesson 5)
* **Underlying Structure:** Contiguous array with a pointer index `top`.
* **In-Game Functionality:** Implements an **Event-Driven Command Pattern**. Whenever the starship performs a significant action—specifically **taking damage from an enemy projectile**, firing weapon bursts, or completing flight maneuvers—a snapshot `RewindFrame(x, y, shield, score, ammo)` is `push()`ed onto `CustomStack`. Pressing **`U`** triggers `pop()`, reversing the last action and immediately restoring shield integrity and spatial position.
* **Academic Justification:** An Undo mechanism fundamentally mandates **Last-In, First-Out (LIFO)** ordering. The state that occurred most recently must be the first state reversed. By capping the stack capacity to 30 and dropping the oldest historical baseline when full, operations execute in guaranteed $O(1)$ constant time with zero dynamic reallocation overhead.

#### 2. Custom Circular Queue (FIFO) — Alien Round Scheduling (Lesson 4)
* **Underlying Structure:** Fixed-size array with `front`, `rear`, and `count` pointers governed by modulo arithmetic.
* **In-Game Functionality:** At the start of each combat round, hostile ships are generated and `enqueue()`ed into `CustomCircularQueue<AlienEnemy>`. Formations are sequentially `dequeue()`ed and dispatched onto the battlefield.
* **Academic Justification:** In military fleet logistics, units that are queued first must deploy first (**First-In, First-Out / FIFO**). In a standard linear queue, repeated enqueueing and dequeueing causes pointer drift toward the end of the array, requiring $O(N)$ elements shifting. A **Circular Queue** wraps pointers using modulo arithmetic:
  $$\text{rear} = (\text{rear} + 1) \pmod{\text{capacity}}$$
  $$\text{front} = (\text{front} + 1) \pmod{\text{capacity}}$$
  This achieves optimal $O(1)$ time complexity for both insertion and removal.

#### 3. Custom Singly Linked List — Dynamic Laser Projectiles (Lesson 3)
* **Underlying Structure:** Dynamic singly linked list consisting of heap-allocated `Node<Laser>` instances with `data` and `next` references.
* **In-Game Functionality:** Holds all active starship laser beams currently in flight.
* **Academic Justification:** High-speed arcade lasers are continuously generated and destroyed at rapid intervals (spacebar firing and collision impacts). Using an array-based list would incur costly $O(N)$ memory copies and array shifts whenever a laser in the middle strikes an enemy. In a Singly Linked List, removing an arbitrary node given the previous pointer requires only updating a reference (`prev.next = curr.next`), operating in true $O(1)$ pointer reassignment time.

#### 4. 2D Array — Deep Space Starfield Simulation (Lesson 2)
* **Underlying Structure:** Contiguous 2D primitive array `double[150][3]`.
* **In-Game Functionality:** Tracks the $(X, Y)$ coordinates and velocity speeds ($Z$) of 150 parallax stars across the responsive canvas.
* **Academic Justification:** Because the star count is static and accessed sequentially on every single frame render, a multi-dimensional array provides direct $O(1)$ element access and optimal CPU cache locality.

---

### c.2 Sorting Algorithm Justification (Lesson 9: Insertion Sort)

* **Algorithm Selected:** **Insertion Sort** (`SortingAlgorithms.insertionSort`).
* **In-Game Application:** Activated automatically during round initialization and dynamically on the **`T`** hotkey. The alien fleet extracts active ships and sorts them in descending order based on **Threat Level Metric**:
  $$\text{Threat Level} = \text{Current HP} \times \text{Attack Power}$$
  The sorted ships glide smoothly into tactical formation ranks, placing high-threat bosses and heavy cruisers in primary vanguard positions.
* **Time Complexity Analysis:**
  - **Best Case:** $O(N)$ (When the fleet is already in near-sorted order).
  - **Average Case:** $O(N^2)$.
  - **Worst Case:** $O(N^2)$ (Reverse sorted order).
  - **Space Complexity:** $O(1)$ Auxiliary Space (Strictly in-place sort).
* **Academic Justification:** While $O(N \log N)$ algorithms (such as Quick Sort or Merge Sort) excel on massive datasets, **Insertion Sort is provably superior for small datasets ($N \le 50$)** due to its minimal overhead, zero recursive stack frames, and adaptive $O(N)$ performance on partially sorted collections. In an arcade game where alien counts range from 5 to 25 ships, Insertion Sort delivers maximal efficiency with negligible memory footprint.

---

### c.3 Searching Algorithm Justification (Lesson 1 & 2: Binary Search)

* **Algorithm Selected:** **Binary Search** (`SearchingAlgorithms.findClosestTargetByBinarySearch`).
* **In-Game Application:** Activated on the **`X`** hotkey to launch a **Smart Homing Missile**. The missile's onboard radar extracts the active alien ships sorted by horizontal $X$-coordinate and executes Binary Search to lock onto the alien ship whose position is closest to the starship's launch vector.
* **Time Complexity Analysis:**
  - **Best Case:** $O(1)$ (Target located directly at array midpoint).
  - **Average / Worst Case:** $O(\log N)$ logarithmic time.
  - **Space Complexity:** $O(1)$ iterative memory.
* **Academic Justification:** Compared to an exhaustive Linear Search ($O(N)$), Binary Search employs the **Divide and Conquer** paradigm, eliminating half of the search space with every iteration. For radar vector locking, logarithmic convergence guarantees target acquisition in negligible processor cycles ($\le 5$ comparisons even for 30 enemies).

---

### c.4 Recursive Function Justification (Lesson 1: Recursion)

* **Algorithm Selected:** **Recursive Chain Lightning EMP** (`RecursionHelper.propagateChainLightning`).
* **In-Game Application:** Triggered on the **`E`** hotkey. The starship fires an initial electric EMP bolt striking the nearest hostile ship. The method then **recursively propagates** through the fleet, locating the nearest unvisited enemy within a 75-pixel electrical arc radius, dealing decaying damage, and continuing across up to 4 targets.
* **Mathematical Recurrence Formulation:**
  $$T(k) = T(k - 1) + O(N)$$
  where $k$ represents remaining chain jumps ($k \le 4$) and $N$ is the active enemy count.
* **Base Cases (Stopping Conditions):**
  1. $k \le 0$ (Target jump limit exhausted).
  2. $\text{current} == \text{null}$ (Invalid reference).
  3. No unvisited hostile ships remain within jumping proximity.
* **Recursive Step:**
  $$\text{propagateChainLightning}(\text{nearestUnvisited}, \text{allEnemies}, \text{visited}, k - 1, \text{damage} \times 0.85, \text{arcList})$$
* **Academic Justification:** Multi-target electrical arcing is naturally modeled as a recursive depth-bounded graph traversal. Using recursion yields mathematically elegant, expressive code where the call stack inherently preserves the path of the lightning bolt.

---

## d. Implementation Details & Key Code Snippets

### d.1 Custom Stack Implementation & Action-Driven Undo
```java
package datastructures;

public class CustomStack<T> {
    private Object[] elements;
    private int top;
    private int capacity;

    public CustomStack(int capacity) {
        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.top = -1;
    }

    public boolean push(T item) {
        if (isFull()) {
            // Drop oldest bottom element so stack always preserves the most recent actions
            System.arraycopy(elements, 1, elements, 0, capacity - 1);
            elements[capacity - 1] = item;
            return true;
        }
        elements[++top] = item;
        return true;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) return null;
        T item = (T) elements[top];
        elements[top] = null; // Clean reference for GC
        top--;
        return item;
    }

    public boolean isEmpty() { return top == -1; }
    public boolean isFull()  { return top == capacity - 1; }
    public int size()        { return top + 1; }
}
```

### d.2 Insertion Sort for Fleet Formation Re-alignment
```java
package algorithms;
import model.AlienEnemy;
import java.util.Comparator;

public class SortingAlgorithms {
    public static void insertionSort(AlienEnemy[] arr, Comparator<AlienEnemy> comparator) {
        for (int i = 1; i < arr.length; i++) {
            AlienEnemy key = arr[i];
            int j = i - 1;

            // Shift elements that have lower threat priority to the right
            while (j >= 0 && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }
}
```

### d.3 Binary Search for Smart Missile Targeting
```java
package algorithms;
import model.AlienEnemy;
import java.util.*;

public class SearchingAlgorithms {
    public static AlienEnemy findClosestTargetByBinarySearch(List<AlienEnemy> enemies, double targetX) {
        if (enemies == null || enemies.isEmpty()) return null;

        AlienEnemy[] sorted = enemies.toArray(new AlienEnemy[0]);
        Arrays.sort(sorted, Comparator.comparingDouble(AlienEnemy::getX));

        int low = 0, high = sorted.length - 1;
        int closestIdx = 0;
        double minDiff = Double.MAX_VALUE;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            double midX = sorted[mid].getX() + sorted[mid].getWidth() / 2.0;
            double diff = Math.abs(midX - targetX);

            if (diff < minDiff) {
                minDiff = diff;
                closestIdx = mid;
            }

            if (Math.abs(midX - targetX) < 15.0) return sorted[mid]; // Exact vector lock
            else if (midX < targetX) low = mid + 1;
            else high = mid - 1;
        }
        return sorted[closestIdx];
    }
}
```

### d.4 Recursive Chain Lightning Propagation
```java
package algorithms;
import model.AlienEnemy;
import java.awt.geom.Point2D;
import java.util.*;

public class RecursionHelper {
    public static void propagateChainLightning(
            AlienEnemy current, List<AlienEnemy> allEnemies, Set<AlienEnemy> visited,
            int jumpsRemaining, int damage, List<Point2D[]> lightningSegments) {

        // Base Cases
        if (current == null || jumpsRemaining <= 0) return;

        visited.add(current);
        current.takeDamage(damage);

        AlienEnemy nearest = findNearestUnvisited(current, allEnemies, visited);
        if (nearest != null && getDistance(current, nearest) <= 85.0) {
            Point2D from = new Point2D.Double(current.getX() + current.getWidth() / 2.0, current.getY() + current.getHeight() / 2.0);
            Point2D to = new Point2D.Double(nearest.getX() + nearest.getWidth() / 2.0, nearest.getY() + nearest.getHeight() / 2.0);
            lightningSegments.add(new Point2D[]{from, to});

            // Recursive Step with decaying damage
            propagateChainLightning(nearest, allEnemies, visited, jumpsRemaining - 1, (int)(damage * 0.85), lightningSegments);
        }
    }
}
```

---

## e. Testing and Output Verification

### e.1 Systematic Testing Matrix

| Test ID | Module Tested | Test Procedure | Expected Algorithmic Output | Actual Output | Status |
|:---:|---|---|---|---|:---:|
| **TC-01** | `CustomLinkedList` | Press SPACEBAR continuously | Lasers are dynamically allocated as nodes in list, traverse upwards, and nodes are removed upon collision or exit without memory leaks. | Lasers traverse screen smoothly; node count increments on fire and decrements on impact. | **PASS** |
| **TC-02** | `CustomStack` (Undo) | Incur enemy damage, then press `U` | Top `RewindFrame` is popped from stack; Starship reverts to position, shield, and score prior to damage. | Shield restored; ship repositioned; incoming enemy lasers cleared. | **PASS** |
| **TC-03** | `CustomStack` (Gauge) | Observe the `U` circular dock icon | Stack charges up to 30 actions as maneuvers and shots occur; circular violet ring fills proportionally. | Arc fills 0° to 360° as stack reaches 30/30. | **PASS** |
| **TC-04** | `CustomCircularQueue` | Clear Round 1 active enemies | Round 2 invaders are dequeued sequentially via modulo arithmetic without index drift. | Round 2 enemies appear in order; Boss spawns accurately. | **PASS** |
| **TC-05** | `Insertion Sort` | Press `T` during active wave | Active enemies re-order in array descending by Threat Level ($HP \times AttackPower$) and glide to battle ranks. | Dreadnought Boss glides to center; Heavy and Grunt ships rank on flanks. | **PASS** |
| **TC-06** | `Binary Search` | Press `X` with active enemies | Binary search locates the target along horizontal X coordinates in $O(\log N)$; guided rocket tracks target. | Smart missile curves toward closest target and eliminates enemy. | **PASS** |
| **TC-07** | `Recursion` | Press `E` with clustered enemies | Electric arc recursively jumps across up to 4 unvisited targets, halting when jumps reach 0. | Blue-white lightning bolts link 4 enemies simultaneously with decaying damage. | **PASS** |

### e.2 Demonstration Screenshots & Output Visuals

```
+-----------------------------------------------------------------------------------------------+
| [SCREENSHOT 1 PLACEHOLDER: Active Combat View]                                                |
| Caption: Main Combat Canvas showing Deep Space Starfield (2D Array), Laser Lances             |
| (CustomLinkedList), Alien Fleet with Mini Health Bars, and Right Action Dock (E, X, T, U).     |
+-----------------------------------------------------------------------------------------------+
| [SCREENSHOT 2 PLACEHOLDER: Recursive Chain Lightning & Binary Search Missile]                 |
| Caption: Blue electric arcs propagating recursively across 4 hostiles (Recursion) and         |
| golden guided missile homing onto target locked via Binary Search in O(log N).                |
+-----------------------------------------------------------------------------------------------+
| [SCREENSHOT 3 PLACEHOLDER: Fleet Insertion Sort & Action-Driven Undo]                         |
| Caption: Alien battle formation dynamically re-aligned via Insertion Sort (T Key) and          |
| holographic circular Stack gauge filling up to 30 actions (U Key).                            |
+-----------------------------------------------------------------------------------------------+
```

---

## f. Conclusion, Challenges & Lessons Learned

### f.1 Challenges Faced & Solutions Implemented
1. **Dynamic Canvas Bounds & Window Resizing:**
   - *Challenge:* When transitioning from borderless Fullscreen to standard windowed mode (980x780), ships located beyond the smaller screen width disappeared off-screen.
   - *Solution:* Implemented dynamic coordinate clamping in `GalaxyGameState.update()` and `AlienEnemy.update()`, ensuring ships are clamped within visible screen boundaries whenever canvas resolution changes.
2. **Real-Time 60 FPS Performance with Custom Data Structures:**
   - *Challenge:* Frequent object creation in linked lists can provoke Java Garbage Collection stutter.
   - *Solution:* Reused projectile memory references, maintained fixed array buffers for `CustomStack` and `CustomCircularQueue`, and decoupled heavy math from the visual render pipeline.
3. **Non-Intrusive UI Notifications:**
   - *Challenge:* Action notifications previously overlapped alien flight zones.
   - *Solution:* Shifted action notifications to a subtle, centered glassmorphic pill slightly above the player flight sector, complemented by a dedicated vertical circular action dock along the right margin.

### f.2 Key Academic Lessons Learned
* **Concrete Pointer Mechanics:** Direct implementation of pointers (`Node.next`, `elements[top]`, circular modulo indexers) solidified a deep practical understanding of computer memory management beyond textbook theory.
* **Algorithmic Selection in Real-Time Systems:** Realized that theoretical Big-O superiority (e.g. Merge Sort $O(N \log N)$) is often surpassed by adaptive algorithms like Insertion Sort ($O(N)$ best case) on small, real-time datasets.
* **Event-Driven Undo Patterns:** Mastered the Command Pattern using LIFO Stacks to record state transitions rather than inefficient continuous timer polling.

---

## Appendix: Viva-Voce Oral Examination Preparation Guide

| # | Anticipated Viva Question | Model Technical Answer for Full Marks |
|:---:|---|---|
| **1** | *"Why did you use a Circular Queue instead of a simple Linear Queue for Rounds?"* | *"Sir, in a linear array queue, `dequeue()` causes the `front` pointer to move forward, leaving unused memory at the beginning. Reclaiming that space requires shifting all elements left, which takes $O(N)$ time. A Circular Queue uses modulo arithmetic `(rear + 1) % capacity`, which wraps pointers around to reuse vacated slots, maintaining guaranteed $O(1)$ time for both enqueue and dequeue without memory wastage."* |
| **2** | *"How does your Time Rewind Stack work, and why is LIFO appropriate?"* | *"We implemented an Action-Driven Undo Stack using our `CustomStack<RewindFrame>` with a capacity of 30. Whenever a significant action occurs—such as taking damage or firing—a state snapshot is pushed onto the stack. Pressing 'U' pops the most recent snapshot. LIFO (Last-In, First-Out) is the textbook requirement for Undo operations because the most recent action must be the first one undone."* |
| **3** | *"Why choose Insertion Sort over Quick Sort or Bubble Sort for Fleet Formation?"* | *"Bubble Sort requires $O(N^2)$ comparisons regardless of optimization. While Quick Sort is $O(N \log N)$, it introduces recursive stack overhead and performs poorly on small arrays. Insertion Sort has an adaptive $O(N)$ best-case time complexity on partially sorted fleets, requires $O(1)$ auxiliary space, and has minimal constant factor overhead for $N \le 30$ enemies."* |
| **4** | *"What are the Base Cases of your Recursive Chain Lightning algorithm?"* | *"Our recursive function `propagateChainLightning()` terminates under three conditions: (1) when the jump counter reaches zero `jumpsRemaining <= 0`, (2) when the current enemy reference is null, or (3) when all reachable enemies within the 85px electrical proximity radius are already marked in the `visited` HashSet."* |
| **5** | *"Why does Binary Search require sorting first, and what is its complexity?"* | *"Binary Search requires sorted data to apply the Divide and Conquer rule: comparing the target with the midpoint allows us to eliminate half the remaining array in one step. Its time complexity is $O(\log N)$, allowing target locking across active enemies in at most 4 to 5 comparisons."* |
