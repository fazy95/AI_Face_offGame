import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Binary-search based guessing player.
 * This player is for task C.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class BinaryGuessPlayer implements Player {

    // Possible people Mapping is declared when traversing through the configuration.
    HashMap<String, Integer> possPeopMap = new HashMap<String, Integer>();

    // Array List to get possible number of players remaining
    ArrayList<String> possNm = new ArrayList<String>();

    // Array List for player's attributes
    ArrayList<LinkedList<String>> attrb = new ArrayList<LinkedList<String>>();

    // Array List for player's personal attributes
    ArrayList<LinkedList<String>> SelectedPlayer = new ArrayList<LinkedList<String>>();

    // Arrya List for getting total attributes
    ArrayList<LinkedList<String>> Attributes = new ArrayList<LinkedList<String>>();

    // Attribute's distance is initially set to 0
    int attDist = 0;



    /**
     * Loads the game configuration from gameFilename, and also store the chosen
     * person.
     *
     * @param gameFilename Filename of game configuration.
     * @param chosenName Name of the chosen person for this player.
     * @throws IOException If there are IO issues with loading of gameFilename.
     *    Note you can handle IOException within the constructor and remove
     *    the "throws IOException" method specification, but make sure your
     *    implementation exits gracefully if an IOException is thrown.
     */
    public BinaryGuessPlayer(String gameFilename, String chosenName)
            throws IOException
    {

        int pointer = 0; // setting the pointer initially to 0 player attributes

        boolean fPlyr = false; // Player found is initially set to false
        LinkedList<String> nwAtt = new LinkedList<String>();
        String curLne; // declaring the current line


        try
        {
            BufferedReader buffRdr = new BufferedReader(new FileReader(gameFilename)); // buffer reader for reading the file
            while ((curLne = buffRdr.readLine()) != null)
            {
                String[] curAttLne = curLne.split(" "); // Using 'split' to format array

                // Adding total attributes
                if (curAttLne.length > 1 && fPlyr == false) {
                    for (int i = 0; i < curAttLne.length; i++) {
                        nwAtt.add(curAttLne[i]);
                    }
                    Attributes.add(nwAtt);
                    nwAtt = new LinkedList<String>();
                }

                // Adding player personal attributes
                if (curAttLne.length == 1 && curAttLne[0].equals(chosenName)) {
                    nwAtt = new LinkedList<String>();
                    fPlyr = true;
                    possNm.add(curAttLne[0]);
                    SelectedPlayer.add(nwAtt);
                    nwAtt.add(curAttLne[0]);
                    attrb.add(nwAtt);
                    // Adds the name of possible player left
                    possPeopMap.put(curAttLne[0], pointer);
                    nwAtt = new LinkedList<String>();
                    curAttLne = buffRdr.readLine().split(" ");
                    pointer++;
                    while (curAttLne.length == 2) {
                        for (int i = 0; i < curAttLne.length; i++) {
                            nwAtt.add(curAttLne[i]);
                        }
                        attrb.add(nwAtt);
                        SelectedPlayer.add(nwAtt);
                        nwAtt = new LinkedList<String>();
                        curAttLne = buffRdr.readLine().split(" ");
                        pointer++;
                    }
                }

                // Adds player attributes
                if (curAttLne.length == 1 && curAttLne[0].contains("P")) {

                    //If player found then close
                    fPlyr = true;

                    // add player name to possible player left
                    possPeopMap.put(curAttLne[0], pointer);
                    possNm.add(curAttLne[0]);

                    nwAtt = new LinkedList<String>();
                    nwAtt.add(curAttLne[0]);
                    attrb.add(nwAtt);
                    nwAtt = new LinkedList<String>();
                    curAttLne = buffRdr.readLine().split(" ");
                    pointer++;
                    while (curAttLne.length == 2) {
                        for (int i = 0; i < curAttLne.length; i++) {
                            nwAtt.add(curAttLne[i]);
                        }
                        attrb.add(nwAtt);
                        nwAtt = new LinkedList<String>();
                        // Catches exception on last attempt here
                        curAttLne = buffRdr.readLine().split(" ");
                        pointer++;
                    }

                }

            }

            buffRdr.close();

        } catch (Exception e) {
        }
    } // end of BinaryGuessPlayer()

    public Guess guess() {
        String commAtt = null;
        String commVal = null;
        int commNum = 0;

        if (possNm.size() > 1) {
            if (attDist == 0) {
                attDist = (possPeopMap.get(possNm.get(1)) - possPeopMap.get(possNm.get(0)));
            }
            try {
                String current;
                for (int i = 1; i < attDist; i++) {
                    int nameIndex = 0;
                    String nwAtt[] = new String[possNm.size()];
                    for (int loop = 0; loop <= possNm.size() - 1; loop++) {
                        nwAtt[loop] = attrb
                                .get(possPeopMap.get(possNm.get(nameIndex)) + i).get(1);
                        if (nameIndex < possNm.size() - 1) {
                            nameIndex++;
                        }
                    }
                    HashMap<String, Integer> comAtt = getcomAtt(nwAtt);
                    String fVal = null;
                    for (String key : comAtt.keySet()) {
                        fVal = key;
                    }
                    if (comAtt.get(fVal) > commNum
                            && comAtt.get(fVal) != possNm.size()) {

                        // Set the attribute
                        commAtt = attrb.get(possPeopMap.get(possNm.get(0)) + i)
                                .get(0);
                        // Set the value
                        commVal = fVal;
                        // Number of times the value is found
                        commNum = comAtt.get(fVal);
                    }
                }

                // placeholder, replace
            } catch (Exception e) {

            }

            return new Guess(Guess.GuessType.Attribute, commAtt, commVal);
        }
        return new Guess(Guess.GuessType.Person, "", possNm.get(0));
    } // end of guess()

    public boolean answer(Guess currGuess) {

        if (!currGuess.getType().equals(Guess.GuessType.Person)) {
            for (int i = 0; i < SelectedPlayer.size() - 1; i++) {

                if (SelectedPlayer.get(i).get(0).equals(currGuess.getAttribute())) {

                    if (SelectedPlayer.get(i).get(1).equals(currGuess.getValue())) {
                        return true;
                    }

                }
            }
            return false;

        } else {
            if (SelectedPlayer.get(0).get(0).equals(currGuess.getValue())) {
                return true;
            }
        }
        // placeholder, replace
        return false;
    } // end of answer()

    public boolean receiveAnswer(Guess currGuess, boolean answer) {
        String curPlyr = null;
        // Check if guess was a person
        if (currGuess.getType() == Guess.GuessType.Person) {
            if (answer == true) {
                return true;
            }
            return false;
        }
        else {
            // Remove the player that has same attribute
            if (answer == true) {

                for (int i = 0; i < attrb.size() - 1; i++) {
                    if (attrb.get(i).size() == 1 & attrb.get(i).get(0).contains("P")) {
                        curPlyr = attrb.get(i).get(0);
                    }
                    if (attrb.get(i).get(0).equals(currGuess.getAttribute())) {
                        if (!attrb.get(i).get(1).equals(currGuess.getValue())) {
                            delPlayerr(curPlyr);
                        }
                    }
                }

            }
            else {

                for (int i = 0; i < attrb.size() - 1; i++) {
                    if (attrb.get(i).size() == 1 & attrb.get(i).get(0).contains("P")) {
                        curPlyr = attrb.get(i).get(0);
                    }
                    if (attrb.get(i).get(0).equals(currGuess.getAttribute())) {
                        if (attrb.get(i).get(1).equals(currGuess.getValue())) {
                            delPlayerr(curPlyr);
                        }
                    }
                }
            }

            return false;
        }

    } // end of receiveAnswer()

    public HashMap<String, Integer> getcomAtt(String[] attributes) {
        HashMap<String, Integer> comAtt = new HashMap<String, Integer>();
        int fnlCnt =0;
        int count;
        String fnlVal;
        String curVal = null;

        // Two player has same attributes
        if (attributes.length == 2) {
            if (attributes[0].equals(attributes[1])) {
                comAtt.put(attributes[0], 2);
                return comAtt;
            } else {
                comAtt.put(attributes[0], 1);
                return comAtt;
            }
        }
        for (int i = 0; i < attributes.length - 1; i++) {
            count = 0;
            curVal = attributes[i];

            if (i == 0) {
                count = 1;
            }
            for (int loop = 0; loop < attributes.length - 1; loop++) {
                if (curVal.equals(attributes[loop])) {
                    count++;
                }
            }
            if (count > fnlCnt) {
                fnlCnt = count;
                fnlVal = curVal;
            }
        }
        comAtt.put(curVal, fnlCnt);
        return comAtt;

    }

    //Deleting Player
    public void delPlayerr(String curPlyr) {
        for (int i = 0; i < possNm.size(); i++) {
            if (possNm.get(i).equals(curPlyr)) {
                possNm.remove(i);
            }
        }
        possPeopMap.remove(curPlyr);

    }
} // end of class BinaryGuessPlayer
