package ru.nikitasemiklit.model;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;

import static ru.nikitasemiklit.model.Constants.*;

public class RawData {



    private Vector<Long> rawTime = new Vector<>();
    private Vector<Double> rawSpeed = new Vector<>();
    private Vector<Double> rawCastLength = new Vector<>();
    private Vector<double[]> rawTemperature = new Vector<>();

    private RawData(Vector<Long> rawTime, Vector<Double> rawSpeed, Vector<Double> rawCastLength, Vector<double[]> rawTemperature) {
        this.rawTime = rawTime;
        this.rawSpeed = rawSpeed;
        this.rawCastLength = rawCastLength;
        this.rawTemperature = rawTemperature;
    }

    Vector<Long> getRawTime() {
        return rawTime;
    }

    Vector<Double> getRawSpeed() {
        return rawSpeed;
    }

    Vector<Double> getRawCastLength() {
        return rawCastLength;
    }

    Vector<double[]> getRawTemperature() {
        return rawTemperature;
    }

    public static RawData parseFile (File file) throws IOException, ParseException{

        Vector<Long> rawTime = new Vector<>();
        Vector<Double> rawSpeed = new Vector<>();
        Vector<Double> rawCastLength = new Vector<>();
        Vector<double[]> rawTemperature = new Vector<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        bufferedReader.readLine();
        String input;
        while ((input = bufferedReader.readLine()) != null) {
            String[] fields = input.split(";");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.ENGLISH);
            rawTime.add(dateFormat.parse(fields[TIME_COLUMN]).toInstant().getEpochSecond());
            rawCastLength.add(Double.parseDouble(fields[CAST_LENGTH_COLUMN]));
            rawSpeed.add(Double.parseDouble(fields[SPEED_COLUMN]));
            rawTemperature.add(new double[SENSORS_COUNT]);
            for (int i = 0; i < SENSORS_COUNT; i++) {
                if (fields[FIRST_SENSOR_STATUS_COLUMN + i * 2].equals("1")) {
                    rawTemperature.lastElement()[i] = Double.parseDouble(fields[FIRST_SENSOR_DATA_COLUMN + i * 2]);
                } else {
                    if (rawTemperature.size() != 1) {
                        rawTemperature.lastElement()[i] = rawTemperature.elementAt(rawTemperature.indexOf(rawTemperature.lastElement()) - 1)[i];
                    } else {
                        rawTemperature.lastElement()[i] = .0;
                    }
                }
            }
        }

        bufferedReader.close();

        return new RawData(rawTime, rawSpeed, rawCastLength, rawTemperature);

    }

}
