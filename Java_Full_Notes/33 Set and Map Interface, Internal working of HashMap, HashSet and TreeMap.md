# Lecture 33
## Topics: Set and Map Interface, Internal working of HashMap, HashSet and TreeMap.

Both **Set** and **Map** are fundamental parts of the Java Collections Framework, but they store data differently: a `Set` models a collection of unique elements, whereas a `Map` models key-value lookup pairs.

---

### 1. Set vs. Map Interface Overview

* **`Set<E>`**: Extends `Collection<E>`. It guarantees **no duplicate elements** (`e1.equals(e2)` is never true).
* **`Map<K, V>`**: Does **not** extend `Collection`. It maps unique keys to values. Keys cannot be duplicated, but values can.

| Feature | `Set` Implementations | `Map` Implementations |
| --- | --- | --- |
| **Unordered & Fast** | `HashSet` | `HashMap` |
| **Sorted by Value/Key** | `TreeSet` | `TreeMap` |
| **Insertion Order** | `LinkedHashSet` | `LinkedHashMap` |

---

### 2. Internal Working of `HashMap`

A `HashMap` uses an **Array of Nodes** (also called "buckets") to store data, relying on **Hashing** for $O(1)$ constant-time lookup.

#### How `put(key, value)` Works Step-by-Step:

1. **Calculate Hash:** Java calls `key.hashCode()` and applies a hash function to compute the bucket array index:

$$\text{index} = \text{hash} \ \& \ (n - 1)$$


2. **Handle Collisions:**
* If the bucket is **empty**, a new `Node(hash, key, value, next)` is placed there.
* If two keys hash to the **same index** (a collision), the new node is appended to a **LinkedList** at that bucket.


3. **Treeification (Java 8 Optimization):**
* If a single bucket's linked list grows past **8 nodes** and the total capacity is at least 64, Java converts that LinkedList into a **Red-Black Tree**. This improves search time from $O(n)$ down to $O(\log n)$ for heavily collided buckets.


4. **Resizing:**
* Default initial capacity is **16**, with a default **load factor of 0.75**. When the map becomes 75% full ($16 \times 0.75 = 12$ items), the array size doubles to 32 and rehashes all entries.



#### How `get(key)` Works:

1. Calculates the bucket index using `key.hashCode()`.
2. Searches through the bucket's node chain (or Red-Black tree) using `key.equals()` to find the exact match.

---

### 3. Internal Working of `HashSet`

Here is the secret of `HashSet`: **It does not have its own unique lookup mechanism.**

Under the hood, a `HashSet` is literally just a wrapper around a `HashMap` instance!

```java
public class HashSet<E> {
    private transient HashMap<E, Object> map;
    
    // Dummy value stored alongside every element key in the backing HashMap
    private static final Object PRESENT = new Object();

    public HashSet() {
        map = new HashMap<>();
    }

    public boolean add(E e) {
        // Your element is stored as the KEY. PRESENT is stored as the dummy VALUE.
        return map.put(e, PRESENT) == null; 
    }
}

```

Because `HashMap` enforces unique keys, `HashSet` automatically guarantees unique elements for free.

---

### 4. Internal Working of `TreeMap`

Unlike `HashMap`, `TreeMap` does **not** use hashing or array buckets. Instead, it relies on a self-balancing binary search tree called a **Red-Black Tree**.

#### Key Characteristics:

* **Sorted Order:** Elements are stored in natural sorted order (via `Comparable`) or by a custom `Comparator` passed at creation.
* **Performance:** Insertions, deletions, and lookups run in **$O(\log n)$** time.
* **No Hash Dependency:** `TreeMap` ignores `hashCode()` and `equals()`. It compares keys strictly using `compareTo()` or `compare()`. If `compareTo()` returns `0`, the keys are treated as identical.

> **TreeSet Connection:** Just like `HashSet` wraps a `HashMap`, `TreeSet` is simply a wrapper around a `NavigableMap` (specifically `TreeMap`).
