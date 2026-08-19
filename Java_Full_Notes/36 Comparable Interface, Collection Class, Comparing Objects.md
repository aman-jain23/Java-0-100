# Lecture 36
## Topics: Comparable Interface, Collection Class, Comparing Objects.

In Java, comparing objects requires defining explicit sorting criteria. Java provides two primary mechanisms for this: the **`Comparable` interface** (for natural ordering) and the **`Comparator` interface** (for custom/multiple ordering rules). The utility class **`Collections`** leverages these interfaces to sort, search, and manipulate collections.

---

### 1. The `Comparable` Interface (Natural Ordering)

A class implements `Comparable<T>` to define its default or **natural ordering**.

It contains a single method: `compareTo(T o)`:

* Returns a **negative number** if `this` object is less than `o`.
* Returns **zero** if `this` object is equal to `o`.
* Returns a **positive number** if `this` object is greater than `o`.

```java
import java.util.*;

class Student implements Comparable<Student> {
    int id;
    String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Compare by student ID (Natural Order)
    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return id + ": " + name;
    }
}

public class ComparableDemo {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>(List.of(
            new Student(103, "Alice"),
            new Student(101, "Bob")
        ));

        // Uses Student's compareTo() automatically
        Collections.sort(students); 
        System.out.println(students); // Output: [101: Bob, 103: Alice]
    }
}

```

---

### 2. Comparing Objects: `Comparable` vs. `Comparator`

When you cannot modify a class source code or need **multiple sorting strategies** (e.g., sort by name, then by age), use `Comparator`.

| Feature | `Comparable` | `Comparator` |
| --- | --- | --- |
| **Package** | `java.lang` | `java.util` |
| **Primary Method** | `compareTo(Object o)` | `compare(Object o1, Object o2)` |
| **Purpose** | Defines a single "natural" sort rule | Defines alternative/custom sort rules |
| **Class Modification** | Modifies the original class | Does not modify the original class |

#### Modern `Comparator` Techniques (Java 8+)

```java
List<Student> students = new ArrayList<>(List.of(
    new Student(103, "Charlie"),
    new Student(101, "Alice")
));

// 1. Sort by name using Comparator.comparing()
students.sort(Comparator.comparing(s -> s.name));

// 2. Chaining comparators: sort by name, then by ID
students.sort(Comparator.comparing((Student s) -> s.name)
                        .thenComparingInt(s -> s.id));

```

---

### 3. The `Collections` Utility Class

`java.util.Collections` consists entirely of `static` methods that operate on or return collections.

#### A. Sorting & Searching

* `Collections.sort(List list)`: Sorts elements using `Comparable`.
* `Collections.sort(List list, Comparator c)`: Sorts using a custom `Comparator`.
* `Collections.binarySearch(List list, T key)`: Finds element index in $O(\log n)$ time (list **must** be sorted first).

#### B. Reordering & Modification

* `Collections.reverse(List list)`: Reverses the element sequence.
* `Collections.shuffle(List list)`: Randomizes element positions.
* `Collections.frequency(Collection c, Object o)`: Counts occurrences of an element.

#### C. Thread Safety & Read-Only Wrappers

* `Collections.synchronizedList(List list)`: Wraps a list to make it thread-safe.
* `Collections.unmodifiableList(List list)`: Returns a read-only view that throws `UnsupportedOperationException` on modification attempts.

```java
List<Integer> nums = new ArrayList<>(List.of(40, 10, 20, 30));

Collections.sort(nums);                   // [10, 20, 30, 40]
int index = Collections.binarySearch(nums, 30); // Returns index: 2
Collections.reverse(nums);                // [40, 30, 20, 10]

```
