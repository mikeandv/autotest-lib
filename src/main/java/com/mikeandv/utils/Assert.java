package com.mikeandv.utils;

import com.mikeandv.entity.ArrayAssertException;
import com.mikeandv.entity.ObjectAssertException;

import java.util.Arrays;

/**
 *  Утилитарный класс для проверки утверждений
 */
public class Assert {
    /**
     * Сравнивает два объекта
     * @param expected
     * @param actual
     * @throws ObjectAssertException случае неравенства переданных объектов
     */
    public void assertEquals(Object expected, Object actual) throws ObjectAssertException {
        if (!actual.equals(expected)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\t\t\t").append("expected: ").append(expected.toString()).append("\n");
            sb.append("\t\t\t").append("actual: ").append(actual.toString());
            throw new ObjectAssertException(sb.toString());
        }
    }

    /**
     * Сравнивает два массива
     * @param expected
     * @param actual
     * @throws ArrayAssertException в случае неравенства переданных массивов объектов
     */
    public void arrayAssertEquals(Object[] expected, Object[] actual) throws ArrayAssertException {
        StringBuilder sb = new StringBuilder();

        if (actual.length != expected.length) {
            sb.append("\t\t\t").append("expected: ").append("expected array length = ").append(expected.length).append("\n");
            sb.append("\t\t\t").append("actual: ").append("actual array length = ").append(actual.length);
            throw new ArrayAssertException(sb.toString());
        }
        for (int i = 0; i < expected.length; i++) {
            if (!actual[i].equals(expected[i])) {
                sb.append("\t\t\t").append("expected: ").append(Arrays.toString(expected)).append("\n");
                sb.append("\t\t\t").append("actual: ").append(Arrays.toString(actual));
                throw new ArrayAssertException(sb.toString());
            }
        }
    }
}
