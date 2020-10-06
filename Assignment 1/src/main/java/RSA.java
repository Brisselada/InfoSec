import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;

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


    public static String RSACipher(boolean encrypt, double p, double q, double e, String inputText) {
        double N = p * q;
        double modulo = (p - 1) * (q - 1);

        // Private key:
        double d = modInverse(e, modulo);
        // Public key: (N, e)

        StringBuilder results = new StringBuilder();


        for (String line : inputText.split("\n")) {
            if (encrypt) {
                double M = Double.parseDouble(line);
                double C = RSA.ChineseRemainder(M, e, N, p, q);
                results.append((int) C).append('\n');
            } else {
                double C = Double.parseDouble(line);
                double M = RSA.ChineseRemainder(C, d, N, p, q);
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
        if (isPrime(modulo)) {
            double reducedExponent = modulo(exponent, modulo - 1);
            double reducedNumber = modulo(number, modulo);
            return repeatedSquare(reducedNumber, reducedExponent, modulo);
        }
        return repeatedSquare(number, exponent, modulo);
    }

    private static double ChineseRemainder(double C, double d, double N, double p, double q) {
        if (isRelativelyPrime(p, q)){

            double m1 = ChineseRemainderExponent(C, d, p);
            double m2 = ChineseRemainderExponent(C, d, q);

            if (m1 == 0|| m2 == 0) {
                return RSA.repeatedSquare(C, d, N);
            }

            if (m1 > m2) {
                double x = (m1 - m2) / modulo(q, p);
                return q * x + m2;
            } else {
                // Dit werkt nog niet dus
                double x = (m2 - m1) / modulo(p, q);
                return p * x + m1;
            }

        } else {
            return RSA.repeatedSquare(C, d, N);
        }
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

    private static double modInverse(double a, double m)
    {
        BigInteger bga = BigDecimal.valueOf(a).toBigInteger();
        BigInteger bgm = BigDecimal.valueOf(m).toBigInteger();

        return bga.modInverse(bgm).doubleValue();
    }

    /**
     * Reads the line with a timeout
     * @param in The supplied BufferedReader
     * @return Returns the line that has been read
     * @throws IOException
     */
    private static String readLineWithTimeout(BufferedReader in) throws IOException {
        double x = 0.1; // wait 0.2 second at most

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