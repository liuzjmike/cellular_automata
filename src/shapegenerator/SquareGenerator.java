package shapegenerator;

import cellsociety.Cell;
import cellsociety.Grid;
import cellsociety.ShapeGenerator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Generates squares according to the grid given to fill in a space with certain width
 * @author Mike Liu
 *
 */
public class SquareGenerator extends ShapeGenerator {
    
    public static final Color STROKE = Color.BLACK;
    public static final Color ALTERNATIVE_STROKE = Color.WHITE;

    public SquareGenerator(double cellWidth, Grid<? extends Cell> grid) {
        super(cellWidth, grid);
    }

    @Override
    protected Shape getShape(int row, int col, double width, Color color) {
        Rectangle ret = new Rectangle(col*width, row*width, width, width);
        ret.setStroke(color==STROKE ? ALTERNATIVE_STROKE : STROKE);
        ret.setFill(color);
        return ret;
    }

}
