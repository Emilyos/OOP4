package OOP.Solution;


import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class OOPUnitCore {
    public static void assertEquals(Object expected, Object actual) {
    }

    public static void fail() {
    }


    public static OOPTestSummary runClass(Class<?> testClass) {
        return runClass(testClass, "");
    }


    public static OOPTestSummary runClass(Class<?> testClass, String tag) {
        if (testClass == null || !isOOPTestClass(testClass)) {
            throw new IllegalArgumentException();
        }


        return null;
    }

    /**
     * returns methods with annotation OOPSetup, these methods should be run in the order Stack::pop.
     *
     * @param clazz OOPTestClass to start the search from.
     * @return Stack of methods to run, Ordered.
     */
    private static Vector<Method> getSetupMethods(Class<?> clazz) {
        Stack<Method> result = new Stack<>();
        Class<?> currentClass = clazz;
        while (isOOPTestClass(currentClass)) {
            List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(OOPSetup.class))
                    .collect(Collectors.toList());
            if (!methods.isEmpty() && result.search(methods.get(0)) == -1) {
                result.push(methods.get(0));
            }
            currentClass = currentClass.getSuperclass();
        }
        return result;
    }


    private static boolean isOOPTestClass(Class<?> clazz) {
        return clazz != null && clazz.isAnnotationPresent(OOPTestClass.class);
    }


}
