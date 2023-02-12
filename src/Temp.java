import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Temp {

    public static void main(String[] args) throws Exception{

        int size = 4000;
        String fileName = "temp.txt";
        String userName = "vignesh";
        String password = "password";
        List<Integer> algoList = Arrays.asList(2,3,5);

        byte[] saltBytes = secureRandomNumber(16);
        byte[] ivBytes = secureRandomNumber(16);
        byte[] userNameBytes = userName.getBytes(StandardCharsets.UTF_8);
        byte[] userNamePadded = new byte[128];
        System.arraycopy(userNameBytes, 0, userNamePadded, 0, userNameBytes.length);
        System.out.println(new String(userNameBytes));
        System.out.println(new String(userNamePadded));
        System.out.println(userNamePadded.length);

        byte[] passwordBytes = password.getBytes();
        byte[] saltAndPassword = new byte[saltBytes.length + passwordBytes.length];
        ByteBuffer buff = ByteBuffer.wrap(saltAndPassword);
        buff.put(saltBytes);
        buff.put(passwordBytes);
        saltAndPassword = buff.array(); // 48 bytes - 384 bits (SHA384 hashing)

        byte[] hashBytes = hash_SHA384(saltAndPassword);

        System.out.println("Final Hashed : " + new String(hashBytes));
        /*System.out.println("password length " + passwordBytes.length);
        System.out.println("Salt length " + saltBytes.length);
        System.out.println("Hashed length " + hashBytes.length);*/
    }

    public static String[] generate_iv(){
        String keySpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefhijkprstuvwx";
        StringBuilder iv = new StringBuilder();
        int length = 8;
        Random rand = new Random();
        for ( int i=0; i<length; i++){
            iv.append(keySpace.charAt(rand.nextInt(keySpace.length())));
        }
        String out = iv.toString();
        System.out.println("IV Generation");
//        System.out.println(out.substring(0, out.length()/2));
//        System.out.println(out.substring(out.length()/2));
        return new String[]{out.substring(0, out.length()/2), out.substring(out.length()/2+1)};
    }

    public static String generate_salt(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


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
                javax.swing.JOptionPane.showMessageDialog(null, "Error: cannot convert negative number " + array[i] + " into character");
                //return "";
            }
            ret += (char) array[i];
        }

        return ret;
    }
}
