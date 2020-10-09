import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Steganography {

    public static void main(String[] args) throws IOException {

//        File fi = new File("myfile.jpg");
//        byte[] fileContent = Files.readAllBytes(fi.toPath());

        byte[] requestBytes = Files.readAllBytes(Path.of("src\\main\\resources\\Steganography.jpg"));
        System.out.write(requestBytes);

        String result = "";
        int counter = 0;
        String text = "";
        byte[] byteresult = new byte[20000];

        for (byte b : requestBytes) {
//            result += getLastBits(b);
            result += getLastBits(b);
            counter ++;
            if (counter == 3) {
                int i = Byte.parseByte(result, 2);
                byte a = Byte.parseByte(result, 2);
//                byteresult += a;
                char c = (char) i;
                text += c;
                result = "";
                counter = 0;
            }
        }
        System.out.println(text);
        FileOutputStream out = new FileOutputStream("the-file-name");
        out.write(byteresult);
        out.close();
    }

    public static String getLastBits(Byte b) {
        String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
//        String result = bits.substring(6,2);
//        return result;
        char[] charArray = bits.toCharArray();
        String result = String.valueOf(charArray[6]) + String.valueOf(charArray[7]);
        return result;
    }

    public static String getLastBit(Byte b) {
        String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
//        String result = bits.substring(6,2);
//        return result;
        char[] charArray = bits.toCharArray();
        String result = String.valueOf(charArray[7]);
        return result;
    }
}
