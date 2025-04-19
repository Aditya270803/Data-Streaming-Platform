package Project.StreamingAnalyticsPlatform;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RealTimeChartApp {

    private final XYChart.Series<Number, Number> dataSeries;   // Series for live data
    private final XYChart.Series<Number, Number> avgSeries;    // Series for moving average
    private final Label infoLabel;                             // Label for displaying current average and value
    private int timeCounter = 0;                         // Tracks time for X-axis

    public RealTimeChartApp(Stage stage) {
        // Create X and Y axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        yAxis.setLabel("Value");

        // Create LineChart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Real-Time Analytics Visualization");

        // Initialize series for data and moving average
        dataSeries = new XYChart.Series<>();
        dataSeries.setName("Data Points");
        avgSeries = new XYChart.Series<>();
        avgSeries.setName("Moving Average");
        lineChart.getData().addAll(dataSeries, avgSeries);

        // Create an info label to display current values
        infoLabel = new Label("Current Average: 0.0 | Latest Value: 0.0");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-padding: 5;");

        // Add chart and label to the layout
        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(lineChart, infoLabel);

        // Position the label at the bottom of the chart
        AnchorPane.setBottomAnchor(infoLabel, 10.0);  // Move to bottom
        AnchorPane.setLeftAnchor(infoLabel, 320.0);  // Center horizontally (adjust as needed)

        AnchorPane.setTopAnchor(lineChart, 10.0);
        AnchorPane.setBottomAnchor(lineChart, 50.0);
        AnchorPane.setLeftAnchor(lineChart, 10.0);
        AnchorPane.setRightAnchor(lineChart, 10.0);

//                 Scene layout
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Real-Time Chart App");
        stage.show();
    }

    public void updateChart(double latestValue, double movingAverage) {
        Platform.runLater(() -> {
            // Add data points to the chart
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(timeCounter, latestValue);
            dataSeries.getData().add(dataPoint);

            XYChart.Data<Number, Number> avgPoint = new XYChart.Data<>(timeCounter, movingAverage);
            avgSeries.getData().add(avgPoint);

            // Add data point labels
            addDataLabel(dataPoint);
            addDataLabel(avgPoint);

            // Update the info label
            infoLabel.setText("Current Average: " + movingAverage + " | Latest Value: " + latestValue);

            // Increment time counter
            timeCounter++;

            // Limit displayed points to avoid clutter
            if (dataSeries.getData().size() > 50) {
                dataSeries.getData().remove(0);
            }
            if (avgSeries.getData().size() > 50) {
                avgSeries.getData().remove(0);
            }
        });
    }

    private void addDataLabel(XYChart.Data<Number, Number> dataPoint) {
        // Add a label to the data point
        Label label = new Label(String.valueOf(dataPoint.getYValue()));
        label.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-padding: 2;");
        dataPoint.setNode(label);
    }
}