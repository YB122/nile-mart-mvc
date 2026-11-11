package org.route.controller;

import jakarta.validation.Valid;
import org.route.exception.BusinessRuleException;
import org.route.model.Category;
import org.route.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryController {
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("categories", categoryService.findAllForList());
		return "categories";
	}

	@GetMapping("/new")
	public String newCategory(Model model) {
		model.addAttribute("categories", new Category());
		addParentOptions(model);
		return "categoryForm";
	}

	@PostMapping
	public String saveCategory(@Valid @ModelAttribute("category") Category category,
	                           BindingResult result, Model model) {
		if (!result.hasErrors()) {
			try {
				categoryService.save(category);
			} catch (BusinessRuleException err) {
				result.rejectValue(
						err.getFiled(),
						"business.rule",
						err.getMessage()
				);
			}
		}

		if (result.hasErrors()) {
			addParentOptions(model);
			return "categoryForm";
		}
		return "redirect:/categories";
	}

	private void addParentOptions(Model model) {
		model.addAttribute(
				"parentCategories",
				categoryService.findActiveTopLevel()
		);
	}
}
