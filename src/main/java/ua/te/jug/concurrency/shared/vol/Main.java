package ua.te.jug.concurrency.shared.vol;

import java.util.concurrent.TimeUnit;

public class Main {

    public static final Service state = new Service();

    public static void main(String[] args) throws InterruptedException {
        ServiceThread serviceThread = new ServiceThread();
        ServiceStopThread stopThread = new ServiceStopThread();
        serviceThread.start();

        TimeUnit.SECONDS.sleep(8);

        stopThread.start();

    }
}
