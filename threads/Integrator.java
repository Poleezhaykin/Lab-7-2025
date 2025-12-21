package threads;

import functions.Functions;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;
    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }
    @Override
    public void run() {
        int taskCount = task.getTaskCount();
        int completed = 0;

        while (completed < taskCount && !isInterrupted()) {
            try {
                semaphore.beginRead();
                if (task.getFunction() != null) { //простая проверка во избежание NPE
                    double left = task.getLeft();
                    double right = task.getRight();
                    double step = task.getStep();
                    try {
                        double result = Functions.integrate(task.getFunction(),
                                left, right, step);
                        System.out.printf("Integrator (semaphore): Result %.4f %.4f %.4f %.10f%n",
                                left, right, step, result);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Integrator (semaphore): Ошибка при интегрировании: " + e.getMessage());
                    }
                }
                semaphore.endRead();
                completed++;
            } catch (InterruptedException e) {
                interrupt();
                break;
            }
        }
    }
}