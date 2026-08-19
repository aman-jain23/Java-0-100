# Lecture 30
## Topics: Iterable interface, Why iterator exists, for-each loop.

Think of Java collections like different types of containers: an ArrayList is a neat row of labeled boxes, while a HashSet is a bucket where items bounce around in no particular order.

Even though they store data differently under the hood, Java gives us a unified way to look at every item in those containers using three core ideas: Iterable, Iterator, and the For-Each Loop.

## 1. The Iterable Interface: "I can be looped through!"
If a Java class implements the Iterable interface, it is making a contract: "I guarantee that you can step through my items one by one."

Almost all collection classes in Java (ArrayList, LinkedList, HashSet, TreeSet) implement Iterable.

```
// Because ArrayList implements Iterable, we know we can walk through it
List<String> fruits = new ArrayList<>();
fruits.add("Apple");
fruits.add("Banana");
fruits.add("Cherry");
```

## 2. Why Does Iterator Exist?
Before the Iterator came along, developers used standard for loops with indices (list.get(i)). This creates two major problems:

Not all data structures have indices: A HashSet or LinkedList doesn't support fast indexed access like an array. get(i) on a LinkedList is painfully slow because it has to recount nodes from the start every single time.

Concurrent Modification Errors: If you try to remove an item from a list inside a standard for loop while iterating over it, the indices shift, leading to skipped items or crashes.

The Iterator exists as a universal pointer that knows how to traverse any data structure safely without needing index numbers.

The Iterator in Action
An iterator gives you three main methods:

### hasNext(): "Is there another item ahead?" (Returns true/false)

### next(): "Hand me the next item and move the pointer forward."

### remove(): "Safely delete the item I just passed."

```
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorExample {
    public static void main(String[] args) {
        List<String> fruits = new ArrayList<>(List.of("Apple", "Banana", "Cherry"));

        // Get the iterator object from the list
        Iterator<String> it = fruits.iterator();

        // Loop using the iterator
        while (it.hasNext()) {
            String fruit = it.next();
            
            // Safe removal using Iterator's remove method
            if (fruit.equals("Banana")) {
                it.remove(); // Safely removes "Banana" without breaking the loop
            }
        }

        System.out.println(fruits); // Output: [Apple, Cherry]
    }
}
```
## 3. The For-Each Loop: Syntactic Sugar
While Iterator is powerful, writing while (it.hasNext()) every time gets repetitive. Java introduced the for-each loop (enhanced for loop) to simplify code readability.

Here is the secret: The for-each loop is just hidden Iterator code under the hood! It works on any object that implements Iterable.

```
import java.util.List;

public class ForEachExample {
    public static void main(String[] args) {
        List<String> fruits = List.of("Apple", "Banana", "Cherry");

        // Clean, readable syntax
        for (String fruit : fruits) {
            System.out.println(fruit);
        }
    }
}
```
