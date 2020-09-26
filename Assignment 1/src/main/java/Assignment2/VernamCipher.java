package Assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VernamCipher {

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            // Read the line
            StringBuilder request = new StringBuilder();
            request.append(br.readLine());
            String input = "";

            do {
                try {
                    input = readLineWithTimeout(br);
                    if (input != null) {
                        request.append(input).append("\n");
                    }
                } catch (IOException e) {
                    input = null;
                }
            } while (input != null);

            br.close();

            String[] reqArray = request.toString().split("ÿ");
            String key = reqArray[0];
            String text = reqArray[1];

            handleMessage(key, text);

        } catch (Exception e) {
            System.out.println(e);
        }
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

    /**
     * Encrypts the given String text through and XOR operation with given String key
     * @param key The key that should be used for the XOR operation
     * @param text The text that should be encrypted/decrypted
     */
    private static void handleMessage(String key, String text) {

        byte[] textBytes = text.getBytes();
        byte[] keyBytes = key.getBytes();
        byte[] result = new byte[textBytes.length];

        for(int i = 0; i < textBytes.length; i++){
            result[i] = (byte) (keyBytes[i] ^ textBytes[i]);
        }
        System.out.println(result);
    }
}
