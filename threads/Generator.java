package threads;

import functions.basic.Log;
public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }
    @Override
    public void run() {
        int taskCount = task.getTaskCount();

        for (int i = 0; i < taskCount && !isInterrupted(); i++) {
            try {
                semaphore.beginWrite();
                double base = 1 + Math.random() * 9;
                Log logFunc = new Log(base);
                double left = 100 * Math.random();
                double right = 100 + 100 * Math.random();
                double step = Math.random();
                task.setAll(logFunc, left, right, step);
                System.out.printf("Generator (semaphore): Source %.4f %.4f %.4f%n",
                        left, right, step);
                semaphore.endWrite();
            } catch (InterruptedException e) {
                interrupt();
                break;
            }
        }
    }
}