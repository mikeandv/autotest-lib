package com.mikeandv.entity;

import com.mikeandv.analyzer.GeneralRunner;

import java.util.List;

/**
 * Класс класс описывающий тестовый надор
 */
public class TestSuit implements GeneralRunner {
    private Statuses status;
    private String name;
    private int leadTime;
    private List<SingleTest> setUps;
    private List<SingleTest> tearDown;
    private List<TestCase> testCases;
    private String message = "";

    public TestSuit(String name) {
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

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public List<SingleTest> getSetUps() {
        return setUps;
    }

    public void setSetUps(List<SingleTest> setUps) {
        this.setUps = setUps;
    }

    public List<SingleTest> getTearDown() {
        return tearDown;
    }

    public void setTearDown(List<SingleTest> tearDown) {
        this.tearDown = tearDown;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "\t\t" + status + "\n" );

        if (!testCases.isEmpty()) {
            testCases.forEach((t) -> sb.append(t.toString()));
        }

        sb.append(this.message);
        return sb.toString();
    }

    /**
     * Выполняет запуск единичных тестов, которые аннотированы @SetUp и @TearDown и тестовые кейсы
     */
    @Override
    public void run() {
        if (!this.setUps.isEmpty()) {
            for(SingleTest t : this.setUps) {
                if (!t.runTest()) {
                    this.message = this.message + "\t\t\t" + "@SetUp method " + t.getMethod().getName() + t.getMessage() + "\n";
                }
            }
        }

        for (TestCase tc : testCases) {
            tc.run();
        }

        if (!this.tearDown.isEmpty()) {
            for(SingleTest t : this.tearDown) {
                if (!t.runTest()) {
                    this.message = this.message + "\t\t\t" + "@TearDown method " + t.getMethod().getName() + t.getMessage() + "\n";
                }
            }
        }

        if (this.setUps
                .stream()
                .anyMatch(l -> l.getStatus().equals(Statuses.FAILED))
                || this.tearDown
                .stream()
                .anyMatch(l -> l.getStatus().equals(Statuses.FAILED))
                || this.testCases
                .stream()
                .anyMatch(l -> l.getStatus().equals(Statuses.FAILED))
        ) {
            this.status = Statuses.FAILED;
        } else {
            this.status = Statuses.PASSED;
        }
    }
}
