// حصه
package com.mycompany.chargedparticlesimulator.utilities;

public class InputValidator {

    public double parsePositive(
            String text,
            String fieldName,
            double maxValue)
            throws InvalidSimulationInputException {

        try {

            double value = Double.parseDouble(text.trim());

            if (!Double.isFinite(value) || value <= 0) {
                throw new InvalidSimulationInputException(
                        fieldName + " must be a positive number.");
            }

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

    public double parseFinite(
            String text,
            String fieldName,
            double maxValue)
            throws InvalidSimulationInputException {

        try {

            double value = Double.parseDouble(text.trim());

            if (!Double.isFinite(value)) {
                throw new InvalidSimulationInputException(
                        fieldName + " must be a finite number.");
            }

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
    public double parsePositive(
        String text,
        String fieldName)
        throws InvalidSimulationInputException {

    return parsePositive(text, fieldName, Double.MAX_VALUE);
}

public double parseFinite(
        String text,
        String fieldName)
        throws InvalidSimulationInputException {

    return parseFinite(text, fieldName, Double.MAX_VALUE);
}
}
