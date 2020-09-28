import java.io.BufferedReader;
import java.math.*;
import java.io.InputStreamReader;

public class KnapsackEncryption {

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
                    encrypt(publicKey, Integer.parseInt(line));
                }
            }
            else {
                // Decryption
                String[] mn = br.readLine().split(" ");
                int m = Integer.parseInt(mn[0]);
                int n = Integer.parseInt(mn[1]);
                String[] knapSackStrings = br.readLine().split(" ");
                int[] knapSack = stringArrToIntArr(knapSackStrings);

                String line;
                while ((line = br.readLine()) != null) {
                    decrypt(m, n, knapSack, Integer.parseInt(line));
                }
            }

            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static int[] stringArrToIntArr(String[] stringArr) {
        int[] result = new int[stringArr.length];
        for (int i = 0; i < stringArr.length; i++) {
            result[i] = Integer.parseInt(stringArr[i]);
        }
        return result;
    }

    private static int[] stringToIntArr(String string) {
        char[] stringArr = string.toCharArray();
        int[] result = new int[stringArr.length];
        for (int i = 0; i < stringArr.length; i++) {
            result[i] = stringArr[i];
        }
        return result;
    }

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

    private static int[] solveKnapsack(int[] knapSack, int target ) {
        int keyLength = knapSack.length;
        int[] result = new int[keyLength];

        for (int i = (keyLength - 1); i >= 0; i--) {
            if (knapSack[i] <= target) {
                result[i] = 1;
                target -= knapSack[i];
            }
        }
        return result;
    }

    private static void encrypt(int[] publicKey, int p) {

        int keyLength = publicKey.length;
        int[] bits = intToBits(p, keyLength);
        int sum = 0;

        for (int i = 0; i < keyLength; i++) {
            if (bits[i] == 1) {
                sum += publicKey[i];
            }
        }
        System.out.println(sum);
    }

    private static void decrypt(int m, int n, int[] knapsack, int c) {

        // Determine the modular inverse of m
        BigInteger bigM = BigInteger.valueOf(m);
        BigInteger bigN = BigInteger.valueOf(n);
        int mInverse = (bigM.modInverse(bigN)).intValue();

        // Determine the value of the encrypted character
        int cypherValue = (c * mInverse) % n;

        // Retrieve bits of original character
        int[] cypherBits = solveKnapsack(knapsack, cypherValue);

        // Convert found bits
        int result = bitsToInt(cypherBits);
        System.out.println(result);
    }
}
