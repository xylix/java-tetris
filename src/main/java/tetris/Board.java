package tetris;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collection;

/**
 * The class <b>Board</b> represents the board/grid used in Tetris. This class
 * contains the moving shape, a list of points of fallen shapes, and stats about the
 * game. It contains the methods used for collision detection, manipulating the 
 * falling shape, and clearing full lines all while keeping up with the score, 
 * level, number of cleared lines, and the speed of the falling shape.
 * 
 * @author Benoît Jeaurond
 */
class Board {

    private boolean gameOver;

    /**
     * True if the gravity feature is on (gravity on blocks above cleared lines with space underneath the cleared line)
     */
    public boolean gravity;

    /**
     * Stats about the current game
     */
    private int numClearedLines, level, timePerBlock;

    public int score;

    /**
     * A list of points of the fallen shapes
     */
    private List<Point> points;

    /**
     * Random object to create random numbers for the type of shape
     */
    private Random rand;

    /**
     * The shape that is currently moving
     */
    private Shape currentShape;

    /**
     * Constants specifying the board size
     */
    public static final int WIDTH = 10, HEIGHT = 22;

    /**
     * Constructor of the Board class
     */
    public Board() {
        this.points = new ArrayList<Point>();
        this.rand = new Random();
        this.gameOver = false;
        this.numClearedLines = 0;
        this.level = 0;
        this.score = 0;
        this.gravity = true;
        this.timePerBlock = 800;

        currentShape = createShape();
    }

    /**
     * Creates a new shape randomly and sets it as the current shape
     */
    public Shape createShape() {
        int num = rand.nextInt(8);

        if (num == 7 || (currentShape != null && num == currentShape.type)) {
            num = rand.nextInt(7);
        }

        if (currentShape != null) {
            points.addAll(currentShape.points);
        }

        return new Shape(num + 1);
    }

    private List<Point> generatePoints(List<Point> points, Function<Point, Point> mutation) {
        return points.stream()
            .map(mutation)
            .collect(Collectors.toList());
    }


    private boolean anyMatch(Collection<Point> collection) {
        Predicate<Point> predicate = mutatedPoint -> currentShape.points.contains(mutatedPoint);
        return collection.parallelStream().anyMatch(predicate);
    }

    /**
     * @return true if there are point(s) down the current shape
     */
    private boolean hasPointsDown() {
        var oneLowerPoints = generatePoints(points, point -> new Point(point.x, point.y + 1));
        return anyMatch(oneLowerPoints);
    }

    /**
     * @return true if there are point(s) right of the current shape
     */
    private boolean hasPointsRight() {
        Function<Point, Point> mutation = point -> new Point(point.x + 1, point.y);
        var oneHigherPoints = generatePoints(points, mutation);
        return anyMatch(oneHigherPoints);
    }

    /**
     * @return true if there are point(s) left of the current shape
     */
    private boolean hasPointsLeft() {
        var oneLowerPoints = generatePoints(points, point -> new Point(point.x - 1, point.y));
        return anyMatch(oneLowerPoints);
    }

    /**
     * @return true if the current shape is close to the top
     */
    private boolean closeToTopBorder() {
        return currentShape.points.stream().anyMatch(p -> p.y == 0);
    }

    /**
     * Helper method for moving the shape left
     * 
     * @return true if the current shape is close to the left
     */
    private boolean closeToLeftBorder() {
        return currentShape.points.stream().anyMatch(p -> p.x == 0);
    }

    /**
     * Helper method for moving the shape right
     * 
     * @return true if the current shape is close to the right
     */
    private boolean closeToRightBorder() {
        return currentShape.points.stream().anyMatch(p -> p.x == WIDTH - 1);
    }

    /**
     * Helper method for moving the shape down
     * 
     * @return true if the current shape is close to the bottom
     */
    private boolean closeToBottomBorder() {
        return currentShape.points.stream().anyMatch(p -> p.y == HEIGHT - 1);
    }

    /**
     * Helper method for rotating the current shape
     * 
     * @return true if the next rotation is within the board and does not collide
     *         with other shapes
     */
    private boolean canRotate() {
        List<Point> rotated = currentShape.getRotatePoints();

        for (Point i : rotated) {
            if (i.x >= WIDTH || i.y >= HEIGHT || i.x < 0 || i.y < 0 || points.contains(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Rotates the current shape
     */
    public void rotate() {
        if (canRotate()) {
            currentShape.rotate();
        }
    }

    /**
     * Moves the current shape left
     */
    public void moveLeft() {
        if (!hasPointsLeft() && !closeToLeftBorder()) {
            currentShape.moveLeft();
        }
    }

    /**
     * Moves the current shape right
     */
    public void moveRight() {
        if (!hasPointsRight() && !closeToRightBorder()) {
            currentShape.moveRight();
        }
    }

    /**
     * Moves the current shape down, checks if the game is finished and creates
     * another shape if it can't move down
     */
    public void moveDown() {
        if (!hasPointsDown() && !closeToBottomBorder()) {
            currentShape.moveDown();
        } else {
            if (closeToTopBorder()) {
                gameOver = true;
            } else {
                currentShape = createShape();
                removeLines();
            }
        }
    }

    /**
     * Removes full lines (if present), updates score and level
     */
    private void removeLines() {
        boolean gravityTriggerd;

        do {
            gravityTriggerd = false;
            List<Integer> fullLines = new ArrayList<Integer>(HEIGHT);

            List<Point> allPoints = getPoints();

            if (allPoints.size() != 0) {
                for (int i = 0; i < HEIGHT; i++) {
                    boolean full = true;
                    row: for (int j = 0; j < WIDTH; j++) {
                        if (!allPoints.contains(new Point(j, i))) {
                            full = false;
                            break row;
                        }
                    }

                    if (full) {
                        fullLines.add(i);
                    }
                }
            }

            if (fullLines.size() != 0) {
                numClearedLines += fullLines.size();
                score += calculateCurrentScore(fullLines.size());

                int mostBottomLine = 0;

                for (int i : fullLines) {
                    if (i > mostBottomLine) {
                        mostBottomLine = i;
                    }

                    Predicate<Point> pointsPredicate = p -> p.y == i;
                    points.removeIf(pointsPredicate);

                    // points.stream().forEach(point -> point.y += 1);

                    for (int j = 0; j < points.size(); j++) {
                        if (points.get(j).y < i) {
                            points.get(j).y += 1;
                        }
                    }
                }

                if (mostBottomLine != HEIGHT - 1 && gravity) {

                    allPoints = getPoints();

                    for (int i = 0; i < WIDTH; i++) {
                        int numOfEmpty = 0;

                        for (int j = mostBottomLine + 1; j < HEIGHT; j++) {
                            if (!allPoints.contains(new Point(i, j))) {
                                numOfEmpty++;
                            } else {
                                break;
                            }
                        }

                        if (numOfEmpty != 0) {
                            gravityTriggerd = false;
                            for (int j = 0; j < points.size(); j++) {
                                if (points.get(j).x == i && points.get(j).y <= mostBottomLine) {
                                    points.get(j).y += numOfEmpty;
                                    gravityTriggerd = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (gravityTriggerd);

        // Based on this website
        // https://en.wikipedia.org/wiki/Tetris#Variations
        level = numClearedLines / 10;

        updateSpeed();
    }

    /**
     * Calculates the score of the current cleared lines Based on
     * http://tetris.wikia.com/wiki/Scoring
     * 
     * @param num number of cleared lines at once
     * @return the score for the that number of lines
     */
    private int calculateCurrentScore(int num) {
        int baseNum = switch (num) {
            case 2 -> 100;
            case 3 -> 300;
            case 4 -> 1000;
            default -> 40;
        };

        return baseNum * (level + 1);
    }

    /**
     * Updates the time (milliseconds) per block on screen according to the level
     * and this source 
     * https://gaming.stackexchange.com/questions/13057/tetris-difficulty
     */
    private void updateSpeed() {
        double baseFrame = 48.0;

        if (-1 < level && level < 9) {
            timePerBlock = (int) (((baseFrame - (level * 5.0)) / 60.0) * 1000.0);
        } else if (level == 9) {
            timePerBlock = (int) ((6.0 / 60.0) * 1000.0);
        } else if (9 < level && level < 19) {
            timePerBlock = (int) (((8.0 - ((13.0 - 1.0) / 3.0)) / 60.0) * 1000.0);
        } else if (18 < level && level < 29) {
            timePerBlock = (int) ((2.0 / 60.0) * 1000.0);
        } else {
            timePerBlock = (int) ((1.0 / 60.0) * 1000.0);
        }
    }

    /**
     * Returns a list of points
     * 
     * @return new list containing all the points on the board (including the current
     *         shape)
     */
    public List<Point> getPoints() {
        List<Point> points = new ArrayList<Point>();

        points.addAll(this.points);
        points.addAll(currentShape.points);

        Set<Point> set = new HashSet<Point>();
        set.addAll(points);
        points.clear();
        points.addAll(set);

        return points;
    }

    /**
     * Print the board and the value of some methods, used for testing this class
     */
    public void getStatus() {
        StringBuffer sb = new StringBuffer();

        sb.append(toString());
        sb.append("--- Border ---\n");
        sb.append("Left " + closeToLeftBorder() + "\n");
        sb.append("Right " + closeToRightBorder() + "\n");
        sb.append("Bottom " + closeToBottomBorder() + "\n");
        sb.append("--- Points ---\n");
        sb.append("Left " + hasPointsLeft() + "\n");
        sb.append("Right " + hasPointsRight() + "\n");
        sb.append("Bottom " + hasPointsDown() + "\n");
        sb.append("--- Rotate ---\n");
        sb.append(canRotate());

        System.out.println(sb.toString());
    }

    /**
     * Getter of numClearedLines
     * 
     * @return the total number of cleared lines
     */
    public int getNumClearedLines() {
        return numClearedLines;
    }

    /**
     * Getter of gameOver
     * 
     * @return true if the game is finished
     */
    public boolean getGameOver() {
        return gameOver;
    }

    /**
     * Getter of level
     * 
     * @return the current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter of timePerBlock
     * 
     * @return the time for each block to be on the screen
     */
    public int getTimePerBlock() {
        return timePerBlock;
    }

    @Override
    public String toString() {
        int[][] board = new int[HEIGHT][WIDTH];
        for (Point i : points) {
            board[i.y][i.x] = i.type;
        }

        for (Point i : currentShape.points) {
            board[i.y][i.x] = currentShape.type;
        }

        String str = "";
        for (int[] i : board) {
            str += Arrays.toString(i) + "\n";
        }
        return str;
    }

}
