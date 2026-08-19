# Lecture 27
## Topics: Java Generics, Bounded types using extends

### 1. Generics (The Safety Label)
Without generics, a List in Java is like an unlabeled box: you can put anything inside it (Strings, Numbers, etc.). But when you pull something out, you have to guess what it is and cast it manually. If you guess wrong, your program crashes.

```
// UNLABELED BOX (No Generics)
List myBox = new ArrayList(); 
myBox.add("Apple");
myBox.add(100); // Java lets you do this, but it's dangerous!

// You have to force-cast it when pulling items out:
String item = (String) myBox.get(1); // CRASH! 100 is an Integer, not a String.
```
Generics let you put a label on the box using angle brackets <> to restrict what goes inside.
```
// LABELED BOX (With Generics)
List<String> stringBox = new ArrayList<>(); 
stringBox.add("Apple"); 
// stringBox.add(100); // COMPILER ERROR! Java stops you BEFORE you even run the code.

String item = stringBox.get(0); // No casting needed. Java KNOWS it's a String.
```

### 2. Generic Classes (Making Your Own Labeled Box)
Instead of hardcoding a class to only work with String or Integer, you use a placeholder like <T> (which just stands for Type).

```
// T is a placeholder for whatever data type the user chooses later
public class GlassBox<T> {
    private T item;

    public void put(T item) {
        this.item = item;
    }

    public T get() {
        return item;
    }
}
```
Now, you can re-use this exact same class for any type:

```
// Create a box specifically for Integers
GlassBox<Integer> intBox = new GlassBox<>();
intBox.put(50);

// Create a box specifically for Strings
GlassBox<String> strBox = new GlassBox<>();
strBox.put("Hello");
```

### 3. Bounded Types Using extends (Setting Limits)
Sometimes, a placeholder like <T> is too flexible. What if you want to write a calculator box, but someone puts a String or a Dog inside it? You can't calculate math on a Dog!

Using <T SomeClass extends> places an upper limit on your placeholder. It tells Java: "T can be any type, as long as it is a subclass of SomeClass."

```
// T MUST be Number or a child of Number (Integer, Double, Float, Long)
public class CalculatorBox<T extends Number> {
    private T number;

    public CalculatorBox(T number) {
        this.number = number;
    }

    public double square() {
        // Safe to call .doubleValue() because Java GUARANTEES T is a Number!
        return number.doubleValue() * number.doubleValue();
    }
}
```

How it behaves in code:

```
public class Main {
    public static void main(String[] args) {
        // WORKS: Integer extends Number
        CalculatorBox<Integer> calc1 = new CalculatorBox<>(5);
        System.out.println(calc1.square()); // Output: 25.0

        // WORKS: Double extends Number
        CalculatorBox<Double> calc2 = new CalculatorBox<>(2.5);
        System.out.println(calc2.square()); // Output: 6.25

        // ERROR: String does NOT extend Number! Java rejects this instantly.
        // CalculatorBox<String> calc3 = new CalculatorBox<>("Hello"); 
    }
}
---

# Lecture 36
## Topics: Comparable Interface, Collection class, Comparing Objects.

