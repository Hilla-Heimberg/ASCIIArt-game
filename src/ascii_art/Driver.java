// USER_NAME = hilla_heimberg
// ID - 208916221

package ascii_art;
import image.Image;
import java.util.logging.Logger;

/**
 * This class is the Driver class of the project.
 * It has the main method which runs this all project.
 * @author Hilla Heimberg
 */
public class Driver {
    // --------------------------------- STRINGS FOR GAME ---------------------------------
    private static final String USAGE_INVALID_INPUT_ERROR_MESSAGE = "USAGE: java asciiArt ";
    private static final String FAILED_TO_OPEN_IMAGE_ERROR_MESSAGE = "Failed to open image file ";

    // -------------------------------------- METHODS --------------------------------------
    /**
     * The main function of this project.
     * @param args arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println(USAGE_INVALID_INPUT_ERROR_MESSAGE);
            return;
        }
        Image img = Image.fromFile(args[0]);
        if (img == null) {
            Logger.getGlobal().severe(FAILED_TO_OPEN_IMAGE_ERROR_MESSAGE + args[0]);
            return;
        }
        new Shell(img).run();
    }
}
