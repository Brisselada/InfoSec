import java.io.*;

public class Feistel {

    /***
     * Start a feistel cipher instance to read bytes from the console and encrypt it with the given arguments
     * @param args The arguments for encrypting
     */
    public static void main(String[] args) {
        try {
            int nRead;
            byte[] data = new byte[16384];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            while ((nRead = readLineWithTimeout(data, data.length, 0.2)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] requestBytes = buffer.toByteArray();

            handleMessage(requestBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Handle the input from the console and process feistel cipher.
     * @param request Input bytes from the console.
     * @throws IOException When printing the result goes wrong.
     */
    private static void handleMessage(byte[] request) throws IOException {

        // Check encryption or decryption
        boolean encrypt = request[0] == (byte) 'e';
        int n = findSeparatorIndex(request);

        // Get key and text from request
        byte[] keyBytes = new byte[n - 2];
        byte[] textBytes = new byte[request.length - n - 1];
        System.arraycopy(request, 2, keyBytes, 0, n - 2);
        System.arraycopy(request, n+1, textBytes, 0, request.length - n - 1);

        byte[] result = FeistelCipher(textBytes, keyBytes, encrypt);
        System.out.write(result);
    }

    /***
     * Reads the line with a given timeout.
     * @param buffer The buffer to write the bytes to.
     * @param nRead Total number of bytes to read.
     * @param timeoutSeconds Timeout in seconds.
     * @return Returns the total number of bytes that have been read and -1 if no bytes where read.
     * @throws IOException When reading the input goes wrong.
     */
    private static int readLineWithTimeout(byte[] buffer, int nRead, double timeoutSeconds) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000 && !br.ready()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (br.ready()) {
            return System.in.read(buffer, 0, nRead);
        } else {
            return -1;
        }
    }

    /***
     * Find the separator character (byte 0xFF) in given input bytes
     * @param bytes Input bytes to find the separator in.
     * @return The index of the separator. Returns -1 if the separator is not found.
     */
    private static int findSeparatorIndex(byte[] bytes) {
        byte target = (byte) 0xFF;
        for (int b = 2; b < bytes.length; b++){
            if (bytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    /***
     * Processes the feistel rounds based on the given iterations
     * @param L Left side of the bytes.
     * @param R Right side of the bytes.
     * @param keyBytes The key that will be XOR'd.
     * @param iterations The amount of iterations.
     * @param encrypt True if the text has to be encrypted. False if it has to be decrypted.
     * @return the result part.
     */
    private static byte[] processRounds(byte[] L, byte[] R, byte[] keyBytes, int iterations, boolean encrypt) {

        // Determine what's left and what's right depending on encrypt or decrypt and iterations
        byte[] prevL = encrypt ? R : (iterations % 2 == 0 ? R : L);
        byte[] prevR = encrypt ? L : (iterations % 2 == 0 ? L : R);

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

    /***
     * Process a feistel cipher on the given input text with the given key.
     * @param text Input text to decrypt or encrypt.
     * @param key Input key used for encryption or decryption.
     * @param encrypt True if the text has to be encrypted. False if it has to be decrypted.
     * @return The result bytes.
     */
    public static byte[] FeistelCipher(byte[] text, byte[] key, boolean encrypt) {

        // Counts how many times 4 fits in key length
        int iterations = key.length / 4;

        // Amount of separate textParts that should be processed
        int textPartCount = text.length / 8;

        byte[] result = new byte[text.length];

        // Process each part of text
        for (int t = 0; t < textPartCount; t++) {
            // Split textPart into two pieces
            byte[] L = new byte[4];
            byte[] R = new byte[4];
            System.arraycopy(text, (8 * t), L, 0, 4);
            System.arraycopy(text, (8 * t) + 4, R, 0, 4);

            byte[] partResult;

            partResult = processRounds(L, R, key, iterations, encrypt);

            // Append result with partResult
            System.arraycopy(partResult, 0, result, (8 * t), 8);
        }

        return result;
    }
}
