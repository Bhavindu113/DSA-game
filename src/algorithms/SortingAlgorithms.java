package algorithms;

import model.AlienEnemy;
import java.util.Comparator;

/**
 * SortingAlgorithms - Implementation of classic Sorting Algorithms.
 * Corresponds directly to General Sir John Kotelawala Defence University (KDU) IT22013 Lesson 9: Sorting Techniques.
 * 
 * @author Member D (ID: IT/IS/42/004) - Individual Contribution: 20%
 * Module: Alien Fleet Threat Hierarchy & In-Place Sorting Techniques
 * 
 * Includes:
 * 1. Insertion Sort (Lesson 9, Slides 11-12) - Best: O(N), Worst: O(N^2), Space: O(1)
 * 2. Bubble Sort (Lesson 9, Slides 7-8)       - Best: O(N), Worst: O(N^2), Space: O(1)
 * 3. Selection Sort (Lesson 9, Slides 9-10)   - Best: O(N^2), Worst: O(N^2), Space: O(1)
 * 
 * In-Game Application:
 * - Dynamically arranges active Alien Invader formations by descending Threat Level (HP * AttackPower)
 *   using Insertion Sort upon pressing 'T'.
 * - Telemetry benchmarks comparison counts, swap counts, and CPU execution nanoseconds for academic analysis.
 */
public class SortingAlgorithms {

    public static class SortResult {
        public int comparisons = 0;
        public int swaps = 0;
        public long durationNanos = 0;
        public String algorithmName;
    }

    /**
     * Insertion Sort - Lesson 9 Slide 11.
     * Builds sorted array one element at a time by inserting each item into its proper place.
     * Time Complexity: Best O(N), Worst O(N^2)
     */
    public static <T> SortResult insertionSort(T[] array, Comparator<T> comparator) {
        SortResult result = new SortResult();
        result.algorithmName = "Insertion Sort";
        long start = System.nanoTime();
        int n = array.length;

        for (int i = 1; i < n; i++) {
            T key = array[i];
            int j = i - 1;

            while (j >= 0) {
                result.comparisons++;
                if (comparator.compare(array[j], key) > 0) {
                    array[j + 1] = array[j];
                    result.swaps++;
                    j--;
                } else {
                    break;
                }
            }
            array[j + 1] = key;
        }
        result.durationNanos = System.nanoTime() - start;
        return result;
    }

    /**
     * Bubble Sort - Lesson 9 Slide 7.
     * Consecutive adjacent pairs are compared and swapped if out of order.
     * Time Complexity: Best O(N), Worst O(N^2)
     */
    public static <T> SortResult bubbleSort(T[] array, Comparator<T> comparator) {
        SortResult result = new SortResult();
        result.algorithmName = "Bubble Sort";
        long start = System.nanoTime();
        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                result.comparisons++;
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    T temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    result.swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        result.durationNanos = System.nanoTime() - start;
        return result;
    }

    /**
     * Selection Sort - Lesson 9 Slide 9.
     * Finds smallest element and exchanges it with element in first position.
     * Time Complexity: O(N^2)
     */
    public static <T> SortResult selectionSort(T[] array, Comparator<T> comparator) {
        SortResult result = new SortResult();
        result.algorithmName = "Selection Sort";
        long start = System.nanoTime();
        int n = array.length;

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                result.comparisons++;
                if (comparator.compare(array[j], array[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                T temp = array[minIdx];
                array[minIdx] = array[i];
                array[i] = temp;
                result.swaps++;
            }
        }
        result.durationNanos = System.nanoTime() - start;
        return result;
    }
}
