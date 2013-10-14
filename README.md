Term
====

Create simple command line access to java methods within classes.

Main config
===========

```java
@Termconf (usage           = "<usage string>",   // optional
           example         = "<example string>", // optional
           ops_width       = 30,                 // optional:
                                                 //   define width of options
                                                 //   column in help message
                                                 // (default: 24)
           term_width      = 100,                // optional:
                                                 //   define width of your
                                                 //   terminal for help message
                                                 // (default: 80)
           ignored_methods = {"main", "init"}    // optional:
                                                 //   Sting array containing
                                                 //   names of methods which
                                                 //   should be ignored by Term
                                                 // (default: {"main"})
          )
public class myclass {
    ...
}
```

Method config
=============

```java
@Termmeth (short_param = "t",                  // one character
           description = "method description",
           des_form    = false,                // optional:
                                               //   whether to format
                                               //   description string or
                                               //   not
                                               // (default: true)
           arguments   = {"TEXT", "INT"}       // String array containing
                                               // method arguments
                                               // description printed in
                                               // help message
          )
public static void ...
```

Example
=======

```java
@Termconf (usage   = "java Example [option] [arguments]",
           example = "java Example -t ha 5")
public class Example {
    public static void main (String[] args) {
        Term.init ("Example", args);
    }

    @Termmeth (short_param = "t",
               description = "This is a test method",
               arguments   = { "TEXT", "REPETITIONS" })
    public static void test (String a, int b) {
        String res = "";
        for (int i = 0; i < b; i++) {
            res += a;
        }
        System.out.println (res);
    }

    @Termmeth (short_param = "c",
               description = "Concatenate two strings",
               arguments   = { "TEXT", "TEXT" })
    public static String cat (String a, String b) {
        return a + b;
    }

    @Termmeth (short_param = "m",
               description = "Multiplicate two integer values",
               des_form    = false,
               arguments   = { "INT", "INT" })
    public static int mul (int a, int b) {
        return a * b;
    }
}
```

### Help message output ###
    Usage: java Example [option] [arguments]
    
    Available options:
      -h, --help            Print this help page
      -t, --test TEXT REPETITIONS
                            This is a test method
      -c, --cat TEXT TEXT   Concatenate two strings
      -m, --mul INT INT     Multiplicate two integer values
    
    Example:
      java Example -t ha 5

### Invoke a method ###

    bin $ java Example --cat hey ho
    heyho
    bin $
