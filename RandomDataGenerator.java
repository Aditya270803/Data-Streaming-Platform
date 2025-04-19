package Project.StreamingAnalyticsPlatform;

import java.util.Random;

public class RandomDataGenerator {
    private final double min; // Minimum value for the range
    private final double max; // Maximum value for the range
    private final Random random; // Random number generator

    public RandomDataGenerator(double min, double max) {
        this.min = min;
        this.max = max;
        this.random = new Random();
    }

    public double generate() {
        // Generate a random value within the range [min, max]
        double val=random.nextDouble(min, max);
        return Math.round(val*100.0)/100.0;
    }
}
