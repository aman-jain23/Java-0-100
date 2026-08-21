# Lecture 51
## Topics: Monitor Locks, Synchronized keywords, Static Sync, Custom locks.

### 1. Monitor Locks (Intrinsic Locks)

In Java, every single object (including instances of classes and `Class` objects) has an **Intrinsic Lock** or **Monitor Lock** associated with it.

* **How it Works:** To execute code inside a synchronized region on an object, a thread must first **acquire** that object's monitor lock.
* **Mutual Exclusion:** Only one thread can hold a monitor lock at any given moment. Any other thread trying to enter a synchronized block locked by the same monitor will enter the `BLOCKED` state until the lock is released.
* **Reentrancy:** Java locks are **reentrant**. If a thread already holds an object's monitor lock, it can freely enter other synchronized methods/blocks on the *same object* without deadlocking itself.

---

### 2. The `synchronized` Keyword

The `synchronized` keyword provides two guarantees:

1. **Mutual Exclusion (Atomicity):** Prevents race conditions by allowing only one thread to execute a block at a time.
2. **Visibility & Ordering:** Establishes a **Happens-Before** relationship—flushes local CPU caches to main RAM when exiting, and refreshes local CPU caches from main RAM when entering.

#### Instance-Level Synchronization

Locking is bound to the current class instance (`this`). Two different instances run synchronized methods concurrently without blocking each other.

```java
public class Counter {
    private int count = 0;

    // Synchronized Method (Locks on 'this' instance)
    public synchronized void increment() {
        count++;
    }

    // Synchronized Block (Equivalent to above, fine-grained control)
    public void incrementBlock() {
        // Code before lock runs concurrently
        synchronized (this) {
            count++;
        }
        // Code after lock runs concurrently
    }

    public synchronized int getCount() {
        return count;
    }
}

```

---

### 3. Static Synchronization

When a method or block is declared `static synchronized`, it locks on the **Class-level monitor object** (`ClassName.class`), NOT an individual instance (`this`).

* **Scope:** Global across all threads and across all instances of that class.
* **Key Distinction:** Instance-level synchronization and static synchronization do **not** block each other because they lock on completely different monitor objects (`this` vs `ClassName.class`).

```java
public class Bank {
    private static double totalBankCapital = 1000000.0;

    // Locks on Bank.class object
    public static synchronized void deductCapital(double amount) {
        totalBankCapital -= amount;
    }

    // Equivalent block syntax
    public static void addCapital(double amount) {
        synchronized (Bank.class) {
            totalBankCapital += amount;
        }
    }
}

```

---

### 4. Custom Locks (Explicit Locks via `java.util.concurrent.locks`)

While `synchronized` is simple, it has limitations:

* It is inflexible (must release locks in exact reverse order of acquisition).
* No non-blocking attempt (`tryLock()`).
* No timeout support when waiting for a lock.
* No unfairness/fairness control.

Java provides explicit lock implementations via the `Lock` interface (most commonly **`ReentrantLock`**).

#### Key Features of `ReentrantLock`

* `lock()`: Acquires lock (blocks indefinitely until available).
* `unlock()`: Releases lock (must be placed inside a `finally` block).
* `tryLock()`: Non-blocking attempt to acquire lock; returns `boolean`.
* `tryLock(timeout, timeUnit)`: Attempts to acquire lock with a timeout limit.
* `ReentrantLock(true)`: Optional fairness policy (grants lock to the longest-waiting thread).

#### Custom Lock Example with `ReentrantLock`

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Account {
    private double balance = 1000;
    // Explicit custom lock
    private final Lock lock = new ReentrantLock();

    public void withdraw(double amount) {
        // Try acquiring lock within 1 second instead of blocking indefinitely
        try {
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " acquired lock.");
                    if (balance >= amount) {
                        balance -= amount;
                        System.out.println(Thread.currentThread().getName() + " withdrew $" + amount);
                    }
                } finally {
                    // MUST unlock in a finally block to prevent permanent deadlocks
                    lock.unlock();
                    System.out.println(Thread.currentThread().getName() + " released lock.");
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " couldn't acquire lock (timed out).");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class CustomLockDemo {
    public static void main(String[] args) {
        Account acc = new Account();

        Thread t1 = new Thread(() -> acc.withdraw(500), "Thread-1");
        Thread t2 = new Thread(() -> acc.withdraw(700), "Thread-2");

        t1.start();
        t2.start();
    }
}

```

---

### Summary Comparison

| Metric | `synchronized` (Instance) | `static synchronized` | `ReentrantLock` (Custom) |
| --- | --- | --- | --- |
| **Lock Target** | Instance (`this` object) | Class object (`Class.class`) | Custom `Lock` object reference |
| **Scope** | Per class instance | Global (All instances) | Per lock instance |
| **Lock Acquisition** | Automatic on entering block | Automatic on entering block | Explicit via `.lock()` or `.tryLock()` |
| **Lock Release** | Automatic on exiting block | Automatic on exiting block | Explicit via `.unlock()` in `finally` |
| **Timeout Support** | No | No | Yes (`tryLock(timeout, unit)`) |
| **Fairness Option** | No (Arbitrary execution) | No (Arbitrary execution) | Yes (Fair lock parameter) |
