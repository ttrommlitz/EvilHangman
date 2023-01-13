package hangman;

import java.lang.System;

public class EmptyDictionaryException extends Exception {
	public void printMessage() {
        System.out.println("Empty Dictionary!");
    }
}
