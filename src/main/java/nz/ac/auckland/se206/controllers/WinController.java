package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.CountDownTimer;
import nz.ac.auckland.se206.HoverManager;
import nz.ac.auckland.se206.TextManager;

/**
 * Controller for the win screen.
 *
 * <p>Handles the click and hover events for the exit button.
 */
public class WinController {

  @FXML private ImageView exit;
  @FXML private Label result;
  @FXML private Label wonLabel;
  @FXML private Label taskLabel;

  /**
   * Getter method for the result label.
   *
   * @return the result label.
   */
  public Label getResult() {
    return result;
  }

  /**
   * Getter method for the won label.
   *
   * @return the won label.
   */
  public Label getWonLabel() {
    return wonLabel;
  }

  /**
   * Getter method for the task label.
   *
   * @return the task label.
   */
  public Label getTaskLabel() {
    return taskLabel;
  }

  /**
   * Handles the click event for the exit button and closes the application.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickExit(MouseEvent event) {
    // Terminate text to speech
    TextManager.close();
    // Terminate the timer
    if (CountDownTimer.countdownTimeline != null) {
      CountDownTimer.countdownTimeline.stop();
    }

    // Close the application
    Stage stage = (Stage) exit.getScene().getWindow();
    stage.close();
  }

  /**
   * Handles the hover event for the exit button and scales the image up.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the unhover event for the exit button and scales the image down.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleDown(image);
  }
}
