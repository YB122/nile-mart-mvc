package org.route.service;

import org.route.exception.BusinessRuleException;
import org.route.model.Category;
import org.route.model.CategoryListItem;
import org.route.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//import static io.micrometer.common.util.StringUtils.isBlank;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public Optional<Category> findBySlug(String slug) {
		if(slug == null || slug.isBlank()) {
			return Optional.empty();
		}
		return categoryRepository.findBySlug(slug);
	}

	public List<Category> findActiveTopLevel() {
		return categoryRepository.findAll().stream()
				.filter(Category::isActive)
				.filter(category -> isBlank(category.getParentSlug()))
				.sorted(Comparator.comparingInt(Category::getDisplayOrder).thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	public List<Category> findActiveSubCategories() {
		return categoryRepository.findAll().stream()
				.filter(Category::isActive)
				.filter(category -> !isBlank(category.getParentSlug()))
				.sorted(Comparator.comparing(Category::getParentSlug)
						.thenComparingInt(Category::getDisplayOrder)
						.thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	public List<CategoryListItem> findAllForList() {
		List<Category> all = categoryRepository.findAll();

		List<Category> parents = all.stream()
				.filter(category -> isBlank(category.getParentSlug()))
				.sorted(Comparator.comparingInt(Category::getDisplayOrder)
						.thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
				.toList();

		return parents.stream()
				.flatMap(parent -> {
					CategoryListItem parentRow = new CategoryListItem(
							parent.getName(),
							parent.getSlug(),
							"-",
							parent.getDisplayOrder(),
							parent.isActive(),
							false
					);

					List<CategoryListItem> children = all.stream()
							.filter(child -> Objects.equals(
									child.getParentSlug(),
									parent.getSlug()
							))
							.sorted(Comparator.comparingInt(Category::getDisplayOrder)
									.thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
							.map(child -> new CategoryListItem(
									"- " + child.getName(),
									child.getSlug(),
									parent.getName(),
									child.getDisplayOrder(),
									child.isActive(),
									true
							))
							.toList();

					return java.util.stream.Stream.concat(
							java.util.stream.Stream.of(parentRow),
							children.stream()
					);
				})
				.toList();
	}

  public void save(Category category) {
		category.setName(category.getName().trim());
		category.setSlug(category.getSlug().trim());
		category.setParentSlug(normalizeParentSlug(category.getParentSlug()));

		validateParent(category.getParentSlug());
		validateUniqueName(category.getName(), category.getParentSlug());

		if (categoryRepository.existsBySlug(category.getSlug())) {
			throw new BusinessRuleException(
					"slug",
					"Slug already exists"
			);
		}

		categoryRepository.save(category);
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private String normalizeParentSlug(String parentSlug) {
		return parentSlug == null ? "" : parentSlug.trim();
	}

	private void validateParent(String parentSlug) {
		if (isBlank(parentSlug)) {
			return;
		}

		Category parent = categoryRepository.findBySlug(parentSlug)
				.orElseThrow(() -> new BusinessRuleException(
						"parentSlug",
						"Selected parent category does not exist"
				));

		if (!isBlank(parent.getParentSlug())) {
			throw new BusinessRuleException(
					"parentSlug",
					"Parent must be a top-level category"
			);
		}

		if (!parent.isActive()) {
			throw new BusinessRuleException(
					"parentSlug",
					"Inactive categories cannot be selected as a parent"
			);
		}
	}

	private void validateUniqueName(String name, String parentSlug) {
		Optional<Category> duplicate = categoryRepository.findAll().stream()
				.filter(existing -> existing.getName().equalsIgnoreCase(name))
				.filter(existing -> Objects.equals(
						normalizeParentSlug(existing.getParentSlug()),
						parentSlug
				))
				.findFirst();

		if (duplicate.isPresent()) {
			String parentName = isBlank(parentSlug)
					? "the top level"
					: categoryRepository.findBySlug(parentSlug)
					.map(Category::getName)
					.orElse("the selected parent");

			throw new BusinessRuleException(
					"name",
					"A category with this name already exists under " + parentName
			);
		}
	}
}
