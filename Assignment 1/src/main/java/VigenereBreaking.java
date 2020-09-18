import java.io.BufferedReader;
import java.io.InputStreamReader;

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
            String cipherText = "";
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                //TODO: Op een bepaald punt sluiten
                System.out.println("Line is " + line);
                cipherText += line;
            }
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

        for (int keyLength = lowerBound; keyLength <= upperBound; keyLength++) {

            double totalStdDev = 0;

            int[][] doubleArray = new int[keyLength][26];

            for (int j = 0; j < arr.length; j++) {
                if (!Character.isLetter(arr[j])) {
                    continue;
                }
                currentChar = Character.toLowerCase(arr[j]);
                charIndex = abc.indexOf(currentChar);

                doubleArray[keyCount % keyLength][charIndex]++;
                keyCount++;
            }

            for (int k = 0; k < keyLength; k++) {
                int sumExp = 0;
                int sum = 0;
                for (int h : doubleArray[k]) {
                    sumExp += doubleArray[k][h] * doubleArray[k][h];
                    sum += doubleArray[k][h];
                }
                double stdDev = Math.sqrt(sumExp / (26 - Math.pow((sum / 26), 2)));
                totalStdDev += stdDev;
            }
            System.out.println("The sum of " + keyLength + " std. devs: " + totalStdDev);
        }
    }
}
