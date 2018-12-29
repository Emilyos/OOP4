package OOP.Solution;


import OOP.Provided.OOPAssertionFailure;
import OOP.Provided.OOPExceptionMismatchError;
import OOP.Provided.OOPExpectedException;
import OOP.Provided.OOPResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class OOPUnitCore {
    public static void assertEquals(Object expected, Object actual) {
        if (!(expected.equals(actual))) {
            throw new OOPAssertionFailure(expected, actual);
        }
    }

    public static void fail() {
        throw new OOPAssertionFailure();
    }


    public static OOPTestSummary runClass(Class<?> testClass) {
        return runClass(testClass, "");
    }


    public static OOPTestSummary runClass(Class<?> testClass, String tag) {
        if (!isOOPTestClass(testClass)) {
            throw new IllegalArgumentException();
        }
        Object instance = null;
        try {
            instance = testClass.getConstructor().newInstance();
        } catch (Exception ignore) {
            return null;
        }
        if (!runSetupMethods(testClass, instance)) {
            return null; // error running setup methods;
        }
        Map<String, OOPResult> testResults = new HashMap<>();
        List<Method> testMethods = getTestMethods(testClass, tag);
        for (Method testMethod : testMethods) {
            try {
                runBeforeMethods(testMethod, testClass, instance);
            } catch (Exception e) {
                System.out.print(e.getMessage());
                testResults.put(testMethod.getName(), new OOPResultImpl(OOPResult.OOPTestResult.ERROR, e.getMessage()));
                continue;
            }

            OOPResult result = testMethod(testMethod, instance, testClass);
            testResults.put(testMethod.getName(), result);

            try {

            } catch (Exception e) {

            }
        }

        return new OOPTestSummary(testResults);
    }


    private static OOPResult testMethod(Method method, Object instance, Class<?> testClass) {
        if (!isOOPTestMethod(method) || !isOOPTestClass(testClass)) {
            return null;
        }
        Field expectedException = null;
        for (Field f : testClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(OOPExceptionRule.class)) {
                expectedException = f;
            }
        }
        OOPResult.OOPTestResult result = null;
        String message = "";
        boolean coughtException = false;
        try {
            method.invoke(instance);
            expectedException.setAccessible(true);
            OOPExpectedException exception = (OOPExpectedException) expectedException.get(instance);
            if (exception.getExpectedException() != null) {
                result = OOPResult.OOPTestResult.ERROR;
            } else {
                result = OOPResult.OOPTestResult.SUCCESS;
                message = null;
            }

        } catch (IllegalAccessException ignore) {
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            coughtException = true;
            if (cause == null) { //wtf?
                result = OOPResult.OOPTestResult.ERROR;
                message = "msh 3arf shu aktb :D :D :D";
            } else {
                if (cause instanceof OOPAssertionFailure) {
                    result = OOPResult.OOPTestResult.FAILURE;
                    message = cause.getMessage();
                } else if (expectedException == null) {
                    result = OOPResult.OOPTestResult.ERROR;
                    message = getExceptionClassName(e);
                } else { // we expected something...
                    try {
                        expectedException.setAccessible(true);
                        OOPExpectedException exception = (OOPExpectedException) expectedException.get(instance);
                        if (exception.assertExpected((Exception) e.getCause())) {
                            result = OOPResult.OOPTestResult.SUCCESS;
                            message = null;
                        } else {
                            result = OOPResult.OOPTestResult.EXPECTED_EXCEPTION_MISMATCH;
                            Class<? extends Exception> fd = exception.getExpectedException();
                            message = new OOPExceptionMismatchError(fd,
                                    ((Exception) e.getCause()).getClass()).getMessage();
                        }
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }

        }
        return new OOPResultImpl(result, message);
    }

    private static boolean runSetupMethods(Class<?> testClass, Object instance) {
        Stack<Method> setupMethods = getSetupMethods(testClass);
        while (!setupMethods.empty()) {
            Method method = setupMethods.pop();
            try {
                method.invoke(instance);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private static void runBeforeMethods(Method method, Class<?> testClass, Object instance) throws Exception {
        Stack<Method> beforeMethods = getBeforeMethodsFor(method, testClass);
        //TODO do backup?s
        while (!beforeMethods.empty()) {
            try {
                beforeMethods.pop().invoke(instance);
            } catch (IllegalAccessException ignore) {
            } catch (InvocationTargetException cause) {
                throw new Exception(getExceptionClassName(cause));
            }
        }
    }

    /**
     * returns methods with annotation OOPSetup, these methods should be run in the order Stack::pop.
     *
     * @param clazz OOPTestClass to start the search from.
     * @return Stack of methods to run, Ordered.
     */
    private static Stack<Method> getSetupMethods(Class<?> clazz) {
        Stack<Method> result = new Stack<>();
        Class<?> currentClass = clazz;
        while (isOOPTestClass(currentClass)) {
            List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(OOPUnitCore::isOOPSetupMethod)
                    .collect(Collectors.toList());
            if (!methods.isEmpty() && result.search(methods.get(0)) == -1) {
                result.push(methods.get(0));
            }
            currentClass = currentClass.getSuperclass();
        }
        return result;
    }

    private static List<Method> getTestMethods(Class<?> clazz, String tag) {
        if (!isOOPTestClass(clazz)) {
            return new Stack<>();
        }
        ArrayList<Method> unordered = new ArrayList<>();
        HashSet<Method> ordered = new HashSet<>();
        boolean isOrdered = clazz.getAnnotation(OOPTestClass.class).value() == OOPTestClass.OOPTestClassType.ORDERED;
        Class<?> testClass = clazz;
        while (isOOPTestClass(testClass)) {
            Set<Method> testMethods = Arrays.stream(clazz.getDeclaredMethods()).filter(method -> {
                if (!isOOPTestMethod(method)) return false;
                return tag == null || tag.isEmpty() || method.getAnnotation(OOPTest.class).tag().equals(tag);
            }).collect(Collectors.toSet());
            if (!isOrdered) {
                unordered.addAll(testMethods);
            } else {
                boolean currOrdered = testClass.getAnnotation(OOPTestClass.class).value() == OOPTestClass.OOPTestClassType.ORDERED;
                if (currOrdered) {
                    ordered.addAll(testMethods);
                } else {
                    unordered.addAll(testMethods);
                }
            }
            testClass = testClass.getSuperclass();
        }
        List<Method> orderedList = ordered.stream().sorted(Comparator.comparing(m -> (m.getAnnotation(OOPTest.class).order()))).collect(Collectors.toList());
        unordered.addAll(orderedList);
        return unordered;
    }

    private static Stack<Method> getBeforeMethodsFor(Method method, Class<?> clazz) {
        if (!isOOPTestMethod(method)) {
            return new Stack<>();
        }
        String name = method.getName();
        Stack<Method> result = new Stack<>();
        Class<?> testClass = clazz;
        while (isOOPTestClass(testClass)) {
            result.addAll(Arrays.stream(testClass.getDeclaredMethods()).filter(m -> {
                if (!isOOPBeforeMethod(m)) return false;
                for (String s : m.getAnnotation(OOPBefore.class).value()) {
                    if (s.equals(name)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList()));
            testClass = testClass.getSuperclass();
        }
        return result;
    }

    /**
     * TODO Code duplication from <method>OOPUnitCore.getBeforeMethodsFor</method>
     */
    private static Stack<Method> getAfterMethodsFor(Method method, Class<?> clazz) {
        if (!isOOPTestMethod(method)) {
            return new Stack<>();
        }
        String name = method.getName();
        Stack<Method> result = new Stack<>();
        Class<?> testClass = clazz;
        while (isOOPTestClass(testClass)) {
            result.addAll(Arrays.stream(testClass.getDeclaredMethods()).filter(m -> {
                if (!isOOPAfterMethod(m)) return false;
                for (String s : m.getAnnotation(OOPAfter.class).value()) {
                    if (s.equals(name)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList()));
            testClass = testClass.getSuperclass();
        }
        return reverseMethodsOrder(result);
    }

    private static Stack<Method> reverseMethodsOrder(Stack<Method> methods) {
        Stack<Method> result = new Stack<>();
        while (!methods.empty()) {
            result.push(methods.pop());
        }
        return result;
    }

    private static boolean isOOPTestClass(Class<?> clazz) {
        return clazz != null && clazz.isAnnotationPresent(OOPTestClass.class);
    }

    private static boolean isOOPTestMethod(Method method) {
        return method != null && method.isAnnotationPresent(OOPTest.class);
    }

    private static boolean isOOPSetupMethod(Method method) {
        return method != null && method.isAnnotationPresent(OOPSetup.class);
    }

    private static boolean isOOPBeforeMethod(Method method) {
        return method != null && method.isAnnotationPresent(OOPBefore.class);
    }

    private static boolean isOOPAfterMethod(Method method) {
        return method != null && method.isAnnotationPresent(OOPAfter.class);
    }


    private static String getExceptionClassName(InvocationTargetException cause) {
        Throwable rootCause = cause;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
            rootCause = rootCause.getCause();
        return rootCause.getStackTrace()[0].getClassName();
    }
}
