# Lecture 50
## Topics: Problems in Multithreading, Race Condition, Visibility, Ordering.

When multiple threads run concurrently and access shared data, three fundamental multithreading problems can arise: **Race Conditions**, **Visibility Issues**, and **Ordering Problems**.

---

### 1. Race Condition (Data Race)

#### The Problem

A **Race Condition** occurs when two or more threads attempt to read and write to the same shared resource simultaneously, and the final outcome depends on the exact execution sequence (timing) of the threads.

#### Root Cause

Non-atomic operations. An operation like `count++` looks like a single step in high-level code, but at the bytecode/CPU level, it consists of **three distinct steps**:

1. **Read** current value of `count` from memory into a CPU register.
2. **Modify** the value (add 1).
3. **Write** the updated value back to memory.

If two threads execute these steps concurrently, their operations can interleave, causing lost updates.

```
Thread 1: Read (0) ───────> Add (1) ─────────────────> Write (1)
Thread 2: ───────> Read (0) ───────> Add (1) ──> Write (1)

Result: count = 1 (Expected: 2)

```

#### Code Example

```java
class UnsafeCounter {
    private int count = 0;

    public void increment() {
        count++; // Race condition here
    }

    public int getCount() {
        return count;
    }
}

```

#### Solution

* Use `synchronized` methods or blocks.
* Use explicit locks (`ReentrantLock`).
* Use thread-safe atomic classes (`AtomicInteger`).

---

### 2. Visibility Problem

#### The Problem

The **Visibility Problem** occurs when a thread updates a shared variable in its local cache, but other threads reading the same variable cannot see the change immediately or at all.

#### Root Cause

Modern CPU architectures feature multi-level hardware caches (L1, L2, L3 Registers) to speed up execution. To avoid slow main memory (RAM) reads, threads cache variables locally. Without explicit memory barriers, changes written to a local CPU cache might not flush to main RAM right away.

```
  Thread 1 (CPU Core 1)                 Thread 2 (CPU Core 2)
┌───────────────────────┐             ┌───────────────────────┐
│ L1 Cache: flag = true │             │ L1 Cache: flag = false│
└───────────┬───────────┘             └───────────▲───────────┘
            │ Writes                              │ Reads stale value
            ▼                                     │
┌─────────────────────────────────────────────────┴───────────┐
│                   Main Memory (RAM)                         │
└─────────────────────────────────────────────────────────────┘

```

#### Code Example

```java
public class VisibilityDemo {
    // Thread 2 may loop infinitely because it reads a cached 'false' value
    private static boolean flag = false; 

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (!flag) {
                // Busy spin: Reads 'flag' from local CPU cache
            }
            System.out.println("Thread finished!");
        }).start();

        Thread.sleep(100);
        flag = true; // Main thread changes flag, but change isn't visible to Thread 2
    }
}

```

#### Solution

* Mark the variable with the **`volatile`** keyword, forcing reads and writes to go directly to main RAM.
* Use **`synchronized`** or **Locks**, which guarantee a *happens-before* relationship (flushes cache on exit, refreshes cache on entry).

---

### 3. Ordering Problem (Instruction Reordering)

#### The Problem

The **Ordering Problem** happens when the JVM compiler or hardware CPU reorders bytecode instructions to optimize execution, altering the execution sequence expected by multi-threaded code.

#### Root Cause

For single-threaded code, compilers and CPUs reorder instructions as long as single-threaded outcome remains unchanged (As-If-Serial Semantics). However, in multi-threaded contexts, instruction reordering can expose partially initialized objects or invalid states to other threads.

#### Code Example (Out-of-Order Execution)

```java
public class ReorderingDemo {
    private int x = 0;
    private boolean ready = false;

    // Thread 1
    public void writer() {
        x = 42;          // Instruction 1
        ready = true;    // Instruction 2
        // Compiler/CPU might swap these to:
        // ready = true;
        // x = 42;
    }

    // Thread 2
    public void reader() {
        if (ready) {     // If reordered, ready could be true while x is still 0!
            System.out.println(x); // Might print 0 instead of 42
        }
    }
}

```

#### Solution

* Use **`volatile`**: Prevents instruction reordering around reads/writes of volatile variables via memory barrier instruction inserts.
* Use **`synchronized`** or **Locks**.

---

### Summary Comparison

| Multithreading Issue | Caused By | Main Symptom | Core Remedy |
| --- | --- | --- | --- |
| **Race Condition** | Non-atomic read-modify-write operations across threads. | Lost updates, corrupted or inconsistent data. | `synchronized`, `ReentrantLock`, `Atomic*` classes. |
| **Visibility Issue** | CPU L1/L2 local memory caching prevents cross-core updates. | Threads see stale variable values continuously. | `volatile` keyword, `synchronized` blocks. |
| **Ordering Issue** | Compiler/CPU instruction reordering optimizations. | Unpredictable behavior due to out-of-order execution steps. | `volatile` memory barriers, `synchronized` locks. |
