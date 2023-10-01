package com.teamviewer.challenge.ecommerce.controller;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.model.OrderItem;
import com.teamviewer.challenge.ecommerce.service.OrderItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemServiceImpl orderItemServiceImpl;

    @Autowired
    public OrderItemController(OrderItemServiceImpl orderItemServiceImpl) {
        this.orderItemServiceImpl = orderItemServiceImpl;
    }

    @Operation(summary = "Get a list of all order items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of order items")
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemServiceImpl.getAllOrderItems());
    }

    @Operation(summary = "Get an order item by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order item"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemServiceImpl.getOrderItemById(id);
        return ResponseEntity.ok(orderItem);
    }

    @Operation(summary = "Create a new order item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created order item"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@Valid @RequestBody OrderItemDto orderItemDto) {
        OrderItem createdOrderItem = orderItemServiceImpl.createOrderItem(orderItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
    }

    @Operation(summary = "Update an existing order item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated order item"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItemDto orderItemDto) {
        OrderItem updatedOrderItem = orderItemServiceImpl.updateOrderItem(id, orderItemDto);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @Operation(summary = "Delete an order item by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted order item"),
            @ApiResponse(responseCode = "404", description = "Order item not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemServiceImpl.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
