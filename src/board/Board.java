package board;

import static board.Board.Rotation.AntiClockwise;
import static board.Board.Rotation.Clockwise;
import shapes.AntiClockwiseRotator;
import shapes.ClockwiseRotator;
import static shapes.RandomShapeGenerator.getNewShapeAtRandom;
import shapes.Rotator;
import shapes.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class Board {
    int rows, columns;
    List<Cell> cells;
    public static int START_ROW = 0;
    public static int END_ROW = 3;
    public static int START_COL = 3;
    public static int END_COL = 6;
    public static Color DEFAULT_EMPTY_COLOUR = Color.gray, BEING_REMOVED = Color.white;
    protected MovingShape movingShape;
    boolean gameOver = false;
    RotatorFactory rotators;


    public boolean gameOver() {
        return gameOver;
    }

    static class RotatorFactory {
        Rotator clockwiseRotator, antiClockwiseRotator;

        RotatorFactory(Board board) {
            clockwiseRotator = new ClockwiseRotator(board);
            antiClockwiseRotator = new AntiClockwiseRotator(board);
        }

        Rotator get(Rotation rotation) {
            return rotation == Rotation.AntiClockwise ? antiClockwiseRotator : clockwiseRotator;
        }
    }

    enum Rotation {
        Clockwise,
        AntiClockwise
    }

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        rotators = new RotatorFactory(this);
        setCells(rows, columns);
    }

    public void moveShapeToRight() {
        movingShape.moveToRight();
    }

    public void moveShapeToLeft() {
        movingShape.moveToLeft();
    }

    public void addNewShape(Shape shape) {
        if (canAddNewShape()) movingShape = new MovingShape(shape);
        else
            gameOver = true;
    }

    private boolean canAddNewShape() {
        for (int row = START_ROW; row <= END_ROW; row++) {
            for (int col = START_COL; col <= END_COL; col++) {
                if (cellAt(row, col).isPopulated()) return false;
            }
        }
        return true;
    }

    public void tick() {
        if (movingShape != null) {
            //don't check after moving, so player can still move sideways before next tick
            if (movingShapeCannotMoveDownAnymore()) {
                addNewShapeAtRandom();
                removeCompletedRows();
            }
            movingShape.move(1, 0);//move down one row
        }
    }

    private void removeCompletedRows() {
        for (int row = 0; row < rows; row++) {
            boolean complete = true;
            for (Cell cell : cellsInRow(row)) {
                if (!cell.isPopulated()) complete = false;
            }
            if (complete) {
                removeRow(row);
            }
        }
    }

    private void removeRow(int removeMe) {
        for (int row = removeMe; row > 0; row--) {
            for (Cell cell : cellsInRow(row)) {
                Cell cellAbove = cellAt(row - 1, cell.column);
                if (getMovingShape().shapeCellsAsList().contains(cellAbove)) {
                    cell.setPopulated(false);
                    cell.setColour(Board.DEFAULT_EMPTY_COLOUR);
                } else {
                    cell.setPopulated(cellAbove.isPopulated());
                    cell.setColour(cellAbove.getColour());
                }
            }
        }
    }

    private List<Cell> cellsInRow(int row) {
        List<Cell> cellsInRow = new ArrayList<Cell>();
        for (Cell cell : cells) {
            if (cell.row == row) cellsInRow.add(cell);
        }
        return cellsInRow;
    }

    public void addNewShapeAtRandom() {
        addNewShape(getNewShapeAtRandom());
    }

    public boolean movingShapeCannotMoveDownAnymore() {
        for (Cell cell : movingShape.shapeCellsAsList()) {
            if (cellIsOnBottomRow(cell) || somethingBelowCell(cell))
                return true;
        }
        return false;
    }

    public void rotateShapeClockwise() {
        movingShape.rotate(Clockwise);
    }

    public void rotateShapeAntiClockwise() {
       movingShape.rotate(AntiClockwise);
    }

    public class MovingShape {

        Cell[][] shapeCells;
        Shape shape;

        public MovingShape(Shape shape) {
            this.shape = shape;
            shapeCells = boardCellsForNewShape(Board.START_ROW, Board.START_COL);
        }

        void moveToRight() {
            move(0, 1);
        }

        void moveToLeft() {
            move(0, -1);
        }

        boolean canMove(int columns) {
            for (Cell cell : shapeCellsAsList()) {
                //cell is on right edge and attempt to move right
                if (cell.column == getColumns() - 1 && columns > 0)
                    return false;

                //cell is on left edge and attempt to move left
                if (cell.column == 0 && columns < 0)
                    return false;

                //trying to move sideways into a populated cell
                if (columns != 0 && cellAt(cell.row, cell.column + columns).isPopulated() &&
                        (!movingShape.shapeCellsAsList().contains(new Cell(cell.row, cell.column + columns))))
                    return false;
            }
            return true;
        }

        synchronized void move(int rows, int columns) {
            if (!canMove(columns)) columns = 0;//trying to move left or right when blocked or on the edge

            if (rows != 0 || columns != 0)
                setCellsForNewPosition(rows, columns);
        }

        private void setCellsForNewPosition(int rows, int columns) {
            setCurrentCellsToUnpopulated();

            for (int row = 0; row < shapeCells.length; row++) {
                for (int column = 0; column < shapeCells[0].length; column++) {
                    shapeCells[row][column] = setNewCell(rows, columns, shapeCells[row][column]);
                }
            }
        }

        private Cell setNewCell(int rows, int columns, Cell cell) {
            if (cell != null) {
                return cellAt(cell.row + rows, cell.column + columns).setPopulated(true, getShape());
            }
            return cell;
        }

        private void setCurrentCellsToUnpopulated() {
            for (Cell cell : shapeCellsAsList()) {
                cell.setPopulated(false);
            }
        }

        public List<Cell> shapeCellsAsList() {
            List<Cell> cellList = new ArrayList<Cell>();
            for (Cell[] cells : shapeCells) {
                for (Cell cell : cells) {
                    if (cell != null) cellList.add(cell);
                }
            }
            return cellList;
        }

        public void rotate(Rotation rotation) {
            setNewShapeCells(rotateCells(rotation));
        }

        private void setNewShapeCells(Cell[][] newShapeCells) {
            setAllCells(shapeCells, false);
            setAllCells(newShapeCells, true);
            shapeCells = newShapeCells;
        }

        private Cell[][] rotateCells(Rotation rotation) {
            rotators.get(rotation).rotate();

            return mapNewShapeToBoardCells();
        }

        private Cell[][] mapNewShapeToBoardCells() {
            return boardCellsForNewShape(startingBoardRow(), startingBoardColumn());
        }

        private Cell[][] boardCellsForNewShape(int startRow, int startCol) {
            Cell[][] newShapeCells = new Cell[4][4];
            for (int row = 0; row < getShape().getCells().length; row++) {
                for (int col = 0; col < getShape().getCells()[0].length; col++) {
                    newShapeCells[row][col] =
                            getShape().getCells()[row][col] == 0 ?
                                    null :
                                    cellAt(startRow + row, startCol + col).setPopulated(true, getShape());
                }
            }
            return newShapeCells;
        }

        //board column at (0,0) of this matrix
        public int startingBoardColumn() {
            for (Cell[] row : shapeCells) {
                for (int col = 0; col < shapeCells[0].length; col++) {
                    if (row[col] != null) {
                        return row[col].column - col;
                    }
                }
            }
            //fail fast, fail big
            throw new RuntimeException();
        }

        //board row at (0,0) of this matrix
        public int startingBoardRow() {
            for (int row = 0; row < shapeCells.length; row++) {
                for (int col = 0; col < shapeCells[0].length; col++) {
                    if (shapeCells[row][col] != null) {
                        return shapeCells[row][col].row - row;
                    }
                }
            }
            //fail fast, fail big
            throw new RuntimeException();
        }

        public Shape getShape() {
            return shape;
        }

    }

    private void setAllCells(Cell[][] cells, boolean populated) {
        for (Cell[] cell : cells) {
            for (int col = 0; col < cells[0].length; col++) {
                if (cell[col] != null) {
                    cell[col].setPopulated(populated, getMovingShape().getShape());
                }
            }
        }
    }

    public MovingShape getMovingShape() {
        return movingShape;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public Cell getCell(int row, int column) {
        return cellAt(row, column);
    }

    Cell cellAt(int row, int column) {
        if (cells.indexOf(new Cell(row, column)) < 0){
            throw new RuntimeException("cell not found at "+row+","+column);
        }
        return cells.get(cells.indexOf(new Cell(row, column)));
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    private void setCells(int rows, int columns) {
        cells = new ArrayList<Cell>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                cells.add(new Cell(row, col));
            }
        }
    }

    private boolean cellIsOnBottomRow(Cell cell) {
        return cell.row == rows - 1;
    }

    private boolean somethingBelowCell(Cell cell) {
        return (cellAt(cell.row + 1, cell.column).isPopulated())//cell below has something in it
                &&
                //and it's not because it's one of its own cells
                (!movingShape.shapeCellsAsList().contains(new Cell(cell.row + 1, cell.column)));
    }
}
