package com.github.dwolverton.consoletester.runner;

public enum BlockEndType {

	INPUT("the program is waiting for user input"),
	END("the program ended"),
	HANG("the program is hung, perhaps in an infinite loop"),
	EXCEPTION("the program terminated with an exception");

	private String actualMessage;

	private BlockEndType(String actualMessage) {
		this.actualMessage = actualMessage;
	}

	public String getActualMessage() {
		return actualMessage;
	}

}
