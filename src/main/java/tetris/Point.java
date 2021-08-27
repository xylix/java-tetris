package tetris;
/**
 * The class <b>Point</b> helps keeping the position of the shape in the 
 * Tetris game. They have a x, y coordinate and keep the kind of shape that
 * point in particular is.
 * 
 * @author Benoît Jeaurond
 */
class Point {

    /**
     * Coordinates of the point
     */
    public int x, y;

    /**
     * Type of shape of the point
     */
    public int type;

    /**
     * Constructor of the Point class specifying the coordinates of the point
     * 
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor of the Point class specifying the coordinates of the point and its type
     * 
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param type the type of shape of the point
     */
    public Point(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Setter for both coordinates
     * 
     * @param x the new x coordinate of the point
     * @param y the new y coordinate of the point
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            return x == ((Point) obj).x && y == ((Point) obj).y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Point X: " + x + " Y: " + y + " Type: " + type;
    }
}
