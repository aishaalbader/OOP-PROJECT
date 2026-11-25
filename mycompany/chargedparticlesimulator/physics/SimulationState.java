// رغد
package com.mycompany.chargedparticlesimulator.physics;

import com.mycompany.chargedparticlesimulator.model.Vector2D;

public record SimulationState(
        double time,
        Vector2D position,
        Vector2D velocity) {
}
