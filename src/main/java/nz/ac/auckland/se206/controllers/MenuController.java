package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CountDownTimer;
import nz.ac.auckland.se206.GameSettings;
import nz.ac.auckland.se206.GameSettings.GameDifficulty;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.HoverManager;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * Controller for the menu screen.
 *
 * <p>Handles the click and hover events for the difficulty and time limit rectangles.
 */
public class MenuController {

  @FXML private Pane pane;
  @FXML private Rectangle easy;
  @FXML private Rectangle medium;
  @FXML private Rectangle hard;
  @FXML private ImageView easyImage;
  @FXML private ImageView mediumImage;
  @FXML private ImageView hardImage;
  @FXML private Rectangle two;
  @FXML private Rectangle four;
  @FXML private Rectangle six;
  @FXML private Label twoText;
  @FXML private Label fourText;
  @FXML private Label sixText;
  @FXML private ImageView play;
  @FXML private Label playText;
  @FXML private ImageView settingButton;

  // The colour of the selected rectangle
  private Color unselected = new Color(1.0, 0.7176, 0.0, 1.0);

  public void initialize() {}

  /**
   * Handles the click event for easy difficulty and sets the difficulty to easy.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickEasy(MouseEvent event) {
    GameSettings.difficulty = GameDifficulty.EASY;
    // Change colour of the difficulty
    changeColourDifficulty(easy);
  }

  /**
   * Handles the click event for medium difficulty and sets the difficulty to medium.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickMedium(MouseEvent event) {
    GameSettings.difficulty = GameDifficulty.MEDIUM;
    // Medium difficulty has 5 hints
    GameState.hintsLeft = 5;
    // Change colour of the difficulty
    changeColourDifficulty(medium);
  }

  /**
   * Handles the click event for hard difficulty and sets the difficulty to hard.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickHard(MouseEvent event) {
    GameSettings.difficulty = GameDifficulty.HARD;
    // Hard difficulty has 0 hints
    GameState.hintsLeft = 0;
    // Change colour of the difficulty
    changeColourDifficulty(hard);
  }

  /**
   * Handles the click event for the 2 minute time limit and sets the time limit to 2 minutes.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickTwo(MouseEvent event) {
    GameSettings.timeLimit = 2;
    changeColourTime(two);
  }

  /**
   * Handles the click event for the 4 minute time limit and sets the time limit to 4 minutes.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickFour(MouseEvent event) {
    GameSettings.timeLimit = 4;
    changeColourTime(four);
  }

  /**
   * Handles the click event for the 6 minute time limit and sets the time limit to 6 minutes.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickSix(MouseEvent event) {
    GameSettings.timeLimit = 6;
    changeColourTime(six);
  }

  /**
   * Handles the click event for the play button and switches to the main room.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickPlay(MouseEvent event) {
    // Only switch to the main room if the difficulty and time limit have been set
    if (GameSettings.difficulty != null && GameSettings.timeLimit != 0) {
      // Update the hint label depending on the difficulty
      MainRoomController mainRoomController =
          (MainRoomController) SceneManager.getController("mainroom");
      PantryController pantryController = (PantryController) SceneManager.getController("pantry");
      RocketController rocketController = (RocketController) SceneManager.getController("rocket");
      mainRoomController.updateHintsLabel();
      pantryController.updateHintsLabel();
      rocketController.updateHintsLabel();

      // Switch to the main room
      switchToRoom();
    }
  }

  /**
   * Handles the click event for the settings button and switches to the settings menu.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickSetting(MouseEvent event) {
    // Ensure onClickSettings has the  SceneManager.getAppUi(AppUi."currentscene"); to work
    App.setUi(AppUi.SETTING);
    SceneManager.getAppUi(AppUi.MENU);
  }

  /**
   * Changes the colour of the difficulty rectangles.
   *
   * @param rectangle the rectangle to change the colour of.
   */
  @FXML
  public void changeColourDifficulty(Rectangle rectangle) {
    // set the effects to the first colour
    easy.setFill(unselected);
    medium.setFill(unselected);
    hard.setFill(unselected);
    // set the effects to the variables to nothing
    easy.setEffect(null);
    medium.setEffect(null);
    hard.setEffect(null);
    // Set the effects to the selected rectangle
    selectedObject(rectangle);
  }

  /**
   * Changes the colour of the time limit rectangles.
   *
   * @param rectangle the rectangle to change the colour of.
   */
  @FXML
  public void changeColourTime(Rectangle rectangle) {
    // set the effects to the first colour
    two.setFill(unselected);
    four.setFill(unselected);
    six.setFill(unselected);
    // set the effects to the variables to nothing
    two.setEffect(null);
    four.setEffect(null);
    six.setEffect(null);
    // set the effects to the selected rectangle
    selectedObject(rectangle);
  }

  /**
   * Sets the effects of the selected rectangle.
   *
   * @param rectangle the rectangle to set the effects of.
   */
  @FXML
  public void selectedObject(Rectangle rectangle) {
    // Drop shadow effect
    DropShadow dropShadowEffect = new DropShadow();
    // Customise the drop shadow effect
    dropShadowEffect.setColor(Color.WHITE);
    dropShadowEffect.setOffsetX(5.0);
    dropShadowEffect.setOffsetY(5.0);
    dropShadowEffect.setRadius(10.0);
    // Set the effects to the rectangle
    rectangle.setEffect(dropShadowEffect);
    rectangle.setFill(Color.WHITE);
    rectangle.setOpacity(0.5);
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

  /**
   * Handles the hover event for the difficulty rectangles and changes the opacity.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onMouseEnter(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    image.setOpacity(0.1);
  }

  /**
   * Handles the unhover event for the difficulty rectangles and changes the opacity.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onMouseExit(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    image.setOpacity(1);
  }

  /** Switches to the main room. */
  private void switchToRoom() {
    App.setUi(AppUi.MAIN_ROOM);
    GameState.isGameActive = true;

    // Initialise the countdown timer
    CountDownTimer.initialiseCountdownTimer();
  }
}
