import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ECC {

    /***
     * Main function for ECC. Reads input and prints the output of the ECC.
     * @param args Input arguments from the console
     */
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 20000);

            String[] input = br.readLine().split(",");

            double x = Double.parseDouble(input[0].replace("(", ""));
            double y = Double.parseDouble(input[1].replace(")", ""));

            input = br.readLine().split(" ");

            // input index 1 is the b parameter value on the ECC curve. This value is not used.
            double a = Double.parseDouble(input[0]);
            double p = Double.parseDouble(input[2]);

            input = br.readLine().split(" ");
            double m = Double.parseDouble(input[0]);
            double n = Double.parseDouble(input[1]);

            // Get the result of the ECC key exchange.
            double[] result = ECC.ECCKeyExchange(x, y, a, p, m, n);
            double xResult = result[0];
            double yResult = result[1];
            System.out.println("(" + fmt(xResult) + ", " + fmt(yResult) + ")");


        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /***
     * Format a double without the zero decimals. "1.10" -> "1.1"
     * @param value Value to format
     * @return Formatted value
     */
    public static String fmt(double value)
    {
        if(value == (long) value)
            return String.format("%d", (long) value);
        else
            return String.format("%s", value);
    }

    /***
     * Calculate the modulo with negative number support.
     * @param value Number to calculate a modulo on
     * @param modulo The modulo
     * @return value with modulo result
     */
    private static double modulo(double value, double modulo) {
        // no division by zero
        if (modulo == 0) {
            return value;
        }

        if (value < 0) {
            double factor = Math.ceil(value * -1 / modulo);
            value += factor * modulo;
        }
        return  value % modulo;
    }

    /***
     * Calculate the modular inverse of a number.
     * @param value The value to calculate the modular inverse of
     * @param modulo The modulo
     * @return the modular inverse
     */
    private static double modInverse(double value, double modulo)
    {
        BigInteger bga = BigDecimal.valueOf(value).toBigInteger();
        BigInteger bgm = BigDecimal.valueOf(modulo).toBigInteger();

        return bga.modInverse(bgm).doubleValue();
    }


    /***
     * Add two points in ECC and get P3.
     * @param x1 x of point one
     * @param y1 y of point one
     * @param x2 x of point two
     * @param y2 y of point who
     * @param modulo the modulo
     * @param a the 'a' parameter of the ECC formula
     * @return P3 on the ECC curve
     */
    private static double[] addPointsInECCurve(double x1, double y1, double x2, double y2, double modulo, double a) {
        double m;

        if (x1 != x2 || y1 != y2) {
            m = (y2 - y1) * modInverse((x2 - x1), modulo);
        } else {
            m = ((3 * Math.pow(x1, 2) + a) * modInverse((2 * y1), modulo));
        }

        m = modulo(m, modulo);

        double x3 = modulo(Math.pow(m, 2) - x1 - x2, modulo);
        double y3 = modulo((m * (x1 - x3) - y1),  modulo);
        return new double[] {x3, y3};
    }

    /***
     * Compute a point multiplication on the ECC curve
     * @param x the x value of a point on the curve
     * @param y the u value of a point on the curve
     * @param multiplication the multiplication that must be aplied on the point
     * @param modulo the modulo
     * @param a the 'a' parameter of the ECC formula
     * @return the multiplication on the point
     */
    private static double[] computePointMultiplication(double x, double y, double multiplication, double modulo, double a) {
        char[] bits = Integer.toBinaryString((int) multiplication).substring(1).toCharArray();

        double[] Q = new double[] {x,y};

        for (char bit : bits) {
            if (bit == '1') {
                // double
                Q = ECC.addPointsInECCurve(Q[0], Q[1], Q[0], Q[1], modulo, a);
                // addition
                Q = ECC.addPointsInECCurve(Q[0], Q[1], x, y, modulo, a);
            } else {
                // double
                Q = ECC.addPointsInECCurve(Q[0], Q[1], Q[0], Q[1], modulo, a);
            }
        }

        return  Q;
    }

    /***
     * Compute a ECC key exchange
     * @param x the initial x point on the ECC curve
     * @param y the initial y point on the ECC curve
     * @param a the 'a' parameter of the ECC formula
     * @param modulo modulo the modulo
     * @param m the secret of person 1
     * @param n the secret of person 2
     * @return The result position on the ECC curve
     */
    public static double[] ECCKeyExchange(double x, double y, double a, double modulo, double m, double n) {
        try {
            double[] privateSecret = ECC.computePointMultiplication(x, y, n, modulo, a);
            double[] sharedSecret = ECC.computePointMultiplication(privateSecret[0], privateSecret[1], m, modulo, a);

            return new double[]{sharedSecret[0], sharedSecret[1]};

        } catch (ArithmeticException e) {
            return new double[]{x, y};
        }
    }


}
