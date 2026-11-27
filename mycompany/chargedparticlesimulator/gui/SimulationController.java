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
    
    // View component reference
    private final SimulationView view;
    
    // Helper utilities for validation, physics calculation, and file export
    private final InputValidator validator = new InputValidator();
    private final AnalyticalSolver solver = new AnalyticalSolver();
    private final CsvFileManager csvManager = new CsvFileManager();
    
    // Model entities and calculated trajectory data
    private Particle particle;
    private ElectricField field;
    private List<SimulationState> playbackStates =
            List.of();

    // Animation loop state variables
    private int index = 0;
    private boolean running = false;
    private long lastFrame = 0;
    private double accumulator = 0;
    private AnimationTimer timer;

    // Constructor: Binds the view and initializes UI action listeners &amp; timer
    public SimulationController(SimulationView view) {
        this.view = view;
        configureActions();
        updateDisplayOptions();
        createAnimationTimer();
    }

    // Creates and initializes the AnimationTimer for continuous frame rendering
    private void createAnimationTimer() {
        timer = new AnimationTimer() {
            
     // Frame handling loop called on every animation tick (~60 FPS)
            @Override
            public void handle(long now) {
                
                // Pause check: skip execution if simulation is stopped
                if (!running) {
                    return;
                }
                // Set initial timestamp reference on the first frame
                if (lastFrame == 0) {
                    lastFrame = now;
                }
                // Calculate elapsed time in seconds and update accumulator with slider playback speed
                double elapsed = (now - lastFrame) / 1_000_000_000.0;
                lastFrame = now;
                accumulator += elapsed * view.speedSlider.getValue();

                // Advance simulation frame index using fixed timestep interval (0.025s)
                double interval = 0.025;
                while (accumulator >= interval && index < playbackStates.size() - 1) {
                    index++;
                    accumulator -= interval;
                }
                // Render current particle state on the GUI
                showCurrentState();
                
                // Stop timer loop and update status text when simulation reaches final state
                if (index >= playbackStates.size() - 1) {
                    running = false;
                    view.statusLabel.setText("Simulation finished.");
                }
            }
        };
    }
    
    // Configure and register event handlers for UI components    
    private void configureActions() {
        // Register action handlers for buttons using lambda expressions
        view.runButton.setOnAction(e -> runSimulation());
        view.pauseButton.setOnAction(e -> pause());
        view.resetButton.setOnAction(e -> reset());
        view.stepButton.setOnAction(e -> singleStep());
        view.exportButton.setOnAction(e -> exportCsv());
        
        // Register property listeners for check box selection changes
        view.gridCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateDisplayOptions());
        view.fieldCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateDisplayOptions());
        view.pathCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateDisplayOptions());
        view.vectorsCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateDisplayOptions());
    }

    // Action handler to start or resume the simulation
    private void runSimulation() {

        // Resume the existing simulation if paused.
        if (!playbackStates.isEmpty() && index < playbackStates.size() - 1) {
            running = true;
            lastFrame = 0;
            timer.start();
            view.statusLabel.setText("Simulation resumed.");
            return;
        }

        // Try block to parse GUI input fields and convert values to standard SI units
        try {
            double q = validator.parseFinite(view.chargeField.getText(),"Charge", 50) * 1e-6;
            double m = validator.parsePositive(view.massField.getText(),"Mass", 25) * 1e-16;
            double e = validator.parseFinite(view.electricFieldField.getText(),"Electric field", 5000);

            // Validate physics constraints and throw custom exception for negative electric field
            if (e < 0) {
                throw new InvalidSimulationInputException("Electric field cannot be negative.");
            }
            
            double gap = validator.parsePositive(view.gapField.getText(),"Plate gap", 10) / 100.0;
            double speed = validator.parseFinite(view.velocityField.getText(),"Initial speed", 50) * 1e5;

            // Validate physics constraints and throw custom exception for negative speed
            if (speed < 0) {
               throw new InvalidSimulationInputException("Initial speed cannot be negative.");
            }

            // Convert angle from degrees to radians and time parameters to seconds
            double angle = Math.toRadians(validator.parseFinite(view.angleField.getText(),"Launch angle"));
            double totalTime = validator.parsePositive(view.timeField.getText(),"Total time") * 1e-9;
            double dt = validator.parsePositive(view.stepField.getText(),"Time step") * 1e-9;

            // Validate time step constraint against total simulation duration
            if (dt > totalTime) {
                throw new InvalidSimulationInputException("Time step cannot exceed total time.");
            }
            // Instantiate model objects (ChargedParticle and ElectricField)
            particle = new ChargedParticle(q,m,new Vector2D(0, 0),new Vector2D(speed * Math.cos(angle),speed * Math.sin(angle)));
            int direction = view.directionBox.getValue().equals("Up") ? 1 : -1;
            field = new ElectricField(e, direction);

            // Compute physics trajectory states and validate solver output
            playbackStates = solver.calculate(particle,field,totalTime,dt);
            if (playbackStates.isEmpty()) {
                throw new InvalidSimulationInputException("No simulation data was generated.");
            }

            // Reset animation state variables and set up canvas for rendering
            index = 0;
            running = false;
            lastFrame = 0;
            accumulator = 0;
            view.canvas.setExperiment(particle,field,gap,playbackStates,List.of());
            showCurrentState();

            // Start animation timer and update status display
            running = true;
            timer.start();
            view.statusLabel.setText("Running simulation...");
        }

        // Multi-catch block to handle invalid input exceptions and display error message
        catch (InvalidSimulationInputException | IllegalArgumentException ex) {
            running = false;
            timer.stop();
            view.statusLabel.setText("Input error: " + ex.getMessage());
        }
    }
    
    // Action handler to pause the running simulation
    private void pause() {
        running = false;
        timer.stop();
        view.statusLabel.setText("Paused. Press Run to resume.");
    }

    // Action handler to reset simulation state and update display
    private void reset() {
        running = false;
        timer.stop();
        index = 0;
        lastFrame = 0;
        accumulator = 0;

        // Reset display to first frame if simulation data exists, otherwise clear canvas
        if (!playbackStates.isEmpty()) {
            showCurrentState();
            view.statusLabel.setText("Simulation reset.");
        } 
            
        else {
            view.canvas.setCurrent(null);
            view.statusLabel.setText("Ready");
        }
    }
    
    // Action handler to execute single-step animation (advance one frame)
    private void singleStep() {
        // Run and pause simulation if states are not yet generated
        if (playbackStates.isEmpty()) {
            runSimulation();
            
            if (playbackStates.isEmpty()) {
                return;
            }
            pause();
        }

        // Freeze continuous timer and advance simulation frame index by 1
        running = false;
        timer.stop();
        if (index < playbackStates.size() - 1) {
            index++;
        }
        // Render updated particle position and update status text
        showCurrentState();
        view.statusLabel.setText("Single-step mode.");
    }

    // Helper method to update GUI canvas and text labels with current simulation state
    private void showCurrentState() {
        if (playbackStates.isEmpty()) {
            return;
        }

        // Retrieve current frame state safely and update canvas rendering
        SimulationState state = playbackStates.get(Math.min(index, playbackStates.size() - 1));
        view.canvas.setCurrent(state);

        // Format and display kinematics data (time, position, velocity)
        view.timeValue.setText(String.format("%.3f ns", state.time() * 1e9));
        view.positionValue.setText(String.format("(%.4g, %.4g) m", state.position().x(),state.position().y()));
        view.velocityValue.setText(String.format("(%.4g, %.4g) m/s", state.velocity().x(), state.velocity().y()));

        // Compute dynamic physics values (force, acceleration, voltage) and update display labels
        Vector2D force = PhysicsCalculator.calculateForce(particle, field);
        Vector2D acceleration = PhysicsCalculator.calculateAcceleration(particle, field);
        view.forceValue.setText(String.format("(%.3g, %.3g) N",force.x(),force.y()));
        view.accelerationValue.setText(String.format("(%.3g, %.3g) m/s²",acceleration.x(),acceleration.y()));

        
        double gap = Double.parseDouble(view.gapField.getText()) / 100.0;
        double voltage = PhysicsCalculator.calculatePotentialDifference(field.getMagnitude(), gap);
        view.voltageValue.setText(String.format("%.4g V", voltage));
    }

    // Helper method to update canvas display options based on check box selections
    private void updateDisplayOptions() {
        view.canvas.setDisplayOptions(view.gridCheck.isSelected(),view.fieldCheck.isSelected(),view.pathCheck.isSelected(),view.vectorsCheck.isSelected());
    }

    // Action handler to launch FileChooser dialog and prepare CSV export
    private void exportCsv() {
        if (playbackStates.isEmpty()) {
            view.statusLabel.setText("Run a simulation before exporting.");
            return;
        }

        // Configure JavaFX FileChooser dialog for CSV file saving
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export simulation data");
        chooser.setInitialFileName("charged-particle-results.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = chooser.showSaveDialog(view.getScene().getWindow());

        // Cancel export execution if user dismisses the file dialog
        if (file == null) {
            return;
        }

        // Try block to save simulation playback states to the selected CSV file path
        try {
            csvManager.saveStates( Path.of(file.toURI()), playbackStates);
            
            // Update JavaFX status label upon successful export
            view.statusLabel.setText("CSV exported successfully.");
        }

        // Exception handler block to catch file I/O errors and notify the user    
        catch (Exception ex) {
            view.statusLabel.setText("Export failed: " + ex.getMessage());
        }
    }
}
