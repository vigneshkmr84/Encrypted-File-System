public class TestCase{

    static final String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/hmac.txt";
    //        String password = "2b7e151628aed2a6abf71589";
    static final String userName = "vignesh";
    static final String password = "password";
    static final String over_write = "OVERWRITE TEXT. this is a sample small text of short length of less than 100 characters.";

    // actual file length = //2234
    static int file_length = 2234;
    static EFS efs = new EFS(null);

    public static void main(String[] args) throws Exception {


//        String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they o";
        String text = "this is a sample small text of short length of less than 100 characters.";

        System.out.println("Score : " + efs.getScore(fileName));
        System.out.println("Length of text : " + text.length());

//        efs.create(fileName, userName, password);

//        case1();
//        case2();
//        case3();
//        case4();
        case5();
//        case6();


    }

    public static void case_read_new_all() throws Exception {
        byte[] contents = efs.read_new(fileName, 0, 2234, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    // writing on 0th position
    // only first file affected, rest remains same.
    public static void case1() throws Exception {
        efs.write(fileName, 0, over_write.getBytes(), password);
        byte[] contents = efs.read_new(fileName, 0, file_length, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    public static void case2() throws Exception {
        efs.write(fileName, 100, over_write.getBytes(), password);
        byte[] contents = efs.read_new(fileName, 0, 2234, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    // exactly the end of file 1. Only file 1 will be affected
    public static void case3() throws Exception {
        efs.write(fileName, 905, over_write.getBytes(), password);
        byte[] contents = efs.read_new(fileName, 0, file_length, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    public static void case4() throws Exception {
        efs.write(fileName, 1000, over_write.getBytes(), password);
        byte[] contents = efs.read_new(fileName, 0, file_length, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    public static void case5() throws Exception {
        efs.write(fileName, 2100, over_write.getBytes(), password);
        byte[] contents = efs.read_new(fileName, 0, file_length, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    // writing on last part file
    // Overflow case
    public static void case6() throws Exception {
        efs.write(fileName, 2200, over_write.getBytes(), password);
        byte[] contents = efs.read_new(fileName, 0, 2287, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

}
