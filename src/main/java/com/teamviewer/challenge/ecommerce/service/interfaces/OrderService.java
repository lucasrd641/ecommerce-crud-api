package com.teamviewer.challenge.ecommerce.service.interfaces;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(OrderDto order);
    Order updateOrder(Long id, OrderDto orderDto);
    void deleteOrder(Long id);
}
