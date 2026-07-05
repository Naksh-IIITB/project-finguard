package com.finguard.core.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryEncoderTest {

    private final CategoryEncoder categoryEncoder = new CategoryEncoder();

    @Test
    void encodeReturnsKnownCategoryCodeIgnoringCaseAndWhitespace() {
        assertEquals(2, categoryEncoder.encode(" Entertainment "));
    }

    @Test
    void encodeReturnsZeroForUnknownCategory() {
        assertEquals(0, categoryEncoder.encode("Pets"));
    }
}
