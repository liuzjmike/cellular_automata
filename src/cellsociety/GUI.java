package cellsociety;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import cellsociety.grid.Grid;
import cellsociety.grid.neighborfinder.NeighborFinder;
import cellsociety.handler.LoadHandler;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * User interface that sets up the display and lets user interact with the simulation
 * @author Mike Liu
 *
 */
public class GUI {
    
    public static final String TITLE = "Cell Automata";
    public static final String DEFAULT_RESOURCE_PACKAGE = "resources/";
    public static final String STYLESHEET = "default.css";
    public static final String PROPERTIES = "default";
    public static final String DATA_FILE_EXTENSION = "*.xml";

    public static final double SCENE_WIDTH = 1200;
    public static final double SCENE_HEIGHT = 680;
    public static final double GRID_WIDTH = 600;

    private Stage myStage, inputStage;
    private BorderPane myRoot;
    private Button load;
    private List<Button> otherButtons;
    private Label speedLabel, shapeLabel, gridLabel, neighborLabel;
    private Slider speedSlider;
    private ChoiceBox<String> shapeChooser, gridChooser, neighborChooser;
    private Controller myController;
    private ResourceBundle myResources;
    private FileChooser myChooser;

    public GUI(Stage stage) {
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + PROPERTIES);
        myStage = stage;
        inputStage = createInputStage();
        myController = new Controller(GRID_WIDTH);
        myRoot = createRoot();
        enableInput(!myController.hasModel());
        myStage.setTitle(TITLE);
        myStage.setScene(createScene());
        myStage.setResizable(false);
        myChooser = makeFileChooser(DATA_FILE_EXTENSION);
    }
    
    /**
     * Displays the whole program
     */
    public void show() {
        myStage.show();
    }
    
    private Stage createInputStage() {
        Stage stage = new Stage();
        stage.initOwner(myStage);
        stage.setTitle(myResources.getString("InputWindow"));
        return stage;
    }
    
    /**
     * Creates a BorderPane to serve as the root of this display
     * Depends on myController
     * @return
     */
    private BorderPane createRoot() {
        BorderPane bp = new BorderPane();
        ScrollPane cp = new ScrollPane();
        cp.setPrefWidth(GRID_WIDTH);
        cp.setContent(myController.getGridView());
        bp.setCenter(cp);
        bp.setBottom(initInputPanel());
        myController.getGraphView().setId("graph-view");
        bp.setRight(myController.getGraphView());
        return bp;
    }
    
    private Scene createScene() {
        Scene scene = new Scene(myRoot, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(DEFAULT_RESOURCE_PACKAGE + STYLESHEET);
        return scene;
    }

    private Node initInputPanel() {
        createButtons();
        createSpeedChooser();
        createShapeChooser();
        createGridChooser();
        createNeighborPatternChooser();
        HBox inputPanel = new HBox(load);
        inputPanel.getChildren().addAll(otherButtons);
        inputPanel.getChildren().addAll(
                speedLabel, speedSlider,
                shapeLabel, shapeChooser,
                gridLabel, gridChooser,
                neighborLabel, neighborChooser);
        inputPanel.setId("input-panel");
        return inputPanel;
    }

    private void createButtons() {
        load = createButton(myResources.getString("LoadButton"), e -> {
            myController.pause();
            try {
                myChooser.setTitle(myResources.getString("OpenFile"));
                File dataFile = myChooser.showOpenDialog(myStage);
                if(dataFile != null) {
                    myController.load(dataFile, new MyLoadHandler());
                    shapeChooser.getSelectionModel().selectFirst();
                    gridChooser.getSelectionModel().selectFirst();
                }
            } catch(CAException ce) {
                showError(ce.getMessage());
            }
            enableInput(!myController.hasModel());
        });
        otherButtons = new ArrayList<Button>();
        Button play = createButton(myResources.getString("PlayButton"), e -> myController.play());
        Button pause = createButton(myResources.getString("PauseButton"), e -> myController.pause());
        Button step = createButton(myResources.getString("StepButton"), e -> myController.step());
        Button options = createButton(myResources.getString("OptionButton"), e -> {
            inputStage.show();
            inputStage.setX(myStage.getX() + myStage.getWidth());
        });
        Button save = createButton(myResources.getString("SaveButton"), e -> {
            myController.pause();
            try {
                myChooser.setTitle(myResources.getString("SaveFile"));
                File dataFile = myChooser.showSaveDialog(myStage);
                if(dataFile != null) {
                    myController.save(dataFile);
                }
            } catch(CAException ce) {
                showError(ce.getMessage());
            }
        });
        Button zoomIn = createButton(myResources.getString("ZoomIn"), e -> myController.zoomIn());
        Button zoomOut = createButton(myResources.getString("ZoomOut"), e -> myController.zoomOut());
        otherButtons = Arrays.asList(play, pause, step, options, save, zoomIn, zoomOut);
    }

    private Button createButton(String label, EventHandler<ActionEvent> e) {
        Button b = new Button();
        b.setText(label);
        b.setOnAction(e);
        return b;
    }

    private void enableInput(boolean disable) {
        for(Button b: otherButtons) {
            b.setDisable(disable);
        }
        speedSlider.setDisable(disable);
        shapeChooser.setDisable(disable);
        gridChooser.setDisable(disable);
        neighborChooser.setDisable(disable);
    }

    private void createSpeedChooser() {
        speedSlider = createSpeedSlider();
        speedLabel = createLabel(String.format(myResources.getString("SpeedLabel"), Controller.DEFAULT_FPS));
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            speedLabel.setText(String.format(myResources.getString("SpeedLabel"), newValue.intValue()));
            myController.setSpeed(newValue.intValue());
        });
    }
    
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        return label;
    }
    
    private Slider createSpeedSlider() {
        Slider slider = new Slider(Controller.MIN_SPEED, Controller.MAX_SPEED, Controller.DEFAULT_FPS);
        slider.setMajorTickUnit(Controller.MAX_SPEED - Controller.MIN_SPEED);
        slider.setMinorTickCount(Controller.MAX_SPEED - Controller.MIN_SPEED - 1);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        return slider;
    }

    private void createShapeChooser() {
        shapeLabel = createLabel(myResources.getString("ShapeLabel"));
        shapeChooser = createChoiceBox(Controller.SHAPE_CHOICES, (observable, oldValue, newValue) -> {
            myController.setShape(newValue.intValue());
            });
    }
    
    private void createGridChooser() {
        gridLabel = createLabel(myResources.getString("GridLabel"));
        gridChooser = createChoiceBox(Grid.GRID_TYPE);
        gridChooser.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    myController.setGrid(newValue.toString());
                });
    }
    
    private void createNeighborPatternChooser() {
        neighborLabel = createLabel(myResources.getString("NeighborLabel"));
        neighborChooser = createChoiceBox(NeighborFinder.NEIGHBOR_PATTERN,
                (observable, oldValue, newValue) -> {
                    myController.setNeighborPattern(newValue.intValue());
                    });
    }
    
    private ChoiceBox<String> createChoiceBox(List<String> items, ChangeListener<? super Number> listener) {
        ChoiceBox<String> cb = createChoiceBox(items);
        cb.getSelectionModel().selectedIndexProperty().addListener(listener);
        return cb;
    }
    
    private ChoiceBox<String> createChoiceBox(List<String> items) {
        ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList(items));
        cb.getSelectionModel().selectFirst();
        return cb;
    }

    private FileChooser makeFileChooser(String extensionAccepted) {
        FileChooser result = new FileChooser();
        result.setInitialDirectory(new File(System.getProperty("user.dir")));
        result.getExtensionFilters().setAll(new ExtensionFilter("Text Files", extensionAccepted));
        return result;
    }
    
    private void showError(String message) {
        Alert a = new Alert(AlertType.ERROR);
        a.setContentText(message);
        a.showAndWait();
    }
    
    class MyLoadHandler implements LoadHandler {

        @Override
        public void setModelInput(Region root) {
            root.setId("model-input");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(DEFAULT_RESOURCE_PACKAGE + STYLESHEET);
            inputStage.setScene(scene);
            inputStage.sizeToScene();
        }
    }
}
