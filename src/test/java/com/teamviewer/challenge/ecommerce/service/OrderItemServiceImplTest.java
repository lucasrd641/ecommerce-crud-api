package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.model.OrderItem;
import com.teamviewer.challenge.ecommerce.model.Product;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderItem() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(10);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(new BigDecimal(10));

        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setProduct(mockProduct);
        mockOrderItem.setQuantity(10);

        when(productRepository.findById(dto.getProductId())).thenReturn(Optional.of(mockProduct));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(mockOrderItem);

        OrderItem result = orderItemServiceImpl.createOrderItem(dto);

        assertEquals(mockOrderItem, result);
    }

    @Test
    void testUpdateOrderItem() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(5);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(new BigDecimal(10));

        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setId(1L);
        existingOrderItem.setProduct(mockProduct);
        existingOrderItem.setQuantity(3);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));
        when(productRepository.findById(dto.getProductId())).thenReturn(Optional.of(mockProduct));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(existingOrderItem);

        OrderItem result = orderItemServiceImpl.updateOrderItem(1L, dto);

        assertEquals(5, result.getQuantity().intValue());
    }

    @Test
    void testUpdateOrder_ItemNotFound() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(5);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemServiceImpl.updateOrderItem(1L, dto));
    }

    @Test
    void testDeleteOrderItem() {
        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setId(1L);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));

        orderItemServiceImpl.deleteOrderItem(1L);

        verify(orderItemRepository).delete(existingOrderItem);
    }

    @Test
    void testGetOrderItem_InvalidId() {
        when(orderItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemServiceImpl.getOrderItemById(2L));
    }

    @Test
    void testDeleteOrder_ItemInvalidId() {
        when(orderItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemServiceImpl.deleteOrderItem(2L));
    }

    @Test
    void testGetAllOrder_ItemsEmptyList() {
        when(orderItemRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderItem> result = orderItemServiceImpl.getAllOrderItems();

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateOrderItem_NonExistentProduct() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(3L);
        dto.setQuantity(10);

        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemServiceImpl.createOrderItem(dto));
    }

    @Test
    void testUpdateOrderItem_NonExistentProduct() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(3L);
        dto.setQuantity(5);

        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setId(1L);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemServiceImpl.updateOrderItem(1L, dto));
    }

    @Test
    void testUpdateOrderItem_InvalidOrderItemId() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(5);

        when(orderItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemServiceImpl.updateOrderItem(2L, dto));
    }
}
