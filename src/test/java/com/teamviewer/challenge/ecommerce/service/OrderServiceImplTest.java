package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.entity.Order;
import com.teamviewer.challenge.ecommerce.entity.OrderItem;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOrders() {
        Order mockOrder = new Order();
        mockOrder.setCustomerName("John Doe");
        mockOrder.setAddress("123 Main St");
        when(orderRepository.findAll()).thenReturn(Arrays.asList(mockOrder));

        var result = orderServiceImpl.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());
    }

    @Test
    void testGetOrderById() {
        Order mockOrder = new Order();
        mockOrder.setCustomerName("John Doe");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        var result = orderServiceImpl.getOrderById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderServiceImpl.getOrderById(1L));
    }

    @Test
    void testCreateOrder_AllOrderItemIdsExist() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L));
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Main St");

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal(10));

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal(20));

        when(orderItemRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(item1, item2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderServiceImpl.createOrder(orderDto);

        assertNotNull(result);
        assertEquals(new BigDecimal(30), result.getTotalPrice());
    }

    @Test
    void testCreateOrder_SomeOrderItemIdsDoNotExist() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L, 3L));
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Main St");

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal(10));

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal(20));

        when(orderItemRepository.findAllById(Arrays.asList(1L, 2L, 3L)))
                .thenReturn(Arrays.asList(item1, item2));

        assertThrows(ResourceNotFoundException.class, () -> orderServiceImpl.createOrder(orderDto));
    }

    @Test
    void testUpdateOrder_AllOrderItemIdsExist() {
        Order existingOrder = new Order();
        existingOrder.setCustomerName("Jane Doe");
        existingOrder.setAddress("456 Elm St");
        existingOrder.setTotalPrice(new BigDecimal(15));

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L));
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Main St");

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal(10));

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal(20));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderItemRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(item1, item2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderServiceImpl.updateOrder(1L, orderDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("123 Main St", result.getAddress());
        assertEquals(new BigDecimal(30), result.getTotalPrice());
    }

    @Test
    void testUpdateOrder_SomeOrderItemIdsDoNotExist() {
        Order existingOrder = new Order();

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L, 3L));
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Main St");

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal(10));

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal(20));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderItemRepository.findAllById(Arrays.asList(1L, 2L, 3L)))
                .thenReturn(Arrays.asList(item1, item2));

        assertThrows(ResourceNotFoundException.class, () -> orderServiceImpl.updateOrder(1L, orderDto));
    }

    @Test
    void testUpdateOrder_OrderDoesNotExist() {
        OrderDto orderDto = new OrderDto();

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderServiceImpl.updateOrder(1L, orderDto));
    }

    @Test
    void testDeleteOrder_OrderExists() {
        Order mockOrder = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        doNothing().when(orderRepository).delete(mockOrder);

        orderServiceImpl.deleteOrder(1L);

        verify(orderRepository).delete(mockOrder);
    }

    @Test
    void testDeleteOrder_OrderDoesNotExist() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderServiceImpl.deleteOrder(1L));
    }
}
