
package com.mycompany.chargedparticlesimulator.model;

public class ElectricField {

    private final double magnitude;
    private final int direction;

    public ElectricField(double magnitude, int direction) {

        if (magnitude < 0 || !Double.isFinite(magnitude)) {
            throw new IllegalArgumentException(
                    "Field magnitude must be non-negative.");
        }

        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException(
                    "Direction must be 1 or -1.");
        }

        this.magnitude = magnitude;
        this.direction = direction;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public int getDirection() {
        return direction;
    }

    public Vector2D asVector() {
        return new Vector2D(0, magnitude * direction);
    }
}
