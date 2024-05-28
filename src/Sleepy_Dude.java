import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Sleepy_Dude implements MovingEntity {
    private String id;
    private final PathingStrategy pathingStrategy = new AStarPathingStrategy();
    private Point position;
    private List<PImage> images;
    private int imageIndex = 0;
    private int resourceLimit;
    private double actionPeriod;
    private double animationPeriod;
    private int sleepDuration = 10;

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

    public double getActionPeriod() {
        return this.actionPeriod;
    }

    public Sleepy_Dude(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        MovingEntity dude = (MovingEntity) Factory.createDudeNotFull("dude", this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, imageStore.getImageList("dude"));
        world.removeEntity(scheduler, this);
        System.out.println(this.getImageIndex());


        // Unscheduling all events associated with Sleepy_Dude
        scheduler.unscheduleAllEvents(this);

        world.addEntity(dude);
        dude.scheduleActivity(scheduler, world, imageStore);
        dude.scheduleAnimation(scheduler);
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (sleepDuration > 0) {
            // Decrease sleep duration and reschedule
            sleepDuration--;
            scheduler.scheduleEvent(this, (Action) -> executeActivity(world, imageStore, scheduler), this.actionPeriod);
        } else {
            transformFull(world, scheduler, imageStore);
        }
    }

    @Override
    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        return false;
    }

}