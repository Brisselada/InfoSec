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
                double C = RSA.repeatedSquare(M, e, N);
                results.append((int) C).append('\n');
            } else {
                double C = Double.parseDouble(line);
                double M = RSA.repeatedSquare(C, d, N);
                results.append((int) M).append('\n');
            }
        }

        return results.toString();
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
        double x = 0.2; // wait 0.2 second at most

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
