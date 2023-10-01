package com.teamviewer.challenge.ecommerce.controller;

import com.teamviewer.challenge.ecommerce.dto.OrderDto;
import com.teamviewer.challenge.ecommerce.model.Order;
import com.teamviewer.challenge.ecommerce.service.OrderServiceImpl;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    @Autowired
    public OrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @Operation(summary = "Get a list of all orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderServiceImpl.getAllOrders());
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderServiceImpl.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created order"),
            @ApiResponse(responseCode = "400", description = "Invalid order input")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderServiceImpl.createOrder(order));
    }

    @Operation(summary = "Update an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated order"),
            @ApiResponse(responseCode = "400", description = "Invalid order input"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderServiceImpl.updateOrder(id, orderDto));
    }

    @Operation(summary = "Delete an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderServiceImpl.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
