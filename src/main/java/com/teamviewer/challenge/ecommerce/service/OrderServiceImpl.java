package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.model.Order;
import com.teamviewer.challenge.ecommerce.model.OrderItem;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.OrderRepository;
import com.teamviewer.challenge.ecommerce.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public Order createOrder(OrderDto orderDto) {
        Order order = new Order();
        return processOrder(orderDto, order);
    }

    @Override
    public Order updateOrder(Long id, OrderDto orderDto) {
        Order order = getOrderById(id);
        return processOrder(orderDto, order);
    }

    private Order processOrder(OrderDto orderDto, Order order) {
        order.setCustomerName(orderDto.getCustomerName());
        order.setAddress(orderDto.getAddress());

        List<OrderItem> orderItems = orderItemRepository.findAllById(orderDto.getOrderItemIds());
        if (orderItems.size() != orderDto.getOrderItemIds().size()) {
            throw new ResourceNotFoundException("Some OrderItem IDs provided were not found");
        }
        orderItems.forEach(order::addOrderItem);

        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::getOrderItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}