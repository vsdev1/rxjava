package de.idealo.offers.bestprice.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Util {

    public static void delay() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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

    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        return CompletableFuture.supplyAsync(() -> futures.stream().map(CompletableFuture::join).collect(toList()));
    }
}
