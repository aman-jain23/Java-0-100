# Lecture 37
## Topics: Java Lambdas and Functional interfaces, Comparator Interface.

**1. Functional Interfaces & Lambda Expressions**

A **Functional Interface** is an interface that contains **exactly one abstract method**. It acts as the blueprint for a single action.

A **Lambda Expression** is a shortcut to write an anonymous method that implements a functional interface. It eliminates boilerplate code like class declarations and method names.

* **Syntax:** `(parameters) -> { body }`

**Example:**

```java
// Functional Interface ( annotated with @FunctionalInterface for clarity )
@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}

public class LambdaExample {
    public static void main(String[] args) {
        // Old Way: Anonymous Inner Class
        MathOperation additionOld = new MathOperation() {
            @Override
            public int operate(int a, int b) {
                return a + b;
            }
        };

        // Lambda Way: Concise and clean
        MathOperation addition = (a, b) -> a + b;
        MathOperation multiplication = (a, b) -> a * b;

        System.out.println("Add: " + addition.operate(5, 3));        // Output: 8
        System.out.println("Multiply: " + multiplication.operate(5, 3)); // Output: 15
    }
}

```

---

**2. The Comparator Interface**

The `Comparator<T>` interface is used to define custom sorting logic for objects without modifying the original class. Because `Comparator` is a **functional interface** (its single abstract method is `compare(T o1, T o2)`), it works seamlessly with Lambda expressions.

* Returns a **negative integer** if `o1 < o2`
* Returns **zero** if `o1 == o2`
* Returns a **positive integer** if `o1 > o2`

**Example:**

```java
import java.util.*;

class Student {
    String name;
    int age;

    Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}

public class ComparatorExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 22),
            new Student("Bob", 19),
            new Student("Charlie", 25)
        );

        // Sort by age using a Lambda Comparator
        Collections.sort(students, (s1, s2) -> Integer.compare(s1.age, s2.age));
        System.out.println("Sorted by Age: " + students);

        // Sort by name using Comparator.comparing() helper
        students.sort(Comparator.comparing(s -> s.name));
        System.out.println("Sorted by Name: " + students);
    }
}

```
