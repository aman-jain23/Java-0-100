# Lecture 38
## Topics: Lambdas and Functional Interface, Method Referencing and Composition.

**1. Lambdas and Functional Interfaces**

A **Functional Interface** is an interface with **exactly one abstract method**. It defines a single behavior contract. A **Lambda Expression** provides a clear, concise way to write that single method inline without creating an anonymous class.

* **Syntax:** `(parameters) -> expression` or `(parameters) -> { statements; }`

```java
@FunctionalInterface
interface TextFormatter {
    String format(String input); // Single abstract method
}

public class LambdaDemo {
    public static void main(String[] args) {
        // Lambda implementation of TextFormatter
        TextFormatter loudPrinter = text -> text.toUpperCase() + "!";
        
        System.out.println(loudPrinter.format("hello")); // Output: HELLO!
    }
}

```

---

**2. Method References and Composition**

**Method References** are shorthand readable syntax for lambdas that simply call an existing method. Instead of `(x) -> ClassName.method(x)`, you use `ClassName::method`.

* **Static Method:** `Math::max` equivalent to `(a, b) -> Math.max(a, b)`
* **Instance Method on Parameter:** `String::toLowerCase` equivalent to `(str) -> str.toLowerCase()`
* **Instance Method on Existing Object:** `System.out::println` equivalent to `(x) -> System.out.println(x)`
* **Constructor:** `ArrayList::new` equivalent to `() -> new ArrayList<>()`

**Composition** allows you to chain multiple functions or predicates together using default methods on functional interfaces (e.g., `andThen()`, `compose()`, `and()`, `or()`).

```java
import java.util.function.Function;
import java.util.function.Predicate;

public class ReferenceAndCompositionDemo {
    public static void main(String[] args) {
        // 1. Method Reference Shorthand
        Function<String, String> trimStr = String::trim; // Equivalent to s -> s.trim()
        Function<String, String> upperStr = String::toUpperCase; // Equivalent to s -> s.toUpperCase()

        // 2. Function Composition (Chaining with .andThen)
        Function<String, String> cleanAndUpper = trimStr.andThen(upperStr);
        System.out.println(cleanAndUpper.apply("  java lambdas  ")); // Output: JAVA LAMBDAS

        // 3. Predicate Composition
        Predicate<String> startsWithJ = s -> s.startsWith("J");
        Predicate<String> lengthIsFour = s -> s.length() == 4;

        // Combine predicates using .and()
        Predicate<String> validTopic = startsWithJ.and(lengthIsFour);

        System.out.println(validTopic.test("Java")); // true
        System.out.println(validTopic.test("JavaScript")); // false
    }
}

```
