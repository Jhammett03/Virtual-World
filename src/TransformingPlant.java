import processing.core.PImage;

import java.util.List;

public abstract class TransformingPlant implements ActiveEntity, AnimatedEntity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;
    private int health;

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public int getImageIndex() {
        return this.imageIndex;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setImageIndex(int i) {
        this.imageIndex = i;
    }

    public List<PImage> getImages() {
        return this.images;
    }

    public double getAnimationPeriod() { // move to Entity
        return this.animationPeriod;
    }

    public double getActionPeriod() {return this.actionPeriod;}

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public TransformingPlant(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
    }

    protected abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

}