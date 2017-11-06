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
    private final int minimumCastLentgh;
    private final double minimumSpeed;
    private final double maximumSpeedRate;
    private final long timeForAlarmChecking;
    private final long minimumTimeSinceLastAlarmDetected;

    public ModelParameters(int intervalForCalculatingSec, double minimumRisingRate, double maximumRisingRate, double minimumFallingRate, double maximumFallingRate, int minimumTempreture, int maximumTempreture, int minimumDurationTempRise, int maximumDurationTempRise, int minimumDurationTempFall, double minimumRatioStickerSpeedToCastingSpeed, double maximumRatioStickerSpeedToCastingSpeed, int abnormalInterval, int abnormalStickerAlarm, int abnormalStickerWarning, int minimumCastLentgh, double minimumSpeed, double maximumSpeedRate, long timeForAlarmChecking, long minimumTimeSinceLastAlarmDetected) {
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
        this.minimumCastLentgh = minimumCastLentgh;
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
        minimumCastLentgh = 3;
        minimumSpeed = 0.5;
        maximumSpeedRate = 0.006;
        timeForAlarmChecking = 120;
        minimumTimeSinceLastAlarmDetected = 60;

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

    public int getMinimumTempreture() {
        return minimumTempreture;
    }

    public int getMaximumTempreture() {
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

    public int getMinimumCastLentgh() {
        return minimumCastLentgh;
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
