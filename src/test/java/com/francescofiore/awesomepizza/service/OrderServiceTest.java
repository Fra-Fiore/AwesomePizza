package com.francescofiore.awesomepizza.service;

import com.francescofiore.awesomepizza.entity.Order;
import com.francescofiore.awesomepizza.entity.OrderPizza;
import com.francescofiore.awesomepizza.entity.Pizza;
import com.francescofiore.awesomepizza.exception.InvalidOrderStateException;
import com.francescofiore.awesomepizza.exception.OrderAlreadyInPreparationException;
import com.francescofiore.awesomepizza.exception.OrderNotFoundException;
import com.francescofiore.awesomepizza.repository.OrderRepository;
import com.francescofiore.awesomepizza.service.impl.OrderServiceImpl;
import com.francescofiore.awesomepizza.util.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getAllOrders() {
        Order order1 = new Order().setOrderId(1L);
        Order order2 = new Order().setOrderId(2L);

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_ExistingId() {
        Order order = new Order().setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getOrderId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NonExistingId() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(1L));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderStatusByCode_ExistingCode() {
        var orderCode = "ORD-" + Instant.now().toEpochMilli();
        Order order = new Order().setOrderId(1L).setOrderCode(orderCode).setStatus(OrderStatus.CREATED);

        when(orderRepository.findByOrderCode(orderCode)).thenReturn(Optional.of(order));

        String foundOrder = orderService.getOrderStatusByCode(orderCode);

        assertNotNull(foundOrder);
        assertEquals(OrderStatus.CREATED.name(), foundOrder);
        verify(orderRepository, times(1)).findByOrderCode(orderCode);
    }

    @Test
    void getOrderStatusByCode_NonExistingCode() {
        var orderCode = "ORD-" + Instant.now().toEpochMilli();
        when(orderRepository.findByOrderCode(orderCode)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderStatusByCode(orderCode));
        verify(orderRepository, times(1)).findByOrderCode(orderCode);
    }

    @Test
    void createOrder() {
        Order order = new Order().setStatus(OrderStatus.CREATED);
        OrderPizza orderPizza = new OrderPizza().setOrder(order).setPizza(new Pizza().setPizzaId(1L).setPrice(5D));
        order.setPizzas(List.of(orderPizza));

        when(orderRepository.save(order)).thenReturn(order.setOrderId(1L));

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(OrderStatus.CREATED, createdOrder.getStatus());
        assertEquals(1L, createdOrder.getOrderId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatus_ValidTransition() {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.IN_PREPARATION);

        assertEquals(OrderStatus.IN_PREPARATION, updatedOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatus_InvalidTransition() {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class, () ->
                orderService.updateOrderStatus(1L, OrderStatus.READY));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getFilteredOrders() {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findByStatusIn(List.of(OrderStatus.CREATED), pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.getFilteredOrders(List.of(OrderStatus.CREATED), pageable);

        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findByStatusIn(List.of(OrderStatus.CREATED), pageable);
    }

    @Test
    void startOrderPreparation_OrderAlreadyInPreparation() {
        when(orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)).thenReturn(true);

        assertThrows(OrderAlreadyInPreparationException.class, () ->
                orderService.startOrderPreparation(1L));

        verify(orderRepository, never()).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void startOrderPreparation_OK() {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        when(orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order updatedOrder = orderService.startOrderPreparation(1L);

        assertEquals(OrderStatus.IN_PREPARATION, updatedOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
    }
}