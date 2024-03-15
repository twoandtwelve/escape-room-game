package nz.ac.auckland.se206.controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.FoodRecipe;
import nz.ac.auckland.se206.GameSettings;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GptActions;
import nz.ac.auckland.se206.HoverManager;
import nz.ac.auckland.se206.Log;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.TextManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;

/**
 * Controller for the pantry interior screen.
 *
 * <p>Handles the click and hover events for the ingredients, notes, cat and chat.
 */
public class PantryController {

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
  @FXML private Rectangle backButton;
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

  // Room elements
  @FXML private Pane pane;
  @FXML private ImageView back;
  @FXML private ImageView pantryImage;
  @FXML private ImageView plantImage;
  @FXML private Pane note1Pane;
  @FXML private Pane note2Pane;

  // Food ingredients
  @FXML private ImageView ingredientMilk;
  @FXML private ImageView ingredientCheese;
  @FXML private ImageView ingredientCarrot;
  @FXML private ImageView ingredientMushroom;
  @FXML private ImageView ingredientBeer;
  @FXML private ImageView ingredientBurger;
  @FXML private ImageView ingredientToast;
  @FXML private ImageView ingredientPudding;
  @FXML private ImageView ingredientFish;
  @FXML private ImageView ingredientBanana;
  @FXML private ImageView ingredientLollipop;
  @FXML private ImageView ingredientMeat;
  @FXML private ImageView ingredientChicken;
  @FXML private ImageView ingredientEgg;
  @FXML private ImageView ingredientPear;
  @FXML private ImageView ingredientHotdog;
  @FXML private ImageView ingredientIceCream;
  @FXML private ImageView ingredientOnigiri;

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

  // Text to speech manager
  private TextManager textManager = new TextManager();

  private int currentHint = 1;

  // Array to store all the ingredients to later shadow them
  private ArrayList<ImageView> shadowArray = new ArrayList<>();

  /** Initialise method for the pantry. */
  public void initialize() {
    // Add all hud elements to an array
    hudElements = new ArrayList<Object>();
    hudElements.add(torch);
    hudElements.add(note1);
    hudElements.add(note2);
    hudElements.add(torchCount);
    hudElements.add(note1Count);
    hudElements.add(note2Count);

    // Add all task elements to an array
    taskList = new ArrayList<Label>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);

    // assigns a value 1-3 to each food item
    initialiseUserData();

    // adds ingredients to collections in FoodRecipe
    storeIngredients();

    // creates a random recipe the player will have to replicate
    FoodRecipe.initialiseDesiredRecipe();

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
   * Method to put drop shadow behind image if it is selected.
   *
   * @param image the image to put drop shadow behind.
   * @param colour the colour of the drop shadow.
   */
  public void dropShadow(ImageView image, String colour) {
    DropShadow dropShadow = new DropShadow();
    // if colour selected is already white then stay white otherwise become green
    if (colour.equals("WHITE")) {
      dropShadow.setColor(javafx.scene.paint.Color.WHITE);
    } else {
      dropShadow.setColor(javafx.scene.paint.Color.GREEN);
    }
    // set dropshadow values
    dropShadow.setRadius(10.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    // apply the effect to the image
    image.setEffect(dropShadow);
    shadowArray.add(image);
  }

  /** Method to remove the selected highlight from all the array elements. */
  public void removeShadow() {
    for (ImageView imageView : shadowArray) {
      imageView.setEffect(null);
    }
  }

  /**
   * Handles the click event on the ingredients.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickIngredient(MouseEvent event) {
    TextManager.close();

    // Get the image view of the ingredient clicked
    ImageView ingredient = (ImageView) event.getTarget();
    // If the ingredient is already selected or the recipe is resolved then return
    if (FoodRecipe.playerRecipe.contains(ingredient) || GameState.isRecipeResolved) {
      return;
    }
    // Add the ingredient to the player recipe
    FoodRecipe.playerRecipe.add(ingredient);
    dropShadow(ingredient, "WHITE");

    // If the player recipe is full then check if it is correct
    if (FoodRecipe.playerRecipe.size() == 3) {
      if (FoodRecipe.checkEqual(FoodRecipe.desiredRecipe, FoodRecipe.playerRecipe)) {
        GameState.isRecipeResolved = true;

        // completes task 2
        Log.completeTask2();

        // Drop shadow behind all desired ingredients
        for (ImageView desired : FoodRecipe.desiredRecipe) {
          dropShadow(desired, "GREEN");
        }

        // enable plant
        plantImage.setDisable(false);
        // Cat response
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
        // message from GPT
        Task<Void> initiateDeviceTask =
            new Task<Void>() {
              // Call GPT
              @Override
              protected Void call() throws Exception {
                // clear messages
                GptActions.clearMessages(GptActions.chatCompletionRequest2);
                GptActions.chatCompletionRequest2 =
                    new ChatCompletionRequest()
                        .setN(1)
                        .setTemperature(0.2)
                        .setTopP(0.5)
                        .setMaxTokens(100);
                ChatMessage chatMessage;
                chatMessage =
                    GptActions.runGpt(
                        new ChatMessage(
                            "user", GptPromptEngineering.getFinishPantryPuzzleMessage()),
                        GptActions.chatCompletionRequest2);

                Platform.runLater(
                    () -> {
                      // Update all text areas
                      GptActions.updateTextAreaAll(chatMessage);
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
                    });
                // tts for cat speaking
                TextManager.speakChatMessage(chatMessage.getContent());

                return null;
              }
            };

        Thread initiateDeviceThread = new Thread(initiateDeviceTask);
        initiateDeviceThread.start();
      } else {
        FoodRecipe.playerRecipe.clear();
        removeShadow();
        // Cat response if incorrect dish
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
        // message from GPT
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
                              "user",
                              GptPromptEngineering.getWrongDishPantryMessageHard(FoodRecipe.food)),
                          GptActions.chatCompletionRequest2);
                } else {
                  chatMessage =
                      GptActions.runGpt(
                          new ChatMessage(
                              "user",
                              GptPromptEngineering.getWrongDishPantryMessage(FoodRecipe.food)),
                          GptActions.chatCompletionRequest2);
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

                      // Enable cat
                      catImageActive.setDisable(false);
                      // show return button
                      back.setVisible(true);
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
   * Handles the click event on the note 1 return button which hides the note 1 pane.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote1Return(MouseEvent event) {
    TextManager.close();
    note1Pane.setVisible(false);
  }

  /**
   * Handles the click event on the note 2 return button which hides the note 2 pane.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickNote2Return(MouseEvent event) {
    TextManager.close();
    note2Pane.setVisible(false);
  }

  /**
   * Handles the click event on the back button which switches to the main room.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickBack(MouseEvent event) {

    switchToRoom();
  }

  /**
   * Handles the escape key press event which switches to the main room if the back button is
   * visible.
   *
   * @param event the mouse event.
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

  /** Switches the scene to the main room. */
  private void switchToRoom() {
    TextManager.close();
    App.setUi(AppUi.MAIN_ROOM);
  }

  /** Initialise cat response upon entering the pantry for the first time. */
  public void catInitialise() {
    // If pantry has been entered before then return
    if (GameState.isPantryFirstEntered) {
      return;
    }
    // Disable cat
    catImageActive.setDisable(true);
    // Hide return button
    back.setVisible(false);
    // Initiate first message from GPT
    Task<Void> initiateDeviceTask =
        new Task<Void>() {
          // Call GPT
          @Override
          protected Void call() throws Exception {
            GptActions.chatCompletionRequest2 =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            ChatMessage chatMessage;

            // If hard difficulty then call GPT with hard message and etc depending on difficulty
            if (GameSettings.difficulty == GameSettings.GameDifficulty.HARD) {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage(
                          "user",
                          GptPromptEngineering.getFirstEnterPantryMessageHard(FoodRecipe.food)),
                      GptActions.chatCompletionRequest2);
            } else {
              chatMessage =
                  GptActions.runGpt(
                      new ChatMessage(
                          "user", GptPromptEngineering.getFirstEnterPantryMessage(FoodRecipe.food)),
                      GptActions.chatCompletionRequest2);
            }

            Platform.runLater(
                () -> {
                  // Update all text areas
                  GptActions.updateTextAreaAll(chatMessage);
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

                  // assigning task 2 for main room log
                  Log.showTask2();
                });
            // text to speech for cat speaking
            TextManager.speakChatMessage(chatMessage.getContent());

            return null;
          }
        };

    Thread initiateDeviceThread = new Thread(initiateDeviceTask);
    initiateDeviceThread.start();

    GameState.isPantryFirstEntered = true;
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
    System.out.println("cat clicked");
    TextManager.close();
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
   * @param event the key event
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

  /** Handles the GPT calling when replying. */
  public void reply() {
    // Stop the current text to speech
    TextManager.close();
    // Get message from reply text field and trim
    String message = replyTextField.getText().trim();
    // If message is empty then return
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
                          GptActions.chatCompletionRequest2);
                } else {
                  // If current hint is 4
                  if (currentHint >= 4) {
                    lastMsg =
                        GptActions.runGpt(
                            new ChatMessage("user", GptPromptEngineering.getHintMessageNone()),
                            GptActions.chatCompletionRequest2);
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
                                      "Each word in "
                                          + FoodRecipe.food
                                          + " represents an ingredient that should be taken from"
                                          + " the shelf.")),
                              GptActions.chatCompletionRequest2);
                    } else if (currentHint == 2) {
                      lastMsg =
                          GptActions.runGpt(
                              new ChatMessage(
                                  "user",
                                  GptPromptEngineering.getHintMessage(
                                      "An example with 'Sweet Jiggly Fish', the lollipop, pudding"
                                          + " and fish should be taken.")),
                              GptActions.chatCompletionRequest2);
                    } else {
                      String recipe =
                          FoodRecipe.desiredRecipe.get(0).getId().substring(10).toLowerCase();
                      lastMsg =
                          GptActions.runGpt(
                              new ChatMessage(
                                  "user",
                                  GptPromptEngineering.getHintMessage(
                                      "One of the items you should bring is " + recipe + ".")),
                              GptActions.chatCompletionRequest2);
                    }
                    currentHint++;
                    // If hints left is 0
                    if (GameState.hintsLeft == 0 && !GameState.isHintUsed) {
                      hintsUsed();
                    }
                  }
                }
              } else {
                // If the message does not start with 'Meowlp' then call GPT with the original
                // message
                System.out.println("meow");
                ChatMessage msg = new ChatMessage("user", message);
                lastMsg = GptActions.runGpt(msg, GptActions.chatCompletionRequest2);
              }
            } else {
              // If the message does not start with 'Meowlp' then call GPT with the original message
              System.out.println("meow");
              ChatMessage msg = new ChatMessage("user", message);
              lastMsg = GptActions.runGpt(msg, GptActions.chatCompletionRequest2);
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
                  // Show return button
                  back.setVisible(true);
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
   * Handles the click event on the plant.
   *
   * @param event the mouse event.
   */
  @FXML
  public void clickPlant(MouseEvent event) {
    TextManager.close();
    // disable plant
    plantImage.setDisable(true);

    App.setUi(AppUi.TREE);

    // update game state
    GameState.note2Found = true;
  }

  /**
   * Handles the click event on the setting button which switches to the setting scene.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onClickSetting(MouseEvent event) {
    TextManager.close();

    // Ensure onClickSettings has the  SceneManager.getAppUi(AppUi."currentscene"); to work
    App.setUi(AppUi.SETTING);
    SceneManager.getAppUi(AppUi.PANTRY_INTERIOR);
  }

  /** Method to initialise the user data for each ingredient. */
  private void initialiseUserData() {
    // 1 indicates adjective ingredient
    // 2 indicates noun ingredient
    // 3 indicates base ingredient
    ingredientMilk.setUserData(1);
    ingredientCheese.setUserData(1);
    ingredientCarrot.setUserData(2);
    ingredientMushroom.setUserData(2);
    ingredientBeer.setUserData(1);
    ingredientBurger.setUserData(3);
    ingredientToast.setUserData(3);
    ingredientPudding.setUserData(1);
    ingredientFish.setUserData(2);
    ingredientBanana.setUserData(2);
    ingredientLollipop.setUserData(1);
    ingredientMeat.setUserData(2);
    ingredientChicken.setUserData(2);
    ingredientEgg.setUserData(3);
    ingredientPear.setUserData(1);
    ingredientHotdog.setUserData(3);
    ingredientIceCream.setUserData(3);
    ingredientOnigiri.setUserData(3);
  }

  /** Method to store all the ingredients in the FoodRecipe class. */
  private void storeIngredients() {
    // get all fields in PantryController
    Field[] fields = PantryController.class.getDeclaredFields();
    // iterate through all fields
    for (Field field : fields) {
      // checks if the field is an ImageView and its name starts with "ingredient"
      if (field.getType() == ImageView.class && field.getName().startsWith("ingredient")) {
        ImageView ingredient = null;
        try {
          field.setAccessible(true);
          ingredient = (ImageView) field.get(this);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        // if the ingredient is not null then add it to the appropriate collection in FoodRecipe
        if (ingredient != null) {
          int userData = (int) ingredient.getUserData();
          if (userData == 1 || userData == 2) {
            FoodRecipe.prefixIngredient.add(ingredient);
          } else if (userData == 3) {
            FoodRecipe.baseIngredient.add(ingredient);
          }
        }
      }
    }
  }

  /**
   * Handles the hover event on interactable elements by scaling them up.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverInteractable(MouseEvent event) {
    ImageView image = (ImageView) (Node) event.getTarget();
    HoverManager.scaleUp(image);
  }

  /**
   * Handles the unhover event on interactable elements by scaling them down.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveInteractable(MouseEvent event) {
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

  /** Method to update hint label on the scene. */
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

  // Hud highlight methods

  /**
   * Handles the hover event on the torch by making the highlight visible.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverTorch(MouseEvent event) {
    highlightTorch.setVisible(true);
  }

  /**
   * Handles the unhover event on the torch by making the highlight invisible.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveTorch(MouseEvent event) {
    highlightTorch.setVisible(false);
  }

  /**
   * Handles the hover event on note 1 by making the highlight visible.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverNote1(MouseEvent event) {
    highlightNote1.setVisible(true);
  }

  /**
   * Handles the unhover event on note 1 by making the highlight invisible.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onLeaveNote1(MouseEvent event) {
    highlightNote1.setVisible(false);
  }

  /**
   * Handles the hover event on note 2 by making the highlight visible.
   *
   * @param event the mouse event.
   */
  @FXML
  public void onHoverNote2(MouseEvent event) {
    highlightNote2.setVisible(true);
  }

  /**
   * Handles the unhover event on note 2 by making the highlight invisible.
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
}
