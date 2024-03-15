package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.ImageView;

/**
 * Stores the desired recipe, the player recipe and the ingredients.
 *
 * <p>Stores the desired recipe, the player recipe and the ingredients. The desired recipe is
 * initialised with 3 random ingredients. The player recipe is cleared when the player loses or
 * wins.
 */
public class FoodRecipe {

  // Stores the desired recipe, the player recipe and the ingredients
  public static ArrayList<ImageView> prefixIngredient = new ArrayList<ImageView>();
  public static ArrayList<ImageView> baseIngredient = new ArrayList<ImageView>();
  public static ArrayList<ImageView> desiredRecipe = new ArrayList<ImageView>();
  public static ArrayList<ImageView> playerRecipe = new ArrayList<ImageView>();
  public static String food;

  /** Initialises the desired recipe with 3 random ingredients. */
  public static void initialiseDesiredRecipe() {

    // adds 1 random base ingredient
    int randomIndex = ThreadLocalRandom.current().nextInt(0, baseIngredient.size());
    desiredRecipe.add(baseIngredient.get(randomIndex));

    // adds 2 random prefix ingredients
    int firstRandomPrefixIndex;
    int secondRandomPrefixIndex;
    do {
      // makes sure the 2 random prefix ingredients are not the same
      firstRandomPrefixIndex = ThreadLocalRandom.current().nextInt(0, prefixIngredient.size());
      secondRandomPrefixIndex = ThreadLocalRandom.current().nextInt(0, prefixIngredient.size());
    } while (firstRandomPrefixIndex == secondRandomPrefixIndex);
    desiredRecipe.add(prefixIngredient.get(firstRandomPrefixIndex));
    desiredRecipe.add(prefixIngredient.get(secondRandomPrefixIndex));

    // reorders the recipe to be in the correct order
    FoodRecipe.reorderRecipe(FoodRecipe.desiredRecipe);
    food = FoodRecipe.recipeToString(FoodRecipe.desiredRecipe);
  }

  /**
   * Converts the recipe to a string.
   *
   * @param recipe the recipe to convert.
   * @return the recipe as a string.
   */
  public static String recipeToString(ArrayList<ImageView> recipe) {
    // create a StringBuilder to efficiently build the string
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < recipe.size(); i++) {
      ImageView ingredient = recipe.get(i);
      String add = ingredientToString(ingredient);
      builder.append(add);

      if (i == 2) {
        break;
      }

      if (i == 0) {
        // on ingredient #1
        if ((int) ingredient.getUserData() == 1
            && (i + 1 < recipe.size())
            && (int) recipe.get(i + 1).getUserData() == 1) {
          builder.append("-");
        } else if ((int) ingredient.getUserData() == 2
            && (i + 1 < recipe.size())
            && (int) recipe.get(i + 1).getUserData() == 2) {
          builder.append(" & ");
        } else {
          builder.append(" ");
        }
      } else {
        builder.append(" ");
      }
    }

    return builder.toString();
  }

  /**
   * Reorders the recipe to be in the correct order.
   *
   * @param recipe the recipe to reorder.
   */
  public static void reorderRecipe(ArrayList<ImageView> recipe) {
    // Comparator to sort the recipe
    Comparator<ImageView> comparator =
        new Comparator<ImageView>() {
          @Override
          public int compare(ImageView img1, ImageView img2) {
            int userData1 = (int) img1.getUserData();
            int userData2 = (int) img2.getUserData();
            return Integer.compare(userData1, userData2);
          }
        };

    // Sort the recipe
    Collections.sort(recipe, comparator);
  }

  /**
   * Takes the ingredient imageview and translates it to its string name.
   *
   * @param ingredient the ingredient to convert.
   * @return the ingredient as a string.
   */
  public static String ingredientToString(ImageView ingredient) {

    String string = "";
    // dependent on the name of the imageview, set its string
    // make it have the correct casing
    switch (ingredient.getId().substring(10).toLowerCase()) {
      case "milk":
        string = "Milky";
        break;
      case "cheese":
        string = "Cheesy";
        break;
        // make it have the correct casing
      case "carrot":
        string = "Carrot";
        break;
      case "mushroom":
        string = "Mushroom";
        break;
      case "beer":
        string = "Tipsy";
        break;
      case "burger":
        string = "Burger";
        break;
      case "toast":
        string = "Sandwich";
        break;
        // make it have the correct casing
      case "pudding":
        string = "Jiggly";
        break;
      case "fish":
        string = "Fish";
        break;
      case "banana":
        string = "Banana";
        break;
      case "lollipop":
        string = "Sweet";
        break;
      case "meat":
        string = "Meat";
        break;
        // make it have the correct casing
      case "chicken":
        string = "Chicken";
        break;
      case "egg":
        string = "Omelette";
        break;
      case "pear":
        string = "Fruity";
        break;
        // make it have the correct casing
      case "hotdog":
        string = "Hotdog";
        break;
      case "icecream":
        string = "Ice Cream";
        break;
      case "onigiri":
        string = "Onigiri";
        break;
    }
    return string;
  }

  /**
   * Checks if one recipe is equal to another.
   *
   * @param recipe1 the first recipe.
   * @param recipe2 the second recipe.
   * @return true if the recipes are equal, false otherwise.
   */
  public static boolean checkEqual(ArrayList<ImageView> recipe1, ArrayList<ImageView> recipe2) {
    // if the recipes are different sizes, they are not equal
    if (recipe1.size() != recipe2.size()) {
      return false;
    }
    // if the recipes are the same size, check if they contain the same ingredients
    for (ImageView ingredient : recipe1) {
      if (!recipe2.contains(ingredient)) {
        return false;
      }
    }
    // if the recipes are the same size and contain the same ingredients, they are equal
    return true;
  }
}
