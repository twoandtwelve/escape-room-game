package nz.ac.auckland.se206;

import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.controllers.MainRoomController;
import nz.ac.auckland.se206.controllers.PantryController;
import nz.ac.auckland.se206.controllers.RocketController;

/**
 * Stores the HUD elements which the user can pick up and use.
 *
 * <p>Stores the HUD elements which the user can pick up and use. The HUD elements are updated when
 * the user picks up/uses an item.
 */
public class Hud {

  /**
   * Updates the torch in all HUDs.
   *
   * @param status the current torch status.
   * @param count the current torch count.
   */
  public static void updateTorch(boolean status, String count) {
    // Update the torch in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    ImageView image = (ImageView) pantry.getHudElements().get(0);
    image.setVisible(status);
    Label label = (Label) pantry.getHudElements().get(3);
    label.setVisible(status);
    label.setText(count);
    // Update the torch in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    image = (ImageView) rocket.getHudElements().get(0);
    image.setVisible(status);
    label = (Label) rocket.getHudElements().get(3);
    label.setVisible(status);
    label.setText(count);
    // Update the torch in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    image = (ImageView) mainRoom.getHudElements().get(0);
    image.setVisible(status);
    label = (Label) mainRoom.getHudElements().get(3);
    label.setVisible(status);
    label.setText(count);
  }

  /**
   * Updates note 1 in all HUDs.
   *
   * @param status the current note 1 status.
   * @param count the current note 1 count.
   */
  public static void updateNote1(boolean status, String count) {
    // Update the note 1 in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    ImageView image = (ImageView) pantry.getHudElements().get(1);
    image.setVisible(status);
    Label label = (Label) pantry.getHudElements().get(4);
    label.setVisible(status);
    label.setText(count);
    // Update the note 1 in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    image = (ImageView) rocket.getHudElements().get(1);
    image.setVisible(status);
    label = (Label) rocket.getHudElements().get(4);
    label.setVisible(status);
    label.setText(count);
    // Update the note 1 in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    image = (ImageView) mainRoom.getHudElements().get(1);
    image.setVisible(status);
    label = (Label) mainRoom.getHudElements().get(4);
    label.setVisible(status);
    label.setText(count);
  }

  /**
   * Updates note 2 in all HUDs.
   *
   * @param status the current note 2 status.
   * @param count the current note 2 count.
   */
  public static void updateNote2(boolean status, String count) {
    // Update the note 2 in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    ImageView image = (ImageView) pantry.getHudElements().get(2);
    image.setVisible(status);
    Label label = (Label) pantry.getHudElements().get(5);
    label.setVisible(status);
    label.setText(count);
    // Update the note 2 in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    image = (ImageView) rocket.getHudElements().get(2);
    image.setVisible(status);
    label = (Label) rocket.getHudElements().get(5);
    label.setVisible(status);
    label.setText(count);
    // Update the note 2 in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    image = (ImageView) mainRoom.getHudElements().get(2);
    image.setVisible(status);
    label = (Label) mainRoom.getHudElements().get(5);
    label.setVisible(status);
    label.setText(count);
  }

  /** Disables the torch in all HUDs. */
  public static void disableTorch() {
    // Disable the torch in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    ImageView image = (ImageView) pantry.getHudElements().get(0);
    image.setDisable(true);
    greyScale(image);
    // Disable the torch in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    image = (ImageView) rocket.getHudElements().get(0);
    image.setDisable(true);
    greyScale(image);
    // Disable the torch in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    image = (ImageView) mainRoom.getHudElements().get(0);
    image.setDisable(true);
    greyScale(image);
  }

  /** Disables note 1 in all HUDs. */
  public static void disableNote1() {
    // Disable the note 1 in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    ImageView image = (ImageView) pantry.getHudElements().get(1);
    image.setDisable(true);
    greyScale(image);
    // Disable the note 1 in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    image = (ImageView) rocket.getHudElements().get(1);
    image.setDisable(true);
    greyScale(image);
    // Disable the note 1 in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    image = (ImageView) mainRoom.getHudElements().get(1);
    image.setDisable(true);
    greyScale(image);
  }

  /** Disables note 2 in all HUDs. */
  public static void disableNote2() {
    // Disable the note 2 in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    ImageView image = (ImageView) pantry.getHudElements().get(2);
    image.setDisable(true);
    greyScale(image);
    // Disable the note 2 in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    image = (ImageView) rocket.getHudElements().get(2);
    image.setDisable(true);
    greyScale(image);
    // Disable the note 2 in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    image = (ImageView) mainRoom.getHudElements().get(2);
    image.setDisable(true);
    greyScale(image);
  }

  /**
   * Greyscales an image view by using a colour adjust effect.
   *
   * @param image the image view to greyscale.
   */
  private static void greyScale(ImageView image) {
    ColorAdjust colourAdjust = new ColorAdjust(0, -1, -0.4, 0);
    image.setEffect(colourAdjust);
  }
}
