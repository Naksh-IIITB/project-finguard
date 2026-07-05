package com.finguard.core.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CategoryEncoder {

    private static final Map<String, Integer> CATEGORY_CODES = Map.ofEntries(
            Map.entry("groceries", 1),
            Map.entry("entertainment", 2),
            Map.entry("travel", 3),
            Map.entry("utilities", 4),
            Map.entry("food", 5),
            Map.entry("shopping", 6),
            Map.entry("health", 7),
            Map.entry("education", 8)
    );

    public int encode(String category) {
        if (category == null) {
            return 0;
        }

        return CATEGORY_CODES.getOrDefault(category.trim().toLowerCase(), 0);
    }
}
