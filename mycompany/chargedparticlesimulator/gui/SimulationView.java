
package com.mycompany.chargedparticlesimulator.gui;

import com.mycompany.chargedparticlesimulator.visualization.SimulationCanvas;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;

public class SimulationView extends BorderPane {

    final SimulationCanvas canvas =
            new SimulationCanvas(850, 560);

    final TextField chargeField = new TextField("5");
    final TextField massField = new TextField("2");
    final TextField electricFieldField = new TextField("1000");
    final TextField gapField = new TextField("5");

    final TextField velocityField = new TextField("5");
    final TextField angleField = new TextField("0");

    final TextField timeField = new TextField("100");
    final TextField stepField = new TextField("0.5");

    final ComboBox<String> directionBox = new ComboBox<>();

    final Slider speedSlider = new Slider(0.25, 4, 1);

    final CheckBox gridCheck = new CheckBox("Show grid");
    final CheckBox fieldCheck = new CheckBox("Show electric field");
    final CheckBox pathCheck = new CheckBox("Show trajectory");
    final CheckBox vectorsCheck = new CheckBox("Show velocity vector");

    final Button runButton = new Button("Run");
    final Button pauseButton = new Button("Pause");
    final Button resetButton = new Button("Reset");
    final Button stepButton = new Button("Single Step");
    final Button exportButton = new Button("Export CSV");

    final Label statusLabel = new Label("Ready");

    final Label timeValue = new Label("0 ns");
    final Label positionValue = new Label("(0, 0) m");
    final Label velocityValue = new Label("(0, 0) m/s");
    final Label forceValue = new Label("(0, 0) N");
    final Label accelerationValue = new Label("(0, 0) m/s²");
    final Label voltageValue = new Label("0 V");

    public SimulationView() {

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

        directionBox.getItems().addAll("Up", "Down");
        directionBox.setValue("Up");

        gridCheck.setSelected(true);
        fieldCheck.setSelected(true);
        pathCheck.setSelected(true);
        vectorsCheck.setSelected(true);

        canvas.setWidth(620);
        canvas.setHeight(450);

        VBox controls = buildControls();

        HBox body = new HBox(12, canvas, controls);

        HBox.setHgrow(canvas, Priority.ALWAYS);

        setCenter(body);

        createMetricsPanel();

        new SimulationController(this);
    }

    private VBox buildControls() {

        VBox box = new VBox(9);

        box.setPadding(new Insets(12));
        box.setPrefWidth(320);
        box.setMinWidth(300);

        box.getStyleClass().add("control-panel");

        Label heading = new Label("Experiment Controls");
        heading.setFont(Font.font(17));

        GridPane inputs = new GridPane();

        inputs.setHgap(8);
        inputs.setVgap(7);

        int row = 0;

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

        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);

        HBox buttons1 = new HBox(
                6, runButton, pauseButton);

        HBox buttons2 = new HBox(
                6, resetButton, stepButton);

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

    private void addInput(
            GridPane grid,
            int row,
            String label,
            TextField field) {

        grid.add(new Label(label), 0, row);

        field.setPrefColumnCount(7);

        grid.add(field, 1, row);
    }

    private void createMetricsPanel() {

        GridPane metrics = new GridPane();

        metrics.setHgap(18);
        metrics.setVgap(5);

        metrics.setPadding(new Insets(10, 0, 0, 0));

        metrics.addRow(
                0,
                new Label("Time:"),
                timeValue,
                new Label("Position:"),
                positionValue,
                new Label("Velocity:"),
                velocityValue
        );

        metrics.addRow(
                1,
                new Label("Force:"),
                forceValue,
                new Label("Acceleration:"),
                accelerationValue,
                new Label("Potential difference:"),
                voltageValue
        );

        VBox bottom = new VBox(
                5,
                new Separator(),
                metrics,
                statusLabel
        );

        setBottom(bottom);
    }
}
