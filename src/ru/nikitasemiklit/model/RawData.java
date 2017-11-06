package ru.nikitasemiklit.model;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;

public class RawData {

    private static final int SENSORS_COUNT = 504;

    private Vector<Long> rawTime = new Vector<>();
    private Vector<Double> rawSpeed = new Vector<>();
    private Vector<Double> rawCastLength = new Vector<>();
    private Vector<double[]> rawTempreture = new Vector<>();

    private RawData(Vector<Long> rawTime, Vector<Double> rawSpeed, Vector<Double> rawCastLength, Vector<double[]> rawTempreture) {
        this.rawTime = rawTime;
        this.rawSpeed = rawSpeed;
        this.rawCastLength = rawCastLength;
        this.rawTempreture = rawTempreture;
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

    Vector<double[]> getRawTempreture() {
        return rawTempreture;
    }

    public static RawData parseFile (File file) throws IOException, ParseException{

        Vector<Long> rawTime = new Vector<>();
        Vector<Double> rawSpeed = new Vector<>();
        Vector<Double> rawCastLength = new Vector<>();
        Vector<double[]> rawTempreture = new Vector<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        bufferedReader.readLine();
        String input;
        while ((input = bufferedReader.readLine()) != null) {
            String[] fields = input.split(";");
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.ENGLISH);
            rawTime.add(dateFormat.parse(fields[0]).toInstant().getEpochSecond());
            rawCastLength.add(Double.parseDouble(fields[1]));
            rawSpeed.add(Double.parseDouble(fields[2]));
            rawTempreture.add(new double[SENSORS_COUNT]);
            for (int i = 0; i < SENSORS_COUNT; i++) {
                if (fields[48 + i * 2].equals("1")) {
                    rawTempreture.lastElement()[i] = Double.parseDouble(fields[47 + i * 2]);
                } else {
                    if (rawTempreture.size() != 1){
                        rawTempreture.lastElement()[i] = rawTempreture.elementAt(rawTempreture.indexOf(rawTempreture.lastElement()) - 1)[i];
                    } else {
                        rawTempreture.lastElement()[i] = .0;
                    }
                }
            }
        }


        return new RawData(rawTime, rawSpeed, rawCastLength, rawTempreture);

    }

}
