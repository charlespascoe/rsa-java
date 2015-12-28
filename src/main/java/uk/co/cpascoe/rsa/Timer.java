package uk.co.cpascoe.rsa;

import java.util.Map;
import java.util.HashMap;

public class Timer {
    private static Map<String,Long> timers = new HashMap<String,Long>();

    public static void start(String key) {
        System.out.println("Start " + key);
        Timer.timers.put(key, System.currentTimeMillis());
    }

    public static void stop(String key) {
        long diff = System.currentTimeMillis() - Timer.timers.get(key);
        System.out.println("Stop " + key + ", duration " + Long.toString(diff) + "ms");
    }

    public static void startNano(String key) {
        System.out.println("Start " + key);
        Timer.timers.put(key, System.nanoTime());
    }

    public static void stopNano(String key) {
        long diff = System.nanoTime() - Timer.timers.get(key);
        System.out.println("Stop " + key + ", duration " + Long.toString(diff) + "ns");
    }
}

