package functions.basic;
import functions.Function;
public class Log implements Function {
    private double base;
    public Log(double base) {
        if (base <= 0 || Math.abs(base - 1) < 1e-10) {
            throw new IllegalArgumentException("Base must be positive and not equal to 1");
        }
        this.base = base;
    }
    @Override
    public double getLeftDomainBorder() {
        return 0;
    }
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN;
        }
        return Math.log(x) / Math.log(base);
    }
    public double getBase() {
        return base;
    }
}