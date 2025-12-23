import java.io.*;
import functions.basic.*;
import functions.*;
import threads.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\nЗадание 1");
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 10, 5);
        for (FunctionPoint p : arrayFunc) {
            System.out.println(p);
        }
        TabulatedFunction linkedFunc = new LinkedListTabulatedFunction(0, 10, 5);
        for (FunctionPoint p : linkedFunc) {
            System.out.println(p);
        }
        System.out.println("\nЗадание 2");
        Function f = new Cos();
        TabulatedFunction tf;
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("По умолчанию: " + tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(
                new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());

        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("После смены фабрики: " + tf.getClass());

        System.out.println("\nЗадание 3");
        TabulatedFunction reflectFunc;

        reflectFunc = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println("1) " + reflectFunc.getClass());

        reflectFunc = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println("2) " + reflectFunc.getClass());

        reflectFunc = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class,
                new FunctionPoint[] {
                        new FunctionPoint(0, 0),
                        new FunctionPoint(10, 10)
                }
        );
        System.out.println("3) " + reflectFunc.getClass());

        reflectFunc = TabulatedFunctions.tabulate(
                LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println("4) " + reflectFunc.getClass());
        System.out.println("\nЗадание 3: Методы чтения через рефлексию");

        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(byteOut);
            dos.writeInt(3);
            dos.writeDouble(0.0); dos.writeDouble(0.0);
            dos.writeDouble(5.0); dos.writeDouble(25.0);
            dos.writeDouble(10.0); dos.writeDouble(100.0);
            dos.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());

            TabulatedFunction readFromStream = TabulatedFunctions.inputTabulatedFunction(
                    LinkedListTabulatedFunction.class, byteIn);
            System.out.println("inputTabulatedFunction(LinkedList): " + readFromStream.getClass());

        } catch (Exception e) {
            System.err.println("Ошибка inputTabulatedFunction: " + e.getMessage());
        }
        try {
            StringReader reader = new StringReader("3 0.0 0.0 5.0 25.0 10.0 100.0");

            TabulatedFunction readFromReader = TabulatedFunctions.readTabulatedFunction(
                    ArrayTabulatedFunction.class, reader);
            System.out.println("readTabulatedFunction(Array): " + readFromReader.getClass());

        } catch (Exception e) {
            System.err.println("Ошибка readTabulatedFunction: " + e.getMessage());
        }
    }
}