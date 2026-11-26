// حصه
package com.mycompany.chargedparticlesimulator.utilities;

public class InputValidator {
    // Validates values that must be positive
    public double parsePositive(
            String text,
            String fieldName,
            double maxValue)
            throws InvalidSimulationInputException {

        try {
            double value = Double.parseDouble(text.trim());
            // Reject non-finite and non-positive values
            if (!Double.isFinite(value) || value <= 0) {
                throw new InvalidSimulationInputException(
                        fieldName + " must be a positive number.");
            }
            // Check the maximum allowed value
            if (value > maxValue) {
                throw new InvalidSimulationInputException(
                        fieldName + " cannot exceed " + maxValue + ".");
            }

            return value;

        } catch (NumberFormatException ex) {

            throw new InvalidSimulationInputException(
                    fieldName + " must be a valid number.");
        }
    }
    // Validates values that can be positive, negative, or zero
    public double parseFinite(
            String text,
            String fieldName,
            double maxValue)
            throws InvalidSimulationInputException {

        try {
            double value = Double.parseDouble(text.trim());
            // Reject infinite and NaN values
            if (!Double.isFinite(value)) {
                throw new InvalidSimulationInputException(
                        fieldName + " must be a finite number.");
            }
            // Check the absolute value against the maximum limit
            if (Math.abs(value) > maxValue) {
                throw new InvalidSimulationInputException(
                        fieldName + " cannot exceed " + maxValue + ".");
            }

            return value;

        } catch (NumberFormatException ex) {

            throw new InvalidSimulationInputException(
                    fieldName + " must be a valid number.");
        }
    }
    // Uses the default maximum value
    public double parsePositive(
        String text,
        String fieldName)
        throws InvalidSimulationInputException {

    return parsePositive(text, fieldName, Double.MAX_VALUE);
}
    // Uses the default maximum value
    public double parseFinite(
        String text,
        String fieldName)
        throws InvalidSimulationInputException {

    return parseFinite(text, fieldName, Double.MAX_VALUE);
    }
}
