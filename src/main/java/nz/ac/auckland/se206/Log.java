package nz.ac.auckland.se206;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.controllers.MainRoomController;
import nz.ac.auckland.se206.controllers.PantryController;
import nz.ac.auckland.se206.controllers.RocketController;

/** Updates the contents in the task log. */
public class Log {

  /** Method handles enabling the log in all rooms by making them visible. */
  public static void enableLog() {
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.getLogPane().setVisible(true);
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.getLogPane().setVisible(true);
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    mainRoom.getLogPane().setVisible(true);
  }

  /**
   * Method handles updating to show task 1 in the log for all scenes in the game by writing it in.
   */
  public static void showTask1() {
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    mainRoom.getTasks().get(0).setText("- Find the toy");
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.getTasks().get(0).setText("- Find the toy");
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.getTasks().get(0).setText("- Find the toy");
  }

  /**
   * Method handles updating to show task 2 in the log for all scenes in the game by writing it in.
   */
  public static void showTask2() {
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    mainRoom.getTasks().get(1).setText("- Make food");
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.getTasks().get(1).setText("- Make food");
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.getTasks().get(1).setText("- Make food");
  }

  /**
   * Method handles updating to show task 3 in the log for all scenes in the game by writing it in.
   */
  public static void showTask3() {
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    mainRoom.getTasks().get(2).setText("- Find a way out");
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.getTasks().get(2).setText("- Find a way out");
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.getTasks().get(2).setText("- Find a way out");
  }

  /** Method handles completing task 1 in all scene logs by setting the text to green. */
  public static void completeTask1() {
    // Set the text to green for each scene
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    Label label = mainRoom.getTasks().get(0);
    setToGreen(label);
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    label = pantry.getTasks().get(0);
    setToGreen(label);
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    label = rocket.getTasks().get(0);
    setToGreen(label);
  }

  /** Method handles completing task 2 in all scene logs by setting the text to green. */
  public static void completeTask2() {
    // Set the text to green for each scene
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    Label label = mainRoom.getTasks().get(1);
    setToGreen(label);
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    label = pantry.getTasks().get(1);
    setToGreen(label);
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    label = rocket.getTasks().get(1);
    setToGreen(label);
  }

  /** Method handles completing task 3 in all scene logs by setting the text to green. */
  public static void completeTask3() {
    // Set the text to green for each scene
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    Label label = mainRoom.getTasks().get(2);
    setToGreen(label);
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    label = pantry.getTasks().get(2);
    setToGreen(label);
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    label = rocket.getTasks().get(2);
    setToGreen(label);
  }

  /**
   * Method handles setting the input text to a green colour.
   *
   * @param label the text to set to green.
   */
  public static void setToGreen(Label label) {
    label.setTextFill(Color.web("#00ff33"));
  }
}
