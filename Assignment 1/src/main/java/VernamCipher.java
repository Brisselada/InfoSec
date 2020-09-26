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
            handleMessage(request.toString());

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void Vernam() {

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
            handleMessage(request.toString());

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
     * @param request String containing the key and text that should be XOR'd
     */
    private static void handleMessage(String request) throws IOException {

        byte[] reqBytes = request.getBytes();
        int n = reqBytes.length / 2 - 1;

        byte[] textBytes = new byte[n];
        byte[] keyBytes = new byte[n];
        System.arraycopy(reqBytes, 0, keyBytes, 0, n);
        System.arraycopy(reqBytes, n+1, textBytes, 0, n);

        byte[] result = new byte[n];


        for(int i = 0; i < textBytes.length; i++){
            result[i] = (byte) (keyBytes[i] ^ textBytes[i]);
        }
        System.out.write(result);
    }
}
