import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public interface MovingEntity extends ActiveEntity, AnimatedEntity {

    default Point nextPosition(WorldModel world, Point destPos, Class<?> targetClass) {
        List<Point> path = new AStarPathingStrategy().computePath(
                this.getPosition(),
                destPos,
                (p) -> world.withinBounds(p) && !world.isOccupied(p),
                (p1, p2) -> Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) == 1,
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        if (!path.isEmpty()) {
            return path.get(0);
        } else {
            return this.getPosition();
        }
    }

//    private boolean isObstacle(WorldModel world, Point pos) {
//        Entity occupant = world.getOccupancyCell(pos);
//        return occupant != null && occupant.isObstacle();
//    }






    boolean move(WorldModel world, Entity target, EventScheduler scheduler);
}
