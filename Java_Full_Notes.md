**********************************************************************************
Lecture 28
Topics: Wildcards in Generics, ? , ? extends , ? super

1. Unbounded Wildcard (?)
Meaning: "A list of some unknown type."

Use this when your method only performs operations that don't depend on the specific type inside the collection (e.g., printing elements, checking size, clearing list).

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








**********************************************************************************
