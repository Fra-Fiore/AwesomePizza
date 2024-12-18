package com.francescofiore.awesomepizza.controller;

import com.francescofiore.awesomepizza.dto.response.OrderResponseDTO;
import com.francescofiore.awesomepizza.entity.Order;
import com.francescofiore.awesomepizza.exception.InvalidOrderStateException;
import com.francescofiore.awesomepizza.exception.OrderNotFoundException;
import com.francescofiore.awesomepizza.mapper.OrderMapper;
import com.francescofiore.awesomepizza.service.interfaces.OrderService;
import com.francescofiore.awesomepizza.util.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @Test
    void testGetAllOrders() throws Exception {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        OrderResponseDTO orderResponse = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.CREATED.name());

        Mockito.when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value(OrderStatus.CREATED.toString()));
    }

    @Test
    void testGetOrderById() throws Exception {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        OrderResponseDTO orderResponse = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.CREATED.name());

        Mockito.when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        Mockito.when(orderService.getOrderById(Mockito.any())).thenReturn(order);

        mockMvc.perform(get("/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(OrderStatus.CREATED.toString()));
    }

    @Test
    void testGetOrderStatusByCode() throws Exception {
        var orderCode = "ORD-" + Instant.now().toEpochMilli();

        Mockito.when(orderService.getOrderStatusByCode(orderCode)).thenReturn(OrderStatus.CREATED.name());

        mockMvc.perform(get("/orders/{orderCode}/status", orderCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(OrderStatus.CREATED.name()));
    }

    @Test
    void testGetOrderByCode_KO() throws Exception {
        var orderCode = "ORD-" + Instant.now().toEpochMilli();

        Mockito.when(orderService.getOrderStatusByCode(orderCode)).thenThrow(new OrderNotFoundException(orderCode));

        mockMvc.perform(get("/orders/{orderCode}/status", orderCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format("Order with code %s not found", orderCode)));
    }

    @Test
    void testStartOrderPreparation_OK() throws Exception {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.IN_PREPARATION);

        OrderResponseDTO orderResponse = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.IN_PREPARATION.name());

        Mockito.when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        Mockito.when(orderService.startOrderPreparation(1L)).thenReturn(order);

        mockMvc.perform(post("/orders/{id}/start-preparation", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(OrderStatus.IN_PREPARATION.toString()));
    }

    @Test
    void testStartOrderPreparation_KO() throws Exception {
        Mockito.when(orderService.startOrderPreparation(1L))
                .thenThrow(new InvalidOrderStateException(1L, OrderStatus.IN_PREPARATION, OrderStatus.IN_PREPARATION));

        mockMvc.perform(post("/orders/{id}/start-preparation", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Order 1 cannot be update from state IN_PREPARATION to IN_PREPARATION"));
    }

    @Test
    void testMarkOrderAsReady() throws Exception {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.READY);

        OrderResponseDTO orderResponse = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.READY.name());

        Mockito.when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        Mockito.when(orderService.updateOrderStatus(1L, OrderStatus.READY)).thenReturn(order);

        mockMvc.perform(post("/orders/{id}/mark-ready", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(OrderStatus.READY.toString()));
    }

    @Test
    void testMarkOrderAsDelivered() throws Exception {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.DELIVERED);

        OrderResponseDTO orderResponse = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.DELIVERED.name());

        Mockito.when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        Mockito.when(orderService.updateOrderStatus(1L, OrderStatus.DELIVERED)).thenReturn(order);

        mockMvc.perform(post("/orders/{id}/mark-delivered", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERED.toString()));
    }

    @Test
    void testCreateOrder() throws Exception {
        Order order = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        OrderResponseDTO orderResponse = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.CREATED.name())
                .setPizzas(List.of(
                        new OrderResponseDTO.OrderPizzaResponseDTO().setPizzaName("Margherita").setQuantity(2),
                        new OrderResponseDTO.OrderPizzaResponseDTO().setPizzaName("Diavola").setQuantity(1).setNotes("note")
                ));

        Mockito.when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        Mockito.when(orderService.createOrder(Mockito.any())).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pizzas": [
                                    {
                                      "pizzaId": 1,
                                      "quantity": 2
                                    },
                                    {
                                      "pizzaId": 2,
                                      "quantity": 1,
                                      "notes": "note"
                                    }
                                  ]
                                }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.pizzas[0].pizzaName").value("Margherita"))
                .andExpect(jsonPath("$.pizzas[0].quantity").value(2))
                .andExpect(jsonPath("$.pizzas[0].notes").isEmpty())
                .andExpect(jsonPath("$.pizzas[1].pizzaName").value("Diavola"))
                .andExpect(jsonPath("$.pizzas[1].quantity").value(1))
                .andExpect(jsonPath("$.pizzas[1].notes").value("note"));
    }

    @Test
    void testFilteredListOrders() throws Exception {
        Order order1 = new Order()
                .setOrderId(1L)
                .setStatus(OrderStatus.CREATED);

        Order order2 = new Order()
                .setOrderId(2L)
                .setStatus(OrderStatus.READY);

        OrderResponseDTO orderResponse1 = new OrderResponseDTO()
                .setId(1L)
                .setStatus(OrderStatus.CREATED.name());

        OrderResponseDTO orderResponse2 = new OrderResponseDTO()
                .setId(2L)
                .setStatus(OrderStatus.READY.name());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        Mockito.when(orderService.getFilteredOrders(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(ordersPage);

        Mockito.when(orderMapper.toResponse(order1)).thenReturn(orderResponse1);
        Mockito.when(orderMapper.toResponse(order2)).thenReturn(orderResponse2);

        mockMvc.perform(get("/orders/search")
                        .param("status", "CREATED", "READY")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("CREATED"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].status").value("READY"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void testOrderNotFound() throws Exception {
        Mockito.when(orderService.getOrderById(1L))
                .thenThrow(new OrderNotFoundException(1L));

        mockMvc.perform(get("/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order with id 1 not found"));
    }

}

