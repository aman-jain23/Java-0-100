# Lecture 39
## Topics: Java Streams, Creation and Architecture.

**1. What is a Java Stream?**

A **Stream** in Java (introduced in Java 8 via `java.util.stream`) is a sequence of elements that supports sequential and parallel aggregate operations.

Unlike collections, streams:

* **Do not store data:** They act as a pipeline to convey and transform data from a source (like a `List`, `Set`, or `Array`).
* **Do not modify the source:** Operations on a stream produce a new result without altering the original collection.
* **Are lazy-evaluated:** Intermediate operations are executed only when a terminal operation is invoked.
* **Are single-use:** A stream cannot be reused once a terminal operation executes.

---

**2. Stream Architecture: The 3-Stage Pipeline**

Every stream operation follows a pipeline structure with three distinct phases:

```
[ Data Source ] ──> [ Intermediate Operations (Lazy) ] ──> [ Terminal Operation ]

```

| Phase | Description | Examples |
| --- | --- | --- |
| **1. Source** | Where the data originates (Collection, Array, I/O channel). | `list.stream()`, `Arrays.stream()` |
| **2. Intermediate Operations** | Transforms the stream into another stream. **Lazy**: Executes only when a terminal operation triggers it. | `map()`, `filter()`, `sorted()`, `distinct()`, `limit()` |
| **3. Terminal Operation** | Consumes the stream to produce a non-stream result (a value, a collection, or a side effect). | `collect()`, `forEach()`, `reduce()`, `count()`, `findFirst()` |

---

**3. Ways to Create a Stream**

```java
import java.util.*;
import java.util.stream.*;

public class StreamCreationDemo {
    public static void main(String[] args) {
        // 1. From Collections
        List<String> names = List.of("Anna", "Bob", "Charlie");
        Stream<String> fromList = names.stream();

        // 2. From Arrays
        String[] colors = {"Red", "Green", "Blue"};
        Stream<String> fromArray = Arrays.stream(colors);

        // 3. Direct Values (Stream.of)
        Stream<Integer> directValues = Stream.of(10, 20, 30);

        // 4. Infinite Streams
        Stream<Integer> generated = Stream.generate(() -> 42).limit(3);     // [42, 42, 42]
        Stream<Integer> iterated = Stream.iterate(0, n -> n + 2).limit(3);  // [0, 2, 4]

        // 5. Primitive Streams (Avoids boxing overhead)
        IntStream range = IntStream.range(1, 5); // 1, 2, 3, 4
    }
}

```

---

**4. End-to-End Practical Example**

```java
import java.util.*;
import java.util.stream.Collectors;

public class StreamPipelineExample {
    public static void main(String[] args) {
        List<String> fruits = List.of("apple", "banana", "avocado", "apricot", "blueberry");

        // Goal: Find fruits starting with 'a', convert to uppercase, and collect to a List
        List<String> result = fruits.stream()                   // 1. Source
            .filter(f -> f.startsWith("a"))                      // 2. Intermediate Op
            .map(String::toUpperCase)                           // 2. Intermediate Op
            .sorted()                                           // 2. Intermediate Op
            .collect(Collectors.toList());                      // 3. Terminal Op

        System.out.println(result); // Output: [APPLE, APRICOT, AVOCADO]
    }
}

```
