package com.francescofiore.awesomepizza.exception;

import com.francescofiore.awesomepizza.util.order.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(Long orderId, OrderStatus currentState, OrderStatus nextState) {
        super(String.format("Order %d cannot be update from state %s to %s", orderId, currentState, nextState));
    }
}

