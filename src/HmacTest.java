import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class HmacTest {

    public static void main(String[] args) throws Exception {

        // Working logic for HMAC calculation
        String originalMessage = "wkGzcan0NzzqKZoa53tP9B2jWoBWo9VFFdfTkphr9RMldAmNRrWd5qTo4twEva8asF9HFBEzjIyUN6Mq6c76JzaMRVNthBCo9TXLyJKjtE5TzqmHzBrd88AKW9juc4LJZcpyHNvSymT8n0M8W5AzyS1hDfVcBMKPaJDYN9fCRuDZT24HED84gAsBbEeEthmGATBm7SsPeOjRP8Y5A2nw8lfyIfdNfjQzWOYqCTIiGl0nQvZeF2d1i5uuJhRQKba5MNfpeA2Is8IfNO3nePLytpqssAv8Hk0j98e1wnHvAV751pAjLJHGg19JNcIW5O3Gk8eHIB8OY8ZplpVVf0UPhsbNUgSV5Mxy04z6JueFxFOS0Lq3GEf4W900KxMuInCHgswlIVqy3FDTQzzKlasts6t5YqmL42e74cCheTHaHdFK8jJbeRzbctMGSph9IUAK6k06FJ1FajcrZFbavWtDxLH0tRyFkI0GKKD0tJVgLo7BPm5ixXwxx9xJO7ZgPk7efBWmQYBF7yCb4Z1P4C7aVMunpS6JVn3mSBYPK63lfPYaom4S5lJCj0C6NdXAV8BsuZzfEmg0JS8J8cpHjtOjJDMBXgpr2QmWKr7U8iImDAZdpFw6K9c1jRbW9WTyAg8al05dYItbAzCSecBBx6qKewQMBzo0zSoYtcdrK4cRcrgWKpclRuOmS52fijrYWZHgX2kb0yDIOCwSawPfE6kkovEPS3ainYXPXG1KkTe93ua09EsGuojMaIRNretWMNggdgF9TBa79S7WkJVSLmKJMgQ1bYUQ3UJusPhmGNXoOo2xn0YGbNwmF59kNtEosJrP0vZMQfUl4xg1i0GdeUbF9d5MBKRuYN3iFZrieMZHTMtKp83ZG0zMYXl4EvPI8dXLssUbhFGBHlrBSczhOK4NX5XfLuyuyeNSGMJuxOxpxjBYltQne4REpkURz5rHm2HdVe8CY9583NvEyAjOOIFNt1QABIwuvb40i6eQUaY28SAlMgFKjlqY1n7TduYt9cwi";
        byte[] str = originalMessage.getBytes();
        //        byte[] str = "The quick brown fox jumps over the lazy dog".getBytes();
        byte[] key = "key".getBytes();

        byte[] hmac = calculateHMAC(str, key);
        System.out.println(hmac.length);
        System.out.println(Base64.getEncoder().encodeToString(hmac));
    }

    public static byte[] calculateHMAC(byte[] message, byte[] key) throws Exception {

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
}
