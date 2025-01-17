import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RSA {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 20000);

            boolean encrypt = br.readLine().equals("e");
            String arguments = br.readLine();


            StringBuilder inputText = new StringBuilder();
            String input = "";
            do {
                try {
                    input = RSA.readLineWithTimeout(br);
                    if (input != null) {
                        inputText.append(input).append("\n");
                    }
                } catch (IOException e) {
                    input = null;
                }
            } while (input != null);

            RSA.handleMessage(encrypt, arguments, inputText.toString());


        } catch (IOException e) {
            System.out.println(e);
        }
    }


    private static void handleMessage(boolean encrypt, String arguments, String inputText){
        String[] args = arguments.split(" ");
        double p = Double.parseDouble(args[0]);
        double q = Double.parseDouble(args[1]);
        double e = Double.parseDouble(args[2]);

        String result = RSA.RSACipher(encrypt, p, q, e, inputText);
        System.out.println(result.trim());

    }

    private static double modInverse(double a, double m)
    {
        if (GCD(a,m) != 1) {
            return -1;
        }
        int x;
        for (x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                break;
            }
        }
        return x;
    }

    private static int[] egcd(int x, int y) {
        int[] result = new int[3];
        int floordiv;
        if (y == 0) {
            result[0] = x;
            result[1] = 1;
            result[2] = 0;
        } else {
            floordiv = x / y;
            result = egcd(y, x % y);
            int temp = result[1] - floordiv * result[2];
            result[1] = result[2];
            result[2] = temp;
        }
        return result;
    }

//    private static double FindSmallerCoprime(double n) {
//        for (double e = 2; e < n; e++) {
//            double[] gcd = egcd(n, e);
//            if (gcd[0] == 1) {
//                return e;
//            }
//        }
//        return -1;
//    }

    public static String RSACipher(boolean encrypt, double p, double q, double e, String inputText) {
        double N = p * q;
        double phiN = (p - 1) * (q - 1);
        double d = -1;

        StringBuilder results = new StringBuilder();


        for (String line : inputText.split("\n")) {
            if (encrypt) {
                double M = Double.parseDouble(line);
                double C = RSA.exp(M, e, N);
//                double C = RSA.ChineseRemainder(M, e, N, p, q);
                results.append((int) C).append('\n');
            } else {

                if (d == -1) {
                    int[] x = egcd((int) e, (int) phiN);
                    d = x[1];

                    if(d < 0) {
                        d = d + phiN;
                    }
                }

                double C = Double.parseDouble(line);
                double M = RSA.exp(C, d, N);
//                double M = RSA.ChineseRemainder(C, d, N, p, q);
                results.append((int) M).append('\n');
            }
        }

        return results.toString();
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


    private static double ChineseRemainderExponent(double number, double exponent, double modulo) {
        double reducedExponent = modulo(exponent, modulo - 1);
        double reducedNumber = modulo(number, modulo);
        return exp(reducedNumber, reducedExponent, modulo);
    }

    private static double GCD(double a, double b)
    {
        if (b == 0)
            return a;

        return GCD(b, a % b);
    }

    private static double LCM(double gcd, double[] b)
    {
        double total = 1;

        for (int k = 0; k < (double) 2; k++)
            total *= b[k];

        return total/gcd;
    }


    private static double solveSimultaneousPairs(double[] a, double[] b)
    {
        double c = 1;
        for (int k = 0; k < (double) 2; k++)
            c *= b[k];

        double gcd = 0;
        for (int k = 0; k < (double) 2; k++)
            gcd = GCD(gcd, b[k]);

        for (int k = 0; k < (double) 2; k++)
        {
            if ((a[k] % 2 == 1) && (b[k] % 2 == 0))
            {
                for (int j = k + 1; j < (double) 2; j++)
                    if ((a[j] % 2 == 0) && (b[j] % 2 == 0))
                        return -1;	  // no solution
            }
        }

        double lcm = LCM(gcd, b);

        for (int k = 0; k < lcm; k++)
        {
            int j;
            for (j = 0; j < (double) 2; j++)
                if (k % b[j] != a[j])
                    break;

            if (j == (double) 2)
            {
                return k;
            }
        }

        return -1;	 // no solution
    }

    private static double ChineseRemainder(double C, double d, double N, double p, double q) {
        double m1 = ChineseRemainderExponent(C, d, p);
        double m2 = ChineseRemainderExponent(C, d, q);

        return solveSimultaneousPairs(new double[]{m1, m2}, new double[] {p, q});
    }

    /***
     * Check if two numbers are relatively prime
     * @param m Input m
     * @param n Input n
     * @return True if m and n are relatively prime.
     */
    private static boolean isRelativelyPrime(double m, double n) {
        double temp;
        while(n != 0){
            temp = m;
            m = n;
            n = temp % n;
        }
        return m == 1;
    }

    private static boolean isPrime(double n) {
        int i = 2;
        boolean notPrime = false;
        while(i <= n/2)
        {
            // condition for nonprime number
            if(n % i == 0)
            {
                notPrime = true;
                break;
            }

            ++i;
        }

        return !notPrime;
    }

    private static double exp(double a, double b, double modulo) {
        if (b == 0) {
            return 1 % modulo;
        }

        if (b == 1) {
            return a % modulo;
        }

        // if b is odd
        if (b % 2 != 0) {
            return ((a % modulo) * exp(a, b -1, modulo) % modulo);
        }

        double result = exp(a, b /2, modulo);
        return (result * result) % modulo;
    }

    private static double repeatedSquare(double a, double b, double modulo) {
        char[] binary = ("0" + Long.toBinaryString((long) b)).toCharArray();

        double currentValue = 1;
        for(int i = 0; i < binary.length-1; i ++) {
            // If next bit is 1. add one
            boolean addOne = i < binary.length - 1 && binary[i+1] == '1';
            currentValue = (Math.pow(currentValue, 2) * (addOne ? a : 1)) % modulo;
        }

        return currentValue;
    }


    /**
     * Reads the line with a timeout
     * @param in The supplied BufferedReader
     * @return Returns the line that has been read
     * @throws IOException
     */
    private static String readLineWithTimeout(BufferedReader in) throws IOException {
        double x = 0.02; // wait 0.2 second at most

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < x * 1000 && !in.ready()) {
            // wait
        }
        if (in.ready()) {
            return in.readLine();
        } else {
            return null;
        }
    }

}