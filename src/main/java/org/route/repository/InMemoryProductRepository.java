package org.route.repository;

import jakarta.annotation.PostConstruct;
import org.route.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryProductRepository implements ProductRepository {
    private final List<Product> products = new ArrayList<>();

    @PostConstruct
    public void seed() {
        products.add(new Product(
            "DL-001",
            "Desk Lamp",
            new BigDecimal("1250.00"),
            12,
            "lighting",
            ""
        ));

        products.add(new Product(
            "ASUS-TUF",
            "ASUS TUF",
            new BigDecimal("52000.00"),
            5,
            "laptops",
            "asus"
        ));

        products.add(new Product(
            "PH-A55-128",
            "Samsung Galaxy A55 128GB",
            new BigDecimal("18500.00"),
            7,
            "mobile-phones",
            "samsung"
        ));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    @Override
    public void save(Product product) {
        products.add(product);
    }
}
