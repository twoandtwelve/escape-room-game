package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameSettings;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GptActions;
import nz.ac.auckland.se206.HoverManager;
import nz.ac.auckland.se206.Hud;
import nz.ac.auckland.se206.Log;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.TextManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller for the main room.
 *
 * <p>Handles the click and hover events for the cat, rocket, pantry, torch, note1, note2, bush,
 * footprints, settings, log, reply, note1 return, note2 return, interactable objects, and the
 * timer.
 */
public class MainRoomController {
  // Main Room Elements
  @FXML private Pane room;
  @FXML private ImageView roomImage;
  @FXML private ImageView rocketImage;
  @FXML private ImageView pantryImage;
  @FXML private Rectangle dim;
  @FXML private Pane note1Pane;
  @FXML private Pane note2Pane;

  // Cat and Chat Elements
  @FXML private ImageView catImageSleep;
  @FXML private ImageView catImageAwoken;
  @FXML private ImageView catImageActive;
  @FXML private Pane chatPane;
  @FXML private TextArea catTextArea;
  @FXML private TextField replyTextField;
  @FXML private ImageView replyImage;
  @FXML private Rectangle replyRectangle;
  @FXML private Label hintsLabel;

  // Toy Puzzle Elements
  @FXML private Pane footprintPane;
  @FXML private ImageView footprint1Image;
  @FXML private ImageView footprint2Image;
  @FXML private ImageView footprint3Image;
  @FXML private ImageView footprint4Image;
  @FXML private ImageView footprint5Image;
  @FXML private ImageView footprint6Image;
  @FXML private ImageView footprint7Image;
  @FXML private ImageView footprint8Image;
  @FXML private ImageView footprint9Image;
  @FXML private ImageView footprint10Image;
  @FXML private ImageView footprint11Image;
  @FXML private ImageView bushImage;
  @FXML private ImageView torchImage;

  // HUD Elements
  @FXML private ImageView settingButton;
  @FXML private ImageView torch;
  @FXML private ImageView note1;
  @FXML private ImageView note2;
  @FXML private Label torchCount;
  @FXML private Label note1Count;
  @FXML private Label note2Count;
  private ArrayList<Object> hudElements;
  @FXML private Rectangle highlightTorch;
  @FXML private Rectangle highlightNote1;
  @FXML private Rectangle highlightNote2;

  // Task Log elements
  private ArrayList<Label> taskList;
  @FXML private Pane logPane;
  @FXML private Rectangle logBackground;
  @FXML private Rectangle logHover;
  @FXML private Label task1;
  @FXML private Label task2;
  @FXML private Label task3;

  // Timer element
  @FXML private Label timer;

  // Text to speech element
  private TextToSpeech textToSpeech;

  // Arraylist of all the footprints
  private ArrayList<ImageView> footprints = new ArrayList<ImageView>();
  // Index of last footprint that was enabled
  private int lastFootprint;

  // Hints
  private int currentHint = 1;

  // TTS
  private TextManager textManager = new TextManager();

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {

    // Add all the hud elements to the arraylist
    hudElements = new ArrayList<Object>();
    hudElements.add(torch);
    hudElements.add(note1);
    hudElements.add(note2);
    hudElements.add(torchCount);
    hudElements.add(note1Count);
    hudElements.add(note2Count);

    // Add all the tasks to the arraylist
    taskList = new ArrayList<Label>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);

    // Adds all the footprints to the arraylist
    footprints.add(footprint1Image);
    footprints.add(footprint2Image);
    footprints.add(footprint3Image);
    footprints.add(footprint4Image);
    footprints.add(footprint5Image);
    footprints.add(footprint6Image);
    footprints.add(footprint7Image);
    footprints.add(footprint8Image);
    footprints.add(footprint9Image);
    footprints.add(footprint10Image);
    footprints.add(footprint11Image);
    lastFootprint = 0;

    // Unfocus replyTextField when room is clicked
    room.setOnMouseClicked(
        event -> {
          if (replyTextField.isFocused()) {
            // unfocus replyTextField
            replyTextField.getParent().requestFocus();
          }
        });
  }

  /**
   * Getter method for the timer label.
   *
   * @return the timer label.
   */
  public Label getTimer() {
    return timer;
  }

  /**
   * Getter method for the text manager.
   *
   * @return the text manager.
   */
  public TextManager getTextManager() {
    return textManager;
  }

  /**
   * Getter method for the hud elements.
   *
   * @return the hud elements.
   */
  public ArrayList<Object> getHudElements() {
    return hudElements;
  }

  /**
   * Getter method for the tasks.
   *
   * @return the tasks.
   */
  public ArrayList<Label> getTasks() {
    return taskList;
  }

  /**
   * Handles the cat initialise click event at the start of the game.
   *
   * @param catInitialise the mouse event.
   */
  @FXML
  public void catInitialise(MouseEvent catInitialise) {
    System.out.println("cat first clicked");

    // Disable cat
    catImageSleep.setDisable(true);
    // Hide sleeping cat
    catImageSleep.setVisible(false);
    // Show awake cat
    catImageAwoken.setVisible(true);

    // Initiate first message from GPT after cat is clicked using a thread
    Task<Void> initiateDeviceTask =
        new Task<Void>() {
          // Call GPT
          @Override
          protected Void call() throws Exception {
            GptActions.chatCompletionRequest1 =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            ChatMessage chatMessage;
            // Get message depending on difficulty
            if (GameSettings.difficulty == GameSettings.GameDifficulty.EASY) {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage("user", GptPromptEngineering.getIntroductionMessageEasy()),
                      GptActions.chatCompletionRequest1);
            } else if (GameSettings.difficulty == GameSettings.GameDifficulty.MEDIUM) {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage("user", GptPromptEngineering.getIntroductionMessageMedium()),
                      GptActions.chatCompletionRequest1);
            } else {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage("user", GptPromptEngineering.getIntroductionMessageHard()),
                      GptActions.chatCompletionRequest1);
            }

            Platform.runLater(
                () -> {
                  // Set chat message to text area
                  GptActions.updateTextAreaAll(chatMessage);
                  // Make chat pane visible
                  chatPane.setVisible(true);
                  // Hide catImageAwoken
                  catImageAwoken.setVisible(false);
                  // Show catImageActive
                  catImageActive.setVisible(true);
                  // Show reply area
                  replyTextField.setVisible(true);
                  replyImage.setVisible(true);
                  replyRectangle.setVisible(true);
                  // Change catImageSleep mouse click event to clickCatSleep
                  catImageSleep.setOnMouseClicked(
                      event -> {
                        clickCatSleep(event);
                      });
                  // Enable cat
                  catImageSleep.setDisable(false);

                  // enabling room switches
                  rocketImage.setDisable(false);
                  pantryImage.setDisable(false);

                  // shows logs
                  Log.enableLog();

                  // assigns task 1 to log
                  Log.showTask1();

                  // removes dim
                  dim.setVisible(false);
                });

            TextManager.speakChatMessage(chatMessage.getContent());

            return null;
          }
        };

    // Start thread for initiateDeviceTask
    Thread initiateDeviceThread = new Thread(initiateDeviceTask);
    initiateDeviceThread.start();
  }

  /**
   * Handles the click event on awoken cat.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickCatAwoken(MouseEvent event) {
    System.out.println("cat clicked");
  }

  /**
   * Handles the click event on sleeping cat.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickCatSleep(MouseEvent event) {
    System.out.println("cat clicked");
    // disable cat
    catImageSleep.setDisable(true);
    // Small animation to make cat look like it is waking up using a thread
    Task<Void> catAwokenTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // Wait 100ms
            Thread.sleep(150);
            // Hide catImageSleep
            catImageSleep.setVisible(false);
            // Show catImageAwoken
            catImageAwoken.setVisible(true);
            // Wait 100ms
            Thread.sleep(300);
            // Hide catImageAwoken
            catImageAwoken.setVisible(false);
            // Show catImageActive
            catImageActive.setVisible(true);
            // Show/Hide chat pane
            chatPane.setVisible(!chatPane.isVisible());
            // Show/Hide reply area
            toggleReplyArea();
            // Enable cat
            catImageSleep.setDisable(false);

            return null;
          }
        };

    // Start thread for catAwokenTask
    Thread catAwokenThread = new Thread(catAwokenTask);
    catAwokenThread.start();
  }

  /**
   * Handles the click event on active cat.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickCatActive(MouseEvent event) {
    TextManager.close();
    System.out.println("cat clicked");
    // Hide active cat
    catImageActive.setVisible(false);
    // Show sleeping cat
    catImageSleep.setVisible(true);
    // Show/Hide reply area
    toggleReplyArea();
    // Show/Hide chat pane
    chatPane.setVisible(!chatPane.isVisible());
  }

  /** Method to toggle visibility of the reply area. */
  public void toggleReplyArea() {
    // Show/Hide reply area
    replyTextField.setVisible(!replyTextField.isVisible());
    replyImage.setVisible(!replyImage.isVisible());
    replyRectangle.setVisible(!replyRectangle.isVisible());
  }

  /**
   * Handles the click event on the reply button.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickReply(MouseEvent event) {

    System.out.println("reply clicked");
    // call reply method
    reply();
  }

  /**
   * Handles the key press event on the reply text field.
   *
   * @param event the key event.
   */
  @FXML
  public void onPressKeyReply(KeyEvent event) {
    // Check if enter key is pressed
    if (event.getCode().toString().equals("ENTER")) {

      System.out.println("enter pressed");
      // call reply method
      reply();
    }
  }

  /** Reply method which handles GPT calling and obtaining a response. */
  public void reply() {
    // Stop the current text to speech
    TextManager.close();
    // Get message from reply text field and trim
    String message = replyTextField.getText().trim();
    // If message is empty, do nothing
    if (message.isEmpty()) {
      return;
    }
    // Append message to chat log
    SettingsController settings = (SettingsController) SceneManager.getController("settings");
    settings.getChatBox().appendText("You: " + message + "\n\n");

    System.out.println("replying: " + message);
    // clear reply text field
    replyTextField.clear();
    // Disable reply button
    replyImage.setDisable(true);
    replyImage.setOpacity(0.5);

    // Update cat image to thinking
    Image image = new Image("images/ThinkingCat.png");
    catImageActive.setImage(image);
    // Disable cat image
    catImageActive.setDisable(true);
    // hide current chat pane
    chatPane.setVisible(false);
    // hide reply area
    toggleReplyArea();

    // Task for calling GPT
    Task<Void> replyTask =
        new Task<Void>() {
          // Call GPT
          @Override
          protected Void call() throws Exception {
            ChatMessage lastMsg;
            // If 'Meowlp' are the first six characters of the message, case insensitive, then give
            // hint
            // first check if message is six characters or more
            if (message.length() >= 6) {
              System.out.println("more than 6 characters");
              if (message.substring(0, 6).equalsIgnoreCase("Meowlp")) {
                System.out.println("mewolp");

                // Call GPT for hint
                // If hard difficulty or hints used
                if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD
                    || GameState.isHintUsed) {
                  lastMsg =
                      GptActions.runGpt(
                          new ChatMessage("user", GptPromptEngineering.getHintMessageHard()),
                          GptActions.chatCompletionRequest1);
                } else {
                  // If current hint is 4
                  if (currentHint >= 4) {
                    lastMsg =
                        GptActions.runGpt(
                            new ChatMessage("user", GptPromptEngineering.getHintMessageNone()),
                            GptActions.chatCompletionRequest1);
                  } else {
                    // Update GameState if medium difficulty
                    if (GameSettings.difficulty == GameSettings.GameDifficulty.MEDIUM) {
                      GameState.hintsLeft--;
                    }

                    // Call GPT for hint depending on current hint
                    if (currentHint == 1) {
                      lastMsg =
                          GptActions.runGpt(
                              new ChatMessage(
                                  "user",
                                  GptPromptEngineering.getHintMessage(
                                      "Find my torch which dropped outside and follow the"
                                          + " footprints with it.")),
                              GptActions.chatCompletionRequest1);
                    } else if (currentHint == 2) {
                      lastMsg =
                          GptActions.runGpt(
                              new ChatMessage(
                                  "user",
                                  GptPromptEngineering.getHintMessage(
                                      "Turn the torch on and follow from the first footprint which"
                                          + " can be found near where the torch was dropped.")),
                              GptActions.chatCompletionRequest1);
                    } else {
                      lastMsg =
                          GptActions.runGpt(
                              new ChatMessage(
                                  "user",
                                  GptPromptEngineering.getHintMessage(
                                      "The footprints lead to the bush outside. Check the bush"
                                          + " after following all footprints.")),
                              GptActions.chatCompletionRequest1);
                    }
                    currentHint++;
                    // If hints left is 0 and hint has not been used, call hintsUsed method
                    if (GameState.hintsLeft == 0 && !GameState.isHintUsed) {
                      hintsUsed();
                    }
                  }
                }
              } else {
                // If message does not start with 'Meowlp', call GPT with original message
                System.out.println("meow");
                ChatMessage msg = new ChatMessage("user", message);
                lastMsg = GptActions.runGpt(msg, GptActions.chatCompletionRequest1);
              }
            } else {
              // If message does not start with 'Meowlp', call GPT with original message
              System.out.println("meow");
              ChatMessage msg = new ChatMessage("user", message);
              lastMsg = GptActions.runGpt(msg, GptActions.chatCompletionRequest1);
            }

            Platform.runLater(
                () -> {
                  // Update text area
                  GptActions.updateTextAreaAll(lastMsg);

                  // Update hint label
                  GameState.updateAllHintsLabel();

                  // Enable reply button
                  replyImage.setDisable(false);
                  replyImage.setOpacity(1);
                  // Show chat pane
                  chatPane.setVisible(true);
                  // Update cat image to active
                  Image image = new Image("images/NeutralCat.png");
                  catImageActive.setImage(image);
                  // Enable cat image
                  catImageActive.setDisable(false);
                  // Show reply area
                  toggleReplyArea();
                });
            // tts for cat speaking
            TextManager.speakChatMessage(lastMsg.getContent());

            return null;
          }
        };

    Thread replyThread = new Thread(replyTask);
    replyThread.start();
  }

  /**
   * Handles the click event on the rocket.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickRocket(MouseEvent event) {
    // Call catInitialise of RocketController
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    rocket.catInitialise();

    switchToRocket();
    System.out.println("rocket clicked");
  }

  /** Switches the scene to rocket. */
  private void switchToRocket() {
    TextManager.close();
    App.setUi(AppUi.ROCKET_INTERIOR);
    // gives focus to rocket
    Parent rocketScene = SceneManager.getAppUi(AppUi.ROCKET_INTERIOR);
    App.getScene().setRoot(rocketScene);
    rocketScene.requestFocus();
  }

  /**
   * Handles the click event on the pantry.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickPantry(MouseEvent event) {
    // Call catInitialise of PantryController
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    pantry.catInitialise();

    switchToPantry();
    System.out.println("pantry clicked");
  }

  /** Switches the scene to pantry. */
  private void switchToPantry() {
    TextManager.close();
    App.setUi(AppUi.PANTRY_INTERIOR);
    // gives focus to pantry
    Parent pantryScene = SceneManager.getAppUi(AppUi.PANTRY_INTERIOR);
    App.getScene().setRoot(pantryScene);
    pantryScene.requestFocus();
  }

  /**
   * Handles the click event on the torch on ground.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickTorchGround(MouseEvent event) {
    System.out.println("torch ground clicked");
    // Update GameState
    GameState.torchFound = true;
    // Hide torch
    torchImage.setVisible(false);
    // Enable torch in the hud
    Hud.updateTorch(true, "x1");
  }

  /**
   * Handles the click event on the torch hud.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickTorch(MouseEvent event) {
    System.out.println("torch hud clicked");
    // note 1 is already found - torch cannot be used
    if (GameState.note1Found) {
      return;
    }
    // When torch is being turned on
    if (!GameState.isTorchOn) {
      // Update GameState
      GameState.isTorchOn = true;
      // Change image
      Image image = new Image("images/Torchlit.png");
      torch.setImage(image);
      // Change image of torch in all other scenes too
      RocketController rocket = (RocketController) SceneManager.getController("rocket");
      ImageView torchImage = (ImageView) rocket.getHudElements().get(0);
      torchImage.setImage(image);
      PantryController pantry = (PantryController) SceneManager.getController("pantry");
      torchImage = (ImageView) pantry.getHudElements().get(0);
      torchImage.setImage(image);
      // Show footprints pane
      footprintPane.setVisible(true);
      // Enable first footprint
      footprint1Image.setDisable(false);
    } else if (GameState.isTorchOn
        && !GameState.footprintsFound) { // when torch is being turned off but footprints not found
      // Update GameState
      GameState.isTorchOn = false;
      // Change image
      Image image = new Image("images/Torch.png");
      torch.setImage(image);
      // Change image of torch in all other scenes too
      RocketController rocket = (RocketController) SceneManager.getController("rocket");
      ImageView torchImage = (ImageView) rocket.getHudElements().get(0);
      torchImage.setImage(image);
      PantryController pantry = (PantryController) SceneManager.getController("pantry");
      torchImage = (ImageView) pantry.getHudElements().get(0);
      torchImage.setImage(image);
      // Hide footprints pane
      footprintPane.setVisible(false);
      // Disable and hide all footprints except first and set opacity to 0
      for (int i = 1; i < footprints.size(); i++) {
        footprints.get(i).setDisable(true);
        footprints.get(i).setVisible(false);
        footprints.get(i).setOpacity(0);
      }
      lastFootprint = 0;
    } else if (GameState.isTorchOn
        && GameState
            .footprintsFound) { // when torch is being turned off and footprints found (but bush not
      // clicked)
      // Update GameState
      GameState.isTorchOn = false;
      // Change image
      Image image = new Image("images/Torch.png");
      torch.setImage(image);
      // Hide footprints pane
      footprintPane.setVisible(false);
    }
  }

  /**
   * Handles the click event on the bush.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickBush(MouseEvent event) {

    // change image of torchhud
    Image image = new Image("images/Torch.png");
    torch.setImage(image);
    // change image of torch in all other scenes too
    RocketController rocket = (RocketController) SceneManager.getController("rocket");
    ImageView torch = (ImageView) rocket.getHudElements().get(0);
    torch.setImage(image);
    PantryController pantry = (PantryController) SceneManager.getController("pantry");
    torch = (ImageView) pantry.getHudElements().get(0);
    torch.setImage(image);
    // hide footprints pane
    footprintPane.setVisible(false);
    // disables torch in the hud
    Hud.updateTorch(true, "x0");
    Hud.disableTorch();

    switchToBush();
  }

  /** Switches the scene to bush. */
  private void switchToBush() {
    TextManager.close();
    App.setUi(AppUi.BUSH);
  }

  /**
   * Handles the hover event on the footprints.
   *
   * @param event the mouse event.
   */
  @FXML
  public void hoverFootprints(MouseEvent event) {
    System.out.println("footprints hovered");
    // get the image that was hovered
    ImageView image = (ImageView) (Node) event.getTarget();

    // Fade transition
    FadeTransition ft = new FadeTransition(Duration.millis(150), image);

    // set the transition to change opacity from 0 to 1
    // if fxid is footprint1Image, set to 0.33
    if (image.getId().equals("footprint1Image")) {
      ft.setFromValue(0.33);
    } else {
      ft.setFromValue(0);
    }
    ft.setToValue(1);

    // play the transition
    ft.play();

    // enable the next footprint if this footprint is the last enabled footprint
    if (footprints.indexOf(image) == lastFootprint) {
      enableFootprint(lastFootprint + 1);
      lastFootprint++;
    }

    // if lastFootprint is footprints.size(), then enable the bush and update GameState
    if (lastFootprint == footprints.size()) {
      System.out.println("bush enabled");
      bushImage.setDisable(false);
      GameState.footprintsFound = true;
    }
  }

  /**
   * Handles the unhover event on the footprints.
   *
   * @param event the mouse event.
   */
  @FXML
  public void unhoverFootprints(MouseEvent event) {
    System.out.println("footprints unhovered");
    // get the image that was hovered
    ImageView image = (ImageView) (Node) event.getTarget();

    // Fade transition
    FadeTransition ft = new FadeTransition(Duration.millis(150), image);

    // set the transition to change opacity from 1 to 0
    ft.setFromValue(1);
    ft.setToValue(0.33);

    // play the transition
    ft.play();
  }

  /**
   * Method to enable the footprints.
   *
   * @param index the index of the next footprint to be enabled.
   */
  private void enableFootprint(int index) {
    // if index is less than length of footprints, show and enable the next footprint
    if (index < footprints.size()) {
      System.out.println("footprint " + index + " enabled");
      footprints.get(index).setDisable(false);
      footprints.get(index).setVisible(true);
    }
  }

  /**
   * Handles the click event on the settings which switches the scene to the settings view.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickSetting(MouseEvent event) {
    TextManager.close();
    // Ensure onClickSettings has the  SceneManager.getAppUi(AppUi."currentscene"); to work
    App.setUi(AppUi.SETTING);
    SceneManager.getAppUi(AppUi.MAIN_ROOM);
  }

  /**
   * Handles the click event on the note1.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote1(MouseEvent event) {
    // Show note1Pane
    note1Pane.setVisible(true);
    // removes highlight
    highlightNote1.setVisible(false);
  }

  /**
   * Handles the click event on the note2.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote2(MouseEvent event) {
    // Show note2Pane
    note2Pane.setVisible(true);
    // removes highlight
    highlightNote2.setVisible(false);
  }

  /**
   * Handles the click event on the note1 return button.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote1Return(MouseEvent event) {
    TextManager.close();
    // Hide note1Pane
    note1Pane.setVisible(false);
  }

  /**
   * Handles the click event on the note2 return button.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote2Return(MouseEvent event) {
    TextManager.close();
    // Hide note2Pane
    note2Pane.setVisible(false);
  }

  /**
   * Handles the hover event on the interactable objects.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    // Scale up image hovered
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the unhover event on the interactable objects.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
    // Scale down image hovered
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleDown(image);
  }

  /**
   * Getter method for the cat text area.
   *
   * @return the cat text area.
   */
  public TextArea getCatTextArea() {
    return catTextArea;
  }

  /**
   * Getter method for the log pane.
   *
   * @return the task list.
   */
  public Pane getLogPane() {
    return logPane;
  }

  /** Method that calls GPT when hints are used up in medium difficulty. */
  public void hintsUsed() {
    // Change difficulty to hard to ensure future prompts are given in hard difficulty which include
    // no hints.
    GameSettings.difficulty = GameSettings.GameDifficulty.HARD;
    GameState.isHintUsed = true;
    // Change hints label text to red
    hintsLabel.setStyle("-fx-text-fill: red;");
  }

  /** Handles the updating of hint label. */
  public void updateHintsLabel() {
    // If easy difficult, set label to inf.
    if (GameSettings.difficulty == GameSettings.GameDifficulty.EASY) {
      hintsLabel.setText("Hints left: inf.");
      return;
    }
    hintsLabel.setText("Hints left: " + GameState.hintsLeft);
    // If no hints left, change label text to red
    if (GameState.hintsLeft == 0) {
      hintsLabel.setStyle("-fx-text-fill: red;");
    }
  }

  /** Method to terminate text to speech. */
  public void terminateTextToSpeech() {
    if (textToSpeech != null) {
      textToSpeech.terminate();
    }
  }

  // Hud highlight methods

  /**
   * Handles the hover event on the torch which shows the torch highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverTorch(MouseEvent event) {
    highlightTorch.setVisible(true);
  }

  /**
   * Handles the unhover event on the torch which hides the torch highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveTorch(MouseEvent event) {
    highlightTorch.setVisible(false);
  }

  /**
   * Handles the hover event on the note1 which shows the note1 highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverNote1(MouseEvent event) {
    highlightNote1.setVisible(true);
  }

  /**
   * Handles the unhover event on the note1 which hides the note1 highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveNote1(MouseEvent event) {
    highlightNote1.setVisible(false);
  }

  /**
   * Handles the hover event on the note2 which shows the note2 highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverNote2(MouseEvent event) {
    highlightNote2.setVisible(true);
  }

  /**
   * Handles the unhover event on the note2 which hides the note2 highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveNote2(MouseEvent event) {
    highlightNote2.setVisible(false);
  }

  /**
   * Handles the hover event on the task log.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverLog(MouseEvent event) {
    logBackground.setVisible(true);
    logHover.setVisible(true);
    task1.setVisible(true);
    task2.setVisible(true);
    task3.setVisible(true);
  }

  /**
   * Handles the unhover event on the task log.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveLog(MouseEvent event) {
    logBackground.setVisible(false);
    logHover.setVisible(false);
    task1.setVisible(false);
    task2.setVisible(false);
    task3.setVisible(false);
  }
}
