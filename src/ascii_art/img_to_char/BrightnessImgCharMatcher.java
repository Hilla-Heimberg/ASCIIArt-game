// USER_NAME = hilla_heimberg
// ID - 208916221

package ascii_art.img_to_char;

import image.Image;
import java.awt.*;
import java.util.HashMap;

/**
 * This class is the BrightnessImgCharMatcher class.
 * It's responsible for the all logic of converting color image to ascii image.
 * @author Hilla Heimberg
 */
public class BrightnessImgCharMatcher {
    // -------------------------------------- PRIVATE -------------------------------------
    // CONSTANTS
    private static final int NUM_OF_PIXELS = 16;
    private static final int ILLUMINATE_MAX_VALUE = 255;
    private static final double CONVERT_RED_VALUE = 0.2126;
    private static final double CONVERT_GREEN_VALUE = 0.7152;
    private static final double CONVERT_BLUE_VALUE = 0.0722;

    private final HashMap<Image, Double> cache = new HashMap<>();
    private final Image image;
    private final String fontChar;
    // -------------------------------------- METHODS --------------------------------------

    /**
     * Constructor. Construct a new BrightnessImgCharMatcher.
     * @param image    the image for the process
     * @param fontChar the font of the char
     */
    public BrightnessImgCharMatcher(Image image, String fontChar) {
        this.image = image;
        this.fontChar = fontChar;
    }

    /**
     * This method gets number of character for each line and array of chars and returns array of ascii
     * characters which representing image.
     * @param numCharsInRow the number of character for each line in the ascii image.
     * @param charSet       array of chars that can be assembled from it an ascii image.
     * @return a two-dimensional array of ascii characters which representing the image we got in the
     * constructor.
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        if (!(charSet.length == 0)){
            double[] arrayAfterProcess = brightnessForChar(charSet);
            char[][] asciiArt = convertImageToAscii(arrayAfterProcess, numCharsInRow, charSet);
            return asciiArt;
        }
        return new char[0][0]; //else -  if the charSet is empty
    }

    /*
    This method returns array which contains the brightness values for each char from the charSet.
     */
    private double[] brightnessForChar(Character[] charSet) {
        int numOfChars = charSet.length;
        double[] arrOfBrightnessValues = new double[numOfChars];

        for (int index = 0; index < numOfChars; index++) {
            double valueOfBrightness = calculationBrightnessValue(charSet[index]);
            arrOfBrightnessValues[index] = valueOfBrightness;
        }

        return normalizationForBrightness(arrOfBrightnessValues);
    }

    /*
    This method calculate the brightness value for each character.
     */
    private double calculationBrightnessValue(char character) {
        double numOfWhitePixels = 0;
        boolean[][] arrOfPixels = CharRenderer.getImg(character, NUM_OF_PIXELS, this.fontChar);

        for (int row = 0; row < arrOfPixels[0].length; row++) {
            for (int column = 0; column < arrOfPixels[0].length; column++) {
                if (arrOfPixels[row][column]) {
                    numOfWhitePixels++;
                }
            }
        }
        double valueOfBrightness = numOfWhitePixels / (NUM_OF_PIXELS * NUM_OF_PIXELS);
        return valueOfBrightness;
    }


    /*
    This method normalizes the brightness values in the array, by formula of linear stretching.
     */
    private double[] normalizationForBrightness(double[] arrOfBrightnessValues) {
        double[] normalizationValuesArray = new double[arrOfBrightnessValues.length];
        double maxValue = 0, minValue = 1;
        for (double valueOfChar : arrOfBrightnessValues) {
            if (valueOfChar > maxValue) {
                maxValue = valueOfChar;
            }
            if (valueOfChar < minValue) {
                minValue = valueOfChar;
            }
        }

        // if those values are equal - we can return an array contains 0 or 1. Without limiting generality-0.
        if (maxValue == minValue) {
            return new double[arrOfBrightnessValues.length];
        }

        linearStretching(arrOfBrightnessValues, normalizationValuesArray, maxValue, minValue);
        return normalizationValuesArray;
    }

    /*
    This methods responsible for the linear stretching formula and runs it on each value in the values array.
     */
    private void linearStretching(double[] arrOfBrightnessValues, double[] normalizationValuesArray,
                                  double maxValue, double minValue) {
        for (int index = 0; index < arrOfBrightnessValues.length; index++) {
            double formulaForNormalization =
                    (arrOfBrightnessValues[index] - minValue) / (maxValue - minValue);
            normalizationValuesArray[index] = formulaForNormalization;
        }
    }

    /*
    This method converts image to ascii.
    It will divide the image into sub-images and for each sub-image it will match the character with the
    closest brightness level.
     */
    private char[][] convertImageToAscii(double[] brightnessValue, int numCharsInRow, Character[] charSet) {
        int pixels = this.image.getWidth() / numCharsInRow;
        int rowsInAsciiArt = this.image.getHeight()/pixels, columnsInAsciiArt = this.image.getWidth()/pixels;
        char[][] asciiArt = new char[rowsInAsciiArt][columnsInAsciiArt];

        int row = 0, column = 0;
        // for each sub image - we want to find the closest value to the value average and to match between
        // the closest value to the suitable character.
        for (Image subImage : this.image.squareSubImagesOfSize(pixels)) {
            int updatedIndex = checksClosestValue(brightnessValue, subImage);
            asciiArt[row][column] = charSet[updatedIndex];
            // updating the indexes for the asciiArt
            if (column >= columnsInAsciiArt-1) {
                column = 0;
                row ++;
            }
            else {
                column ++;
            }
        }

        return asciiArt;
    }

    /*
    This method checks for the closest value to the average value of brightness.
     */
    private int checksClosestValue(double[] brightnessValue, Image subImage) {
        double closestToValue = 1;
        int updatedIndex = 0;
        double averageValue;

        if (this.cache.containsKey(subImage)) {  // if we already have the value of this sub-image
            averageValue =  this.cache.get(subImage); // we can save runtime
        }

        else {
            averageValue = calculationBrightnessAverage(subImage);
        }

        for (int index = 0; index < brightnessValue.length; index++){
            // we will find the closest value to the average by the absolute value
            if (Math.abs(averageValue - brightnessValue[index]) <= closestToValue){
                closestToValue = Math.abs(averageValue - brightnessValue[index]);
                updatedIndex = index;
            }
        }
        return updatedIndex;
    }

    /*
    This method calculates the average value of the pixel brightness in the image, by formula.
     */
    private double calculationBrightnessAverage(Image image) {
        double sum = 0;
        for (Color pixel : image.pixels()) {
            // the formula for converting color pixel to grey pixel
            sum += (pixel.getRed() * CONVERT_RED_VALUE + pixel.getGreen() * CONVERT_GREEN_VALUE +
                    pixel.getBlue() * CONVERT_BLUE_VALUE) / ILLUMINATE_MAX_VALUE;
        }

        int numOfPixels = image.getWidth() * image.getHeight();
        double averageValue = sum/numOfPixels;

        cache.put(image, averageValue);
        return averageValue;
    }
}
