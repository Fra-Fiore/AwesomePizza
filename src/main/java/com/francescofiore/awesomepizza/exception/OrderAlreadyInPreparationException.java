package com.francescofiore.awesomepizza.exception;

public class OrderAlreadyInPreparationException extends RuntimeException {
    public OrderAlreadyInPreparationException() {
        super("There is already an order in IN_PREPARATION status");
    }
}

