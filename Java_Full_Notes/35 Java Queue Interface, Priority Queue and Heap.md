# Lecture 35
## Topics: Java Queue Interface, Priority Queue and Heap.

The `Queue` interface models collections designed to hold elements prior to processing, typically ordering elements in a **FIFO (First-In, First-Out)** sequence or based on natural/custom priority.

---

### 1. The Java Queue Interface

The `Queue` interface extends `Collection<E>`. It provides two sets of methods for key operations: one set throws an exception on failure, while the other returns a special value (`null` or `false`).

| Operation | Throws Exception | Returns Special Value | Behavior |
| --- | --- | --- | --- |
| **Insert** | `add(e)` | `offer(e)` | Inserts an element at the back. |
| **Remove** | `remove()` | `poll()` | Retrieves and removes the element at the head. |
| **Examine** | `element()` | `peek()` | Retrieves, but does not remove, the element at the head. |

> **Best Practice:** Use `offer()`, `poll()`, and `peek()` when working with queues, as handling `null` returns is safer than dealing with runtime exceptions in capacity-restricted queues.

---

### 2. Internal Mechanics: PriorityQueue and Binary Heap

A `PriorityQueue` process elements based on priority rather than insertion order. By default, elements are ordered according to their **natural order** (e.g., numbers in ascending order) or by a custom `Comparator`.

Under the hood, `PriorityQueue` is backed by a dynamic array representing a **Binary Min-Heap**.

#### How a Binary Heap Works in Array Storage:

In a Binary Heap, elements form a complete binary tree where every parent node is smaller than or equal to its children (in a Min-Heap). This tree is stored efficiently in a single flat array (`Object[]`):

* **Parent node at index $i$:**

$$\text{Left Child Index} = 2i + 1$$


$$\text{Right Child Index} = 2i + 2$$


* **Child node at index $k$:**

$$\text{Parent Index} = \frac{k - 1}{2}$$



#### Heap Operations & Time Complexities:

* **`offer(e)` / Insertion — $O(\log n)$:** The element is appended at the end of the array and **percolates up** (swaps places with its parent) until the heap property is restored.
* **`poll()` / Extraction — $O(\log n)$:** The root element (index 0) is removed. The last element in the array moves to index 0 and **percolates down** (swaps places with its smaller child) until order is restored.
* **`peek()` / Inspection — $O(1)$:** Directly returns the element at index 0 without modifying the array.

---

### 3. PriorityQueue Code Examples

#### A. Min-Heap (Default Natural Order)

```java
import java.util.PriorityQueue;
import java.util.Queue;

public class MinHeapDemo {
    public static void main(String[] args) {
        // Natural ordering yields a Min-Heap (smallest element comes out first)
        Queue<Integer> pq = new PriorityQueue<>();
        pq.offer(40);
        pq.offer(10);
        pq.offer(30);

        // Min element is retrieved first
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " "); // Output: 10 30 40
        }
    }
}

```

#### B. Max-Heap (Custom Comparator)

```java
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

public class MaxHeapDemo {
    public static void main(String[] args) {
        // Reverse order Comparator transforms it into a Max-Heap
        Queue<Integer> maxPq = new PriorityQueue<>(Collections.reverseOrder());
        maxPq.offer(40);
        maxPq.offer(10);
        maxPq.offer(30);

        System.out.println(maxPq.poll()); // Output: 40 (largest element first)
    }
}

```
