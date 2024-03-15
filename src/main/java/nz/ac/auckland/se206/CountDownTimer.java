package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.BushController;
import nz.ac.auckland.se206.controllers.MainRoomController;
import nz.ac.auckland.se206.controllers.MemoryGameController;
import nz.ac.auckland.se206.controllers.PantryController;
import nz.ac.auckland.se206.controllers.RocketController;
import nz.ac.auckland.se206.controllers.SettingsController;
import nz.ac.auckland.se206.controllers.TreeController;

/**
 * Countdown timer for the game. The timer is updated every second and is displayed in the HUD. When
 * the timer reaches 0, the player loses.
 */
public class CountDownTimer {

  // Stores the time left in seconds
  public static int timeLeft;
  public static Timeline countdownTimeline;

  /** Handles the intialisation of the coutndown timer and starts it. */
  public static void initialiseCountdownTimer() {
    timeLeft = GameSettings.timeLimit * 60;
    // Format time to string and update all timers
    String formattedTime = timeToString(timeLeft);
    updateTimerAll(formattedTime);

    // creates a timeline to update the countdown timer every second
    countdownTimeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    if (timeLeft > 0) {
                      // updating HUD timer
                      String formattedTime = timeToString(timeLeft);
                      updateTimerAll(formattedTime);

                      timeLeft--;

                    } else {
                      // time up - player loses
                      countdownTimeline.stop();
                      GameState.isGameActive = false;
                      TextManager.close();
                      App.setUi(AppUi.LOSS);
                    }
                  }
                }));
    // repeat timeline indefinitely
    countdownTimeline.setCycleCount(Timeline.INDEFINITE);
    countdownTimeline.play();
  }

  /**
   * Converts time in seconds to a string in the format mm:ss.
   *
   * @param timeInSeconds time in seconds.
   * @return time in the format mm:ss.
   */
  public static String timeToString(int timeInSeconds) {
    int minutes = timeInSeconds / 60;
    int seconds = timeInSeconds % 60;

    // format minutes and seconds to 2 digits
    String formattedMinutes = String.format("%02d", minutes);
    String formattedSeconds = String.format("%02d", seconds);

    return formattedMinutes + ":" + formattedSeconds;
  }

  /**
   * Updates the timer in all scenes.
   *
   * @param time the time to update the timer to.
   */
  public static void updateTimerAll(String time) {
    // Update timer in bush
    BushController bush = (BushController) SceneManager.getController("bush");
    bush.getTimer().setText(time);
    // Update timer in memory game
    MemoryGameController memoryGame =
        (MemoryGameController) SceneManager.getController("memorygame");
    memoryGame.getTimer().setText(time);
    // Update timer in pantry
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.getTimer().setText(time);
    // Update timer in rocket
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.getTimer().setText(time);
    // Update timer in main room
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    mainRoom.getTimer().setText(time);
    // Update timer in tree
    TreeController tree = (TreeController) SceneManager.getController("tree");
    tree.getTimer().setText(time);
    // Update timer in settings
    SettingsController settings = (SettingsController) SceneManager.getController("settings");
    settings.getTimer().setText(time);
  }
}
