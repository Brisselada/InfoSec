import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VigenereEncryption {
    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            // Read the line
            String requests = br.readLine();
            StringBuilder plainText = new StringBuilder();
            String input = "";

            do {
                try {
                    input = readLineWithTimeout(br);
                    if (input != null) {
                        plainText.append(input).append("\n");
                    }
                } catch (IOException e) {
                    input = null;
                }
            } while (input != null);

            br.close();

            handleMessage(requests, plainText.toString());

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
     * Encrypt/decrypt using the Vigenere method
     * @param request The key used in the Vigenere method
     * @param plaintext The text that should be encrypted/decrypted
     */
    private static void handleMessage(String request, String plaintext) {

        String abc = "abcdefghijklmnopqrstuvwxyz";
        String result;
        String[] req = request.split("\\s+");

        int encrypt = req[0].equals("e") ? 1 : -1;
        char[] key = req[1].toCharArray();
        int keyCount = 0;

        char oldChar;
        char newChar;
        boolean isUpper;

        char[] arr = plaintext.toCharArray();

        int shiftValue;
        int oldIndex;
        int newIndex;

        // Loop through the text
        for (int j = 0; j < arr.length; j++) {
            if (!Character.isLetter(arr[j])) {
                continue;
            }
            // Shiftvalue is index of current keychar
            shiftValue = abc.indexOf(key[(keyCount % key.length)]) * encrypt;

            isUpper = Character.isUpperCase(arr[j]);
            oldChar = Character.toLowerCase(arr[j]);

            // Determine the new character on this position
            oldIndex = abc.indexOf(oldChar);
            newIndex = (oldIndex + shiftValue + 26) % 26;
            newChar = abc.toCharArray()[newIndex];

            arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
            keyCount++;
        }

        // Convert back to string
        result = String.valueOf(arr);
        System.out.println(result.trim() + '\n');
    }
}
