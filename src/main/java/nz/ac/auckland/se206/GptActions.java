package nz.ac.auckland.se206;

import nz.ac.auckland.se206.controllers.MainRoomController;
import nz.ac.auckland.se206.controllers.PantryController;
import nz.ac.auckland.se206.controllers.RocketController;
import nz.ac.auckland.se206.controllers.SettingsController;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/** Class to help with game actions involving the GPT model. */
public class GptActions {

  /** The chat completion request. */
  public static ChatCompletionRequest
      chatCompletionRequest1; // Main request, first used in main room

  public static ChatCompletionRequest
      chatCompletionRequest2; // Pantry specific request, used in pantry

  public static ChatCompletionRequest
      chatCompletionRequest3; // Rocket specific request, used in rocket

  /**
   * Append message to the a text area.
   *
   * @param msg the message to append.
   * @param textArea the text area to append to.
   */
  public static void appendChatMessage(ChatMessage msg, javafx.scene.control.TextArea textArea) {
    textArea.appendText("Cat: " + msg.getContent() + "\n\n");
  }

  /**
   * Set message to the a text area and append it to the chat log.
   *
   * @param msg the message to set.
   * @param textArea the text area to set.
   */
  public static void setChatMessage(ChatMessage msg, javafx.scene.control.TextArea textArea) {
    textArea.setText(msg.getContent());

    // Append the message to the chat log
    SettingsController settings = (SettingsController) SceneManager.getController("settings");
    appendChatMessage(msg, settings.getChatBox());
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process.
   * @param chatCompletionRequest the chat completion request.
   * @return the response chat message.
   * @throws ApiProxyException if there is an error communicating with the API proxy.
   */
  public static ChatMessage runGpt(ChatMessage msg, ChatCompletionRequest chatCompletionRequest)
      throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    try {
      // does the chat complete method and finds the result to update chat reply
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Clears messages from the chat completion request.
   *
   * @param chatCompletionRequest the chat completion request.
   */
  public static void clearMessages(ChatCompletionRequest chatCompletionRequest) {
    chatCompletionRequest.getMessages().clear();
  }

  /**
   * Updates the text area of all scenes.
   *
   * @param msg the message to update the text area with.
   */
  public static void updateTextAreaAll(ChatMessage msg) {
    MainRoomController mainRoom = (MainRoomController) SceneManager.getController("mainroom");
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    mainRoom.getCatTextArea().setText(msg.getContent());
    pantry.getCatTextArea().setText(msg.getContent());
    rocket.getCatTextArea().setText(msg.getContent());

    // Append the message to the chat log
    SettingsController settings = (SettingsController) SceneManager.getController("settings");
    appendChatMessage(msg, settings.getChatBox());
  }
}
