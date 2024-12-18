package com.francescofiore.awesomepizza.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super(String.format("Order with id %d not found", orderId));
    }

    public OrderNotFoundException(String orderCode) {
        super(String.format("Order with code %s not found", orderCode));
    }
}

