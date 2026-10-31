package datastructures;

/**
 * CustomCircularQueue - Array-based FIFO (First In First Out) Circular Queue.
 * Corresponds to General Sir John Kotelawala Defence University (KDU) IT22013 Lesson 4: Queue Data Structure.
 * 
 * @author Member C (ID: IT/IS/42/003) - Individual Contribution: 20%
 * Module: FIFO Round Scheduling & Alien Wave Staging
 * 
 * Circular Queue Equations from KDU Lecture Slides:
 * - Enqueue pointer update: rear = (rear + 1) % capacity
 * - Dequeue pointer update: front = (front + 1) % capacity
 * - Full Condition:  count == capacity
 * - Empty Condition: count == 0
 * 
 * Theoretical Complexities:
 * - Enqueue: O(1) Time, O(1) Space
 * - Dequeue: O(1) Time, O(1) Space
 * - Peek:    O(1) Time, O(1) Space
 * - Space Complexity: O(K) where K is max wave capacity (fixed allocation, zero drift)
 * 
 * In-Game Application:
 * 1. Alien Round Invasion Queue (CustomCircularQueue<AlienEnemy>).
 * 2. Hostile fleet formations are enqueued at round commencement and deployed in strict FIFO order,
 *    eliminating linear array shifting overheads.
 *
 * @param <T> generic element type stored in the queue
 */
public class CustomCircularQueue<T> {
    private Object[] elements;
    private int front;
    private int rear;
    private int count;
    private int capacity;

    public CustomCircularQueue(int capacity) {
        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }

    /**
     * Enqueue / Insert element to the rear of queue.
     * Time Complexity: O(1)
     */
    public boolean enqueue(T item) {
        if (isFull()) {
            return false; // Queue overflow
        }
        rear = (rear + 1) % capacity;
        elements[rear] = item;
        count++;
        return true;
    }

    /**
     * Dequeue / Remove element from the front of queue.
     * Time Complexity: O(1)
     */
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            return null; // Queue underflow
        }
        T item = (T) elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        count--;
        return item;
    }

    /**
     * Peek at front element without removing it.
     * Time Complexity: O(1)
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return (T) elements[front];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == capacity;
    }

    public int size() {
        return count;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns elements in current FIFO order for visualization in GUI.
     */
    @SuppressWarnings("unchecked")
    public Object[] getSnapshot() {
        Object[] snapshot = new Object[count];
        for (int i = 0; i < count; i++) {
            int index = (front + i) % capacity;
            snapshot[i] = elements[index];
        }
        return snapshot;
    }

    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
        front = 0;
        rear = -1;
        count = 0;
    }
}
