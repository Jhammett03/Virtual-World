// Any entity that uses scheduleActivity will be an Active Entity. An Entity can be both Animated and Active, or only one of either
public interface ActiveEntity extends Entity {

    double getActionPeriod();

    default void scheduleActivity(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
