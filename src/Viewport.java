public final class Viewport {

    private int row;
    private int col;
    private int numRows;
    private int numCols;

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public int getNumRows() {
        return numRows;
    }
    public int getNumCols() {
        return numCols;
    }

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public Point worldToViewport(int col, int row) { // move to Viewport
        return new Point(col - this.col, row - this.row);
    }

    public Point viewportToWorld(int col, int row) { // move to Viewport
        return new Point(col + this.col, row + this.row);
    }

    public void shift(int col, int row) { // move to Viewport
        this.col = col;
        this.row = row;
    }

    public boolean contains(Point p) { // move to Viewport
        return p.y >= this.row && p.y < this.row + this.numRows && p.x >= this.col && p.x < this.col + this.numCols;
    }
}
