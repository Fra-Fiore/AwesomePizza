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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Ordini", description = "API per la gestione degli ordini")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Visualizza la lista degli ordini", description = "Restituisce tutti gli ordini")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperata con successo")
    })
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Visualizza la lista degli ordini filtrati", description = "Restituisce tutti gli ordini filtrati")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperata con successo")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<OrderResponseDTO>> filteredOrdersList(
            @ParameterObject @PageableDefault() Pageable pageable,
            @RequestParam(required = false) List<OrderStatus> status) {
        Page<OrderResponseDTO> orders = orderService.getFilteredOrders(status, pageable)
                .map(orderMapper::toResponse);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Visualizza i dettagli di un ordine", description = "Restituisce i dettagli di un ordine dato il suo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordine trovato con successo"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.getOrderById(id)));
    }

    @Operation(summary = "Visualizza lo stato di un ordine", description = "Restituisce lo stato di un ordine dato il suo codice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordine trovato con successo"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @GetMapping("/{orderCode}/status")
    public ResponseEntity<String> getOrderById(@PathVariable @NotNull String orderCode) {
        return ResponseEntity.ok(orderService.getOrderStatusByCode(orderCode));
    }

    @Operation(summary = "Crea un nuovo ordine", description = "Permette di creare un nuovo ordine")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ordine creato con successo"),
            @ApiResponse(responseCode = "400", description = "Input con sintassi non valida")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.createOrder(orderMapper.toEntity(request))));
    }

    @Operation(summary = "Prendi in carico un ordine", description = "Aggiorna lo stato di un ordine da 'CREATED' a 'IN_PREPARATION'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordine aggiornato con successo"),
            @ApiResponse(responseCode = "409", description = "Ordine non nello stato corretto"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @PostMapping("/{id}/start-preparation")
    public ResponseEntity<OrderResponseDTO> startOrderPreparation(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.startOrderPreparation(id)));
    }

    @Operation(summary = "Segna un ordine come pronto", description = "Aggiorna lo stato di un ordine da 'IN_PREPARATION' a 'READY'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordine segnato come pronto con successo"),
            @ApiResponse(responseCode = "409", description = "Ordine non nello stato corretto"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @PostMapping("/{id}/mark-ready")
    public ResponseEntity<OrderResponseDTO> markOrderAsReady(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateOrderStatus(id, OrderStatus.READY)));
    }

    @Operation(summary = "Segna un ordine come consegnato", description = "Aggiorna lo stato di un ordine da 'READY' a 'DELIVERED'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordine consegnato con successo"),
            @ApiResponse(responseCode = "409", description = "Ordine non nello stato corretto"),
            @ApiResponse(responseCode = "404", description = "Ordine non trovato")
    })
    @PostMapping("/{id}/mark-delivered")
    public ResponseEntity<OrderResponseDTO> markOrderAsDelivered(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateOrderStatus(id, OrderStatus.DELIVERED)));
    }
}