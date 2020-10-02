import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Vernam {

    /***
     * Start a Vernam cipher instance to read bytes from the console and encrypt it with the given arguments
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
     * Handle the input from the console and process feistel cipher.
     * @param request Input bytes from the console.
     * @throws IOException When printing the result goes wrong.
     */
    private static void handleMessage(byte[] request) throws IOException {
        int n = findSeparatorIndex(request);

        byte[] textBytes = new byte[n];
        byte[] keyBytes = new byte[n];
        System.arraycopy(request, 0, keyBytes, 0, n);
        System.arraycopy(request, n+1, textBytes, 0, n);

        byte[] result = new byte[n];


        for(int i = 0; i < textBytes.length; i++){
            result[i] = (byte) (keyBytes[i] ^ textBytes[i]);
        }

        System.out.write(result);
    }

    /***
     * Find the separator character (byte 0xFF) in given input bytes
     * @param bytes Input bytes to find the separator in.
     * @return The index of the separator. Returns -1 if the separator is not found.
     */
    private static int findSeparatorIndex(byte[] bytes) {
        byte target = (byte) 0xFF;
        for (int b = 0; b < bytes.length; b++){
            if (bytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    /***
     * Process Vernam cipher on the given input text and key.
     * @param text The text to encrypt
     * @param key The key to use when encrypting.
     * @return The encrypted result.
     */
    public static byte[] VernamCipher(byte[] text, byte[] key) {
        byte[] result = new byte[text.length];

        for(int i = 0; i < text.length; i++){
            result[i] = (byte) (key[i] ^ text[i]);
        }
        return result;
    }
}
