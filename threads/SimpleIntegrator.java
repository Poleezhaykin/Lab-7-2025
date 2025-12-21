package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        int taskCount = task.getTaskCount();
        int completed = 0;
        while (completed < taskCount) {
            double left, right, step;
            // синхронизация для получения согласованных данных
            synchronized (task) {
                while (task.getFunction() == null) {
                    try {
                        task.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                left = task.getLeft();
                right = task.getRight();
                step = task.getStep();
                try {
                    double result = Functions.integrate(task.getFunction(),
                            left, right, step);
                    System.out.printf("Integrator: Result %.4f %.4f %.4f %.10f%n",
                            left, right, step, result);
                } catch (IllegalArgumentException e) {
                    System.out.println("Integrator: Ошибка при интегрировании: " + e.getMessage());
                }
                task.setFunction(null);
                completed++;
            }
        }
    }
}