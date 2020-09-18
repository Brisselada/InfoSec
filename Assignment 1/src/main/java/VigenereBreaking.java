import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class VigenereBreaking {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 20000);
        try {
            System.out.println("Please enter lower bound of key size range:");
            int lowerBound = Integer.parseInt(br.readLine());

            System.out.println("Please enter upper bound of key size range:");
            int upperBound = Integer.parseInt(br.readLine());

            System.out.println("Please enter plaintext:");
            //TODO: Grote input handlen
//            String cipherText = br.readLine();
//            String cipherText = "";
            String cipherText = Files.readString(Path.of("src\\main\\resources\\textToBreak.txt"), StandardCharsets.US_ASCII);

            //            for (String line = br.readLine(); line != null; line = br.readLine()) {
//                //TODO: Op een bepaald punt sluiten
//                System.out.println("Line is " + line);
//                cipherText += line;
//            }
            System.out.println("Finished reading");
            guessKey(lowerBound, upperBound, cipherText);
            br.readLine();

//            for (String line = br.readLine(); line != null; line = br.readLine()) {
//                //TODO: Op een bepaald punt sluiten
//                handleMessage(requests, line);
//            }

            br.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void guessKey(int lowerBound, int upperBound, String cipherText) {
        // Voor elke sleutellengte: per positie een vector van 26 met frequency voor elke letter
        //
        String abc = "abcdefghijklmnopqrstuvwxyz";
        char[] arr = cipherText.toCharArray();
        int keyCount = 0;
        int charIndex;
        char currentChar;
        int bestKeyLength = 0;
        double highestSumStdDev = 0;
        int[][] bestKeyDoubleArray = new int[1][26]; //TODO: Bad initialization

        for (int keyLength = lowerBound; keyLength <= upperBound; keyLength++) {

            double totalStdDev = 0;

            int[][] doubleArray = new int[keyLength][26];

            // Count letter frequency per key position
            for (int j = 0; j < arr.length; j++) {
                if (!Character.isLetter(arr[j]) || arr[j] == '\r' || arr[j] == '\n') {
//                    j++;
                    continue;
                }
                currentChar = Character.toLowerCase(arr[j]);
                charIndex = abc.indexOf(currentChar);


                doubleArray[keyCount % keyLength][charIndex]++;
                keyCount++;
            }

            // Calculate std dev for this keylength
            for (int k = 0; k < keyLength; k++) {
                int sumExp = 0;
                int sum = 0;
                for (int h = 0; h < 26; h++) {
                    sumExp += doubleArray[k][h] * doubleArray[k][h];
                    sum += doubleArray[k][h];
                }
                double stdDev = Math.sqrt(((double)sumExp / 26.0) - Math.pow(((double)sum / 26.0), 2));
                totalStdDev += stdDev;
            }
            if (totalStdDev > highestSumStdDev) {
                highestSumStdDev = totalStdDev;
                bestKeyLength = keyLength;
                bestKeyDoubleArray = doubleArray;
            }
            System.out.println("The sum of " + keyLength + " std. devs: " + Math.round(totalStdDev * 100.00)/ 100.00);
        }

        char[] guessedKeyArray = new char[bestKeyLength];

        // Guess shift for each key position
        for (int l = 0; l < bestKeyLength; l++) {
            int maxFreq = 0;
            int maxFreqIndex = 0;

            // Get highest value of key position
            for (int m = 0; m < 26; m++) {
                if (bestKeyDoubleArray[l][m] > maxFreq) {
                    maxFreq = bestKeyDoubleArray[l][m];
                    maxFreqIndex = m;
                }
            }

            int likelyIndex = (maxFreqIndex + 21) % 26;
            guessedKeyArray[l] = abc.toCharArray()[likelyIndex];
        }

        System.out.println("\nKey guess:");
        System.out.println(String.valueOf(guessedKeyArray));
    }
}
