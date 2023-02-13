import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception{


        int fileSize = 4000;
        String fileName = "temp.txt";
        String userName = "vignesh";
        String password = "password";
        String file = "/Users/vigneshthirunavukkarasu/Info-sec/test.txt ";

        /*byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);
        System.out.println("Salt Bytes Length :" + saltBytes.length);

        byte[] passwordHash = hash_SHA384(concatenateByteArrays(password.getBytes(), saltBytes));
//        System.out.println(String.valueOf(fileSize).getBytes().length);
        byte[] line1 = getSize(password, fileSize);
        byte[] line2 = userName.getBytes();
        byte[] line3 = concatenateByteArrays(concatenateByteArrays(passwordHash, saltBytes), ivBytes);

        System.out.println("Final Metadata Byte append");

        String meta_data = byteArray2String(line1) + System.lineSeparator() + byteArray2String(line2) + System.lineSeparator()+ byteArray2String(line3);

        System.out.println(meta_data);
//        System.out.println(Base64.getEncoder().encode(line1));
//        System.out.println(line1);
        System.out.println("Final Metadata String append");
        System.out.println(new String(line1) + System.lineSeparator() + new String(line2) + System.lineSeparator() + new String(line3) + System.lineSeparator());
        save_to_file(meta_data.getBytes(), new File("/Users/vigneshthirunavukkarasu/Downloads/Project1/Test", "0"));*/

        create("/Users/vigneshthirunavukkarasu/Info-sec/test.txt", userName, password);
    }

    public static void create(String file_name, String user_name, String password) throws Exception {
        File dir = new File(file_name);
        if (dir.exists() && dir.isDirectory()){
//            throw new Exception("File already exists");
            dir.delete();
        }

        dir.mkdirs();
        System.out.println("Directory Created");
        File meta_file = new File(file_name, "0");
        save_to_file(generateNewMetaFile(user_name, password, file_name), meta_file);
        System.out.println("Metadata file written successfully");
    }

    public static String zeroPadding(byte[] message){
        byte[] out = new byte[128];
        return null;
    }
    public static byte[] generateNewMetaFile(String user_name, String password, String fileName) throws Exception {

        String randChars = "abcdefghijklmnopqrstuvwxyz";
        randChars += randChars.toUpperCase() + "0123456789";
        StringBuilder sb = new StringBuilder();

        // 16 bytes = 128 bits
        byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);

        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] paddedPass = concatenateByteArrayList(Arrays.asList(saltBytes, passBytes));
        byte[] hashedPass = hash_SHA384(paddedPass);

        String line1 = Base64.getEncoder().encodeToString(Integer.toString(0).getBytes());
        String line2 = Base64.getEncoder().encodeToString(user_name.getBytes());
        String line3 = Base64.getEncoder().encodeToString(randomPadding(hashedPass, 128));
        String line4 = Base64.getEncoder().encodeToString(randomPadding(concatenateByteArrayList(Arrays.asList(saltBytes, ivBytes)), 256));
        String line5 = Base64.getEncoder().encodeToString(randomPadding("01234567899876543210123456789012".getBytes(), 128));

        sb.append(line1).append(System.lineSeparator())
                .append(line2).append(System.lineSeparator())
                .append(line3).append(System.lineSeparator())
                .append(line4).append(System.lineSeparator())
                .append(line5).append(System.lineSeparator());

        System.out.println("line 3,4,5 length : " + (line3.length() + line4.length() + line5.length()));
        int length = sb.toString().length();
        System.out.println("initial length : " + length);

        // padding for remaining bytes
        while ( sb.toString().length() + 1 <= Config.BLOCK_SIZE){
            sb.append(randChars.charAt(Math.abs(secureRandomNumber(1)[0]) % randChars.length()));
        }
        System.out.println("Total size after padding : " + sb.length());

        return sb.toString().getBytes();
    }

    public static byte[] randomPadding(byte[] message, int padding){
        byte[] rand = secureRandomNumber(padding);
        System.out.println(new String(Base64.getEncoder().encode(rand)));

        System.arraycopy(message, 0, rand, 0, message.length);

        return rand;
    }

    public static byte[] getSize(int fileSize, String fileName){
        int score = getScore(fileName);
        System.out.println(score);
        return String.valueOf(fileSize * score).getBytes();
    }

    public static int getScore(String fileName){
        fileName = fileName.replaceAll("[^A-Za-z0-9]","");
        int sum = 0;
        fileName = fileName.toLowerCase();

        for (int i = 0; i < fileName.length(); i++)
            sum += fileName.charAt(i) - 'a' + 1;
        return  sum / fileName.length();
    }
    public static byte[] concatenateByteArrays(byte[] b1, byte[] b2) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( b1 );
        outputStream.write( b2 );
        return outputStream.toByteArray();
    }

    public static byte[] concatenateByteArrayList(List<byte[]> b1) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for (byte[] bytes : b1) {
            outputStream.write(bytes);
        }
        return outputStream.toByteArray();
    }
    public static byte[] zeroPad(byte[] b, int outputByteSize){
        byte[] out = new byte[outputByteSize];
        System.arraycopy(b, 0, out, 0, b.length);

        return out;
    }

    // PROVIDED FUNCTIONS
    public static byte[] encript_AES(byte[] plainText, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }

    public static byte[] decript_AES(byte[] cypherText, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cypherText);
    }

    public static byte[] hash_SHA256(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash1 = digest.digest(message);
        return hash1;
    }

    public static byte[] hash_SHA384(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-384");
        byte[] hash1 = digest.digest(message);
        return hash1;
    }

    public static byte[] hash_SHA512(byte[] message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash1 = digest.digest(message);
        return hash1;
    }

    public static byte[] secureRandomNumber(int randomNumberLength) {
        byte[] randomNumber = new byte[randomNumberLength];
        SecureRandom random = new SecureRandom();
        random.nextBytes(randomNumber);
        return randomNumber;
    };

    public static String byteArray2String(byte[] array) {
        String ret = "";
        for (int i = 0; i < array.length; i++) {
            if (array[i] < 0)
            {
//                javax.swing.JOptionPane.showMessageDialog(null, "Error: cannot convert negative number " + array[i] + " into character");
                //return "";
                ret += (char) (Byte.MAX_VALUE + array[i]);
            }else
                ret += (char) array[i];
//            ret += (char) Math.abs(array[i]);
        }

        return ret;
    }

    public static byte[] read_from_file(File file) throws Exception {
        DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(file)));

        int size = in.available();

        byte[] toR = new byte[size];

        in.read(toR);

        in.close();
        return toR;

    }

    public static void save_to_file(byte[] s, File file) throws Exception {
        if (file == null) {
            return;
        }
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.write(s);
        out.close();

    }
}
