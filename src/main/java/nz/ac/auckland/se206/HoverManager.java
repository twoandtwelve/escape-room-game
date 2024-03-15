package nz.ac.auckland.se206;

import javafx.scene.image.ImageView;

/**
 * Scales up and down images.
 *
 * <p>Used for hovering over objects.
 */
public class HoverManager {

  // used for hovering over objects
  private static double originalWidth;
  private static double originalHeight;
  private static double originalX;
  private static double originalY;

  /**
   * Method which scales up the image given.
   *
   * @param image the image to scale up.
   */
  public static void scaleUp(ImageView image) {
    // storing original image parameters
    originalWidth = image.getFitWidth();
    originalHeight = image.getFitHeight();
    originalX = image.getLayoutX();
    originalY = image.getLayoutY();

    // calculating scaled image sizes
    double aspectRatio = originalWidth / originalHeight;
    double newWidth = originalWidth + 6;
    double newHeight = newWidth / aspectRatio;

    // calculating the difference in width and height
    double widthDiff = newWidth - originalWidth;
    double heightDiff = newHeight - originalHeight;

    // adjusting the layout position to maintain the same center point
    image.setLayoutX(image.getLayoutX() - widthDiff / 2);
    image.setLayoutY(image.getLayoutY() - heightDiff / 2);

    image.setFitWidth(newWidth);
    image.setFitHeight(newHeight);
  }

  /**
   * Method which scales down the image given.
   *
   * @param image the image to scale down.
   */
  public static void scaleDown(ImageView image) {

    // reverts to the original width and height
    image.setFitWidth(originalWidth);
    image.setFitHeight(originalHeight);

    // reverts to the original position
    image.setLayoutX(originalX);
    image.setLayoutY(originalY);
  }
}
