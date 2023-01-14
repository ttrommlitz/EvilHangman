package hangman;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) {
        var eh = new EvilHangman();
        EvilHangmanGame game = new EvilHangmanGame();
        game.setNumGuesses(Integer.parseInt(args[2]));
        try {
            game.startGame(new File(args[0]), Integer.parseInt(args[1]));
            Set<String> res = new HashSet<>();
            res.add("This should never be displayed");
            while (game.getNumGuesses() > 0) {
                res = eh.playRound(game);
                if (!game.getGuessedPortion().contains("" + '-')) {
                    System.out.println("You win! The correct word was: " + res.iterator().next());
                    break;
                }
            }
            if (game.getNumGuesses() == 0) {
                System.out.println("You lose! The correct word was: " + res.iterator().next());
            }
        } catch (EmptyDictionaryException ex) {
            ex.printMessage();
        } catch (IOException ex) {
            System.out.println("IOException: Do better next time lol");
        }
    }

    //helpers
    private Set<String> playRound(EvilHangmanGame game) {
        System.out.println("You have " + game.getNumGuesses() + " guesses left");
        System.out.println("Used letters: [" + getAlreadyGuessed(game) + "]");
        System.out.println("Word: " + game.getGuessedPortion());
        System.out.println("Enter guess: ");

        var scanner = new Scanner(System.in);
        boolean badInput = false;
        Set<String> result = null;
        do {
            String input = scanner.nextLine();
            if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                badInput = true;
                System.out.println("Bad Input! Try again: ");
                continue;
            }
            try {
                result = game.makeGuess(input.charAt(0));
                int numCorrectLetters = 0;
                for (int i = 0; i < result.iterator().next().length(); i++) {
                    if (result.iterator().next().charAt(i) == input.charAt(0)) {
                        numCorrectLetters++;
                    }
                }
                if (numCorrectLetters > 0) {
                    System.out.println("Yes, there is " + numCorrectLetters + " " + input.charAt(0) + "\n");
                    badInput = false;
                } else {
                    System.out.println("Sorry, there are no " + input.charAt(0) + "\n");
                    badInput = false;
                }

            } catch (GuessAlreadyMadeException ex) {
                badInput = true;
                ex.printMessage();
            }
        } while (badInput);
        return result;

    }

    private String getAlreadyGuessed(EvilHangmanGame game) {
        StringBuilder sb = new StringBuilder();
        for (char element : game.getGuessedLetters()) {
            sb.append(element);
            sb.append(", ");
        }
        if (game.getGuessedLetters().size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

}
