import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Feistel {

    public static void main(String[] args) throws IOException {

        //TODO: OPTIE 1: Proberen deze werkende te krijgen, loopt nu vast

        try {

            int nRead;
            byte[] data = new byte[16384];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            while ((nRead = readLineWithTimeout(data, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] requestBytes = buffer.toByteArray();

//            byte[] fileArray = Files.readAllBytes(Path.of("src\\main\\resources\\feistel2.in"));
            byte[] result = handleMessage(requestBytes);
            System.out.write(result);

//            inputStream.close();

        } catch (Exception e) {
            System.out.println(e);
        }



//TODO: OPTIE 2: Pakt separator teken niet goed op met String -> byte[]

//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            // Read the line
//            StringBuilder request = new StringBuilder();
//            request.append(br.readLine());
//            String input = "";
//
//            do {
//                try {
//                    input = readLineWithTimeout(br);
//                    if (input != null) {
//                        request.append(input);
//                    }
//                } catch (IOException e) {
//                    input = null;
//                }
//            } while (input != null);
//
////            request.append("\n");
//
//            br.close();
//            String builderResult = request.toString();
//            byte[] requestBytes = builderResult.getBytes();
//            byte[] result = handleMessage(requestBytes);
//            System.out.write(result);
//
////            byte[] fileArray = Files.readAllBytes(Path.of("src\\main\\resources\\feistel2.in"));
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }



          //TODO: OPTIE 3: Dit is de oude oplossing die werkte
        //TODO: bytes should be read from input
//        byte[] fileArray = Files.readAllBytes(Path.of("src\\main\\resources\\feistel2.in"));
//        byte[] in = handleMessage(fileArray);
//        byte[] out = Files.readAllBytes(Path.of("src\\main\\resources\\feistel2.out"));
////        System.out.println("filearray: " + fileArray);
//        System.out.println("\n");
//        System.out.write(in);
//        System.out.println("\n");
//        System.out.write(out);


    }

    /**
     * Reads the line with a timeout
     * @return Returns the line that has been read
     * @throws IOException
     */
    private static int readLineWithTimeout(byte[] buffer, int nRead) throws IOException {
        int x = 4; // wait 1 second at most

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


    private static int findSeparatorIndex(byte[] reqBytes) {
        byte target = (byte) 0xFF;
        for (int b = 2; b < reqBytes.length; b++){
            if (reqBytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    private static byte[] encrypt(byte[] L, byte[] R, byte[] keyBytes, int iterations) {
        byte[] prevL = R;
        byte[] prevR = L;

        for (int i = 0; i < iterations; i++) {
            L = prevR;
            R = prevL;

            // Left is XOR'd, Right passes
            byte[] tempL = new byte[4];
            for (int k = 0; k < 4; k++) {
                tempL[k] = (byte) ( L[k] ^ keyBytes[(i * 4) + k]);
            }
            L = tempL;
            prevL = L;
            prevR = R;
        }

        // Concatenate L and R
        byte[] partResult = new byte[8];
        System.arraycopy(R, 0, partResult, 0, 4);
        System.arraycopy(L, 0, partResult, 4, 4);

        return partResult;
    }

    private static byte[] decrypt(byte[] L, byte[] R, byte[] keyBytes, int iterations) {

        // Determine what's left and what's right depending on iterations
        byte[] prevL = iterations % 2 == 0 ? R : L;
        byte[] prevR = iterations % 2 == 0 ? L : R;

        for (int i = 0; i < iterations; i++) {
            L = prevR;
            R = prevL;

            // Left is XOR'd, Right passes
            byte[] tempL = new byte[4];
            for (int k = 0; k < 4; k++) {
                tempL[k] = (byte) ( L[k] ^ keyBytes[(i * 4) + k]);
            }
            L = tempL;
            prevL = L;
            prevR = R;
        }

        // Concatenate L and R
        byte[] result = new byte[8];
        System.arraycopy(R, 0, result, 0, 4);
        System.arraycopy(L, 0, result, 4, 4);

        return result;
    }


    private static byte[] handleMessage(byte[] request) {

        // Check encryption or decryption
        boolean encrypt = request[0] == (byte) 'e';
        int n = findSeparatorIndex(request);

        // Get key and text from request
        byte[] keyBytes = new byte[n - 2];
        byte[] textBytes = new byte[request.length - n - 1];
        System.arraycopy(request, 2, keyBytes, 0, n - 2);
        System.arraycopy(request, n+1, textBytes, 0, request.length - n - 1);

        // Counts how many times 4 fits in key length
        int iterations = keyBytes.length / 4;

        // Amount of separate textParts that should be processed
        int textPartCount = textBytes.length / 8;

        byte[] result = new byte[textBytes.length];

        // Process each part of text
        for (int t = 0; t < textPartCount; t++) {
            // Split textPart into two pieces
            byte[] L = new byte[4];
            byte[] R = new byte[4];
            System.arraycopy(textBytes, (8 * t), L, 0, 4);
            System.arraycopy(textBytes, (8 * t) + 4, R, 0, 4);

            byte[] partResult;

            if (encrypt) {
                partResult = encrypt(L, R, keyBytes, iterations);
            }
            else {
                partResult =  decrypt(L, R, keyBytes, iterations);
            }

            // Append result with partResult
            System.arraycopy(partResult, 0, result, (8 * t), 8);
        }

        return result;
    }
}
