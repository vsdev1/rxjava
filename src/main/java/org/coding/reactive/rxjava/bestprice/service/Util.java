package org.coding.reactive.rxjava.bestprice.service;

public class Util {

    public static void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static double roundTo2DecimalPlaces(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

}
