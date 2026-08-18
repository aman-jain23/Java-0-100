# Lecture 27
## Topics: Java Generics, Bounded types using extends

### 1. Generics (The Safety Label)
Without generics, a List in Java is like an unlabeled box: you can put anything inside it (Strings, Numbers, etc.). But when you pull something out, you have to guess what it is and cast it manually. If you guess wrong, your program crashes.

```
// UNLABELED BOX (No Generics)
List myBox = new ArrayList(); 
myBox.add("Apple");
myBox.add(100); // Java lets you do this, but it's dangerous!

// You have to force-cast it when pulling items out:
String item = (String) myBox.get(1); // CRASH! 100 is an Integer, not a String.
```
Generics let you put a label on the box using angle brackets <> to restrict what goes inside.
```
// LABELED BOX (With Generics)
List<String> stringBox = new ArrayList<>(); 
stringBox.add("Apple"); 
// stringBox.add(100); // COMPILER ERROR! Java stops you BEFORE you even run the code.

String item = stringBox.get(0); // No casting needed. Java KNOWS it's a String.
```

### 2. Generic Classes (Making Your Own Labeled Box)
Instead of hardcoding a class to only work with String or Integer, you use a placeholder like <T> (which just stands for Type).

```
// T is a placeholder for whatever data type the user chooses later
public class GlassBox<T> {
    private T item;

    public void put(T item) {
        this.item = item;
    }

    public T get() {
        return item;
    }
}
```
Now, you can re-use this exact same class for any type:

```
// Create a box specifically for Integers
GlassBox<Integer> intBox = new GlassBox<>();
intBox.put(50);

// Create a box specifically for Strings
GlassBox<String> strBox = new GlassBox<>();
strBox.put("Hello");
```

### 3. Bounded Types Using extends (Setting Limits)
Sometimes, a placeholder like <T> is too flexible. What if you want to write a calculator box, but someone puts a String or a Dog inside it? You can't calculate math on a Dog!

Using <T SomeClass extends> places an upper limit on your placeholder. It tells Java: "T can be any type, as long as it is a subclass of SomeClass."

```
// T MUST be Number or a child of Number (Integer, Double, Float, Long)
public class CalculatorBox<T extends Number> {
    private T number;

    public CalculatorBox(T number) {
        this.number = number;
    }

    public double square() {
        // Safe to call .doubleValue() because Java GUARANTEES T is a Number!
        return number.doubleValue() * number.doubleValue();
    }
}
```

How it behaves in code:

```
public class Main {
    public static void main(String[] args) {
        // WORKS: Integer extends Number
        CalculatorBox<Integer> calc1 = new CalculatorBox<>(5);
        System.out.println(calc1.square()); // Output: 25.0

        // WORKS: Double extends Number
        CalculatorBox<Double> calc2 = new CalculatorBox<>(2.5);
        System.out.println(calc2.square()); // Output: 6.25

        // ERROR: String does NOT extend Number! Java rejects this instantly.
        // CalculatorBox<String> calc3 = new CalculatorBox<>("Hello"); 
    }
}
```

--- 
# Lecture 28
## Topics: Wildcards in Generics, ? , ? extends , ? super

### 1. Unbounded Wildcard (?)
Meaning: "A list of some unknown type."

Use this when your method only performs operations that don't depend on the specific type inside the collection (e.g., printing elements, checking size, clearing list).

```
import java.util.List;

public class UnboundedExample {
    // Accepts a List of ANY type
    public static void printList(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
    public static void main(String[] args) {
        List<String> names = List.of("Aman", "Rahul");
        List<Integer> numbers = List.of(10, 20, 30);
        printList(names);   // Works!
        printList(numbers); // Works!
    }
}
```
### 2. Upper Bounded Wildcard (? extends T)
Meaning: "A list of T or any subclass of T."

This sets a ceiling (upper limit). It allows you to READ data safely from a structure, but you CANNOT WRITE (add) new data to it (because Java doesn't know the exact subtype at runtime).

```
import java.util.List;

public class UpperBoundExample {
    // Accepts List of Number, Integer, Double, Float, etc.
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number num : list) {
            sum += num.doubleValue(); // Safe to READ as Number
        }
        return sum;
    }
    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.5, 2.5);
        System.out.println(sumOfList(intList));    // Output: 6.0
        System.out.println(sumOfList(doubleList)); // Output: 4.0
        // READ-ONLY RESTRICTION:
        // list.add(10); // COMPILE ERROR! Java prevents adding to ? extends
    }
}
```

### 3. Lower Bounded Wildcard (? super T)
Meaning: "A list of T or any superclass of T."

This sets a floor (lower limit). It allows you to WRITE (add) elements of type T safely into the collection.
``` 
import java.util.ArrayList;
import java.util.List;

public class LowerBoundExample {
    // Accepts List of Integer, Number, or Object
    public static void addNumbers(List<? super Integer> list) {
        // Safe to WRITE Integers because the list is guaranteed to hold Integer or its parent types
        list.add(10);
        list.add(20);
    }

    public static void main(String[] args) {
        List<Integer> intList = new ArrayList<>();
        List<Number> numList = new ArrayList<>();
        List<Object> objList = new ArrayList<>();

        addNumbers(intList); // Works!
        addNumbers(numList); // Works!
        addNumbers(objList); // Works!

        System.out.println(intList); // [10, 20]
    }
}
```
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

