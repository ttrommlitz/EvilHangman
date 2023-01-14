package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    private int numGuesses;
    private int wordLength;
    private HashSet<String> wordSet;
    private HashMap<String, HashSet<String>> partition;
    private TreeSet<Character> wrongGuesses;
    private String guessedPortion;

    public EvilHangmanGame() {
        wrongGuesses = new TreeSet<>();
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        this.wordLength = wordLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            sb.append("-");
        }
        guessedPortion = sb.toString();
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
        if (!wrongGuesses.add(Character.toLowerCase(guess))) { throw new GuessAlreadyMadeException(); }
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
        wordSet = Tiebreaker(partition, guess);
        return wordSet;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return wrongGuesses;
    }

    public int getNumGuesses() {
        return numGuesses;
    }

    public void setNumGuesses(int guesses) {
        numGuesses = guesses;
    }

    public String getGuessedPortion() {
        return guessedPortion;
    }

    //helpers
    private String getSubsetKey(String word, char guessedLetter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guessedLetter) {
                sb.append(guessedLetter);
            } else {
                sb.append('-');
            }
        }
        return sb.toString();
    }

    private HashSet<String> Tiebreaker(HashMap<String, HashSet<String>> partition, char guess) {
        int maxSize = 0;
        var maxSets = new HashMap<String, HashSet<String>>();
        for (Map.Entry<String, HashSet<String>> currEntry : partition.entrySet()) {
            if (currEntry.getValue().size() == maxSize) {
                maxSets.put(currEntry.getKey(), currEntry.getValue());
            } else if (currEntry.getValue().size() > maxSize) {
                maxSize = currEntry.getValue().size();
                maxSets = new HashMap<>();
                maxSets.put(currEntry.getKey(), currEntry.getValue());
            }
        }
        if (maxSets.size() == 1) { return theReturn(maxSets.entrySet().iterator().next()); }

        var newMaxSets = new HashMap<String, HashSet<String>>();
        int letterCount = wordLength;
        for (var currEntry : maxSets.entrySet()) {
            if (!currEntry.getKey().contains("" + guess)) {
                return theReturn(currEntry);
            }
            int currCount = 0;
            for (int j = 0; j < currEntry.getKey().length(); j++) {
                if (currEntry.getKey().charAt(j) == guess) { currCount++; }
            }
            if (currCount == letterCount) { newMaxSets.put(currEntry.getKey(), currEntry.getValue()); }
            else if (currCount < letterCount) {
                letterCount = currCount;
                newMaxSets = new HashMap<>();
                newMaxSets.put(currEntry.getKey(), currEntry.getValue());
            }
        }
        if (newMaxSets.size() == 1) { return theReturn(newMaxSets.entrySet().iterator().next()); }

        int i = 0;
        while (newMaxSets.size() != 1) {
            maxSets = new HashMap<>();
            for (var currEntry : newMaxSets.entrySet()) {
                if (currEntry.getKey().charAt(currEntry.getKey().length() - 1 - i) == guess) {
                    maxSets.put(currEntry.getKey(), currEntry.getValue());
                }
            }
            i++;
            if (maxSets.size() != 0) {
                newMaxSets = maxSets;
            }
        }
        for (var currEntry : newMaxSets.entrySet()) {
            return theReturn(currEntry);
        }
        return null;
    }

    private HashSet<String> theReturn(Map.Entry<String, HashSet<String>> lastEntry) {
        boolean correctGuess = false;
        for (int i = 0; i < lastEntry.getKey().length(); i++) {
            if (lastEntry.getKey().charAt(i) != '-') {
                correctGuess = true;
                break;
            }
        }
        if (!correctGuess) {
            numGuesses--;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guessedPortion.length(); i++) {
            if (guessedPortion.charAt(i) == '-' && lastEntry.getKey().charAt(i) != '-') {
                sb.append(lastEntry.getKey().charAt(i));
            } else {
                sb.append(guessedPortion.charAt(i));
            }
        }
        guessedPortion = sb.toString();
        return lastEntry.getValue();
    }
}
