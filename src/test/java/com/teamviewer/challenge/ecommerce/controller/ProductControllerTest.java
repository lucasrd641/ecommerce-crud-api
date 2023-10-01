package com.teamviewer.challenge.ecommerce.controller;

import com.teamviewer.challenge.ecommerce.dto.ProductDto;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.model.Product;
import com.teamviewer.challenge.ecommerce.service.ProductServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductServiceImpl productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_ReturnsOk() {
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(new Product()));

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getProductById_ReturnsOk() {
        when(productService.getProductById(anyLong())).thenReturn(new Product());

        ResponseEntity<Product> response = productController.getProductById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createProduct_ReturnsCreated() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setPrice(new BigDecimal(10));
        when(productService.createProduct(any())).thenReturn(new Product());

        ResponseEntity<Product> response = productController.createProduct(productDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateProduct_ReturnsOk() {
        ProductDto productDto = new ProductDto("Updated Product", new BigDecimal(20));
        when(productService.updateProduct(anyLong(), any())).thenReturn(new Product());

        ResponseEntity<Product> response = productController.updateProduct(1L, productDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteProduct_ReturnsNoContent() {
        doNothing().when(productService).deleteProduct(anyLong());

        ResponseEntity<Void> response = productController.deleteProduct(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getProductById_ProductNotFound_ReturnsNotFound() {
        when(productService.getProductById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            productController.getProductById(1L);
        });
    }

    @Test
    void updateProduct_ProductNotFound_ReturnsNotFound() {
        when(productService.updateProduct(anyLong(), any())).thenThrow(ResourceNotFoundException.class);

        ProductDto productDto = new ProductDto("Updated Product", new BigDecimal(20));

        assertThrows(ResourceNotFoundException.class, () -> {
            productController.updateProduct(1L, productDto);
        });
    }

    @Test
    void deleteProduct_ProductNotFound_ReturnsNotFound() {
        doThrow(ResourceNotFoundException.class).when(productService).deleteProduct(anyLong());

        assertThrows(ResourceNotFoundException.class, () -> {
            productController.deleteProduct(1L);
        });
    }
}
