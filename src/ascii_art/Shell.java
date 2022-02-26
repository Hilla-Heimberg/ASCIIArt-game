// USER_NAME = hilla_heimberg
// ID - 208916221

package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class is the Shell class.
 * This class will perform all interface operations and will be operated by the "Driver" class.
 *
 * @author Hilla Heimberg
 */
public class Shell {
    // -------------------------------------- PRIVATE -------------------------------------
    // CONSTANTS
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int RESOLUTION_FACTOR = 2;
    private static final int LENGTH_FOR_RANGE = 3;

    private final Set<Character> charSet = new HashSet<>();
    private final BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;
    private int charsInRow;
    private final int minCharsInRow;
    private final int maxCharsInRow;

    // --------------------------------- STRINGS FOR GAME ---------------------------------
    private static final String INITIAL_CHARS_RANGE = "0-9";
    private static final char HYPHEN_SYMBOL = '-';
    private static final char SPACE_SYMBOL = ' ';
    private static final char ALL_SYMBOL = '~';
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final String WIDTH_SET_MESSAGE = "Width set to ";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String RES = "res";
    private static final String CHARS = "chars";
    private static final String RENDER = "render";
    private static final String CONSOLE = "console";
    private static final String SPACE = "space";
    private static final String ALL = "all";
    private static final String CMD_EXIT = "exit";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String ARROWS_INPUT = ">>> ";
    private static final String REGEX_FOR_SPLIT = "\\s+";
    private static final String EMPTY_STRING = "";

    // MESSAGES
    private static final String MAX_RESOLUTION_ERR_MSG = "You're using the maximum resolution";
    private static final String MIN_RESOLUTION_ERR_MSG = "You're using the minimum resolution";
    private static final String INVALID_INPUT_MESSAGE = "Your input is invalid, please try again :)";
    private static final String PARAMETER_MISSING_ERROR_MESSAGE = "Parameter is missing, please try again :)";

    // -------------------------------------- METHODS --------------------------------------

    /**
     * Constructor. Construct the "Shell" application.
     *
     * @param img the image for the ascii process
     */
    public Shell(Image img) {
        addOrRemoveChars(INITIAL_CHARS_RANGE, ADD);
        this.minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        this.output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
    }

    /*
    Responsible for printing the ascii image to the HTML.
     */
    private void render() {
        if (!(charSet.isEmpty())) {
            char[][] chars = charMatcher.chooseChars(charsInRow, charSet.toArray(new Character[0]));
            this.output.output(chars);
        }
    }

    /*
    Changes the printing to be in the console. The ascii image will be printed in the console from now on.
     */
    private void console() {
        this.output = new ConsoleAsciiOutput();
    }

    /*
     Changes the resolution in accordance to the commit "res up"/"res down". In addition, prints the value
     each iteration.
     */
    private void resChange(String s) {
        if (s.equals(UP)) {
            if (!(changeResolution(this.charsInRow * RESOLUTION_FACTOR <= this.maxCharsInRow,
                    this.charsInRow * RESOLUTION_FACTOR, MAX_RESOLUTION_ERR_MSG))) {
                return;
            }
        } else if (s.equals(DOWN)) {
            if (!(changeResolution(this.charsInRow / RESOLUTION_FACTOR >= this.minCharsInRow,
                    this.charsInRow / RESOLUTION_FACTOR, MIN_RESOLUTION_ERR_MSG))) {
                return;
            }
        } else {
            System.out.println(INVALID_INPUT_MESSAGE);
            return;
        }
        System.out.println(WIDTH_SET_MESSAGE + charsInRow);
    }

    /*
    Changes the resolution by updating the "charsInRow" field (multiply for "UP", division for "DOWN").
    If we got to the maximum or to the minimum - it will print error message for the user.
     */
    private boolean changeResolution(boolean canChangeResolution, int updatedValueForResolution,
                                     String resolutionErrorMessage) {
        if (canChangeResolution) {
            this.charsInRow = updatedValueForResolution;
            return true;
        } else {  // we got to the maximum or to the minimum
            System.out.println(resolutionErrorMessage);
            return false;
        }
    }

    /*
    Shows the chars which exists in the charSet by printing them to the console.
     */
    private void showChars() {
        this.charSet.stream().sorted().forEach(c -> System.out.print(c + " "));
        System.out.println();
    }

    /*
    The main method of the "shell" application. Receives the user input, checks if the input is valid and
    handles the user commands by calling other methods.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(ARROWS_INPUT);
        String cmd = scanner.nextLine().trim();

        String[] words = cmd.split(REGEX_FOR_SPLIT);
        while (!cmd.equals(CMD_EXIT)) {
            if (!words[0].equals(EMPTY_STRING)) {
                if (words.length == 1) {
                    handleShortCommand(cmd);
                }
                if (words.length > 1) {
                    handleLongCommand(words);
                }
            }
            System.out.print(ARROWS_INPUT);
            cmd = scanner.nextLine().trim();
            words = cmd.split(REGEX_FOR_SPLIT);
        }
    }

    /*
    Handles the user commands that are from the shape "add _", "remove _", "res _" (The long commands).
    The commands must be in length 2.
     */
    private void handleLongCommand(String[] words) {
        String param = words[1];

        if (!(words.length == 2)) { // if the input not in length 2 - invalid input
            System.out.println(INVALID_INPUT_MESSAGE);
            return;
        }

        switch (words[0]) {
            case REMOVE:
            case ADD:
                addOrRemoveChars(param, words[0]);
                break;
            case RES:
                resChange(param);
                break;
            default: // if the command is invalid
                System.out.println(INVALID_INPUT_MESSAGE);
        }
    }

    /*
    Handles the following user commands- "chars", "render", "console" (The short commands).
    The commands must be in length 1.
     */
    private void handleShortCommand(String cmd) {
        switch (cmd) {
            case CHARS:
                showChars();
                break;
            case RENDER:
                render();
                break;
            case CONSOLE:
                console();
                break;
            default: // if the command is invalid
                // if the command is from the form "add"/"remove"/"res" and parameter is missing after that
                if (cmd.equals(ADD) || cmd.equals(REMOVE) || cmd.equals(RES)) {
                    System.out.println(PARAMETER_MISSING_ERROR_MESSAGE);
                    break;
                }
                System.out.println(INVALID_INPUT_MESSAGE);
        }
    }

    /*
    Add or remove chars from the charSet.
     */
    private void addOrRemoveChars(String s, String command) {
        char[] range = parseCharRange(s);
        if (range != null) {
            if (command.equals(REMOVE)) { // remove all range from charSet
                Stream.iterate(range[0], c -> c <= range[1], c ->
                        (char) ((int) c + 1)).forEach(this.charSet::remove);
            } else if (command.equals(ADD)) { // add all range to charSet
                Stream.iterate(range[0], c -> c <= range[1], c ->
                        (char) ((int) c + 1)).forEach(this.charSet::add);
            }
        } else {
            System.out.println(INVALID_INPUT_MESSAGE);
        }
    }

    /*
    Returns array contains chars accordance to the input command of the user. This method will be called if
     the user-input will be in form "add _" / "remove _".
     */
    private static char[] parseCharRange(String param) {
        if (param.equals(SPACE)) {
            return new char[]{SPACE_SYMBOL, SPACE_SYMBOL};
        }

        if (param.equals(ALL)) {
            return new char[]{SPACE_SYMBOL, ALL_SYMBOL};
        }

        if (param.length() == 1) { // one char only
            char singleChar = param.charAt(0);
            return new char[]{singleChar, singleChar};
        }

        // if the param from the form "char-char" (RANGE)
        if (param.length() == LENGTH_FOR_RANGE && param.charAt(1) == HYPHEN_SYMBOL) {
            char startChar = (char) Math.min(param.charAt(0), param.charAt(2));
            char endChar = (char) Math.max(param.charAt(0), param.charAt(2));
            return new char[]{startChar, endChar};
        }

        return null;
    }
}