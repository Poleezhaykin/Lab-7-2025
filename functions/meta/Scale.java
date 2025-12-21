package functions.meta;
import functions.Function;
public class Scale implements Function {
    private Function f;
    private double scaleX;
    private double scaleY;

    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder() * scaleX;
    }

    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder() * scaleX;
    }

    @Override
    public double getFunctionValue(double x) {
        double scaledX = x / scaleX;
        double y = f.getFunctionValue(scaledX);
        if (Double.isNaN(y)) {
            return Double.NaN;
        }
        return y * scaleY;
    }
}