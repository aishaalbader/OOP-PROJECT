// حنين
package com.mycompany.chargedparticlesimulator.gui;

import com.mycompany.chargedparticlesimulator.visualization.SimulationCanvas;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;

public class SimulationView extends BorderPane {

    // Create the canvas to display particle motion
    final SimulationCanvas canvas =
            new SimulationCanvas(850, 560);

    // Text fields for entering the physical values
    final TextField chargeField = new TextField("5");
    final TextField massField = new TextField("2");
    final TextField electricFieldField = new TextField("1000");
    final TextField gapField = new TextField("5");

    final TextField velocityField = new TextField("5");
    final TextField angleField = new TextField("0");

    final TextField timeField = new TextField("100");
    final TextField stepField = new TextField("0.5");

    // Allow the user to select the electric field direction
    final ComboBox<String> directionBox = new ComboBox<>();

    // Slider to control the animation speed
    final Slider speedSlider = new Slider(0.25, 4, 1);

    // Checkboxes to show or hide simulation elements
    final CheckBox gridCheck = new CheckBox("Show grid");
    final CheckBox fieldCheck = new CheckBox("Show electric field");
    final CheckBox pathCheck = new CheckBox("Show trajectory");
    final CheckBox vectorsCheck = new CheckBox("Show velocity vector");

    // Buttons to control the simulation and export results
    final Button runButton = new Button("Run");
    final Button pauseButton = new Button("Pause");
    final Button resetButton = new Button("Reset");
    final Button stepButton = new Button("Single Step");
    final Button exportButton = new Button("Export CSV");

    // Labels to display the simulation status and results
    final Label statusLabel = new Label("Ready");

    final Label timeValue = new Label("0 ns");
    final Label positionValue = new Label("(0, 0) m");
    final Label velocityValue = new Label("(0, 0) m/s");
    final Label forceValue = new Label("(0, 0) N");
    final Label accelerationValue = new Label("(0, 0) m/s²");
    final Label voltageValue = new Label("0 V");

    // Constructor to initialize the simulation interface
    public SimulationView() {

        // Load the CSS file to style the interface
        URL css = getClass().getResource(
                "/styles/dark-purple.css");

        if (css != null) {
            getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("CSS FILE NOT FOUND!");
        }

        setPadding(new Insets(12));

        Label title = new Label(
                "Charged Particle in a Uniform Electric Field");

        title.setFont(Font.font(23));
        title.getStyleClass().add("title-label");

        setTop(title);

        // Set the default electric field direction to Up
        directionBox.getItems().addAll("Up", "Down");
        directionBox.setValue("Up");

        // Enable all visual options by default
        gridCheck.setSelected(true);
        fieldCheck.setSelected(true);
        pathCheck.setSelected(true);
        vectorsCheck.setSelected(true);

        // Set the size of the simulation canvas
        canvas.setWidth(620);
        canvas.setHeight(450);

        // Arrange the canvas and control panel side by side
        VBox controls = buildControls();

        HBox body = new HBox(12, canvas, controls);

        HBox.setHgrow(canvas, Priority.ALWAYS);

        setCenter(body);

        createMetricsPanel();

        // Connect the interface to the simulation controller
        new SimulationController(this);
    }

    // Create the control panel for user inputs and buttons
    private VBox buildControls() {

        VBox box = new VBox(9);

        box.setPadding(new Insets(12));
        box.setPrefWidth(320);
        box.setMinWidth(300);

        box.getStyleClass().add("control-panel");

        Label heading = new Label("Experiment Controls");
        heading.setFont(Font.font(17));

        // Use GridPane to organize the input fields
        GridPane inputs = new GridPane();

        inputs.setHgap(8);
        inputs.setVgap(7);

        int row = 0;

        // Add the physical input fields to the control panel
        addInput(inputs, row++, "Charge (μC)", chargeField);
        addInput(inputs, row++, "Mass (×10⁻¹⁶ kg)", massField);
        addInput(inputs, row++, "Field (N/C)", electricFieldField);
        addInput(inputs, row++, "Plate gap (cm)", gapField);
        addInput(inputs, row++, "Initial speed (×10⁵ m/s)", velocityField);
        addInput(inputs, row++, "Launch angle (°)", angleField);
        addInput(inputs, row++, "Total time (ns)", timeField);
        addInput(inputs, row++, "Time step (ns)", stepField);

        Label directionLabel =
                new Label("Electric field direction");

        Label speedLabel =
                new Label("Animation speed");

        // Display tick marks and labels on the speed slider
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);

        // Arrange the simulation buttons into two rows
        HBox buttons1 = new HBox(
                6, runButton, pauseButton);

        HBox buttons2 = new HBox(
                6, resetButton, stepButton);

        // Add all controls to the panel
        box.getChildren().addAll(
                heading,
                inputs,
                directionLabel,
                directionBox,
                new Separator(),
                gridCheck,
                fieldCheck,
                pathCheck,
                vectorsCheck,
                speedLabel,
                speedSlider,
                buttons1,
                buttons2,
                exportButton
        );

        return box;
    }

    // Add a label and its text field to the specified row
    private void addInput(
            GridPane grid,
            int row,
            String label,
            TextField field) {

        grid.add(new Label(label), 0, row);

        field.setPrefColumnCount(7);

        grid.add(field, 1, row);
    }

    // Create a panel to display simulation results
    private void createMetricsPanel() {

        GridPane metrics = new GridPane();

        metrics.setHgap(18);
        metrics.setVgap(5);

        metrics.setPadding(new Insets(10, 0, 0, 0));

        // Display time, position, and velocity
        metrics.addRow(
                0,
                new Label("Time:"),
                timeValue,
                new Label("Position:"),
                positionValue,
                new Label("Velocity:"),
                velocityValue
        );

        // Display force, acceleration, and potential difference
        metrics.addRow(
                1,
                new Label("Force:"),
                forceValue,
                new Label("Acceleration:"),
                accelerationValue,
                new Label("Potential difference:"),
                voltageValue
        );

        // Group the results and status label at the bottom
        VBox bottom = new VBox(
                5,
                new Separator(),
                metrics,
                statusLabel
        );

        setBottom(bottom);
    }
}
