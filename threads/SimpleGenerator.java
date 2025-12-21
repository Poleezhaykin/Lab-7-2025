package threads;

import functions.basic.Log;
public class SimpleGenerator implements Runnable {
    private Task task;
    public SimpleGenerator(Task task) {
        this.task = task;
    }
    @Override
    public void run() {
        int taskCount = task.getTaskCount();
        for (int i = 0; i < taskCount; i++) {
            double base = 1 + Math.random() * 9; // от 1 до 10
            Log logFunc = new Log(base);
            double left = 100 * Math.random();
            double right = 100 + 100 * Math.random();
            double step = Math.random();
            // синхронизация для предотвращения несогласованных данных
            synchronized (task) {
                task.setAll(logFunc, left, right, step);
                System.out.printf("Generator: Source %.4f %.4f %.4f%n",
                        left, right, step);
                task.notify();
            }
        }
    }
}