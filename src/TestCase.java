import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestCase{
    public static void main(String[] args) throws Exception {

        EFS efs = new EFS(null);
        String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/fi.txt";
//        String password = "2b7e151628aed2a6abf71589";
        String userName = "vignesh";
        String password = "password";
        /*boolean passwordCheck = efs.verifyPassword(password, fileName);
        System.out.println("Password check : " + passwordCheck);
        System.out.println("Username check : " + (Objects.equals(efs.findUser(fileName), "vignesh")));
        System.out.println("Length check : " + (efs.length(fileName, password) == 48));*/

        String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they oh hope of. General Agreed joy ma";

        System.out.println("Score : " + efs.getScore(fileName));
        System.out.println("Length of text : " + text.length());

        new File(fileName).delete();
        efs.create(fileName, userName, password);



        /*String readFile = "/Users/vigneshthirunavukkarasu/Info-sec/hmac.txt";
        int readPos = 0;
        int readLength = 30;

        String additionalText = "wkGzcan0NzzqKZoa53tP9B2jWoBWo9VFFdfTkphr9RMldAmNRrWd5qTo4twEva8asF9HFBEzjIyUN6Mq6c76JzaMRVNthBCo9TXLyJKjtE5TzqmHzBrd88AKW9juc4LJZcpyHNvSymT8n0M8W5AzyS1hDfVcBMKPaJDYN9fCRuDZT24HED84gAsBbEeEthmGATBm7SsPeOjRP8Y5A2nw8lfyIfdNfjQzWOYqCTIiGl0nQvZeF2d1i5uuJhRQKba5MNfpeA2Is8IfNO3nePLytpqssAv8Hk0j98e1wnHvAV751pAjLJHGg19JNcIW5O3Gk8eHIB8OY8ZplpVVf0UPhsbNUgSV5Mxy04z6JueFxFOS0Lq3GEf4W900KxMuInCHgswlIVqy3FDTQzzKlasts6t5YqmL42e74cCheTHaHdFK8jJbeRzbctMGSph9IUAK6k06FJ1FajcrZFbavWtDxLH0tRyFkI0GKKD0tJVgLo7BPm5ixXwxx9xJO7ZgPk7efBWmQYBF7yCb4Z1P4C7aVMunpS6JVn3mSBYPK63lfPYaom4S5lJCj0C6NdXAV8BsuZzfEmg0JS8J8cpHjtOjJDMBXgpr2QmWKr7U8iImDAZdpFw6K9c1jRbW9WTyAg8al05dYItbAzCSecBBx6qKewQMBzo0zSoYtcdrK4cRcrgWKpclRuOmS52fijrYWZHgX2kb0yDIOCwSawPfE6kkovEPS3ainYXPXG1KkTe93ua09EsGuojMaIRNretWMNggdgF9TBa79S7WkJVSLmKJMgQ1bYUQ3UJusPhmGNXoOo2xn0YGbNwmF59kNtEosJrP0vZMQfUl4xg1i0GdeUbF9d5MBKRuYN3iFZrieMZHTMtKp83ZG0zMYXl4EvPI8dXLssUbhFGBHlrBSczhOK4NX5XfLuyuyeNSGMJuxOxpxjBYltQne4REpkURz5rHm2HdVe8CY9583NvEyAjOOIFNt1QABIwuvb40i6eQUaY28SAlMgFKjlqY1n7TduYt9cwi";

        efs.write(fileName, 0, text.getBytes(), password);
        efs.cut(fileName, 30, "password");

        byte[] contents = efs.read(readFile, readPos, readLength, password);
        String out = new String(contents);
        System.out.println("Read Final Output : " + out);
        System.out.println("Read Output length: " + out.length());

        efs.write(fileName, 30, additionalText.getBytes(), password);*/

        System.out.println( "File integral " + efs.check_integrity(fileName, password));


    }

}
