
package com.mycompany.chargedparticlesimulator.data;

import com.mycompany.chargedparticlesimulator.physics.SimulationState;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvFileManager {

    public void saveStates(
            Path file,
            List<SimulationState> states) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            writer.write(
                    "Time (s),X (m),Y (m),Vx (m/s),Vy (m/s)");

            writer.newLine();

            for (SimulationState state : states) {

                writer.write(
                        state.time() + "," +
                        state.position().x() + "," +
                        state.position().y() + "," +
                        state.velocity().x() + "," +
                        state.velocity().y());

                writer.newLine();
            }
        }
    }
}
