// Alexander Wolf
// Word Search Generator
// 03/08/2022

package wordsearchgenerator; // part of wordsearchgenerator package

// import classes from Java library packages
import java.awt.*; // contains Point class
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List; // import List interface
import java.util.Random;
import java.util.Scanner;

// begin AWWordSearchGeneratorMain class
public class AWWordSearchGeneratorMain {
    // begin main method
    public static void main(String[] args) throws FileNotFoundException {
        // create WordSearchGenerator object
        WordSearchGenerator wordSearch = new WordSearchGenerator();
        wordSearch.printMenu();
    } // end main
} // end AWWordSearchGeneratorMain class

// begin WordSearchGenerator class
class WordSearchGenerator {
    // set two private 2 dimensional arrays as null, to be
    // created later
    private char[][] wordSearchPuzzle;
    private char[][] wordSearchSolution;
    // private ArrayList field to store Strings from user input
    private ArrayList<String> words;
    // private int to define word search dimensions
    private int dimensions;

    // begin constructor
    public WordSearchGenerator() {
        this.dimensions = Size.MEDIUM.getSize();
    } // end constructor

    // post: instructions have been printed to console
    public static void printIntro() {
        System.out.println("Welcome to my word search generator!\n" +
                "This program will allow you to generate your own " +
                "word search puzzle.\n" +
                "Please select an option:\n" +
                "\tGenerate a new word search (g)\n" +
                "\tPrint your own word search (p)\n" +
                "\tShow the solution to your word search (s)\n" +
                "\tPrint word search puzzle to file (f)\n" +
                "\tQuit the program (q)");
    } // end printIntro

    // post: user has entered words to be included in word search and
    // two word searches have been generated, one with random letters
    // and one showing solution to puzzle
    public void generate(Scanner console) {
        Random rand = new Random(); // create Random object
        boolean clear; // used to test clear path for words in puzzle

        // create word list from user input and store length of longest
        // word in list
        int max = createWordList(console);
        System.out.println();

        // add max word length value to W x H of 2D array
        dimensions = Size.MEDIUM.getSize() + max;
        // create 2 dimensional array for word search
        wordSearchPuzzle = new char[dimensions][dimensions];

        // create MatchingPoint object, has fields for Point x, y coordinates
        // of char match in 2 dimensional array, as well as index in String of
        // matching char
        MatchingPoint match;

        // random selection of word orientation
        // 0 = vertical, 1 = diagonal, 2/default = horizontal
        int orientation = rand.nextInt(3);

        // create point at origin
        Point p = new Point(0, 0);
        // place first word in upper left corner of puzzle
        placeWord(words.get(0), p, 0, orientation);

        // for every subsequent word in words list after first word
        for (int j = 1; j < words.size(); j++) {
            // select next orientation randomly
            orientation = rand.nextInt(3);
            // find first point where char from current word matches
            // char in 2 dimensional grid and return MatchingPoint object
            match = findFirstMatchingPoint(words.get(j));

            // current word being inspected
            String currWord;
            // reverse every third word before placing
            // in word search
            if (j % 3 == 0) {
                // current word being inspected
                currWord = reverse(words.get(j));
            } else {
                currWord = words.get(j);
            } // end if/else
            // Point where a char in current word matches char in 2 dimensional
            // array, Point x and y = -1 if no match found
            p = match.getPoint();
            // index of char in word which matches char in 2D array, -1 if no
            // match found
            int index = match.getIndex();
            // true if path for word in 2 dimensional array is clear
            clear = checkPath(currWord, p, index, orientation);

            // if match index does not = -1 (no match found) and path is clear
            if (match.getIndex() != -1 && clear) {
                // place word in 2 dimensional array
                placeWord(currWord, p, index, orientation);
            // if no match found or path is not clear
            } else {
                // will attempt to place word randomly in 2 dimensional array
                // 100 times, informing user if word could not be placed
                for (int i = 0; i <= 100; i++) {
                    // get new random word orientation
                    orientation = rand.nextInt(3);
                    // get random point in 2 dimensional array
                    p = getPoint(currWord, orientation);
                    // check if path for word is clear, and place if so
                    if (checkPath(currWord, p, 0, orientation)) {
                        placeWord(currWord, p, 0, orientation);
                        break; // break out of for loop after word placed
                    } // end if
                    if (i == 100) {
                        // notify user that word could not be placed after
                        // 100 attempts
                        notifyOfPlacementFailure(currWord, 100);
                    } // end if
                } // end for
            } // end if/else
        } // end for

        // create new 2 dimensional array which will show solution
        wordSearchSolution = new char[wordSearchPuzzle.length][dimensions];
        // copy contents of puzzle array to solution array
        for (int i = 0; i < wordSearchPuzzle.length; i++) {
            for (int j = 0; j < wordSearchPuzzle.length; j++) {
                wordSearchSolution[i] = wordSearchPuzzle[i].clone();
            } // end horizontal for
        } // end vertical for

        // populate empty elements in word search array with random
        // characters
        fillEmptySpaces(wordSearchPuzzle);
        // populate empty elements in solution key with X
        fillEmptySpaces(wordSearchSolution, '_');
    } // end generate

    // post: puzzle has been printed to output file
    public void print() {
        for (char[] arr: wordSearchPuzzle) {
            for (char c : arr) {
                System.out.printf(" %c ", c);
            } // end inner for
            System.out.println();
        } // end outer for
    } // end print

    // post: puzzle has been printed to output file
    public void print(char[][] wordSearch, PrintStream output) {
        for (char[] arr: wordSearch) {
            for (char c : arr) {
                output.printf(" %c ", c);
            } // end inner for
            output.println();
        } // end outer for
    } // end print

    // post: word search with random characters represented
    // as 'X' has been printed
    public void showSolution() {
        for (char[] arr: wordSearchSolution) {
            for (char c : arr) {
                System.out.printf(" %c ", c);
            } // end inner for
            System.out.println();
        } // end outer for
    } // end showSolution

    // pre: 0 <= direction <= 2
    // post: placement path for String word has been checked for
    // obstruction and boolean has been returned indicating state
    // of path
    public boolean checkPath(String word, Point p, int index, int direction) {
        switch (direction) {
            case 0: // vertical
                // traverse over word path vertically, checking if every index except
                // character intersection index is clear
                for (int i = 0; i < word.length(); i++) {
                    if (i == index) {
                        continue;
                    }
                    // if potential word placement would be out of array bounds or if
                    // path for word is not clear of other characters or if first char
                    // in String is not placed at empty index or index of matching char
                    if (p.y + (word.length() - index) > dimensions || p.y - index < 0
                            || wordSearchPuzzle[p.y - index + i][p.x] != 0
                            || (wordSearchPuzzle[p.y - index][p.x] != word.charAt(index)
                            && wordSearchPuzzle[p.y - index][p.x] != 0)) {
                        return false; // return and end method
                    } // end if
                } // end for
                break;

            case 1: // diagonal
                // traverse over word path diagonally, checking if every index except
                // character intersection index is clear
                for (int i = 0; i < word.length(); i++) {
                    if (i == index) {
                        continue;
                    }
                    // if potential word placement would be out of array bounds or if
                    // path for word is not clear of other characters or if first char
                    // in String is not placed at empty index or index of matching char
                    if (p.y + (word.length() - index) > dimensions || p.y - index < 0
                            || p.x + (word.length() - index) > dimensions || p.x - index < 0
                            || wordSearchPuzzle[p.y - index + i][p.x - index + i] != 0
                            || (wordSearchPuzzle[p.y - index][p.x - index] != word.charAt(index)
                            && wordSearchPuzzle[p.y - index][p.x - index] != 0)) {
                        return false; // return and end method
                    } // end if
                } // end for
                break;

            // only int 0-2 should ever be passed in
            default: // horizontal
                // traverse over word path horizontally, checking if every index except
                // character intersection index is clear
                for (int i = 0; i < word.length(); i++) {
                    if (i == index) {
                        continue;
                    }
                    // if potential word placement would be out of array bounds or if
                    // path for word is not clear of other characters or if first char
                    // in String is not placed at empty index or index of matching char
                    if (p.x + (word.length() - index) > dimensions || p.x - index < 0
                        || wordSearchPuzzle[p.y][p.x - index + i] != 0
                        || (wordSearchPuzzle[p.y][p.x - index] != word.charAt(index)
                            && wordSearchPuzzle[p.y][p.x - index] != 0)) {
                        return false; // return and end method
                    } // end if
                } // end for
                break;
        } // end switch
        return true; // returns true if path is clear
    } // end checkPath

    // pre: 0 <= orientation <= 2
    // post: word has been placed in clear spaces in 2D array
    public void placeWord(String word, Point p, int index, int orientation) {
        switch (orientation) {
            case 0: // vertical
                for (int i = 0; i < word.length(); i++) {
                    wordSearchPuzzle[p.y - index + i][p.x] = word.charAt(i);
                } // end for
                break;

            case 1: // diagonal
                for (int i = 0; i < word.length(); i++) {
                    wordSearchPuzzle[p.y - index + i][p.x - index + i]
                            = word.charAt(i);
                } // end for
                break;

            // only int 0-2 should ever be passed in
            default: // horizontal
                for (int i = 0; i < word.length(); i++) {
                    wordSearchPuzzle[p.y][p.x - index + i] = word.charAt(i);
                } // end for
                break;
        } // end switch
    } // end placeWord

    // pre: 0 <= orientation <= 2
    // point with x and y values within constraints determined by word
    // orientation has been returned
    public Point getPoint(String word, int orientation) {
        Random rand = new Random(); // create new Random object
        Point p = new Point(); // create new Point object
        switch (orientation) {
            case 0: // vertical
                p.x = rand.nextInt(dimensions);
                // limit y value range
                p.y = rand.nextInt(dimensions - word.length());
                break;

            case 1: // diagonal
                // limit both x and y value ranges
                p.x = rand.nextInt(dimensions - word.length());
                p.y = rand.nextInt(dimensions - word.length());
                break;

            // only int 0-2 should ever be passed in
            default: // horizontal
                // limit x value range
                p.x = rand.nextInt(dimensions - word.length());
                p.y = rand.nextInt(dimensions);
                break;
        } // end switch
        return p;
    } // end getPoint

    // post: MatchingPoint object which stores Point x, y coordinates
    // of first place char from String matches char in 2 dimensional
    // array, as well as index in String of matching char, has been
    // returned - if no match found, all MatchingPoint field values
    // = -1
    public MatchingPoint findFirstMatchingPoint(String word) {
        for (int i = 0; i < word.length(); i++) {
            for (int y = 0; y < wordSearchPuzzle.length; y++) {
                for (int x = 0; x < wordSearchPuzzle[y].length; x++) {
                    if (wordSearchPuzzle[y][x] == word.charAt(i)) {
                        Point p = new Point(x, y);
                        return new MatchingPoint(i, p);
                    }
                } // end horizontal for
            } // end vertical for
        } // end String traversal loop
        // if no matching char is found, returns -1 for all fields
        return new MatchingPoint(-1, new Point(-1, -1));
    } // end findMatchingPoints

    // post: user has been prompted to enter words and add to list from
    // which to populate word search
    public int createWordList(Scanner console) {
        // keep track of the longest word while populating
        // String array of words
        int max = 0;
        int numWords = 0; // word count

        boolean success = false;
        do { // prompt for valid word number value
            try {
                System.out.print("How many words would you like to " +
                        "include in your\nword search? ");
                String input = console.nextLine();
                // parse int from String input
                numWords = Integer.parseInt(input);
                success = true; // if no exception thrown, integer parsed
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.\n");
            } // end try/catch
        } while (!success); // loop while Exceptions caught

        // list will be populated with Strings from user input
        words = new ArrayList<>();

        // handle user input of each word
        for (int i = 0; i < numWords; i++) {
            System.out.printf("Please enter word number %d: ", i + 1);
            String newWord = console.next().toUpperCase();
            // add word from user input to ArrayList, upper case
            words.add(newWord);
        } // end for
        console.nextLine(); // consume newline character

        sort(words); // sort Strings in array in descending order of length
        for (String word : words) {
            // if longer than previous word, set new max word
            // length
            if (word.length() > max) {
                max = word.length();
            } // end if
        } // end for
        return max; // return length of longest word in list
    } // end createWordList method

    // post: Strings in array have been sorted by length using bubble
    // sort algorithm
    public void sort(List<String> words) {
        // pass over entire String ArrayList
        for (int i = 0; i < words.size(); i++) {
            // compare length of adjacent Strings
            for (int j = 0; j < words.size() - 1; j++) {
                if (words.get(j).length() < words.get(j + 1).length()) {
                    String temp = words.get(j); // store String temporarily
                    words.set(j, words.get(j + 1)); // switch Strings
                    words.set(j + 1, temp);
                } // end if
            } // end inner for
        } // end outer for
    } // end sort

    // post: empty spaces in 2 dimensional array reference parameter
    // have been filled in with character parameter
    public void fillEmptySpaces(char[][] arr, char c) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] == 0) {
                    arr[i][j] = c;
                } // end if
            } // end inner for
        } // end outer for
    } // end fillEmptySpaces

    // post: empty spaces in 2 dimensional array reference parameter
    // have been filled in with random characters
    public void fillEmptySpaces(char[][] arr) {
        Random rand = new Random(); // create new Random object

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] == 0) {
                    // random int in range of uppercase characters,
                    // cast to char
                    arr[i][j] = (char) (rand.nextInt(25) + 65);
                } // end if
            } // end inner for
        } // end outer for
    } // end fillEmptySpaces

    // post: String has been reversed and returned
    public String reverse(String s) {
        // create StringBuilder object to concatenate String
        StringBuilder reversedString = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            reversedString.append(s.charAt(i));
        } // end for
        return reversedString.toString();
    } // end reverse

    // post: method has returned true if word search puzzle
    // is empty
    public boolean isEmpty() {
        for (char[] chars : wordSearchPuzzle) {
            for (char aChar : chars) {
                if (aChar != 0) {
                    return false;
                }
            } // end inner for
        } // end outer for
        return true;
    } // end isEmpty

    // post: user has been notified that their word was not successfully
    // placed after the specified number of attempts
    public void notifyOfPlacementFailure(String word, int numTries) {
        System.out.println("The program tried to place the word "
                + word + "\n" + numTries + " times unsuccessfully.");
    } // end notifyOfPlacementFailure

    // post: list of words contained in puzzle has been printed
    public void printWordList(ArrayList<String> wordList) {
        System.out.println("Word list:");
        for (String word : wordList) {
            System.out.printf("\t%s\n", word);
        } // end for
    } // end word list

    // post: menu has displayed program functions to the user, handled
    // user input related to their menu choice, and continued to be
    // displayed until user has selected to end the program
    public void printMenu() throws FileNotFoundException {
        // true once a word search has been generated
        boolean generated = false;
        // create new Scanner object
        Scanner console = new Scanner(System.in);
        String selection; // initialize String for user selection
        // repeat menu display while user does not choose to quit
        do {
            printIntro();
            // user selection of choices from menu
            selection = console.nextLine();

            switch(selection) {
                case "g": // generate word search
                    generate(console);
                    generated = true;
                    break;

                case "p": // print word search
                    if (generated) {
                        print();
                    } else { // if word search has not yet been generated
                        System.out.println("You must first generate a "
                                + "word search.\n");
                    } // end if/else
                    System.out.println();
                    break;

                case "s": // print solution to word search
                    if (generated) {
                        // print list of words in puzzle
                        printWordList(words);
                        showSolution();
                    } else { // if word search has not yet been generated
                        System.out.println("You must first generate a "
                                + "word search.\n");
                    } // end if/else
                    break;

                case "f": // print to output file
                    if (generated) {
                        PrintStream output =
                                new PrintStream("wordsearchpuzzle.txt");
                        print(wordSearchPuzzle, output);
                        System.out.println("Your puzzle has been printed to " +
                                "a file named\n\"wordsearchpuzzle.txt\"\n");
                    } else { // if word search has not yet been generated
                        System.out.println("You must first generate a "
                                + "word search.\n");
                    } // end if/else
                    break;

                default:
                    // invalid input, do nothing
                    break;
            } // end switch
        } while (!selection.equals("q"));
    } // end printMenu

    // get methods for both word searches
    public char[][] getWordSearchPuzzle() {
        return wordSearchPuzzle;
    } // end getWordSearchPuzzle
    public char[][] getWordSearchSolution() {
        return wordSearchSolution;
    } // end getWordSearchSolution
} // end WordSearchGenerator class

// begin MatchingPoint class - store index in String and x,y
// coordinates in 2 dimensional array of intersections of Strings
class MatchingPoint {
    private int index;
    private Point p;

    // begin constructor
    public MatchingPoint(int index, Point p) {
        this.index = index;
        this.p = p;
    } // end constructor

    // get methods
    public int getIndex() {
        return index;
    } // end getIndex
    public Point getPoint() {
        return p;
    } // end getPoint

    // toString method
    public String toString() {
        return "[index = " + index + "; Point x value = "
                + p.x + ", Point y value = " + p.y + "]";
    } // end toString
} // end MatchingPoint

// begin Size enum - define constants for additional spaces
// to append to word search dimensions
enum Size {
    LARGE(8), MEDIUM(4), SMALL(2);

    int size; // initialize size int
    // constructor
    Size(int size) {
        this.size = size;
    } // end constructor

    // post: size int has been returned
    public int getSize() {
        return size;
    } // end getSize
} // end Size enum