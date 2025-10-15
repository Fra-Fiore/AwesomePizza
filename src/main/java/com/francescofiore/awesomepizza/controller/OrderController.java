package com.francescofiore.awesomepizza.controller;

import com.francescofiore.awesomepizza.dto.request.OrderRequestDTO;
import com.francescofiore.awesomepizza.dto.response.OrderResponseDTO;
import com.francescofiore.awesomepizza.mapper.OrderMapper;
import com.francescofiore.awesomepizza.service.interfaces.OrderService;
import com.francescofiore.awesomepizza.util.order.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders", description = "API for managing customer orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "List every order", description = "Returns every order stored in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List fetched successfully")
    })
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Search orders by status", description = "Returns the orders matching the provided status filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List fetched successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<OrderResponseDTO>> filteredOrdersList(
            @ParameterObject @PageableDefault() Pageable pageable,
            @RequestParam(required = false) List<OrderStatus> status) {
        Page<OrderResponseDTO> orders = orderService.getFilteredOrders(status, pageable)
                .map(orderMapper::toResponse);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Retrieve order details", description = "Returns the details of an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.getOrderById(id)));
    }

    @Operation(summary = "Check the status of an order", description = "Returns the status of an order by its public code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderCode}/status")
    public ResponseEntity<String> getOrderStatusByCode(@PathVariable @NotNull String orderCode) {
        return ResponseEntity.ok(orderService.getOrderStatusByCode(orderCode));
    }

    @Operation(summary = "Create a new order", description = "Persists a new order and returns its details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderMapper.toResponse(orderService.createOrder(orderMapper.toEntity(request)));
        return ResponseEntity.created(URI.create("/orders/" + response.getId())).body(response);
    }

    @Operation(summary = "Start preparing an order", description = "Changes the status from 'CREATED' to 'IN_PREPARATION'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "409", description = "Order is not in the expected state"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/start-preparation")
    public ResponseEntity<OrderResponseDTO> startOrderPreparation(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.startOrderPreparation(id)));
    }

    @Operation(summary = "Mark an order as ready", description = "Changes the status from 'IN_PREPARATION' to 'READY'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as ready successfully"),
            @ApiResponse(responseCode = "409", description = "Order is not in the expected state"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/mark-ready")
    public ResponseEntity<OrderResponseDTO> markOrderAsReady(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateOrderStatus(id, OrderStatus.READY)));
    }

    @Operation(summary = "Mark an order as delivered", description = "Changes the status from 'READY' to 'DELIVERED'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order delivered successfully"),
            @ApiResponse(responseCode = "409", description = "Order is not in the expected state"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/mark-delivered")
    public ResponseEntity<OrderResponseDTO> markOrderAsDelivered(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateOrderStatus(id, OrderStatus.DELIVERED)));
    }
}