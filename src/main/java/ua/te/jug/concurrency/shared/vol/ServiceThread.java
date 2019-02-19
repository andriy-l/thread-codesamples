package ua.te.jug.concurrency.shared.vol;

public class ServiceThread extends Thread {
    @Override
    public void run() {
        Main.state.loop();
    }
}
