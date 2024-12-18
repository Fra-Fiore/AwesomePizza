package com.francescofiore.awesomepizza.service.impl;

import com.francescofiore.awesomepizza.entity.Order;
import com.francescofiore.awesomepizza.exception.InvalidOrderStateException;
import com.francescofiore.awesomepizza.exception.OrderAlreadyInPreparationException;
import com.francescofiore.awesomepizza.exception.OrderNotFoundException;
import com.francescofiore.awesomepizza.repository.OrderRepository;
import com.francescofiore.awesomepizza.service.interfaces.OrderService;
import com.francescofiore.awesomepizza.util.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.francescofiore.awesomepizza.util.order.OrderUtils.isValidStatusTransition;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getFilteredOrders(List<OrderStatus> status, Pageable pageable) {
        return orderRepository.findByStatusIn(status, pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public String getOrderStatusByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(o -> o.getStatus().name())
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED)
                .setTotalPrice(calculateTotalPrice(order))
                .setOrderCode("ORD-" + Instant.now().toEpochMilli());
        order.getPizzas().forEach(orderPizza -> orderPizza.setOrder(order));
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order order = getOrderById(id)
                .setStatus(updatedOrder.getStatus())
                .setPizzas(updatedOrder.getPizzas())
                .setTotalPrice(calculateTotalPrice(updatedOrder));
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public Order startOrderPreparation(Long orderId) {
        if (orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)) {
            log.warn("There is already an order in IN_PREPARATION status");
            throw new OrderAlreadyInPreparationException();
        }
        return updateOrderStatus(orderId, OrderStatus.IN_PREPARATION);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);

        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            log.warn("Invalid status transition: {} -> {}", order.getStatus(), newStatus);
            throw new InvalidOrderStateException(orderId, order.getStatus(), newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private double calculateTotalPrice(Order order) {
        return order.getPizzas().stream()
                .mapToDouble(op -> op.getPizza().getPrice() * op.getQuantity())
                .sum();
    }
}
