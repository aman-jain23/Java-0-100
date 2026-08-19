# Lecture 34
## Topics: Set and Map Methods, Enum Maps, Identity HashMaps.

Here is a practical breakdown of standard **Set and Map methods**, along with two specialized implementations: **EnumMap** and **IdentityHashMap**.

---

### 1. Essential Set & Map Methods

While both interfaces share logical overlap, their API structures differ because `Map` operates on `(Key, Value)` pairs.

| Operation | `Set<E>` Method | `Map<K, V>` Method | Description |
| --- | --- | --- | --- |
| **Insertion** | `add(e)` | `put(key, value)` | Adds an element or updates a key-value mapping. |
| **Retrieval** | *N/A (Use Iteration)* | `get(key)` | `Map` returns the value for a given key. |
| **Safe Get** | *N/A* | `getOrDefault(key, default)` | Returns `default` if the key isn't in the map. |
| **Removal** | `remove(e)` | `remove(key)` | Deletes the element or mapping. |
| **Check Presence** | `contains(e)` | `containsKey(key)` / `containsValue(val)` | Checks if the element/key/value exists. |
| **Views** | `iterator()` | `keySet()`, `values()`, `entrySet()` | Extracts elements, keys, values, or key-value entries. |

```java
Map<String, Integer> map = new HashMap<>();
map.put("Alice", 90);

// Useful Map methods added in Java 8
map.putIfAbsent("Alice", 95); // Ignores, "Alice" already exists
int score = map.getOrDefault("Bob", 0); // Returns 0 instead of null

```

---

### 2. EnumMap: High-Performance Map for Enums

`EnumMap` is a specialized implementation designed **exclusively when keys belong to a single `enum` type**.

#### Internal Mechanics:

* **Array-Backed:** Under the hood, `EnumMap` stores values inside a simple **Java array** (`Object[]`).
* **No Hashing Needed:** It maps each enum key to an array index using `enum.ordinal()` (the declaration order index).
* **Ultra-Fast Performance:** Operations (`get`, `put`) perform faster than `HashMap` because there are zero hash collisions, no linked lists/trees, and minimal memory overhead.

```java
enum Day { MONDAY, TUESDAY, WEDNESDAY }

public class EnumMapDemo {
    public static void main(String[] args) {
        // Must pass the enum Class token in the constructor
        Map<Day, String> schedule = new EnumMap<>(Day.class);
        
        schedule.put(Day.MONDAY, "Gym");
        schedule.put(Day.WEDNESDAY, "Coding");

        System.out.println(schedule.get(Day.MONDAY)); // "Gym"
    }
}

```

---

### 3. IdentityHashMap: Reference Equality (`==`)

Standard maps (like `HashMap`) use `.equals()` and `.hashCode()` to check key equality. **`IdentityHashMap` intentionally violates this contract and compares keys using reference equality (`==`)**.

#### Internal Mechanics:

* Uses `System.identityHashCode(key)` to determine position.
* Two keys `k1` and `k2` are considered identical **if and only if `k1 == k2**` (they point to the exact same memory address).
* It uses a single flat array where keys and values alternate at `[key1, value1, key2, value2]`.

#### Common Use Cases:

1. Building framework tools like object graph serializers or deep cloners (traversing objects while avoiding infinite loops).
2. Maintaining metadata/annotations attached to specific in-memory object instances.

```java
import java.util.*;

public class IdentityMapDemo {
    public static void main(String[] args) {
        Map<String, String> identityMap = new IdentityHashMap<>();

        String key1 = new String("Java");
        String key2 = new String("Java"); // Different reference in heap memory

        identityMap.put(key1, "Version 1");
        identityMap.put(key2, "Version 2");

        // Size is 2 because key1 != key2 in memory reference comparison
        System.out.println("IdentityHashMap size: " + identityMap.size()); // Output: 2

        // A standard HashMap would have size 1 because "Java".equals("Java") is true
        Map<String, String> standardMap = new HashMap<>();
        standardMap.put(key1, "Version 1");
        standardMap.put(key2, "Version 2");
        System.out.println("Standard HashMap size: " + standardMap.size()); // Output: 1
    }
}

```

---

### Summary Comparison

| Map Type | Key Equality Logic | Internal Storage | Primary Benefit |
| --- | --- | --- | --- |
| **`HashMap`** | `equals()` & `hashCode()` | Array of Buckets (Nodes / Trees) | General-purpose lookup |
| **`EnumMap`** | Enum Ordinal (`==`) | Compact Flat Array | Fastest speed and smallest memory footprint for Enums |
| **`IdentityHashMap`** | Reference Equality (`==`) | Single Alternating Key-Value Array | Compares distinct object instances |
