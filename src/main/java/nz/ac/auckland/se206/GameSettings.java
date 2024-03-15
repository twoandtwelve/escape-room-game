package nz.ac.auckland.se206;

/** Stores the game settings chosen by the player at the menu screen. */
public class GameSettings {

  /** Enum for the game difficulties. */
  public enum GameDifficulty {
    EASY,
    MEDIUM,
    HARD
  }

  public static GameDifficulty difficulty = null;

  public static int timeLimit = 0;
}
