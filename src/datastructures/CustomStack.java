package datastructures;

/**
 * CustomStack - Array-based LIFO (Last In First Out) Data Structure.
 * Corresponds to General Sir John Kotelawala Defence University (KDU) IT22013 Lesson 5: Stack Data Structure.
 * 
 * @author Member B (ID: IT/IS/42/002) - Individual Contribution: 20%
 * Module: Action-Driven LIFO Undo & Temporal Damage Reversal Stack
 * 
 * Theoretical Complexities:
 * - Push:  O(1) Amortized Time, O(1) Space
 * - Pop:   O(1) Time, O(1) Space
 * - Peek:  O(1) Time, O(1) Space
 * - Space Complexity: O(M) bounded buffer where M = 30 max rewind frames
 * 
 * In-Game Application:
 * 1. Action-Driven Undo & Temporal Damage Reversal (CustomStack<RewindFrame>).
 * 2. Pushes tactical snapshots when damage is received, weapons are fired, or significant maneuvers occur.
 * 3. Popping restores shield, score, ammo, and starship position in exact reverse chronological order.
 * 4. Includes bounded capacity ring-buffer behavior: drops oldest bottom record upon reaching capacity (30)
 *    so memory remains bounded while preserving recent player actions.
 *
 * @param <T> generic element type stored in the stack
 */
public class CustomStack<T> {
    private Object[] elements;
    private int top;
    private int capacity;

    public CustomStack(int capacity) {
        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.top = -1; // -1 indicates empty stack
    }

    /**
     * Push element onto the top of stack.
     * Time Complexity: O(1)
     */
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

    /**
     * Pop and return the top element from the stack.
     * Time Complexity: O(1)
     */
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T item = (T) elements[top];
        elements[top] = null; // Help GC
        top--;
        return item;
    }

    /**
     * Peek at the top element without removing it.
     * Time Complexity: O(1)
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return (T) elements[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == capacity - 1;
    }

    public int size() {
        return top + 1;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Return all elements currently in stack from bottom to top for visualization.
     */
    @SuppressWarnings("unchecked")
    public Object[] getSnapshot() {
        Object[] snapshot = new Object[size()];
        for (int i = 0; i <= top; i++) {
            snapshot[i] = elements[i];
        }
        return snapshot;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) {
            elements[i] = null;
        }
        top = -1;
    }
}
