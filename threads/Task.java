package threads;

import functions.Function;

public class Task {
    private Function function;
    private double left;
    private double right;
    private double step;
    private int taskCount;

    public Task() {
        this.function = null;
        this.left = 0;
        this.right = 0;
        this.step = 0;
        this.taskCount = 0;
    }

    public synchronized void setFunction(Function function) {
        this.function = function;
    }

    public synchronized Function getFunction() {
        return function;
    }

    public synchronized void setLeft(double left) {
        this.left = left;
    }

    public synchronized double getLeft() {
        return left;
    }

    public synchronized void setRight(double right) {
        this.right = right;
    }

    public synchronized double getRight() {
        return right;
    }

    public synchronized void setStep(double step) {
        this.step = step;
    }

    public synchronized double getStep() {
        return step;
    }

    public synchronized void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public synchronized int getTaskCount() {
        return taskCount;
    }

    public synchronized void setAll(Function function, double left, double right, double step) {
        this.function = function;
        this.left = left;
        this.right = right;
        this.step = step;
    }

    public synchronized void getAll(double[] params) {
        params[0] = left;
        params[1] = right;
        params[2] = step;
    }
}