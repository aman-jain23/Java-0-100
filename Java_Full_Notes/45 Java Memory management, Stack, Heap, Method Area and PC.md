# Lecture 45
## Topics: Java Memory management, Stack, Heap, Method Area and PC.

Java memory management is handled by the Java Virtual Machine (JVM). When a Java program runs, the JVM divides system memory into distinct logical regions, each serving a specific purpose during execution.

---

**JVM Memory Structure**

```
                       ┌─────────────────────────────────────────┐
                       │               JVM Memory                │
                       └────────────────────┬────────────────────┘
                                            │
         ┌──────────────────────────────────┴──────────────────────────────────┐
         │                                                                     │
  Per-Thread Regions                                                  Shared Regions
 (Created per thread)                                             (Shared across threads)
 ┌───────────────────────┐                                       ┌───────────────────────┐
 │   Program Counter     │                                       │       Heap Area       │
 │       (PC)            │                                       │ (Objects & Instances) │
 └───────────────────────┘                                       └───────────────────────┘
 ┌───────────────────────┐                                       ┌───────────────────────┐
 │    JVM Stack Area     │                                       │      Method Area      │
 │    (Frame-based)      │                                       │  (Metaspace / Class)  │
 └───────────────────────┘                                       └───────────────────────┘
 ┌───────────────────────┐
 │  Native Method Stack  │
 └───────────────────────┘

```

---

**1. Stack Area**

* **Scope:** Created per thread (Thread-safe by default).
* **Purpose:** Stores **method frames**, **local variables** (primitives), and **references** to objects living on the Heap.
* **Lifecycle:** Follows Last-In-First-Out (LIFO). A frame is created when a method is called and destroyed when the method returns.
* **Error:** Throws `java.lang.StackOverflowError` if stack space is exhausted (e.g., infinite recursion).

---

**2. Heap Area**

* **Scope:** Shared across all threads in the application.
* **Purpose:** Stores **all objects** created via `new`, array instances, and instance variables.
* **Lifecycle:** Managed automatically by the **Garbage Collector (GC)**. Objects remain on the heap until they are no longer reachable by any thread.
* **Error:** Throws `java.lang.OutOfMemoryError: Java heap space` if the allocated space is full and GC cannot free memory.

---

**3. Method Area (Metaspace since Java 8)**

* **Scope:** Shared across all threads.
* **Purpose:** Stores **class-level structure**, including:
* Class metadata (loaded bytecode, field descriptions, method data).
* `static` variables and methods.
* Runtime Constant Pool (literal constants, method/field references).


* **Implementation:** Prior to Java 8, this was stored in the JVM's "PermGen" (Permanent Generation). Since Java 8, it is called **Metaspace**, which uses native off-heap memory and grows dynamically.

---

**4. Program Counter (PC) Register**

* **Scope:** Created per thread.
* **Purpose:** Holds the memory address of the JVM instruction currently being executed by that thread.
* **Behavior:** Advances to the next instruction as code runs. If executing a native method (`JNI`), the PC register value remains undefined.

---

**Code Execution & Memory Walkthrough**

```java
public class MemoryDemo {
    public static void main(String[] args) {
        int id = 101;                       // Stack
        String name = "Java";               // Reference on Stack, Object on Heap
        Person p = new Person(id, name);    // Reference 'p' on Stack, Person object on Heap
        p.display();
    }
}

class Person {
    static String type = "Human";          // Method Area (Metaspace)
    int personId;                          // Heap (part of Person instance)
    String personName;                     // Heap (part of Person instance)

    public Person(int id, String name) {
        this.personId = id;
        this.personName = name;
    }

    public void display() {
        System.out.println(personId + " : " + personName);
    }
}

```

**Memory Layout During `main()` Execution:**

```
      STACK (Per Thread)                          HEAP (Shared)
┌─────────────────────────────┐        ┌──────────────────────────────────┐
│  main() Frame               │        │  Person Object                   │
│  ├── id = 101               │        │  ├── personId = 101              │
│  ├── name ──────────────────┼───────>│  └── personName ─────────────────┼──┐
│  └── p ─────────────────────┼──┐     └──────────────────────────────────┘  │
└─────────────────────────────┘  │                                           │
                                 └────>┌──────────────────────────────────┐  │
                                       │  String Object ("Java")          │<─┘
                                       └──────────────────────────────────┘

                                            METHOD AREA / METASPACE (Shared)
                                       ┌──────────────────────────────────┐
                                       │  MemoryDemo.class, Person.class  │
                                       │  static String type = "Human"    │
                                       └──────────────────────────────────┘

```

---

**Summary Comparison**

| Memory Region | Shared/Per-Thread | Stores | Managed By | Primary Error |
| --- | --- | --- | --- | --- |
| **Stack** | Per-Thread | Local variables, primitives, object reference pointers, method execution frames | Method execution lifecycle (LIFO) | `StackOverflowError` |
| **Heap** | Shared | All instantiated objects, arrays, instance fields | Garbage Collector | `OutOfMemoryError: Java heap space` |
| **Method Area** | Shared | Class metadata, static members, runtime constant pool | Metaspace / JVM Class Loader | `OutOfMemoryError: Metaspace` |
| **PC Register** | Per-Thread | Memory address of current bytecode instruction | JVM CPU thread scheduling | None |
