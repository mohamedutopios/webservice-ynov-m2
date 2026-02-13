package com.foodexpress.order.repository;

import com.foodexpress.order.entity.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public List<Order> findByCustomerId(Long customerId) {
        return list("customerId = ?1 ORDER BY createdAt DESC", customerId);
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return list("status", status);
    }

    public List<Order> findByRestaurantAndPeriod(Long restaurantId, LocalDateTime from, LocalDateTime to) {
        return list("restaurantId = ?1 and createdAt >= ?2 and createdAt <= ?3", restaurantId, from, to);
    }

    public BigDecimal totalAmountByCustomer(Long customerId) {
        return getEntityManager()
                .createQuery("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customerId = :cid AND o.status != :cancelled", BigDecimal.class)
                .setParameter("cid", customerId)
                .setParameter("cancelled", Order.OrderStatus.CANCELLED)
                .getSingleResult();
    }
}
