// any Entity that uses scheduleAnimation will be an AnimatedEntity. An Entity can be both Animated and Active, or only one of either
public interface AnimatedEntity extends Entity {

    default void scheduleAnimation(EventScheduler scheduler) {
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    default void nextImage() { // only the entities with animations need this
        if (getImageIndex() < 3) {
            setImageIndex(getImageIndex() + 1);
        }else{
            setImageIndex(0);
        }
    }

    double getAnimationPeriod();
    void setImageIndex(int i);

    int getImageIndex();
}
