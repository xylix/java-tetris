package tetris;

class Point {

    /**
     * Coordinates of the point
     */
    public int x, y;

    /**
     * Type of shape of the point
     */
    public int type;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.type = p.type;
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
