
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Test1 {

    public static void main(String[] args) throws Exception {

        /*byte[] test = new byte[]{116, 101, 115, 116};
        System.out.println(new String(test));
        System.out.println(new String(Base64.getEncoder().encode(test)));
        byte[] paddedBytes = randomPaddingTo128Bytes(test);
        String be = new String(Base64.getEncoder().encode(paddedBytes));
        System.out.println(be);
        System.out.println(new String(splitBytes(0, 3, Base64.getDecoder().decode(be))));
        */

//        byte[] test = new byte[]{116, 101, 115, 116, 111, 123, 50};
        /*byte[] test = Base64.getDecoder().decode("XjNYfcIUDUtiNTw3lSYQZAf73KJ8qjDlD4DjRMf8NuKabHusVyT24f+ip2uVUb+bwPhJdIiSaVs8DksYw+AmF+QQPcitkmXyfq0Ua+6rmedTYjol9OOidQ0MaQGdKhAFOY6fEi6xnEHHRf6VOrk2vGq4eQfNSI2AxLgf1ZjPuKWs8Stc/IRoz+WP8BC4kfTW33h/0HnhIAGSVBfOWQEXtX50NriJc/dliLUVaI4SVsYBohHanLv+sURVbdsRsFNB142nTn31BWuGNGHf+l4I64Rk66caH0QSwCiIHaZmWDvmJDsXlS8G6c5nZGR0mcu0FTUVbXeeL8qxSAtNZifSGw==");

        byte[] out = splitBytes(0, 47, test);
        for (byte b : out) {
            System.out.println(b);
        }*/

        // Working logic for HMAC calculation
        /*byte[] str = "The quick brown fox jumps over the lazy dog".getBytes();
        byte[] key = "key".getBytes();

        byte[] hmac = hmac(str, key);
        System.out.println(Base64.getEncoder().encodeToString(hmac));*/

        /*String s = "MA==";
        String s1 = new String(Base64.getDecoder().decode(s));
        System.out.println(s1);
        System.out.println(Integer.valueOf(new String(Base64.getDecoder().decode(s))));*/

        /*String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they oh hope of. General Agreed joy ma";
        String text1 = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes.Agreed joy va";

//        byte[] s1 = secureRandomNumber(16);
        byte[] s1 = "lbfwrzjjppnpgdng".getBytes();

        System.out.println(new String(s1));
        System.out.println(Utility.encript_AES(text.getBytes(), s1).length);
        System.out.println(Utility.encript_AES(text1.getBytes(), s1).length);

        Test.save_to_file(Utility.encript_AES(text.getBytes(), s1), new File("/Users/vigneshthirunavukkarasu/Info-sec/text.txt"));
        Test.save_to_file(Utility.encript_AES(text1.getBytes(), s1), new File("/Users/vigneshthirunavukkarasu/Info-sec/text1.txt"));

        System.out.println(new String(Utility.encript_AES(text.getBytes(), s1)).length());
        System.out.println(new String(Utility.encript_AES(text1.getBytes(), s1)).length());*/

//        byte[] b = "this is a test12".getBytes();

        /*byte[] b = "Agreed joy vanity".getBytes();

        System.out.println(new String(splitBytesWithSize(b, 3, 8)));*/

        /*String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/text.txt";

        System.out.println(EFS.getScore(fileName));*/

//        String p = "nPJ3nEzy/gMCVcjWjU+Bo6RPg6SrF+xMUoADoiv7g3IfVuLRVrzeaQiIY8wkYCYsw7tMCxrKELi1aE3N4eEeLGS+e8rB0IY6Tvsv2wYfRBryYK635uhF/mu3GNfi6/PJNtlLfwb4eBjvT8eOW6TrFyeRzmA9iY5wwOKJW/XgKPw=";
//        byte[] pwd = Base64.getDecoder().decode(p);

//        String s = "MmI3ZTE1MTYyOGFlZDJhNmFiZjcxNTg5MmI3ZTE1MTYyOGFlZDJhNmFiZjcxNTg5OG1UWENXYXNFMnlLR1REQjJqU21RcU41ODFzaFBMcldpSUF6NlNLeWN1ekFzRjBxbEVWbWlsNzVvRmhGTHNLZTFrZlFZVkgxbURUa0dvSHAzeUFzMXRiclBKRll6ZkhmSU9tRnNJdWJxbTU5NWdTU28yTWd2cWxvR2VrWjQ0VXdTM052YjNQbTJRNzhpalhmY3prMlc0NGtIZVFrc0QzOGZkcFNkaHJOUWRIMWtHTU1ER2dpY2w1YnB3ZEpnUWEzNmFiNHFaUXNkQXBhYkU4Yg==";
//        splitBytes(pwd, )

//        byte[] b = {1, 2, 3, 4, 127};
        byte[] b = "abc".getBytes();

//        printBytes(b);
//        System.out.println("string " + new String(b));
//        for ( int i=0; i< 20; i++){
//            incrementIV(b, 4);
////            increment(b);
//            System.out.println(new String(b));
//        }

        /*for (int i = 0; i < b.length; i++) {
            b[i] += 1;
        }*/

        System.out.println("Before : " + new String(b, StandardCharsets.UTF_8));


//        for ( int i=0; i< 200; i++) {
//            incrementIV(b);
//            System.out.println("After : " + new String(b, StandardCharsets.UTF_8));
//        }
//        printBytes(b);



//        byte[] iv = secureRandomNumber(16);
        byte[] iv = "1234567890123456".getBytes();
        String msg = "wkGzcan0NzzqKZoa53tP9B2jWoBWo9VFFdfTkphr9RMldAmNRrWd5qTo4twEva8asF9HFBEzjIyUN6Mq6c76JzaMRVNthBCo9TXLyJKjtE5TzqmHzBrd88AKW9juc4LJZcpyHNvSymT8n0M8W5AzyS1hDfVcBMKPaJDYN9fCRuDZT24HED84gAsBbEeEthmGATBm7SsPeOjRP8Y5A2nw8lfyIfdNfjQzWOYqCTIiGl0nQvZeF2d1i5uuJhRQKba5MNfpeA2Is8IfNO3nePLytpqssAv8Hk0j98e1wnHvAV751pAjLJHGg19JNcIW5O3Gk8eHIB8OY8ZplpVVf0UPhsbNUgSV5Mxy04z6JueFxFOS0Lq3GEf4W900KxMuInCHgswlIVqy3FDTQzzKlasts6t5YqmL42e74cCheTHaHdFK8jJbeRzbctMGSph9IUAK6k06FJ1FajcrZFbavWtDxLH0tRyFkI0GKKD0tJVgLo7BPm5ixXwxx9xJO7ZgPk7efBWmQYBF7yCb4Z1P4C7aVMunpS6JVn3mSBYPK63lfPYaom4S5lJCj0C6NdXAV8BsuZzfEmg0JS8J8cpHjtOjJDMBXgpr2QmWKr7U8iImDAZdpFw6K9c1jRbW9WTyAg8al05dYItbAzCSecBBx6qKewQMBzo0zSoYtcdrK4cRcrgWKpclRuOmS52fijrYWZHgX2kb0yDIOCwSawPfE6kkovEPS3ainYXPXG1KkTe93ua09EsGuojMaIRNretWMNggdgF9TBa79S7WkJVSLmKJMgQ1bYUQ3UJusPhmGNXoOo2xn0YGbNwmF59kNtEosJrP0vZMQfUl4xg1i0GdeUbF9d5MBKRuYN3iFZrieMZHTMtKp83ZG0zMYXl4EvPI8dXLssUbhFGBHlrBSczhOK4NX5XfLuyuyeNSGMJuxOxpxjBYltQne4REpkURz5rHm2HdVe8CY9583NvEyAjOOIFNt1QABIwuvb40i6eQUaY28SAlMgFKjlqY1n7TduYt9cwi";
//        byte[] message = "this is a testthis is a test".getBytes(); //is is a test1234
        byte[] message = msg.getBytes();
//        byte[] message = "this is a test".getBytes();

        byte[] ivEnc = new byte[iv.length];
        byte[] ivDec = new byte[iv.length];

        System.arraycopy(iv, 0, ivEnc, 0, iv.length-1);
        System.arraycopy(iv, 0, ivDec, 0, iv.length-1);

        System.out.println("Original : " + new String(message));

        byte[] encrypted = blockEncrypt(message, ivEnc, 16);

        System.out.println(Base64.getEncoder().encodeToString(encrypted));
        System.out.println(new String(iv));
        System.out.println(new String(ivEnc));

        byte[] decrypted = blockDecrypt(encrypted, ivDec, message.length, 16);

        System.out.println(new String(iv));
        System.out.println(new String(ivEnc));
        System.out.println("decrypted : " + new String(decrypted));
    }

    private static void printBytes(byte[] b) {
        for ( byte i: b)
            System.out.print(i + ", ");
    }

    public static byte[] blockEncrypt(byte[] message, byte[] iv, int blockSize ) throws Exception {

        byte[] padded = zeroPad(message, (int) Math.ceil((double) message.length / blockSize) * blockSize);
        byte[] outBytes = new byte[]{};

        System.out.println("Encryption Padded bytes : " + padded.length);
        int i=0;
        while (i< padded.length){
            byte[] block = splitBytes(i, i+blockSize-1, padded);
            byte[] blockOut = encript_AES(block, iv);
            outBytes = concatenateByteArrayList(Arrays.asList(outBytes, blockOut));
            i+=blockSize;
            incrementIV(iv);
        }
        System.out.println("Encryption final out length " + outBytes.length);
        return outBytes;
    }

    public static byte[] blockDecrypt(byte[] message, byte[] iv, int messageSize, int blockSize ) throws Exception {

        byte[] outBytes = new byte[]{};

        int i=0;
        System.out.println("Decryption Byte length " + message.length);

        while (i< message.length){
            byte[] block = splitBytes(i, i+blockSize-1, message);
            byte[] blockOut = decript_AES(block, iv);
            outBytes = concatenateByteArrayList(Arrays.asList(outBytes, blockOut));
            i+=blockSize;
            incrementIV(iv);
        }

        byte[] out = trimByteArray(outBytes, messageSize);

        System.out.println("Decryption final out length " + outBytes.length);
        System.out.println("Decryption final out length " + out.length);

        return out;
    }

    /**
     * Given byte array is encrypted with the 128 bit key array
     * AES/ECB/NoPadding is used
     * 1024 byte input gives 1024 byte output (As there is no padding)
     * @param plainText
     * @param key
     * @return
     * @throws Exception
     */
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
    public static void incrementIV(byte[] iv) {

        int i = iv.length-1;
        while ( true ){
            if ( i == 0 && iv[i] == Byte.MAX_VALUE){
                iv[iv.length-1] = Byte.MIN_VALUE;
                break;
            } else if ( iv[i] == Byte.MAX_VALUE ){
                i--;
            }else{
                iv[i] += 1;
                break;
            }
        }
    }

    public static byte[] trimByteArray(byte[] input, int size){
        int i=0;
        byte[] out = new byte[size];
        while ( i < size){
            out[i] = input[i];
            i++;
        }

        return out;
    }


    public static boolean verifyPassword(byte[] line3, byte[] line4){

        return false;
    }

    public static byte[] splitBytesWithSize(byte[] array, int sp, int len) {
        return Arrays.copyOfRange(array, sp, sp + len);
    }

    public static byte[] zeroPad(byte[] b, int outputByteSize){
        byte[] out = new byte[outputByteSize];
        System.arraycopy(b, 0, out, 0, b.length);
        return out;
    }
    public static byte[] byteXOR(byte[] key, byte[] pad){
        byte[] out = new byte[key.length];
        for ( int i=0; i< key.length; i++)
            out[i] = (byte) (key[i] ^ pad[i%pad.length]);

        return out;
    }
    public static byte[] hmac(byte[] message, byte[] key) throws Exception {

        /*if (key.length > 64)
            key = Test.hash_SHA256(key);
        else
            key = zeroPad(key, 64);*/

        key = key.length > 64 ? Test.hash_SHA256(key): zeroPad(key, 64);

        byte[] ipad = new byte[]{0x36};
        byte[] opad = new byte[]{0x5c};

        byte[] ipadKey = byteXOR(key, ipad);
        byte[] opadKey = byteXOR(key, opad);

        byte[] firstHash = Utility.hash_SHA256(concatenateByteArrayList(Arrays.asList(ipadKey, message)));

        return Utility.hash_SHA256(concatenateByteArrayList(Arrays.asList(opadKey, firstHash)));
    }

    public static byte[] concatenateByteArrayList(List<byte[]> b1) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for (byte[] bytes : b1) {
            outputStream.write(bytes);
        }
        return outputStream.toByteArray();
    }
    public static byte[] splitBytes(int sp, int ep, byte[] message){
        byte[] out = new byte[ep-sp+1];
        for ( int i=sp, k=0; i<=ep; i++, k++){
            out[k] = message[i];
        }
        return out;
    }
    public static byte[] secureRandomNumber(int randomNumberLength) {
        byte[] randomNumber = new byte[randomNumberLength];
        SecureRandom random = new SecureRandom();
        random.nextBytes(randomNumber);
        return randomNumber;
    };

    public static byte[] randomPaddingTo128Bytes(byte[] message){
        byte[] rand = secureRandomNumber(128);
        System.out.println(new String(Base64.getEncoder().encode(rand)));

        System.arraycopy(message, 0, rand, 0, message.length);

        return rand;
    }
}
