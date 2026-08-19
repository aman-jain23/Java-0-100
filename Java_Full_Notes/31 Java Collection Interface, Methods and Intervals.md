# Lecture 31
## Topics: Java Collection Interface, Methods and Intervals.

The **Java Collection Interface** is the root of the standard Java Collections Framework hierarchy (for lists, sets, and queues). It defines the fundamental operations that *every* single-value container in Java must support.

Here is a clear breakdown of the **Collection Interface**, its **Core Methods**, and how **Intervals** (Ranges/Sub-lists) work in Java.

---

### 1. The Collection Interface

Think of `Collection<E>` as the master blueprint. It sits directly underneath `Iterable<E>` and provides a uniform API regardless of whether you are working with an `ArrayList`, a `HashSet`, or a `LinkedList`.

```
         Iterable<E>
              │
         Collection<E>
   ┌──────────┼──────────┐
   ▼          ▼          ▼
 List<E>    Set<E>    Queue<E>

```

> **Note:** `Map<K,V>` is **not** part of the `Collection` interface hierarchy because it stores key-value pairs rather than single elements.

---

### 2. Core Methods of `Collection`

Because all concrete collections implement this interface, these methods work identically across Lists, Sets, and Queues:

#### Basic Operations

* `add(E e)`: Adds an element. Returns `true` if changed.
* `remove(Object o)`: Removes a single instance of the element.
* `contains(Object o)`: Checks if the element exists (`true`/`false`).
* `size()`: Returns the number of elements.
* `isEmpty()`: Checks if `size() == 0`.
* `clear()`: Removes all elements.

#### Bulk Operations

* `addAll(Collection c)`: Adds all elements from another collection.
* `removeAll(Collection c)`: Removes all elements that exist in the target collection.
* `containsAll(Collection c)`: Checks if *all* specified elements exist.
* `retainAll(Collection c)`: Keeps **only** elements that are present in both (intersection).

```java
import java.util.*;

public class CollectionMethodsDemo {
    public static void main(String[] args) {
        Collection<String> groupA = new HashSet<>(List.of("Alice", "Bob", "Charlie"));
        Collection<String> groupB = new ArrayList<>(List.of("Bob", "Charlie", "David"));

        // Bulk operations example: Retain only common elements
        groupA.retainAll(groupB); 
        System.out.println("Intersection: " + groupA); // Output: [Bob, Charlie]

        // Check containment
        System.out.println("Contains Bob? " + groupA.contains("Bob")); // Output: true
    }
}

```

---

### 3. Intervals (Ranges & Sub-Views)

In Java, working with an **interval** (a specific range or slice of a collection) depends on the collection type you are using.

Java uses **half-open intervals**: `[fromIndex, toIndex)` — meaning the starting index is **included**, but the ending index is **excluded**.

#### A. Intervals in Lists (`subList`)

`List` provides the `subList(int fromIndex, int toIndex)` method. Crucially, this returns a **view** of the original list, not a new copy. Changes to the sublist directly affect the parent list.

```java
import java.util.*;

public class ListIntervalDemo {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(List.of(10, 20, 30, 40, 50, 60));

        // Get interval from index 1 (inclusive) to 4 (exclusive) -> elements 20, 30, 40
        List<Integer> interval = numbers.subList(1, 4);
        System.out.println("SubList Interval: " + interval); // [20, 30, 40]

        // Modifying the interval modifies the original list!
        interval.clear(); // Removes 20, 30, 40 from the parent list
        System.out.println("Original List after clear: " + numbers); // [10, 50, 60]
    }
}

```

#### B. Intervals in Sorted Sets (`SortedSet` / `TreeSet`)

For ordered sets, Java provides methods to extract range-based intervals based on actual element values rather than numeric indices:

* `subSet(fromElement, toElement)`: Range between two elements `[from, to)`.
* `headSet(toElement)`: Everything strictly less than `toElement`.
* `tailSet(fromElement)`: Everything greater than or equal to `fromElement`.

```java
import java.util.*;

public class TreeSetIntervalDemo {
    public static void main(String[] args) {
        TreeSet<Integer> scores = new TreeSet<>(List.of(10, 25, 40, 55, 70, 85, 100));

        // Interval between 25 (inclusive) and 70 (exclusive)
        SortedSet<Integer> midScores = scores.subSet(25, 70);
        System.out.println("Scores in interval [25, 70): " + midScores); // [25, 40, 55]

        // Interval of all scores >= 70
        System.out.println("Top Scores: " + scores.tailSet(70)); // [70, 85, 100]
    }
}
```
