import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
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

            StringBuilder cipherText = new StringBuilder();
            String input = "";

            do {
                try {
                    input = VigenereBreaking.readLineWithTimeout(br);
                    if (input != null) {
                        cipherText.append(input).append("\n");
                    }
                } catch (IOException e) {
                    input = null;
                }
            } while (input != null);



            //TODO: Grote input handlen van console
//            String cipherText = br.readLine();
            //TODO: Readall hier implementeren? Textfile is nu tijdelijke oplossing
//            String cipherText = Files.readString(Path.of("src\\main\\resources\\textToBreak.txt"), StandardCharsets.US_ASCII);

            //            for (String line = br.readLine(); line != null; line = br.readLine()) {
//                //TODO: Op een bepaald punt sluiten
//                System.out.println("Line is " + line);
//                cipherText += line;
//            }
            guessKey(lowerBound, upperBound, cipherText.toString());
            br.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private static String readLineWithTimeout(BufferedReader in) throws IOException {

        int x = 3; // wait 3 seconds at most

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

    public static void guessKey(int lowerBound, int upperBound, String cipherText) {
        String abc = "abcdefghijklmnopqrstuvwxyz";
        char[] arr = cipherText.toCharArray();
        int charIndex;
        char currentChar;
        int bestKeyLength = 0;
        double highestSumStdDev = 0;
        int[][] bestKeyDoubleArray = new int[1][26];

        for (int keyLength = lowerBound; keyLength <= upperBound; keyLength++) {

            int keyCount = 0;
            double totalStdDev = 0;

            int[][] doubleArray = new int[keyLength][26];

            // Count letter frequency per key position
            for (int j = 0; j < arr.length; j++) {
                if (!Character.isLetter(arr[j]) || arr[j] == '\r' || arr[j] == '\n') {
                    continue;
                }
                currentChar = Character.toLowerCase(arr[j]);
                charIndex = abc.indexOf(currentChar);

                doubleArray[keyCount % keyLength][charIndex]++;
                keyCount++;
            }

            // Calculate std dev for this keyLength
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

            String stdDevsPercentage = String.format("%.02f", Math.round(totalStdDev * 100.00)/ 100.00);
            System.out.println("The sum of " + keyLength + " std. devs: " + stdDevsPercentage);
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
//            System.out.println("Most frequent letter:" + abc.toCharArray()[maxFreqIndex]);
            int likelyIndex = (maxFreqIndex + 22) % 26;
            guessedKeyArray[l] = abc.toCharArray()[likelyIndex];
        }

        System.out.println("\nKey guess:");
        System.out.println(String.valueOf(guessedKeyArray));
    }
}
