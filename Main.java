
import functions.basic.*;
import functions.*;
import threads.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n Задание 1 ");
        TabulatedFunction func = new ArrayTabulatedFunction(0, 10, 5);
        for (FunctionPoint p : func) { 
            System.out.println(p);
        }
        System.out.println("\n Задание 2 ");
        Function f = new Cos();
        TabulatedFunction tf;
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(
                new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());

        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println(tf.getClass());

        System.out.println("\n Задание 3 ");
        TabulatedFunction reflectFunc;

        reflectFunc = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(reflectFunc.getClass());

        reflectFunc = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println(reflectFunc.getClass());

        reflectFunc = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class,
                new FunctionPoint[] {
                        new FunctionPoint(0, 0),
                        new FunctionPoint(10, 10)
                }
        );
        System.out.println(reflectFunc.getClass());

        reflectFunc = TabulatedFunctions.tabulate(
                LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println(reflectFunc.getClass());
    }
}