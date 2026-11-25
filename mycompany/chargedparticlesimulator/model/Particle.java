// لمار
package com.mycompany.chargedparticlesimulator.model;


// Abstract base class for all particle types
public abstract class Particle {

    // Private final fields for encapsulation
    private final double charge;
    private final double mass;
    private final Vector2D initialPosition;
    private final Vector2D initialVelocity;

    // Parameterized constructor
    public Particle(
            double charge,
            double mass,
            Vector2D initialPosition,
            Vector2D initialVelocity) {

        // Check invalid values (mass must be positive)
        if (mass <= 0 || !Double.isFinite(mass)) {
            throw new IllegalArgumentException("Mass must be positive.");
        }

        // Charge must be finite value
        if (!Double.isFinite(charge)) {
            throw new IllegalArgumentException("Charge must be finite.");
        }

        this.charge = charge;
        this.mass = mass;
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
    }

    // Getter methodes for private fields
    public double getCharge() {
        return charge;
    }

    public double getMass() {
        return mass;
    }

    public Vector2D getInitialPosition() {
        return initialPosition;
    }

    public Vector2D getInitialVelocity() {
        return initialVelocity;
    }

    // Abstract method must be implemented by subclasses
    public abstract String getParticleType();
}
