// رغد
package com.mycompany.chargedparticlesimulator.physics;

import com.mycompany.chargedparticlesimulator.model.*;

import java.util.ArrayList;
import java.util.List;

public class AnalyticalSolver {

    //Calculation of the particle's motion using analytical equations
    public List<SimulationState> calculate(
            Particle particle,
            ElectricField field,
            double totalTime,
            double timeStep) {

        validateTime(totalTime, timeStep);

        //Calculate constant acceleration from the electric field 
        Vector2D acceleration =
                PhysicsCalculator.calculateAcceleration(
                        particle, field);

        Vector2D initialPosition =
                particle.getInitialPosition();

        Vector2D initialVelocity =
                particle.getInitialVelocity();

        List<SimulationState> states = new ArrayList<>();

        //Determine the number of the simulation steps
        int steps = (int) Math.ceil(totalTime / timeStep);

        //Calculate position and velocity at each time step
        for (int i = 0; i <= steps; i++) {

            double t = Math.min(i * timeStep, totalTime);

            //Analytical position equation 
            Vector2D position = initialPosition
                    .add(initialVelocity.scale(t))
                    .add(acceleration.scale(0.5 * t * t));

            //Analytical velocity equation
            Vector2D velocity = initialVelocity
                    .add(acceleration.scale(t));

            //Store the calculated state
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

    //Validate the simulation time and limits the number of steps
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
