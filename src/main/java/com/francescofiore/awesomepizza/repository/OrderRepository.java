package com.francescofiore.awesomepizza.repository;

import com.francescofiore.awesomepizza.entity.Order;
import com.francescofiore.awesomepizza.util.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT o
        FROM Order o
        WHERE (:status IS NULL OR o.status in :status)
        """)
    Page<Order> findByStatusIn(List<OrderStatus> status, Pageable pageable);

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByStatus(OrderStatus status);
}
