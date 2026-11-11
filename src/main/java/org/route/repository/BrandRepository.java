package org.route.repository;

import org.route.model.Brand;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface BrandRepository {
  List<Brand> findAll();

  Optional<Brand> findBySlug(String slug);

  boolean existsBySlug(String slug);

  boolean existsByNameIgnoreCase(String name);

  void save(Brand brand);
}
