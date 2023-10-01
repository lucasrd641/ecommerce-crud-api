package com.teamviewer.challenge.ecommerce.service.interfaces;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.model.OrderItem;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> getAllOrderItems();
    OrderItem getOrderItemById(Long id);
    OrderItem createOrderItem(OrderItemDto orderItemDto);
    OrderItem updateOrderItem(Long id, OrderItemDto orderItemDto);
    void deleteOrderItem(Long id);
}
