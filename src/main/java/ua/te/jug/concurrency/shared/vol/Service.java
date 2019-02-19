package ua.te.jug.concurrency.shared.vol;

public class Service {
//    private volatile boolean flag = true;
    private boolean flag = true;
    private long val = 0;

    public void loop() {
        System.out.println("Service has been started " + val);
        long i = 0;
        // if not volatile this thread see first value of this flag
        while (flag) {
            val = i++;
        }
        System.out.println("Service has been stopped " + val);
    }

    public void stop() {
        flag = false;
        System.out.println("Stop service " + val);
    }
}
