package com.budgetmaster.utils.string;

import com.budgetmaster.common.utils.StringUtils;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {
	// -- Test Data --
	private static final String testString = TestData.StringUtilsTestDataConstants.TEST_STRING;
	private static final String expectedCapitalized = TestData.StringUtilsTestDataConstants.TEST_STRING_CAPITALIZED;
	
	// -- Capitalize Tests --
	@Test
	void capitalize_ValidString_ReturnsCapitalized() {
		String result = StringUtils.capitalize(testString);
		assertEquals(expectedCapitalized, result);
	}

	@Test
	void capitalize_NullInput_ReturnsNull() {
		String result = StringUtils.capitalize(null);
		assertNull(result);
	}

	@Test
	void capitalize_EmptyString_ReturnsEmpty() {
		String result = StringUtils.capitalize(TestData.CommonTestDataConstants.EMPTY_STRING);
		assertEquals(TestData.CommonTestDataConstants.EMPTY_STRING, result);
	}
} 