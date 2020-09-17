import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VigenereEncryption {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Please enter requests:");
            // Read the line
            //TODO: Newline char in opdracht?
            String requests = br.readLine();
            System.out.println("Please enter plaintext:");
            String plaintext = br.readLine();
            handleMessage(requests, plaintext);

            br.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void handleMessage(String request, String plaintext) {

        String abc = "abcdefghijklmnopqrstuvwxyz";
        String result;
        String[] req = request.split("\\s+");

        //TODO Kan veiliger/generieker
        int encrypt = req[0].equals("e") ? 1 : -1;
        char[] key = req[1].toCharArray();;
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
            // Shiftvalue is index van huidige keychar
            shiftValue = abc.indexOf(key[(keyCount % key.length)]) * encrypt;

            isUpper = Character.isUpperCase(arr[j]);
            oldChar = Character.toLowerCase(arr[j]);

            oldIndex = abc.indexOf(oldChar);
            newIndex = (oldIndex + shiftValue + 26) % 26;
            newChar = abc.toCharArray()[newIndex];

            arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
            keyCount++;
        }

        // Convert back to string
        result = String.valueOf(arr);
        System.out.println(result);

    }
}
