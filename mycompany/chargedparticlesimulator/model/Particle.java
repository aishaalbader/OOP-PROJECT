// لمار
package com.mycompany.chargedparticlesimulator.model;

public abstract class Particle {

    private final double charge;
    private final double mass;
    private final Vector2D initialPosition;
    private final Vector2D initialVelocity;

    public Particle(
            double charge,
            double mass,
            Vector2D initialPosition,
            Vector2D initialVelocity) {

        if (mass <= 0 || !Double.isFinite(mass)) {
            throw new IllegalArgumentException("Mass must be positive.");
        }

        if (!Double.isFinite(charge)) {
            throw new IllegalArgumentException("Charge must be finite.");
        }

        this.charge = charge;
        this.mass = mass;
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
    }

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

    public abstract String getParticleType();
}
