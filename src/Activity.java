/**
 * An action that can be taken by an entity
 */
public final class Activity implements Action{
    private ActiveEntity entity;
    private WorldModel world;
    private ImageStore imageStore;

    public Activity(ActiveEntity entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) { // move to Action
        this.entity.executeActivity(this.world, this.imageStore, scheduler);
    }
}

