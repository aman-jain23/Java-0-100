# Lecture 46
## Topics: Java Heap Memory, Garbage Collection Algorithm.

### Java Heap Memory Structure

To make garbage collection efficient, the Java Heap is divided into distinct generations based on the **Weak Generational Hypothesis**—the empirical observation that most objects die shortly after creation (e.g., short-lived local variables).

```
┌─────────────────────────────────────────────────────────────────────────┐
│                               JAVA HEAP                                 │
├───────────────────────────────────────────┬─────────────────────────────┤
│               Young Generation            │       Old Generation        │
│                (~1/3 of Heap)             │        (~2/3 of Heap)       │
├───────────────────┬───────────────────────┼─────────────────────────────┤
│    Eden Space     │    Survivor Spaces    │       Tenured / Old         │
│     (~80%)        │   S0 (8%)  │  S1 (8%)  │          Space              │
└───────────────────┴────────┴──────────────┴─────────────────────────────┘

```

#### 1. Young Generation

Where all newly created objects are allocated (`new` keyword).

* **Eden Space:** The initial landing spot for almost all new objects.
* **Survivor Spaces (S0 / FromSpace & S1 / ToSpace):** Two equal-sized spaces used during minor collections. One survivor space is always kept empty.
* **Collection Type:** Triggered when Eden fills up. Known as **Minor GC**. It is fast because most objects in Eden are dead and quickly discarded.

#### 2. Old Generation (Tenured)

Holds long-surviving objects.

* Objects that survive a specific number of Minor GC cycles (controlled by the `-XX:MaxTenuringThreshold` flag, default up to 15) are promoted from Survivor space to the Old Generation.
* Large objects that cannot fit in Eden may be allocated directly in the Old Generation.
* **Collection Type:** Triggered when the Old Generation reaches a threshold capacity. Known as **Major GC** or **Full GC**. It inspects a much larger memory area and takes significantly longer.

---

### How Garbage Collection Works

Garbage Collection (GC) automatically identifies unreachable objects and reclaims their memory.

#### Step 1: Object Reachability (Root Tracing)

The GC determines an object's eligibility by tracing paths from **GC Roots**. An object is reachable if there is a chain of references connecting it to a GC Root.

**Common GC Roots:**

* Local variables and parameters active inside stack frames.
* Loaded class `static` references.
* Active thread references.
* JNI (Java Native Interface) global pointers.

```
[ GC Root ] ──> [ Object A ] ──> [ Object B ]  (Reachable -> Retained)

[ Object C ] ──> [ Object D ]                  (Unreachable -> Collected)

```

#### Step 2: GC Execution Phases

1. **Marking:** The GC traverses the object graph starting from GC Roots and marks all reachable objects as "live."
2. **Sweeping:** Unmarked (unreachable) objects are identified and their memory addresses are reclaimed.
3. **Compacting (Optional):** Live objects are moved to a contiguous block of memory to eliminate memory fragmentation (gaps between memory slots).

---

### Core Garbage Collection Algorithms

| Algorithm | How It Works | Pros | Cons |
| --- | --- | --- | --- |
| **Mark-Sweep** | Marks live objects, then sweeps through memory to free dead objects. | Simple implementation; no overhead of moving objects. | Leaves memory fragmented with empty gaps. |
| **Mark-Compact** | Marks live objects, sweeps dead ones, then slides all remaining live objects to the beginning of the heap. | Eliminates fragmentation; allocates new objects fast. | Higher pause times due to object relocation. |
| **Copying** | Splits memory into two halves. Copies live objects from active half to secondary half, then wipes active half. | Zero fragmentation; extremely fast sweep. | Requires double the memory capacity; half is always idle (used in Survivor S0/S1 spaces). |

---

### Production Garbage Collectors in Modern Java

Java provides several specialized GC implementations tailored for different application demands:

* **Serial GC (`-XX:+UseSerialGC`):** Single-threaded collector designed for single-threaded or small CLI environments. Halts application threads during operation ("Stop-The-World").
* **Parallel GC (`-XX:+UseParallelGC`):** Multi-threaded collector for the Young and Old generations. Focuses on **maximum throughput** at the cost of execution pause times.
* **G1 GC (Garbage-First) (`-XX:+UseG1GC`):** Default since Java 9. Divides heap into thousands of equal region blocks ($1\text{ MB} - 32\text{ MB}$). Collects regions with the most garbage first to maintain low, predictable pause times.
* **ZGC (`-XX:+UseZGC`) / Shenandoah:** Low-latency collectors (Java 15+). Perform concurrent marking and compacting without pausing application execution for more than a few milliseconds, even on terabyte-scale heaps.

---

### Code Trace Example

```java
public class GCDemo {
    public static void main(String[] args) {
        // Allocated in Eden Space
        DataHolder obj1 = new DataHolder("Active Object"); 
        
        createTempObjects(); // Allocates short-lived objects
        
        // System.gc() requests JVM to run Garbage Collection (not guaranteed)
        System.gc(); 
    }

    private static void createTempObjects() {
        // Allocated in Eden Space
        DataHolder temp = new DataHolder("Temporary Object"); 
    } // 'temp' loses stack scope here -> eligible for GC
}

class DataHolder {
    String name;
    public DataHolder(String name) { this.name = name; }
}

```

**Memory Flow:**

1. `obj1` is created in **Eden Space** and referenced by the `main` stack frame (GC Root).
2. `temp` is created in **Eden Space** inside `createTempObjects()`.
3. When `createTempObjects()` completes, the reference to `temp` is popped from the stack. The object `"Temporary Object"` becomes unreachable.
4. On the next Minor GC cycle, `"Temporary Object"` is collected from **Eden**. `"Active Object"` is copied to **Survivor Space S0** because it is still reachable from `obj1`.
