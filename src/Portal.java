import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Portal implements Entity, AnimatedEntity{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;
    private int health;
    private String linkedPortalId; // Added field
    private Point linkedPortalPosition; // Added field

    private Animation animation;

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
        return false;
    }


    public double getActionPeriod() {
        return this.actionPeriod;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public static final String PORTAL_KEY = "portal";

    public Portal(String id, Point position, List<PImage> images,
                  double actionPeriod, double animationPeriod,
                  String linkedPortalId, Point linkedPortalPosition) {
        this.id = id;
        this.position = position;
        this.images = images;  // Initialize the images field with the provided images
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.linkedPortalId = linkedPortalId;
        this.linkedPortalPosition = linkedPortalPosition;
    }
    public Point getLinkedPortalPosition(){
        return linkedPortalPosition;
    }
    @Override
    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    public void setLinkedPortalPosition(Point linkedPortalPosition) {
        this.linkedPortalPosition = linkedPortalPosition;
    }
    @Override
    public void setImageIndex(int i) {
        this.imageIndex = i;
    }

    @Override
    public int getImageIndex() {
        return this.imageIndex;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Your existing logic...

        // Schedule animation action
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

}
