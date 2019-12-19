package com.mikeandv.entity;

import com.mikeandv.annotation.Ignore;
import com.mikeandv.annotation.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Класс описывающий единичный тест
 */
public class SingleTest {
    private Method method;
    private Statuses status;
    private String message = "";

    public SingleTest(Method m) {
        this.method = m;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Выполняет запуск единичных тестов
     * @return возвращает <tt>true</tt>, если тестовый метод не бросил исключение,
     * тестовый метод бросил исключение которое ождалось в параметрах аннотации,
     * проверка утверждений Assert была корректна и не было брошено исключение
     * <tt>false<tt/> во всех остальных сулучаях.
     */
    public boolean runTest(Object obj) {

        boolean result;

        if (method.isAnnotationPresent(Ignore.class)) {
            this.status = Statuses.IGNORE;
            result = true;

        } else if (method.isAnnotationPresent(Test.class)) {

            com.mikeandv.annotation.Test annotation = method.getAnnotation(Test.class);
            Class expected = annotation.expected();

            try {
                methodInvoke(obj);
                //Правки по замечанию №6
                if (expected == Throwable.class) {
                    this.status = Statuses.PASSED;
                    result = true;
                } else {
                    this.status = Statuses.FAILED;
                    this.message = "Test " + annotation.testName() + " failed " + expected.getSimpleName() + " was expected to be thrown, but it was't ";
                    result = false;
                }
            } catch (Throwable e) {

                if (expected == e.getCause().getClass()) {
                    this.status = Statuses.PASSED;
                    result = true;

                } else if (e.getCause().getClass() == ObjectAssertException.class
                        || e.getCause().getClass() == ArrayAssertException.class) {
                    this.status = Statuses.FAILED;
                    this.message = "Test " + annotation.testName() + " failed " + e.getCause().getStackTrace()[0].getMethodName() + ":" + "\n" + e.getCause().getMessage();
                    result = false;

                } else if (expected != e.getCause().getClass()) {
                    this.status = Statuses.FAILED;
                    this.message = "Test " + annotation.testName() + " thrown " + e.getCause().getClass().getSimpleName();
                    result = false;

                }  else {
                    this.status = Statuses.FAILED;
                    this.message = "Test " + annotation.testName() + " expected " + expected.getSimpleName()
                            + " but thrown " + e.getCause().getClass().getSimpleName();
                    result = false;
                }
            }
        } else {

            try {
                methodInvoke(obj);
                this.status = Statuses.PASSED;
                result = true;

            } catch (Throwable e) {
                if (e.getCause().getClass() == ObjectAssertException.class
                || e.getCause().getClass() == ArrayAssertException.class) {
                    this.status = Statuses.FAILED;
                    this.message = " failed " + e.getCause().getStackTrace()[0].getMethodName() + ":" + "\n" + e.getCause().getMessage();
                    result = false;

                } else {
                    this.status = Statuses.FAILED;
                    this.message = " thrown " + e.getCause().getClass().getSimpleName();
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * Выполняет запуск через рефлексию метода с инстансом класса или значением null
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void methodInvoke(Object obj) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (Modifier.isStatic(method.getModifiers())) {
            this.method.invoke(null);
        } else {
            this.method.invoke(obj);
        }
    }
}