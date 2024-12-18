package com.francescofiore.awesomepizza.util.order;

import java.util.List;
import java.util.Map;

public class OrderUtils {

    public static boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        Map<OrderStatus, List<OrderStatus>> allowedTransitions = Map.of(
                OrderStatus.CREATED, List.of(OrderStatus.IN_PREPARATION),
                OrderStatus.IN_PREPARATION, List.of(OrderStatus.READY),
                OrderStatus.READY, List.of(OrderStatus.DELIVERED)
        );
        return allowedTransitions.getOrDefault(current, List.of()).contains(next);
    }
}
