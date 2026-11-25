// رغد
package com.mycompany.chargedparticlesimulator.physics;

import com.mycompany.chargedparticlesimulator.model.Vector2D;

//Store the particle's state at a specific time
public record SimulationState(
        double time,
        Vector2D position,
        Vector2D velocity) {
}
