import org.w3c.dom.ls.LSOutput;

/*
 * PRACTICE 01: Introduction to Java
 * -----------------------------------
 * Goal: get comfortable with the compile -> run cycle and prove to yourself
 * (and any recruiter reading this file) that you understand WORA mechanically.
 *
 * Instructions: Write the code yourself below each TODO. 
 * Do not paste solutions.
 * Compile with:  javac Practice01_Introduction.java
 * Run with:      java Practice01_Introduction
 */
public class Practice01_Introduction {

    public static void main(String[] args) {

        // TODO 1: Print "Hello, Java!" to the console.
        System.out.println("Hello, Java!");

        // TODO 2: Print the JVM version your program is running on.
        //   Hint: look up System.getProperty and the correct key for java version.

        String version = System.getProperty("java.version");
        System.out.println("Java Version: " + version);

        // TODO 3: Print the operating system name your program is running on.
        //   Hint: same System.getProperty approach, different key.

        String osName = System.getProperty("os.name");
        System.out.println("OS Name: " + osName);

        // TODO 4: Write 3-4 lines of comments (as if explaining to a recruiter)
        //   describing what happens between running "javac Practice01_Introduction.java"
        //   and seeing output on screen. Mention bytecode and the JVM by name.

        String comment = "First of alll the=dsj dsjfds fdsjfsl jkfkl flksjfldskjfdslkfj skfjdslkfj dsfjds fdsfdsjlfkjdsfkldsjfkldsjflkdsjfdslfj dsklfdsjflkdsjfldskjfldskjfdslfkjdsfl dsfjsklfdsjflkdsjfldsj flkdsjfldskjfdslkfjdslkfjslkfjdskjfewiojfdsiffjadskfaa adfsd adsf fads fadsf adsfadsfadsfads fads asdfadsf adsfdfadsfadsf afadsf adsf s";
        System.out.println(comment);

        // TODO 5 (stretch): Compile this file, then open the generated .class file
        //   in a text editor or hex viewer. It will look like garbage/binary — that's
        //   the point. Write one comment line describing what you observed and why
        //   it's not human-readable, unlike the .java file.

        // TODO 6 (stretch, for your GitHub README): In your own words (2-3 sentences,
        //   write it as a code comment here first, then move it to README later),
        //   explain what "Write Once, Run Anywhere" means using THIS program as the example.
    }
}