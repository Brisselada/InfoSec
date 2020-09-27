import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Feistel {

    public static void main(String[] args) throws IOException {

        byte[] fileArray = Files.readAllBytes(Path.of("src\\main\\resources\\feistel3.in"));
        byte[] in = handleMessage(fileArray);
        byte[] out = Files.readAllBytes(Path.of("src\\main\\resources\\feistel3.out"));
        System.out.println("filearray: " + fileArray);
        System.out.println("in: \n");
        System.out.write(in);
        System.out.println("out: \n");
        System.out.write(out);
    }

    /**
     * Reads the line with a timeout
     * @param in The supplied BufferedReader
     * @return Returns the line that has been read
     * @throws IOException
     */
    private static String readLineWithTimeout(BufferedReader in) throws IOException {
        int x = 1; // wait 1 second at most

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


    private static int findSeparatorIndex(byte[] reqBytes) {
        byte target = (byte) 255;
        for (int b = 2; b < reqBytes.length; b++){
            if (reqBytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    private static byte[] encrypt(byte[] L, byte[] R, byte[] keyBytes, int iterations, int partSize) {
        byte[] prevL = R;
        byte[] prevR = L;

        for (int i = 0; i < iterations; i++) {
            L = prevR;
            R = prevL;

            // Left is XOR'd, Right passes
            byte[] tempL = new byte[partSize];
            for (int k = 0; k < partSize; k++) {
                //TODO: Repeat key if it doesn't fit partsize?
                tempL[k] = (byte) ( L[k] ^ keyBytes[(iterations + k) % keyBytes.length]);
            }
            L = tempL;
            prevL = L;
            prevR = R;

        }

        byte[] result = new byte[partSize * 2];
        System.arraycopy(L, 0, result, 0, partSize);
        System.arraycopy(R, 0, result, partSize, partSize);

        return result;
    }

    private static byte[] decrypt(byte[] L, byte[] R, byte[] keyBytes, int iterations, int partSize) {

        // Determine what's left and what's right depending on iterations
        byte[] prevL = iterations % 2 == 0 ? R : L;
        byte[] prevR = iterations % 2 == 0 ? L : R;

        for (int i = 0; i < iterations; i++) {
            L = prevR;
            R = prevL;

            // Left is XOR'd, Right passes
            byte[] tempL = new byte[partSize];
            for (int k = 0; k < partSize; k++) {
                //TODO: Repeat key if it doesn't fit partsize?
                tempL[k] = (byte) ( L[k] ^ keyBytes[(iterations + k) % partSize]);
            }
            L = tempL;
            prevL = L;
            prevR = R;
        }

        byte[] result = new byte[partSize * 2];
        System.arraycopy(L, 0, result, 0, partSize);
        System.arraycopy(R, 0, result, partSize, partSize);

        return result;
    }



    private static byte[] handleMessage(byte[] request) {

        // Check encryption or decryption
        boolean encrypt = request[0] == (byte) 'e';
        int n = findSeparatorIndex(request);

        // Get key and text from request
        byte[] keyBytes = new byte[n - 2];
        byte[] textBytes = new byte[request.length - n - 1];
        System.arraycopy(request, 2, keyBytes, 0, n - 2);
        System.arraycopy(request, n+1, textBytes, 0, request.length - n - 1);

        // Determine number of iterations depending on key
        int iterations = textBytes.length / (keyBytes.length / 2);

        int partSize = textBytes.length / 2;

        // Split text into two pieces
        byte[] L = new byte[partSize];
        byte[] R = new byte[partSize];
        System.arraycopy(textBytes, 0, L, 0, partSize);
        System.arraycopy(textBytes, partSize, R, 0, partSize);

        if (encrypt) {
            return encrypt(L, R, keyBytes, iterations, partSize);
        }
        else {
            return decrypt(L, R, keyBytes, iterations, partSize);
        }
//        byte[] result = encrypt(L, R, keyBytes, iterations, partSize);
//
//        return result;
    }
}
