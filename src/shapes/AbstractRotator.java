package shapes;

import board.Board;
import board.Cell;

import java.util.List;

/**
 */
public abstract class AbstractRotator implements Rotator {

    Board board;

    public AbstractRotator(Board board) {
        this.board = board;
    }

    public AbstractRotator() {
    }

    @SuppressWarnings({"ManualArrayCopy"})
    public void rotate() {
        int[][] currentMatrix = board.getMovingShape().getShape().getCells();
        List<Cell> shapeCells = board.getMovingShape().shapeCellsAsList();
        int[][] newMatrix = getRotatedMatrix(currentMatrix);
        if (!(shapeWillMoveToLeft(newMatrix, currentMatrix) && shapeIsOnEdge(shapeCells, 0)) &&
                !(shapeWillMoveToRight(newMatrix, currentMatrix) && shapeIsOnEdge(shapeCells, board.getColumns() - 1)) &&
                !(shapeWillOccupyPopulatedCell(newMatrix))) {
            for (int row = 0; row < currentMatrix.length; row++) {
                for (int col = 0; col < currentMatrix[0].length; col++) {
                    currentMatrix[row][col] = newMatrix[row][col];
                }
            }
        }
    }

    private boolean shapeWillOccupyPopulatedCell(int[][] newMatrix) {
        for (int row = 0; row < newMatrix.length; row++) {
            for (int col = 0; col < newMatrix[0].length; col++) {
                if (boardCellAt(row, col).isPopulated() &&
                        newMatrix[row][col] == 1 &&
                        !(board.getMovingShape().shapeCellsAsList().contains(boardCellAt(row, col)))) return true;
            }
        }
        return false;
    }

    private Cell boardCellAt(int row, int col) {
        return board.getCell(row(row), col(col));
    }

    private int col(int col) {
        return (board.getMovingShape().startingBoardColumn() + col) >= board.getColumns() ?
                board.getColumns() - 1 :
                (board.getMovingShape().startingBoardColumn() + col) < 0 ?
                0 :
                board.getMovingShape().startingBoardColumn() + col;
    }

    private int row(int row) {
        return (board.getMovingShape().startingBoardRow() + row) >= board.getRows() ?
                board.getRows() - 1 :
                (board.getMovingShape().startingBoardRow() + row) < 0 ?
                0 :
                board.getMovingShape().startingBoardRow() + row;
    }

    private boolean shapeWillMoveToRight(int[][] newMatrix, int[][] currentMatrix) {
        return matrixContainsCellInColumn(newMatrix, 3) &&
                !matrixContainsCellInColumn(currentMatrix, 3);
    }

    private boolean shapeIsOnEdge(List<Cell> shapeCells, int column) {
        for (Cell cell : shapeCells) {
            if (cell.column == column) return true;
        }
        return false;
    }

    private boolean shapeWillMoveToLeft(int[][] newMatrix, int[][] currentMatrix) {
        return matrixContainsCellInColumn(newMatrix, 0) &&
                !matrixContainsCellInColumn(currentMatrix, 0);
    }

    private boolean matrixContainsCellInColumn(int[][] newMatrix, int column) {
        for (int[] row : newMatrix) {
            if (row[column] == 1) return true;
        }
        return false;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public int[][] getRotatedMatrix(int[][] currentMatrix) {
        int[][] newMatrix = new int[4][4];
        int rowCount = 0;
        for (int[] row : currentMatrix) {
            int columnCount = 0;
            for (int col = currentMatrix.length - 1; col >= 0; col--) {
                rotateMatrixPosition(currentMatrix, newMatrix, rowCount, columnCount, col);
                columnCount++;
            }
            rowCount++;
        }
        return newMatrix;
    }

    public abstract void rotateMatrixPosition(int[][] currentMatrix, int[][] newMatrix, int rowCount, int columnCount, int col);
}
