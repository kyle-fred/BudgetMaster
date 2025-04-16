package com.budgetmaster.utils.string;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {
	// -- Test Data --
	private static final String testString = "test string";
	private static final String expectedCapitalized = "TEST STRING";
	
	// -- Capitalize Tests --
	@Test
	void capitalize_ValidString_ReturnsCapitalized() {
		String result = StringUtils.capitalize(testString);
		assertEquals(expectedCapitalized, result, "Should return the capitalized string");
	}

	@Test
	void capitalize_NullInput_ReturnsNull() {
		String result = StringUtils.capitalize(null);
		assertNull(result, "Should return null for null input");
	}

	@Test
	void capitalize_EmptyString_ReturnsEmpty() {
		String result = StringUtils.capitalize("");
		assertEquals("", result, "Should return empty string for empty input");
	}
} 