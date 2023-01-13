package hangman;

import java.io.File;
import java.io.IOException;

public class EvilHangman {

    public static void main(String[] args) {
        var eh = new EvilHangman();
        EvilHangmanGame game = new EvilHangmanGame();
        try {
            game.startGame(new File(args[0]), Integer.parseInt(args[1]));
        } catch (EmptyDictionaryException ex) {
            ex.printMessage();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        for (int i = 0; i < Integer.parseInt(args[1]); i++) {
            eh.playRound(game);
        }
    }

    //helpers
    private void playRound(EvilHangmanGame game) {
        System.out.println("You have " + game.getNumGuesses() + " guesses left\n");
        StringBuilder sb = new StringBuilder();
        for (char element : game.getGuessedLetters()) {
            sb.append(element);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        System.out.println("Used letters: [" + sb + "]\n");
        System.out.println("Word: " + game.getGuessedLetters() + "\n");
        System.out.println("Enter guess: ");

    }

}
