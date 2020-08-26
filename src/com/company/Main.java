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
    private static final int NUM_ATTEMPTS = 3; //Number of attempts to ask for name
    //Number of guesses off of optimal number for concludeGame output.
    private static final int OPTIMAL_GUESS_BUFFER = 2;

    /**
     * Gets the player's name, starts and repeats the game as needed, and concludes with game stats.
     *
     * @param args - array of Strings for command line arguments.
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int totalGuesses = 0; //Total guesses throughout all of player's games
        int totalGames = 1; //Total number of games the player has played.
        boolean playAgain;
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
        concludeGame(playerName, totalGuesses, totalGames); //Generates the stats for all of player's games
    }

    /**
     * Asks the player if they want to play antoher game, checking for valid input and looping until
     * that input is received. Valid inputs are any case forms of y,yes,n,no.
     *
     * @param s Scanner object for reading input from the user
     * @return A boolean that is true to play again and false if not
     */
    private static boolean askToPlayAgain(Scanner s) {
        boolean playAgain = false;
        boolean properInput;
        do {
            String playerAnswer = s.nextLine();
            //Checks input of player to see if valid case of y,yes,n, or no is entered.
            if (playerAnswer.equalsIgnoreCase("y") || playerAnswer.equalsIgnoreCase("yes")) {
                properInput = true;
                playAgain = true;
            } else if (playerAnswer.equalsIgnoreCase("n") || playerAnswer.equalsIgnoreCase("no")) {
                properInput = true;
                playAgain = false;
            } else {
                System.out.println("Improper input, try again.");
                properInput = false;
            }
        } while (!properInput);
        return playAgain;
    }

    /**
     * Provides the end of play results message assessing how well the player did
     * based on average number of guesses they took to win. Reports the average
     * number of guesses to one decimal place.
     *
     * @param name         Use the player's name to personalize the game messages
     * @param totalGuesses Total number of guesses across all games played
     * @param totalGames   Total number of games played
     */
    private static void concludeGame(String name, int totalGuesses, int totalGames) {
        //Calculates average guesses per game for player.
        double avgGuess = 0;
        //Checks for divide-by-zero exception.
        if (totalGames != 0) {
            avgGuess = (double) totalGuesses / totalGames;
        }
        //Provides stats to the player
        System.out.printf("The optimal number of guesses was: %.1f \n", OPTIMAL_GUESSES);
        System.out.printf("%s, it took you an average of %.1f guesses to find the secret number\n", name, avgGuess);
        //Different output is given based on how much better or worse player did in reference to optimal guess number.
        if (avgGuess > (OPTIMAL_GUESSES + OPTIMAL_GUESS_BUFFER)) {
            System.out.println("No offense, but you did pretty bad. :/");
        } else if (avgGuess > OPTIMAL_GUESSES) {
            System.out.println("Seriously? I know you can do better than that!");
        } else if (avgGuess > (OPTIMAL_GUESSES - OPTIMAL_GUESS_BUFFER)) {
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
     * Asks for the player's name. If they continue to give a blank name, they
     * will be called SecretAgent
     *
     * @param s Scanner object for reading input from the user
     * @return The name provided by the user or default name
     */
    private static String getPlayerName(Scanner s) {
        System.out.println("Hello there, what is your name?");
        int nameCounter = 0; //Counter increases if player enters blank name.
        String playerName;
        //Continues to ask for player name until 3 attempts are made.
        do {
            playerName = s.nextLine();
            if (playerName.isEmpty()) {
                System.out.println("I didn't catch that name, silent treatment huh? Try again");
                nameCounter += 1;
            }
        } while (playerName.isEmpty() && nameCounter < NUM_ATTEMPTS);
        //Gives player default name if too many blanks were entered.
        if (nameCounter >= NUM_ATTEMPTS) {
            System.out.println("Alright, you must be undercover huh?");
            playerName = "SecretAgent";
        }
        return playerName;
    }

    /**
     * Main functionality of the High-low game. Sass meter turned to the max.
     *
     * @param playerName Use the player name to personalize game prompts.
     * @param s          Scanner object for reading from the console.
     * @return The number of guesses that it took the player to guess the secret number.
     */
    private static int playHighLow(String playerName, Scanner s) {
        Random rand = new Random(System.currentTimeMillis()); //Initializes random object based on time.
        //Initializing variables used throughout the game.
        int numGuesses = 0;
        int userGuess = MIN_NUMBER;
        int upperBound = MAX_NUMBER;//Upper bound of player's previous guesses.
        int lowerBound = MIN_NUMBER;//Lower bound of player's previous guesses.
        boolean properInput; //Boolean to check if player entered proper guess.
        String playerInput;
        int secretNumber = rand.nextInt(MAX_NUMBER);//Initializes the secret number.
        //Asks the user for a guess of the secret number until they get it.
        do {
            numGuesses++;
            System.out.printf("%s, please enter integer guess %d(Hint: The max number is %d)\n",
                    playerName, numGuesses, MAX_NUMBER);
            //Tries to get player input, catching any non-integer or out-of-bound inputs.
            try {
                properInput = true;
                playerInput = s.nextLine();
                //Prompts user for input until non-blank guess is given.
                if (playerInput.isEmpty()) {
                    do {
                        System.out.println("Sorry? I didn't catch your guess.");
                        playerInput = s.nextLine();
                    } while (playerInput.isEmpty());
                }
                //Checks to see if input is an integer.
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
                //If input was improper, will skip high/low hints and ask for next guess.
                properInput = false;
            }
            if (properInput) {
                //Prompts user to enter a lower number
                if (userGuess > secretNumber) {
                    //Updates upper bound of user's previous guesses
                    if (userGuess < upperBound) {
                        upperBound = userGuess;
                    }
                    //Gives some sass if user's guess was higher than previous high guess.
                    if ((userGuess > upperBound)) {
                        System.out.println("Hey sweetie, if your last guess was too high, going higher won't help :)");
                    } else {
                        System.out.println("Your guess was too high.");
                    }
                }
                //Prompts user to enter a higher number
                if (userGuess < secretNumber) {
                    //Updates the lower bound of the user's previous guesses.
                    if (userGuess > lowerBound) {
                        lowerBound = userGuess;
                    }
                    //Gives some sass if user's guess was lower than previous low guess.
                    if (userGuess < lowerBound) {
                        System.out.println("Hey sweetie, if your last guess was too low, going lower won't help :)");
                    } else {
                        System.out.println("Your guess was too low.");
                    }
                }
            }

        } while (userGuess != secretNumber);
        //Gives nice output based on if player took reasonable number of guesses, otherwise the game gets sassy.
        if (numGuesses < (OPTIMAL_GUESSES + OPTIMAL_GUESS_BUFFER)) {
            System.out.println("Oh nice, you got it!");
        } else {
            System.out.println("Oh good, you finally got it. Took you long enough");
        }
        //Prints total number of guesses taken in game.
        System.out.println("It took you a total of " + numGuesses + " guesses to get the secret number.");
        return numGuesses;
    }

}
