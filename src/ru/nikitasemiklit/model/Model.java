package ru.nikitasemiklit.model;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import ru.nikitasemiklit.enums.STATUS_ABORTING_ALARM;
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

    private final Vector<StickerInColumn[][]> abnormalTCDetectedMatrix = new Vector<>();

    private final Vector<short[]> temperatureRiseDetected = new Vector<>();
    private final Vector<short[]> temperatureFallDetected = new Vector<>();
    //private final Vector<RisingSensor> risingSensors = new Vector<>();
    //private final Vector<FallingSensor> fallingSensors = new Vector<>();
    //private final Vector<int[]> abnormalTCDetected = new Vector<>();

    private long lastDetectedAlarmTime = 0;

    private Model(Vector<Long> timeLine, Vector<Double> speedLine, Vector<Double> speedRate , Vector<Double> castLengthLine, Vector<double[]> temperatureLine, Vector<double[]> temperatureRate) {
        this.timeLine = timeLine;
        this.speedLine = speedLine;
        this.speedRate = speedRate;
        this.castLengthLine = castLengthLine;
        this.temperatureLine = temperatureLine;
        this.temperatureRate = temperatureRate;
    }

    public void smooth (double alpha){
        for (int i = 0; i<SENSORS_COUNT; i++) {
            for (int j = 1; j < temperatureRate.size(); j++) {
                temperatureRate.elementAt(j)[i] = temperatureRate.elementAt(j)[i] * alpha + temperatureRate.elementAt(j-1)[i] * (1 - alpha);
            }
        }
    }

    public int countCriticalSensors (ModelParameters modelParameters, boolean showResults) {

        //risingSensors.clear();
        //fallingSensors.clear();
        temperatureRiseDetected.clear();
        temperatureFallDetected.clear();
        //abnormalTCDetected.clear();
        abnormalTCDetectedMatrix.clear();

        lastDetectedAlarmTime = 0;

        temperatureRate.forEach(el -> {
            temperatureRiseDetected.add(new short[SENSORS_COUNT]);
            temperatureFallDetected.add(new short[SENSORS_COUNT]);
            //abnormalTCDetected.add(new int[COLUMNS_COUNT]);
            abnormalTCDetectedMatrix.add(new StickerInColumn[COLUMNS_COUNT][LINES_COUNT]);
        });

        for (int i = 0; i < SENSORS_COUNT; i++) {

            long firstTimeRise = 0;
            long lastTimeRise = 0;
            long firstTimeFall = 0;
            long lastTimeFall = 0;

            for (double[] el : temperatureRate) {
                //ищем повышение температуры для каждого датчика
                if (el[i] > modelParameters.getMinimumRisingRate()){
                    if (el[i] < modelParameters.getMaximumRisingRate()) {
                        //если начало повышения не записано, и температура ближе к минимуму, чем к максимуму
                        if ((firstTimeRise == 0) && (el[i] - modelParameters.getMinimumRisingRate() < modelParameters.getMaximumRisingRate() - el[i])){
                            firstTimeRise = timeLine.elementAt(temperatureRate.indexOf(el));
                        }
                        lastTimeRise = timeLine.elementAt(temperatureRate.indexOf(el));
                    } else{
                        firstTimeRise = 0;
                        lastTimeRise = 0;
                    }
                } else {
                    if (lastTimeRise != 0 && firstTimeRise != 0) {
                        if ((lastTimeRise - firstTimeRise > modelParameters.getMinimumDurationTempRise()) && (lastTimeRise - firstTimeRise < modelParameters.getMaximumDurationTempRise())) {
                            //risingSensors.add(new RisingSensor(i, firstTimeRise, lastTimeRise));
                            //если повышение длилось необходимое время, записываем время его начала
                            temperatureRiseDetected.elementAt(timeLine.indexOf(firstTimeRise))[i] = 1;
                        }
                        firstTimeRise = 0;
                        lastTimeRise = 0;
                    }
                }

                //ищем понижение температуры для каждого датчика
                if (el[i] < modelParameters.getMinimumFallingRate()){
                    if (el[i] > modelParameters.getMaximumFallingRate()) {
                        if ((firstTimeFall == 0) && (el[i] - modelParameters.getMinimumFallingRate() > modelParameters.getMaximumFallingRate() - el[i])) {
                            firstTimeFall = timeLine.elementAt(temperatureRate.indexOf(el));
                        }
                        lastTimeFall = timeLine.elementAt(temperatureRate.indexOf(el));
                    }
                    else {
                        firstTimeFall = 0;
                        lastTimeFall = 0;
                    }
                } else {
                    if (lastTimeFall != 0 && firstTimeFall != 0) {
                        if (lastTimeFall - firstTimeFall > modelParameters.getMinimumDurationTempFall()) {
                            //fallingSensors.add(new FallingSensor(i, firstTimeFall, lastTimeFall));
                            //если понижение длилось необзодимое время, записываем его
                            temperatureFallDetected.elementAt(timeLine.indexOf(firstTimeFall))[i] = 1;
                        }
                        firstTimeFall = 0;
                        lastTimeFall = 0;
                    }
                }

            }
        }

      //  StringBuilder resultStringBuilder = new StringBuilder();

      //  risingSensors.forEach(risingSensor -> resultStringBuilder.append(risingSensor.toString() + "\n"));

      //  fallingSensors.forEach(fallingSensor -> resultStringBuilder.append(fallingSensor.toString() + "\n"));
      //  if (showResults) {
      //      ResultPane rs = new ResultPane("Output", resultStringBuilder.toString());
      //      rs.pack();
      //      rs.setVisible(true);
      //  }
        //вычисление скоростей стикеров
        for (int i = 0; i < SENSORS_COUNT; i++) {
            for (short[] el : temperatureRiseDetected) {
                //если датчик сработал
                //if (el[i] == 1) {
                    int currentIndex = temperatureRiseDetected.indexOf(el);
                    long latestTime = timeLine.elementAt(currentIndex) - modelParameters.getAbnormalInterval();

                    StickerInColumn stickerInColumn = new StickerInColumn(timeLine.elementAt(currentIndex));

                    //подсчет количества сработавших датчиков в предшествующий период
                    getFirstTimeOfSticker(i, currentIndex, latestTime, stickerInColumn);
                    int sensorsCount = stickerInColumn.getSensorsWithStickersCount();
                    //если скорость распротранения удовлетворяет условию
                    if (sensorsCount != 0 ) {
                        if (stickerInColumn.getFirstTimeOfSticker() != timeLine.elementAt(currentIndex)) {
                            double castSpeed = speedLine.elementAt(currentIndex);
                            double stickerSpeed = 60 * (sensorsCount + 1) * DISTANCE_BETWEEN_SENSORS / (timeLine.elementAt(currentIndex) - stickerInColumn.getFirstTimeOfSticker());
                            if (modelParameters.getMinimumRatioStickerSpeedToCastingSpeed() * castSpeed <= stickerSpeed && modelParameters.getMaximumRatioStickerSpeedToCastingSpeed() * castSpeed >= stickerSpeed) {
                                //проверяем, падала ли температура
                                boolean temperatureFall = false;
                                for (int j = timeLine.indexOf(stickerInColumn.getFirstTimeOfSticker()); j < currentIndex; j++) {
                                    if (temperatureFallDetected.elementAt(j)[stickerInColumn.getFirstSensorWithSticker()] == 1) {
                                        temperatureFall = true;
                                    }
                                }
                                if (temperatureFall) {
                                    //все условия выполнены, записываем сработавшие датчики
                                    //abnormalTCDetected.elementAt(currentIndex)[coords[i] - 1] += sensorsCount;
                                    abnormalTCDetectedMatrix.elementAt(currentIndex) [coords[i] - 1][i / COLUMNS_COUNT] = stickerInColumn;
                                }
                            }
                        }
                    }
                    //if (abnormalTCDetectedMatrix.elementAt(currentIndex) [coords[i] - 1][i / COLUMNS_COUNT] == null){
                    //    abnormalTCDetectedMatrix.elementAt(currentIndex) [coords[i] - 1][i / COLUMNS_COUNT] = new StickerInColumn();
                    //}
                //}
            }
        }

        StringBuilder algorithmResult = new StringBuilder();

        //for (int[] el : abnormalTCDetected){
            //for (int i = 0; i < el.length - 2; i++){
        for (StickerInColumn[][] el : abnormalTCDetectedMatrix){
            for (int line = 1; line < LINES_COUNT; line++){
                for (int column = 1; column < COLUMNS_COUNT - 1; column = getNextColumn(column)){
                    if (el[column][line] != null) {
                        int sensorsInCurrentColumn = el[column][line].getSensorsWithStickersCount();
                        int sensorsInSideColumns = 0;
                        if (el[getPreviousColumn(column)][line - 1] != null){
                            sensorsInSideColumns += el[getPreviousColumn(column)][line - 1].getSensorsWithStickersCount();
                        }
                        if (el[getNextColumn(column)][line - 1] != null){
                            sensorsInSideColumns += el[getNextColumn(column)][line - 1].getSensorsWithStickersCount();
                        }
                        if ((sensorsInCurrentColumn >= 2) && (sensorsInSideColumns >= 2)) {
                            //Проверяем условия отмены
                            STATUS_ABORTING_ALARM isAlarmPossible = checkAlarmPossibility(abnormalTCDetectedMatrix.indexOf(el), modelParameters);
                            int totalSensorsCount = sensorsInCurrentColumn + sensorsInSideColumns;
                            if (totalSensorsCount >= modelParameters.getAbnormalStickerAlarm()) {
                                if (isAlarmPossible == STATUS_ABORTING_ALARM.POSSIBLE) {
                                    lastDetectedAlarmTime = timeLine.elementAt(abnormalTCDetectedMatrix.indexOf(el));
                                    algorithmResult.append("Alarm at " + timeLine.elementAt(abnormalTCDetectedMatrix.indexOf(el)) + " in " + (column + 1) + " with " +
                                            totalSensorsCount + " sensors: " + el[column][line].getSensorsWithSticker() + "\n");
                                    if (!showResults) {
                                        return 1;
                                    }
                                } else {
                                    algorithmResult.append("Aborted alarm at " + timeLine.elementAt(abnormalTCDetectedMatrix.indexOf(el)) + " in " + (column + 1) + " because of ");
                                    switch (isAlarmPossible) {
                                        case SPEED:
                                            algorithmResult.append("too slow speed");
                                            break;
                                        case ACCELERATION:
                                            algorithmResult.append("too fast acceleration");
                                            break;
                                        case LENGTH:
                                            algorithmResult.append("too little cast length");
                                            break;
                                        case PREVIOUS_ALARM:
                                            algorithmResult.append("previous alarm");
                                    }
                                    algorithmResult.append(" with " +
                                            totalSensorsCount + " sensors: " + el[column][line].getSensorsWithSticker() + "\n");
                                }
                            } else {
                                if (totalSensorsCount >= modelParameters.getAbnormalStickerWarning()) {
                                    algorithmResult.append("Warning at " + timeLine.elementAt(abnormalTCDetectedMatrix.indexOf(el)) + " in " + (column + 1) + " with " +
                                            totalSensorsCount + " sensors: " + el[column][line].getSensorsWithSticker() + "\n");
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showResults) {
            ResultPane algorithmResultPane = new ResultPane("Output", algorithmResult.toString());
            algorithmResultPane.pack();
            algorithmResultPane.setVisible(true);
        }

        return 0;
    }

    private int getNextColumn (int currentColumn){
        switch (currentColumn){
            case 1: return 3;
            case 9: return 11;
            case 17: return 19;
            case 25: return 27;
        }
        return ++currentColumn;
    }

    private int getPreviousColumn (int currentColumn){
        switch (currentColumn){
            case 3: return 1;
            case 11: return 9;
            case 19: return 17;
            case 27: return 25;
        }
        return --currentColumn;
    }

    private STATUS_ABORTING_ALARM checkAlarmPossibility (int index, ModelParameters modelParameters){

        if (castLengthLine.elementAt(index) < modelParameters.getMinimumCastLength()) {
            return STATUS_ABORTING_ALARM.LENGTH;
        }

        if ((lastDetectedAlarmTime > timeLine.elementAt(index) - modelParameters.getMinimumTimeSinceLastAlarmDetected())&&(lastDetectedAlarmTime != timeLine.elementAt(index))){
            return STATUS_ABORTING_ALARM.PREVIOUS_ALARM;
        }

        int currentIndex = index;
        while (currentIndex >= 0 && timeLine.elementAt(currentIndex) >= timeLine.elementAt(index) - modelParameters.getTimeForAlarmChecking()){

            if (speedLine.elementAt(currentIndex) < modelParameters.getMinimumSpeed()){
                return STATUS_ABORTING_ALARM.SPEED;

            }
            if (speedRate.elementAt(currentIndex) > modelParameters.getMaximumSpeedRate()){
                return STATUS_ABORTING_ALARM.ACCELERATION;
            }
            currentIndex--;
        }
        return STATUS_ABORTING_ALARM.POSSIBLE;
    }

    private void getFirstTimeOfSticker (int sensorId, int currentTimeIndex ,long latestTime, StickerInColumn stickerInColumn){
        for (int i = currentTimeIndex; i>=0; i--) {
            if (timeLine.elementAt(i) < latestTime){
                return;
            }
            if (temperatureRiseDetected.elementAt(i)[sensorId] == 1) {
                stickerInColumn.setFirstTimeOfSticker(timeLine.elementAt(i));
                stickerInColumn.incSensorsWithStickersCount();
                stickerInColumn.addSensorsWithSticker(sensorId);
                int previousSensor = getPreviousSensorInColumn(sensorId);
                if (previousSensor != -1) {
                    getFirstTimeOfSticker(previousSensor, i, latestTime, stickerInColumn);
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
        int firstElement = -1;

        for (Long tl : timeLine){
            if ((tl >= timeFrom) && (tl <= timeTo)) {
                if (firstElement == -1){
                    firstElement = timeLine.indexOf(tl);
                }
                size++;
            }
        }

        if (firstElement == -1){
            firstElement = 0;
        }

        double [] xTime = new double[size];
        for (int i = 0; i < size; i++){
            xTime[i] = timeLine.elementAt(firstElement + i);

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
                ySpeed[i] = speedLine.elementAt(firstElement + i);
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
            //j = l;
            j++;
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




}
