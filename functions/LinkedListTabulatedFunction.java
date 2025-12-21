package functions;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {
    private static final long serialVersionUID = 3L;
    protected class FunctionNode implements Serializable {
        private static final long serialVersionUID = 4L;
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }

        FunctionNode() {
            this.point = null;
            this.prev = this;
            this.next = this;
        }
    }

    private FunctionNode head;
    private int pointsCount;
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;
    private static final double EPSILON = 1e-10;
    public LinkedListTabulatedFunction() {
        head = new FunctionNode();
        head.prev = head;
        head.next = head;
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }


        int startIndex;
        FunctionNode current;

        if (Math.abs(index - lastAccessedIndex) < Math.abs(index)) {
            startIndex = lastAccessedIndex;
            current = lastAccessedNode;
        } else {
            startIndex = 0;
            current = head.next;
        }

        if (index > startIndex) {
            for (int i = startIndex; i < index; i++) {
                current = current.next;
            }
        } else {
            for (int i = startIndex; i > index; i--) {
                current = current.prev;
            }
        }

        lastAccessedNode = current;
        lastAccessedIndex = index;
        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(new FunctionPoint(), head.prev, head);
        head.prev.next = newNode;
        head.prev = newNode;
        pointsCount++;
        return newNode;
    }

    private FunctionNode addNodeByIndex(int index) {
        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(new FunctionPoint(), nodeAtIndex.prev, nodeAtIndex);
        nodeAtIndex.prev.next = newNode;
        nodeAtIndex.prev = newNode;
        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = index;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (pointsCount <= 2) {
            throw new IllegalStateException("Cannot delete point - minimum 2 points required");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;
        pointsCount--;

        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = nodeToDelete.next;
            if (lastAccessedNode == head) {
                lastAccessedNode = head.next;
                lastAccessedIndex = 0;
            }
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }

        return nodeToDelete;
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        this();
        if (points.length < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX() + EPSILON) {
                throw new IllegalArgumentException("Points must be strictly ordered by increasing X");
            }
        }
        for (FunctionPoint point : points) {
            try {
                addPoint(new FunctionPoint(point));
            } catch (InappropriateFunctionPointException e) {
                throw new RuntimeException("Unexpected error in constructor", e);
            }
        }
    }
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        this();
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border must be less than right border");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Points count must be at least 2");
        }
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            try {
                addPoint(new FunctionPoint(x, 0.0));
            } catch (InappropriateFunctionPointException e) {
                throw new RuntimeException("Unexpected error in constructor", e);
            }
        }
    }
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this();
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border must be less than right border");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Points count must be at least 2");
        }

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            try {
                addPoint(new FunctionPoint(x, values[i]));
            } catch (InappropriateFunctionPointException e) {
                throw new RuntimeException("Unexpected error in constructor", e);
            }
        }
    }

    public double getLeftDomainBorder() {
        if (pointsCount == 0) {
            throw new IllegalStateException("Function has no points");
        }
        return head.next.point.getX();
    }

    public double getRightDomainBorder() {
        if (pointsCount == 0) {
            throw new IllegalStateException("Function has no points");
        }
        return head.prev.point.getX();
    }

  
    public int getPointsCount() {
        return pointsCount;
    }
    public FunctionPoint getPoint(int index) {
        checkIndex(index);

        return new FunctionPoint(getNodeByIndex(index).point);
    }
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        FunctionNode node = getNodeByIndex(index);

        double newX = point.getX();
        double leftBound = (index > 0) ? node.prev.point.getX() : -Double.MAX_VALUE;
        double rightBound = (index < pointsCount - 1) ? node.next.point.getX() : Double.MAX_VALUE;

        if (newX <= leftBound + EPSILON || newX >= rightBound - EPSILON) {
            throw new InappropriateFunctionPointException("X coordinate violates ordering");
        }

        FunctionNode current = head.next;
        while (current != head) {
            if (current != node && Math.abs(current.point.getX() - newX) < EPSILON) {
                throw new InappropriateFunctionPointException("Point with same X already exists");
            }
            current = current.next;
        }
        node.point = new FunctionPoint(point);
    }


    public double getPointX(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getX();
    }


    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        FunctionNode node = getNodeByIndex(index);

        double leftBound = (index > 0) ? node.prev.point.getX() : -Double.MAX_VALUE;
        double rightBound = (index < pointsCount - 1) ? node.next.point.getX() : Double.MAX_VALUE;

        if (x <= leftBound + EPSILON || x >= rightBound - EPSILON) {
            throw new InappropriateFunctionPointException("X coordinate violates ordering");
        }

        FunctionNode current = head.next;
        while (current != head) {
            if (current != node && Math.abs(current.point.getX() - x) < EPSILON) {
                throw new InappropriateFunctionPointException("Point with same X already exists");
            }
            current = current.next;
        }

        node.point.setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        getNodeByIndex(index).point.setY(y);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Index: " + index);
        }
    }
    public void deletePoint(int index) {
        checkIndex(index);
        if (pointsCount <= 2) {
            throw new IllegalStateException("Cannot delete point - minimum 2 points required");
        }

        deleteNodeByIndex(index);
    }


    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.next;
        while (current != head && current.point != null) {
            if (Math.abs(current.point.getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Point with same X already exists");
            }
            current = current.next;
        }
        current = head.next;
        int index = 0;
        while (current != head && current.point != null && current.point.getX() < point.getX()) {
            current = current.next;
            index++;
        }
        FunctionNode newNode = new FunctionNode(new FunctionPoint(point), null, null);
        if (pointsCount == 0) {
            newNode.prev = head;
            newNode.next = head;
            head.next = newNode;
            head.prev = newNode;
        } else if (index == pointsCount) {
            newNode.prev = head.prev;
            newNode.next = head;
            head.prev.next = newNode;
            head.prev = newNode;
        } else {
            newNode.prev = current.prev;
            newNode.next = current;
            current.prev.next = newNode;
            current.prev = newNode;
        }
        pointsCount++;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }

    public double getFunctionValue(double x) {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        double left = getLeftDomainBorder();
        double right = getRightDomainBorder();
        if (x < left - EPSILON || x > right + EPSILON) {
            return Double.NaN;
        }
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - x) < EPSILON) {
                return current.point.getY();
            }
            current = current.next;
        }
        current = head.next;
        while (current.next != head) {
            double x1 = current.point.getX();
            double x2 = current.next.point.getX();
            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                double y1 = current.point.getY();
                double y2 = current.next.point.getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.next;
        }
        return Double.NaN;
    }
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);

        FunctionNode current = head.next;
        while (current != head) {
            out.writeDouble(current.point.getX());
            out.writeDouble(current.point.getY());
            current = current.next;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        head = new FunctionNode();
        head.prev = head;
        head.next = head;
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            try {
                addPoint(new FunctionPoint(x, y));
            } catch (InappropriateFunctionPointException e) {
                throw new IOException("Ошибка при чтении функции", e);
            }
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        FunctionNode current = head.next;
        int count = 0;

        while (current != head) {
            if (count > 0) sb.append(", ");
            sb.append(current.point.toString());
            current = current.next;
            count++;
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
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction otherList = (LinkedListTabulatedFunction) o;

            FunctionNode current1 = this.head.next;
            FunctionNode current2 = otherList.head.next;

            while (current1 != this.head && current2 != otherList.head) {
                if (!current1.point.equals(current2.point)) {
                    return false;
                }
                current1 = current1.next;
                current2 = current2.next;
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
        FunctionNode current = head.next;
        while (current != head) {
            hash ^= current.point.hashCode();
            current = current.next;
        }
        hash ^= pointsCount;

        return hash;
    }
    @Override
    public Object clone() {
        LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction();
        if (pointsCount == 0) {
            return cloned;
        }
        FunctionNode current = this.head.next;
        FunctionNode lastClonedNode = null;
        FunctionPoint firstPointClone = (FunctionPoint) current.point.clone();
        FunctionNode firstClonedNode = cloned.new FunctionNode(firstPointClone, cloned.head, cloned.head);
        cloned.head.next = firstClonedNode;
        cloned.head.prev = firstClonedNode;
        lastClonedNode = firstClonedNode;
        current = current.next;
        while (current != this.head) {
            FunctionPoint pointClone = (FunctionPoint) current.point.clone();
            FunctionNode clonedNode = cloned.new FunctionNode(pointClone, lastClonedNode, cloned.head);

            lastClonedNode.next = clonedNode;
            lastClonedNode = clonedNode;

            current = current.next;
        }
        cloned.head.prev = lastClonedNode;
        cloned.pointsCount = this.pointsCount;
        return cloned;
    }
    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.next;
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
                FunctionPoint result = new FunctionPoint(currentNode.point);
                currentNode = currentNode.next;
                currentIndex++;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }
}