import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VigenereEncryption {
    private static final String abc = "abcdefghijklmnopqrstuvwxyz";

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

            handleInput(requests, plainText.toString());

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

    /***
     * Handle the input from the command line
     * @param requests The request string input containing the operations
     * @param plainText The input text that should me encrypted or decrypted.
     */
    private static void handleInput(String requests, String plainText) {
        String[] req = requests.split("\\s+");
        boolean encrypt = req[0].equals("e");
        String key = req[1];

        String result = EncryptVigenere(plainText, key, encrypt);
        System.out.println(result);
    }

    /**
     * Encrypt/decrypt using the Vigenere method
     * @param inputText The text that should be encrypted or decrypted
     * @param key The key that is used for encrypting or decrypting the inputText
     */
    public static String EncryptVigenere(String inputText, String key, boolean encrypt) {
        String result;

        int encryptMultiplier = encrypt ? 1 : -1;
        char[] keyArray = key.toCharArray();
        int keyCount = 0;

        char oldChar;
        char newChar;
        boolean isUpper;

        char[] arr = inputText.toCharArray();

        int shiftValue;
        int oldIndex;
        int newIndex;

        // Loop through the text
        for (int j = 0; j < arr.length; j++) {
            if (!Character.isLetter(arr[j])) {
                continue;
            }
            // Shiftvalue is index of current keychar
            shiftValue = abc.indexOf(keyArray[(keyCount % keyArray.length)]) * encryptMultiplier;

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
        return result.trim() + '\n';
    }
}
