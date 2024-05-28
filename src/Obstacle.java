import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Obstacle implements AnimatedEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double animationPeriod;


    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getImageIndex() {
        return this.imageIndex;
    }

    public void setImageIndex(int i) {
        this.imageIndex = i;
    }

    public List<PImage> getImages() {
        return this.images;
    }

    @Override
    public boolean isObstacle() {
        return true;
    }

    public double getAnimationPeriod() { // move to Entity
        return this.animationPeriod;
    }

    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
    }
}

