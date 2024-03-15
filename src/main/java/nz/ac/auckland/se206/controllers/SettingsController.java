package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;

/**
 * Controller for the settings screen Manages user preferences and settings.
 *
 * <p>Handles the click event for the back button.
 */
public class SettingsController {
  // Stores the volume
  private static double volume = 1;

  /**
   * Getter method for the volume.
   *
   * @return the volume.
   */
  public static double getVolume() {
    return volume;
  }

  // Settings elements
  @FXML private Button applyButton;
  @FXML private Button backButton;
  @FXML private TextArea chatBox;

  // Preference elements
  @FXML private ToggleButton toggleButton;
  @FXML private Slider volumeSlider;

  // Timer
  @FXML private Label timer;

  /**
   * Initializes the settings screen.
   *
   * <p>Sets the volume slider to 1 and the toggle button to off.
   */
  public void initialize() {
    volumeSlider.setValue(1);
    // set the toggle (TextToSpeech) as off
    toggleButton.setSelected(false);
  }

  /**
   * Getter method for the timer label.
   *
   * @return the timer label.
   */
  public Label getTimer() {
    return timer;
  }

  // to ensure that back goes to the scene it was just in, use the getPreviousScene method when
  // onClickBack is done

  /**
   * Handles the click event for the back button and returns to the previous scene.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickBack(MouseEvent event) {
    App.setUi(SceneManager.getPreviousScene());
  }

  /**
   * Handles the click event for the apply button and applies the settings.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickApply(MouseEvent event) {
    volume = volumeSlider.getValue();
    // Takes the instance of mainroom to be able to access and change the text to speech volume
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    mainRoom.getTextManager().setVolume((float) volume);
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.getTextManager().setVolume((float) volume);
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.getTextManager().setVolume((float) volume);

    // Checks if the toggle button is selected or not and sets the text to speech to true or false
    // if the Game setting is true
    if (toggleButton.isSelected()) {
      GameState.textToSpeech = true;
    } else {
      GameState.textToSpeech = false;
    }
  }

  /**
   * Handles the click event for the toggle button and changes the text on the button.
   *
   * @param event the mouse event.
   */
  @FXML
  private void onToggleClicked(ActionEvent event) {
    if (toggleButton.isSelected()) {
      toggleButton.setText("Turn On");
    } else {
      toggleButton.setText("Turn Off");
    }
  }

  /**
   * Getter method for the chat box.
   *
   * @return the chat box.
   */
  public TextArea getChatBox() {
    return chatBox;
  }
}
