package datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * CustomLinkedList - Singly Linked List Data Structure.
 * Corresponds to General Sir John Kotelawala Defence University (KDU) IT22013 Lesson 3: Linked Lists.
 * 
 * @author Member A (ID: IT/IS/42/001) - Individual Contribution: 20%
 * Module: Dynamic Projectile Management & Linked Node Allocations
 * 
 * Theoretical Complexities:
 * - Insert First: O(1) Time, O(1) Space
 * - Insert Last:  O(N) Time (O(1) with tail pointer), O(1) Space
 * - Delete Node:  O(1) Time (when predecessor node is known) / O(N) traversal search
 * - Full Traversal: O(N) Time, O(1) Auxiliary Space
 * 
 * In-Game Application:
 * 1. Dynamic starship laser projectile chain (CustomLinkedList<Laser>).
 * 2. Rapid insertion of projectile nodes when spacebar is pressed (insertLast).
 * 3. O(1) dynamic node unlinking when a laser strikes an alien enemy or leaves the combat canvas,
 *    preventing array re-allocation pauses during 60 FPS gameplay.
 *
 * @param <T> generic element type stored in the linked list
 */
public class CustomLinkedList<T> implements Iterable<T> {

    public static class Node<T> {
        public T data;
        public Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private int size;

    public CustomLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Add new data to the first node (Insert at Head).
     * Corresponds to Lesson 3 Activity: "Add new data to the first node".
     * Time Complexity: O(1)
     */
    public void insertFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    /**
     * Add new data to the end of the list.
     * Time Complexity: O(N)
     */
    public void insertLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /**
     * Insert data at a specific 0-based position.
     * Corresponds to Lesson 3 Activity: "Add new data in the middle of the linked list".
     * Time Complexity: O(N)
     */
    public boolean insertAt(int index, T data) {
        if (index < 0 || index > size) {
            return false;
        }
        if (index == 0) {
            insertFirst(data);
            return true;
        }
        Node<T> newNode = new Node<>(data);
        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        size++;
        return true;
    }

    /**
     * Remove first occurrence of element matching value.
     * Time Complexity: O(N)
     */
    public boolean delete(T data) {
        if (head == null) return false;

        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.next != null && !current.next.data.equals(data)) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
            size--;
            return true;
        }
        return false;
    }

    /**
     * Remove element at specific index.
     * Time Complexity: O(N)
     */
    public T deleteAt(int index) {
        if (index < 0 || index >= size || head == null) {
            return null;
        }
        if (index == 0) {
            T removed = head.data;
            head = head.next;
            size--;
            return removed;
        }

        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        T removed = current.next.data;
        current.next = current.next.next;
        size--;
        return removed;
    }

    /**
     * Get element at index.
     * Time Complexity: O(N)
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node<T> getHead() {
        return head;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Convert linked list to array for sorting/searching operations.
     */
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }
        return arr;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
