// رغد
package com.mycompany.chargedparticlesimulator.physics;

import com.mycompany.chargedparticlesimulator.model.*;

import java.util.ArrayList;
import java.util.List;

public class AnalyticalSolver {

    public List<SimulationState> calculate(
            Particle particle,
            ElectricField field,
            double totalTime,
            double timeStep) {

        validateTime(totalTime, timeStep);

        Vector2D acceleration =
                PhysicsCalculator.calculateAcceleration(
                        particle, field);

        Vector2D initialPosition =
                particle.getInitialPosition();

        Vector2D initialVelocity =
                particle.getInitialVelocity();

        List<SimulationState> states = new ArrayList<>();

        int steps = (int) Math.ceil(totalTime / timeStep);

        for (int i = 0; i <= steps; i++) {

            double t = Math.min(i * timeStep, totalTime);

            Vector2D position = initialPosition
                    .add(initialVelocity.scale(t))
                    .add(acceleration.scale(0.5 * t * t));

            Vector2D velocity = initialVelocity
                    .add(acceleration.scale(t));

            states.add(new SimulationState(
                    t,
                    position,
                    velocity
            ));

            if (t >= totalTime) {
                break;
            }
        }

        return states;
    }

    private void validateTime(
            double totalTime,
            double timeStep) {

        if (totalTime <= 0 ||
                timeStep <= 0 ||
                !Double.isFinite(totalTime) ||
                !Double.isFinite(timeStep)) {

            throw new IllegalArgumentException(
                    "Time and time step must be positive.");
        }

        if (Math.ceil(totalTime / timeStep) > 20000) {

            throw new IllegalArgumentException(
                    "Too many simulation steps. Increase the time step.");
        }
    }
}
