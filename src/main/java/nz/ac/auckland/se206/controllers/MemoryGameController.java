package nz.ac.auckland.se206.controllers;

import java.lang.reflect.Field;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ButtonSequence;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.HoverManager;
import nz.ac.auckland.se206.Log;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.TextManager;

/**
 * Controller for the memory game.
 *
 * <p>Handles the click and hover events for the back button and play button
 */
public class MemoryGameController {

  @FXML private Pane pane;
  @FXML private ImageView back;
  @FXML private ImageView play;
  @FXML private Label text;
  @FXML private ImageView button1;
  @FXML private ImageView button2;
  @FXML private ImageView button3;
  @FXML private ImageView button4;
  @FXML private ImageView button5;
  @FXML private ImageView button6;
  @FXML private ImageView button7;
  @FXML private ImageView button8;
  @FXML private ImageView button9;
  @FXML private ImageView button10;
  @FXML private ImageView button11;
  @FXML private ImageView button12;
  @FXML private ImageView button13;
  @FXML private ImageView button14;
  @FXML private ImageView button15;
  @FXML private ImageView button16;

  // Timer element
  @FXML private Label timer;

  private int sequenceIndex;

  /** Initialise method which is called as soon as the class is loaded. */
  public void initialize() {
    // creates a random button sequence
    ButtonSequence.initialiseCorrectSequence();

    // assigns each button to values 1-16 respectively
    initialiseUserData();
  }

  /**
   * Getter method for the timer.
   *
   * @return the timer.
   */
  public Label getTimer() {
    return timer;
  }

  /**
   * Handles the click event for the back button and switches to the rocket.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickBack(MouseEvent event) {
    switchToRocket();
  }

  /**
   * Handles the click event for the play button and plays the memory game.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickPlay(MouseEvent event) {
    // if animation is running dont play again
    if (GameState.isAnimationRunning) {
      return;
    }
    sequenceIndex = 0;
    // plays the correct memory sequence recursively
    playSequence();
  }

  /** Method which switches the scene to the rocket. */
  private void switchToRocket() {
    TextManager.close();
    App.setUi(AppUi.ROCKET_INTERIOR);
    // gives focus to memory game
    Parent rocketScene = SceneManager.getAppUi(AppUi.ROCKET_INTERIOR);
    App.getScene().setRoot(rocketScene);
    rocketScene.requestFocus();
    // resets player sequence when exiting the memory game
    ButtonSequence.clear();
  }

  /**
   * Handles the key press event for the escape key and switches to the rocket.
   *
   * @param event the key event.
   */
  @FXML
  public void onPressKey(KeyEvent event) {
    if (event.getCode() == KeyCode.ESCAPE) {
      switchToRocket();
    }
  }

  /**
   * Handles the click event for the buttons.
   *
   * @param event the mouse event.
   */
  @FXML
  private void pressButton(MouseEvent event) {
    // if animation is running or game is resolved then return
    if (GameState.isAnimationRunning || GameState.isMemoryGameResolved) {
      return;
    }

    // button turns green when pressed
    setToGreen((ImageView) event.getTarget());
  }

  /**
   * Handles the release event for the buttons.
   *
   * @param event the mouse event.
   */
  @FXML
  private void releaseButton(MouseEvent event) {
    // if animation is running or game is resolved then return
    if (GameState.isAnimationRunning || GameState.isMemoryGameResolved) {
      return;
    }
    ImageView image = (ImageView) event.getTarget();

    // button turns to original when released
    setToOriginal(image);

    // retrives assigned value from button
    int button = Integer.parseInt((String) image.getUserData());
    ButtonSequence.add(button);

    // this occurs when the most recently added value does not match the value at the same position
    // in the correct sequence
    if (!ButtonSequence.correctSequence
        .get(ButtonSequence.playerSequence.size() - 1)
        .equals(ButtonSequence.playerSequence.get(ButtonSequence.playerSequence.size() - 1))) {
      ButtonSequence.clear();

      text.setText("Incorrect!");

      // Create a Timeline animation
      Timeline timeline =
          new Timeline(
              new KeyFrame(
                  Duration.seconds(2), new KeyValue(text.textProperty(), "Try again ^=_=^")),
              new KeyFrame(Duration.seconds(1), new KeyValue(text.textProperty(), "Incorrect!")));

      timeline.setCycleCount(2); // Alternate between two values
      timeline.play();
    }
    // this occurs when all the added values match the values in the correct sequence
    if (ButtonSequence.correctSequence.equals(ButtonSequence.playerSequence)) {
      GameState.isMemoryGameResolved = true;

      RocketController rocket = (RocketController) SceneManager.getController("rocket");

      // hide memory game rectangle
      rocket.getMemoryGameRectangle().setVisible(false);
      // initialise the riddle
      rocket.initialiseFinalRiddle();

      // sets all buttons to green
      setAllGreen();
      // Disables play button
      play.setDisable(true);

      // completes task 3
      Log.completeTask3();

      // Sets the text
      text.setText(("  /|\n(˚ˎ 。7\n|、˜〵\nじしˍ,)/"));

      // Create a Timeline animation
      Timeline timeline =
          new Timeline(
              new KeyFrame(
                  Duration.seconds(2),
                  new KeyValue(text.textProperty(), ("  /|\n(˚ˎ 。7\n|、˜〵\nじしˍ,)/"))),
              new KeyFrame(Duration.seconds(1), new KeyValue(text.textProperty(), "Correct!")));

      timeline.setCycleCount(2); // Alternate between two values
      timeline.play();
    }
  }

  /** Sets all buttons to green and disables them. */
  private void setAllGreen() {
    // gets all the buttons
    Field[] buttonFields = getClass().getDeclaredFields();

    // sets each button green and disables them
    for (Field field : buttonFields) {
      if (field.getName().startsWith("button")) {
        try {
          ImageView button = (ImageView) field.get(this);
          // sets each button green
          setToGreen(button);
          // disables button
          button.setDisable(true);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // save the buttons that the user inupts
  private void initialiseUserData() {
    Field[] buttonFields = getClass().getDeclaredFields();
    // set user data into the array
    for (Field field : buttonFields) {
      if (field.getName().startsWith("button")) {
        try {
          // get the number of the button
          int buttonNumber = Integer.parseInt(field.getName().substring(6));
          ImageView button = (ImageView) field.get(this);
          button.setUserData(Integer.toString(buttonNumber));
        } catch (IllegalAccessException | NumberFormatException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // Each button has a custom variable, this returns correct button with given user date
  private ImageView findButtonByUserData(int value) {
    try {
      // change the button variable to string and set name
      String num = Integer.toString(value);
      String buttonName = "button" + num;
      // returns the imageview variable that has the name button name
      Field field = getClass().getDeclaredField(buttonName);
      field.setAccessible(true);
      return (ImageView) field.get(this);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Takes an image as input then sets the image to green.
   *
   * @param image the image to set to green.
   */
  private void setToGreen(ImageView image) {
    // ColorAdjust is used to change the hue, brightness, contrast and saturation of the image
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setHue(0.56);
    colorAdjust.setBrightness(0.26);
    colorAdjust.setContrast(0.26);
    colorAdjust.setSaturation(1);
    image.setEffect(colorAdjust);
  }

  /**
   * Takes an image as input then sets the image to original.
   *
   * @param image the image to set to original.
   */
  private void setToOriginal(ImageView image) {
    // ColorAdjust is used to change the hue, brightness, contrast and saturation of the image
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setHue(0);
    image.setEffect(colorAdjust);
  }

  /** Plays the sequence of buttons which are randomised. */
  private void playSequence() {
    // if game is already running dont play again
    GameState.isAnimationRunning = true;
    // save buttons pressed and highlight if it is pressed
    int currentInteger = ButtonSequence.correctSequence.get(sequenceIndex);
    ImageView button = findButtonByUserData(currentInteger);

    // Set the button to green
    setToGreen(button);

    // Pause for 0.6 seconds
    PauseTransition firstPause = new PauseTransition(Duration.seconds(0.6));
    firstPause.setOnFinished(
        (ActionEvent e) -> {
          setToOriginal(button);
          sequenceIndex++;
          // play again if incorrect
          if (sequenceIndex < ButtonSequence.correctSequence.size()) {
            PauseTransition secondPause = new PauseTransition(Duration.seconds(0.5));
            secondPause.setOnFinished((ActionEvent event1) -> playSequence());
            secondPause.play();
          } else {
            GameState.isAnimationRunning = false;
          }
        });
    firstPause.play();
  }

  /**
   * Handles the hover event for the back button and scales the image up.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the unhover event for the back button and scales the image down.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleDown(image);
  }
}
