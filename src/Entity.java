import processing.core.PImage;

import java.util.List;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public interface Entity {

    String getId();

    Point getPosition();

    void setPosition(Point position);

    default boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    default String log(){
        return getId().isEmpty() ? null :
                String.format("%s %d %d %d", getId(), getPosition().x, getPosition().y, getImageIndex());
    }

    default int getImageIndex() {return 0;};

    List<PImage> getImages();

    default PImage getCurrentImage() { // double between Entity and Background
        return getImages().get(getImageIndex() % getImages().size());
    }

    boolean isObstacle();
}
