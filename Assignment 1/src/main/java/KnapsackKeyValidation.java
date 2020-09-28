import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KnapsackKeyValidation {

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String[] requests = br.readLine().split(" ");
            int m = Integer.parseInt(requests[0]);
            int n = Integer.parseInt(requests[1]);
            String[] privString = br.readLine().split(" ");
            String[] pubString = br.readLine().split(" ");
            int[] privateKey = stringArrToIntArr(privString);
            int[] publicKey = stringArrToIntArr(pubString);
            int result = validateKeys(m, n, privateKey, publicKey);
            System.out.println(result);

//            while ((line = br.readLine()) != null) {
//                handleMessage(requests, line);
//            }
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

    private static boolean validatePrivateKey (int[] privateKey, int n) {
        int sum = privateKey[0];
        int lastElement = privateKey[0];
        for (int i = 1; i < privateKey.length; i++) {
            if ((privateKey[i] <= sum) || (privateKey[i] <= lastElement) || (privateKey[i] <= 0)) {
                return false;
            }
            sum += privateKey[i];
            lastElement = privateKey[i];
        }
        return n > sum;
    }

    private static boolean gcd(int m, int n) {
        int temp;
        while(n != 0){
            temp = m;
            m = n;
            n = temp % n;
        }
        return m == 1;
    }

    private static int validateKeys(int m, int n, int[] privateKey, int[] publicKey) {

        // Return -1 if private key is invalid (weight of an element is not greater than sum of previous elements)
        if (privateKey.length != publicKey.length)  { return 0; }
        if (!validatePrivateKey(privateKey, n)) { return -1; }

        // Check if m and n are relatively primes
        if (!gcd(m, n)) { return -1; }

        // Check if public key derives from private key
        for (int i = 0; i < privateKey.length; i++) {
            int ele = (privateKey[i] * m) % n;
            if (publicKey[i] != ele) {
                return 0;
            }
        }
        return 1;
    }
}
