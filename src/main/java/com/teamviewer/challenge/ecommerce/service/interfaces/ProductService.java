package com.teamviewer.challenge.ecommerce.service.interfaces;

import com.teamviewer.challenge.ecommerce.dto.ProductDto;
import com.teamviewer.challenge.ecommerce.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(ProductDto productDto);
    Product updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
}
