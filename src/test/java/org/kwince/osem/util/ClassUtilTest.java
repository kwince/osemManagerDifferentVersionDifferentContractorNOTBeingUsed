package org.kwince.osem.util;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Test;

public class ClassUtilTest {
    @Test
    public void getStringValueTest() throws Exception {
        TestClass testClass = new TestClass();
        testClass.field1 = "value1";
        testClass.field2 = "value2";

        Field field1 = TestClass.class.getDeclaredField("field1");
        Field field2 = TestClass.class.getDeclaredField("field2");

        assertEquals("value1", ClassUtil.INSTANCE.getStringValue(field1, testClass));
        assertEquals("value2", ClassUtil.INSTANCE.getStringValue(field2, testClass));
    }

    @SuppressWarnings("unused")
    private class TestClass {
        String field1;
        String field2;
    }
}
