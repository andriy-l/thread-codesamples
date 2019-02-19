package ua.te.jug.concurrency.shared.vol;

public class ServiceStopThread extends Thread {
    @Override
    public void run() {
        Main.state.stop();
    }
}
