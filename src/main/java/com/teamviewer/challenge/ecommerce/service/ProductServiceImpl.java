package com.teamviewer.challenge.ecommerce.service;

import com.teamviewer.challenge.ecommerce.dto.OrderItemDto;
import com.teamviewer.challenge.ecommerce.dto.ProductDto;
import com.teamviewer.challenge.ecommerce.exception.DuplicateElementException;
import com.teamviewer.challenge.ecommerce.exception.ResourceNotFoundException;
import com.teamviewer.challenge.ecommerce.entity.Product;
import com.teamviewer.challenge.ecommerce.repository.OrderItemRepository;
import com.teamviewer.challenge.ecommerce.repository.ProductRepository;
import com.teamviewer.challenge.ecommerce.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        if (!isValid(productDto)) throw new IllegalArgumentException("ProductDto is not valid");
        if (productRepository.existsByNameIgnoreCase(productDto.getName())) {
            throw new DuplicateElementException("Product with the same name already exists.");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setUnitsInStock(productDto.getUnitsInStock());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, ProductDto productDto) {
        if (!isValid(productDto)) throw new IllegalArgumentException("ProductDto is not valid");
        Product existingProductWithSameName = productRepository.findByNameIgnoreCase(productDto.getName());

        if (existingProductWithSameName != null && !existingProductWithSameName.getId().equals(id)) {
            throw new DuplicateElementException("Product with the same name already exists.");
        }
        if (!orderItemRepository.findByProductId(id).isEmpty()) {
            throw new IllegalStateException("Cannot update/delete product because it is associated with an existing order.");
        }

        Product productToUpdate = getProductById(id);
        productToUpdate.setName(productDto.getName());
        productToUpdate.setPrice(productDto.getPrice());
        productToUpdate.setUnitsInStock(productDto.getUnitsInStock());
        return productRepository.save(productToUpdate);
    }

    boolean isValid(ProductDto orderItemDto) {
        if (orderItemDto == null) return false;
        if (orderItemDto.getPrice().signum() < 0) return false;
        if (orderItemDto.getName().isBlank()) return false;
        return orderItemDto.getUnitsInStock() >= 0;
    }

    @Override
    public void deleteProduct(Long id) {
        if (!orderItemRepository.findByProductId(id).isEmpty()) {
            throw new IllegalStateException("Cannot update/delete product because it is associated with an existing order.");
        }
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
