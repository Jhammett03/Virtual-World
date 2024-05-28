import java.util.*;
import java.util.function.BiPredicate;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Dude_Not_Full implements MovingEntity {
    private final PathingStrategy pathingStrategy = new AStarPathingStrategy();
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private double actionPeriod;
    private double animationPeriod;
    private boolean inRangeOfPortal;


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

    public double getAnimationPeriod() { // move to Entity
        return this.animationPeriod;
    }

    public List<PImage> getImages() {
        return this.images;
    }

    @Override
    public boolean isObstacle() {
        return true;
    }

    public double getActionPeriod() {return this.actionPeriod;}

    public Dude_Not_Full(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.inRangeOfPortal = false;
    }


    @Override
    public PImage getCurrentImage() {
        if (inRangeOfPortal) {
            // Apply a purple tint to the current image
            return ImageUtils.applyTint(images.get(imageIndex), 255, 0, 255);
        } else {
            // Return the original image
            return images.get(imageIndex);
        }
    }

    public void updateInRangeOfPortal(boolean inRange) {
        this.inRangeOfPortal = inRange;
    }



    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) { // likely move to Entity
        // Define withinReach condition here
        BiPredicate<Point, Point> withinReach = (currentPosition, goalPosition) -> currentPosition.adjacent(goalPosition);

        List<Point> path = pathingStrategy.computePath(this.position, target.getPosition(),
                p -> world.withinBounds(p) && (!world.isOccupied(p)),
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);



        if (!(target instanceof Tree)){
            Optional<Entity> newTarget = world.findNearest(this.position, new ArrayList<>(List.of(Tree.class)));

            // Select a new target and attempt to move again
            newTarget.ifPresent(entity -> move(world, entity, scheduler));
            return false;
        }

        int i;
        if (this.pathingStrategy instanceof AStarPathingStrategy){
            i = 1;
        } else {
            i = 0;
        }

        if (path != null && path.size() > i && world.withinBounds(path.get(i))) {
            Point nextPos = path.get(i);
            Entity nextEntity = world.getOccupant(nextPos).orElse(null);


            if (nextEntity instanceof Portal) {
                Portal portal = (Portal) nextEntity;
                return true;
            } else {
                world.moveEntity(scheduler, this, nextPos);
                this.position = nextPos; // Update the entity's position
            }
        }
        else {
            this.resourceCount += 1;
            TransformingPlant temp = (TransformingPlant) target;
            temp.setHealth(temp.getHealth() - 1);
            return true;
        }
        Optional<Entity> newTarget = world.findNearest(this.position, new ArrayList<>(List.of(Tree.class)));

        // Select a new target and attempt to move again
        return false;
    }

    private boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) { // move to Entity
        if (this.resourceCount >= this.resourceLimit) {
            MovingEntity dude = (MovingEntity) Factory.createDudeFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActivity(scheduler, world, imageStore);
            dude.scheduleAnimation(scheduler);

            return true;
        }

        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) { // move to Entity
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.move(world, target.get(), scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.actionPeriod);
        }


    }



}
