package com.teamviewer.challenge.ecommerce.controller;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.entity.OrderItem;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.service.OrderItemServiceImpl;
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

class OrderItemControllerTest {

    @Mock
    private OrderItemServiceImpl orderItemService;

    @InjectMocks
    private OrderItemController orderItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllOrderItems_ReturnsOk() {
        when(orderItemService.getAllOrderItems()).thenReturn(Collections.singletonList(new OrderItem()));

        ResponseEntity<List<OrderItem>> response = orderItemController.getAllOrderItems();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getOrderItemById_ReturnsOk() {
        when(orderItemService.getOrderItemById(anyLong())).thenReturn(new OrderItem());

        ResponseEntity<OrderItem> response = orderItemController.getOrderItemById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createOrderItem_ReturnsCreated() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(1L);
        orderItemDto.setQuantity(2);

        when(orderItemService.createOrderItem(any())).thenReturn(new OrderItem());

        ResponseEntity<OrderItem> response = orderItemController.createOrderItem(orderItemDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateOrderItem_ReturnsOk() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(2L);
        orderItemDto.setQuantity(3);

        when(orderItemService.updateOrderItem(anyLong(), any())).thenReturn(new OrderItem());

        ResponseEntity<OrderItem> response = orderItemController.updateOrderItem(1L, orderItemDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteOrderItem_ReturnsNoContent() {
        doNothing().when(orderItemService).deleteOrderItem(anyLong());

        ResponseEntity<Void> response = orderItemController.deleteOrderItem(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getOrderItemById_OrderItemNotFound_ReturnsNotFound() {
        when(orderItemService.getOrderItemById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            orderItemController.getOrderItemById(1L);
        });
    }

    @Test
    void updateOrderItem_OrderItemNotFound_ReturnsNotFound() {
        when(orderItemService.updateOrderItem(anyLong(), any())).thenThrow(ResourceNotFoundException.class);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(3L);
        orderItemDto.setQuantity(4);

        assertThrows(ResourceNotFoundException.class, () -> {
            orderItemController.updateOrderItem(1L, orderItemDto);
        });
    }

    @Test
    void deleteOrderItem_OrderItemNotFound_ReturnsNotFound() {
        doThrow(ResourceNotFoundException.class).when(orderItemService).deleteOrderItem(anyLong());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderItemController.deleteOrderItem(1L);
        });
    }
}
