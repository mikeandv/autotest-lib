package com.mikeandv.entity;

public class ObjectAssertException extends RuntimeException {
    public ObjectAssertException(String assertMessage) {
        super(assertMessage);
    }
}
