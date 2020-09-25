import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
//            String requests = mergeOperations(br.readLine());
            String requests = mergeRequests(br.readLine());

            // WITHOUT MERGE
//             String requests = br.readLine();

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
                        myShiftvalue = myShiftvalue + shiftValue;
                    } else {
                        myShiftvalue = (myShiftvalue - shiftValue);
                    }

                    if (myShiftvalue < 0) {
                        double valueToAdd = Math.floor(-myShiftvalue / 26);
                        myShiftvalue += valueToAdd * 26;
                    } else {
                        myShiftvalue = myShiftvalue % 26;
                    }

                    nextIndex += 2;
                }

                result.add(reqs[i] + " " + (int) myShiftvalue);
                i = nextIndex - 3;


            } else {

                // Convert initial abc to indices
                int[] firstKeyIndices = mapLettersToIndices(abc, abc);
                int[] nextKeyIndices = mapLettersToIndices(reqs[i + 1], abc);
                boolean mergeEncrypt = isEncrypt(reqs[i]);

                int[] resultKeyIndices = getResultKeyIndices(firstKeyIndices, nextKeyIndices, mergeEncrypt);

                // Check method of next en/decryption
                int nextIndex = i + 3;
                // merge while no numbers found or out of range
                while (nextIndex < reqs.length && !reqs[nextIndex].matches("-?\\d+")) {
                    mergeEncrypt = isEncrypt(reqs[nextIndex - 1]);
//                    String mergeLetters = reqs[nextIndex];
                    // Convert mapping key to indices as found in the alphabet
                    nextKeyIndices = mapLettersToIndices(reqs[nextIndex], abc);
                    resultKeyIndices = getResultKeyIndices(resultKeyIndices, nextKeyIndices, mergeEncrypt);

                    nextIndex += 2;
                }
                // i jumps to last found mapping method
                i = nextIndex - 3;

                String newKey = mapIndicesToLetters(resultKeyIndices);
//                result.add(reqs[i] + " " + newKey);
                result.add("e " + newKey);
            }
        }

        return result.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    private static int[] getResultKeyIndices(int[] firstKeyIndices, int[] nextKeyIndices, boolean mergeEncrypt) {

        int[] plainTextArray = firstKeyIndices;
        int[] abcArray = mapLettersToIndices(abc, abc);

        int[] fromKey = mergeEncrypt ? abcArray : nextKeyIndices;
        int[] toKey = mergeEncrypt ? nextKeyIndices : abcArray;

        int[] resultKeyIndices = new int[26];

        for (int t = 0; t < plainTextArray.length; t++) {
            int oldIndex = plainTextArray[t];
            int foundIndex = 0;
            for (int f = 0; f < 26; f++) {
                if (fromKey[f] == oldIndex) {
                    foundIndex = f;
                }
            }
            resultKeyIndices[t] = toKey[foundIndex];
        }

        return resultKeyIndices;
    }

    private static String mapIndicesToLetters (int[] indices) {
        char[] letterArray = new char[indices.length];

        for (int i = 0; i < indices.length; i ++) {
            letterArray[i] = abc.toCharArray()[indices[i]];
        }

        return String.valueOf(letterArray);
    }


    // index of letters a in b
    // Generally used to get the index of each letter in a in (alphabet) b
    private static int[] mapLettersToIndices(String a, String b) {
        int[] indices = new int[a.length()];
        char[] aArray = a.toCharArray();

        for (int i = 0; i < a.length(); i ++) {
            indices[i] = b.indexOf(aArray[i]);
        }

        return  indices;
    }

    /**
     *
     * @param input
     * @return
     */
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

    /**
     *
     * @param arr
     * @param value
     * @param encrypt
     * @return
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

    private static char[] MappingMethod(char[] arr, String mappingKey, boolean encrypt) {

        boolean isUpper;
        char oldChar;
        char newChar;

        //TODO variabele namen vervangen
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

    private static String mergeRequests(String requests) {

        String result = abc;
        String[] reqs = requests.split("\\s+");
        boolean encrypt;
//        char oldChar;
//        char newChar;
//        boolean isUpper;

        // Loop through the requests
        for (int i = 0; i < reqs.length; i += 2) {
            encrypt = isEncrypt(reqs[i]);

            // Every odd element (method)
            char[] arr = result.toCharArray();

            if (reqs[i + 1].matches("-?\\d+")) {
                // Method is shift
//                System.out.println("Method is shift");

                arr = ShiftMethod(arr, reqs[i + 1], encrypt);

//                double readValue = Double.parseDouble(reqs[i + 1]);
//                double shiftValue = readValue * (encrypt ? 1 : -1);
//
//                if (shiftValue < 0) {
//                    double valueToAdd = Math.round(-shiftValue / 26);
//                    shiftValue += valueToAdd * 26;
//                }
//
//                double oldIndex;
//                double newIndex;
//
//                // Loop through the text
//                for (int j = 0; j < arr.length; j++) {
//                    if (!Character.isLetter(arr[j])) {
//                        continue;
//                    }
//                    isUpper = Character.isUpperCase(arr[j]);
//                    oldChar = Character.toLowerCase(arr[j]);
//
//                    oldIndex = abc.indexOf(oldChar);
//                    newIndex = (oldIndex + shiftValue + 26) % 26;
//                    newChar = abc.toCharArray()[(int) newIndex];
//
//                    arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
//                }
            } else {

                arr = MappingMethod(arr, reqs[i + 1], encrypt);
//                // Method is mapping
//                //TODO 26 char regex?
////                System.out.println("Method is mapping");
//
//                //TODO variabele namen vervangen
//                String a = encrypt ? abc : reqs[i + 1];
//                String b = encrypt ? reqs[i + 1] : abc;
//                int index;
//
//                // Loop through the text
//                for (int j = 0; j < arr.length; j++) {
//                    if (!Character.isLetter(arr[j])) {
//                        continue;
//                    }
//                    isUpper = Character.isUpperCase(arr[j]);
//                    oldChar = Character.toLowerCase(arr[j]);
//
//                    index = a.indexOf(oldChar);
//                    newChar = b.toCharArray()[index];
//
//                    arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
//                }
            }
            // Convert back to string
            result = String.valueOf(arr);

        }
        return "e " + result;
    }

    private static void handleMessage(String requests, String plaintext) {

        String result = plaintext;
        String[] reqs = requests.split("\\s+");
        boolean encrypt = true;
//        char oldChar;
//        char newChar;
//        boolean isUpper;

        // Loop through the requests
        for (int i = 0; i < reqs.length; i += 2) {
            encrypt = isEncrypt(reqs[i]);

            // Every odd element (method)
            char[] arr = result.toCharArray();

            if (reqs[i + 1].matches("-?\\d+")) {
                // Method is shift
//                System.out.println("Method is shift");

                arr = ShiftMethod(arr, reqs[i + 1], encrypt);

//                double readValue = Double.parseDouble(reqs[i + 1]);
//                double shiftValue = readValue * (encrypt ? 1 : -1);
//
//                if (shiftValue < 0) {
//                    double valueToAdd = Math.round(-shiftValue / 26);
//                    shiftValue += valueToAdd * 26;
//                }
//
//                double oldIndex;
//                double newIndex;
//
//                // Loop through the text
//                for (int j = 0; j < arr.length; j++) {
//                    if (!Character.isLetter(arr[j])) {
//                        continue;
//                    }
//                    isUpper = Character.isUpperCase(arr[j]);
//                    oldChar = Character.toLowerCase(arr[j]);
//
//                    oldIndex = abc.indexOf(oldChar);
//                    newIndex = (oldIndex + shiftValue + 26) % 26;
//                    newChar = abc.toCharArray()[(int) newIndex];
//
//                    arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
//                }
            } else {

                arr = MappingMethod(arr, reqs[i + 1], encrypt);
//                // Method is mapping
//                //TODO 26 char regex?
////                System.out.println("Method is mapping");
//
//                //TODO variabele namen vervangen
//                String a = encrypt ? abc : reqs[i + 1];
//                String b = encrypt ? reqs[i + 1] : abc;
//                int index;
//
//                // Loop through the text
//                for (int j = 0; j < arr.length; j++) {
//                    if (!Character.isLetter(arr[j])) {
//                        continue;
//                    }
//                    isUpper = Character.isUpperCase(arr[j]);
//                    oldChar = Character.toLowerCase(arr[j]);
//
//                    index = a.indexOf(oldChar);
//                    newChar = b.toCharArray()[index];
//
//                    arr[j] = isUpper ? Character.toUpperCase(newChar) : newChar;
//                }
            }
            // Convert back to string
            result = String.valueOf(arr);

            if (reqs.length == i + 2) {
                System.out.println(result);
            }

        }
    }
}
