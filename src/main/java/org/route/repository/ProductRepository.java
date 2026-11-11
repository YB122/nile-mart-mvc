package org.route.repository;

import org.route.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ProductRepository {

    List<Product> findAll();

    void save(Product product);
}
