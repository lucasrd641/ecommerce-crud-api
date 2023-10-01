package com.teamviewer.challenge.ecommerce.controller;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.model.Order;
import com.teamviewer.challenge.ecommerce.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllOrders_ReturnsOk() {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(new Order()));

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getOrderById_ReturnsOk() {
        when(orderService.getOrderById(anyLong())).thenReturn(new Order());

        ResponseEntity<Order> response = orderController.getOrderById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createOrder_ReturnsCreated() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Collections.singletonList(1L));
        orderDto.setCustomerName("John Doe");
        orderDto.setAddress("123 Street");

        when(orderService.createOrder(any())).thenReturn(new Order());

        ResponseEntity<Order> response = orderController.createOrder(orderDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateOrder_ReturnsOk() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Collections.singletonList(2L));
        orderDto.setCustomerName("Jane Doe");
        orderDto.setAddress("456 Avenue");

        when(orderService.updateOrder(anyLong(), any())).thenReturn(new Order());

        ResponseEntity<Order> response = orderController.updateOrder(1L, orderDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteOrder_ReturnsNoContent() {
        doNothing().when(orderService).deleteOrder(anyLong());

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getOrderById_OrderNotFound_ReturnsNotFound() {
        when(orderService.getOrderById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            orderController.getOrderById(1L);
        });
    }

    @Test
    void updateOrder_OrderNotFound_ReturnsNotFound() {
        when(orderService.updateOrder(anyLong(), any())).thenThrow(ResourceNotFoundException.class);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItemIds(Collections.singletonList(3L));
        orderDto.setCustomerName("Alice");
        orderDto.setAddress("789 Boulevard");

        assertThrows(ResourceNotFoundException.class, () -> {
            orderController.updateOrder(1L, orderDto);
        });
    }

    @Test
    void deleteOrder_OrderNotFound_ReturnsNotFound() {
        doThrow(ResourceNotFoundException.class).when(orderService).deleteOrder(anyLong());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderController.deleteOrder(1L);
        });
    }
}
