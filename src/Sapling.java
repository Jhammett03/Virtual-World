import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Sapling extends TransformingPlant {
    private static final String STUMP_KEY = "stump";
    private static final String TREE_KEY = "tree";
    private static final double TREE_ANIMATION_MAX = 0.600;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;
    private int healthLimit;


    private static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    private static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    public Sapling(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, actionPeriod, animationPeriod, health);
        this.healthLimit = healthLimit;
    }

    protected boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) { // move to Entity
        if (this.getHealth() <= 0) {
            Entity stump = Factory.createStump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (this.getHealth() >= this.healthLimit) {
            TransformingPlant tree = (TransformingPlant) Factory.createTree(TREE_KEY + "_" + this.getId(), this.getPosition(), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActivity(scheduler, world, imageStore);
            tree.scheduleAnimation(scheduler);

            return true;
        }

        return false;
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) { // move to Entity
        this.setHealth(this.getHealth() + 1);
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    @Override
    public boolean isObstacle() {
        return true;
    }
}
