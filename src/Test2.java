public class Test2 {

    public static void main(String[] args){

        byte[] b1 = new byte[] {1, 2, 3, 4, 5};
        byte[] b2 = new byte[] {6, 7, 8, 9, 10};

        byte[] out = new byte[]{};

        System.arraycopy(b1, 0, out, out.length , b1.length);

        for ( byte b : out)
            System.out.println(b);

    }
}
