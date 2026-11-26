//حصه
package com.mycompany.chargedparticlesimulator.data;

import com.mycompany.chargedparticlesimulator.physics.SimulationState;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
// Saves simulation results into a CSV file
public class CsvFileManager {

    public void saveStates(
            Path file,
            List<SimulationState> states) throws IOException {
// Automatically closes the writer after use
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
// Write the CSV column headers
            writer.write(
                    "Time (s),X (m),Y (m),Vx (m/s),Vy (m/s)");

            writer.newLine();
// Write each simulation state in a separate row
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
