# Lecture 47
## Topics: Multi threading, process vs thread.

### 1. Process vs. Thread

A **Process** is an independent, executing program with its own dedicated memory space allocated by the Operating System.

A **Thread** is the smallest unit of execution within a process. Multiple threads within the same process run concurrently and share the process's resources (like Heap memory and open files), while maintaining their own independent execution paths (Stack memory and PC Register).

```
┌────────────────────────────────────────────────────────────────────────┐
│                              PROCESS                                   │
│  ┌──────────────────────────┐         ┌─────────────────────────────┐  │
│  │   Heap Memory (Shared)   │         │   Method Area (Shared)      │  │
│  └──────────────────────────┘         └─────────────────────────────┘  │
│                                                                        │
│  ┌────────────────────────┐             ┌──────────────────────────┐   │
│  │        THREAD 1        │             │         THREAD 2         │   │
│  │  ┌──────────────────┐  │             │  ┌────────────────────┐  │   │
│  │  │   Stack Area     │  │             │  │     Stack Area     │  │   │
│  │  └──────────────────┘  │             │  └────────────────────┘  │   │
│  │  ┌──────────────────┐  │             │  ┌────────────────────┐  │   │
│  │  │   PC Register    │  │             │  │     PC Register    │  │   │
│  │  └──────────────────┘  │             │  └────────────────────┘  │   │
│  └────────────────────────┘             └──────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘

```

| Property | Process | Thread |
| --- | --- | --- |
| **Definition** | An isolated, executing instance of a program. | A lightweight path of execution inside a process. |
| **Memory** | Has its own independent address space (Heap & Stack). | Shares Heap and Method area; has its own Stack & PC. |
| **Creation & Destruction** | Resource-heavy and slow. | Lightweight and fast. |
| **Context Switching** | Expensive (requires OS-level address space swapping). | Inexpensive (swaps execution context within same space). |
| **Isolation / Safety** | High (one process crash doesn't affect another). | Low (an unhandled exception in one thread can impact the process). |
| **Inter-communication** | Requires IPC (Inter-Process Communication, e.g., sockets, files). | Communicates directly via shared variables/objects in Heap. |

---

### 2. Multi-Threading in Java

Multi-threading allows a single Java program to run multiple threads simultaneously, maximizing CPU utilization—especially on multi-core processors.

#### Ways to Create Threads in Java

There are two primary ways to define a thread in core Java:

1. **Extending the `Thread` Class**
2. **Implementing the `Runnable` Interface** (*Recommended*, as it leaves your class free to extend other classes).

```java
// Method 1: Extending Thread class
class WorkerThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running via Thread class: " + Thread.currentThread().getName());
    }
}

// Method 2: Implementing Runnable interface
class WorkerTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread running via Runnable: " + Thread.currentThread().getName());
    }
}

public class MultiThreadingDemo {
    public static void main(String[] args) {
        // Starting Thread 1
        WorkerThread t1 = new WorkerThread();
        t1.start(); // Spawns a new thread call stack and executes run()

        // Starting Thread 2
        Thread t2 = new Thread(new WorkerTask());
        t2.start();

        // Method 3: Using Lambda (Java 8+) for concise Runnable definition
        Thread t3 = new Thread(() -> 
            System.out.println("Thread running via Lambda: " + Thread.currentThread().getName())
        );
        t3.start();
    }
}

```

> **Important Note:** Always call `.start()` instead of `.run()`. Calling `.start()` tells the JVM to allocate a new call stack and register the thread with the OS scheduler. Calling `.run()` directly executes the method synchronously on the *current* thread without multi-threading.

---

### 3. Lifecycle of a Thread (Thread States)

A Java thread moves through five distinct states managed by the JVM Thread Scheduler:

```
                  ┌───────────────┐
                  │      NEW      │
                  └───────┬───────┘
                          │ .start()
                          ▼
                  ┌───────────────┐
      ┌──────────>│   RUNNABLE    │<──────────┐
      │           └───────┬───────┘           │
      │                   │ Scheduled         │
      │                   ▼                   │
      │           ┌───────────────┐           │
      │           │    RUNNING    │           │
      │           └───────┬───────┘           │
      │                   │                   │
      │      ┌────────────┴────────────┐      │
      │      ▼                         ▼      │
┌───────────┴──────────┐    ┌─────────────────┴──────────┐
│  WAITING / TIMED_WAIT│    │  BLOCKED (Waiting for lock)│
└──────────────────────┘    └────────────────────────────┘
      │                                       │
      └───────────────────┬───────────────────┘
                          │ Execution completes
                          ▼
                  ┌───────────────┐
                  │  TERMINATED   │
                  └───────────────┘

```

1. **New:** The thread instance is created (`new Thread()`), but `.start()` has not been called yet.
2. **Runnable:** `.start()` was called. The thread is ready to run and waiting for CPU time from the OS thread scheduler.
3. **Running:** The CPU is actively executing the code inside the thread's `run()` method.
4. **Blocked / Waiting / Timed Waiting:** The thread is temporarily inactive because:
* **Blocked:** Waiting to acquire a synchronized monitor lock.
* **Waiting:** Waiting indefinitely for another thread to perform an action (`Object.wait()` or `Thread.join()`).
* **Timed Waiting:** Sleeping or waiting for a specified duration (`Thread.sleep(ms)` or `wait(timeout)`).


5. **Terminated (Dead):** The `run()` method has completed execution or exited due to an unhandled exception.

---

### 4. Shared Resource Problem & Synchronization Preview

When multiple threads read and write to the same shared memory location concurrently, race conditions occur.

```java
class Counter {
    private int count = 0;

    // Without 'synchronized', concurrent calls cause lost updates (Race Condition)
    public synchronized void increment() {
        count++; // 3-step operation: read, update, write
    }

    public int getCount() {
        return count;
    }
}

public class RaceConditionDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start();
        t2.start();

        t1.join(); // Wait for t1 to complete
        t2.join(); // Wait for t2 to complete

        System.out.println("Final Count: " + counter.getCount()); // Always 2000 due to synchronization
    }
}

```
