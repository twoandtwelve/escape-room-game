package nz.ac.auckland.se206;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import nz.ac.auckland.se206.controllers.MainRoomController;
import nz.ac.auckland.se206.controllers.PantryController;
import nz.ac.auckland.se206.controllers.RocketController;
import nz.ac.auckland.se206.controllers.SettingsController;

/** Manages the text to speech. */
public class TextManager {

  private static Voice voice;
  private static TextManager textManager;

  /** Handles the closing of the voice. */
  public static void close() {
    if (voice != null) {
      // Deallocate the voice in each scene
      MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
      mainRoom.getTextManager().deallocate();
      RocketController rocket = (RocketController) SceneManager.getController("rocket");
      rocket.getTextManager().deallocate();
      PantryController pantry = (PantryController) SceneManager.getController("pantry");
      pantry.getTextManager().deallocate();
    }
  }

  /**
   * Handles speaking the message in the chat.
   *
   * @param message the message to speak.
   */
  public static void speakChatMessage(String message) {

    // Run the text to speech task on the JavaFX thread
    Platform.runLater(
        () -> {
          // Create a new TTS manager instance
          textManager = new TextManager();
          textManager.setVolume((float) SettingsController.getVolume());
          // Text to speech task
          Task<Void> textToSpeechTask =
              new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                  textManager.speak(message);
                  return null;
                }
              };
          // Start the text to speech thread
          Thread textToSpeechThread = new Thread(textToSpeechTask);
          textToSpeechThread.start();
        });
  }

  /** Constructor for the TextManager which initialises the FreeTTS voice. */
  public TextManager() {
    // Initialize the FreeTTS voice
    System.setProperty(
        "freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
    voice = VoiceManager.getInstance().getVoice("kevin16");
    // Set the voice's attributes
    if (voice != null) {
      voice.allocate();
    } else {
      throw new IllegalStateException("Cannot find the FreeTTS voice.");
    }
    voice.setPitch(280);
    voice.setRate(120);
  }

  /**
   * Sets the volume of the voice if it is not already active.
   *
   * @param volume the volume to set.
   */
  public void setVolume(float volume) {
    if (voice != null) {
      voice.setVolume(volume);
    }
  }

  /**
   * Method which handles the speaking of the text.
   *
   * @param text the text to speak.
   */
  public void speak(String text) {
    // If the gamestate is false (default is on)
    if (voice != null && !GameState.textToSpeech) {
      voice.speak(text);
    }
  }

  /** Handles the deallocation of the voice. */
  private void deallocate() {
    voice.deallocate();
  }
}
