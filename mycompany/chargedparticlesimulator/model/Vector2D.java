// لمار
package com.mycompany.chargedparticlesimulator.model;

public record Vector2D(double x, double y) {

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D scale(double factor) {
        return new Vector2D(x * factor, y * factor);
    }

    public double magnitude() {
        return Math.hypot(x, y);
    }
}
