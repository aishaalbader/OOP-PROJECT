// رغد
package com.mycompany.chargedparticlesimulator.physics;

import com.mycompany.chargedparticlesimulator.model.*;

public class PhysicsCalculator {

    public static Vector2D calculateForce(
            Particle particle,
            ElectricField field) {

        return field.asVector().scale(particle.getCharge());
    }

    public static Vector2D calculateAcceleration(
            Particle particle,
            ElectricField field) {

        Vector2D force = calculateForce(particle, field);

        return force.scale(1.0 / particle.getMass());
    }

    public static double calculatePotentialDifference(
            double fieldMagnitude,
            double distance) {

        return fieldMagnitude * distance;
    }
}
