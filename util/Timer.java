package util;

public class Timer {
    private long startTime;
    private long endTime;

    public Timer() {
        this.start();
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }
}
