# Lecture 53
## Topics: Java Locks, Reentrant Lock, Read Write Lock, Stamped Look, Semaphore and Condition.

Java provides the explicit locking mechanism package `java.util.concurrent.locks` alongside synchronization primitive utilities to give finer, higher-performance concurrency control than intrinsic `synchronized` blocks.

---

### 1. `ReentrantLock`

An explicit implementation of the `Lock` interface that works similarly to `synchronized` methods (mutual exclusion), but provides flexible features like **timed lock attempts**, **try-lock capability**, and **fairness settings**.

* **Reentrancy:** A thread holding the lock can re-acquire it multiple times without deadlocking itself.
* **Fairness:** When passed `true` in the constructor, it grants the lock to the longest-waiting thread (prevents thread starvation).

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    private final Lock lock = new ReentrantLock(true); // Fair lock
    private int count = 0;

    public void increment() {
        lock.lock(); // Acquire lock
        try {
            count++;
        } finally {
            lock.unlock(); // Always release in finally block
        }
    }
}

```

---

### 2. `ReentrantReadWriteLock`

Maintains a pair of associated locks: one for **read-only operations** and one for **write operations**.

* **Read Lock:** Can be held concurrently by multiple reading threads, provided no thread holds the write lock.
* **Write Lock:** Exclusive—only one thread can write at a time. Blocks all read and write attempts by other threads.
* **Best Use Case:** High-read, low-write data structures (e.g., caches, lookups).

```java
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private int data = 0;

    public int readData() {
        rwLock.readLock().lock();
        try {
            return data;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void writeData(int value) {
        rwLock.writeLock().lock();
        try {
            this.data = value;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}

```

---

### 3. `StampedLock` (Java 8+)

An advanced lock mechanism that improves read performance over `ReadWriteLock` by introducing **Optimistic Reading**. It returns a numeric **stamp** (long value) used to validate or release locks.

* **Not Reentrant:** A thread cannot re-acquire the lock without risking a deadlock.
* **Optimistic Reading:** Allows reader threads to read data *without acquiring a full read lock*. Before using the read data, it validates the stamp. If a write occurred in between, it falls back to a standard read lock.

```java
import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {
    private final StampedLock sl = new StampedLock();
    private double x, y;

    // Optimistic Read Example
    public double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead(); // Try optimistic lock
        double currentX = x, currentY = y;   // Read shared state

        if (!sl.validate(stamp)) {           // Check if a write happened in between
            stamp = sl.readLock();           // Fallback to full pessimistic read lock
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}

```

---

### 4. `Semaphore`

A counting lock used to manage access to a **fixed pool of shared resources**.

* **Permits:** Maintains a set of permits. Threads call `acquire()` to claim a permit and `release()` to return it.
* **Resource Throttling:** If no permits are available, `acquire()` blocks until one is returned. A binary semaphore (1 permit) acts similarly to a mutual exclusion lock.

```java
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    // Limit access to a max of 3 concurrent threads
    private final Semaphore semaphore = new Semaphore(3);

    public void accessResource() {
        try {
            semaphore.acquire(); // Decrements permit count
            System.out.println(Thread.currentThread().getName() + " accessing connection pool");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); // Increments permit count
        }
    }
}

```

---

### 5. `Condition`

Replaces the traditional `Object` wait/notify methods (`wait()`, `notify()`, `notifyAll()`) when using explicit `Lock` implementations.

* **Multiple Wait-Sets:** A single `Lock` can have multiple `Condition` instances (e.g., `notFull` and `notEmpty` conditions for a single buffer), enabling targeted thread signaling.
* **Key Methods:** `await()` (replaces `wait()`), `signal()` (replaces `notify()`), and `signalAll()` (replaces `notifyAll()`).

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer {
    private final Lock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition(); 
    private final Condition notEmpty = lock.newCondition(); 

    private final Object[] items = new Object[10];
    private int putptr, takeptr, count;

    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) 
                notFull.await(); // Wait until buffer has space
            items[putptr] = x;
            if (++putptr == items.length) putptr = 0;
            ++count;
            notEmpty.signal();   // Signal waiting consumers
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) 
                notEmpty.await(); // Wait until buffer has items
            Object x = items[takeptr];
            if (++takeptr == items.length) takeptr = 0;
            --count;
            notFull.signal();     // Signal waiting producers
            return x;
        } finally {
            lock.unlock();
        }
    }
}

```

---

### Quick Comparison Matrix

| Lock Type | Primary Purpose | Reentrant? | Key Advantage |
| --- | --- | --- | --- |
| **`ReentrantLock`** | Explicit mutual exclusion | **Yes** | Flexible locking, timed tries, fair policy options. |
| **`ReentrantReadWriteLock`** | Multiple readers, single writer | **Yes** | Increases throughput for read-heavy workloads. |
| **`StampedLock`** | High-performance lock with optimistic reads | **No** | Fastest read performance via optimistic verification. |
| **`Semaphore`** | Limit concurrent access to resources | N/A | Controls rate and throughput for resource pools. |
| **`Condition`** | Advanced inter-thread signaling | N/A | Multiple condition queues bound to a single lock. |

