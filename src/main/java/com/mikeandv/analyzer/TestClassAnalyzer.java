package com.mikeandv.analyzer;

import com.mikeandv.annotation.TestClass;
import com.mikeandv.entity.TestCase;
import com.mikeandv.entity.TestSuit;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Описывает состояние анализатора аннотаций @TestCase
 */
public class TestClassAnalyzer {
    private boolean isError;
    private String error = "";


    public String getError() {
        return error;
    }

    public boolean isError() {
        return isError;
    }

    /**
     * Анализирует jar-файлы переданные в спике jarURL и выполняет первичные проверки тестовых наборов
     * если в переданных jar-файлах не найдено не одного класса помеченного аннтотацией @TestClass анализатор
     * завершает работу с сообщением "No test cases".
     * если класс помеченный аннотацией @TestClass нет публичного конструктора по мумолчанию, анализатор завершает
     * работу с сообщением "<i>clazz.getName()</i> class does not have a public default constructor"
     * @param jarURL
     * @return возвращает список тестовых наборов
     */
    public List<TestSuit> analyze(URL[] jarURL){

        URLClassLoader loader = new URLClassLoader(jarURL);
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(jarURL)
                        .addClassLoaders(loader)
                        .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false)));
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(TestClass.class);


        if (classes.isEmpty()) {
            this.isError = true;
            this.error = "No test cases";
            return null;
        }

        List<TestSuit> testSuits = new ArrayList<>();

        for (Class clazz : classes) {

            TestClass annotation = (TestClass) clazz.getAnnotation(TestClass.class);
            TestSuit testSuit = new TestSuit(annotation.testSuit());

            Constructor[] constructors =  clazz.getConstructors();
            boolean hasPublicConstructor = false;

            for (int i = 0; i < constructors.length; i++) {
                if (Modifier.isPublic(constructors[i].getModifiers()) && constructors[i].getParameterCount() == 0) {
                    hasPublicConstructor = true;
                }
            }
            if (!hasPublicConstructor) {
                this.isError = true;
                this.error = clazz.getName() + " class does not have a public default constructor";
                return null;
            }


            //вызов для обратобки тест-кейсов
            TestCaseAnalyzer ga = new TestCaseAnalyzer();
            List<TestCase> testCases = ga.analyze(clazz);

            if (ga.isError()) {
                this.isError = true;
                this.error = ga.getError();
                return null;
            }

            testSuit.setTestCases(testCases);
            testSuit.setSetUps(ga.getSetUpMethods());
            testSuit.setTearDown(ga.getTearDownMethods());

            testSuits.add(testSuit);
        }

        return testSuits;
    }
}
