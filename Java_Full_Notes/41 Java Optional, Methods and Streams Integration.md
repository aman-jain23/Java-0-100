# Lecture 41
## Topics: Java Optional, Methods and Streams Integration.

Both **`Optional`** and **Stream Methods** were introduced in Java 8 to make code cleaner, safer, and more expressive by moving away from explicit null checks and verbose `for` loops.

---

### 1. Java Optional (`java.util.Optional<T>`)

#### The Problem

Before Java 8, accessing a reference without checking for `null` caused the infamous `NullPointerException` (NPE).

```java
// Without Optional
String name = getUserName(); // might return null
System.out.println(name.toUpperCase()); // Throws NullPointerException if name is null

```

#### The Solution

`Optional<T>` is a single-element container that either holds a non-null value (**present**) or holds nothing (**empty**). It forces you to explicitly handle the possibility of a missing value.

#### Creating an `Optional`

```java
// 1. Empty Optional
Optional<String> emptyOpt = Optional.empty();

// 2. Non-null value (throws NPE immediately if value is null)
Optional<String> nameOpt = Optional.of("Alex");

// 3. Value that might be null (returns empty Optional if null)
String unknownName = null;
Optional<String> nullableOpt = Optional.ofNullable(unknownName);

```

#### Common Methods & Examples

* **`orElse()`**: Provides a fallback value if empty.
* **`orElseGet()`**: Computes a fallback value via a Supplier (useful for heavy computations).
* **`ifPresent()`**: Executes a lambda only if a value exists.
* **`map()`**: Transforms the contained value if present.

```java
import java.util.Optional;

public class OptionalDemo {
    public static void main(String[] args) {
        String name = getDbUserName(101); // Returns null

        // Wrap in Optional
        Optional<String> optName = Optional.ofNullable(name);

        // Fallback value
        String result = optName.orElse("Guest User");
        System.out.println("User: " + result); // Output: User: Guest User

        // Execute action if present
        Optional<String> validName = Optional.of("John");
        validName.ifPresent(n -> System.out.println("Hello, " + n.toUpperCase())); 
        // Output: Hello, JOHN

        // Transform value with map()
        String uppercaseName = optName
                .map(String::toUpperCase)
                .orElse("UNKNOWN");
        System.out.println(uppercaseName); // Output: UNKNOWN
    }

    private static String getDbUserName(int id) {
        return null; // Simulating missing record
    }
}

```

---

### 2. Methods and Streams Integration

Streams allow functional-style operations on collections (filtering, mapping, reducing) without mutating the underlying data. Integrating standard methods and method references into Stream pipelines makes processing data concise.

#### Common Stream Pipeline Methods

* **`filter(Predicate)`**: Retains elements matching a condition.
* **`map(Function)`**: Transforms each element.
* **`sorted(Comparator)`**: Sorts the elements.
* **`collect(Collector)`**: Accumulates elements into a collection (e.g., `List`, `Set`).
* **`forEach(Consumer)`**: Executes an action for each element.

#### Example 1: Filtering and Transforming a List

Here, method references like `String::toUpperCase` and `System.out::println` are integrated directly into the stream steps.

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamMethodsDemo {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("rahul", "amit", "rohit", "anita", "suresh");

        // Goal: Find names starting with 'r', convert them to uppercase, and collect into a list
        List<String> rNames = names.stream()
                .filter(name -> name.startsWith("r")) // Filter names starting with 'r'
                .map(String::toUpperCase)             // Transform using method reference
                .sorted()                             // Sort alphabetically
                .collect(Collectors.toList());        // Collect result

        System.out.println(rNames); // Output: [RAHUL, ROHIT]
    }
}

```

#### Example 2: Integrating Custom Class Methods

You can pass methods of custom objects directly into streams.

```java
import java.util.List;

class Employee {
    private String name;
    private double salary;

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() { return name; }
    public double getSalary() { return salary; }
}

public class CustomStreamDemo {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("Alice", 75000),
            new Employee("Bob", 45000),
            new Employee("Charlie", 90000)
        );

        // Calculate total salary of employees earning more than 50,000
        double totalHighSalary = employees.stream()
                .filter(e -> e.getSalary() > 50000)   // Custom method call
                .mapToDouble(Employee::getSalary)     // Method reference on custom getter
                .sum();                               // Stream numeric reduction method

        System.out.println("Total High Salary: " + totalHighSalary); // Output: 165000.0
    }
}

```

---

### Combining `Optional` and Streams

Streams and `Optional` work together seamlessly. Starting in Java 9, `Optional` has a `.stream()` method, making it easy to convert an `Optional` into a `Stream` of 0 or 1 element.

```java
import java.util.List;
import java.util.Optional;

public class StreamOptionalIntegration {
    public static void main(String[] args) {
        List<Optional<String>> optionalList = List.of(
            Optional.of("Java"),
            Optional.empty(),
            Optional.of("C++"),
            Optional.empty()
        );

        // Filter out empty Optionals and collect present values
        List<String> validLanguages = optionalList.stream()
                .flatMap(Optional::stream) // Converts each Optional into a Stream of 0 or 1 item
                .map(String::toUpperCase)
                .toList();

        System.out.println(validLanguages); // Output: [JAVA, C++]
    }
}

```
