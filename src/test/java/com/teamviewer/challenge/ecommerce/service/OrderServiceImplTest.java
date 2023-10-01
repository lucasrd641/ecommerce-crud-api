package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.entity.Order;
import com.teamviewer.challenge.ecommerce.entity.OrderItem;
import com.teamviewer.challenge.ecommerce.exception.DuplicateElementException;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(new Order()));
        List<Order> orders = orderService.getAllOrders();
        assertFalse(orders.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrderById() {
        Order mockOrder = new Order();
        mockOrder.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        Order order = orderService.getOrderById(1L);
        assertNotNull(order);
        assertEquals(1L, order.getId());
    }

    @Test
    public void testCreateOrder() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L)); // let's assume these are the order item IDs provided in the DTO

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal("100.00"));

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal("150.00"));

        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(Arrays.asList(item1, item2));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // returning what's passed to it

        Order savedOrder = orderService.createOrder(orderDto);
        assertNotNull(savedOrder);
        assertEquals("John Doe", savedOrder.getCustomerName());
        assertEquals(new BigDecimal("250.00"), savedOrder.getTotalPrice());  // assert that the total price calculation is correct
        assertEquals(2, savedOrder.getOrderItems().size());
    }


    @Test
    public void testCreateOrderWithDuplicateCustomerName() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");

        Order existingOrder = new Order();
        existingOrder.setCustomerName("John Doe");
        existingOrder.setAddress("456 Avenue");

        when(orderRepository.existsByCustomerNameIgnoreCase("John Doe")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderDto));
    }

    @Test
    public void testUpdateOrder() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("Jane Smith");
        orderDto.setAddress("789 Boulevard");
        orderDto.setOrderItemIds(Arrays.asList(3L, 4L));

        OrderItem item3 = new OrderItem();
        item3.setOrderItemPrice(new BigDecimal("200.00"));

        OrderItem item4 = new OrderItem();
        item4.setOrderItemPrice(new BigDecimal("300.00"));

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setCustomerName("John Doe");
        existingOrder.setAddress("456 Avenue");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(Arrays.asList(item3, item4));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));  // returning what's passed to it

        Order updatedOrder = orderService.updateOrder(1L, orderDto);
        assertNotNull(updatedOrder);
        assertEquals("Jane Smith", updatedOrder.getCustomerName());
        assertEquals(new BigDecimal("500.00"), updatedOrder.getTotalPrice()); // assert that the total price calculation is correct
        assertEquals(2, updatedOrder.getOrderItems().size());
    }

    @Test
    public void testUpdateNonExistentOrder() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("Jane Smith");
        orderDto.setAddress("789 Boulevard");
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L));
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, orderDto));
    }

    @Test
    public void testDeleteNonExistentOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
    }

    @Test
    public void testCreateOrderWithInvalidDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName(""); // Invalid: Empty name
        orderDto.setAddress("789 Boulevard");

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderDto));
    }

    @Test
    public void testCreateOrderWithMissingOrderItems() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L));

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal("100.00"));

        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(Arrays.asList(item1)); // Only one item returned

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderDto));
    }

    @Test
    public void testUpdateOrderWithExistingDifferentCustomerName() {
        OrderItem item = new OrderItem();
        item.setOrderItemPrice(new BigDecimal("200.00"));

        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Smith"); // Intend to change to this name
        orderDto.setAddress("789 Avenue");
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L));

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setCustomerName("Jane Smith"); // Original name
        existingOrder.setAddress("789 Avenue");
        existingOrder.setOrderItems(List.of(item));

        Order anotherOrder = new Order();
        anotherOrder.setId(2L);
        anotherOrder.setCustomerName("John Smith"); // Another order with the intended name
        anotherOrder.setAddress("789 Avenue");
        anotherOrder.setOrderItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.findByCustomerNameIgnoreCase(orderDto.getCustomerName())).thenReturn(anotherOrder);

        assertThrows(DuplicateElementException.class, () -> orderService.updateOrder(1L, orderDto));
    }

    @Test
    public void testCalculateTotalPriceOnOrderCreation() {

        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L));

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal("100.00"));

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal("150.00"));

        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(Arrays.asList(item1, item2));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(Arrays.asList(item1, item2));

        Order order = orderService.createOrder(orderDto);

        assertEquals(new BigDecimal("250.00"), order.getTotalPrice());
    }

    @Test
    public void testRemoveOrderItemOnOrderUpdate() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");
        orderDto.setOrderItemIds(List.of(1L)); // Updating to have only one item

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setCustomerName("John Doe");


        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal("100.00"));
        item1.setOrder(existingOrder);
        existingOrder.addOrderItem(item1);

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal("150.00"));
        item2.setOrder(existingOrder);
        existingOrder.addOrderItem(item2);

        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(List.of(item1));

        Order updatedOrder = orderService.updateOrder(1L, orderDto);

        assertEquals(3, updatedOrder.getOrderItems().size());
    }

    @Test
    public void testAddOrderItemOnOrderUpdate() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");
        orderDto.setOrderItemIds(Arrays.asList(1L, 2L, 3L));

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setCustomerName("John Doe");

        OrderItem item1 = new OrderItem();
        item1.setOrderItemPrice(new BigDecimal("100.00"));
        item1.setOrder(existingOrder);
        existingOrder.addOrderItem(item1);

        OrderItem item2 = new OrderItem();
        item2.setOrderItemPrice(new BigDecimal("150.00"));
        item2.setOrder(existingOrder);
        existingOrder.addOrderItem(item2);

        OrderItem item3 = new OrderItem();
        item3.setOrderItemPrice(new BigDecimal("200.00"));

        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderItemRepository.findAllById(orderDto.getOrderItemIds())).thenReturn(Arrays.asList(item1, item2, item3));

        Order updatedOrder = orderService.updateOrder(1L, orderDto);

        assertEquals(5, updatedOrder.getOrderItems().size());
    }

    @Test
    public void testUpdateOrderWithInvalidAddress() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("");

        assertThrows(IllegalArgumentException.class, () -> orderService.updateOrder(1L, orderDto));
    }


}
