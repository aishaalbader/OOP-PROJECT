// لمار
package com.mycompany.chargedparticlesimulator.model;

public class ChargedParticle extends Particle {

    public ChargedParticle(
            double charge,
            double mass,
            Vector2D initialPosition,
            Vector2D initialVelocity) {

        super(charge, mass, initialPosition, initialVelocity);
    }

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
