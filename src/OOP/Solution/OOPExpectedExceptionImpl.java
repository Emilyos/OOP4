package OOP.Solution;

import OOP.Provided.OOPExpectedException;

public class OOPExpectedExceptionImpl implements OOPExpectedException {
    @Override
    public Class<? extends Exception> getExpectedException() {
        return null;
    }

    @Override
    public OOPExpectedException expect(Class<? extends Exception> expected) {
        return null;
    }

    @Override
    public OOPExpectedException expectMessage(String msg) {
        return null;
    }

    @Override
    public boolean assertExpected(Exception e) {
        return false;
    }

    public static OOPExpectedException none(){
        return null;
    }
}
