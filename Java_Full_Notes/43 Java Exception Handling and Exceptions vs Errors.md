# Lecture 43
## Topics: Java Exception Handling and Exceptions vs Errors.

Exception handling is Java’s mechanism to handle runtime errors gracefully so that the program doesn't crash unexpectedly.

---

### 1. Exceptions vs. Errors

Both `Exception` and `Error` inherit from the `java.lang.Throwable` class, but they represent fundamentally different issues.

```
                  Throwable
                      │
        ┌─────────────┴─────────────┐
        │                           │
      Error                     Exception
  (Unrecoverable)                   │
                       ┌────────────┴────────────┐
                       │                         │
               Checked Exception        Unchecked Exception
             (Compile-time check)        (RuntimeException)

```

| Feature | Exception | Error |
| --- | --- | --- |
| **Origin** | Caused by application code or external conditions (e.g., missing file, invalid input). | Caused by severe system failures or JVM memory issues. |
| **Recoverability** | **Recoverable** — can be caught and handled using `try-catch`. | **Irrecoverable** — the program should terminate; catching them is discouraged. |
| **Type** | Checked (`IOException`, `SQLException`) or Unchecked (`NullPointerException`). | Always unchecked. |
| **Examples** | `FileNotFoundException`, `ArithmeticException`, `NumberFormatException`. | `OutOfMemoryError`, `StackOverflowError`, `NoClassDefFoundError`. |

---

### 2. Exception Hierarchy: Checked vs. Unchecked

#### Checked Exceptions

* Checked by the compiler at compile-time.
* The compiler forces you to either catch them (`try-catch`) or declare them in the method signature (`throws`).
* **Examples:** `IOException`, `SQLException`, `ClassNotFoundException`.

```java
import java.io.FileReader;
import java.io.FileNotFoundException;

public class CheckedExceptionDemo {
    // Must declare 'throws' or wrap in a try-catch, otherwise compilation fails
    public static void main(String[] args) throws FileNotFoundException {
        FileReader file = new FileReader("non_existent_file.txt");
    }
}

```

#### Unchecked Exceptions (`RuntimeException`)

* Occur at runtime due to logical bugs or improper API usage.
* Not checked by the compiler, so explicit handling is optional.
* **Examples:** `NullPointerException`, `ArrayIndexOutOfBoundsException`, `ArithmeticException`.

```java
public class UncheckedExceptionDemo {
    public static void main(String[] args) {
        int result = 10 / 0; // Compiles fine, throws ArithmeticException at runtime
    }
}

```

---

### 3. Exception Handling Keywords & Modern Practices

Java provides five keywords for exception handling: `try`, `catch`, `finally`, `throw`, and `throws`.

#### Basic Handling (`try-catch-finally`)

* **`try`**: Encloses code that might throw an exception.
* **`catch`**: Handles specific exceptions.
* **`finally`**: Block that **always runs**, regardless of whether an exception occurred (used for manual cleanup).

```java
public class BasicExceptionDemo {
    public static void main(String[] args) {
        try {
            int[] nums = {1, 2, 3};
            System.out.println(nums[5]); // Throws ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught exception: Invalid array index accessed.");
        } finally {
            System.out.println("Finally block executes no matter what.");
        }
    }
}

```

#### Multi-Catch Block (Java 7+)

Catches multiple unrelated exceptions in a single `catch` block using the `|` operator.

```java
try {
    int num = Integer.parseInt("invalid_number");
} catch (NumberFormatException | ArithmeticException e) {
    System.out.println("Handled number parsing or arithmetic issue: " + e.getMessage());
}

```

#### Try-With-Resources (Java 7+)

Automatically closes resources (implementing `AutoCloseable` or `Closeable`) when the block finishes, eliminating verbose `finally` blocks.

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesDemo {
    public static void main(String[] args) {
        // BufferedReader is automatically closed after execution
        try (BufferedReader reader = new BufferedReader(new FileReader("test.txt"))) {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
        }
    }
}

```

#### Custom Exceptions

Extend `Exception` (for checked) or `RuntimeException` (for unchecked) to build domain-specific exceptions.

```java
// Custom Unchecked Exception
class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(String message) {
        super(message);
    }
}

public class CustomExceptionDemo {
    static void validateAge(int age) {
        if (age < 18) {
            throw new InvalidAgeException("Age must be 18 or above to apply.");
        }
    }

    public static void main(String[] args) {
        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }
}

```
