package org.route.controller;


import jakarta.validation.Valid;
import org.route.exception.BusinessRuleException;
import org.route.model.Brand;
import org.route.model.Category;
import org.route.model.Product;
import org.route.model.ProductListItem;
import org.route.service.BrandService;
import org.route.service.CategoryService;
import org.route.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
  private final ProductService productService;
  private final CategoryService categoryService;
  private final BrandService brandService;

  public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService) {
      this.productService = productService;
      this.categoryService = categoryService;
      this.brandService = brandService;
  }

  @GetMapping("/")
  public String home() {
      return "redirect:/products";
  }


  @GetMapping("/products")
  public String list(Model model) {
      List<ProductListItem> rows = productService.findAll().stream()
          .map(product -> {
              String categoryName = categoryService
                  .findBySlug(product.getCategory())
                  .map(Category::getName)
                  .orElse(product.getCategory());
              String brandName = brandService
                  .findBySlug(product.getBrandSlug())
                  .map(Brand::getName)
                  .orElse("-");
              return new ProductListItem(
                  product.getSku(),
                  product.getName(),
                  product.getPrice(),
                  product.getStock(),
                  categoryName,
                  brandName
              );
          })
          .toList();
      model.addAttribute("products", rows);
      return "products";
  }

  @GetMapping("/products/new")
  public String newProduct(Model model) {
      model.addAttribute("product", new Product());
      addProductOptions(model);
      return "productForm";
  }

    @PostMapping("/products")
    public String save(
        @Valid @ModelAttribute("product") Product product,
        BindingResult result,
        Model model
    ) {
        if (!result.hasErrors()) {
            try {
                productService.save(product);
            } catch (BusinessRuleException exception) {
                result.rejectValue(
                    exception.getFiled(),
                    "business.rule",
                    exception.getMessage()
                );
            }
        }

        if (result.hasErrors()) {
            addProductOptions(model);
            return "productForm";
        }

        return "redirect:/products";
    }

  private void addProductOptions(Model model) {
      model.addAttribute(
          "subCategories",
          categoryService.findActiveSubCategories()
      );
      model.addAttribute(
          "activeBrands",
          brandService.findActive()
      );
  }
}
