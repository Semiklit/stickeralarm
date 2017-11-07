package ru.nikitasemiklit.model;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import ru.nikitasemiklit.gui.ResultPane;
import ru.nikitasemiklit.enums.TYPE_DATA_TO_DRAW;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

import static org.knowm.xchart.style.lines.SeriesLines.SOLID;
import static ru.nikitasemiklit.model.Constants.*;

public class Model {

    private static final int[] coords = new int[SENSORS_COUNT];

    private final Vector<Long> timeLine;
    private final Vector<Double> speedLine;
    private final Vector<Double> speedRate;
    private final Vector<Double> castLengthLine;
    private final Vector<double[]> temperatureLine;
    private final Vector<double[]> temperatureRate;

    private Vector<short[]> temperatureRiseDetected = new Vector<>();
    private Vector<short[]> temperatureFallDetected = new Vector<>();
    private Vector<RisingSensor> risingSensors = new Vector<>();
    private Vector<FallingSensor> fallingSensors = new Vector<>();
    private Vector<int[]> abnormalTCDeleted = new Vector<>();

    private long lastDetectedAlarmTime = 0;

    private Model(Vector<Long> timeLine, Vector<Double> speedLine, Vector<Double> speedRate , Vector<Double> castLengthLine, Vector<double[]> temperatureLine, Vector<double[]> temperatureRate) {
        this.timeLine = timeLine;
        this.speedLine = speedLine;
        this.speedRate = speedRate;
        this.castLengthLine = castLengthLine;
        this.temperatureLine = temperatureLine;
        this.temperatureRate = temperatureRate;
    }

    public void countCriticalSensors (ModelParameters modelParameters) {

        risingSensors.clear();
        fallingSensors.clear();
        temperatureRiseDetected.clear();
        temperatureFallDetected.clear();
        abnormalTCDeleted.clear();

        temperatureRate.forEach(el -> {
            temperatureRiseDetected.add(new short[SENSORS_COUNT]);
            temperatureFallDetected.add(new short[SENSORS_COUNT]);
            abnormalTCDeleted.add(new int[COLUMNS_COUNT]);
        });

        for (int i = 0; i < SENSORS_COUNT; i++) {

            long firstTimeRise = 0;
            long lastTimeRise = 0;
            long firstTimeFall = 0;
            long lastTimeFall = 0;

            for (double[] el : temperatureRate) {
                //повышение температуры
                if ((el[i] > modelParameters.getMinimumRisingRate() && el[i] < modelParameters.getMaximumRisingRate())) {
                    if (firstTimeRise == 0) {
                        firstTimeRise = timeLine.elementAt(temperatureRate.indexOf(el));
                    }
                    lastTimeRise = timeLine.elementAt(temperatureRate.indexOf(el));
                } else {
                    if (lastTimeRise != 0) {
                        if ((lastTimeRise - firstTimeRise > modelParameters.getMinimumDurationTempRise()) && (lastTimeRise - firstTimeRise < modelParameters.getMaximumDurationTempRise())) {
                            risingSensors.add(new RisingSensor(i, firstTimeRise, lastTimeRise));
                            temperatureRiseDetected.elementAt(timeLine.indexOf(firstTimeRise))[i] = 1;
                        }
                        firstTimeRise = 0;
                        lastTimeRise = 0;
                    }
                }

                //понижение температуры
                if ((el[i] > modelParameters.getMaximumFallingRate() && el[i] < modelParameters.getMinimumFallingRate())) {
                    if (firstTimeFall == 0) {
                        firstTimeFall = timeLine.elementAt(temperatureRate.indexOf(el));
                    }
                    lastTimeFall = timeLine.elementAt(temperatureRate.indexOf(el));
                } else {
                    if (lastTimeFall != 0) {
                        if (lastTimeFall - firstTimeFall > modelParameters.getMinimumDurationTempFall()) {
                            fallingSensors.add(new FallingSensor(i, firstTimeFall, lastTimeFall));
                            temperatureFallDetected.elementAt(timeLine.indexOf(firstTimeFall))[i] = 1;
                        }
                        firstTimeFall = 0;
                        lastTimeFall = 0;
                    }
                }

            }
            if (lastTimeRise != 0) {
                if ((lastTimeRise - firstTimeRise > modelParameters.getMinimumDurationTempRise()) && (lastTimeRise - firstTimeRise < modelParameters.getMaximumDurationTempRise())) {
                    risingSensors.add(new RisingSensor(i, firstTimeRise, lastTimeRise));
                }
            }
            if (lastTimeFall != 0) {
                if (lastTimeFall - firstTimeFall > modelParameters.getMinimumDurationTempFall()) {
                    fallingSensors.add(new FallingSensor(i, firstTimeFall, lastTimeFall));
                }
            }

        }

        StringBuilder resultStringBuilder = new StringBuilder();

        risingSensors.forEach(risingSensor -> resultStringBuilder.append(risingSensor.toString() + "\n"));

        fallingSensors.forEach(fallingSensor -> resultStringBuilder.append(fallingSensor.toString() + "\n"));

        ResultPane rs = new ResultPane("Output", resultStringBuilder.toString());
        rs.pack();
        rs.setVisible(true);


        //вычисление скоростей стикеров
        for (int i = 0; i < SENSORS_COUNT; i++) {
            for (short[] el : temperatureRiseDetected) {
                //если датчик сработал
                if (el[i] == 1) {
                    int currentIndex = temperatureRiseDetected.indexOf(el);
                    long latestTime = timeLine.elementAt(currentIndex) - modelParameters.getAbnormalInterval();

                    StickerInfo stickerInfo = new StickerInfo(timeLine.elementAt(currentIndex));

                    //подсчет количества сработавших датчиков в предшествующий период
                    getFirstTimeOfSticker(i, currentIndex, latestTime, stickerInfo);
                    int sensorsCount = stickerInfo.getSensorsWithStickersCount() + 1;
                    //если скорость распротранения удовлетворяет условию
                    if (stickerInfo.getFirstTimeOfSticker() != timeLine.elementAt(currentIndex)) {
                        double castSpeed = speedLine.elementAt(currentIndex);
                        double stickerSpeed = 60 * sensorsCount * DISTANCE_BETWEEN_SENSORS / (timeLine.elementAt(currentIndex) - stickerInfo.getFirstTimeOfSticker());
                        if (modelParameters.getMinimumRatioStickerSpeedToCastingSpeed() * castSpeed <= stickerSpeed && modelParameters.getMaximumRatioStickerSpeedToCastingSpeed() * castSpeed >= stickerSpeed) {
                            //проверяем, падала ли температура
                            boolean temperatureFall = false;
                            for (int j = timeLine.indexOf(stickerInfo.getFirstTimeOfSticker()); j < currentIndex; j++) {
                                if (temperatureFallDetected.elementAt(j)[i - sensorsCount] == 1) {
                                    temperatureFall = true;
                                }
                            }
                            if (temperatureFall) {
                                //все условия выполнены, записываем сработавшие датчики
                                abnormalTCDeleted.elementAt(currentIndex)[coords[i] - 1] += sensorsCount;
                            }
                        }
                    }
                }
            }
        }

        /*StringBuilder abnormalTCDetectedString = new StringBuilder();

        for (int[] el : abnormalTCDeleted){
            for (int i = 0; i < el.length; i++){
                abnormalTCDetectedString.append( el[i] + " ");
            }
            abnormalTCDetectedString.append("\n");
        }

        ResultPane abnormalTCDetetedPane = new ResultPane("Вывод", abnormalTCDetectedString.toString());
        abnormalTCDetetedPane.pack();
        abnormalTCDetetedPane.setVisible(true);*/

        StringBuilder algorithmResult = new StringBuilder();

        for (int[] el : abnormalTCDeleted){
            for (int i = 0; i < el.length - 2; i++){
                if ((el [i + 1] >= 2) && (el[i] + el[i + 2] >= 1)){
                    //Проверяем условия отмены
                    boolean isAlarmPossible = checkAlarmPossibility(abnormalTCDeleted.indexOf(el), modelParameters);

                    if (el [i] + el[i + 1] + el[i + 2] >= modelParameters.getAbnormalStickerAlarm() && isAlarmPossible){
                        lastDetectedAlarmTime = timeLine.elementAt(abnormalTCDeleted.indexOf(el));
                        algorithmResult.append("Sticker Alarm at " + timeLine.elementAt(abnormalTCDeleted.indexOf(el)) + " in " + coords[i + 1] + "\n");

                    } else {
                        if (el [i] + el[i + 1] + el[i + 2] >= modelParameters.getAbnormalStickerWarning()){
                            algorithmResult.append("Sticker Warning at " + timeLine.elementAt(abnormalTCDeleted.indexOf(el)) + " in " + coords[i + 1] + "\n");
                        }
                    }
                }
            }
        }

        ResultPane algorithmResultPane = new ResultPane("Output", algorithmResult.toString());
        algorithmResultPane.pack();
        algorithmResultPane.setVisible(true);

    }

    private boolean checkAlarmPossibility (int index, ModelParameters modelParameters){

        if (castLengthLine.elementAt(index) < modelParameters.getMinimumCastLentgh()) {
            return false;
        }

        if (lastDetectedAlarmTime > timeLine.elementAt(index) - modelParameters.getMinimumTimeSinceLastAlarmDetected()){
            return false;
        }

        int currentIndex = index;
        while (currentIndex >= 0 && timeLine.elementAt(currentIndex) >= timeLine.elementAt(index) - modelParameters.getTimeForAlarmChecking()){

            if (speedLine.elementAt(currentIndex) < modelParameters.getMinimumSpeed()){
                return false;

            }
            if (speedRate.elementAt(currentIndex) > modelParameters.getMaximumSpeedRate()){
                return false;
            }
            currentIndex--;
        }
        return true;
    }

    private void getFirstTimeOfSticker (int sensorId, int currentTimeIndex ,long latestTime, StickerInfo stickerInfo){
        for (int i = currentTimeIndex; i>=0; i--) {
            if (timeLine.elementAt(i) < latestTime){
                return;
            }
            if (temperatureRiseDetected.elementAt(i)[sensorId] == 1) {
                stickerInfo.setFirstTimeOfSticker(timeLine.elementAt(i));
                stickerInfo.incSensorsWithStickersCount();
                int previousSensor = getPreviousSensorInColumn(sensorId);
                if (previousSensor != -1) {
                    getFirstTimeOfSticker(previousSensor, i, latestTime, stickerInfo);
                }
                return;
            }
        }
    }

    private int getPreviousSensorInColumn (int sensorId){
        if (sensorId == 0) {return -1;}
        int currentColumn = coords[sensorId];
        for (int i = sensorId-1; i>=0; i--){
            if (coords[i] == currentColumn){
                return i;
            }
        }
        return -1;
    }

    public XYChart getChart(long timeFrom, long timeTo, String dataToDraw, boolean isSpeedLineToDraw, TYPE_DATA_TO_DRAW draw) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Let's searcher some stickers").xAxisTitle("Time").yAxisTitle("Temperature Rate").build();

        if (timeFrom == 0){
            timeFrom = timeLine.firstElement();
        }

        if (timeTo == 0){
            timeTo = timeLine.lastElement();
        }

        int size = 0;
        int firstElement = 0;

        for (Long tl : timeLine){
            if ((tl >= timeFrom) && (tl <= timeTo)) {
                if (firstElement == 0){
                    firstElement = timeLine.indexOf(tl);
                }
                size++;
            }
        }

        double [] xTime = new double[size];
        for (int i = 0; i<size; i++){
            xTime[i] = timeLine.elementAt(firstElement + i - 1);
        }



        if (draw == TYPE_DATA_TO_DRAW.SENSORS){
            Scanner sc = new Scanner(new StringReader(dataToDraw));
            while (sc.hasNextInt()){
                int sensorId = sc.nextInt() - 1;
                addLineToChart(temperatureRate, chart, xTime, sensorId, size, firstElement);
            }
        }

        if (draw == TYPE_DATA_TO_DRAW.COLUMNS) {
            Scanner sc = new Scanner(new StringReader(dataToDraw));
            while (sc.hasNextInt()) {
                int columnToDraw = sc.nextInt();
                for (int sensorId = 0; sensorId < SENSORS_COUNT; sensorId++) {
                    if (coords[sensorId] == columnToDraw) {
                        addLineToChart(temperatureRate, chart, xTime, sensorId, size, firstElement);
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
                        addLineToChart(temperatureRate, chart, xTime, sensorId, size, firstElement);
                    }
                }
            }
        }

        if (isSpeedLineToDraw) {
            double[] ySpeed = new double[size];
            for (int i = 0; i<size; i++){
                ySpeed[i] = speedLine.elementAt(firstElement + i - 1);
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
            yData[i] = tempretureRate.elementAt(firstElement + i - 1)[sensorId];
        }
        XYSeries series = chart.addSeries("Sensor " + (sensorId + 1), xTime, yData);
        series.setLineWidth((float) 1);
        series.setLineStyle(SOLID);
        series.setMarker(SeriesMarkers.NONE);
    }

    public static Model countTempRate (RawData rawData, int intervalForCalculatingSec){

        final Vector<Long> timeLine = new Vector<>();
        final Vector<Double> speedLine = new Vector<>();
        final Vector<Double> speedRate = new Vector<>();
        final Vector<double[]> temperatureRate = new Vector<>();
        final Vector<double[]> temperatureLine = new Vector<>();
        final Vector<Double> castLengthLine = new Vector<>();


        final Vector<Long> rawTime = rawData.getRawTime();
        final Vector<Double> rawSpeed = rawData.getRawSpeed();
        final Vector<Double> rawCastLength = rawData.getRawCastLength();
        final Vector<double[]> rawTemperature = rawData.getRawTemperature();

        int l,j = 0;
        l = findTimeIndex(rawTime, j, intervalForCalculatingSec);
        while (l != -1)
        {
            timeLine.add(rawTime.elementAt(l));
            speedLine.add(rawSpeed.elementAt(l));
            temperatureLine.add(rawTemperature.elementAt(l));
            castLengthLine.add(rawCastLength.elementAt(l));
            speedRate.add((rawSpeed.elementAt(l) - rawSpeed.elementAt(j)) / (rawTime.elementAt(l) - rawTime.elementAt(j)));

            temperatureRate.add(new double[SENSORS_COUNT]);
            for (int i = 0; i < SENSORS_COUNT; i++) {
                temperatureRate.lastElement()[i] = (rawTemperature.elementAt(l)[i] - rawTemperature.elementAt(j)[i]) / (rawTime.elementAt(l) - rawTime.elementAt(j));
            }
            j = l;
            l = findTimeIndex(rawTime, j, intervalForCalculatingSec);
        }


        try {
            Scanner scanner = new Scanner(new File(COORDS_FILE));
            int i = 0;
            while (scanner.hasNextInt()) {
                coords[i++] = scanner.nextInt();
            }

        } catch (FileNotFoundException ex) {
            System.out.println("File with sensors coordinates not found");
        }

        return new Model(timeLine, speedLine, speedRate, castLengthLine, temperatureLine, temperatureRate);
    }

    private static int findTimeIndex(Vector<Long> time, int j, int intervalForCalculatingSec){
        for (int i = j + 1; i<time.size() - 1; i++){
            if (time.elementAt(i) - time.elementAt(j) >= intervalForCalculatingSec){
                return i;
            }
        }
        return -1;
    }

    public Long getFirstTime (){
        return timeLine.firstElement();
    }

    public Long getLastTime (){
        return timeLine.lastElement();
    }

    private class StickerInfo{
        private long firstTimeOfSticker;
        private int sensorsWithStickersCount;

        StickerInfo(long firstTimeOfSticker) {
            this.firstTimeOfSticker = firstTimeOfSticker;
            this.sensorsWithStickersCount = 0;
        }

        long getFirstTimeOfSticker() {
            return firstTimeOfSticker;
        }

        void setFirstTimeOfSticker(long firstTimeOfSticker) {
            this.firstTimeOfSticker = firstTimeOfSticker;
        }

        int getSensorsWithStickersCount() {
            return sensorsWithStickersCount;
        }

        void incSensorsWithStickersCount() {
            this.sensorsWithStickersCount++;
        }
    }

}
