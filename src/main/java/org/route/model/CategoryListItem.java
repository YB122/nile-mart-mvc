package org.route.model;

public record CategoryListItem(
        String name,
        String slug,
        String parentName,
        int displayOrder,
        boolean active,
        boolean subCategory
) {
}
