package org.coding.reactive.rxjava.bestprice.service;

/**
 * Exception to throw by certain find methods in case of no result. Should not be "checked" to avoid problems with lambda expressions.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
