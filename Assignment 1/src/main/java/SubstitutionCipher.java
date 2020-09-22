import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SubstitutionCipher {
    private static String abc = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            //TODO: Println weghalen
            System.out.println("Please enter requests:");
            // Read the line
            //TODO: Newline char in opdracht?
            String requests = br.readLine();
            System.out.println("Please enter plaintext:");

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                //TODO: Op een bepaald punt sluiten
                handleMessage(requests, line);
            }
            br.close();
            //TODO: Programma afsluiten

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void handleMessage(String requests, String plaintext) {

        String result = plaintext;
        String[] reqs = requests.split("\\s+");
        boolean encrypt = true;
        char oldChar;
        char newChar;
        boolean isUpper;

        // Loop through the requests
        for (int i = 0; i < reqs.length; i++) {
            //TODO: i += 2 invoeren
            if (i % 2 == 0){
                // Every even element (e or d)
                if(reqs[i].equals("e")) {
                    System.out.println("Encryption");
                    encrypt = true;
                }
                else if(reqs[i].equals("d")) {
                    System.out.println("Decryption");
                    encrypt = false;
                }
                else {
                    throw new IllegalArgumentException(reqs[i] + " is not a valid encryption direction.");
                }
            }
            else {
                // Every odd element (method)
                char[] arr = result.toCharArray();
                if (reqs[i].matches("-?\\d+")) {
                    // Method is shift
                    System.out.println("Method is shift");

                    int shiftValue = Integer.parseInt(reqs[i]) * (encrypt ? 1 : -1);
                    int oldIndex;
                    int newIndex;

                    // Loop through the text
                    for (int j = 0; j < arr.length; j++) {
                        if (!Character.isLetter(arr[j])) {
                            continue;
                        }
                        isUpper = Character.isUpperCase(arr[j]);
                        oldChar = Character.toLowerCase(arr[j]);

                        oldIndex = abc.indexOf(oldChar);
                        newIndex = (oldIndex + shiftValue + 26) % 26;
                        newChar = abc.toCharArray()[newIndex];

                        arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
                    }
                }
                else {
                    // Method is mapping
                    //TODO 26 char regex?
                    System.out.println("Method is mapping");

                    //TODO variabele namen vervangen
                    String a = encrypt ? abc : reqs[i];
                    String b = encrypt ? reqs[i] : abc;
                    int index;

                    // Loop through the text
                    for (int j = 0; j < arr.length; j++) {
                        if (!Character.isLetter(arr[j])) {
                            continue;
                        }
                        isUpper = Character.isUpperCase(arr[j]);
                        oldChar = Character.toLowerCase(arr[j]);

                        index = a.indexOf(oldChar);
                        newChar = b.toCharArray()[index];

                        arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
                    }
                }
                // Convert back to string
                result = String.valueOf(arr);
                System.out.println(result);
                //TODO: Slechts eenmaal printen naar console
            }
        }
    }
}
