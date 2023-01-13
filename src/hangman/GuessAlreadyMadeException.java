package hangman;

public class GuessAlreadyMadeException extends Exception {
    public void printMessage() {
        System.out.println("Already guessed! Enter a new guess: ");
    }
}
