package ru.nikitasemiklit.model;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import ru.nikitasemiklit.enums.TYPE_DATA_TO_DRAW;

import java.io.StringReader;
import java.util.Scanner;
import java.util.Vector;

import static org.knowm.xchart.style.lines.SeriesLines.SOLID;
import static ru.nikitasemiklit.model.Constants.COLUMNS_COUNT;
import static ru.nikitasemiklit.model.Constants.SENSORS_COUNT;

public class ChartManager {

    public final Model model;
    public final long timeBegin;
    public final long timeEnd;

    public ChartManager(Model model) {
        this.model = model;
        timeBegin = model.getFirstTime();
        timeEnd = model.getLastTime();
    }

    public XYChart getChart(long timeFrom, long timeTo, String dataToDraw, boolean isSpeedLineToDraw, TYPE_DATA_TO_DRAW draw) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Let's searcher some stickers").xAxisTitle("Time").yAxisTitle("Temperature Rate").build();

        if (timeFrom == 0){
            timeFrom = model.timeLine.firstElement();
        }

        if (timeTo == 0){
            timeTo = model.timeLine.lastElement();
        }

        int size = 0;
        int firstElement = -1;

        for (Long tl : model.timeLine){
            if ((tl >= timeFrom) && (tl <= timeTo)) {
                if (firstElement == -1){
                    firstElement = model.timeLine.indexOf(tl);
                }
                size++;
            }
        }

        if (firstElement == -1){
            firstElement = 0;
        }

        double [] xTime = new double[size];
        for (int i = 0; i < size; i++){
            xTime[i] = model.timeLine.elementAt(firstElement + i);

        }



        if (draw == TYPE_DATA_TO_DRAW.SENSORS){
            Scanner sc = new Scanner(new StringReader(dataToDraw));
            while (sc.hasNextInt()){
                int sensorId = sc.nextInt() - 1;
                addLineToChart(model.temperatureRate, chart, xTime, sensorId, size, firstElement);
            }
        }

        if (draw == TYPE_DATA_TO_DRAW.COLUMNS) {
            Scanner sc = new Scanner(new StringReader(dataToDraw));
            while (sc.hasNextInt()) {
                int columnToDraw = sc.nextInt();
                for (int sensorId = 0; sensorId < SENSORS_COUNT; sensorId++) {
                    if (model.coords[sensorId] == columnToDraw) {
                        addLineToChart(model.temperatureRate, chart, xTime, sensorId, size, firstElement);
                    }
                }
            }
        }

        if (draw == TYPE_DATA_TO_DRAW.LINES){
            Scanner sc = new Scanner(new StringReader(dataToDraw));
            while (sc.hasNextInt()) {
                int lineToDraw = sc.nextInt();
                for (int sensorId = 0; sensorId < SENSORS_COUNT; sensorId++) {
                    if (sensorId / COLUMNS_COUNT == lineToDraw - 1) {
                        addLineToChart(model.temperatureRate, chart, xTime, sensorId, size, firstElement);
                    }
                }
            }
        }

        if (isSpeedLineToDraw) {
            double[] ySpeed = new double[size];
            for (int i = 0; i<size; i++){
                ySpeed[i] = model.speedLine.elementAt(firstElement + i);
            }

            XYSeries series = chart.addSeries("Speed", xTime, ySpeed);
            series.setMarker(SeriesMarkers.NONE);
            series.setYAxisGroup(1);
            chart.setYAxisGroupTitle(1, "Speed");
        }


        return chart;
    }

    private void addLineToChart(Vector<double[]> tempretureRate, XYChart chart, double[] xTime, int sensorId, int size, int firstElement) {
        double[] yData = new double[size];
        for (int i = 0; i<size; i++){
            yData[i] = tempretureRate.elementAt(firstElement + i)[sensorId];
        }
        XYSeries series = chart.addSeries("Sensor " + (sensorId + 1), xTime, yData);
        series.setLineWidth((float) 1);
        series.setLineStyle(SOLID);
        series.setMarker(SeriesMarkers.NONE);
    }
}
