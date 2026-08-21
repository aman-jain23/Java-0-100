# Lecture 54
## Topics: Lock-free Concurrency, Atomic Variables, CAS.

Lock-free concurrency allows multiple threads to read and write to shared memory concurrently without using traditional blocking locks (like `synchronized` or `ReentrantLock`). This avoids performance bottlenecks like thread suspension, context switching overhead, and deadlocks.

---

### 1. The Core Mechanism: Compare-And-Swap (CAS)

At the heart of lock-free programming is **Compare-And-Swap (CAS)**, an atomic instruction supported directly at the CPU hardware level (e.g., `CMPXCHG` on x86 architectures).

#### How CAS Works

CAS updates a memory location using three parameters:

1. **Memory Location ($V$):** The memory address being updated.
2. **Expected Value ($A$):** The value the thread thinks is currently in $V$.
3. **New Value ($B$):** The new value to write to $V$.

**Operation Logic:**
If $V == A$, update $V$ to $B$ and return `true`. If $V \neq A$ (meaning another thread modified $V$ in the meantime), leave $V$ untouched and return `false`.

```
[ Thread 1 reads V = 10 ] ──────┐
                                 ├─► CAS(V, Expected: 10, New: 11) ──► Success (V is now 11)
[ Thread 2 updates V to 15 ] ────┘
                                 │
[ Thread 1 tries to write ] ────► CAS(V, Expected: 10, New: 12) ──► Fails (V is 15, expected 10)

```

Because CAS is a single hardware instruction, no other thread can interrupt the comparison and update steps.

---

### 2. Atomic Variables in Java (`java.util.concurrent.atomic`)

Java provides atomic wrapper classes that encapsulate primitive types and object references using CAS under the hood via the `Unsafe` / `VarHandle` classes.

#### Primary Atomic Classes

* **Primitives:** `AtomicInteger`, `AtomicLong`, `AtomicBoolean`
* **Arrays:** `AtomicIntegerArray`, `AtomicLongArray`
* **References:** `AtomicReference<T>`, `AtomicStampedReference<T>`
* **High-Throughput Adders:** `LongAdder`, `DoubleAdder` (use striping to reduce contention under high concurrency)

#### Lock-Free Counter vs. Synchronized Counter

```java
import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    // Lock-free increment using CAS loop
    public void increment() {
        int current;
        int next;
        do {
            current = count.get();      // Step 1: Read expected value
            next = current + 1;         // Step 2: Calculate new value
        } while (!count.compareAndSet(current, next)); // Step 3: CAS update loop
    }

    // Built-in convenience method (does the CAS loop internally)
    public void incrementShortcut() {
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}

```

---

### 3. The ABA Problem and Its Solution

#### What is the ABA Problem?

The ABA problem occurs during a CAS operation when a memory location's value changes from $A$ to $B$, and then back to $A$ before the original thread performs its comparison.

1. Thread 1 reads value $A$ from location $V$.
2. Thread 1 is preempted (paused).
3. Thread 2 changes $V$ from $A$ to $B$, and then back to $A$.
4. Thread 1 resumes and executes `CAS(V, Expected: A, New: C)`.
5. The CAS succeeds because $V$ is $A$, but Thread 1 is unaware that the underlying state changed in between.

This is particularly dangerous in lock-free data structures like stacks or linked lists, where node pointers might be reused.

#### Solution: `AtomicStampedReference`

`AtomicStampedReference` solves the ABA problem by pairing an object reference with an integer **stamp** or version number. Every update increments the stamp.

```java
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABASolutionDemo {
    public static void main(String[] args) {
        String initialRef = "A";
        int initialStamp = 1;

        AtomicStampedReference<String> stampedRef = 
            new AtomicStampedReference<>(initialRef, initialStamp);

        // Thread 1 reads reference and stamp
        String ref = stampedRef.getReference(); // "A"
        int stamp = stampedRef.getStamp();      // 1

        // Thread 2 modifies A -> B -> A with updated stamps
        stampedRef.compareAndSet("A", "B", 1, 2); // Stamp becomes 2
        stampedRef.compareAndSet("B", "A", 2, 3); // Stamp becomes 3

        // Thread 1 tries to update expecting stamp = 1
        boolean success = stampedRef.compareAndSet("A", "C", stamp, stamp + 1);

        System.out.println("CAS Success: " + success); // Output: false (Stamp mismatch!)
    }
}

```

---

### 4. Comparison: Lock-Based vs. Lock-Free Concurrency

| Feature | Lock-Based (`synchronized`, `ReentrantLock`) | Lock-Free (CAS, `AtomicInteger`) |
| --- | --- | --- |
| **Strategy** | **Pessimistic:** Assumes conflict will happen; blocks other threads. | **Optimistic:** Assumes no conflict; retries operation if conflict occurs. |
| **Thread State** | Contending threads enter `BLOCKED` or `WAITING` states. | Threads remain in `RUNNABLE` state (busy retry loop). |
| **CPU Overhead** | High context switching and thread parking costs. | High CPU usage under **extreme contention** due to spinning loops. |
| **Performance** | Better under **heavy contention** with long critical sections. | Superior performance under **low to moderate contention**. |
| **Deadlocks** | Possible if locks are acquired out of order. | **Impossible** (no locks held). |
