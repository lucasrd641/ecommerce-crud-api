package com.teamviewer.challenge.ecommerce.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.entity.OrderItem;
import com.teamviewer.challenge.ecommerce.entity.Product;
import com.teamviewer.challenge.ecommerce.exception.InsufficientStockException;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.OrderRepository;
import com.teamviewer.challenge.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class OrderItemServiceImplTest {

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrderItemByIdSuccess() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(new OrderItem()));
        OrderItem item = orderItemService.getOrderItemById(1L);
        assertNotNull(item);
    }

    @Test
    public void testGetOrderItemByIdNotFound() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderItemService.getOrderItemById(1L));
    }

    @Test
    public void testCreateOrderItemSuccess() {
        Product product = new Product();
        product.setUnitsInStock(10);
        product.setPrice(new BigDecimal("10.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(5);

        OrderItem response = orderItemService.createOrderItem(dto);
        assertEquals(new BigDecimal("50.00"), response.getOrderItemPrice());
    }

    @Test
    public void testCreateOrderItemInsufficientStock() {
        Product product = new Product();
        product.setUnitsInStock(3);
        product.setPrice(new BigDecimal("10.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(5);

        assertThrows(InsufficientStockException.class, () -> orderItemService.createOrderItem(dto));
    }

    @Test
    public void testUpdateOrderItemIncreaseQuantity() {
        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(10);
        product.setPrice(new BigDecimal("10.00"));

        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setQuantity(4);
        existingOrderItem.setProduct(product);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));

        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(6);

        OrderItem updatedOrderItem = orderItemService.updateOrderItem(1L, dto);
        assertEquals(6, updatedOrderItem.getQuantity().intValue());
        assertEquals(new BigDecimal("60.00"), updatedOrderItem.getOrderItemPrice());
    }

    @Test
    public void testUpdateOrderItemDecreaseQuantity() {
        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(10);
        product.setPrice(new BigDecimal("10.00"));

        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setProduct(product);
        existingOrderItem.setQuantity(6);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));

        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(4);

        OrderItem updatedOrderItem = orderItemService.updateOrderItem(1L, dto);
        assertEquals(4, updatedOrderItem.getQuantity().intValue());
        assertEquals(new BigDecimal("40.00"), updatedOrderItem.getOrderItemPrice());
    }

    @Test
    public void testDeleteOrderItemWithNonExistingProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(10);
        product.setPrice(new BigDecimal("10.00"));

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(5);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        orderItemService.deleteOrderItem(1L);
    }

    @Test
    public void testUpdateOrderItemInsufficientStock() {
        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(3);
        product.setPrice(new BigDecimal("10.00"));

        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setProduct(product);
        existingOrderItem.setQuantity(2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));

        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(7);

        assertThrows(InsufficientStockException.class, () -> orderItemService.updateOrderItem(1L, dto));
    }

    @Test
    public void testGetAllOrderItems() {
        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(3);
        product.setPrice(new BigDecimal("10.00"));
        product.setName("Test");

        OrderItem item1 = new OrderItem();
        item1.setProduct(product);
        item1.setQuantity(3);

        OrderItem item2 = new OrderItem();
        item2.setProduct(product);
        item2.setQuantity(2);

        List<OrderItem> orderItems = Arrays.asList(item1, item2);
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        List<OrderItem> result = orderItemService.getAllOrderItems();
        assertEquals(2, result.size());
        assertEquals("Test", result.get(0).getProduct().getName());
    }

    @Test
    public void testUpdateOrderItemWithIncreasedQuantity() {
        Product productOld = new Product();
        productOld.setId(1L);
        productOld.setUnitsInStock(3);
        productOld.setPrice(new BigDecimal("10.00"));

        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(5);

        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setQuantity(3);
        existingOrderItem.setProduct(productOld);

        Product product = new Product();
        product.setId(1L);
        product.setName("TestItem");
        product.setUnitsInStock(8);
        product.setPrice(new BigDecimal("10.00"));

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(existingOrderItem));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderItem updatedOrderItem = orderItemService.updateOrderItem(1L, dto);
        assertEquals(5, updatedOrderItem.getQuantity().intValue());
        assertEquals(6, product.getUnitsInStock().intValue());
    }


    @Test
    public void testGetOrderItemByIdFound() {
        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(3);
        product.setPrice(new BigDecimal("10.00"));
        product.setName("Test");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(3);

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(item));

        OrderItem result = orderItemService.getOrderItemById(1L);
        assertEquals("Test", result.getProduct().getName());
    }

    @Test
    public void testCreateOrderItemWithNegativeQuantity() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(-3);

        Product product = new Product();
        product.setId(1L);
        product.setUnitsInStock(10);
        product.setPrice(new BigDecimal("10.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> orderItemService.createOrderItem(dto));
    }
}

