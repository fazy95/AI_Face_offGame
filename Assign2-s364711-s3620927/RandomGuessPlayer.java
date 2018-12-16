import java.io.*;

import java.util.Random;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Random guessing player. This player is for task B.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class RandomGuessPlayer implements Player {

    // Getting all the players and their attributes
    ArrayList<ArrayList<LinkedList<String>>> player = new ArrayList<ArrayList<LinkedList<String>>>();
    ArrayList<LinkedList<String>> allAttributes = new ArrayList<LinkedList<String>>();

    //Storing all the atrributes which are yet to be guess
    ArrayList<LinkedList<String>> Attributes = new ArrayList<LinkedList<String>>();

    // Used to store player's data
    ArrayList<LinkedList<String>> SelectedPlayer = new ArrayList<LinkedList<String>>();

    //Used to get currguess and iterating the guess
    int selIndex;
    String selVal;

    int attIndex;
    String selAt;

    /**
     * Loads the game configuration from gameFilename, and also store the chosen
     * person.
     *
     * @param gameFilename
     *            Filename of game configuration.
     * @param chosenName
     *            Name of the chosen person for this player.
     * @throws IOException
     *             If there are IO issues with loading of gameFilename. Note you
     *             can handle IOException within the constructor and remove the
     *             "throws IOException" method specification, but make sure your
     *             implementation exits gracefully if an IOException is thrown.
     */
    public RandomGuessPlayer(String gameFilename, String chosenName) throws IOException {
        LinkedList<String> currentAttribute = new LinkedList<String>();
        boolean checkPlayer = false;
        String currentLine ;

        try {
            // Reading from the configuration file
            BufferedReader br = new BufferedReader(new FileReader(gameFilename));
            while ((currentLine = br.readLine()) != null) {

                // Formatting String into array
                String[] attLine = currentLine.split(" ");

                // Getting all the attributes
                if (attLine.length > 1 && checkPlayer == false) {
                    for (int i = 0; i < attLine.length; i++) {
                        currentAttribute.add(attLine[i]);
                    }
                    Attributes.add(currentAttribute);
                    currentAttribute = new LinkedList<String>();
                }
                if (attLine.length == 1 && attLine[0].equals( chosenName)) {
                    // Total data is found at top of file, if we find player,
                    // close off the TotalAttributes
                    checkPlayer = true;

                    currentAttribute = new LinkedList<String>();
                    currentAttribute.add(attLine[0]);
                    SelectedPlayer.add(currentAttribute);
                    allAttributes.add(currentAttribute);
                    currentAttribute = new LinkedList<String>();

                    attLine = br.readLine().split(" ");

                    while (attLine.length == 2) {
                        for (int k = 0; k < attLine.length; k++) {
                            currentAttribute.add(attLine[k]);
                        }
                        SelectedPlayer.add(currentAttribute);
                        allAttributes.add(currentAttribute);
                        currentAttribute = new LinkedList<String>();
                        // Catches exception on last attempt here
                        attLine = br.readLine().split(" ");
                    }

                }

                // Getting the attributes of player
                if (attLine.length == 1 && attLine[0].contains("P")) {
                    checkPlayer = true;

                    currentAttribute = new LinkedList<String>();

                    currentAttribute.add(attLine[0]);
                    allAttributes.add(currentAttribute);

                    currentAttribute = new LinkedList<String>();

                    attLine = br.readLine().split(" ");

                    while (attLine.length == 2) {
                        for (int k = 0; k < attLine.length; k++) {
                            currentAttribute.add(attLine[k]);
                        }
                        allAttributes.add(currentAttribute);
                        currentAttribute = new LinkedList<String>();
                        // Catches exception on last attempt here
                        attLine = br.readLine().split(" ");
                    }

                    // putting the data to the player
                    player.add(allAttributes);

                    allAttributes = new ArrayList<LinkedList<String>>();
                }

            }
            br.close();
        } catch (Exception e) {
            // System.err.println(e);
        }
    } // end of RandomGuessPlayer()


    public Guess guess() {

        //Returning player if there is only one player left.
        if (player.size() == 1) {
            // Getting the name of the player
            String guessName = player.get(0).get(0).get(0);

            return new Guess(Guess.GuessType.Person, "", guessName);

        }

        if (Attributes.size() > 0) {
            // Getting the attributes
            attIndex = randInt(0, Attributes.size() - 1);
            selAt = Attributes.get(attIndex).get(0);

            selIndex = randInt(1, Attributes.get(attIndex).size() - 1);
            selVal = Attributes.get(attIndex).get(selIndex);

        }

        // Returning the guess attribute
        return new Guess(Guess.GuessType.Attribute, selAt, selVal);

    } // end of guess()

    public boolean answer(Guess currGuess) {

        // Checking if the attributes guessed is in the selected player.
        if (!currGuess.getType().equals(Guess.GuessType.Person)) {
            for (int i = 0; i < SelectedPlayer.size() - 1; i++) {

                if (SelectedPlayer.get(i).get(0).equals(currGuess.getAttribute())) {

                    if (SelectedPlayer.get(i).get(1).equals(currGuess.getValue())) {
                        return true;
                    }
                }
            }
        } else {

            if (SelectedPlayer.get(0).get(0).equals(currGuess.getValue())) {
                return true;
            }
        }

        return false;
    } // end of answer()

    public boolean receiveAnswer(Guess currGuess, boolean answer) {

        // Used to store the index of the players which are removed
        boolean Del[] = new boolean[player.size()];

        // if guess is of type player and correct returns true
        //Checking which player can be removed
        if (!currGuess.getType().equals(Guess.GuessType.Person)) {
            // looping on all the players
            for (int i = 0; i < player.size(); i++) {
                // looping on all the attributes of a player
                for (int j = 1; j < player.get(i).size(); j++) {

                    // Checking if the guess is correct
                    if (player.get(i).get(j).get(0).equals(currGuess.getAttribute())) {

                        // Cecking if the guess is matched with the guessing player
                        if (player.get(i).get(j).get(1).equals(currGuess.getValue())) {

                            if (!answer) {

                                Del[i] = true;
                            } else {

                                Del[i] = false;
                            }

                        } else {

                            if (answer) {
                                Del[i] = true;
                            }

                        }
                    }
                }

            }

            //Checking if the guess is correct
        } else {

            if (answer) {
                return true;
            }

            return false;


        }
        // Deleting players
        for (int i = (player.size() - 1); i >= 0; i--) {

            if (Del[i] == true) {

                player.remove(i);

            }
        }
        //Deleting the guess which was just made
        Attributes.get(attIndex).remove(selVal);

        //Checking if there are attributes left
        if (Attributes.get(attIndex).size() == 1) {

            Attributes.remove(attIndex);
        } else if (answer) {
            Attributes.remove(attIndex);
        }

        return false;

    } // end of receiveAnswer()

    // returns random number in range
    public static int randInt(int min, int max) {

        Random rand = new Random();

        //Getting a random int by declaring a bound
        int number;
        number= rand.nextInt((max - min) + 1) + min;

        return number;

    }

} // end of class RandomGuessPlayer
