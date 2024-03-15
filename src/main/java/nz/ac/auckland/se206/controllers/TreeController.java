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
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * Controller for the tree scene.
 *
 * <p>Handles the click and hover events for the interactable objects.
 */
public class TreeController {

  //  Interactable elements
  @FXML private ImageView note2;
  @FXML private ImageView back;

  // Timer element
  @FXML private Label timer;

  /**
   * Handles the click event for the note and collects the note.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickNote2(MouseEvent event) {
    // Update GameState
    GameState.note2Found = true;
    if (GameState.note1Found) {
      // reset current hint in rocket
      RocketController rocketController = (RocketController) SceneManager.getController("rocket");
      rocketController.resetCurrentHint();
    }
    ImageView image = (ImageView) event.getTarget();
    // Hide the note image and show the back button
    image.setVisible(false);
    back.setVisible(true);
    // If the notes have not been resolved, update the note in the HUD
    if (!GameState.isNotesResolved) {
      Hud.updateNote2(true, "x1");
    }
  }

  /**
   * Getter method for the timer label.
   *
   * @return the timer label.
   */
  public Label getTimer() {
    return timer;
  }

  /**
   * Handles the click event for the back button and returns to the pantry scene.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickBack(MouseEvent event) {
    App.setUi(AppUi.PANTRY_INTERIOR);
  }

  /**
   * Handles the hover event for the interactable objects and scales the image up.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the unhover event for the interactable objects and scales the image down.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleDown(image);
  }
}
