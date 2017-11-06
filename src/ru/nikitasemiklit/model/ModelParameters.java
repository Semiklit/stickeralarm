package ru.nikitasemiklit.model;

public class ModelParameters {
    private final int intervalForCalculatingSec;
    private final double minimumRisingRate;
    private final double maximumRisingRate;
    private final double minimumFallingRate;
    private final double maximumFallingRate;
    private final int minimumTempreture;
    private final int maximumTeptreture;
    private final int minimumDurationTempRise;
    private final int maximumDurationTempRise;
    private final int minimumDurationTempFall;
    private final double minimumRatioStickerSpeedToCastingSpeed;
    private final double maximumRatioStickerSpeedToCastingSpeed;
    private final int abnormalInterval;
    private final int abnormalStickerAlarm;
    private final int abnormalStickerWarning;

    public ModelParameters(int intervalForCalculatingSec, double minimumRisingRate, double maximumRisingRate, double minimumFallingRate, double maximumFallingRate, int minimumTempreture, int maximumTeptreture, int minimumDurationTempRise, int maximumDurationTempRise, int minimumDurationTempFall, double minimumRatioStickerSpeedToCastingSpeed, double maximumRatioStickerSpeedToCastingSpeed, int abnormalInterval, int abnormalStickerAlarm, int abnormalStickerWarning) {
        this.intervalForCalculatingSec = intervalForCalculatingSec;
        this.minimumRisingRate = minimumRisingRate;
        this.maximumRisingRate = maximumRisingRate;
        this.minimumFallingRate = minimumFallingRate;
        this.maximumFallingRate = maximumFallingRate;
        this.minimumTempreture = minimumTempreture;
        this.maximumTeptreture = maximumTeptreture;
        this.minimumDurationTempRise = minimumDurationTempRise;
        this.maximumDurationTempRise = maximumDurationTempRise;
        this.minimumDurationTempFall = minimumDurationTempFall;
        this.minimumRatioStickerSpeedToCastingSpeed = minimumRatioStickerSpeedToCastingSpeed;
        this.maximumRatioStickerSpeedToCastingSpeed = maximumRatioStickerSpeedToCastingSpeed;
        this.abnormalInterval = abnormalInterval;
        this.abnormalStickerAlarm = abnormalStickerAlarm;
        this.abnormalStickerWarning = abnormalStickerWarning;
    }

    public ModelParameters(){
        intervalForCalculatingSec = 5;
        minimumRisingRate = 0.18;
        maximumRisingRate = 2.2;
        minimumFallingRate = -0.17;
        maximumFallingRate = -2.0;
        minimumTempreture = 50;
        maximumTeptreture = 200;
        minimumDurationTempRise = 3;
        maximumDurationTempRise = 25;
        minimumDurationTempFall = 5;
        minimumRatioStickerSpeedToCastingSpeed = 0.38;
        maximumRatioStickerSpeedToCastingSpeed = 1.5;
        abnormalInterval = 30;
        abnormalStickerAlarm = 6;
        abnormalStickerWarning = 3;
    }

    public int getIntervalForCalculatingSec() {
        return intervalForCalculatingSec;
    }

    double getMinimumRisingRate() {
        return minimumRisingRate;
    }

    double getMaximumRisingRate() {
        return maximumRisingRate;
    }

    double getMinimumFallingRate() {
        return minimumFallingRate;
    }

    double getMaximumFallingRate() {
        return maximumFallingRate;
    }

    int getMinimumTempreture() {
        return minimumTempreture;
    }

    int getMaximumTeptreture() {
        return maximumTeptreture;
    }

    int getMinimumDurationTempRise() {
        return minimumDurationTempRise;
    }

    int getMaximumDurationTempRise() {
        return maximumDurationTempRise;
    }

    int getMinimumDurationTempFall() {
        return minimumDurationTempFall;
    }

    double getMinimumRatioStickerSpeedToCastingSpeed() {
        return minimumRatioStickerSpeedToCastingSpeed;
    }

    double getMaximumRatioStickerSpeedToCastingSpeed() {
        return maximumRatioStickerSpeedToCastingSpeed;
    }

    int getAbnormalInterval() {
        return abnormalInterval;
    }

    int getAbnormalStickerAlarm() {
        return abnormalStickerAlarm;
    }

    int getAbnormalStickerWarning() {
        return abnormalStickerWarning;
    }
}
