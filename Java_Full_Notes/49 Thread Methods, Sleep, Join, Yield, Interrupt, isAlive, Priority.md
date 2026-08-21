# Lecture 49
## Topics: Thread Methods, Sleep, Join, Yield, Interrupt, isAlive, Priority.

Here is a complete breakdown of essential Java `Thread` control methods, their mechanics, and code examples.

---

### Core Thread Control Methods

#### 1. `Thread.sleep(long millis)`

Pauses execution of the **current thread** for a specified time.

* **Lock Retention:** Does **NOT** release any acquired monitor locks or synchronization locks.
* **Type:** Static method. Must catch or declare `InterruptedException`.

```java
try {
    System.out.println("Sleeping for 1 second...");
    Thread.sleep(1000); // Current thread pauses for 1000ms
} catch (InterruptedException e) {
    System.out.println("Sleep was interrupted!");
}

```

#### 2. `join()` / `join(long millis)`

Forces the calling thread to wait until the target thread finishes execution (or until the timeout expires).

* **Use Case:** Coordinating dependent background tasks (e.g., waiting for data loading before processing).

```java
Thread loader = new Thread(() -> System.out.println("Loading data..."));
loader.start();

// Main thread waits until 'loader' thread finishes execution
loader.join(); 
System.out.println("Data loaded. Continuing main execution...");

```

#### 3. `Thread.yield()`

Gives a hint to the OS thread scheduler that the current thread is willing to yield its current CPU timeslice to allow other threads of **equal or higher priority** to run.

* **Behavior:** The thread moves from **`RUNNING`** back to **`RUNNABLE`**.
* **Guarantee:** None—the OS scheduler is free to ignore this hint entirely.

```java
Runnable task = () -> {
    for (int i = 0; i < 3; i++) {
        System.out.println(Thread.currentThread().getName() + " executing");
        Thread.yield(); // Hints CPU to switch to another runnable thread
    }
};

```

#### 4. `interrupt()` & `isInterrupted()`

Sends an interrupt signal flag to a target thread.

* **If the thread is in `TIMED_WAITING` or `WAITING**` (e.g., `sleep()`, `join()`, `wait()`), it clears the flag and throws an `InterruptedException`.
* **If the thread is actively running**, it sets the interrupt status flag to `true`. The thread must manually check `Thread.currentThread().isInterrupted()` to stop gracefully.

```java
Thread worker = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        System.out.println("Working...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during sleep! Exiting...");
            break; // Stop execution on interrupt
        }
    }
});

worker.start();
Thread.sleep(1000);
worker.interrupt(); // Signal the worker thread to stop

```

#### 5. `isAlive()`

Returns `true` if the thread has been started (`start()` called) and has not yet died (reached `TERMINATED` state).

```java
Thread t = new Thread(() -> {});
System.out.println(t.isAlive()); // false (NEW)
t.start();
System.out.println(t.isAlive()); // true (RUNNABLE / RUNNING)

```

#### 6. Thread Priority (`setPriority()` / `getPriority()`)

Hints to the OS scheduler how CPU time should be prioritized.

* **Range:** Integers between `1` (`Thread.MIN_PRIORITY`) and `10` (`Thread.MAX_PRIORITY`). Default is `5` (`Thread.NORM_PRIORITY`).
* **Behavior:** Platform-dependent. Higher priority threads are favored, but execution order is **never guaranteed**.

```java
Thread highPriority = new Thread(() -> {});
highPriority.setPriority(Thread.MAX_PRIORITY); // 10

```

---

### Summary Table

| Method | Static/Instance | Affects Current or Target Thread? | Releases Locks? | Moves Thread To State |
| --- | --- | --- | --- | --- |
| **`sleep(ms)`** | Static | Current Thread | **No** | `TIMED_WAITING` |
| **`join()`** | Instance | Current Thread (waits for Target) | **No** | `WAITING` / `TIMED_WAITING` |
| **`yield()`** | Static | Current Thread | **No** | `RUNNABLE` |
| **`interrupt()`** | Instance | Target Thread | **No** | Interrupts `WAITING` state |
| **`isAlive()`** | Instance | Target Thread | N/A | Returns `boolean` |

---

### Complete Code Walkthrough

```java
public class ThreadMethodsDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("Worker started with priority: " + Thread.currentThread().getPriority());
            try {
                // Pauses thread execution without releasing locks
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                System.out.println("Worker caught InterruptedException!");
            }
        }, "WorkerThread");

        // Set priority before starting
        worker.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Is worker alive before start? " + worker.isAlive()); // false
        worker.start();
        System.out.println("Is worker alive after start? " + worker.isAlive());  // true

        // Send interrupt signal while worker is sleeping
        Thread.sleep(500);
        worker.interrupt();

        // Main thread waits until worker terminates completely
        worker.join(); 
        System.out.println("Is worker alive after join? " + worker.isAlive());   // false
    }
}

```
