package org.route.model;

import java.math.BigDecimal;

public record ProductListItem(
        String sku,
        String name,
        BigDecimal price,
        int stock,
        String categoryName,
        String brandName
) {
}
