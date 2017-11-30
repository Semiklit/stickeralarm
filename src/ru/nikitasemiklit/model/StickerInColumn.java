package ru.nikitasemiklit.model;

import java.util.Vector;

public class StickerInColumn {
    private long firstTimeOfSticker;
    private int sensorsWithStickersCount;
    private final Vector<Integer> sensorsWithSticker = new Vector<>();
    private int firstSensorWithSticker;

    StickerInColumn(long firstTimeOfSticker) {
        this.firstTimeOfSticker = firstTimeOfSticker;
        this.sensorsWithStickersCount = 0;
    }

    long getFirstTimeOfSticker() {
        return firstTimeOfSticker;
    }

    void addLastSensorsWithSticker(int sensor){
        sensorsWithSticker.add(sensor);
        firstSensorWithSticker = sensor;
    }

    void addSensorWithSticker(int sensor){
        sensorsWithSticker.add(sensor);
    }

    int getFirstSensorWithSticker(){
        return firstSensorWithSticker;
    }

    String getSensorsWithSticker (){
        StringBuilder builder = new StringBuilder();
        for (Integer sensor: sensorsWithSticker){
            builder.append((sensor + 1) + " ");
        }
        return builder.toString();
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