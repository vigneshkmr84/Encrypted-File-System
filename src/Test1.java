import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class Test1 {

    public static void main(String[] args){

        /*byte[] test = new byte[]{116, 101, 115, 116};
        System.out.println(new String(test));
        System.out.println(new String(Base64.getEncoder().encode(test)));
        byte[] paddedBytes = randomPaddingTo128Bytes(test);
        String be = new String(Base64.getEncoder().encode(paddedBytes));
        System.out.println(be);
        System.out.println(new String(splitBytes(0, 3, Base64.getDecoder().decode(be))));
        */

//        byte[] test = new byte[]{116, 101, 115, 116, 111, 123, 50};
        byte[] test = Base64.getDecoder().decode("XjNYfcIUDUtiNTw3lSYQZAf73KJ8qjDlD4DjRMf8NuKabHusVyT24f+ip2uVUb+bwPhJdIiSaVs8DksYw+AmF+QQPcitkmXyfq0Ua+6rmedTYjol9OOidQ0MaQGdKhAFOY6fEi6xnEHHRf6VOrk2vGq4eQfNSI2AxLgf1ZjPuKWs8Stc/IRoz+WP8BC4kfTW33h/0HnhIAGSVBfOWQEXtX50NriJc/dliLUVaI4SVsYBohHanLv+sURVbdsRsFNB142nTn31BWuGNGHf+l4I64Rk66caH0QSwCiIHaZmWDvmJDsXlS8G6c5nZGR0mcu0FTUVbXeeL8qxSAtNZifSGw==");

        byte[] out = splitBytes(0, 47, test);
        for (byte b : out) {
            System.out.println(b);
        }

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
