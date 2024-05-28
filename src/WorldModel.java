import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    int numRows;
    int numCols;
    public Background[][] background;
    public Entity[][] occupancy;
    public Set<Entity> entities;

    public int getNumRows() {
        return numRows;
    }


    public int getNumCols() {
        return numCols;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public WorldModel() {}


    private static int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    private static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) { // move to WorldModel as private
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.getPosition(), pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.getPosition(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public Optional<PImage> getBackgroundImage(Point pos) { // move to WorldModel
        if (withinBounds(pos)) {
            return Optional.of((getBackgroundCell(pos).getCurrentImage()));
        } else {
            return Optional.empty();
        }
    }

    protected void setBackgroundCell(Point pos, Background background) { // move to WorldModel
        this.background[pos.y][pos.x] = background;
    }

    private Background getBackgroundCell(Point pos) { // move to WorldModel
        return this.background[pos.y][pos.x];
    }

    private void setOccupancyCell(Point pos, Entity entity) { // move to WorldModel
        if (withinBounds(pos)){
            this.occupancy[pos.y][pos.x] = entity;
        }
    }

    public Entity getOccupancyCell(Point pos) { // move to WorldModel
        return this.occupancy[pos.y][pos.x];
    }

    public Optional<Entity> getOccupant(Point pos) { // move to WorldModel
        if (isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    protected void removeEntityAt(Point pos) { // move to WorldModel
        if (withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }


    public void removeEntity(EventScheduler scheduler, Entity entity) { // move to WorldModel
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) { // move to Entity or WorldModel?
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) { // move to WorldModel
        if (withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public Optional<Entity> findNearest(Point pos, List<Class<?>> kinds) { // move to WorldModel
        List<Entity> ofType = new LinkedList<>();
        for (Class<?> kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass().equals(kind)) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }

    public boolean isOccupied(Point pos) { // move to WorldModel
        return withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
    }


    public void tryAddEntity(Entity entity) { // move to WorldModel
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

}
