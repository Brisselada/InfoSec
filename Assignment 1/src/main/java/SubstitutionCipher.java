import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubstitutionCipher {
    private static String abc = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            //TODO: Println weghalen
//            System.out.println("Please enter requests:");
            // Read the line
            //TODO: Newline char in opdracht?


            // WITH MERGE
            String requests = mergeOperations(br.readLine());

            // WITHOUT MERGE
            // String requests = br.readLine();

//            System.out.println("Please enter plaintext:");
            String line;
            while ((line = br.readLine()) != null) {
                //TODO: Op een bepaald punt sluiten
                handleMessage(requests, line);
            }

//            for (String line = br.readLine(); line != null; line = br.readLine()) {
//                //TODO: Op een bepaald punt sluiten
//                handleMessage(requests, line);
//            }
            br.close();
            //TODO: Programma afsluiten

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private static String mergeOperations(String requests) {
        //  [ "e", "100", "e", "abcasdfaers", "d", "100" ]
        String[] reqs = requests.split("\\s+");
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < reqs.length; i += 2) {
            boolean encrypt = isEncrypt(reqs[i]);
            if (reqs[i + 1].matches("-?\\d+")) {

                double myShiftvalue = Double.parseDouble(reqs[i + 1]);

                int nextIndex = i + 3;
                // merge while numbers found
                while (nextIndex < reqs.length && reqs[nextIndex].matches("-?\\d+")) {
                    boolean mergeEncrypt = isEncrypt(reqs[nextIndex - 1]);
                    double shiftValue = Double.parseDouble(reqs[nextIndex]);

                    if (mergeEncrypt == encrypt) {
                        myShiftvalue = (myShiftvalue + shiftValue) % 26;
                    } else {
                        myShiftvalue = (myShiftvalue - shiftValue);
                    }

                    if (myShiftvalue < 0) {
                        double valueToAdd = Math.floor(-myShiftvalue / 26);
                        myShiftvalue += valueToAdd * 26;
                    }

                    nextIndex += 2;
                }

                result.add(reqs[i] + " " + (int) myShiftvalue);

                i = i + nextIndex - 3;
            } else {
                result.add(reqs[i] + " " + reqs[i + 1]);
            }
        }

        return result.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    private static boolean isEncrypt(String input) {
        if (input.equals("e")) {
//                System.out.println("Encryption");
            return true;
        } else if (input.equals("d")) {
//                System.out.println("Decryption");
            return false;
        } else {
            throw new IllegalArgumentException(input + " is not a valid encryption direction.");
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
        for (int i = 0; i < reqs.length; i += 2) {
            encrypt = isEncrypt(reqs[i]);

            // Every odd element (method)
            char[] arr = result.toCharArray();

            if (reqs[i + 1].matches("-?\\d+")) {
                // Method is shift
//                System.out.println("Method is shift");

                double readValue = Double.parseDouble(reqs[i + 1]);
                double shiftValue = readValue * (encrypt ? 1 : -1);

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
            } else {
                // Method is mapping
                //TODO 26 char regex?
//                System.out.println("Method is mapping");

                //TODO variabele namen vervangen
                String a = encrypt ? abc : reqs[i + 1];
                String b = encrypt ? reqs[i + 1] : abc;
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

            if (reqs.length == i + 2) {
                System.out.println(result);
            }

        }
    }
}
