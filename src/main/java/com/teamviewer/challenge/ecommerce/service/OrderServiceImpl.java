package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.dto.ProductDto;
import com.teamviewer.challenge.ecommerce.entity.Product;
import com.teamviewer.challenge.ecommerce.exception.DuplicateElementException;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.entity.Order;
import com.teamviewer.challenge.ecommerce.entity.OrderItem;
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
        if (!isValid(orderDto)) throw new IllegalArgumentException("OrderDto is not valid");
        if (orderRepository.existsByCustomerNameIgnoreCase(orderDto.getCustomerName())) {
            throw new IllegalArgumentException("Order with the same customer name already exists.");
        }
        Order order = new Order();
        return processOrder(orderDto, order);
    }

    @Override
    public Order updateOrder(Long id, OrderDto orderDto) {
        if (!isValid(orderDto)) throw new IllegalArgumentException("OrderDto is not valid");
        Order existingOrderWithSameCustomerName = orderRepository.findByCustomerNameIgnoreCase(orderDto.getCustomerName());

        if (existingOrderWithSameCustomerName != null && !existingOrderWithSameCustomerName.getId().equals(id)) {
            throw new DuplicateElementException("This customer already has an order in their name");
        }
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
        orderItems.forEach(orderItem -> {
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
        });

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

    boolean isValid(OrderDto orderDto) {
        if (orderDto == null) return false;
        if (orderDto.getOrderItemIds() == null) return false;
        if (orderDto.getOrderItemIds().isEmpty()) return false;
        if (orderDto.getCustomerName().isBlank()) return false;
        return !orderDto.getAddress().isBlank();
    }

}
