import java.util.*;
import java.util.function.BiPredicate;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Dude_Full implements MovingEntity {
    private String id;
    private final PathingStrategy pathingStrategy = new AStarPathingStrategy();
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private double actionPeriod;
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

    public double getActionPeriod() {
        return this.actionPeriod;
    }

    public Dude_Full(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }


    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        // Define withinReach condition here
        BiPredicate<Point, Point> withinReach = (currentPosition, goalPosition) -> currentPosition.adjacent(goalPosition);

        List<Point> path = pathingStrategy.computePath(this.position, target.getPosition(),
                p -> world.withinBounds(p) && !world.isOccupied(p),
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        int i;
        if (this.pathingStrategy instanceof AStarPathingStrategy){
            i = 1;
        } else {
            i = 0;
        }

        if (path != null && path.size() > i && world.withinBounds(path.get(i))) {
            Point nextPos = path.get(i);
            world.moveEntity(scheduler, this, nextPos);
            this.position = nextPos; // Update the entity's position
            return false;
        }

        // Check if the target is not a house before removing
        if (!(target instanceof House)) {
            world.removeEntity(scheduler, target);
        }

        // Find a new target, excluding houses
        Optional<Entity> newTarget = world.findNearest(this.position, new ArrayList<>(List.of(Stump.class)));

        // Select a new target and attempt to move again
        newTarget.ifPresent(entity -> move(world, entity, scheduler));
        return true;
    }





    private void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        MovingEntity dude = (MovingEntity) Factory.createDudeNotFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);
        world.removeEntity(scheduler, this);
        world.addEntity(dude);
        dude.scheduleActivity(scheduler, world, imageStore);
        dude.scheduleAnimation(scheduler);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && this.move(world, fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }



}
