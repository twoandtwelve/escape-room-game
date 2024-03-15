package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

/**
 * Stores the scenes and controllers.
 *
 * <p>Stores the scenes and controllers. The scenes are stored in a hashmap with the AppUi as the
 * key and the scene as the value. The controllers are stored in a hashmap with the FXML file name
 * as the key and the controller as the value.
 */
public class SceneManager {

  /** Enum to store the AppUi of the scenes. */
  public enum AppUi {
    TITLE,
    MENU,
    MAIN_ROOM,
    ROCKET_INTERIOR,
    PANTRY_INTERIOR,
    MEMORY_GAME,
    SETTING,
    BUSH,
    WIN,
    TREE,
    LOSS
  }

  // Hashmap to store FXML scenes
  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();
  // Hashmap to store FXML controllers
  private static HashMap<String, Object> controllerMap = new HashMap<String, Object>();
  // Keep track of the current scene (used for setting back button)
  private static AppUi previousScene = null;

  /**
   * Getter method for the controller associated to the input FXML file.
   *
   * @param name The name of the FXML file.
   * @return The controller associated to the input FXML file.
   */
  public static Object getController(String name) {
    return controllerMap.get(name);
  }

  /**
   * Getter method for the scene associated to the input AppUi.
   *
   * @param appUi The AppUi of the scene.
   * @return The scene associated to the input AppUi.
   */
  public static Parent getAppUi(AppUi appUi) {
    previousScene = appUi;
    return sceneMap.get(appUi);
  }

  /**
   * Getter method for the previous scene.
   *
   * @return The previous scene.
   */
  public static AppUi getPreviousScene() {
    return previousScene; // Get the current scene
  }

  /**
   * Adds the input scene to the sceneMap.
   *
   * @param appUi The AppUi of the scene.
   * @param parent The scene.
   */
  public static void addAppUi(AppUi appUi, Parent parent) {
    sceneMap.put(appUi, parent);
  }

  /**
   * Adds the input controller to the controllerMap.
   *
   * @param name The name of the FXML file.
   * @param controller The controller associated to the input FXML file.
   */
  public static void addController(String name, Object controller) {
    controllerMap.put(name, controller);
  }
}
