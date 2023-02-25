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

        int updatedLength = 993;
//        int toPadLength = updatedLength + (FILE_SIZE_BYTES - (updatedLength % FILE_SIZE_BYTES));

        int toPadLength = updatedLength % FILE_SIZE_BYTES == 0 ? updatedLength : (updatedLength/FILE_SIZE_BYTES + 1) * FILE_SIZE_BYTES;
        System.out.println(toPadLength);


    }
}
