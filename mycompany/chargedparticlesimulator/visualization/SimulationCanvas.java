
package com.mycompany.chargedparticlesimulator.visualization;

import com.mycompany.chargedparticlesimulator.model.*;
import com.mycompany.chargedparticlesimulator.physics.SimulationState;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class SimulationCanvas extends Canvas {

    private Particle particle;
    private ElectricField field;

    private double plateGap = 0.05;

    private List<SimulationState> analytical = List.of();
    private List<SimulationState> numerical = List.of();

    private SimulationState current;

    private boolean showGrid = true;
    private boolean showField = true;
    private boolean showTrajectory = true;
    private boolean showVectors = true;

    private double horizontalScale = 500;
    private double verticalScale = 500;

    private double originX;
    private double originY;

    private static final Color BACKGROUND =
            Color.web("#171122");

    private static final Color GRID =
            Color.web("#38294B");

    private static final Color GRID_AXIS =
            Color.web("#7650A8");

    private static final Color PLATE =
            Color.web("#C5A7F2");

    private static final Color FIELD_COLOR =
            Color.web("#B68AF0");

    private static final Color ANALYTICAL_COLOR =
            Color.web("#C084FC");

    private static final Color NUMERICAL_COLOR =
            Color.web("#F0ABFC");

    private static final Color POSITIVE_PARTICLE =
            Color.web("#A78BFA");

    private static final Color NEGATIVE_PARTICLE =
            Color.web("#F0ABFC");

    private static final Color VECTOR_COLOR =
            Color.web("#FBBF9A");

    private static final Color TEXT_COLOR =
            Color.web("#E8DDF8");

    public SimulationCanvas(double width, double height) {

        super(width, height);

        widthProperty().addListener(
                (obs, oldValue, newValue) -> draw());

        heightProperty().addListener(
                (obs, oldValue, newValue) -> draw());

        draw();
    }

    public void setExperiment(
            Particle particle,
            ElectricField field,
            double plateGap,
            List<SimulationState> analytical,
            List<SimulationState> numerical) {

        this.particle = particle;
        this.field = field;
        this.plateGap = plateGap;

        this.analytical = analytical == null
                ? List.of() : analytical;

        this.numerical = numerical == null
                ? List.of() : numerical;

        current = null;

        calculateScale();
        draw();
    }

    public void setCurrent(SimulationState state) {

        current = state;
        draw();
    }

    public void setDisplayOptions(
            boolean grid,
            boolean electricField,
            boolean trajectory,
            boolean vectors) {

        showGrid = grid;
        showField = electricField;
        showTrajectory = trajectory;
        showVectors = vectors;

        draw();
    }

    private void calculateScale() {

        double width = getWidth();
        double height = getHeight();

        double left = 80;
        double right = width - 35;

        double top = 55;
        double bottom = height - 55;

        double maxX = 0.01;

        List<SimulationState> states =
                !analytical.isEmpty() ? analytical : numerical;

        for (SimulationState state : states) {

            maxX = Math.max(
                    maxX,
                    state.position().x());
        }

        double plateLeft = left + 90;
        double plateRight = right - 20;

        horizontalScale =
                (plateRight - plateLeft - 45) / maxX;

        verticalScale =
                (bottom - top) * 0.72 / plateGap;

        originX = plateLeft - 35;
        originY = (top + bottom) / 2;
    }

    private double screenX(double x) {
        return originX + x * horizontalScale;
    }

    private double screenY(double y) {
        return originY - y * verticalScale;
    }

    public void draw() {

        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        if (particle != null) {
            calculateScale();
        }

        GraphicsContext g = getGraphicsContext2D();

        g.setFill(BACKGROUND);
        g.fillRect(0, 0, width, height);

        drawGrid(g, width, height);
        drawPlates(g, width, height);
        drawElectricField(g, width, height);

        if (showTrajectory) {
            drawTrajectories(g);
        }

        drawParticle(g);
    }

    private void drawGrid(
            GraphicsContext g,
            double width,
            double height) {

        if (!showGrid) {
            return;
        }

        g.setStroke(GRID);
        g.setLineWidth(0.8);
        g.setLineDashes(3, 6);

        for (double x = 40; x < width; x += 45) {
            g.strokeLine(x, 40, x, height - 35);
        }

        for (double y = 40; y < height; y += 45) {
            g.strokeLine(40, y, width - 25, y);
        }

        g.setLineDashes();
        g.setStroke(GRID_AXIS);
        g.setLineWidth(1);

        g.strokeLine(
                40,
                height / 2,
                width - 25,
                height / 2);
    }

    private void drawPlates(
            GraphicsContext g,
            double width,
            double height) {

        double centerY = height / 2;

        double gapPixels = height * 0.70;

        double upperY = centerY - gapPixels / 2;
        double lowerY = centerY + gapPixels / 2;

        double left = 80 + 90;
        double right = width - 55;

        g.setStroke(PLATE);
        g.setLineWidth(3.5);

        g.strokeLine(left, upperY, right, upperY);
        g.strokeLine(left, lowerY, right, lowerY);

        g.setFill(TEXT_COLOR);
        g.setFont(Font.font("Segoe UI", 16));

        g.fillText("−", left - 28, upperY + 7);
        g.fillText("+", left - 28, lowerY + 7);

        g.setFont(Font.font("Segoe UI", 12));

        g.fillText(
                "Negative plate",
                right - 110,
                upperY - 10);

        g.fillText(
                "Positive plate",
                right - 110,
                lowerY + 22);
    }

    private void drawElectricField(
            GraphicsContext g,
            double width,
            double height) {

        if (!showField || field == null) {
            return;
        }

        double centerY = height / 2;
        double gapPixels = height * 0.70;

        double upperY = centerY - gapPixels / 2;
        double lowerY = centerY + gapPixels / 2;

        double left = 80 + 90;
        double right = width - 55;

        g.setStroke(FIELD_COLOR);
        g.setFill(FIELD_COLOR);
        g.setLineWidth(1.5);

        for (double x = left + 12; x < right - 5; x += 23) {

            double startY;
            double endY;

            if (field.getDirection() > 0) {
                startY = lowerY - 8;
                endY = upperY + 8;
            } else {
                startY = upperY + 8;
                endY = lowerY - 8;
            }

            g.strokeLine(x, startY, x, endY);

            double arrowDirection =
                    field.getDirection() > 0 ? 1 : -1;

            g.fillPolygon(
                    new double[]{x - 4, x + 4, x},
                    new double[]{
                        endY + 7 * arrowDirection,
                        endY + 7 * arrowDirection,
                        endY
                    },
                    3);
        }
    }

    private void drawTrajectories(GraphicsContext g) {

        drawPath(g, analytical, ANALYTICAL_COLOR);
        drawPath(g, numerical, NUMERICAL_COLOR);
    }

    private void drawPath(
            GraphicsContext g,
            List<SimulationState> states,
            Color color) {

        if (states.size() < 2) {
            return;
        }

        int lastIndex = states.size() - 1;

        if (current != null) {

            lastIndex = 0;

            for (int i = 0; i < states.size(); i++) {

                if (states.get(i).time() <= current.time()) {
                    lastIndex = i;
                } else {
                    break;
                }
            }
        }

        g.setStroke(color);
        g.setLineWidth(2.5);

        for (int i = 1; i <= lastIndex; i++) {

            Vector2D first =
                    states.get(i - 1).position();

            Vector2D second =
                    states.get(i).position();

            g.strokeLine(
                    screenX(first.x()),
                    screenY(first.y()),
                    screenX(second.x()),
                    screenY(second.y()));
        }
    }

    private void drawParticle(GraphicsContext g) {

        if (particle == null || current == null) {
            return;
        }

        Vector2D position = current.position();

        double x = screenX(position.x());
        double y = screenY(position.y());

        Color particleColor =
                particle.getCharge() >= 0
                        ? POSITIVE_PARTICLE
                        : NEGATIVE_PARTICLE;

        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        g.strokeOval(x - 8, y - 8, 16, 16);

        g.setFill(particleColor);
        g.fillOval(x - 7, y - 7, 14, 14);

        if (showVectors) {
            drawVelocityVector(g, x, y);
        }
    }

    private void drawVelocityVector(
            GraphicsContext g,
            double x,
            double y) {

        Vector2D velocity = current.velocity();

        double scale = 0.00005;

        double endX = x + velocity.x() * scale;
        double endY = y - velocity.y() * scale;

        g.setStroke(VECTOR_COLOR);
        g.setLineWidth(2);

        g.strokeLine(x, y, endX, endY);

        g.setFill(VECTOR_COLOR);
        g.fillOval(endX - 3, endY - 3, 6, 6);
    }
}
