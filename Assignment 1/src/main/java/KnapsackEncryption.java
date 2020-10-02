import java.io.BufferedReader;
import java.io.IOException;
import java.math.*;
import java.io.InputStreamReader;

public class KnapsackEncryption {

    /***
     * Start a knapsack encryption instance and read user input to encrypt or decrypt.
     * @param args The arguments for using knapsack encryption
     */
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String encryptionMethod = br.readLine();
            if (encryptionMethod.charAt(0) == 'e') {
                // Encryption
                String[] publicKeyStrings = br.readLine().split(" ");
                int[] publicKey = stringArrToIntArr(publicKeyStrings);

                String line;
                while ((line = br.readLine()) != null) {
                    int sum = EncryptKnapsack(publicKey, Integer.parseInt(line));
                    System.out.println(sum);
                }
            }
            else {
                // Decryption
                String[] mn = br.readLine().split(" ");
                int m = Integer.parseInt(mn[0]);
                int n = Integer.parseInt(mn[1]);
                String[] knapsackStrings = br.readLine().split(" ");
                int[] knapsack = stringArrToIntArr(knapsackStrings);

                String line;
                while ((line = br.readLine()) != null) {
                    int result = DecryptKnapsack(m, n, knapsack, Integer.parseInt(line));
                    System.out.println(result);
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Parse string array to int array to handle letters as numbers for knapsack.
     * @param stringArr The String array to parse.
     * @return The parsed int array result.
     */
    private static int[] stringArrToIntArr(String[] stringArr) {
        int[] result = new int[stringArr.length];
        for (int i = 0; i < stringArr.length; i++) {
            result[i] = Integer.parseInt(stringArr[i]);
        }
        return result;
    }

    /***
     * Parse numbers to bits (1/0) that will be used to calculate the knapsack sum.
     * @param j Input number
     * @param keyLength total key length.
     * @return the bit result.
     */
    private static int[] intToBits(int j, int keyLength) {
        int[] result = new int[keyLength];
        int maxBits;
        for (int i = 0; i < keyLength; i++) {
            maxBits = (int) Math.pow(2, (keyLength - i - 1));
            if (j >= maxBits) {
                result[keyLength - i - 1] = 1;
                j -= maxBits;
            }
        }
        return result;
    }

    /***
     * Parse bits to integers to calculate the decrypted knapsack result.
     * @param j Input number
     * @return The total sum.
     */
    private static int bitsToInt(int[] j) {
        int keyLength = j.length;

        int sum = 0;
        for (int i = 0; i < keyLength; i++) {
            if (j[i] == 1) {
                sum += (int) Math.pow(2, i);
            }
        }
        return sum;
    }

    /***
     * Solve knapsack using a given integer array (super increasing knapsack).
     * @param knapsack Input knapsack integer array.
     * @param target Target sum to solve.
     * @return Bit array result.
     */
    private static int[] solveKnapsack(int[] knapsack, int target ) {
        int keyLength = knapsack.length;
        int[] result = new int[keyLength];

        for (int i = (keyLength - 1); i >= 0; i--) {
            if (knapsack[i] <= target) {
                result[i] = 1;
                target -= knapsack[i];
            }
        }
        return result;
    }

    /***
     * Encrypt input with a public key using knapsack cipher.
     * @param publicKey Public key to encrypt with.
     * @param p Input number
     */
    public static int EncryptKnapsack(int[] publicKey, int p) {
        int keyLength = publicKey.length;
        int[] bits = intToBits(p, keyLength);
        int sum = 0;

        for (int i = 0; i < keyLength; i++) {
            if (bits[i] == 1) {
                sum += publicKey[i];
            }
        }

        return sum;
    }

    /***
     * Decrypt input using knapsack cipher.
     * @param m Input M.
     * @param n Input N.
     * @param knapsack Input knapsack array.
     * @param c Input character number.
     * @return Decrypted value
     */
    public static int DecryptKnapsack(int m, int n, int[] knapsack, int c) {
        // Determine the modular inverse of m
        BigInteger bigM = BigInteger.valueOf(m);
        BigInteger bigN = BigInteger.valueOf(n);
        int mInverse = (bigM.modInverse(bigN)).intValue();

        // Determine the value of the encrypted character
        int cypherValue = (c * mInverse) % n;

        // Retrieve bits of original character
        int[] cypherBits = solveKnapsack(knapsack, cypherValue);

        // Convert found bits
        return bitsToInt(cypherBits);
    }
}
