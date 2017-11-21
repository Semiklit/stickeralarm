package ru.nikitasemiklit.model;

public class ModelParameters {
    private final int intervalForCalculatingSec;
    private final double minimumRisingRate;
    private final double maximumRisingRate;
    private final double minimumFallingRate;
    private final double maximumFallingRate;
    private final int minimumTempreture;
    private final int maximumTempreture;
    private final int minimumDurationTempRise;
    private final int maximumDurationTempRise;
    private final int minimumDurationTempFall;
    private final double minimumRatioStickerSpeedToCastingSpeed;
    private final double maximumRatioStickerSpeedToCastingSpeed;
    private final int abnormalInterval;
    private final int abnormalStickerAlarm;
    private final int abnormalStickerWarning;
    private final int minimumCastLength;
    private final double minimumSpeed;
    private final double maximumSpeedRate;
    private final long timeForAlarmChecking;
    private final long minimumTimeSinceLastAlarmDetected;

    public ModelParameters(int intervalForCalculatingSec, double minimumRisingRate, double maximumRisingRate, double minimumFallingRate, double maximumFallingRate, int minimumTempreture, int maximumTempreture, int minimumDurationTempRise, int maximumDurationTempRise, int minimumDurationTempFall, double minimumRatioStickerSpeedToCastingSpeed, double maximumRatioStickerSpeedToCastingSpeed, int abnormalInterval, int abnormalStickerAlarm, int abnormalStickerWarning, int minimumCastLength, double minimumSpeed, double maximumSpeedRate, long timeForAlarmChecking, long minimumTimeSinceLastAlarmDetected) {
        this.intervalForCalculatingSec = intervalForCalculatingSec;
        this.minimumRisingRate = minimumRisingRate;
        this.maximumRisingRate = maximumRisingRate;
        this.minimumFallingRate = minimumFallingRate;
        this.maximumFallingRate = maximumFallingRate;
        this.minimumTempreture = minimumTempreture;
        this.maximumTempreture = maximumTempreture;
        this.minimumDurationTempRise = minimumDurationTempRise;
        this.maximumDurationTempRise = maximumDurationTempRise;
        this.minimumDurationTempFall = minimumDurationTempFall;
        this.minimumRatioStickerSpeedToCastingSpeed = minimumRatioStickerSpeedToCastingSpeed;
        this.maximumRatioStickerSpeedToCastingSpeed = maximumRatioStickerSpeedToCastingSpeed;
        this.abnormalInterval = abnormalInterval;
        this.abnormalStickerAlarm = abnormalStickerAlarm;
        this.abnormalStickerWarning = abnormalStickerWarning;
        this.minimumCastLength = minimumCastLength;
        this.minimumSpeed = minimumSpeed;
        this.maximumSpeedRate = maximumSpeedRate;
        this.timeForAlarmChecking = timeForAlarmChecking;
        this.minimumTimeSinceLastAlarmDetected = minimumTimeSinceLastAlarmDetected;
    }

    public ModelParameters(){
        intervalForCalculatingSec = 5;
        minimumRisingRate = 0.18;
        maximumRisingRate = 2.2;
        minimumFallingRate = -0.17;
        maximumFallingRate = -2.0;
        minimumTempreture = 50;
        maximumTempreture = 200;
        minimumDurationTempRise = 3;
        maximumDurationTempRise = 25;
        minimumDurationTempFall = 5;
        minimumRatioStickerSpeedToCastingSpeed = 0.38;
        maximumRatioStickerSpeedToCastingSpeed = 1.5;
        abnormalInterval = 30;
        abnormalStickerAlarm = 6;
        abnormalStickerWarning = 3;
        minimumCastLength = 3;
        minimumSpeed = 0.5;
        maximumSpeedRate = 0.006;
        timeForAlarmChecking = 120;
        minimumTimeSinceLastAlarmDetected = 60;
    }

    public ModelParameters(double[] input){
        intervalForCalculatingSec = 5;
        minimumRisingRate = input[0];
        maximumRisingRate = input[1];
        minimumFallingRate = input[2];
        maximumFallingRate = input[3];
        minimumTempreture = 50;
        maximumTempreture = 200;
        minimumDurationTempRise = (int) input[4];
        maximumDurationTempRise = (int) input[5];
        minimumDurationTempFall = (int) input[6];
        minimumRatioStickerSpeedToCastingSpeed = input[7];
        maximumRatioStickerSpeedToCastingSpeed = input[8];
        abnormalInterval = (int)input[9];
        abnormalStickerAlarm = (int)input[10];
        abnormalStickerWarning = 3;
        minimumCastLength = 3;
        minimumSpeed = 0.5;
        maximumSpeedRate = 0.006;
        timeForAlarmChecking = 120;
        minimumTimeSinceLastAlarmDetected = 60;
    }

    public double[] getParametersArray(){
        double[] result = new double[11];
        result [0] = minimumRisingRate;
        result [1] = maximumRisingRate;
        result [2] = minimumFallingRate;
        result [3] = maximumFallingRate;
        result [4] = minimumDurationTempRise;
        result [5] = maximumDurationTempRise;
        result [6] = minimumDurationTempFall;
        result [7] = minimumRatioStickerSpeedToCastingSpeed;
        result [8] = maximumRatioStickerSpeedToCastingSpeed;
        result [9] = abnormalInterval;
        result [10] = abnormalStickerAlarm;
        return result;
    }

    public int getIntervalForCalculatingSec() {
        return intervalForCalculatingSec;
    }

    public double getMinimumRisingRate() {
        return minimumRisingRate;
    }

    public double getMaximumRisingRate() {
        return maximumRisingRate;

    }

    public double getMinimumFallingRate() {
        return minimumFallingRate;
    }

    public double getMaximumFallingRate() {
        return maximumFallingRate;
    }

    public int getMinimumTemperature() {
        return minimumTempreture;
    }

    public int getMaximumTemperature() {
        return maximumTempreture;
    }

    public int getMinimumDurationTempRise() {
        return minimumDurationTempRise;
    }

    public int getMaximumDurationTempRise() {
        return maximumDurationTempRise;
    }

    public int getMinimumDurationTempFall() {
        return minimumDurationTempFall;
    }

    public double getMinimumRatioStickerSpeedToCastingSpeed() {
        return minimumRatioStickerSpeedToCastingSpeed;
    }

    public double getMaximumRatioStickerSpeedToCastingSpeed() {
        return maximumRatioStickerSpeedToCastingSpeed;
    }

    public int getAbnormalInterval() {
        return abnormalInterval;
    }

    public int getAbnormalStickerAlarm() {
        return abnormalStickerAlarm;
    }

    public int getAbnormalStickerWarning() {
        return abnormalStickerWarning;
    }

    public int getMinimumCastLength() {
        return minimumCastLength;
    }

    public double getMinimumSpeed() {
        return minimumSpeed;
    }

    public double getMaximumSpeedRate() {
        return maximumSpeedRate;
    }

    public long getTimeForAlarmChecking() {
        return timeForAlarmChecking;
    }

    public long getMinimumTimeSinceLastAlarmDetected() {
        return minimumTimeSinceLastAlarmDetected;
    }
}
