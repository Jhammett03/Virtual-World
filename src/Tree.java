import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Tree extends TransformingPlant {
    public static final String STUMP_KEY = "stump";

    public Tree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        super(id, position, images, actionPeriod, animationPeriod, health);
    }

    protected boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) { // move to Entity
        if (this.getHealth() <= 0) {
            Entity stump = Factory.createStump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) { // move to Entity

        if (!this.transform(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public boolean isObstacle() {
        return true;
    }
}
