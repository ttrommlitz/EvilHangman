package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    private int numGuesses;
    private HashSet<String> wordSet;
    private HashMap<String, HashSet<String>> partition;
    private HashSet<Character> guesses;
    private TreeSet<Character> guessedPortion;

    public EvilHangmanGame() {
        guesses = new HashSet<>();
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        numGuesses = wordLength;
        guessedPortion = new TreeSet<>();
        for (int i = 0; i < wordLength; i++) {
            guessedPortion.add('_');
        }
        var scanner = new Scanner(dictionary);
        wordSet = new HashSet<>();
        while (scanner.hasNext()) {
            String word = scanner.next().toLowerCase();
            if (word.length() == wordLength) {
                wordSet.add(word);
            }
        }
        if (wordSet.isEmpty()) { throw new EmptyDictionaryException(); }
        scanner.close();
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if (!guesses.add(Character.toLowerCase(guess))) { throw new GuessAlreadyMadeException(); }
        partition = new HashMap<>();
        for (String element : wordSet) {
            var key = getSubsetKey(element, guess);
            if (partition.containsKey(key)) {
                partition.get(key).add(element);
            } else {
                var set = new HashSet<String>();
                set.add(element);
                partition.put(key, set);
            }
        }
        wordSet = Tiebreaker(partition);
        return wordSet;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedPortion;
    }

    public int getNumGuesses() {
        return numGuesses;
    }

    //helpers
    private String getSubsetKey(String word, char guessedLetter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guessedLetter) {
                sb.append(guessedLetter);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    private HashSet<String> Tiebreaker(HashMap<String, HashSet<String>> partition) {
        int maxSize = 0;
        var maxSet = new HashSet<String>();
        for (Map.Entry<String, HashSet<String>> currEntry : partition.entrySet()) {
            if (currEntry.getValue().size() > maxSize) {
                maxSet = currEntry.getValue();
                maxSize = currEntry.getValue().size();
            }
        }
        return maxSet;
    }
}
