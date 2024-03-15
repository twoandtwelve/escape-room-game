package nz.ac.auckland.se206;

import nz.ac.auckland.se206.controllers.MainRoomController;
import nz.ac.auckland.se206.controllers.PantryController;
import nz.ac.auckland.se206.controllers.RocketController;

/** Represents the state of the game. */
public class GameState {

  public static boolean isGameActive = false;

  public static boolean toyFound = false;

  public static boolean torchFound = false;

  public static boolean footprintsFound = false;

  public static boolean note1Found = false;

  public static boolean note2Found = false;

  public static boolean isAnimationRunning = false;

  public static boolean isMemoryGameResolved = false;

  public static boolean isRecipeResolved = false;

  public static boolean isTorchOn = false;

  public static boolean isLeftMeowPadActivated = false;

  public static boolean isRightMeowPadActivated = false;

  public static boolean isNotesResolved = false;

  public static boolean isPantryFirstEntered = false;

  public static boolean isRocketFirstEntered = false;

  public static int hintsLeft;

  public static boolean isHintUsed = false;

  public static boolean isRiddleSolved = false;

  public static boolean isRiddleActive = false;

  public static boolean textToSpeech = false;

  /** Updates the hint labels of all scenes. */
  public static void updateAllHintsLabel() {
    // Get controllers for each scene
    MainRoomController mainRoomController =
        (MainRoomController) SceneManager.getController("mainroom");
    PantryController pantryController = (PantryController) SceneManager.getController("pantry");
    RocketController rocketController = (RocketController) SceneManager.getController("rocket");
    // Update the hint labels
    mainRoomController.updateHintsLabel();
    pantryController.updateHintsLabel();
    rocketController.updateHintsLabel();
  }
}
