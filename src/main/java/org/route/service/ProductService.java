package org.route.service;


import org.route.exception.BusinessRuleException;
import org.route.model.Brand;
import org.route.model.Category;
import org.route.model.Product;
import org.route.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, BrandService brandService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void save(Product product) {
        product.setSku(product.getSku().trim());
        product.setName(product.getName().trim());
        product.setCategory(product.getCategory().trim());

        product.setBrandSlug(product.getBrandSlug() == null ? "" : product.getBrandSlug().trim());

        Category category = categoryService.findBySlug(product.getCategory())
            .orElseThrow(() -> new BusinessRuleException(
                "category",
                "Selected category does not exist"
            ));
        if (category.getParentSlug() == null || category.getParentSlug().isBlank()) {
            throw new BusinessRuleException(
                "category",
                "Products must belong to a sub-category, not a top-level category"
            );
        }

        if (!category.isActive()) {
            throw new BusinessRuleException(
                "category",
                "Inactive categories cannot be selected for new products"
            );
        }

        if (!product.getBrandSlug().isBlank()) {
            Brand brand = brandService.findBySlug(product.getBrandSlug())
                .orElseThrow(() -> new BusinessRuleException(
                    "brandSlug",
                    "Selected brand does not exist"
                ));

            if (!brand.isActive()) {
                throw new BusinessRuleException(
                    "brandSlug",
                    "Inactive brands cannot be selected for new products"
                );
            }
        }

        productRepository.save(product);
    }
}
