package OOP.Tests;
import static org.junit.Assert.*;

import org.junit.Test;

import OOP.Solution.*;


public class BackupTests {

    @Test
    public void test() {
        OOPTestSummary result = OOPUnitCore.runClass(TestClass.class);
        // Expecting 1 Error from Mtehod1, which throws an exception in it's "Before" method
        assertEquals(1, result.getNumErrors());
        // Expecting 1 success from Method2
        assertEquals(1, result.getNumSuccesses());
    }
}

@OOPTestClass(OOPTestClass.OOPTestClassType.ORDERED)
class TestClass {
    private Field1 field1;
    private Field2 field2;
    private Field3 field3;

    private TestClass() {
        // initializing fields1-3 to "initialized"
        field1 = new Field1();
        field2 = new Field2("init", "ialized");
        field3 = new Field3("init", "ialized");
        field1.val = "initialized";
    }

    @OOPBefore({"Method1"})
    public void ruinThisObject() throws Exception {
        field1.val = "RUINED";
        field2.val = "RUINED";
        field3.val = "RUINED";
        throw new Exception();
    }

    @OOPTest(order = 1)
    public void Method1() {
        // not doing anything, but its "Before" method is supposed to ruin the original values of fields1-3
    }

    @OOPTest(order = 2)
    public void Method2() {
        // Executing Method1's before method has changed this object's state and has thrown an exception,
        // let's see if this object was backed-up and restored correctly.
        // Expecting fields 1+2 to be restored after being ruined by ruinThisObject()
        OOPUnitCore.assertEquals("initialized", field1.val);
        OOPUnitCore.assertEquals("initialized", field2.val);
        OOPUnitCore.assertEquals(true, field1.cloned);
        OOPUnitCore.assertEquals(true, field2.copied);

        // But field 3 doesn't have a clone() method or copy c'tor, so he was backed-up by reference
        OOPUnitCore.assertEquals("RUINED", field3.val);
    }
}

// Field1 supports clone()
class Field1 implements Cloneable {
    public String val;
    public boolean cloned = false;

    protected Field1 clone() {
        Field1 newField = new Field1();
        newField.val = val;
        newField.cloned = true;
        return newField;
    }

    public Field1() {
    }

    public Field1(Field1 otherField) {
        // copy constructor, not supposed to be called because Field1 is supposed to clone itself
        val = otherField.val = "Tried to call Field1 copy c'tor, but it should be tried to be cloned first";
        throw new RuntimeException(val);
    }
}

// Field2 has a copy constructor
class Field2 {
    public String val;
    public boolean copied = false;

    private Field2(Field2 otherField) {
        val = otherField.val;
        copied = true;
    }

    public Field2(String param1, String param2) {
        val = param1 + param2;
    }
}

// Field3 has non of them
class Field3 {
    public String val;

    public Field3(String param1, String param2) {
        val = param1 + param2;
    }
}