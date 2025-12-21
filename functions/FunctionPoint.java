package functions;
import java.io.Serializable;
public class FunctionPoint implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }
    public FunctionPoint() {
        this.x = 0.0;
        this.y = 0.0;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint that = (FunctionPoint) o;
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(that.x) &&
                Double.doubleToLongBits(y) == Double.doubleToLongBits(that.y);
    }
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        int xHash = (int)(xBits ^ (xBits >>> 32));
        int yHash = (int)(yBits ^ (yBits >>> 32));
        return xHash ^ yHash;
    }
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}