package com.francescofiore.awesomepizza.service.interfaces;

import com.francescofiore.awesomepizza.entity.Order;
import com.francescofiore.awesomepizza.util.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();

    Page<Order> getFilteredOrders(List<OrderStatus> status, Pageable pageable);

    Order getOrderById(Long id);

    Order createOrder(Order order);

    Order updateOrder(Long id, Order updatedOrder);

    void deleteOrder(Long id);

    Order startOrderPreparation(Long orderId);

    Order updateOrderStatus(Long orderId, OrderStatus newStatus);

}