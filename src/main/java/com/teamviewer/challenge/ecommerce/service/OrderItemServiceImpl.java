package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.exception.InsufficientStockException;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.entity.OrderItem;
import com.teamviewer.challenge.ecommerce.entity.Product;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.ProductRepository;
import com.teamviewer.challenge.ecommerce.service.interfaces.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + id));
    }

    @Override
    public OrderItem createOrderItem(OrderItemDto orderItemDto) {
        if (!isValid(orderItemDto)) throw new IllegalArgumentException("OrderItemDto is not valid");

        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + orderItemDto.getProductId()));

        if (product.getUnitsInStock() < orderItemDto.getQuantity()) {
            throw new InsufficientStockException("Not enough units in stock for product: " + product.getName());
        }

        product.setUnitsInStock(product.getUnitsInStock() - orderItemDto.getQuantity());
        productRepository.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setOrderItemPrice(product.getPrice().multiply(new BigDecimal(orderItemDto.getQuantity())));
        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem updateOrderItem(Long id, OrderItemDto orderItemDto) {
        if (!isValid(orderItemDto)) throw new IllegalArgumentException("OrderItemDto is not valid");
        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + orderItemDto.getProductId()));

        OrderItem orderItem = getOrderItemById(id);

        int quantityDifference = orderItemDto.getQuantity() - orderItem.getQuantity();

        if (quantityDifference > 0) {
            if (product.getUnitsInStock() < quantityDifference) {
                throw new InsufficientStockException("Not enough units in stock for product: " + product.getName());
            }
            product.setUnitsInStock(product.getUnitsInStock() - quantityDifference);
        } else {
            product.setUnitsInStock(product.getUnitsInStock() + Math.abs(quantityDifference));
        }

        productRepository.save(product);
        orderItem.setProduct(product);
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setOrderItemPrice(product.getPrice().multiply(new BigDecimal(orderItemDto.getQuantity())));

        return orderItemRepository.save(orderItem);
    }


    @Override
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + id));

        Product product = productRepository.findById(orderItem.getProduct().getId()).orElse(null);

        if (product != null) {
            product.setUnitsInStock(product.getUnitsInStock() + orderItem.getQuantity());
            productRepository.save(product);
        }

        orderItemRepository.deleteById(id);
    }

    boolean isValid(OrderItemDto orderItemDto) {
        if (orderItemDto.getQuantity() < 0) return false;
        return orderItemDto.getProductId() != null;
    }

}
