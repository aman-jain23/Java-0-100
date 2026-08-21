# Lecture 55
## Topics: CAS Retry, Compare and Swap, ABA Problem.

### 1. Compare and Swap (CAS)

**Compare-and-Swap (CAS)** is an atomic hardware-level CPU instruction (e.g., `CMPXCHG` on x86) used to achieve synchronization without using thread locks.

#### How CAS Works

CAS updates a memory location atomically by comparing its current value with an expected value:

* **$V$ (Memory Location):** The target variable being modified.
* **$A$ (Expected Value):** The value the thread expects $V$ to currently hold.
* **$B$ (New Value):** The new value to write to $V$.

**Operation Rule:**
If $V == A$, update $V$ to $B$ and return `true`. If $V \neq A$ (another thread modified $V$ first), make no changes and return `false`.

---

### 2. The CAS Retry Loop (Spin Loop)

Because CAS operations can fail under concurrent writes, lock-free algorithms execute CAS inside a **retry loop** (also called optimism or spin-wait). The thread continuously re-reads the value and retries the CAS instruction until it succeeds.

#### Code Pattern (Lock-Free Increment)

```java
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicInteger;

public class CASRetryDemo {
    private final AtomicInteger count = new AtomicInteger(0);

    // Standard Lock-Free Increment Loop
    public void increment() {
        int oldValue;
        int newValue;
        
        do {
            oldValue = count.get();       // 1. Read current memory state
            newValue = oldValue + 1;      // 2. Compute new state
            // 3. Attempt CAS: If current value still equals oldValue, set to newValue
        } while (!count.compareAndSet(oldValue, newValue)); // Retry on failure
    }
}

```

#### Under Heavy Contention: Spin Overhead

If hundreds of threads attempt to update the same atomic variable simultaneously:

* Threads continuously fail CAS and stay in a high-CPU retry loop.
* **Solution for Heavy Contention:** Use striped adders like **`LongAdder`**, which split internal variables across CPU cores to reduce contention points.

---

### 3. The ABA Problem

#### What is the ABA Problem?

The **ABA Problem** occurs in a CAS loop when a memory location changes from value $A$ to $B$, and then back to $A$ before the observing thread completes its CAS check.

The thread checks $V == A$, sees $A$, and assumes nothing changed—unaware that intermediate state modifications took place.

```
Time ──►

Thread 1: Reads V = A  ─────────────── [Paused] ───────────────► CAS(V, Exp: A, New: C)
                                                                    │
Thread 2:              ──► Changes V: A -> B                        │
Thread 2:              ──► Changes V: B -> A                        ▼
                                                          CAS Succeeds! (Unaware of B)

```

#### Why it Breaks Lock-Free Data Structures (Example: Stack Node Reuse)

Consider a lock-free Stack: `Top ──► [Node A] ──► [Node B] ──► [Node C]`

1. **Thread 1** prepares to pop `Node A`. It reads `Top = A` and `Next = B`.
2. **Thread 1 is paused** before running CAS.
3. **Thread 2** pops `Node A`, then pops `Node B`. Stack is now `Top ──► [Node C]`.
4. **Thread 2** pushes `Node A` back onto the stack. Stack is now `Top ──► [Node A] ──► [Node C]`.
5. **Thread 1 resumes** and executes `CAS(Top, Exp: A, New: B)`.
6. Since `Top` is still `A`, CAS succeeds! `Top` is set to `B`, but `Node B` was already freed/popped. **The stack pointer is now corrupted.**

---

### 4. Resolving the ABA Problem: Version Stamps

To resolve the ABA problem, pair the object reference or value with a monotonically increasing **version stamp** or **counter**. Even if value $A$ returns, its version stamp increases ($A_1 \rightarrow B_2 \rightarrow A_3$).

#### Solution in Java: `AtomicStampedReference`

`AtomicStampedReference<T>` holds both an object reference and an `int` stamp, updating both atomically.

```java
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABAResolution {
    public static void main(String[] args) {
        String initialRef = "A";
        int initialStamp = 1;

        AtomicStampedReference<String> atomicRef = 
            new AtomicStampedReference<>(initialRef, initialStamp);

        // Thread 1 reads initial reference and stamp
        String ref = atomicRef.getReference(); // "A"
        int stamp = atomicRef.getStamp();      // 1

        // Thread 2 performs A -> B -> A transitions with updated stamps
        atomicRef.compareAndSet("A", "B", 1, 2); // Stamp = 2
        atomicRef.compareAndSet("B", "A", 2, 3); // Stamp = 3

        // Thread 1 attempts CAS expecting Stamp = 1
        boolean success = atomicRef.compareAndSet("A", "C", stamp, stamp + 1);

        System.out.println("CAS Succeeded? " + success); 
        // Output: false (Value is 'A', but Stamp is 3 instead of expected 1)
    }
}

```

---

### Summary Comparison

| Concept | Primary Purpose | Key Vulnerability / Tradeoff |
| --- | --- | --- |
| **CAS Instruction** | Atomic compare-and-write without hardware locks. | Fails under concurrent writes, requiring retry logic. |
| **CAS Retry Loop** | Ensures value eventually updates by spinning until CAS succeeds. | High CPU usage under heavy thread contention. |
| **ABA Problem** | Masked state changes ($A \rightarrow B \rightarrow A$) fool basic value comparisons. | Memory corruption in pointer-based lock-free data structures. |
| **`AtomicStampedReference`** | Pairs references with version numbers ($A_1 \rightarrow B_2 \rightarrow A_3$). | Slightly higher memory footprint due to stamp storage. |
