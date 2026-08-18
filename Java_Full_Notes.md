--- 
# Lecture 28
## Topics: Wildcards in Generics, ? , ? extends , ? super

### 1. Unbounded Wildcard (?)
Meaning: "A list of some unknown type."

Use this when your method only performs operations that don't depend on the specific type inside the collection (e.g., printing elements, checking size, clearing list).

```
import java.util.List;

public class UnboundedExample {
    // Accepts a List of ANY type
    public static void printList(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
    public static void main(String[] args) {
        List<String> names = List.of("Aman", "Rahul");
        List<Integer> numbers = List.of(10, 20, 30);
        printList(names);   // Works!
        printList(numbers); // Works!
    }
}
```
### 2. Upper Bounded Wildcard (? extends T)
Meaning: "A list of T or any subclass of T."

This sets a ceiling (upper limit). It allows you to READ data safely from a structure, but you CANNOT WRITE (add) new data to it (because Java doesn't know the exact subtype at runtime).

```
import java.util.List;

public class UpperBoundExample {
    // Accepts List of Number, Integer, Double, Float, etc.
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number num : list) {
            sum += num.doubleValue(); // Safe to READ as Number
        }
        return sum;
    }
    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.5, 2.5);
        System.out.println(sumOfList(intList));    // Output: 6.0
        System.out.println(sumOfList(doubleList)); // Output: 4.0
        // READ-ONLY RESTRICTION:
        // list.add(10); // COMPILE ERROR! Java prevents adding to ? extends
    }
}
```

### 3. Lower Bounded Wildcard (? super T)
Meaning: "A list of T or any superclass of T."

This sets a floor (lower limit). It allows you to WRITE (add) elements of type T safely into the collection.
``` 
import java.util.ArrayList;
import java.util.List;

public class LowerBoundExample {
    // Accepts List of Integer, Number, or Object
    public static void addNumbers(List<? super Integer> list) {
        // Safe to WRITE Integers because the list is guaranteed to hold Integer or its parent types
        list.add(10);
        list.add(20);
    }

    public static void main(String[] args) {
        List<Integer> intList = new ArrayList<>();
        List<Number> numList = new ArrayList<>();
        List<Object> objList = new ArrayList<>();

        addNumbers(intList); // Works!
        addNumbers(numList); // Works!
        addNumbers(objList); // Works!

        System.out.println(intList); // [10, 20]
    }
}
```




**********************************************************************************
