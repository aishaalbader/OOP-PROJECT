// لمار
package com.mycompany.chargedparticlesimulator.model;

// Derived class that inherites from Particle
public class ChargedParticle extends Particle {

    // Parameterized constructor
    public ChargedParticle(
            double charge,
            double mass,
            Vector2D initialPosition,
            Vector2D initialVelocity) {

        // Call the superclass constructor 
        super(charge, mass, initialPosition, initialVelocity);
    }

    // Override the abstract method from Particle
    @Override
    public String getParticleType() {

        if (getCharge() > 0) {
            return "Positive";
        }

        if (getCharge() < 0) {
            return "Negative";
        }

        return "Neutral";
    }
}
