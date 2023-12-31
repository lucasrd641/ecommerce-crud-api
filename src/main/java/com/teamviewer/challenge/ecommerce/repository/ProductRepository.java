package com.teamviewer.challenge.ecommerce.repository;

import com.teamviewer.challenge.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);
    Product findByNameIgnoreCase(String name);
}
