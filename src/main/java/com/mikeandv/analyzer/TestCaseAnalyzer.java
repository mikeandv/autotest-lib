package com.mikeandv.analyzer;

import com.mikeandv.annotation.*;
import com.mikeandv.entity.SingleTest;
import com.mikeandv.entity.TestCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс описывающий состояние анализатора тестовых кейсов
 * помеченных аннторациями @SetUP, @TearDown, @ignore, @Test, @BeforeEach, @AfterEach
 * и собирает тестовый кейс
 */
public class TestCaseAnalyzer {
    private List<TestCase> testCases = new ArrayList<>();
    private List<SingleTest> setUpMethods = new ArrayList<>();
    private List<SingleTest> tearDownMethods = new ArrayList<>();
    private boolean isError;
    private String error = "";

    public String getError() {
        return error;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public boolean isError() {
        return isError;
    }

    public List<SingleTest> getSetUpMethods() {
        return setUpMethods;
    }

    public List<SingleTest> getTearDownMethods() {
        return tearDownMethods;
    }

    /**
     * Анализирует класс clazz на наличие методов
     * помеченных аннторациями @SetUP, @TearDown, @ignore, @Test, @BeforeEach, @AfterEach
     * @param clazz
     * @return
     */
    public List<TestCase> analyze(Class<?> clazz) {
        Method[] methods = clazz.getMethods();

        List<SingleTest> beforeEachMethods = new ArrayList<>();
        List<SingleTest> afterEachMethods = new ArrayList<>();
        List<SingleTest> singleTestMethods = new ArrayList<>();

        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {

                if (method.isAnnotationPresent(BeforeEach.class)) {

                    if (hasParameters(method)) {
                        writeError(BeforeEach.class, method, clazz);
                        return null; ///
                    }
                    beforeEachMethods.add(new SingleTest(clazz, method));

                } else if (method.isAnnotationPresent(Test.class)) {

                    if (hasParameters(method)) {
                        writeError(Test.class, method, clazz);
                        return null; ///
                    }
                    singleTestMethods.add(new SingleTest(clazz, method));

                } else if (method.isAnnotationPresent(AfterEach.class)) {

                    if (hasParameters(method)) {
                        writeError(AfterEach.class, method, clazz);
                        return null; ///
                    }
                    afterEachMethods.add(new SingleTest(clazz, method));
                } else if (method.isAnnotationPresent(SetUp.class)) {

                    if (hasParameters(method)) {
                        writeError(SetUp.class, method, clazz);
                        return null; ///
                    }
                    this.setUpMethods.add(new SingleTest(clazz, method));
                } else if (method.isAnnotationPresent(TearDown.class)) {

                    if (hasParameters(method)) {
                        writeError(TearDown.class, method, clazz);
                        return null; ///
                    }
                    this.tearDownMethods.add(new SingleTest(clazz, method));
                } else if (method.isAnnotationPresent(Ignore.class)) {
                    singleTestMethods.add(new SingleTest(clazz, method));
                }
            }
        }

        for (SingleTest t : singleTestMethods) {
            Test test = t.getMethod().getAnnotation(Test.class);

            //Правки по замечанию №5
            String testName = test.testName().isEmpty() ? t.getMethod().getName() : test.testName();
            TestCase testCase = new TestCase(testName);

            if (!beforeEachMethods.isEmpty()) {
                testCase.addBeforeEach(beforeEachMethods);
            }

            testCase.setSingleTest(t);

            if (!afterEachMethods.isEmpty()) {
                testCase.addAfterEach(afterEachMethods);
            }

            testCases.add(testCase);
        }

        return testCases;
    }

    /**
     * Выполняет проверку на наличе у метода параметров
     * @param method
     * @return <tt>true</tt> метод имеет параметы <tt>false</tt> метод без параметров
     */
    private static boolean hasParameters(Method method) {
        return method.getParameterCount() != 0;
    }

    /**
     * Выполняет запись и сборку сообщения об ошибке
     * @param annotation
     * @param m метод по данным которого необходимо сформировтаь сообщение
     * @param cl класс по данным которого необходимо сформировть сообщение
     */
    private void writeError(Class<? extends Annotation> annotation, Method m, Class<?> cl) {
            this.isError = true;
            this.error = "@a" + annotation.getSimpleName()
                    + "method " + m.getName()
                    + " of class " + cl.getName() +  " have parameters";
    }


}
