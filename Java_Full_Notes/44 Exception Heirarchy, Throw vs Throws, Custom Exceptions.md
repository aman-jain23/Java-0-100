# Lecture 44
## Topics: Exception Heirarchy, Throw vs Throws, Custom Exceptions.

**Java Exception Hierarchy**

All exception and error classes in Java inherit from `java.lang.Throwable`.

```
                        Throwable
                            │
            ┌───────────────┴───────────────┐
          Error                         Exception
    (Fatal JVM errors)                      │
   e.g., OutOfMemoryError         ┌─────────┴─────────┐
                                  │                   │
                         Checked Exception   Unchecked Exception
                        (Compile-time check)  (RuntimeException)
                         e.g., IOException    e.g., NullPointerException

```

* **`Throwable`**: Root class of the entire hierarchy.
* **`Error`**: Represents unrecoverable conditions caused by the JVM environment (e.g., `OutOfMemoryError`, `StackOverflowError`). Applications should not catch these.
* **`Exception`**: Represents conditions that a reasonable application might want to catch and recover from.
* **Checked Exceptions**: Direct subclasses of `Exception` (excluding `RuntimeException`). Checked by the compiler at compile-time.
* **Unchecked Exceptions**: Subclasses of `RuntimeException`. Not checked at compile-time; caused by programming errors or invalid assumptions.



---

**`throw` vs. `throws**`

| Feature | `throw` | `throws` |
| --- | --- | --- |
| **Purpose** | Used to **explicitly instantiate and throw** an exception object. | Used in a **method declaration** to declare that the method might throw exceptions. |
| **Location** | Inside method bodies or code blocks. | In the method signature header. |
| **Syntax** | `throw new ExceptionType("message");` | `void myMethod() throws IOException, SQLException` |
| **Quantity** | Throws **one** exception instance at a time. | Can declare **multiple** exception classes separated by commas. |
| **Type** | Works with an object instance (`new Throwable()`). | Works with class types (`ExceptionClass`). |

```java
import java.io.IOException;

public class ThrowVsThrowsDemo {

    // 'throws' declares that this method may propagate an IOException
    public static void checkFile(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            // 'throw' explicitly raises an exception instance
            throw new IOException("Filename cannot be empty.");
        }
        System.out.println("Processing file: " + filename);
    }

    public static void main(String[] args) {
        try {
            checkFile("");
        } catch (IOException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }
}

```

---

**Custom Exceptions**

Custom exceptions allow you to represent domain-specific business rules (e.g., `InsufficientBalanceException`, `UserNotFoundException`).

**Rules for Creating Custom Exceptions:**

1. Extend `Exception` for a **Checked Custom Exception** (caller is forced to handle or declare it).
2. Extend `RuntimeException` for an **Unchecked Custom Exception** (handling is optional at compile time).
3. Provide constructors that pass messages or wrapped root-cause throwables to `super()`.

#### Example: Unchecked Custom Exception

```java
// 1. Define custom unchecked exception
class InsufficientFundsException extends RuntimeException {
    private final double amountMissing;

    public InsufficientFundsException(double amountMissing) {
        super("Transaction failed: Deficit of $" + amountMissing);
        this.amountMissing = amountMissing;
    }

    public double getAmountMissing() {
        return amountMissing;
    }
}

// 2. Class using the custom exception
class BankAccount {
    private double balance = 500.0;

    public void withdraw(double amount) {
        if (amount > balance) {
            double deficit = amount - balance;
            // Throwing custom exception
            throw new InsufficientFundsException(deficit);
        }
        balance -= amount;
        System.out.println("Withdrawal successful. New balance: $" + balance);
    }
}

// 3. Execution
public class CustomExceptionTest {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();

        try {
            account.withdraw(750.0);
        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Shortage amount: $" + e.getAmountMissing());
        }
    }
}

```
