import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView {
    private PApplet screen;
    private WorldModel world;
    private int tileWidth;
    private int tileHeight;
    private Viewport viewport;


    public Viewport getViewport() {
        return viewport;
    }

    public WorldView(int numRows, int numCols, PApplet screen, WorldModel world, int tileWidth, int tileHeight) {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    private static int clamp(int value, int low, int high) { // move to WorldView
        return Math.min(high, Math.max(value, low));
    }

    public void shiftView(int colDelta, int rowDelta) { // move to WorldView
        int newCol = clamp(this.viewport.getCol() + colDelta, 0, this.world.getNumCols() - this.viewport.getNumCols());
        int newRow = clamp(this.viewport.getRow() + rowDelta, 0, this.world.getNumRows() - this.viewport.getNumRows());

        this.viewport.shift(newCol, newRow);
    }

    private void drawEntities() {
        for (Entity entity : this.world.getEntities()) {
            Point pos = entity.getPosition();

            if (this.viewport.contains(pos)) {
                Point viewPoint = this.viewport.worldToViewport(pos.x, pos.y);

                // Retrieve the image to be drawn
                PImage currentImage = entity.getCurrentImage();

                // Check if the currentImage is not null before attempting to draw
                if (currentImage != null) {
                    this.screen.image(currentImage, viewPoint.x * this.tileWidth, viewPoint.y * this.tileHeight);
                }
            }
        }
    }

    private void drawBackground() { // move to WorldView as private
        for (int row = 0; row < this.viewport.getNumRows(); row++) {
            for (int col = 0; col < this.viewport.getNumCols(); col++) {
                Point worldPoint = this.viewport.viewportToWorld(col, row);
                Optional<PImage> image = this.world.getBackgroundImage(worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth, row * this.tileHeight);
                }
            }
        }
    }

    public void drawViewport() { // move to WorldView
        drawBackground();
        drawEntities();
    }
}
