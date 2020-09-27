import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Feistel {

    public static void main(String[] args) throws IOException {

        //TODO: bytes should be read from input
        byte[] fileArray = Files.readAllBytes(Path.of("src\\main\\resources\\feistel2.in"));
        byte[] in = handleMessage(fileArray);
        byte[] out = Files.readAllBytes(Path.of("src\\main\\resources\\feistel2.out"));
//        System.out.println("filearray: " + fileArray);
        System.out.println("\n");
        System.out.write(in);
        System.out.println("\n");
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

    private static byte[] encrypt(byte[] L, byte[] R, byte[] keyBytes, int iterations) {
        byte[] prevL = R;
        byte[] prevR = L;

        for (int i = 0; i < iterations; i++) {
            L = prevR;
            R = prevL;

            // Left is XOR'd, Right passes
            byte[] tempL = new byte[4];
            for (int k = 0; k < 4; k++) {
                tempL[k] = (byte) ( L[k] ^ keyBytes[(i * 4) + k]);
            }
            L = tempL;
            prevL = L;
            prevR = R;
        }

        // Concatenate L and R
        byte[] partResult = new byte[8];
        System.arraycopy(R, 0, partResult, 0, 4);
        System.arraycopy(L, 0, partResult, 4, 4);

        return partResult;
    }

    private static byte[] decrypt(byte[] L, byte[] R, byte[] keyBytes, int iterations) {

        // Determine what's left and what's right depending on iterations
        byte[] prevL = iterations % 2 == 0 ? R : L;
        byte[] prevR = iterations % 2 == 0 ? L : R;

        for (int i = 0; i < iterations; i++) {
            L = prevR;
            R = prevL;

            // Left is XOR'd, Right passes
            byte[] tempL = new byte[4];
            for (int k = 0; k < 4; k++) {
                tempL[k] = (byte) ( L[k] ^ keyBytes[(i * 4) + k]);
            }
            L = tempL;
            prevL = L;
            prevR = R;
        }

        // Concatenate L and R
        byte[] result = new byte[8];
        System.arraycopy(R, 0, result, 0, 4);
        System.arraycopy(L, 0, result, 4, 4);

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

        // Counts how many times 4 fits in key length
        int iterations = keyBytes.length / 4;

        // Amount of separate textParts that should be processed
        int textPartCount = textBytes.length / 8;

        byte[] result = new byte[textBytes.length];

        // Process each part of text
        for (int t = 0; t < textPartCount; t++) {
            // Split textPart into two pieces
            byte[] L = new byte[4];
            byte[] R = new byte[4];
            System.arraycopy(textBytes, (8 * t), L, 0, 4);
            System.arraycopy(textBytes, (8 * t) + 4, R, 0, 4);

            byte[] partResult;

            if (encrypt) {
                partResult = encrypt(L, R, keyBytes, iterations);
            }
            else {
                partResult =  decrypt(L, R, keyBytes, iterations);
            }

            // Append result with partResult
            System.arraycopy(partResult, 0, result, (8 * t), 8);
        }

        return result;
    }
}
