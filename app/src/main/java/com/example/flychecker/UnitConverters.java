package com.example.flychecker;

public abstract class UnitConverters {

    public static double convertToFarhenheit(double celsius) {
        return roundToTwoDecimals(celsius * 1.8 + 32);
    }

    public static double convertToKelvin(double celsius) {
        return roundToTwoDecimals(celsius + 273.15);
    }

    public static double convertToKmph(double mps) {
        return roundToTwoDecimals(mps * 3.6);
    }

    public static double convertToMph(double mps) {
        return roundToTwoDecimals(mps * 2.2369362920544);
    }

    public static double convertToKnots(double mps) {
        return roundToTwoDecimals(mps * 1.9438444924574);
    }

    //round to two decimal places
    private static double roundToTwoDecimals(double d) {
        return Math.round(d * 100.0) / 100.0;
    }

}
