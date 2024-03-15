package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CountDownTimer;
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

/**
 * Controller for the rocket interior screen.
 *
 * <p>Handles the click and hover events for the right/left meowpads, memory game, notes, cat and
 * chat.
 */
public class RocketController {

  @FXML private Pane pane;
  @FXML private ImageView temp;
  @FXML private ImageView launch;
  @FXML private Pane note1Pane;
  @FXML private Pane note2Pane;
  @FXML private ImageView clearBox;

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

  // HUD Elements
  @FXML private ImageView settingButton;
  @FXML private ImageView back;
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

  // Meow Pad
  @FXML private Rectangle memoryGameRectangle;
  @FXML private Rectangle leftMeowPad;
  @FXML private Rectangle rightMeowPad;
  @FXML private ProgressBar leftProgressBar;
  @FXML private ProgressBar rightProgressBar;
  @FXML private Circle leftActivateCircle;
  @FXML private Circle rightActivateCircle;
  private boolean isLeftMeowPadPressed = false;
  private Timeline leftMeowPadPressTimer;
  private boolean isRightMeowPadpressed = false;
  private int rightMeowPadCount;
  private Timeline leftProgressBarTimer;
  private double originalWidth;
  private double originalHeight;
  private Color originalColor = new Color(1.0, 0.6431, 0.6431, 0.2784);

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

  private boolean isRoomFirstEntered = false;
  private boolean currentHint = false;

  // initialise textManager for text to speech
  private TextManager textManager = new TextManager();

  /** Initialise method for the rocket which is called as soon as the scene loads. */
  public void initialize() {
    // Add all hud elements to an arraylist
    hudElements = new ArrayList<Object>();
    hudElements.add(torch);
    hudElements.add(note1);
    hudElements.add(note2);
    hudElements.add(torchCount);
    hudElements.add(note1Count);
    hudElements.add(note2Count);

    // Add all task elements to an arraylist
    taskList = new ArrayList<Label>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);

    // Initialise the left meow pad
    initialiseLeftMeowPad();

    // Initialise the left and right progress bars
    leftProgressBar.setProgress(0);
    rightProgressBar.setProgress(0);

    // Disable and hide the memory game rectangle
    memoryGameRectangle.setDisable(true);
    memoryGameRectangle.setVisible(false);
    memoryGameRectangle.setOpacity(0.5);

    originalWidth = memoryGameRectangle.getWidth();
    originalHeight = memoryGameRectangle.getHeight();

    // Cat and Chat initialisation
    // Hide catImageSleep
    catImageSleep.setVisible(false);
    // Show catImageActive
    catImageActive.setVisible(true);
    // Change image to thinking cat
    Image image = new Image("images/ThinkingCat.png");
    catImageActive.setImage(image);
    // Disable cat
    catImageActive.setDisable(true);

    // Unfocus replyTextField when room is clicked
    pane.setOnMouseClicked(
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
   * Getter method for the hud elements.
   *
   * @return the hud elements.
   */
  public ArrayList<Object> getHudElements() {
    return hudElements;
  }

  /**
   * Getter method for the task list.
   *
   * @return the task list.
   */
  public ArrayList<Label> getTasks() {
    return taskList;
  }

  /**
   * Getter method for the log pane.
   *
   * @return the task list.
   */
  public Pane getLogPane() {
    return logPane;
  }

  /**
   * Handles the click event for the back button and switches to the main room.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickBack(MouseEvent event) {
    switchToRoom();
  }

  /**
   * Handles the click event for the memory game rectangle and switches to the memory game.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickTemp(MouseEvent event) {
    TextManager.close();
    switchToMemoryGame();
  }

  /**
   * Handles the click event on the note1.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote1(MouseEvent event) {
    TextManager.close();
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
    TextManager.close();
    // Show note2Pane
    note2Pane.setVisible(true);
    // removes highlight
    highlightNote2.setVisible(false);
  }

  /**
   * Handles the click event for note 1 return button and hides the note 1 pane.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote1Return(MouseEvent event) {
    TextManager.close();
    note1Pane.setVisible(false);
  }

  /**
   * Handles the click event for note 2 return button and hides the note 2 pane.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote2Return(MouseEvent event) {
    TextManager.close();
    note2Pane.setVisible(false);
  }

  /** Switches the scene to the main room. */
  private void switchToRoom() {
    TextManager.close();
    App.setUi(AppUi.MAIN_ROOM);
  }

  private void updateProgressBar(ProgressBar progressBar, double progress) {
    Platform.runLater(() -> progressBar.setProgress(progress));
  }

  /**
   * Handles the press event for the right meow pad.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onPressRightMeowPad(MouseEvent event) {
    // if the right meow pad is not activated
    if (!GameState.isRightMeowPadActivated) {
      rightMeowPadCount = 0;
      isRightMeowPadpressed = true;
    }
  }

  /**
   * Handles the release event for the right meow pad.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onReleaseRightMeowPad(MouseEvent event) {
    isRightMeowPadpressed = false;
  }

  /**
   * Handles the mouse drag event for the right meow pad.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onMouseDrag(MouseEvent event) {
    // if the right meow pad is not activated and right meow pad is pressed
    if (isRightMeowPadpressed && !GameState.isRightMeowPadActivated) {
      // Increase the count as the mouse drags
      rightMeowPadCount++;
      System.out.println(rightMeowPadCount);
      // if the count is 150, then the right meow pad is activated
      if (rightMeowPadCount == 150) {
        handleRightMeowPadActivation();
      } else {
        // Update the progress bar based on the count
        double progress = rightMeowPadCount / 150.0;
        updateProgressBar(rightProgressBar, progress);
      }
    }
  }

  /**
   * Handles the press event for the left meow pad.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onPressLeftMeowPad(MouseEvent event) {
    Platform.runLater(
        () -> {
          leftProgressBar.setProgress(leftProgressBar.getProgress() + 0.05);
        });
    // if the left meow pad is not activated
    if (!isLeftMeowPadPressed && !GameState.isLeftMeowPadActivated) {
      leftMeowPadPressTimer.play();
      isLeftMeowPadPressed = true;
      leftProgressBarTimer =
          new Timeline(
              new KeyFrame(
                  Duration.millis(100), // Update every 100 milliseconds
                  ae -> {
                    if (leftProgressBar.getProgress() < 1.0) {
                      leftProgressBar.setProgress(leftProgressBar.getProgress() + 0.05);
                    }
                  }));
      leftProgressBarTimer.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
      leftProgressBarTimer.play(); // Start the timer
    }
  }

  /**
   * Handles the release event for the left meow pad.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onReleaseLeftMeowPad(MouseEvent event) {
    leftMeowPadPressTimer.stop();
    isLeftMeowPadPressed = false;
    // Stop the leftprogress bar increase if the mouse has been released.
    if (leftProgressBarTimer != null) {
      leftProgressBarTimer.stop();
      leftProgressBarTimer = null;
      //// Set the left progress bar back to 0 if the mouse has been released and GameState has not
      // changed.
      if (!GameState.isLeftMeowPadActivated) {
        leftProgressBar.setProgress(0);
      }
    }
  }

  /**
   * Handles the mouse hover event for the memory game rectangle.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onMouseRectangle(MouseEvent event) {
    // Highlight the memory game rectangle
    memoryGameRectangle.setOpacity(1);

    // Change the appearance when the mouse enters the rectangle
    memoryGameRectangle.setFill(new Color(0.0, 1.0, 0.0, 0.3));
    memoryGameRectangle.setWidth(originalWidth + 5); // Increase width
    memoryGameRectangle.setHeight(originalHeight + 5); // Increase height
  }

  /**
   * Handles the off mouse for the memory game rectangle and changes it back to blue and original
   * size.
   *
   * @param event the mouse event.
   */
  @FXML
  public void offMouseRectangle(MouseEvent event) {
    // Unhighlight the memory game rectangle
    memoryGameRectangle.setOpacity(0.5);

    // Restore the original appearance when the mouse leaves the rectangle
    memoryGameRectangle.setFill(originalColor);
    memoryGameRectangle.setWidth(originalWidth); // Restore width
    memoryGameRectangle.setHeight(originalHeight); // Restore height
  }

  /** Handles the right meow pad activation. */
  private void handleRightMeowPadActivation() {
    // Update GameState
    GameState.isRightMeowPadActivated = true;
    // Reset current hint in rocket if left meow pad is activated
    if (GameState.isLeftMeowPadActivated && GameState.note1Found && GameState.note2Found) {
      resetCurrentHint();
    }

    // Generate message
    // only if both notes are found AND left meow pad is not activated
    if (GameState.note1Found && GameState.note2Found && !GameState.isLeftMeowPadActivated) {
      hideChat();
      // Initiate first message from GPT
      Task<Void> initiateDeviceTask =
          new Task<Void>() {
            // Call GPT
            @Override
            protected Void call() throws Exception {
              ChatMessage chatMessage;
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getRightPadCompleteMessageHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage("user", GptPromptEngineering.getRightPadCompleteMessage()),
                        GptActions.chatCompletionRequest3);
              }

              Platform.runLater(
                  () -> {
                    // Set chat message to text area
                    GptActions.setChatMessage(chatMessage, catTextArea);
                    showChat();
                  });

              // tts for cat speaking
              TextManager.speakChatMessage(chatMessage.getContent());
              return null;
            }
          };

      Thread initiateDeviceThread = new Thread(initiateDeviceTask);
      initiateDeviceThread.start();
    }

    System.out.println("right Meow pad activated");
    // If the task has been done, then it activates
    rightActivateCircle.setVisible(true);
    // if both pads are activated, then the memory game is activated
    if (GameState.isLeftMeowPadActivated && GameState.isRightMeowPadActivated) {
      GameState.isNotesResolved = true;
      System.out.println("2 notes resolved");
      // Enable the memory game rectangle
      memoryGameRectangle.setDisable(false);
      memoryGameRectangle.setVisible(true);

      // disables both notes
      Hud.updateNote1(true, "x0");
      Hud.disableNote1();
      Hud.updateNote2(true, "x0");
      Hud.disableNote2();

      // Generate message
      hideChat();
      // Initiate first message from GPT
      Task<Void> initiateDeviceTask =
          new Task<Void>() {
            // Call GPT
            @Override
            protected Void call() throws Exception {
              // clear messages
              GptActions.clearMessages(GptActions.chatCompletionRequest3);
              GptActions.chatCompletionRequest3 =
                  new ChatCompletionRequest()
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);
              ChatMessage chatMessage;
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getBothPadCompleteMessageHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage("user", GptPromptEngineering.getBothPadCompleteMessage()),
                        GptActions.chatCompletionRequest3);
              }

              Platform.runLater(
                  () -> {
                    // Set chat message to text area
                    GptActions.setChatMessage(chatMessage, catTextArea);
                    showChat();
                  });

              // tts for cat speaking
              TextManager.speakChatMessage(chatMessage.getContent());
              return null;
            }
          };

      Thread initiateDeviceThread = new Thread(initiateDeviceTask);
      initiateDeviceThread.start();
    }
  }

  /** Handles the activation of the left meow pad. */
  private void handleLeftMeowPadActivation() {
    System.out.println("left Meow pad activated");
    // Update GameState
    GameState.isLeftMeowPadActivated = true;
    // Reset current hint in rocket if both notes are found
    if (GameState.note1Found && GameState.note2Found) {
      resetCurrentHint();
    }
    // Generate message
    // only if both notes are found AND right meow pad is not activated
    if (GameState.note1Found && GameState.note2Found && !GameState.isRightMeowPadActivated) {
      hideChat();
      // Initiate first message from GPT
      Task<Void> initiateDeviceTask =
          new Task<Void>() {
            // Call GPT
            @Override
            protected Void call() throws Exception {
              ChatMessage chatMessage;
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getLeftPadCompleteMessageHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage("user", GptPromptEngineering.getLeftPadCompleteMessage()),
                        GptActions.chatCompletionRequest3);
              }

              Platform.runLater(
                  () -> {
                    // Set chat message to text area
                    GptActions.setChatMessage(chatMessage, catTextArea);
                    showChat();
                  });

              // tts for cat speaking
              TextManager.speakChatMessage(chatMessage.getContent());

              return null;
            }
          };

      Thread initiateDeviceThread = new Thread(initiateDeviceTask);
      initiateDeviceThread.start();
    }

    leftActivateCircle.setVisible(true);
    // if both pad are activated, then the memory game is activated
    if (GameState.isLeftMeowPadActivated && GameState.isRightMeowPadActivated) {
      GameState.isNotesResolved = true;
      System.out.println("2 notes resolved");
      // Enable the memory game rectangle
      memoryGameRectangle.setDisable(false);
      memoryGameRectangle.setVisible(true);

      // disables both notes
      Hud.updateNote1(true, "x0");
      Hud.disableNote1();
      Hud.updateNote2(true, "x0");
      Hud.disableNote2();

      // Generate message
      hideChat();
      // Initiate first message from GPT
      Task<Void> initiateDeviceTask =
          new Task<Void>() {
            // Call GPT
            @Override
            protected Void call() throws Exception {
              // clear messages
              GptActions.clearMessages(GptActions.chatCompletionRequest3);
              GptActions.chatCompletionRequest3 =
                  new ChatCompletionRequest()
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);
              ChatMessage chatMessage;
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getBothPadCompleteMessageHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage("user", GptPromptEngineering.getBothPadCompleteMessage()),
                        GptActions.chatCompletionRequest3);
              }

              Platform.runLater(
                  () -> {
                    // Set chat message to text area
                    GptActions.setChatMessage(chatMessage, catTextArea);
                    showChat();
                  });

              // tts for cat speaking
              TextManager.speakChatMessage(chatMessage.getContent());

              return null;
            }
          };

      Thread initiateDeviceThread = new Thread(initiateDeviceTask);
      initiateDeviceThread.start();
    }
  }

  /**
   * Handles the click event on the launch button and switches to the win scene.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickLaunch(MouseEvent event) {
    // if riddle not yet solved
    if (!GameState.isRiddleSolved) {
      initialiseFinalRiddle();
    } else {
      // Update GameState
      GameState.isGameActive = false;
      switchToWin();
      // Stop the timer
      CountDownTimer.countdownTimeline.stop();
      WinController win = (WinController) SceneManager.getController("win");
      // Update the win scene with the time left
      win.getResult()
          .setText(
              "...with " + CountDownTimer.timeToString(CountDownTimer.timeLeft) + " to spare!");
      // If tasks not all completed, then update the win label and task label
      if (!GameState.note1Found || !GameState.note2Found) {
        win.getWonLabel().setText("WON?");
        win.getTaskLabel().setVisible(true);
      }
    }
  }

  /** Switches the scene to the win scene. */
  private void switchToWin() {
    TextManager.close();
    App.setUi(AppUi.WIN);
  }

  /**
   * Getter method for the launch image view.
   *
   * @return the launch image view.
   */
  public ImageView getLaunch() {
    return this.launch;
  }

  /** Initialise cat response upon entering the rocket for the first time. */
  public void catInitialise() {
    // If the room has been entered before, then do nothing
    if (isRoomFirstEntered) {
      return;
    }
    // Disable cat
    catImageActive.setDisable(true);
    // Hide return button
    back.setVisible(false);
    // Hide chat pane
    chatPane.setVisible(false);
    // Hide reply area
    replyTextField.setVisible(false);
    replyImage.setVisible(false);
    replyRectangle.setVisible(false);

    // Initiate first message from GPT

    // Initiate first message from GPT after cat is clicked using a thread
    Task<Void> initiateDeviceTask =
        new Task<Void>() {
          // Call GPT
          @Override
          protected Void call() throws Exception {
            GptActions.chatCompletionRequest3 =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            ChatMessage chatMessage;
            // If no notes are found, if note 1 only is found, if note 2 only is found, if both
            // notes
            if (!GameState.note1Found && !GameState.note2Found) {
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getFirstEnterRocketMessageHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage("user", GptPromptEngineering.getFirstEnterRocketMessage()),
                        GptActions.chatCompletionRequest3);
              }
            } else if (GameState.note1Found && !GameState.note2Found) {
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user",
                            GptPromptEngineering.getFirstEnterRocketMessageNoteOneFoundHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getFirstEnterRocketMessageNoteOneFound()),
                        GptActions.chatCompletionRequest3);
              }
            } else if (!GameState.note1Found && GameState.note2Found) {
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user",
                            GptPromptEngineering.getFirstEnterRocketMessageNoteTwoFoundHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getFirstEnterRocketMessageNoteTwoFound()),
                        GptActions.chatCompletionRequest3);
              }
            } else {
              // depends on difficulty
              if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user",
                            GptPromptEngineering.getFirstEnterRocketMessageBothNotesFoundHard()),
                        GptActions.chatCompletionRequest3);
              } else {
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user",
                            GptPromptEngineering.getFirstEnterRocketMessageBothNotesFound()),
                        GptActions.chatCompletionRequest3);
              }
            }

            Platform.runLater(
                () -> {
                  // Set chat message to text area
                  GptActions.setChatMessage(chatMessage, catTextArea);
                  // Make chat pane visible
                  chatPane.setVisible(true);
                  // Change image to active cat
                  Image image = new Image("images/NeutralCat.png");
                  catImageActive.setImage(image);
                  // Show reply area
                  replyTextField.setVisible(true);
                  replyImage.setVisible(true);
                  replyRectangle.setVisible(true);
                  // Show return button
                  back.setVisible(true);

                  // Enable cat
                  catImageActive.setDisable(false);

                  // assigning task 3
                  Log.showTask3();
                });

            // tts for cat speaking
            TextManager.speakChatMessage(chatMessage.getContent());

            return null;
          }
        };

    Thread initiateDeviceThread = new Thread(initiateDeviceTask);
    initiateDeviceThread.start();

    isRoomFirstEntered = true;
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
    //
    // Check if enter key is pressed
    if (event.getCode().toString().equals("ENTER")) {
      System.out.println("enter pressed");

      // call reply method
      reply();
    }
  }

  /** Reply method which calls GPT and updates the text area. */
  public void reply() {
    // Stop the current text to speech
    TextManager.close();
    // Get message from reply text field and trim
    String message = replyTextField.getText().trim();
    // If message is empty, then do nothing
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
    // hide return button
    back.setVisible(false);

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
                          GptActions.chatCompletionRequest3);
                } else {
                  // If current hint is used
                  if (currentHint) {
                    lastMsg =
                        GptActions.runGpt(
                            new ChatMessage("user", GptPromptEngineering.getHintMessageNone()),
                            GptActions.chatCompletionRequest3);
                  } else {
                    // Update GameState if medium difficulty
                    if (GameSettings.difficulty == GameSettings.GameDifficulty.MEDIUM) {
                      GameState.hintsLeft--;
                    }

                    // If riddle is active
                    if (GameState.isRiddleActive) {
                      lastMsg =
                          GptActions.runGpt(
                              new ChatMessage("user", GptPromptEngineering.getHintMessageRiddle()),
                              GptActions.chatCompletionRequest3);
                    } else {
                      // hints depending on game states
                      if (!GameState.note1Found) {
                        // note 1 hint
                        lastMsg =
                            GptActions.runGpt(
                                new ChatMessage(
                                    "user",
                                    GptPromptEngineering.getHintMessage(
                                        "I recall I dropped a note somewhere outside the rocket"
                                            + " while playing with my toy.")),
                                GptActions.chatCompletionRequest3);
                      } else if (GameState.note1Found && !GameState.note2Found) {
                        // note 2 hint
                        lastMsg =
                            GptActions.runGpt(
                                new ChatMessage(
                                    "user",
                                    GptPromptEngineering.getHintMessage(
                                        "I recall I dropped a note somewhere in the pantry while"
                                            + " eating.")),
                                GptActions.chatCompletionRequest3);
                      } else {
                        if (!GameState.isLeftMeowPadActivated) {
                          // left meow pad hint
                          lastMsg =
                              GptActions.runGpt(
                                  new ChatMessage(
                                      "user",
                                      GptPromptEngineering.getHintMessage(
                                          "The yellow note seems to suggest holding the left meow"
                                              + " pad down.")),
                                  GptActions.chatCompletionRequest3);
                        } else if (GameState.isLeftMeowPadActivated
                            && !GameState.isRightMeowPadActivated) {
                          // right meow pad hint
                          lastMsg =
                              GptActions.runGpt(
                                  new ChatMessage(
                                      "user",
                                      GptPromptEngineering.getHintMessage(
                                          "The pink note seems to suggest wiggling the right meow"
                                              + " pad around.")),
                                  GptActions.chatCompletionRequest3);
                        } else {
                          // memory game hint
                          lastMsg =
                              GptActions.runGpt(
                                  new ChatMessage(
                                      "user",
                                      GptPromptEngineering.getHintMessage(
                                          "The verification puzzle requires you to memorise the"
                                              + " pattern shown then recreate it. Once completed"
                                              + " the button will be unlocked!")),
                                  GptActions.chatCompletionRequest3);
                        }
                      }
                      currentHint = true;
                    }
                    // If hints left is 0
                    if (GameState.hintsLeft == 0 && !GameState.isHintUsed) {
                      hintsUsed();
                    }
                  }
                }
              } else {
                // If the message does not start with 'Meowlp', then call GPT with the original
                // message
                System.out.println("meow");
                ChatMessage msg = new ChatMessage("user", message);
                lastMsg = GptActions.runGpt(msg, GptActions.chatCompletionRequest3);
              }
            } else {
              // If the message does not start with 'Meowlp', then call GPT with the original
              // message
              System.out.println("meow");
              ChatMessage msg = new ChatMessage("user", message);
              lastMsg = GptActions.runGpt(msg, GptActions.chatCompletionRequest3);
            }

            Platform.runLater(
                () -> {
                  // Update text area
                  GptActions.updateTextAreaAll(lastMsg);

                  // Update hint label
                  GameState.updateAllHintsLabel();

                  // Check if message contained 'Correct' when riddle is active
                  if (GameState.isRiddleActive) {
                    if (lastMsg.getContent().contains("Correct")) {
                      // Update GameState
                      GameState.isRiddleSolved = true;
                      GameState.isRiddleActive = false;

                      // enables launch button
                      launch.setDisable(false);
                      // Set the clear box to invisible
                      clearBox.setVisible(false);
                      // add white drop shadow to launch button
                      launch.setStyle(
                          "-fx-effect: dropshadow(three-pass-box, white, 10, 0.4, 0, 0);");
                    }
                  }

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
                  // Show return button
                  back.setVisible(true);
                });
            TextManager.speakChatMessage(lastMsg.getContent());

            return null;
          }
        };

    Thread replyThread = new Thread(replyTask);
    replyThread.start();
  }

  /**
   * Handles the click event on the settings button and switches to the settings scene.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickSetting(MouseEvent event) {
    TextManager.close();
    // Ensure onClickSettings has the  SceneManager.getAppUi(AppUi."currentscene"); to work
    App.setUi(AppUi.SETTING);
    SceneManager.getAppUi(AppUi.ROCKET_INTERIOR);
  }

  /** Switches the scene to the memory game. */
  private void switchToMemoryGame() {
    TextManager.close();
    App.setUi(AppUi.MEMORY_GAME);
    // gives focus to memory game
    Parent memoryGameScene = SceneManager.getAppUi(AppUi.MEMORY_GAME);
    App.getScene().setRoot(memoryGameScene);
    memoryGameScene.requestFocus();
  }

  /** Initialises the final riddle after the memory game is completed. */
  public void initialiseFinalRiddle() {
    System.out.println("riddle");
    // Update GameState
    GameState.isRiddleActive = true;
    // hide return button
    back.setVisible(false);
    hideChat();
    // Initiate first message from GPT
    Task<Void> initiateDeviceTask =
        new Task<Void>() {
          // Call GPT
          @Override
          protected Void call() throws Exception {
            // clear messages
            GptActions.clearMessages(GptActions.chatCompletionRequest3);
            GptActions.chatCompletionRequest3 =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            ChatMessage chatMessage;
            // Random riddle answer
            List<String> riddleAnswers =
                List.of(
                    "Earth",
                    "Planet",
                    "Sun",
                    "Moon",
                    "Star",
                    "Space",
                    "Galaxy",
                    "Comet",
                    "Alien",
                    "Whiskers",
                    "Paws",
                    "Tail");
            Random random = new Random();
            String riddleAnswer = riddleAnswers.get(random.nextInt(riddleAnswers.size()));
            // depends on difficulty
            if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage(
                          "user", GptPromptEngineering.getRiddleWithGivenWordHard(riddleAnswer)),
                      GptActions.chatCompletionRequest3);
            } else {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage(
                          "user", GptPromptEngineering.getRiddleWithGivenWord(riddleAnswer)),
                      GptActions.chatCompletionRequest3);
            }

            Platform.runLater(
                () -> {
                  // Set chat message to text area
                  GptActions.setChatMessage(chatMessage, catTextArea);
                  showChat();
                });

            // tts for cat speaking
            TextManager.speakChatMessage(chatMessage.getContent());

            return null;
          }
        };

    Thread initiateDeviceThread = new Thread(initiateDeviceTask);
    initiateDeviceThread.start();
  }

  /**
   * Handles the escape key press event and switches to the main room if the return button is
   * visible.
   *
   * @param event the key event
   */
  @FXML
  public void onPressKey(KeyEvent event) {
    if (event.getCode() == KeyCode.ESCAPE) {
      // check if return button is visible
      if (back.isVisible()) {

        switchToRoom();
      }
    }
  }

  /**
   * Handles the hover event on interactable objects and scales them up.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the unhover event on interactable objects and scales them down.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleDown(image);
  }

  /** Initialises the left meow pad. */
  private void initialiseLeftMeowPad() {
    // Timer for left meow pad
    leftMeowPadPressTimer =
        new Timeline(
            new KeyFrame(
                Duration.seconds(2),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    handleLeftMeowPadActivation();
                  }
                }));
    leftMeowPadPressTimer.setCycleCount(1);
    leftMeowPadPressTimer.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            // Sets left meow pad to not pressed once timer is finished
            isLeftMeowPadPressed = false;
          }
        });
  }

  /**
   * Getter method for the cat text area.
   *
   * @return the cat text area.
   */
  public TextArea getCatTextArea() {
    return catTextArea;
  }

  /** Hides all chat elements for when GPT is generating a reponse. */
  public void hideChat() {
    TextManager.close();
    // Hide catImageSleep
    catImageSleep.setVisible(false);
    // hide catImageAwoken
    catImageAwoken.setVisible(false);
    // Show catImageActive
    catImageActive.setVisible(true);
    // Change image to thinking cat
    Image image = new Image("images/ThinkingCat.png");
    catImageActive.setImage(image);
    // Disable cat
    catImageActive.setDisable(true);
    // hide return button
    back.setVisible(false);
    // hide current chat pane
    chatPane.setVisible(false);
    // hide reply area
    replyTextField.setVisible(false);
    replyImage.setVisible(false);
    replyRectangle.setVisible(false);
  }

  /** Shows all chat elements for when GPT is generating a response. */
  public void showChat() {
    // Make chat pane visible
    chatPane.setVisible(true);
    // Change image to active cat
    Image image = new Image("images/NeutralCat.png");
    catImageActive.setImage(image);
    // Show reply area
    replyTextField.setVisible(true);
    replyImage.setVisible(true);
    replyRectangle.setVisible(true);

    // Enable cat
    catImageActive.setDisable(false);
    // show return button
    back.setVisible(true);
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

  /** Method to update hint labels on the scene. */
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

  /** Method to reset current hint back to false. */
  public void resetCurrentHint() {
    currentHint = false;
  }

  /**
   * Getter method for memory game rectangle.
   *
   * @return memory game rectangle.
   */
  public Rectangle getMemoryGameRectangle() {
    return memoryGameRectangle;
  }

  // Hud highlight methods

  /**
   * Handles the hover event on the torch and shows the highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverTorch(MouseEvent event) {
    highlightTorch.setVisible(true);
  }

  /**
   * Handles the unhover event on the torch and hides the highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveTorch(MouseEvent event) {
    highlightTorch.setVisible(false);
  }

  /**
   * Handles the hover event on the note 1 and shows the highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverNote1(MouseEvent event) {
    highlightNote1.setVisible(true);
  }

  /**
   * Handles the unhover event on the note 1 and hides the highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveNote1(MouseEvent event) {
    highlightNote1.setVisible(false);
  }

  /**
   * Handles the hover event on the note 2 and shows the highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverNote2(MouseEvent event) {
    highlightNote2.setVisible(true);
  }

  /**
   * Handles the unhover event on the note 2 and hides the highlight.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveNote2(MouseEvent event) {
    highlightNote2.setVisible(false);
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

  /*
   * Getter method to get the clearBox image to use in memoryGameController
   *
   * @param event the mouse event.
   */
  public ImageView getClearBox() {
    return clearBox;
  }
}
