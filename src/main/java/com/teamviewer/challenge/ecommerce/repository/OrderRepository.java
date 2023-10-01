package com.teamviewer.challenge.ecommerce.repository;

import com.teamviewer.challenge.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByCustomerNameIgnoreCase(String customerName);
    Order findByCustomerNameIgnoreCase(String customerName);
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN TRUE ELSE FALSE END FROM Order o JOIN o.orderItems oi WHERE oi.id = :orderItemId")
    boolean existsByOrderItemId(@Param("orderItemId") Long orderItemId);
}
