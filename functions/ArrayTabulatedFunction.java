package functions;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.Serializable;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 2L;
    private FunctionPoint[] points;
    private int pointsCount;
    private static final double EPSILON = 1e-10;
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX() + EPSILON) {
                throw new IllegalArgumentException("Points must be strictly ordered by increasing X");
            }
        }
        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10];

        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Points count must be at least 2, got: " + pointsCount);
        }
        if (rightX - leftX < EPSILON) {
            throw new IllegalArgumentException(
                    "Left border must be less than right border. Got: leftX=" + leftX + ", rightX=" + rightX
            );
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0.0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("Values array must have at least 2 elements");
        }
        if (rightX - leftX < EPSILON) {
            throw new IllegalArgumentException(
                    "Left border must be less than right border. Got: leftX=" + leftX + ", rightX=" + rightX
            );
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }
    

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);

        double newX = point.getX();
        double leftBound = (index > 0) ? points[index - 1].getX() : -Double.MAX_VALUE;
        double rightBound = (index < pointsCount - 1) ? points[index + 1].getX() : Double.MAX_VALUE;
        
        if (newX <= leftBound + EPSILON || newX >= rightBound - EPSILON) {
            throw new InappropriateFunctionPointException(
                    "New X coordinate " + newX + " would break point ordering. " +
                            "Must be in (" + leftBound + ", " + rightBound + ")"
            );
        }

        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);

        double leftBound = (index > 0) ? points[index - 1].getX() : -Double.MAX_VALUE;
        double rightBound = (index < pointsCount - 1) ? points[index + 1].getX() : Double.MAX_VALUE;

        if (x <= leftBound + EPSILON || x >= rightBound - EPSILON) {
            throw new InappropriateFunctionPointException(
                    "New X coordinate " + x + " would break point ordering. " +
                            "Must be in (" + leftBound + ", " + rightBound + ")"
            );
        }

        points[index].setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }


    public void deletePoint(int index) {
        if (pointsCount <= 2) {
            throw new IllegalStateException(
                    "Cannot delete point: function must have at least 2 points. Current: " + pointsCount
            );
        }

        checkIndex(index);

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {

        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException(
                        "Point with X = " + point.getX() + " already exists at index " + i
                );
            }
        }
        if (pointsCount == points.length) {
            FunctionPoint[] newArray = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            points = newArray;
        }
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX() - EPSILON) {
            insertIndex++;
        }

        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Index " + index + " is out of bounds [0, " + (pointsCount - 1) + "]"
            );
        }
    }


    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON) {
            return Double.NaN;
        }
        
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - x) < EPSILON) {
                return points[i].getY();
            }
        }

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();
            double y1 = points[i].getY();
            double y2 = points[i + 1].getY();
            return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < pointsCount; i++) {
            if (i > 0) sb.append(", ");
            sb.append(points[i].toString());
        }
        sb.append("}");
        return sb.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof TabulatedFunction)) return false;
        TabulatedFunction other = (TabulatedFunction) o;
        if (this.getPointsCount() != other.getPointsCount()) return false;
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction otherArray = (ArrayTabulatedFunction) o;
            for (int i = 0; i < pointsCount; i++) {
                if (!points[i].equals(otherArray.points[i])) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint myPoint = this.getPoint(i);
                FunctionPoint otherPoint = other.getPoint(i);
                if (!myPoint.equals(otherPoint)) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode();
        }
        hash ^= pointsCount;
        return hash;
    }
    @Override
    public Object clone() {
        try {
            ArrayTabulatedFunction cloned = (ArrayTabulatedFunction) super.clone();

            cloned.points = new FunctionPoint[this.points.length];
            for (int i = 0; i < this.pointsCount; i++) {
                cloned.points[i] = (FunctionPoint) this.points[i].clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < pointsCount;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                return new FunctionPoint(points[currentIndex++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }
    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }
}