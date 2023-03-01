public class Test {

    public static void main(String[] args){

        calculate(900, 100);
    }

    public static void calculate(int starting_position, int len){
        int ENC_BLOCK_SIZE = 16;
        int FILE_SIZE_BYTES = 992;

//        int end_pos = starting_position + len;
//        int startFileBlock = starting_position / FILE_SIZE_BYTES;
//        int endFileBlock = (end_pos) / FILE_SIZE_BYTES;
//
//        System.out.println("Start block " + startFileBlock);
//        System.out.println("End block " + endFileBlock);
//
//        int startEncChunk = (starting_position % FILE_SIZE_BYTES) / ENC_BLOCK_SIZE;
//        int endEncChunk = (end_pos % FILE_SIZE_BYTES) / ENC_BLOCK_SIZE;
//
//        System.out.println("Start chunk " + startEncChunk);
//        System.out.println("End chunk " + endEncChunk);


//        System.out.println(roundNumber(30+88, 992));

        String temp = "This isa  teascjasnckasckank  asacbscna  adbvukansc 73yr82y3rf acansca";

        System.out.println("Length before padding : " + temp.length());
        String temp_padding = padRight(temp, 992);
        System.out.println("Length after padding : " + temp_padding.length());
        System.out.println(temp_padding);
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String nullPadString1(String str, int length) {
        return String.format("%1$-" + length + "s", str).replace(' ', '\0');
    }

    static public String nullPadString(String msg, int len){

        // check the padding byte length
        while(msg.getBytes().length < len){
            msg +=("\0");
        }

        return msg;
    }

    public static int roundNumber(int number, int roundToMultiple){

        int remainder = number % roundToMultiple;
        if ( remainder == 0)
            return number;
        else
            return number + (roundToMultiple - remainder);
    }
}
