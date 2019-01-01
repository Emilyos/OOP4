package OOP.Solution;

import OOP.Provided.OOPExpectedException;

import java.util.HashSet;

public class OOPExpectedExceptionImpl implements OOPExpectedException {

    private Class<? extends Exception> expected;
    HashSet<String> expectedMessages;

    private OOPExpectedExceptionImpl(Class<? extends Exception> expected) {
        this.expected = expected;
        expectedMessages = new HashSet<>();
    }

    @Override
    public Class<? extends Exception> getExpectedException() {
        return expected;
    }

    @Override
    public OOPExpectedException expect(Class<? extends Exception> expected) {
        this.expected = expected;
        return this;
    }

    @Override
    public OOPExpectedException expectMessage(String msg) {
        if (this == none()) {
            return null;
        }
        if (!msg.isEmpty()) expectedMessages.add(msg);
        return this;
    }

    @Override
    public boolean assertExpected(Exception e) {
        if (expected == null || !isSubException(e)) return false;
        return expectedMessages.stream().allMatch(expectedMessage -> e.getMessage().contains(expectedMessage));
    }

    /**
     * return true if e1 is subclass exception of expected
     *
     * @param e1
     * @return
     */
    private boolean isSubException(Exception e1) {
        if (e1 == null || expected == null) {
            return false;
        }
        Class<?> current = e1.getClass();
        while (current != null) {
            if (current == expected) {
                return true;
            }
            if (current == Exception.class) {
                break;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    public static OOPExpectedException none() {
        return new OOPExpectedExceptionImpl(null);
    }
}
