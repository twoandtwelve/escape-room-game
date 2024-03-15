package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Stores the correct sequence of buttons and the player sequence of buttons.
 *
 * <p>Stores the correct sequence of buttons and the player sequence of buttons. The correct
 * sequence is initialised with 5 random integers from 1 - 16. The player sequence is cleared when
 * the player loses or wins.
 */
public class ButtonSequence {
  // Stores the correct sequence of buttons and the player sequence of buttons
  public static ArrayList<Integer> correctSequence = new ArrayList<Integer>();
  public static ArrayList<Integer> playerSequence = new ArrayList<Integer>();

  /** Initialises the correct sequence with 5 random integers. */
  public static void initialiseCorrectSequence() {
    for (int i = 0; i < 5; i++) {
      // random integer from 1 - 16
      int randomInt = ThreadLocalRandom.current().nextInt(1, 17);
      correctSequence.add(randomInt);
    }
  }

  /**
   * Adds integer to the player sequence.
   *
   * @param num the integer to add.
   */
  public static void add(int num) {
    playerSequence.add(num);
  }

  /** Method to clear the player sequence of the class. */
  public static void clear() {
    playerSequence.clear();
  }
}
