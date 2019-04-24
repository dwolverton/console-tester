package com.github.dwolverton.consoletester.runner;

public class IOBlock {

	private String output;
	private BlockEndType endType;

	public IOBlock(String output, BlockEndType endType) {
		super();
		this.output = output;
		this.endType = endType;
	}

	public String getOutput() {
		return output;
	}

	public BlockEndType getEndType() {
		return endType;
	}

	@Override
	public String toString() {
		return "IOBlock [output=" + output + ", endType=" + endType + "]";
	}

}
