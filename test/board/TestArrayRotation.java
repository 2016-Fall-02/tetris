package board;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 */
public class TestArrayRotation {

    Board board;

    @Before
    public void setUpBoard() {
        board = new Board(30, 10);
    }

    @Test
    public void basicRotateClockwise() {
        int[][] matrix = new int[][]{
                new int[]{1, 2, 3, 4},
                new int[]{5, 6, 7, 8},
                new int[]{9, 10, 11, 12},
                new int[]{13, 14, 15, 16}};
        matrix = rotateClockwise(matrix);
        assertArrayEquals("basic clockwise rotation failed: " + Arrays.deepToString(matrix),
                matrix, new int[][]{
                        new int[]{13, 9, 5, 1},
                        new int[]{14, 10, 6, 2},
                        new int[]{15, 11, 7, 3},
                        new int[]{16, 12, 8, 4}});
    }

    private int[][] rotateClockwise(int[][] matrix) {
        int[][] newMatrix = new int[4][4];
        int rowCount = 0;
        for (int[] row : matrix) {
            int columnCount = 0;
            for (int col = matrix.length - 1; col >= 0; col--) {
                newMatrix[matrix.length - 1 - columnCount][matrix.length - 1 - rowCount] = matrix[rowCount][col];
                columnCount++;
            }
            rowCount++;
        }
        return newMatrix;
    }

    @Test
    public void basicRotateAntiClockwise() {
        int[][] matrix = new int[][]{
                new int[]{1, 2, 3, 4},
                new int[]{5, 6, 7, 8},
                new int[]{9, 10, 11, 12},
                new int[]{13, 14, 15, 16}};
        matrix = rotateAntiClockwise(matrix);
        assertArrayEquals(matrix, new int[][]{
                new int[]{4, 8, 12, 16},
                new int[]{3, 7, 11, 15},
                new int[]{2, 6, 10, 14},
                new int[]{1, 5, 9, 13}});
        matrix = rotateAntiClockwise(matrix);
        assertArrayEquals(matrix, new int[][]{
                new int[]{16, 15, 14, 13},
                new int[]{12, 11, 10, 9},
                new int[]{8, 7, 6, 5},
                new int[]{4, 3, 2, 1}});
        matrix = rotateAntiClockwise(matrix);
        assertArrayEquals(matrix, new int[][]{
                new int[]{13, 9, 5, 1},
                new int[]{14, 10, 6, 2},
                new int[]{15, 11, 7, 3},
                new int[]{16, 12, 8, 4}});
        matrix = rotateAntiClockwise(matrix);
        assertArrayEquals(matrix, new int[][]{
                new int[]{1, 2, 3, 4},
                new int[]{5, 6, 7, 8},
                new int[]{9, 10, 11, 12},
                new int[]{13, 14, 15, 16}});
    }

    private int[][] rotateAntiClockwise(int[][] matrix) {
        int[][] newMatrix = new int[4][4];
        int rowCount = 0;
        for (int[] row : matrix) {
            int columnCount = 0;
            for (int col = matrix.length - 1; col >= 0; col--) {
                newMatrix[columnCount][rowCount] = matrix[rowCount][col];
                columnCount++;
            }
            rowCount++;
        }
        return newMatrix;
    }

    private Point[][] rotateAntiClockwise(Point[][] matrix) {
        Point[][] newMatrix = new Point[4][4];
        int rowCount = 0;
        for (Point[] row : matrix) {
            int columnCount = 0;
            for (int col = matrix.length - 1; col >= 0; col--) {
                if (matrix[rowCount][col] != null) {
                    Point oldPoint = matrix[rowCount][col];
                    newMatrix[columnCount][rowCount] = p(oldPoint.x - (rowCount - columnCount), oldPoint.y - (col - rowCount));

                }
                columnCount++;
            }
            rowCount++;
        }
        return newMatrix;
    }

    private Point[][] rotateClockwise(Point[][] matrix) {
        Point[][] newMatrix = new Point[4][4];
        int rowCount = 0;
        for (Point[] row : matrix) {
            int columnCount = 0;
            for (int col = matrix.length - 1; col >= 0; col--) {
                if (matrix[rowCount][col] != null) {
                    Point oldPoint = matrix[rowCount][col];
                    newMatrix[matrix.length - 1 - columnCount][matrix.length - 1 - rowCount] =
                            p(oldPoint.x + col - rowCount, oldPoint.y + columnCount - rowCount);
                }
                columnCount++;
            }
            rowCount++;
        }
        return newMatrix;
    }

    @Test
    public void pointRotateClockwise() {
        Point[][] originalPoints = new Point[][]{
                new Point[]{p(10, 10), p(10, 11), p(10, 12), p(10, 13)},
                new Point[]{p(11, 10), p(11, 11), p(11, 12), p(11, 13)},
                new Point[]{p(12, 10), p(12, 11), p(12, 12), p(12, 13)},
                new Point[]{p(13, 10), p(13, 11), p(13, 12), p(13, 13)}
        };
        Point[][] points = rotateClockwise(originalPoints);
        assertArrayEquals("new points not the same as original: " + Arrays.deepToString(points), originalPoints, points);
        points = rotateClockwise(points);
        assertArrayEquals("new points not the same as original: " + Arrays.deepToString(points), originalPoints, points);
    }

    @Test
    public void pointRotateAntiClockwise() {
        Point[][] originalPoints = new Point[][]{
                new Point[]{p(10, 10), p(10, 11), p(10, 12), p(10, 13)},
                new Point[]{p(11, 10), p(11, 11), p(11, 12), p(11, 13)},
                new Point[]{p(12, 10), p(12, 11), p(12, 12), p(12, 13)},
                new Point[]{p(13, 10), p(13, 11), p(13, 12), p(13, 13)}
        };
        Point[][] points = rotateAntiClockwise(originalPoints);
        assertArrayEquals("new points not the same as original: " + Arrays.deepToString(points), originalPoints, points);
        points = rotateAntiClockwise(points);
        assertArrayEquals("new points not the same as original: " + Arrays.deepToString(points), originalPoints, points);
    }

    @Test
    public void rotateClockwiseWithNulls(){
        Point[][] originalPoints = new Point[][]{
                new Point[]{null, p(10, 11), null, null},
                new Point[]{null, p(11, 11), null, null},
                new Point[]{null, p(12, 11), p(12, 12), null},
                new Point[]{null, null, null, null}
        };
        Point[][] points = rotateClockwise(originalPoints);
        assertArrayEquals("points with null not rotated properly:" + Arrays.deepToString(points),
                new Point[][]{
                        new Point[]{null, null, null, null},
                        new Point[]{null,  p(11, 11), p(11, 12), p(11, 13)},
                        new Point[]{null,  p(12, 11), null, null},
                        new Point[]{null, null, null, null}},
                points);
    }

    @Test
    public void rotateAntiClockwiseWithNulls() {
        Point[][] originalPoints = new Point[][]{
                new Point[]{null, p(10, 11), null, null},
                new Point[]{null, p(11, 11), null, null},
                new Point[]{null, p(12, 11), p(12, 12), null},
                new Point[]{null, null, null, null}
        };
        Point[][] points = rotateAntiClockwise(originalPoints);
        assertArrayEquals("points with null not rotated properly:" + Arrays.deepToString(points),
                new Point[][]{
                        new Point[]{null, null, null, null},
                        new Point[]{null, null, p(11, 12), null},
                        new Point[]{p(12, 10), p(12, 11), p(12, 12), null},
                        new Point[]{null, null, null, null}},
                points);

    }


    private Point p(int x, int y) {
        return new Point(x, y);
    }

    class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            if (y != point.y) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}
