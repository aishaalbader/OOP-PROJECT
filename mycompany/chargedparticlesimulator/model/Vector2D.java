// لمار
package com.mycompany.chargedparticlesimulator.model;

// Represents an immutable two-dimmensional vector
public record Vector2D(double x, double y) {

    // Add two vectors
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    // Subtract two vectors
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    // Multiply the vector by a scaler
    public Vector2D scale(double factor) {
        return new Vector2D(x * factor, y * factor);
    }

    // Calculate the vector magnitude
    public double magnitude() {
        return Math.hypot(x, y);
    }
}
