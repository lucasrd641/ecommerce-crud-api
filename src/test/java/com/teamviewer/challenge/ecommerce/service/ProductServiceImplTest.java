package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.ProductDto;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.model.Product;
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

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        Product mockProduct = new Product();
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal(10));
        when(productRepository.findAll()).thenReturn(Collections.singletonList(mockProduct));

        List<Product> result = productServiceImpl.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        assertEquals(new BigDecimal(10), result.get(0).getPrice());
    }

    @Test
    void testGetProductById() {
        Product mockProduct = new Product();
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal(10));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product result = productServiceImpl.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal(10), result.getPrice());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.getProductById(1L));
    }

    @Test
    void testCreateProduct() {
        ProductDto dto = new ProductDto("Test Product", new BigDecimal(10));

        Product mockProduct = new Product();
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal(10));

        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        Product result = productServiceImpl.createProduct(dto);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal(10), result.getPrice());
    }

    @Test
    void testUpdateProduct() {
        ProductDto dto = new ProductDto("Updated Product", new BigDecimal(20));

        Product existingProduct = new Product();
        existingProduct.setName("Old Product");
        existingProduct.setPrice(new BigDecimal(10));

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        Product result = productServiceImpl.updateProduct(1L, dto);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals(new BigDecimal(20), result.getPrice());
    }

    @Test
    void testDeleteProduct() {
        Product mockProduct = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        doNothing().when(productRepository).delete(mockProduct);

        productServiceImpl.deleteProduct(1L);

        verify(productRepository).delete(mockProduct);
    }
    @Test
    void testCreateProduct_NullDto() {
        assertThrows(NullPointerException.class, () -> productServiceImpl.createProduct(null));
    }

    @Test
    void testUpdateProduct_NullDto() {
        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.updateProduct(1L, null));
    }

    @Test
    void testUpdateProduct_NonExistingId() {
        ProductDto mockDto = new ProductDto();
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.updateProduct(1L, mockDto));
    }

    @Test
    void testDeleteProduct_NonExistingId() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productServiceImpl.deleteProduct(1L));
    }

}
