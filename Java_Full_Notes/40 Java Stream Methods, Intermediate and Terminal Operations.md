# Lecture 40
## Topics: Java Stream Methods, Intermediate and Terminal Operations.

**1. Intermediate vs. Terminal Operations**

Stream operations are divided into two categories based on how they process data:

* **Intermediate Operations:** Transform a stream into another stream. They are **lazy** — they don't execute until a terminal operation is called. They can be chained together.
* **Terminal Operations:** Process the stream to produce a result (a list, a single value, or a side effect) and **close the stream**.

| Feature | Intermediate Operations | Terminal Operations |
| --- | --- | --- |
| **Return Type** | `Stream<T>` (or primitive streams) | Non-Stream (e.g., `List`, `long`, `Optional`, `void`) |
| **Execution** | Lazy (deferred) | Immediate |
| **Chainable?** | Yes (multiple allowed) | No (ends the pipeline) |

---

**2. Key Intermediate Operations**

```java
import java.util.List;
import java.util.stream.Stream;

public class IntermediateDemo {
    public static void main(String[] args) {
        List<String> names = List.of("Alice", "Bob", "Charlie", "Bob", "David");

        names.stream()
            // filter: Keeps elements matching a condition
            .filter(name -> name.length() > 3) 
            
            // map: Transforms each element
            .map(String::toUpperCase)          
            
            // distinct: Removes duplicates
            .distinct()                        
            
            // sorted: Sorts elements
            .sorted()                          
            
            // limit: Takes only first N elements
            .limit(2)                          
            
            .forEach(System.out::println);     // Output: ALICE, CHARLIE
            
        // flatMap: Flattens nested streams into a single stream
        List<List<String>> nested = List.of(List.of("A", "B"), List.of("C", "D"));
        List<String> flat = nested.stream()
            .flatMap(List::stream)             // Flattens Stream<List<String>> to Stream<String>
            .toList();                         // [A, B, C, D]
    }
}

```

---

**3. Key Terminal Operations**

```java
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TerminalDemo {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(5, 2, 8, 1, 9);

        // collect: Accumulates elements into a collection
        List<Integer> sortedList = numbers.stream()
            .sorted()
            .collect(Collectors.toList());    // Output: [1, 2, 5, 8, 9]

        // reduce: Combines elements into a single value
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);       // Output: 25

        // count: Returns total number of elements
        long count = numbers.stream().count(); // Output: 5

        // findFirst / findAny: Returns an Optional of an element
        Optional<Integer> first = numbers.stream().filter(n -> n > 5).findFirst(); // Optional[8]

        // anyMatch / allMatch / noneMatch: Returns boolean checks
        boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0); // true
    }
}

```

---

**4. Intermediate vs Terminal Pipeline Execution**

```java
List<String> list = List.of("one", "two", "three", "four");

// Nothing prints here because there is no terminal operation (Lazy evaluation)
Stream<String> stream = list.stream()
    .filter(s -> {
        System.out.println("Filter: " + s);
        return s.length() > 3;
    });

System.out.println("Terminal operation called:");
// Terminal operation triggers the entire pipeline element-by-element
stream.collect(Collectors.toList());

```
