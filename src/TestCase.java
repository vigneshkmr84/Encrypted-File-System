import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class TestCase{

    static final String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/hmac.txt";
    //        String password = "2b7e151628aed2a6abf71589";
    static final String userName = "vignesh";
    static final String password = "password";
    static final String over_write_new = "OVERwrite_new TEXT. this is a sample small text of short length of less than 100 characters.";

    // actual file length = //2234
    static int file_length = 2234;
    static EFS efs = new EFS(null);

    static final String main_contents = "One morning, when Gregor Samsa woke from troubled dreams, he found himself \n\n" +
            "transformed in his bed into a horrible vermin. He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections. The bedding was hardly able to cover it and seemed ready to slide " +
            "off any moment. His many legs, pitifully thin compared with the size of the rest of him, waved about helplessly as he looked. \"What's happened to me?\" he thought. It wasn't a dream. His room, a proper human room although a little too small, lay peacefully between its " +
            "four familiar walls. A collection of textile samples lay spread out on the table - Samsa was a travelling salesman - and above it there hung a picture that he had recently cut out of an illustrated magazine and housed in a nice, gilded frame. It showed a lady fitted out " +
            "with a fur hat and fur boa who sat upright, raising a heavy fur muff that covered the whole of her lower arm \ntowards the viewer. Gregor then turned to look out the window at the dull weather. Drops of rain could be heard hitting the pane, which made him feel quite sad. " +
            "\"How about if I sleep a little bit longer and forget all this nonsense\", he thought, but that was something he was unable to do because he was used to sleeping on his right, and in his present state couldn't get into that position. However hard he threw himself " +
            "onto his right, he always rolled back to where he was. He must have tried it a hundred times, shut his eyes so that he wouldn't have to look at the floundering legs, and only stopped when he began to feel a mild, dull pain there that he had never felt before. \"Oh, God\", " +
            "he thought, \"what a strenuous career it is that I've chosen! Travelling day in and day out. Doing business like this takes much more effort than doing your own business at home, and on top of that there's the curse of travelling, worries about making train connections, " +
            "bad and irregular food, contact with different people all the time so that you can never get to know anyone or \nbecome friendly with them. It can all go to Hell!\" He felt a slight itch up on his belly; pushed himself slowly up on his back towards the headboard so that he " +
            "could lift his head better; found where the itch was, and saw that it was covered with lots of little white spots which he didn't know what to make of; and when he tried to feel the place with one of his legs he drew it quickly back because as soon as he touched it he was " +
            "overcome by a cold shudder. He slid back into his former position. \"Getting up early all the time\", he thought, \"it makes you stupid. You've got to get enough sleep. Other travelling salesmen live a life of luxury. For instance, whenever I go back to the guest house during " +
            "the morning to copy out the contract, these gentlemen are always still sitting there eating their breakfasts. I ought to just try that with my boss; I'd get kicked out on the spot. But who knows, maybe that would be the best thing for me. If I didn't have my parents to think " +
            "about I'd have given in my notice a long time ago, I'd have gone up to the boss and told him just what I think, tell him everything I would, let him know just what I feel. He'd fall right off his desk! And it's a funny sort of business to be sitting up there at your desk, talking dow\n\n";

    public static void main(String[] args) throws Exception {


//        String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they o";


//        case_create();
//        case_first_write();
//        case_read_new_all();
//
//        case_verify_pwd();
//        case1();
//        case_read_new_all();
//        case2();
//        case_read_new_all();
//        case3();
//        case_read_new_all();
//        case4();
//        case_read_new_all();
//        case5();
//        case_read_new_all();
//        case6();
        case_read_new_all();
//        case_check_integrity();


    }

    public static void case_verify_pwd() throws Exception{
        System.out.println(efs.verifyPassword(password, fileName));
    }
    public static void case_create() throws Exception {
        if ( new File(fileName).isDirectory() )
            Files.walk(Paths.get(fileName)).map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);

        efs.create(fileName, "vigneshkmr84@gmail.com", password);
    }

    public static void case_first_write() throws Exception {
        efs.write_new(fileName, 0, main_contents.getBytes(), password);
    }

    public static void case_read_new_all() throws Exception {
        byte[] contents = efs.read2(fileName, 905, 92, password);
        String out = new String(contents);
        System.out.println("read_new Final Output : " + out);
        System.out.println("read_new Output length: " + out.length());
    }

    // writing on 0th position
    // only first file affected, rest remains same.
    public static void case1() throws Exception {
        efs.write_new(fileName, 0, over_write_new.getBytes(), password);
    }

    public static void case2() throws Exception {
        efs.write_new(fileName, 100, over_write_new.getBytes(), password);
    }

    // exactly the end of file 1. Only file 1 will be affected
    public static void case3() throws Exception {
        efs.write_new(fileName, 905, over_write_new.getBytes(), password);
    }

    public static void case4() throws Exception {
        efs.write_new(fileName, 1000, over_write_new.getBytes(), password);
    }

    public static void case5() throws Exception {
        efs.write_new(fileName, 2100, over_write_new.getBytes(), password);
    }

    // writing on last part file
    // Overflow case
    public static void case6() throws Exception {
        efs.write_new(fileName, 3326, over_write_new.getBytes(), password);
    }

    public static void case_check_integrity() throws Exception {
        System.out.println("Integrity : " + efs.check_integrity(fileName, password));;
    }

}
