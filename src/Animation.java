/**
 * An action that can be taken by an entity
 */
public final class Animation implements Action{
    private AnimatedEntity entity;
    private int repeatCount;

    public Animation(AnimatedEntity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) { // move to Action
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, Factory.createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
        }
    }
}

