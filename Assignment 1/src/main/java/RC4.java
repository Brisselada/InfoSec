import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RC4 {

    private static final byte[] S = new byte[256];
    private static final byte[] K = new byte[256];

    public static void main(String[] args) throws IOException {
        //TODO IOException moet later weg
//        String cipherText = Files.readString(Path.of("src\\main\\resources\\2.txt"), StandardCharsets.US_ASCII);
//        System.out.println(cipherText);

        try {
            int nRead;
            byte[] data = new byte[16384];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            while ((nRead = readLineWithTimeout(data, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] requestBytes = buffer.toByteArray();

//            requestBytes = Files.readAllBytes(Path.of("src\\main\\resources\\vernam0.in"));
            byte[] result = handleMessage(requestBytes);
            System.out.write(result);

//            inputStream.close();

        } catch (Exception e) {
            System.out.println(e);
        }


//        byte[] fileArray = Files.readAllBytes(Path.of("src\\main\\resources\\0.in"));
//        byte[] in = handleMessage(fileArray);
//        byte[] out = Files.readAllBytes(Path.of("src\\main\\resources\\RC40.out"));
//        System.out.println("filearray: " + fileArray);
//        System.out.println("\nin: ");
//        System.out.write(in);
//        System.out.println("\nout: ");
//        System.out.write(out);





//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            // Read the line
//            StringBuilder request = new StringBuilder();
//            request.append(br.readLine()).append("\n");
//            String input = "";
//
//            do {
//                try {
//                    input = readLineWithTimeout(br);
//                    if (input != null) {
//                        request.append(input).append("\n");
//                    }
//                } catch (IOException e) {
//                    input = null;
//                }
//            } while (input != null);
//
//            br.close();
//            handleMessage(request.toString());
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
    }


    /**
     * Reads the line with a timeout
     * @return Returns the line that has been read
     * @throws IOException
     */
    private static int readLineWithTimeout(byte[] buffer, int nRead) throws IOException {
        int x = 1; // wait 1 second at most

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < x * 1000 && !br.ready()) {
            // wait
        }
        if (br.ready()) {
            return System.in.read(buffer, 0, nRead);
        } else {
            return -1;
        }
    }

//    /**
//     * Reads the line with a timeout
//     * @param in The supplied BufferedReader
//     * @return Returns the line that has been read
//     * @throws IOException
//     */
//    private static String readLineWithTimeout(BufferedReader in) throws IOException {
//        int x = 1; // wait 1 second at most
//
//        long startTime = System.currentTimeMillis();
//        while ((System.currentTimeMillis() - startTime) < x * 1000 && !in.ready()) {
//            // wait
//        }
//        if (in.ready()) {
//            return in.readLine();
//        } else {
//            return null;
//        }
//    }

    private static byte[] initializeLookupTable(byte[] keyArray) {

        int N = keyArray.length;
        for (int i = 0; i < 256; i++) {
            S[i] = (byte) i;
            K[i] = keyArray[i % N];
        }
        int j = 0;
        byte temp;
        for (int i = 0; i < 256; i++) {
//            j = (j + S[i] + K[i]) & 0xFF;
            j = (j + S[i] + K[i]) & 0xFF;
            temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }

        return S;
    }

    private static int findSeparatorIndex(byte[] reqBytes) {
        byte target = (byte) 255;
        for (int b = 0; b < reqBytes.length; b++){
            if (reqBytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    private static byte[] handleMessage(byte[] request) throws IOException {

        byte[] reqBytes = request;

        // Find index of (255) char
        int n = findSeparatorIndex(reqBytes);

        byte[] keyBytes = new byte[n];
        byte[] textBytes = new byte[reqBytes.length - n - 1];

        System.arraycopy(reqBytes, 0, keyBytes, 0, n);
        System.arraycopy(reqBytes, n+1, textBytes, 0, reqBytes.length - n -1);
        byte[] result = new byte[textBytes.length];

        // Initialize lookup table S using the repeating key
//        byte[] S = initializeLookupTable(keyBytes);
        initializeLookupTable(keyBytes);

        int i = 0;
        int j = 0;
        int t;
        byte temp;

        // Generate 256 keystream bits that are not used
        for (int k = 0; k < 256; k++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }

        byte keystreamByte;
        // Every character in text is XOR'd with a keystream cipher
        for (int b = 0; b < textBytes.length; b++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            temp = S[i];
            S[i] = S[j];
            S[j] = temp;
            t = (S[i] + S[j]) & 0xFF;
            keystreamByte = S[t];
            result[b] = (byte) (keystreamByte ^ textBytes[b]);
        }
//
//
//        for(int i = 0; i < textBytes.length; i++){
//            result[i] = (byte) (keyBytes[i] ^ textBytes[i]);
//        }
//        System.out.write(result);
        return result;
    }


//    private static void handleMessage(String request) throws IOException {
//
//        byte[] reqBytes = request.getBytes();
//
//        // Find index of (255) char
//        int n = findSeparatorIndex(reqBytes);
//
//        byte[] keyBytes = new byte[n];
//        byte[] textBytes = new byte[reqBytes.length - n];
//
//        System.arraycopy(reqBytes, 0, keyBytes, 0, n);
//        System.arraycopy(reqBytes, n+1, textBytes, 0, reqBytes.length - n);
//        byte[] result = new byte[reqBytes.length - n];
//
//        // Initialize lookup table S using the repeating key
//        int[] S = initializeLookupTable(keyBytes);
//
//        // Generate 256 keystream bits that are not used
//        int i = 0;
//        int j = 0;
//        int t;
//        int temp;
//        for (int k = 0; k <= 255; k++) {
//            i = (i + 1) % 256;
//            j = (j + S[i]) % 256;
//            temp = S[i];
//            S[i] = S[j];
//            S[j] = temp;
//        }
//
//        // Now S is ready for use
//        i = 0;
//        j = 0;
//        byte keystreamByte;
//        // Every character in text is XOR'd with a keystream cipher
//        for (int b = 0; b < textBytes.length; b++) {
//            i = (i + 1) % 256;
//            j = (j + S[i]) % 256;
//            temp = S[i];
//            S[i] = S[j];
//            S[j] = temp;
//            t = (S[i] + S[j]) % 256;
//            keystreamByte = (byte) S[t];
//            result[b] = (byte) (keystreamByte ^ textBytes[b]);
//        }
////
////
////        for(int i = 0; i < textBytes.length; i++){
////            result[i] = (byte) (keyBytes[i] ^ textBytes[i]);
////        }
//        System.out.write(result);
//    }
}
