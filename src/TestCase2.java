
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TestCase2 {

    public static void main(String[] args) throws Exception {
        EFS efs = new EFS(null);
        String userName = "vignesh";
        String password = "pass";

        String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/hmac.txt";

        System.out.println(efs.getScore(fileName));
        byte[] file2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus eu dui faucibus, suscipit quam vel, rhoncus velit. Sed congue, libero vel posuere luctus, risus nunc euismod quam, ut bibendum augue ipsum vel lectus. Morbi finibus, sapien nec feugiat elementum, enim nibh bibendum tortor, vel maximus dolor arcu vitae nulla. In auctor lorem sit amet risus vestibulum, ut consectetur sapien fringilla. Donec hendrerit convallis neque, a ultrices mi cursus nec. Aenean maximus tortor vel purus congue, ut consequat risus varius. Nulla bibendum, ipsum in bibendum viverra, enim metus tempor odio, eu pretium ipsum odio eget ex. Fusce suscipit, mi ut consectetur euismod, sem lorem laoreet mi, sed vestibulum ex arcu non turpis. In vulputate eget nisl vel sodales. Suspendisse eget enim rutrum, ultricies odio eu, efficitur lectus. Sed sed mauris aliquet, finibus risus eget, suscipit massa. Nullam id lectus id odio lacinia2sapien luctus malesuada. Etiam at metus non mauris vivemollis.molesti2".getBytes();
        byte[] file3 = "The autumn leaves were falling gently from the trees, blanketing the ground with a tapestry of red, gold, and orange. The cool breeze carried the scent of woodsmoke and apple cider, signaling the arrival of fall. In the distance, a flock of geese honked overhead as they made their way south for the winter. The sky was a brilliant shade of blue, with wispy white clouds drifting lazily by. As I walked through the park, I felt a sense of peace and tranquility wash over me. It was a perfect day to be alive and to savor the beauty of nature.".getBytes();
//        efs.create(fileName, userName, password);
        System.out.println("Original length " + file3.length);
        byte[] iv = efs.getIV(fileName);
        byte[] ivEnc = new byte[iv.length];
        System.arraycopy(iv, 0, ivEnc, 0, iv.length-1);

        System.out.println(Arrays.toString(iv));
        System.out.println(Arrays.toString(ivEnc));
        efs.incrementIV(ivEnc, 62*2);

//        System.out.println("Bit diff " + bitDifference(iv, ivEnc));

        byte[] padded = efs.zeroPad(file3, 1023);
        System.out.println("Padded length : " + padded.length);
        byte[] enc = efs.blockEncrypt(padded, ivEnc, 16);

        save_to_file(enc, fileName + File.separator + "3");
    }


    public static void save_to_file(byte[] message, String fileName){

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(message);
            System.out.println("Bytes written to file.");
        } catch (IOException e) {
            System.out.println("Error writing bytes to file.");
            e.printStackTrace();
        }
    }

}
