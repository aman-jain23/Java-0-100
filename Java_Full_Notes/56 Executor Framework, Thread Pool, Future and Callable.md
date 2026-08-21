# Lecture 56
## Topics: Executor Framework, Thread Pool, Future and Callable.

The `java.util.concurrent` package provides high-level thread management abstractions through the **Executor Framework**, eliminating the need to manually create and manage low-level `Thread` objects.

---

### 1. Executor Framework Architecture

Instead of manually invoking `new Thread(r).start()`, tasks are decoupled from thread management:

* **`Executor`:** Root interface with a single method: `execute(Runnable)`.
* **`ExecutorService`:** Subinterface adding lifecycle management (`shutdown()`, `isTerminated()`) and task submission methods returning `Future` (`submit()`).
* **`Executors`:** Factory class providing pre-configured thread pools.

---

### 2. Thread Pools & `ThreadPoolExecutor`

A **Thread Pool** maintains a pool of worker threads and a task queue. It reuses existing threads to execute tasks, reducing thread creation overhead and limiting resource consumption.

#### Factory Methods via `Executors`

* **`Executors.newFixedThreadPool(int n)`:** Uses a fixed number of threads with an unbounded queue (`LinkedBlockingQueue`).
* **`Executors.newCachedThreadPool()`:** Creates new threads as needed and reuses idle threads (reclaims idle threads after 60s). Good for short-lived asynchronous tasks.
* **`Executors.newSingleThreadExecutor()`:** Uses a single worker thread to process tasks sequentially.
* **`Executors.newScheduledThreadPool(int n)`:** Supports delayed or periodic task execution.

#### Custom Configuration via `ThreadPoolExecutor`

For production systems, construct `ThreadPoolExecutor` directly to avoid out-of-memory risks from default unbounded queues:

```java
import java.util.concurrent.*;

public class ThreadPoolConfig {
    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(
            2,                              // Core pool size
            4,                              // Maximum pool size
            60L, TimeUnit.SECONDS,          // Keep-alive time for excess idle threads
            new ArrayBlockingQueue<>(100),  // Bounded task queue
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy() // Rejection policy when pool + queue are full
        );

        executor.submit(() -> System.out.println("Processing task on " + Thread.currentThread().getName()));
        executor.shutdown();
    }
}

```

---

### 3. `Callable` vs. `Runnable`

Both represent tasks to be executed concurrently by a thread pool, but differ in return capability and exception handling.

| Feature | `Runnable` | `Callable<V>` |
| --- | --- | --- |
| **Method** | `public void run()` | `public V call() throws Exception` |
| **Return Value** | None (`void`) | Returns a value of type `V` |
| **Exception Handling** | Cannot throw checked exceptions | Can throw checked exceptions |
| **Submission** | Executed via `execute()` or `submit()` | Executed via `submit()` |

```java
// Callable task returning a result
Callable<Integer> task = () -> {
    int result = 40 + 2;
    if (result < 0) throw new IllegalArgumentException("Invalid value");
    return result;
};

```

---

### 4. `Future`

A `Future<V>` represents the pending result of an asynchronous computation submitted via `Callable`.

#### Key Methods

* **`get()`:** Blocks until the computation completes, then retrieves its result.
* **`get(long timeout, TimeUnit unit)`:** Blocks for a maximum duration before throwing a `TimeoutException`.
* **`isDone()`:** Returns `true` if the task completed, was cancelled, or threw an exception.
* **`cancel(boolean mayInterruptIfRunning)`:** Attempts to cancel task execution.

```java
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> fetchTask = () -> {
            Thread.sleep(1000); // Simulate network latency
            return "Payload Data";
        };

        // Submit task to receive a Future handle
        Future<String> future = executor.submit(fetchTask);

        System.out.println("Doing main thread work while task runs...");

        // Non-blocking check
        while (!future.isDone()) {
            System.out.println("Waiting for task completion...");
            Thread.sleep(200);
        }

        // Blocking call to fetch result
        String result = future.get();
        System.out.println("Result received: " + result);

        executor.shutdown();
    }
}

```

---

### Summary Comparison

| Concept | Role in Framework | Primary Usage |
| --- | --- | --- |
| **`ExecutorService`** | Management Interface | Controls worker thread lifecycle and manages task queues. |
| **`ThreadPoolExecutor`** | Implementation | Configurable execution pool managing core/max threads and queue bounds. |
| **`Callable<V>`** | Task Definition | Encapsulates async work that computes a value or throws exceptions. |
| **`Future<V>`** | Result Handle | Provides blocking and non-blocking ways to track and fetch async results. |
