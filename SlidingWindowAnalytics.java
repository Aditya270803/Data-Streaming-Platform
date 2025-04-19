package Project.StreamingAnalyticsPlatform;



import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


public class SlidingWindowAnalytics extends Application {

    private final Queue<Double> slidingWindow;
    private final PriorityQueue<Double> topNQueue;
    private final int windowSize;
    private final int topN;
    private final RandomDataGenerator dataGenerator;
    private final int upperThreshold;
    private final int lowerThreshold;
    private Clip alarmClip;
    private RealTimeChartApp realTimeChartApp;

    // No-argument constructor required by JavaFX
    public SlidingWindowAnalytics() {
        // Default values
        this.windowSize = 7;
        this.topN = 3;
        this.lowerThreshold = 80;
        this.upperThreshold = 120;
        this.dataGenerator = new RandomDataGenerator(60, 150);
        this.slidingWindow = new LinkedList<>();
        this.topNQueue = new PriorityQueue<>();

        try {
            File alarmFile = new File("C://Users//hp//OneDrive//Desktop//Semester VI//Problem_Solving//Project//StreamingAnalyticsPlatform//alarm.wav"); // Adjust the path as needed
            this.alarmClip = AudioSystem.getClip();
            this.alarmClip.open(AudioSystem.getAudioInputStream(alarmFile));
        } catch (Exception e) {
            System.out.println("Error initializing alarm: " + e.getMessage());
        }
    }

    public void processData() {
        double newValue = dataGenerator.generate();

        // Update sliding window
        slidingWindow.add(newValue);
        if (slidingWindow.size() > windowSize) {
            double oldest = slidingWindow.remove();
            topNQueue.remove(oldest);
        }

        // Update Top-N Queue
        topNQueue.add(newValue);
        if (topNQueue.size() > topN) {
            topNQueue.remove();
        }

        // Calculate moving average
        double movingAverage = calculateMovingAverage();

        // Check thresholds and trigger alarm if necessary
        checkThresholds(movingAverage);

        // Update the chart
        realTimeChartApp.updateChart(newValue, movingAverage);

        // Print results
        printResults(newValue, movingAverage);
    }

    private double calculateMovingAverage() {
        double sum = 0.0;
        for (double val : slidingWindow) {
            sum += val;
        }
        double result = (sum / slidingWindow.size());
        return Math.round(result * 100.0) / 100.0; // Round to 2 decimal places
    }

    private void checkThresholds(double movingAverage) {
        if (movingAverage < lowerThreshold || movingAverage > upperThreshold) {
            playAlarm();
        } else {
            if (alarmClip != null && alarmClip.isRunning()) {
                alarmClip.stop();
            }
        }
    }

    private void playAlarm() {
        try {
            if (alarmClip != null) {
                alarmClip.stop();
                alarmClip.setFramePosition(0);
                alarmClip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing alarm: " + e.getMessage());
        }
    }

    private void printResults(double newValue, double movingAverage) {
        System.out.println("New Data Point: " + newValue);
        System.out.println("Current Sliding Window: " + slidingWindow);
        System.out.println("Moving Average: " + movingAverage);
        System.out.println("Top " + topN + " Values: " + topNQueue);
        System.out.println("-------------------------------");
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize RealTimeChartApp
        realTimeChartApp = new RealTimeChartApp(primaryStage);

        // Use a Timeline to process data and update chart every 2 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> processData()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args); // This launches the JavaFX application
    }
}
