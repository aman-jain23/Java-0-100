# Lecture 52
## Topics: Inner Thread Communication, Wait(), Notify(), NotifyAll().

**Inter-Thread Communication** allows threads to coordinate and send signals to each other. Instead of forcing a thread to continuously poll a shared resource in a resource-heavy loop (busy-waiting), Java provides a mechanism where threads can suspend execution and wait until notified by another thread.

---

### The Big Rule: The Synchronized Requirement

`wait()`, `notify()`, and `notifyAll()` are defined on the **`Object`** class (not `Thread`), because they operate directly on an object's **intrinsic monitor lock**.

> **Crucial Rule:** You **MUST** call `wait()`, `notify()`, or `notifyAll()` inside a `synchronized` block or method on the exact object whose monitor lock you are holding. Calling them outside a synchronized context throws an **`IllegalMonitorStateException`**.

---

### Core Methods Explained

#### 1. `wait()` / `wait(long timeout)`

* **What it does:** Causes the current thread to release its monitor lock immediately and go into the **`WAITING`** (or **`TIMED_WAITING`**) state.
* **Lock behavior:** **Releases the lock** so other threads can enter synchronized blocks on the same monitor.
* **Resume condition:** Stays paused until another thread calls `notify()` or `notifyAll()` on the same object, or the timeout expires.

#### 2. `notify()`

* **What it does:** Wakes up **one single arbitrary thread** that is currently waiting on that object's monitor.
* **Lock behavior:** The awakened thread doesn't run immediately; it moves to the **`BLOCKED`** state until the notifying thread exits its `synchronized` block and releases the lock.

#### 3. `notifyAll()`

* **What it does:** Wakes up **all threads** waiting on that object's monitor.
* **Which thread runs?** All woken threads move to the **`BLOCKED`** state to compete for the monitor lock. One gets the lock, runs, releases it, and then the others compete again.

---

### Classic Pattern: Producer-Consumer Model

A classic scenario demonstrating inter-thread communication using a shared buffer:

```java
class SharedBuffer {
    private int data;
    private boolean hasData = false;

    // Consumer method
    public synchronized int consume() throws InterruptedException {
        // ALWAYS use a while-loop instead of an if-statement for wait()
        // to handle spurious wakeups and state changes!
        while (!hasData) {
            System.out.println("Consumer: No data. Waiting...");
            wait(); // Releases lock and pauses until notified
        }

        System.out.println("Consumer: Consumed -> " + data);
        hasData = false;
        notify(); // Notify producer that buffer is empty
        return data;
    }

    // Producer method
    public synchronized void produce(int value) throws InterruptedException {
        while (hasData) {
            System.out.println("Producer: Buffer full. Waiting...");
            wait(); // Releases lock and pauses until notified
        }

        this.data = value;
        this.hasData = true;
        System.out.println("Producer: Produced -> " + value);
        notify(); // Notify consumer that data is ready
    }
}

public class InterThreadDemo {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    buffer.produce(i * 10);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    buffer.consume();
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer");

        consumer.start();
        producer.start();
    }
}

```

---

### Why `while(!condition)` Instead of `if(!condition)`?

Always put `wait()` calls inside a **`while` loop**, never a simple `if` condition:

```java
// BAD PRACTICE
if (!hasData) {
    wait();
}

// GOOD PRACTICE
while (!hasData) {
    wait();
}

```

1. **Spurious Wakeups:** A waiting thread can occasionally wake up without `notify()` being called (a system/OS-level phenomenon). The loop forces it to re-check the predicate condition.
2. **Multiple Waiting Threads:** If `notifyAll()` wakes up multiple consumer threads when only 1 item is produced, the first thread will consume it. When the second thread acquires the lock, the item is already gone. A `while` loop forces the second thread to re-verify `hasData` and go back to sleeping.

---

### Key Comparison: `Thread.sleep()` vs `Object.wait()`

| Feature | `Thread.sleep(ms)` | `Object.wait()` |
| --- | --- | --- |
| **Class Owner** | `java.lang.Thread` | `java.lang.Object` |
| **Lock Behavior** | **Keeps** all acquired locks while sleeping. | **Releases** the object's monitor lock while waiting. |
| **Synchronization** | Can be called anywhere. | Must be called inside a `synchronized` context. |
| **Waking Mechanism** | Wakes up automatically when time expires. | Wakes up on `notify()`, `notifyAll()`, or timeout. |
| **State Transition** | Moves to `TIMED_WAITING`. | Moves to `WAITING` or `TIMED_WAITING`. |

