import java.io.ByteArrayOutputStream;
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

        String s = "MA==";
        String s1 = new String(Base64.getDecoder().decode(s));
        System.out.println(s1);
        System.out.println(Integer.valueOf(new String(Base64.getDecoder().decode(s))));
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
