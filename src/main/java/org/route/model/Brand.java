package org.route.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  @NotBlank(message = "Slug is required")
  @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Use lowercase letters, numbers and single hyphens only")
  private String slug;

  private boolean active = true;
}
