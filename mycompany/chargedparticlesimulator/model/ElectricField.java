// لمار
package com.mycompany.chargedparticlesimulator.model;

// Represents a uniform electric field
public class ElectricField {

    // Field magnitude and direction
    private final double magnitude;
    private final int direction;

    // Parameterized constructor 
    public ElectricField(double magnitude, int direction) {

        // Check invalidation (field magnitude cannot be negative)
        if (magnitude < 0 || !Double.isFinite(magnitude)) {
            throw new IllegalArgumentException(
                    "Field magnitude must be non-negative.");
        }

        // Check invalidation (direction must be either +1 or -1)
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException(
                    "Direction must be 1 or -1.");
        }

        this.magnitude = magnitude;
        this.direction = direction;
    }

    // Getter methods 
    public double getMagnitude() {
        return magnitude;
    }

    public int getDirection() {
        return direction;
    }

    // Convert the electric field to a vertical 2D vector
    public Vector2D asVector() {
        return new Vector2D(0, magnitude * direction);
    }
}
