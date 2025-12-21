package threads;

public class Semaphore {
    private boolean canWrite = true;

    public synchronized void beginWrite() throws InterruptedException {
        while (!canWrite) {
            wait();
        }
        canWrite = false;
    }

    public synchronized void endWrite() {
        canWrite = true;
        notifyAll();
    }

    public synchronized void beginRead() throws InterruptedException {
        while (canWrite) {
            wait();
        }
    }

    public synchronized void endRead() {
        canWrite = true;
        notifyAll();
    }
}