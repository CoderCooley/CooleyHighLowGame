package com.company;

import java.util.Random;
import java.util.Scanner;

import static java.lang.Character.isDigit;
import static java.lang.Math.*;

/**
 * Creation of the High Low Game, which allows the player to guess a
 * random number with sassy hints from the game. Cooley edition of the game.
 *
 * @author C22Jake.Cooley
 */
public class Main {
    private static final int MAX_NUMBER = 100; // Upper bound of the random number in game.
    private static final int MIN_NUMBER = 0; //Lower bound of the random number in game.
    //Optimal number of guesses for given range
    private static final double OPTIMAL_GUESSES = ceil(log10(MAX_NUMBER) / log10(2));

    /**
     * Gets the player's name, starts and repeats the game as needed, and concludes with game stats.
     * @param args - array of Strings for command line arguments.
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int totalGuesses = 0; //Total guesses throughout all of player's games
        int totalGames = 1; //Total number of games the player has played.
        boolean playAgain = false;
        String playerName = getPlayerName(s); //Gets the player's name
        System.out.println("Hi there " + playerName);
        //Runs the game until the player says they no longer want to play.
        do {
            totalGuesses += playHighLow(playerName, s);
            System.out.println("Would you like to play again, " + playerName + "?");
            playAgain = askToPlayAgain(s);
            if (playAgain) {
                System.out.println("You like the game? :o, Great!");
                totalGames += 1;
            } else {
                System.out.println("Oh...well I didn't really want to play with you either, anyway here are your stats");
            }
        } while (playAgain);
        concludeGame(playerName, totalGuesses, totalGames);
    }

    /**
     * @param s
     * @return
     */
    private static boolean askToPlayAgain(Scanner s) {
        boolean playAgain;
        boolean properInput = true;
        do {
            String playerAnswer = s.nextLine();
            if (playerAnswer.equalsIgnoreCase("y") || playerAnswer.equalsIgnoreCase("yes")) {
                properInput = true;
                playAgain = true;
            } else if (playerAnswer.equalsIgnoreCase("n") || playerAnswer.equalsIgnoreCase("no")) {
                properInput = true;
                playAgain = false;
            } else {
                System.out.println("Improper input, try again.");
                properInput = false;
                playAgain = false; // This is here just in case something weird happens.
            }
        } while (!properInput);
        return playAgain;
    }

    /**
     * @param name
     * @param totalGuesses
     * @param totalGames
     */
    private static void concludeGame(String name, int totalGuesses, int totalGames) {
        double avgGuess = 0;
        if (totalGames != 0) {
            avgGuess = (double) totalGuesses / totalGames;
        }
        System.out.printf("The optimal number of guesses was: %.1f \n", OPTIMAL_GUESSES);
        System.out.printf("%s, it took you an average of %.1f guesses to find the secret number\n", name, avgGuess);
        if (avgGuess > (OPTIMAL_GUESSES + 2)) {
            System.out.println("Ouch, looks like you need some more practice!");
        } else if (avgGuess > OPTIMAL_GUESSES) {
            System.out.println("Not bad, but I still think you can do better");
        } else if (avgGuess > (OPTIMAL_GUESSES - 2)) {
            if (avgGuess == OPTIMAL_GUESSES) {
                System.out.println("Wow, right on the nose!");
            } else {
                System.out.println("Wow, you did pretty well!");
            }
        } else {
            System.out.println("You did so well, I'd almost suspect you were cheating...");
        }
    }

    /**
     * @param s
     * @return
     */
    private static String getPlayerName(Scanner s) {
        System.out.println("Hello there, what is your name?");
        boolean properName = false;
        int nameCounter = 0;
        String playerName;
        do {
            playerName = s.nextLine();
            if (playerName.isEmpty()) {
                System.out.println("I didn't catch that name, silent treatment huh? Try again");
                nameCounter += 1;
            }
        } while (playerName.isEmpty() && nameCounter < 3);
        if (nameCounter >= 3) {
            System.out.println("Alright, you must be undercover huh?");
            playerName = "SecretAgent";
        }
        return playerName;
    }

    /**
     * @param playerName
     * @param s
     * @return
     */
    private static int playHighLow(String playerName, Scanner s) {
        Random rand = new Random(System.currentTimeMillis());
        int numGuesses = 0;
        int userGuess = MIN_NUMBER;
        int upperBound = MAX_NUMBER;
        int lowerBound = MIN_NUMBER;
        boolean properInput = true;
        String playerInput;
        int secretNumber = rand.nextInt(MAX_NUMBER);
        do {
            numGuesses++;
            System.out.printf("%s, please enter guess %d(Hint: The max number is %d)\n",
                    playerName, numGuesses, MAX_NUMBER);
            try {
                properInput = true;
                playerInput = s.nextLine();
                if (playerInput.isEmpty()) {
                    do {
                        System.out.println("Sorry? I didn't catch your guess.");
                        playerInput = s.nextLine();
                    } while (playerInput.isEmpty());
                }
                if (!isDigit(playerInput.charAt(0))) {
                    throw new ArithmeticException("Improper input, must be integer");
                }
                userGuess = Integer.parseInt((playerInput));
                if ((userGuess > MAX_NUMBER || userGuess < MIN_NUMBER)) {
                    throw new ArithmeticException("Improper input, guess must be between " + MIN_NUMBER +
                            " and " + MAX_NUMBER);
                }
            } catch (Exception e) {
                System.out.println("Exception occurred: " + e.getMessage());
                properInput = false;
            }
            if (properInput) {
                if (userGuess > secretNumber) {
                    if (userGuess < upperBound) {
                        upperBound = userGuess;
                    }
                    if ((userGuess > upperBound)) {
                        System.out.println("Hey sweetie, if your last guess was too high, going higher won't help :)");
                    } else {
                        System.out.println("Your guess was too high.");
                    }
                }
                if (userGuess < secretNumber) {
                    if (userGuess > lowerBound) {
                        lowerBound = userGuess;
                    }
                    if (userGuess < lowerBound) {
                        System.out.println("Hey sweetie, if your last guess was too low, going lower won't help :)");
                    } else {
                        System.out.println("Your guess was too low.");
                    }
                }
            }

        } while (userGuess != secretNumber);
        if (numGuesses < (OPTIMAL_GUESSES + 2)) {
            System.out.println("Oh nice, you got it!");
        } else {
            System.out.println("Oh good, you finally got it. Took you long enough");
        }
        System.out.println("It took you a total of " + numGuesses + " guesses to get the secret number.");
        return numGuesses;
    }

}
