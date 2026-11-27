// سفانة
package com.mycompany.chargedparticlesimulator.gui;
// Project Layer Imports (MVC Architecture Components)
import com.mycompany.chargedparticlesimulator.data.CsvFileManager;
import com.mycompany.chargedparticlesimulator.model.*;
import com.mycompany.chargedparticlesimulator.physics.*;
import com.mycompany.chargedparticlesimulator.utilities.*;
// JavaFX Animation &amp; GUI Utility Imports
import javafx.animation.AnimationTimer;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class SimulationController {
    // Reference to the GUI View component
    private final SimulationView view;
    // Helper utilities and solvers
    private final InputValidator validator = new InputValidator();
    private final AnalyticalSolver solver = new AnalyticalSolver();
    private final CsvFileManager csvManager = new CsvFileManager();

    private Particle particle;
    private ElectricField field;

    private List<SimulationState> playbackStates =
            List.of();

    private int index = 0;

    private boolean running = false;

    private long lastFrame = 0;

    private double accumulator = 0;

    private AnimationTimer timer;

    public SimulationController(SimulationView view) {

        this.view = view;

        configureActions();
        updateDisplayOptions();
        createAnimationTimer();
    }

    private void createAnimationTimer() {

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                if (!running) {
                    return;
                }

                if (lastFrame == 0) {
                    lastFrame = now;
                }

                double elapsed =
                        (now - lastFrame) / 1_000_000_000.0;

                lastFrame = now;

                accumulator +=
                        elapsed * view.speedSlider.getValue();

                double interval = 0.025;

                while (accumulator >= interval
                        && index < playbackStates.size() - 1) {

                    index++;
                    accumulator -= interval;
                }

                showCurrentState();

                if (index >= playbackStates.size() - 1) {

                    running = false;

                    view.statusLabel.setText(
                            "Simulation finished.");
                }
            }
        };
    }

    private void configureActions() {

        view.runButton.setOnAction(e -> runSimulation());
        view.pauseButton.setOnAction(e -> pause());
        view.resetButton.setOnAction(e -> reset());
        view.stepButton.setOnAction(e -> singleStep());
        view.exportButton.setOnAction(e -> exportCsv());

        view.gridCheck.selectedProperty().addListener(
                (obs, oldValue, newValue) -> updateDisplayOptions());

        view.fieldCheck.selectedProperty().addListener(
                (obs, oldValue, newValue) -> updateDisplayOptions());

        view.pathCheck.selectedProperty().addListener(
                (obs, oldValue, newValue) -> updateDisplayOptions());

        view.vectorsCheck.selectedProperty().addListener(
                (obs, oldValue, newValue) -> updateDisplayOptions());
    }

    private void runSimulation() {

        // Resume the existing simulation if paused.
        if (!playbackStates.isEmpty()
                && index < playbackStates.size() - 1) {

            running = true;
            lastFrame = 0;
            timer.start();

            view.statusLabel.setText("Simulation resumed.");

            return;
        }

        try {
            double q = validator.parseFinite(
                    view.chargeField.getText(),
                    "Charge", 50) * 1e-6;
            double m = validator.parsePositive(
                    view.massField.getText(),
                    "Mass", 25) * 1e-16;
            double e = validator.parseFinite(
                    view.electricFieldField.getText(),
                    "Electric field", 5000);

    if (e < 0) {
        throw new InvalidSimulationInputException(
                "Electric field cannot be negative.");
    }
    
    double gap = validator.parsePositive(
            view.gapField.getText(),
            "Plate gap", 10) / 100.0;

    double speed = validator.parseFinite(
            view.velocityField.getText(),
            "Initial speed", 50) * 1e5;

    if (speed < 0) {
        throw new InvalidSimulationInputException(
                "Initial speed cannot be negative.");
    }

    double angle = Math.toRadians(
            validator.parseFinite(
                    view.angleField.getText(),
                    "Launch angle"));

    double totalTime = validator.parsePositive(
            view.timeField.getText(),
            "Total time") * 1e-9;

    double dt = validator.parsePositive(
            view.stepField.getText(),
            "Time step") * 1e-9;

            if (dt > totalTime) {
                throw new InvalidSimulationInputException(
                        "Time step cannot exceed total time.");
            }

            particle = new ChargedParticle(
                    q,
                    m,
                    new Vector2D(0, 0),
                    new Vector2D(
                            speed * Math.cos(angle),
                            speed * Math.sin(angle))
            );

            int direction =
                    view.directionBox.getValue().equals("Up")
                            ? 1 : -1;

            field = new ElectricField(e, direction);

            playbackStates = solver.calculate(
                    particle,
                    field,
                    totalTime,
                    dt
            );

            if (playbackStates.isEmpty()) {
                throw new InvalidSimulationInputException(
                        "No simulation data was generated.");
            }

            index = 0;
            running = false;
            lastFrame = 0;
            accumulator = 0;

            view.canvas.setExperiment(
                    particle,
                    field,
                    gap,
                    playbackStates,
                    List.of()
            );

            showCurrentState();

            running = true;
            timer.start();

            view.statusLabel.setText(
                    "Running simulation...");

        } catch (
                InvalidSimulationInputException |
                IllegalArgumentException ex) {

            running = false;
            timer.stop();

            view.statusLabel.setText(
                    "Input error: " + ex.getMessage());
        }
    }

    private void pause() {

        running = false;
        timer.stop();

        view.statusLabel.setText(
                "Paused. Press Run to resume.");
    }

    private void reset() {

        running = false;
        timer.stop();

        index = 0;
        lastFrame = 0;
        accumulator = 0;

        if (!playbackStates.isEmpty()) {

            showCurrentState();

            view.statusLabel.setText(
                    "Simulation reset.");

        } else {

            view.canvas.setCurrent(null);
            view.statusLabel.setText("Ready");
        }
    }

    private void singleStep() {

        if (playbackStates.isEmpty()) {

            runSimulation();

            if (playbackStates.isEmpty()) {
                return;
            }

            pause();
        }

        running = false;
        timer.stop();

        if (index < playbackStates.size() - 1) {
            index++;
        }

        showCurrentState();

        view.statusLabel.setText("Single-step mode.");
    }

    private void showCurrentState() {

        if (playbackStates.isEmpty()) {
            return;
        }

        SimulationState state =
                playbackStates.get(
                        Math.min(index, playbackStates.size() - 1));

        view.canvas.setCurrent(state);

        view.timeValue.setText(
                String.format("%.3f ns", state.time() * 1e9));

        view.positionValue.setText(
                String.format(
                        "(%.4g, %.4g) m",
                        state.position().x(),
                        state.position().y()));

        view.velocityValue.setText(
                String.format(
                        "(%.4g, %.4g) m/s",
                        state.velocity().x(),
                        state.velocity().y()));

        Vector2D force =
                PhysicsCalculator.calculateForce(particle, field);

        Vector2D acceleration =
                PhysicsCalculator.calculateAcceleration(particle, field);

        view.forceValue.setText(
                String.format(
                        "(%.3g, %.3g) N",
                        force.x(),
                        force.y()));

        view.accelerationValue.setText(
                String.format(
                        "(%.3g, %.3g) m/s²",
                        acceleration.x(),
                        acceleration.y()));

        double gap =
                Double.parseDouble(view.gapField.getText()) / 100.0;

        double voltage =
                PhysicsCalculator.calculatePotentialDifference(
                        field.getMagnitude(), gap);

        view.voltageValue.setText(
                String.format("%.4g V", voltage));
    }

    private void updateDisplayOptions() {

        view.canvas.setDisplayOptions(
                view.gridCheck.isSelected(),
                view.fieldCheck.isSelected(),
                view.pathCheck.isSelected(),
                view.vectorsCheck.isSelected()
        );
    }

    private void exportCsv() {

        if (playbackStates.isEmpty()) {

            view.statusLabel.setText(
                    "Run a simulation before exporting.");

            return;
        }

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Export simulation data");

        chooser.setInitialFileName(
                "charged-particle-results.csv");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV files", "*.csv"));

        File file = chooser.showSaveDialog(
                view.getScene().getWindow());

        if (file == null) {
            return;
        }

        try {

            csvManager.saveStates(
                    Path.of(file.toURI()),
                    playbackStates);

            view.statusLabel.setText(
                    "CSV exported successfully.");

        } catch (Exception ex) {

            view.statusLabel.setText(
                    "Export failed: " + ex.getMessage());
        }
    }
}
