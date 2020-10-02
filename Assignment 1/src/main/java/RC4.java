import java.io.*;

public class RC4 {

    private static final byte[] S = new byte[256];
    private static final byte[] K = new byte[256];

    /***
     * Start a RC4 cipher instance to read bytes from the console and encrypt it with the given arguments
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
            System.out.println(e);
        }
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
     * Handle the input from the console and process RC4 cipher.
     * @param request Input bytes from the console.
     * @throws IOException When printing the result goes wrong.
     */
    private static void handleMessage(byte[] request) throws IOException {
        // Find index of (255) char
        int n = findSeparatorIndex(request);

        byte[] keyBytes = new byte[n];
        byte[] textBytes = new byte[request.length - n - 1];

        System.arraycopy(request, 0, keyBytes, 0, n);
        System.arraycopy(request, n+1, textBytes, 0, request.length - n -1);

        byte[] result = RC4Cipher(textBytes, keyBytes);

        System.out.write(result);
    }

    private static void initializeLookupTable(byte[] keyArray) {
        int N = keyArray.length;
        for (int i = 0; i < 256; i++) {
            S[i] = (byte) i;
            K[i] = keyArray[i % N];
        }
        int j = 0;
        byte temp;
        for (int i = 0; i < 256; i++) {
            j = (j + S[i] + K[i]) & 0xFF;
            temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }
    }

    /***
     * Find the separator character (byte 0xFF) in given input bytes
     * @param bytes Input bytes to find the separator in.
     * @return The index of the separator. Returns -1 if the separator is not found.
     */
    private static int findSeparatorIndex(byte[] bytes) {
        byte target = (byte) 255;
        for (int b = 0; b < bytes.length; b++){
            if (bytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    /***
     * Process RC4 cipher with the given text and key.
     * @param text Text bytes to encrypt
     * @param key Key bytes to use for encrypting
     * @return The encrypted result
     */
    public static byte[] RC4Cipher(byte[] text, byte[] key) {
        byte[] result = new byte[text.length];

        // Initialize lookup table S using the repeating key
        initializeLookupTable(key);

        int i = 0;
        int j = 0;
        int t;
        byte temp;

        // Generate 256 keystream bits that are not used
        for (int k = 0; k < 256; k++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }

        byte keystreamByte;
        // Every character in text is XOR'd with a keystream cipher
        for (int b = 0; b < text.length; b++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            temp = S[i];
            S[i] = S[j];
            S[j] = temp;
            t = (S[i] + S[j]) & 0xFF;
            keystreamByte = S[t];
            result[b] = (byte) (keystreamByte ^ text[b]);
        }

        return result;
    }

}
