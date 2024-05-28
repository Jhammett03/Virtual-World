import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Stump implements Entity {
    private String id;
    private Point position;
    private List<PImage> images;

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public List<PImage> getImages() {
        return this.images;
    }

    @Override
    public boolean isObstacle() {
        return true;
    }

    public Stump(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
    }

}
