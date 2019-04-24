package com.github.dwolverton.consoletester;

import java.util.Scanner;

public class StaticScannerSample {

	private static Scanner scnr = new Scanner(System.in);
	
	public static void main(String[] args) {
		System.out.println("What's your name?");
		String name = scnr.nextLine();
		System.out.println("Hello " + name);
	}

}
