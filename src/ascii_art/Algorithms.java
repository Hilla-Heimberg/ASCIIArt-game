// USER_NAME = hilla_heimberg
// ID - 208916221

package ascii_art;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Class which contains solutions for algorithms questions.
 * @author Hilla Heimberg
 */
public class Algorithms {

    /**
     * Returns the number which repeats twice (or more) in the array.
     * @param numList the numbers array.
     * @return the number which repeats twice or more
     */
    public static int findDuplicate(int[] numList){
        // we will use "Floyd’s cycle-finding algorithm" in order to solve this question
        int slowTurtle = numList[0];
        int fastRabbit = numList[slowTurtle];

        // first - we will check if there is a "circle" in the numbers array by the known trick of "the
        // turtle and the rabbit"
        while (fastRabbit != slowTurtle) {
            slowTurtle = numList[slowTurtle];
            fastRabbit = numList[numList[fastRabbit]];
        }

        // second - we will find the number which repeats twice or more
        slowTurtle = 0;
        while (fastRabbit != slowTurtle) {
            slowTurtle = numList[slowTurtle];
            fastRabbit = numList[fastRabbit];
        }
        return slowTurtle;
    }

    /**
     * Returns the amounts of unique Morse representations.
     * @param words array of English words.
     * @return amounts of unique Morse representations.
     */
    public static int uniqueMorseRepresentations(String[] words){
        String[] morseArray = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.- ",
                ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
                "-.--", "--.."};

        char[] abcArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        HashMap<Character, String> mappingFromCharToMorse = new HashMap<>();
        // Matching between regular char to Morse char
        for (int index = 0; index < abcArray.length; index++) {
            mappingFromCharToMorse.put(abcArray[index] ,morseArray[index]);
        }

        HashSet<String> setOfMorseTranslations = new HashSet<>();
        // Matching between word to the Morse translation
        for (String word : words){
            String translation = "";
            for (int index = 0; index < word.length(); index++){
                translation = translation.concat(mappingFromCharToMorse.get(word.charAt(index)));
            }
            setOfMorseTranslations.add(translation); // add the translation to the set
        }

        // the size of the set is the amount of unique Morse representations
        return setOfMorseTranslations.size();
    }
}
