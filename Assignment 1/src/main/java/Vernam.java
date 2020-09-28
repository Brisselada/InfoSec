import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Vernam {

    public static void main(String[] args) throws IOException {

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

    private static int findSeparatorIndex(byte[] reqBytes) {
        byte target = (byte) 0xFF;
        for (int b = 0; b < reqBytes.length; b++){
            if (reqBytes[b] == target) {
                return b;
            }
        }
        return 0;
    }

    /**
     * Encrypts the given String text through and XOR operation with given String key
     * @param reqBytes Byte array containing the key and text that should be XOR'd
     */
    private static byte[] handleMessage(byte[] reqBytes) throws IOException {

//        byte[] reqBytes = request.getBytes();
//        int n = reqBytes.length / 2 - 1;
        int n = findSeparatorIndex(reqBytes);

        byte[] textBytes = new byte[n];
        byte[] keyBytes = new byte[n];
        System.arraycopy(reqBytes, 0, keyBytes, 0, n);
        System.arraycopy(reqBytes, n+1, textBytes, 0, n);

        byte[] result = new byte[n];


        for(int i = 0; i < textBytes.length; i++){
            result[i] = (byte) (keyBytes[i] ^ textBytes[i]);
        }
        return result;
//        System.out.write(result);
    }
}
