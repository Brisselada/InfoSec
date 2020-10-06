import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ECC {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 20000);


//            double res = modInverse(-9, 5);

//              TEST
//            double[] res = ECC.solveCurve(1,4,3,1, 5, 2);

//             TEST
//            double[] res = ECC.computePointMultiplication(1,2,15,5, -7);


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
            m = ((3 * x1 + a) / Math.pow((2 * y1), -1));
        }

        m = modulo(m, p);

        double x3 = modulo(Math.pow(m, 2) - x1 - x2, p);
        double y3 = modulo((m * (x1 - x3) - y1),  p);
        return new double[] {x3, y3};
    }

    private static double[] computePointMultiplication(double x, double y, double multiplication, double p, double a) {
        double[] Q = new double[] {x,y};
        double[] N = new double[] {x,y};
//        Q = ECC.solveCurve(Q[0], Q[1], N[0], N[1], p, a);

        for (int i = 0; i < multiplication; i ++) {
            Q = ECC.solveCurve(Q[0], Q[1], N[0], N[1], p, a);
            // double points
//            N[0] = N[0] * 2;
//            N[1] = N[1] * 2;
        }

        return Q;
    }

    public static String ECCKeyExchange(double x, double y, double a, double b, double p, double m, double n) {

        double[] alice = ECC.computePointMultiplication(x, y, m, p, a);
        double[] bob = ECC.computePointMultiplication(x, y, n, p, a);

        // The 2 points below should be the same.
        double[] sharedSecretAlice = ECC.computePointMultiplication(bob[0], bob[1], m, p, a);
        double[] sharedSecretBob = ECC.computePointMultiplication(alice[0], alice[1], n, p, a);



        return "(" + fmt(sharedSecretAlice[0]) + ", " + fmt(sharedSecretAlice[1]) + ")";
    }


}
