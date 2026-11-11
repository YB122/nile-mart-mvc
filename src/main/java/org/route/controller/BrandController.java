package org.route.controller;

import jakarta.validation.Valid;
import org.route.exception.BusinessRuleException;
import org.route.model.Brand;
import org.route.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/brands")
public class BrandController {
  public final BrandService brandService;

  public BrandController(BrandService brandService) {
    this.brandService = brandService;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("brands", brandService.findAll());
    return "brands";
  }

  @GetMapping("/new")
  public String newForm(Model model) {
    model.addAttribute("brand", new Brand());
    return "brandForm";
  }

  @PostMapping
  public String save(@Valid @ModelAttribute("brand") Brand brand, BindingResult result) {
    if(result.hasErrors()) {
      return "brandForm";
    }
    try {
      brandService.save(brand);
    }catch (BusinessRuleException err){
      result.reject(err.getFiled());
      return "brandForm";
    }
    return "redirect:/brands";
  }
}
