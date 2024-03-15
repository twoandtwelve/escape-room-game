package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * Controller for the title screen.
 *
 * <p>Handles the click and key press events for the title screen.
 */
public class TitleController {

  // Title elements
  @FXML private Pane pane;
  @FXML private Label movingTextLabel;
  @FXML private Label movingTextCopyLabel;

  /** Initialises the title screen which is called as soon as the scene loads. */
  public void initialize() {
    pane.layoutBoundsProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Update window width when layout bounds change
              double windowWidth = newValue.getWidth();

              // Create a Timeline animation to move the text continuously
              Timeline timelineMain =
                  new Timeline(
                      new KeyFrame(
                          Duration.ZERO, new KeyValue(movingTextLabel.translateXProperty(), 0)),
                      new KeyFrame(
                          Duration.seconds(4),
                          new KeyValue(movingTextLabel.translateXProperty(), windowWidth)));

              timelineMain.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely

              // Create a second KeyFrame to reset the text position when it goes beyond the
              // window's width
              Timeline timelineCopy =
                  new Timeline(
                      new KeyFrame(
                          Duration.ZERO, new KeyValue(movingTextCopyLabel.translateXProperty(), 0)),
                      new KeyFrame(
                          Duration.seconds(4),
                          new KeyValue(movingTextCopyLabel.translateXProperty(), windowWidth)));

              timelineCopy.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely

              // Start the animation
              timelineMain.playFromStart(); // Ensure the animation starts from the beginning
              timelineCopy.playFromStart(); // Ensure the animation starts from the beginning
            });
  }

  /**
   * Handles the mouse click event and switches to the menu scene.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickMouse(MouseEvent event) {
    switchToMenu();
  }

  /**
   * Handles the key press event and switches to the menu scene.
   *
   * @param event the key event.
   */
  @FXML
  public void onPressKey(KeyEvent event) {
    switchToMenu();
  }

  /** Switches to the menu scene. */
  private void switchToMenu() {
    App.setUi(AppUi.MENU);
  }
}
