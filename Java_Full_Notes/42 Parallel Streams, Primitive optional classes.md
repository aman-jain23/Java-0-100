# Lecture 42
## Topics: Parallel Streams, Primitive optional classes.

Here is a clear breakdown of **Parallel Streams** and **Primitive Optional Classes** in Java with code examples.

---

### 1. Parallel Streams

#### Concept

By default, Java streams are **sequential** (single-threaded). A **Parallel Stream** automatically splits tasks across multiple CPU cores using the common `ForkJoinPool`, enabling simultaneous processing of large datasets.

#### Creation Methods

1. Call `.parallelStream()` on any `Collection`.
2. Call `.parallel()` on an existing sequential stream.

```java
import java.util.List;

public class ParallelStreamDemo {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8);

        // Sequential Execution (Runs on a single thread in order)
        System.out.println("--- Sequential Stream ---");
        numbers.stream()
                .forEach(n -> System.out.println(Thread.currentThread().getName() + " processing: " + n));

        // Parallel Execution (Split across multi-threaded pool, order is non-deterministic)
        System.out.println("\n--- Parallel Stream ---");
        numbers.parallelStream()
                .forEach(n -> System.out.println(Thread.currentThread().getName() + " processing: " + n));
    }
}

```

#### Performance Considerations

| Use Parallel Streams When | Avoid Parallel Streams When |
| --- | --- |
| Processing **large datasets** ($N > 10,000$). | Datasets are small (thread management overhead outweighs benefits). |
| Operations are **CPU-intensive** or compute-heavy. | Tasks involve **shared mutable state** (causes race conditions). |
| Data structures split easily (e.g., `ArrayList`, arrays). | Data structures split poorly (e.g., `LinkedList`). |
| Element order does not matter. | Operations rely on explicit ordering (`findFirst`, stateful filters). |

---

### 2. Primitive Optional Classes

#### The Problem with `Optional<T>`

Using `Optional<Integer>`, `Optional<Double>`, or `Optional<Long>` requires **autoboxing** and **unboxing** between primitive types and their object wrappers (e.g., `int` $\leftrightarrow$ `Integer`). This introduces extra memory allocation and performance overhead.

#### The Solution

Java 8 provides specialized primitive wrappers:

* **`OptionalInt`** (for `int`)
* **`OptionalLong`** (for `long`)
* **`OptionalDouble`** (for `double`)

#### Key Method Differences

Instead of `.get()`, primitive optionals use type-specific getters like `.getAsInt()`, `.getAsDouble()`, or `.getAsLong()`.

```java
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class PrimitiveOptionalDemo {
    public static void main(String[] args) {
        // 1. Creating Primitive Optionals manually
        OptionalInt optNum = OptionalInt.of(42);
        OptionalInt emptyOpt = OptionalInt.empty();

        // Safe retrieval
        int val = optNum.orElse(0);
        System.out.println("Value: " + val); // Output: Value: 42

        if (emptyOpt.isEmpty()) {
            System.out.println("emptyOpt contains no value");
        }

        // 2. Natural integration with Primitive Streams (IntStream, DoubleStream)
        IntStream numbers = IntStream.of(10, 20, 30, 40, 50);

        // IntStream.average() returns an OptionalDouble
        OptionalDouble avg = numbers.average();

        avg.ifPresent(a -> System.out.println("Average: " + a)); // Output: Average: 30.0

        // Handling empty streams safely
        IntStream emptyStream = IntStream.empty();
        double resultAvg = emptyStream.average().orElse(0.0);
        System.out.println("Empty stream average: " + resultAvg); // Output: 0.0
    }
}

```

---

### Comparison Summary

```java
// Generic Optional (uses autoboxing overhead)
Optional<Integer> genericOpt = Optional.of(100);
int val1 = genericOpt.get(); 

// Primitive Optional (zero boxing, optimized performance)
OptionalInt primitiveOpt = OptionalInt.of(100);
int val2 = primitiveOpt.getAsInt(); 

```
