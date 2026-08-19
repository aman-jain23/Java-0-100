# Lecture 29
## Topics: Java Collection Framework, Data Structures and complete heirarchy.

The Java Collection Framework (JCF) is a set of readymade classes and interfaces that implement standard data structures (like lists, stacks, queues, and maps) so you don't have to code them from scratch.

## The Complete JCF Hierarchy
The framework is divided into two primary roots:

### 1. Collection Interface: For single elements (List, Set, Queue).
### 2. Map Interface: For key-value pairs (Does not inherit from Collection).
```
Iterable<E> (Interface)
                      │
                 Collection<E> (Interface)
    ┌─────────────────┼─────────────────┐
    │                 │                 │
 List<E>           Set<E>            Queue<E>
 (Interface)      (Interface)       (Interface)
    │                 │                 │
    ├─ ArrayList      ├─ HashSet        ├─ PriorityQueue
    ├─ LinkedList     ├─ LinkedHashSet  └─ Deque (Interface)
    └─ Vector         └─ SortedSet          │
                          │                 └─ ArrayDeque
                      TreeSet

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                  Map<K, V> (Interface)
    ┌─────────────────┼─────────────────┐
 HashMap         LinkedHashMap       SortedMap
                                        │
                                     TreeMap
```
# Core Components & Data Structures Explained
## 1. List Interface (Ordered & Allows Duplicates)

### ArrayList: Backed by a dynamically resizing array. Very fast for reading items by index $O(1)$, but slow for insertions/deletions in the middle $O(n)$ because elements must shift.
### LinkedList: Backed by a doubly-linked list. Fast for insertions/deletions $O(1)$ once positioned, but slower for random access $O(n)$.

```
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListDemo {
    public static void main(String[] args) {
        // ArrayList: Best for searching & indexing
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        arrayList.add("Java"); // Allows duplicates!
        System.out.println("ArrayList Index 1: " + arrayList.get(1)); // Fast lookup

        // LinkedList: Best for frequent insertions/deletions
        List<Integer> linkedList = new LinkedList<>();
        linkedList.add(10);
        linkedList.add(20);
        linkedList.remove(0); // Removes 10 efficiently
    }
}
```

## 2. Set Interface (Unique Elements Only)
### HashSet: Backed by a Hash Table. Unordered. Provides blazing-fast lookup, insertion, and deletion $O(1)$.
### LinkedHashSet: Same as HashSet, but maintains the insertion order of elements.
### TreeSet: Backed by a Red-Black tree. Elements are automatically sorted. Slower lookup $O(\log n)$.

```
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SetDemo {
    public static void main(String[] args) {
        // HashSet: Fast, no order, no duplicates
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Banana");
        hashSet.add("Apple");
        hashSet.add("Apple"); // Duplicate ignored
        System.out.println("HashSet (Unordered): " + hashSet);

        // TreeSet: Sorted automatically
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(40);
        treeSet.add(10);
        treeSet.add(25);
        System.out.println("TreeSet (Sorted): " + treeSet); // Output: [10, 25, 40]
    }
}
```
## 3. Queue & Deque Interface (FIFO & Double-Ended)

### PriorityQueue: Elements are ordered based on priority (min-heap by default), not insertion time.
### ArrayDeque: Double-ended queue. You can add/remove from both ends. It is faster than Stack and LinkedList for stack/queue usage.

```
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

public class QueueDemo {
    public static void main(String[] args) {
        // PriorityQueue: Lowest value comes out first
        Queue<Integer> pq = new PriorityQueue<>();
        pq.add(30);
        pq.add(10);
        pq.add(20);
        System.out.println("Poll highest priority: " + pq.poll()); // Removes & returns 10

        // ArrayDeque: Use as Stack (LIFO) or Queue (FIFO)
        Deque<String> stack = new ArrayDeque<>();
        stack.push("First");
        stack.push("Second");
        System.out.println("Pop from Stack: " + stack.pop()); // Output: Second
    }
}
```
## 4. Map Interface (Key-Value Pairs, Unique Keys)

### HashMap: Fast $O(1)$, unordered key-value pairs.
### LinkedHashMap: Key-value pairs that retain insertion order.
### TreeMap: Keys are sorted automatically $O(\log n)$.

```
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapDemo {
    public static void main(String[] args) {
        // HashMap
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Aman", 95);
        scores.put("Rahul", 88);
        scores.put("Aman", 99); // Overwrites previous value for key "Aman"
        System.out.println("Aman's score: " + scores.get("Aman"));

        // TreeMap (Keys sorted alphabetically)
        Map<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("Zebra", "Animal");
        sortedMap.put("Apple", "Fruit");
        System.out.println("TreeMap Keys: " + sortedMap.keySet()); // [Apple, Zebra]
    }
}
```
