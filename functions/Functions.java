package functions;

import functions.meta.*;

public final class Functions {
    private Functions() {
    }
    public static double integrate(Function f, double left, double right, double step) {
        if (left < f.getLeftDomainBorder() || right > f.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                    String.format("Интервал интегрирования [%.2f, %.2f] выходит за границы области определения функции [%.2f, %.2f]",
                            left, right, f.getLeftDomainBorder(), f.getRightDomainBorder())
            );
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг интегрирования должен быть положительным: step = " + step);
        }
        if (left >= right) {
            throw new IllegalArgumentException(
                    String.format("Левая граница должна быть меньше правой: left = %.2f, right = %.2f", left, right)
            );
        }
        double integral = 0.0;
        double currentX = left;
        while (currentX < right) {
            double nextX = Math.min(currentX + step, right);
            double fCurrent = f.getFunctionValue(currentX);
            double fNext = f.getFunctionValue(nextX);
            double trapezoidArea = (fCurrent + fNext) * (nextX - currentX) / 2.0;
            integral += trapezoidArea;
            currentX = nextX;
        }
        return integral;
    }
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }
}