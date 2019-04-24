package com.github.dwolverton.consoletester;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.dwolverton.consoletester.match.LineUtils;

class LineUtilsTest {

	@Test
	void testFindStartOneLine() {
		assertEquals(0, LineUtils.findLineStart("One line", 0));
		assertEquals(0, LineUtils.findLineStart("One line", 3));
		assertEquals(0, LineUtils.findLineStart("One line", 6));
		assertEquals(0, LineUtils.findLineStart("One line", 8));
	}
	
	@Test
	void testFindEndOneLine() {
		assertEquals(8, LineUtils.findLineEnd("One line", 0));
		assertEquals(8, LineUtils.findLineEnd("One line", 3));
		assertEquals(8, LineUtils.findLineEnd("One line", 6));
		assertEquals(8, LineUtils.findLineEnd("One line", 8));
	}
	
	@Test
	void testFindStartTwoLine() {
		assertEquals(0, LineUtils.findLineStart("Line 1\nLine 2", 0));
		assertEquals(0, LineUtils.findLineStart("Line 1\nLine 2", 3));
		assertEquals(0, LineUtils.findLineStart("Line 1\nLine 2", 6));
		assertEquals(7, LineUtils.findLineStart("Line 1\nLine 2", 7));
		assertEquals(7, LineUtils.findLineStart("Line 1\nLine 2", 10));
		assertEquals(7, LineUtils.findLineStart("Line 1\nLine 2", 13));
	}
	
	@Test
	void testFindEndTwoLine() {
		assertEquals(6, LineUtils.findLineEnd("Line 1\nLine 2", 0));
		assertEquals(6, LineUtils.findLineEnd("Line 1\nLine 2", 3));
		assertEquals(6, LineUtils.findLineEnd("Line 1\nLine 2", 6));
		assertEquals(13, LineUtils.findLineEnd("Line 1\nLine 2", 7));
		assertEquals(13, LineUtils.findLineEnd("Line 1\nLine 2", 10));
		assertEquals(13, LineUtils.findLineEnd("Line 1\nLine 2", 13));
	}
	
	@Test
	void testFindStartTwoLineCRLF() {
		assertEquals(0, LineUtils.findLineStart("Line 1\r\nLine 2", 0));
		assertEquals(0, LineUtils.findLineStart("Line 1\r\nLine 2", 3));
		assertEquals(0, LineUtils.findLineStart("Line 1\r\nLine 2", 6));
		assertEquals(8, LineUtils.findLineStart("Line 1\r\nLine 2", 8));
		assertEquals(8, LineUtils.findLineStart("Line 1\r\nLine 2", 11));
		assertEquals(8, LineUtils.findLineStart("Line 1\r\nLine 2", 14));
	}
	
	@Test
	void testFindEndTwoLineCRLF() {
		assertEquals(6, LineUtils.findLineEnd("Line 1\r\nLine 2", 0));
		assertEquals(6, LineUtils.findLineEnd("Line 1\r\nLine 2", 3));
		assertEquals(6, LineUtils.findLineEnd("Line 1\r\nLine 2", 6));
		assertEquals(14, LineUtils.findLineEnd("Line 1\r\nLine 2", 8));
		assertEquals(14, LineUtils.findLineEnd("Line 1\r\nLine 2", 11));
		assertEquals(14, LineUtils.findLineEnd("Line 1\r\nLine 2", 14));
	}
	
	@Test
	void testFindStartMultiLine() {
		assertEquals(8, LineUtils.findLineStart("Line 1\r\nLine 2\r\nLine 3", 8));
		assertEquals(8, LineUtils.findLineStart("Line 1\r\nLine 2\r\nLine 3", 14));
		assertEquals(16, LineUtils.findLineStart("Line 1\r\nLine 2\r\nLine 3", 16));
		assertEquals(16, LineUtils.findLineStart("Line 1\r\nLine 2\r\nLine 3", 22));
	}

	
	@Test
	void testFindEndMultiLine() {
		assertEquals(6, LineUtils.findLineEnd("Line 1\r\nLine 2\r\nLine 3", 0));
		assertEquals(6, LineUtils.findLineEnd("Line 1\r\nLine 2\r\nLine 3", 6));
		assertEquals(14, LineUtils.findLineEnd("Line 1\r\nLine 2\r\nLine 3", 8));
		assertEquals(14, LineUtils.findLineEnd("Line 1\r\nLine 2\r\nLine 3", 14));
		assertEquals(22, LineUtils.findLineEnd("Line 1\r\nLine 2\r\nLine 3", 16));
		assertEquals(22, LineUtils.findLineEnd("Line 1\r\nLine 2\r\nLine 3", 22));
	}

}
