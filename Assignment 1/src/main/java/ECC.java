import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ECC {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 20000);

            String[] input = br.readLine().split(",");

            double x = Double.parseDouble(input[0].replace("(", ""));
            double y = Double.parseDouble(input[1].replace(")", ""));

            input = br.readLine().split(" ");

            double a = Double.parseDouble(input[0]);
            double b = Double.parseDouble(input[1]);
            double p = Double.parseDouble(input[2]);

            input = br.readLine().split(" ");
            double m = Double.parseDouble(input[0]);
            double n = Double.parseDouble(input[1]);


            String result = ECC.ECCKeyExchange(x, y, a, b, p, m, n);
            System.out.println(result);


        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    private static double modulo(double a, double b) {
        // no division by zero
        if (b == 0) {
            return a;
        }

        if (a < 0) {
            double factor = Math.ceil(a * -1 / b);
            a += factor * b;
        }
        return  a % b;
    }

    private static double modInverse(double a, double m)
    {
        BigInteger bga = BigDecimal.valueOf(a).toBigInteger();
        BigInteger bgm = BigDecimal.valueOf(m).toBigInteger();

        return bga.modInverse(bgm).doubleValue();
    }


    private static double[] solveCurve(double x1, double y1, double x2, double y2, double p, double a) {

        double m;

        if (x1 != x2 || y1 != y2) {
            m = (y2 - y1) * modInverse((x2 - x1), p);
        } else {
            m = ((3 * Math.pow(x1, 2) + a) * modInverse((2 * y1), p));
        }

        m = modulo(m, p);

        double x3 = modulo(Math.pow(m, 2) - x1 - x2, p);
        double y3 = modulo((m * (x1 - x3) - y1),  p);
        return new double[] {x3, y3};
    }

    private static double[] computePointMultiplication(double x, double y, double multiplication, double p, double a) {

        char[] bits = Integer.toBinaryString((int) multiplication).substring(1).toCharArray();

        double[] Q = new double[] {x,y};

        for (char bit : bits) {
            if (bit == '1') {
                // double
                Q = ECC.solveCurve(Q[0], Q[1], Q[0], Q[1], p, a);
                // addition
                Q = ECC.solveCurve(Q[0], Q[1], x, y, p, a);
            } else {
                // double
                Q = ECC.solveCurve(Q[0], Q[1], Q[0], Q[1], p, a);
            }
        }

        return  Q;
    }

    public static String ECCKeyExchange(double x, double y, double a, double b, double p, double m, double n) {

        try {
            double[] alice = ECC.computePointMultiplication(x, y, m, p, a);
            double[] bob = ECC.computePointMultiplication(x, y, n, p, a);

            // The 2 points below should be the same.
            double[] sharedSecretAlice = ECC.computePointMultiplication(bob[0], bob[1], m, p, a);
            double[] sharedSecretBob = ECC.computePointMultiplication(alice[0], alice[1], n, p, a);
            return "(" + fmt(sharedSecretAlice[0]) + ", " + fmt(sharedSecretAlice[1]) + ")";

        } catch (ArithmeticException e) {
            return "(" + fmt(x) + ", " + fmt(y) + ")";
        }
    }


}
