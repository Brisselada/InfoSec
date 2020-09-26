import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SubstitutionCipherBauke {

    private static final String abc = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String requests = mergeRequests(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                handleMessage(requests, line);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Checks whether the method should use encryption or decryption
     * @param input e or d
     * @return True if encryption, false if decryption
     */
    private static boolean isEncrypt(String input) {
        if (input.equals("e")) {
            return true;
        } else if (input.equals("d")) {
            return false;
        } else {
            throw new IllegalArgumentException(input + " is not a valid encryption direction.");
        }
    }

    /**
     * Encrypts/decrypts the given char[] through the mapping method
     * @param arr The original char[]
     * @param value The value by which should be shifted
     * @param encrypt Specifies encryption/decryption
     * @return The encrypted/decrypted char[]
     */
    private static char[] ShiftMethod(char[] arr, String value, boolean encrypt) {

        double readValue = Double.parseDouble(value);
        double shiftValue = readValue * (encrypt ? 1 : -1);
        boolean isUpper;
        char oldChar;
        char newChar;

        if (shiftValue < 0) {
            double valueToAdd = Math.round(-shiftValue / 26);
            shiftValue += valueToAdd * 26;
        }

        double oldIndex;
        double newIndex;

        // Loop through the text
        for (int j = 0; j < arr.length; j++) {
            if (!Character.isLetter(arr[j])) {
                continue;
            }
            isUpper = Character.isUpperCase(arr[j]);
            oldChar = Character.toLowerCase(arr[j]);

            oldIndex = abc.indexOf(oldChar);
            newIndex = (oldIndex + shiftValue + 26) % 26;
            newChar = abc.toCharArray()[(int) newIndex];

            arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
        }
        return arr;
    }

    /**
     * Encrypts/decrypts the given char[] through the mapping method
     * @param arr The original char[]
     * @param mappingKey The value by which should be shifted
     * @param encrypt Specifies encryption/decryption
     * @return The encrypted/decrypted char[]
     */
    private static char[] MappingMethod(char[] arr, String mappingKey, boolean encrypt) {
        boolean isUpper;
        char oldChar;
        char newChar;

        String fromKey = encrypt ? abc : mappingKey;
        String toKey = encrypt ? mappingKey : abc;
        int index;

        // Loop through the text
        for (int j = 0; j < arr.length; j++) {
            if (!Character.isLetter(arr[j])) {
                continue;
            }
            isUpper = Character.isUpperCase(arr[j]);
            oldChar = Character.toLowerCase(arr[j]);

            index = fromKey.indexOf(oldChar);
            newChar = toKey.toCharArray()[index];

            arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
        }
        return arr;
    }

    /**
     * Merges the given encryption/decryption requests by performing them on the abs
     * @param requests All operation requests bundled in one string
     * @return The resulting encryption operation
     */
    private static String mergeRequests(String requests) {
        String result = abc;
        String[] reqs = requests.split("\\s+");
        boolean encrypt;

        // Loop through the requests
        for (int i = 0; i < reqs.length; i += 2) {
            encrypt = isEncrypt(reqs[i]);
            char[] arr = result.toCharArray();

            if (reqs[i + 1].matches("-?\\d+")) {
                arr = ShiftMethod(arr, reqs[i + 1], encrypt);
            } else {
                arr = MappingMethod(arr, reqs[i + 1], encrypt);
            }
            // Convert back to string
            result = String.valueOf(arr);
        }
        return "e " + result;
    }

    /**
     * Encrypts the given plaintext with the given string of merged requests
     * Prints the result to System.out
     * @param request A merged operation request
     * @param plaintext Text to which request should be applied
     */
    private static void handleMessage(String request, String plaintext) {
        String result = plaintext;
        String[] reqs = request.split("\\s+");
        boolean encrypt = isEncrypt(reqs[0]);
        char[] arr = result.toCharArray();

        if (reqs[1].matches("-?\\d+")) {
            arr = ShiftMethod(arr, reqs[1], encrypt);
        } else {
            arr = MappingMethod(arr, reqs[1], encrypt);
        }

        // Convert back to string
        result = String.valueOf(arr);
        System.out.println(result);
    }
}
