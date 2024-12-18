package com.francescofiore.awesomepizza.mapper;

import com.francescofiore.awesomepizza.dto.request.OrderRequestDTO;
import com.francescofiore.awesomepizza.dto.response.OrderResponseDTO;
import com.francescofiore.awesomepizza.entity.Order;
import com.francescofiore.awesomepizza.entity.OrderPizza;
import com.francescofiore.awesomepizza.entity.Pizza;
import com.francescofiore.awesomepizza.exception.PizzaNotFoundException;
import com.francescofiore.awesomepizza.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final PizzaRepository pizzaRepository;

    /**
     * Converte un OrderRequest in un'entità Order.
     *
     * @param request Il DTO di richiesta dell'ordine
     * @return L'entità Order
     */
    public Order toEntity(OrderRequestDTO request) {
        Order order = new Order()
                .setPizzas(
                        request.getPizzas().stream()
                                .map(orderPizzaRequest -> {
                                    OrderPizza orderPizza = new OrderPizza();

                                    Pizza pizza = pizzaRepository.findById(orderPizzaRequest.getPizzaId())
                                            .orElseThrow(() -> new PizzaNotFoundException(orderPizzaRequest.getPizzaId()));

                                    return orderPizza.setPizza(pizza)
                                            .setQuantity(orderPizzaRequest.getQuantity())
                                            .setNotes(orderPizzaRequest.getNotes());
                                })
                                .toList()
                );

        // Associa l'ordine a ciascun OrderPizza
        order.getPizzas().forEach(orderPizza -> orderPizza.setOrder(order));

        return order;
    }

    /**
     * Converte un'entità Order in un OrderResponse.
     *
     * @param order L'entità Order
     * @return Il DTO di risposta dell'ordine
     */
    public OrderResponseDTO toResponse(Order order) {
        return new OrderResponseDTO()
                .setId(order.getOrderId())
                .setTimestamp(order.getTimestamp())
                .setStatus(order.getStatus().toString())
                .setTotalPrice(order.getTotalPrice())
                .setOrderCode(order.getOrderCode())
                .setPizzas(
                        order.getPizzas().stream()
                                .map(orderPizza -> new OrderResponseDTO.OrderPizzaResponseDTO()
                                        .setPizzaName(orderPizza.getPizza().getName())
                                        .setQuantity(orderPizza.getQuantity())
                                        .setNotes(orderPizza.getNotes()))
                                .collect(Collectors.toList())
                );
    }
}
