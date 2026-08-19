# Lecture 32
## Topics: Java list interface, ArrayList, LinkedList, Vector, Stack.

The `List` interface extends `Collection` and represents an **ordered sequence** of elements. Unlike Sets, Lists allow **duplicates** and rely heavily on **zero-based indexing** to access, insert, or modify elements.

Here is a breakdown of the `List` interface and its four major implementation classes.

---

### 1. The `List` Interface

The `List` interface adds positional index operations to the standard `Collection` contract:

* `get(int index)`: Retrieves the element at the specified position.
* `set(int index, E element)`: Replaces the element at the index.
* `add(int index, E element)`: Inserts an element at a specific index, shifting existing elements to the right.
* `indexOf(Object o)`: Returns the index of the first occurrence of an element.

---

### 2. Implementation Classes Compared

#### A. ArrayList

* **Under the Hood:** A dynamic, resizable array.
* **How it grows:** Starts with an initial capacity (default 10). When full, it grows by **50%** of its current size ($1.5\times$).
* **Best used for:** Fast read/write access by index ($O(1)$).
* **Drawback:** Slow insertions/deletions in the middle ($O(n)$) because all subsequent elements must be shifted in memory.

#### B. LinkedList

* **Under the Hood:** A doubly linked list (each node points to its previous and next neighbors). It implements both `List` and `Deque` interfaces.
* **Best used for:** Frequent insertions or deletions at the head/tail or middle ($O(1)$ pointer reassignment).
* **Drawback:** Slow indexed lookup ($O(n)$) because it must traverse nodes sequentially from the head or tail. High memory overhead due to pointer objects.

#### C. Vector

* **Under the Hood:** A dynamic array similar to `ArrayList`.
* **How it grows:** Doubles its size ($2\times$) when full.
* **Thread Safety:** **Synchronized** — every method is locked for thread safety.
* **Best used for:** Legacy codebases. In modern Java, `Vector` is considered obsolete; `ArrayList` combined with `Collections.synchronizedList()` or concurrent collections is preferred due to performance overhead from automatic locking.

#### D. Stack

* **Under the Hood:** Extends `Vector` (and inherits its synchronized behavior). Represents a **LIFO** (Last-In, First-Out) data structure.
* **Core Methods:** `push(E item)`, `pop()`, `peek()`.
* **Modern Alternative:** `ArrayDeque` is strongly recommended over `Stack` for LIFO operations because `ArrayDeque` is unsynchronized and significantly faster.

---

### Summary & Performance Matrix

| Operation / Feature | ArrayList | LinkedList | Vector | Stack |
| --- | --- | --- | --- | --- |
| **Data Structure** | Resizable Array | Doubly Linked List | Resizable Array | Resizable Array (via Vector) |
| **Random Access (`get`)** | $O(1)$ Fast | $O(n)$ Slow | $O(1)$ Fast | $O(1)$ Fast |
| **Insert/Delete (Middle)** | $O(n)$ Slow | $O(1)$ Fast* | $O(n)$ Slow | $O(n)$ Slow |
| **Thread Safe?** | No | No | Yes | Yes |
| **Growth Factor** | $1.5\times$ | N/A (Node based) | $2.0\times$ | $2.0\times$ |

**Insertion in `LinkedList` is $O(1)$ once you already hold a reference to the target position node.*

---

### Code Example

```java
import java.util.*;

public class ListDemo {
    public static void main(String[] args) {
        // 1. ArrayList - Fast lookups
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        System.out.println("ArrayList lookup: " + arrayList.get(0)); // O(1)

        // 2. LinkedList - Efficient front insertion/removal via Deque API
        Deque<String> linkedList = new LinkedList<>();
        linkedList.addFirst("Front");
        linkedList.addLast("Back");
        System.out.println("LinkedList Front: " + linkedList.removeFirst());

        // 3. Stack - LIFO Operations
        Stack<String> stack = new Stack<>();
        stack.push("Base Layer");
        stack.push("Top Layer");
        System.out.println("Popped from Stack: " + stack.pop()); // Output: Top Layer
    }
}

```

