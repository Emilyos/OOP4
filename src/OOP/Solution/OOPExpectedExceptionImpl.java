package OOP.Solution;

import OOP.Provided.OOPExpectedException;

public class OOPExpectedExceptionImpl implements OOPExpectedException {

    private Class<? extends Exception> expected;
    private String message = "";

    private OOPExpectedExceptionImpl(Class<? extends Exception> expected) {
        this.expected = expected;
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
        message = msg;
        return this;
    }

    @Override
    public boolean assertExpected(Exception e) {
        if (expected == null) return false;
        return e.getClass() == expected && e.getMessage() == message;
    }

    public static OOPExpectedException none() {
        return new OOPExpectedExceptionImpl(null);
    }
}
