# Lecture 57
## Topics: Completable Future, Fork - Join Pool, Thread Local and Virtual Threads.

### 1. `CompletableFuture` (Java 8+)

`CompletableFuture` implements `Future` and `CompletionStage`. It enables non-blocking, asynchronous programming by allowing tasks to be chained, combined, and handled reactively using callbacks rather than blocking with `.get()`.

#### Key Pipeline Patterns

* **Pipeline Transformations:** `thenApply` (maps result synchronously), `thenApplyAsync` (maps result on another thread).
* **Chaining Dependent Async Tasks:** `thenCompose` (flattens nested `CompletableFuture`s, similar to `flatMap`).
* **Combining Independent Async Tasks:** `thenCombine` (runs two futures concurrently and combines their results).
* **Exception Handling:** `exceptionally` or `handle`.

```java
import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> fetchUserData(101))             // 1. Async Fetch
            .thenApply(user -> user.toUpperCase())                          // 2. Transform result
            .thenCompose(user -> CompletableFuture.supplyAsync(() -> fetchUserOrders(user))) // 3. Chain dependent async task
            .thenCombine(CompletableFuture.supplyAsync(() -> fetchDiscountCode()),            // 4. Combine independent task
                (orders, discount) -> orders + " with code " + discount)
            .exceptionally(ex -> "Fallback Data: " + ex.getMessage())      // 5. Exception Fallback
            .thenAccept(finalResult -> System.out.println("Output: " + finalResult)); // 6. Consumer
    }

    private static String fetchUserData(int id) { return "User_" + id; }
    private static String fetchUserOrders(String user) { return user + "'s Orders"; }
    private static String fetchDiscountCode() { return "SAVE20"; }
}

```

---

### 2. Fork/Join Framework & `ForkJoinPool` (Java 7+)

Designed for **divide-and-conquer** algorithms that recursively split tasks into smaller subtasks, process them in parallel, and combine the results.

#### Work-Stealing Algorithm

`ForkJoinPool` manages a fixed pool of worker threads. Each thread has its own **double-ended queue (deque)** of tasks:

* A worker pushes new subtasks to the **head** of its own deque and pops them from the head (LIFO order).
* If a worker runs out of tasks, it **steals** tasks from the **tail** of another busy worker's deque (FIFO order). This minimizes thread contention.

```java
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000;
    private final long[] array;
    private final int start, end;

    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if ((end - start) <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) sum += array[i];
            return sum;
        } else {
            int mid = start + (end - start) / 2;
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);

            leftTask.fork(); // Submit left task to work-stealing queue
            long rightResult = rightTask.compute(); // Compute right task directly
            long leftResult = leftTask.join();      // Wait for left task result

            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        long[] numbers = new long[10_000];
        ForkJoinPool pool = ForkJoinPool.commonPool(); // Common pool used by Parallel Streams
        long totalSum = pool.invoke(new SumTask(numbers, 0, numbers.length));
        System.out.println("Sum: " + totalSum);
    }
}

```

---

### 3. `ThreadLocal`

`ThreadLocal` provides **thread-confined state**. Variables wrapped in `ThreadLocal` are isolated per thread, meaning each thread accessing the variable gets its own independently initialized copy.

#### Memory Leak Warning in Thread Pools

Because thread pool worker threads are reused across tasks, thread-local variables survive after a task finishes. If you do not explicitly invoke `.remove()`, leftover data can cause **memory leaks** or **dirty state pollution** across tasks.

```java
public class ThreadLocalDemo {
    // Unique context per thread (e.g., Transaction ID or User Session)
    private static final ThreadLocal<String> userContext = ThreadLocal.withInitial(() -> "GUEST");

    public void processRequest(String userId) {
        try {
            userContext.set(userId); // Bind state to current thread
            System.out.println(Thread.currentThread().getName() + " processing for " + userContext.get());
        } finally {
            userContext.remove();   // CRITICAL: Always clean up to prevent memory leaks in pools
        }
    }
}

```

---

### 4. Virtual Threads (Project Loom, Java 21+)

Virtual Threads are **lightweight threads** managed directly by the Java Virtual Machine (JVM) rather than the underlying Operating System (OS).

#### Platform Threads vs. Virtual Threads

* **Platform Threads:** 1-to-1 wrapper around OS kernel threads. Heavy memory footprint (~1 MB stack space each). Creating millions causes `OutOfMemoryError`.
* **Virtual Threads:** $M$-to-$N$ mapping. Millions of virtual threads run on top of a small pool of OS platform threads (known as **Carrier Threads**).

#### How Virtual Threads Handle I/O Blocking

When a virtual thread executes a blocking operation (e.g., database read, network call, `Thread.sleep`):

1. The JVM **unmounts** the virtual thread from its Carrier Thread.
2. The Carrier Thread is freed up immediately to execute other virtual threads.
3. Once the I/O completes, the JVM **remounts** the virtual thread onto an available Carrier Thread to resume execution.

> **Note on Pinning:** Avoid blocking inside `synchronized` blocks or native methods. This "pins" the virtual thread to its carrier thread, preventing unmounting. Use `ReentrantLock` instead of `synchronized` for virtual thread codebases.

```java
import java.util.concurrent.Executors;

public class VirtualThreadDemo {
    public static void main(String[] args) {
        // Create an ExecutorService that spawns a new Virtual Thread per task
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    Thread.sleep(1000); // Non-blocking for OS threads! Unmounts automatically.
                    return taskId;
                });
            }
        } // AutoCloseable executor waits for all 10,000 tasks to finish
        System.out.println("Finished 10,000 concurrent tasks efficiently!");
    }
}

```

---

### Summary Comparison

| Concept | Primary Use Case | Scaling Limit | Key Takeaway |
| --- | --- | --- | --- |
| **`CompletableFuture`** | Asynchronous pipelines & reactive callbacks. | Dependent on underlying pool. | Non-blocking execution without thread blocking calls like `.get()`. |
| **`ForkJoinPool`** | Parallel divide-and-conquer computation. | CPU Core bounded. | Uses **work-stealing** to maximize CPU core utilization. |
| **`ThreadLocal`** | Thread-isolated state (e.g., Request Context). | Bounded by thread count. | Must call `.remove()` to prevent leaks in thread pools. |
| **Virtual Threads** | High-throughput thread-per-request I/O services. | **Millions of threads**. | Cheap, lightweight JVM threads; eliminates async complexity for I/O bound work. |
