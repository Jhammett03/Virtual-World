import processing.core.PImage;
public class ImageUtils {

    public static PImage applyTint(PImage image, int red, int green, int blue) {
        PImage tinted = image.copy();
        tinted.loadPixels();

        for (int i = 0; i < tinted.pixels.length; i++) {
            int pixel = tinted.pixels[i];
            int originalRed = (pixel >> 16) & 0xFF;
            int originalGreen = (pixel >> 8) & 0xFF;
            int originalBlue = pixel & 0xFF;

            // Combine the original color with the tint color
            int newRed = (originalRed + red) / 2;
            int newGreen = (originalGreen + green) / 2;
            int newBlue = (originalBlue + blue) / 2;

            tinted.pixels[i] = 0xFF000000 | (newRed << 16) | (newGreen << 8) | newBlue;
        }

        tinted.updatePixels();
        return tinted;
    }
}
