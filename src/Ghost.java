import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Ghost implements MovingEntity, Entity {

    public static final String HOUSE_KEY = "house";
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;

    private PathingStrategy pathingStrategy;

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

    public PathingStrategy getPathingStrategy() {
        return pathingStrategy;
    }

    public Ghost(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.pathingStrategy = new AStarPathingStrategy(); // Instantiate A* pathing strategy
    }

    private Point goalPosition;



    // Override the move method to use A* pathing
    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {

        // Define withinReach condition here
        BiPredicate<Point, Point> withinReach = (currentPosition, goalPosition) -> currentPosition.adjacent(goalPosition);

        List<Point> path = pathingStrategy.computePath(this.position, target.getPosition(),
                p -> world.withinBounds(p) && !world.isOccupied(p),
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        //indexing error workaround for easily switching between pathing strategies
        int i;
        if (this.pathingStrategy instanceof AStarPathingStrategy) {
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
        world.removeEntity(scheduler, target);
        Optional<Entity> newTarget = world.findNearest(this.position, new ArrayList<>(List.of(Stump.class)));

        // Select a new target and attempt to move again
//            newTarget.ifPresent(entity -> move(world, entity, scheduler));
        return true;
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) { // move to Entity
        Optional<Entity> ghostTarget = world.findNearest(this.position, new ArrayList<>(List.of(Dude_Not_Full.class)));

        if (ghostTarget.isPresent()) {
            Point tgtPos = ghostTarget.get().getPosition();

            if (this.move(world, ghostTarget.get(), scheduler)) {

                MovingEntity sleepyDude = (MovingEntity) Factory.createSleepyDude("sleepyDude", this.position, this.actionPeriod, this.animationPeriod, 3, imageStore.getImageList("sleepyDude"));
                world.addEntity(sleepyDude);
                sleepyDude.scheduleActivity(scheduler, world, imageStore);
                sleepyDude.scheduleAnimation(scheduler);

            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.actionPeriod);
    }
}
