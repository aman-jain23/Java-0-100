# Lecture 48
## Topics: Java Thread Creation and Lifecycle.

### 1. Thread Creation in Java

There are four primary ways to create threads in Java:

#### Method 1: Implementing `Runnable` Interface (Preferred)

Separates the task logic from the thread implementation. Leaves your class free to extend other classes.

```java
class TaskRunner implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable task executing in: " + Thread.currentThread().getName());
    }
}

public class RunnableDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(new TaskRunner());
        thread.start(); // Spawns new call stack
    }
}

```

#### Method 2: Extending `Thread` Class

Simple, but restricts multiple inheritance because Java doesn't support extending multiple classes.

```java
class WorkerThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread class task executing in: " + getName());
    }
}

public class ThreadClassDemo {
    public static void main(String[] args) {
        WorkerThread worker = new WorkerThread();
        worker.start();
    }
}

```

#### Method 3: Using Anonymous Class or Lambda Expression (Java 8+)

Concise syntax for short, single-use background tasks.

```java
public class LambdaThreadDemo {
    public static void main(String[] args) {
        // Lambda syntax for Runnable
        Thread lambdaThread = new Thread(() -> 
            System.out.println("Lambda thread running in: " + Thread.currentThread().getName())
        );
        lambdaThread.start();
    }
}

```

#### Method 4: Using `Callable` and `Future` (Returns a Result)

Unlike `Runnable`, `Callable` can return a value and throw checked exceptions.

```java
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CallableDemo {
    public static void main(String[] args) throws Exception {
        Callable<Integer> calculationTask = () -> {
            int sum = 0;
            for (int i = 1; i <= 5; i++) sum += i;
            return sum;
        };

        FutureTask<Integer> futureTask = new FutureTask<>(calculationTask);
        Thread thread = new Thread(futureTask);
        thread.start();

        // futureTask.get() blocks until the thread completes and returns the value
        Integer result = futureTask.get(); 
        System.out.println("Result from Callable: " + result); // Output: 15
    }
}

```

> **Crucial Rule:** Always invoke `.start()` to spawn a new execution thread. Calling `.run()` directly executes the method synchronously on the current caller thread without multi-threading.

---

### 2. Thread Lifecycle (States & Transitions)

A Java thread's state is tracked by `Thread.State` enum and managed by the JVM Thread Scheduler.

```
                    ┌──────────────┐
                    │     NEW      │
                    └──────┬───────┘
                           │ .start()
                           ▼
                    ┌──────────────┐
        ┌──────────>│   RUNNABLE   │<───────────┐
        │           └──────┬───────┘            │
        │                  │ CPU assigned       │
        │                  ▼                    │
        │           ┌──────────────┐            │
        │           │   RUNNING    │            │
        │           └──────┬───────┘            │
        │                  │                    │
        │      ┌───────────┴───────────┐        │
        │      ▼                       ▼        │
  ┌───────────┴──────────┐   ┌──────────────────┴──────────┐
  │ WAITING /            │   │          BLOCKED            │
  │ TIMED_WAITING        │   │   (Waiting for lock)        │
  └──────────────────────┘   └─────────────────────────────┘
        │                                       │
        └──────────────────┬────────────────────┘
                           │ Execution finishes / Unhandled exception
                           ▼
                    ┌──────────────┐
                    │  TERMINATED  │
                    └──────────────┘

```

#### Detailed State Breakdown

| State | Description | Transition Trigger |
| --- | --- | --- |
| **`NEW`** | Thread object instantiated (`new Thread()`), but `.start()` not yet called. | Calling `t.start()` $\rightarrow$ **`RUNNABLE`**. |
| **`RUNNABLE`** | Eligible to run; waiting for CPU time from the OS thread scheduler. | CPU core assigned $\rightarrow$ **`RUNNING`**. |
| **`RUNNING`** | Thread is actively executing code inside its `run()` method. | Yields CPU, sleeps, or waits for a lock/notification. |
| **`BLOCKED`** | Suspended while waiting to acquire a monitor lock (e.g., entering a `synchronized` block/method occupied by another thread). | Lock released and acquired $\rightarrow$ **`RUNNABLE`**. |
| **`WAITING`** | Waiting indefinitely for another thread to perform an action (`Object.wait()`, `Thread.join()`, `LockSupport.park()`). | Notified via `notify()` / `notifyAll()` $\rightarrow$ **`RUNNABLE`**. |
| **`TIMED_WAITING`** | Waiting for a specified time period (`Thread.sleep(ms)`, `Object.wait(timeout)`, `Thread.join(timeout)`). | Time expires or interrupted $\rightarrow$ **`RUNNABLE`**. |
| **`TERMINATED`** | Thread finished executing `run()` completely or exited due to an unhandled exception. Cannot be restarted. | End of thread lifecycle. |

---

### 3. Programmatic State Inspection Example

```java
public class ThreadLifecycleDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                // Moving into TIMED_WAITING state
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // State: NEW
        System.out.println("State after creation: " + worker.getState());

        worker.start();
        // State: RUNNABLE (or RUNNING)
        System.out.println("State after start(): " + worker.getState());

        // Wait brief moment to allow thread to enter sleep
        Thread.sleep(100);
        // State: TIMED_WAITING
        System.out.println("State during sleep(): " + worker.getState());

        // Wait for worker thread to finish
        worker.join();
        // State: TERMINATED
        System.out.println("State after completion: " + worker.getState());
    }
}

```
