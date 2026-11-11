package org.route.service;

import org.route.exception.BusinessRuleException;
import org.route.model.Brand;
import org.route.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
  private final BrandRepository brandRepository;

  public BrandService(BrandRepository brandRepository) {
    this.brandRepository = brandRepository;
  }

  public List<Brand> findAll() {
    return brandRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Brand::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
  }

  public List<Brand> findActive() {
    return findAll().stream()
            .filter(Brand::isActive)
            .toList();
  }

  public Optional<Brand> findBySlug(String slug) {
    if(slug == null || slug.isEmpty() || slug.isBlank()) {
      return Optional.empty();
    }
    return brandRepository.findBySlug(slug);
  }

  public void save(Brand brand) {
    brand.setName(brand.getName().trim());
    brand.setSlug(brand.getSlug().trim());

    if(brandRepository.existsBySlug(brand.getName())){
      throw new BusinessRuleException("name", "Brand name already exists");
    }

    if(brandRepository.existsBySlug(brand.getSlug())){
      throw new BusinessRuleException("slug", "Brand slug already exists");
    }

    brandRepository.save(brand);
  }
}
