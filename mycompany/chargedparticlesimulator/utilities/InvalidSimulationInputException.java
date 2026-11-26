// حصه
package com.mycompany.chargedparticlesimulator.utilities;
// Custom exception for invalid simulation inputs
public class InvalidSimulationInputException extends Exception {
    // Passes the error message to the parent Exception class
    public InvalidSimulationInputException(String message) {
        super(message);
    }
}
