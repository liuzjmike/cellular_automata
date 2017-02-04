package cellsociety;

import java.util.Iterator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * Generates shapes according to the grid given to fill in a space with certain width
 * @author Mike Liu
 *
 */
public abstract class ShapeGenerator implements Iterable<Shape> {
    
    private double cellWidth;
    private Grid<? extends Cell> grid;
    
    public ShapeGenerator(double cellWidth, Grid<? extends Cell> grid) {
        this.cellWidth = cellWidth;
        this.grid = grid;
    }

    @Override
    public Iterator<Shape> iterator() {
        return new Iterator<Shape>() {

            private int row = 0;
            private int col = 0;
            
            @Override
            public boolean hasNext() {
                return row < grid.numRows() || col < grid.numCols();
            }

            @Override
            public Shape next() {
                Shape shape = getShape(row, col, cellWidth, grid.get(row, col).getColor());
                if(col < grid.numCols()-1) {
                    col++;
                } else {
                    row++;
                    col = 0;
                }
                return shape;
            }
        };
    }
    
    /**
     * Returns a shape that will be placed at (row, col) in the space
     * @param row - the row that the shape will be placed in
     * @param col - the column that the shape will be placed in
     * @param color - the color of the shape
     * @return the shape that is produced for the given row, col and color
     */
    protected abstract Shape getShape(int row, int col, double width, Color color);
}
