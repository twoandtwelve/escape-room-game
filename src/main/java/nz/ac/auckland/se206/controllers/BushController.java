package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.HoverManager;
import nz.ac.auckland.se206.Hud;
import nz.ac.auckland.se206.Log;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.TextManager;

/**
 * Controller for the bush screen.
 *
 * <p>Handles the click event for the cat toy and note.
 */
public class BushController {
  @FXML private ImageView backButton;
  @FXML private ImageView catToy;
  @FXML private ImageView note1;

  // Timer element
  @FXML private Label timer;

  /**
   * Getter method for the timer.
   *
   * @return the timer.
   */
  public Label getTimer() {
    return timer;
  }

  /**
   * Handles the click event for the cat toy and collects the toy.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickToy(MouseEvent event) {
    // Update GameState
    GameState.toyFound = true;

    // Hides the toy image
    ImageView image = (ImageView) event.getTarget();
    image.setVisible(false);

    // Shows the back button if both the toy and note have been collected
    if (GameState.toyFound && GameState.note1Found) {
      backButton.setVisible(true);
    }

    // task 1 is complete
    Log.completeTask1();
  }

  /**
   * Handles the click event for the note and collects the note.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote1(MouseEvent event) {
    // Update GameState
    GameState.note1Found = true;

    // Reset current hint in rocket
    RocketController rocketController = (RocketController) SceneManager.getController("rocket");
    rocketController.resetCurrentHint();

    // Hides the note image
    ImageView image = (ImageView) event.getTarget();
    image.setVisible(false);
    if (!GameState.isNotesResolved) {
      // The note is hidden and the hud is updated to contain the note
      Hud.updateNote1(true, "x1");
    }
    // if statement ensures that the note and toy have been found before exit
    if (GameState.toyFound && GameState.note1Found) {
      backButton.setVisible(true);
    }
  }

  /**
   * Handles the click event for the back button which switches to the main room.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickBack(MouseEvent event) {
    if ((GameState.toyFound) && (GameState.note1Found)) {
      switchToMainRoom();
    }
  }

  /** Switches to the main room. */
  private void switchToMainRoom() {
    TextManager.close();
    App.setUi(AppUi.MAIN_ROOM);
  }

  /**
   * Handles the hover event for the interactable objects.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the leave event for the interactable objects.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleDown(image);
  }
}
