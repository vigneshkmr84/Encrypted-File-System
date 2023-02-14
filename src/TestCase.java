import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestCase{
    public static void main(String[] args) throws Exception {

        EFS efs = new EFS(null);
        String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/test.txt";
        String password = "password";
//        boolean passwordCheck = efs.verifyPassword(password, fileName);
//        System.out.println("Password check : " + passwordCheck);
        System.out.println("Username check : " + (Objects.equals(efs.findUser(fileName), "vignesh")));
        System.out.println("Length check : " + (efs.length(fileName, password) == 7));

        String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they oh hope of. General Agreed joy ma";

        String metadata_text = "MA==\n" +
                "dmlnbmVzaA==\n" +
                "0sZUyjOVvMVjDbkCuzi2K4FzZQZ8PkBdOmQtenOASSkk5DitVbVXWK1jZ0l3QexgJuHqXdwVbKqVfbMTP8kTzaxNiAFtSZ9YJaqWxdLqLPYUOjScw+kPGeQamZsRmm8A/ndLUeRFbgq3DisNvrtHyjXbDbHNbkpxCVfMPCgjT/s=\n" +
                "/o7qKoxN04kfWFsbfeEvRNyjaA04WfKdW0fs3jqbJrOCuCj3Sxzo5koDsVzrfWbVQB1NzwEPOg6zYM/E8vFAwyJ5bJm3OY5sBDOAIXffxDMAmaljKvZ8pW+ReK7Xu9Jwg8wKeIQzh3Ry+24jZstxjoEo2N/j+a9EDSAuchaNlRuZ4Er9yPXQ+iPn1evV+B6BJt8Fq4swDyL6c3tt99ZlfzpE+HZOHmu0OmmZKuSXcUlhm5leklBktdjjfHwcFQfYyP+Mmn3Z2yVEvt67TI74DkE+InRajeFNYX+GcAed7sUUB7JZanVfE0lxrr5yZ2ItKd+wynIh4SizIgWS9I+lYg==\n" +
                "MDEyMzQ1Njc4OTk4NzY1NDMyMTAxMjM0NTY3ODkwMTJFk1m1NVaI/o1GY50Brd/g+e0W3Axsu5ctr0MzphSonygfh1DDLY9QHnkhm8YEYOlLROr2WEyZ7G2MOQI0S4jPIKsDswBOYJumzoXzBNxQm8cmnP7WxixyzmghvW4F7EU=\n" +
                "dY3ovmcYMPGpGjXRUPvNHQOMYkBf3nGIyqTeqSlddVr7DndxKHicF5iiKd1LQWqosBTF2N6ePxfDhxkkwWI8cA4C7pRT5gy7vCD10N3JYmvsjsyQHEg9MJZ1ImbqeKe8cFA70NCBC0S8ERIOV5CTokKbteAOg7dNE0c1gvJPJnb1NDc9ZKI51WL1sOBAu0yUtfRmMVX7BGTH1wqPokra71Vto2WjyT4dyDULWdmICJefpD6lAS0M2tEIGlaEBk70NM7xc7KiUa8SCYF0t3XdqPSa4g2Bd5vIxSQz8DlWUcO5r9iopbvL7b9FqOG";

//        Test.save_to_file(metadata_text.getBytes(), new File(fileName, "0"));
        System.out.println(Test.getScore(fileName));
        System.out.println("Length of text : " + text.length());
        efs.write(fileName, 0,
                text.getBytes(StandardCharsets.UTF_8), password);

        String readFile = "/Users/vigneshthirunavukkarasu/Info-sec/text.txt";
        int readPos = 5;
        int readLength = 30;

        byte[] contents = efs.read(readFile, readPos, readLength, password);
        String out = new String(contents);
        System.out.println("Final Output : " + out);
        System.out.println("Output length: " + out.length());


        String f2 = "/Users/vigneshthirunavukkarasu/Info-sec/text2.txt";
        Sample s = new Sample(null);
        System.out.println("Final Output : " + new String(s.read(f2, readPos, readLength, password)));
        System.out.println(s.read(f2, readPos, readLength, password).length);
    }

}
