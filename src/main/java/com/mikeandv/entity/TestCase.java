package com.mikeandv.entity;

import com.mikeandv.annotation.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для описывающий тестовый кейс
 */
public class TestCase implements GeneralRunner {
    private Statuses status;
    private String name;
    private SingleTest singleTest;
    private List<SingleTest> beforeEach = new ArrayList<>();
    private List<SingleTest> afterEach = new ArrayList<>();
    private int leadTime;
    private String message = "";

    public TestCase(String name) {
        this.name = name;
    }

    public Statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(int leadTime) {
        this.leadTime = leadTime;
    }

    public SingleTest getSingleTest() {
        return singleTest;
    }

    public void setSingleTest(SingleTest singleTest) {
        this.singleTest = singleTest;
    }

    public List<SingleTest> getBeforeEach() {
        return beforeEach;
    }

    public void addBeforeEach(List<SingleTest> methodList) {
        this.beforeEach.addAll(methodList);
    }

    public List<SingleTest> getAfterEach() {
        return afterEach;
    }

    public void addAfterEach(List<SingleTest> methodList) {
        this.afterEach.addAll(methodList);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {

        return "\t" + name + "\t" + status + "\n" + message;
    }

    /**
     * Выполняет запуск единичных тестов, которые аннотированы @BeforeEach и @AfterEach и единичный тест в рамках тестового кейса
     */
    public void run(Object obj) {

        //правки по замечанию №4
        if (this.singleTest.getMethod().isAnnotationPresent(Ignore.class)) {
            this.singleTest.runTest(obj);
            this.status = Statuses.IGNORE;
            return;
        } else {

            if (!this.beforeEach.isEmpty()) {
                for (SingleTest t : this.beforeEach) {
                    if (!t.runTest(obj)) {
                        this.message = "\t\t\t" + "@BeforeEach method " + t.getMethod().getName() + t.getMessage() + "\n";
                    }
                }
            }
            this.singleTest.runTest(obj);
            this.message = (this.singleTest.getMessage().isEmpty()) ? this.message : this.message + "\t\t\t" + this.singleTest.getMessage() + "\n";


        if (!this.afterEach.isEmpty()) {
            for(SingleTest t : this.afterEach) {
                if(!t.runTest()) {
                    this.message = this.message + "\t\t\t" + "@AfterEach method " + t.getMethod().getName() + t.getMessage() + "\n";
                }
            }
        }

        if (this.singleTest.getStatus().equals(Statuses.IGNORE)) {

        if (this.beforeEach
                .stream()
                .anyMatch(l -> l.getStatus().equals(Statuses.FAILED))
            || this.afterEach
                .stream()
                .anyMatch(l -> l.getStatus().equals(Statuses.FAILED))
            || this.singleTest.getStatus().equals(Statuses.FAILED)
        ) {
            this.status = Statuses.FAILED;
        } else {
            this.status = Statuses.PASSED;
        }
    }
}

