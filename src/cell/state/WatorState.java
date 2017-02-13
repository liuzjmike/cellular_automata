package cell.state;

import java.util.Arrays;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * State constants for Wator simulation
 * @author Mike Liu
 *
 */
public class WatorState extends CellState {
    
    public static final WatorState WATER = new WatorState("Water", Color.BLUE);
    public static final WatorState FISH = new WatorState("Fish", Color.BISQUE);
    public static final WatorState SHARK = new WatorState("Shark", Color.GREY);
    public static final List<WatorState> STATES = Arrays.asList(WATER, FISH, SHARK);
  
    private WatorState(String state, Color color) {
        super(state, color);
    }

    @Override
    protected List<WatorState> getStates() {
        return STATES;
    }
}
