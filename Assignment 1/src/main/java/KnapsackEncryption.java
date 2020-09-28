import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KnapsackEncryption {

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String encryptionMethod = br.readLine();
            if (encryptionMethod.charAt(0) == 'e') {
                // Encryption
//                System.out.println("Encryption");
                String[] publicKeyStrings = br.readLine().split(" ");
                int[] publicKey = stringArrToIntArr(publicKeyStrings);
//                System.out.println("Found publicKey");

                String line;
                while ((line = br.readLine()) != null) {
//                    System.out.println("line is " + line);
                    encrypt(publicKey, Integer.parseInt(line));
                }
            }
            else {
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
//        int maxBits = (int) Math.pow(2, (keyLength)) - 1;
        int maxBits;
        for (int i = 0; i < keyLength; i++) {
            maxBits = (int) Math.pow(2, (keyLength - i - 1));
            if (j >= maxBits) {
                result[keyLength - i - 1] = 1;
                j -= maxBits;
            }
//            maxBits /= 2;
        }
        return result;
    }

    private static void encrypt(int[] publicKey, int p) {

        int keyLength = publicKey.length;

        // Get the max possible bits for the keyLength
//        int maxBits = (int) Math.pow(2, (keyLength - 1)) + 1;
        String StringBits = Integer.toBinaryString(p);
//        int[] bits = stringToIntArr(StringBits);
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
        
    }

}
