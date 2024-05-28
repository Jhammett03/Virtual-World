import java.util.List;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background {
    private String id;
    private List<PImage> images;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }
    public PImage getCurrentImage() { // double between Entity and Background
        int imageIndex = 0; // this used to be an instance variable for Background but was always 0
        return this.images.get(imageIndex);
    }
}
