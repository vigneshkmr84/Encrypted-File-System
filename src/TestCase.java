import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestCase{
    public static void main(String[] args) throws Exception {

        EFS efs = new EFS(null);
        String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/text.txt";
//        String password = "2b7e151628aed2a6abf71589";
        String password = "password";
        boolean passwordCheck = efs.verifyPassword(password, fileName);
        System.out.println("Password check : " + passwordCheck);
        System.out.println("Username check : " + (Objects.equals(efs.findUser(fileName), "vignesh")));
        System.out.println("Length check : " + (efs.length(fileName, password) == 48));

        String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they oh hope of. General Agreed joy ma";

        /*String metadata_text = "MA==\n" +
                "dmlnbmVzaA==\n" +
                "0sZUyjOVvMVjDbkCuzi2K4FzZQZ8PkBdOmQtenOASSkk5DitVbVXWK1jZ0l3QexgJuHqXdwVbKqVfbMTP8kTzaxNiAFtSZ9YJaqWxdLqLPYUOjScw+kPGeQamZsRmm8A/ndLUeRFbgq3DisNvrtHyjXbDbHNbkpxCVfMPCgjT/s=\n" +
                "/o7qKoxN04kfWFsbfeEvRNyjaA04WfKdW0fs3jqbJrOCuCj3Sxzo5koDsVzrfWbVQB1NzwEPOg6zYM/E8vFAwyJ5bJm3OY5sBDOAIXffxDMAmaljKvZ8pW+ReK7Xu9Jwg8wKeIQzh3Ry+24jZstxjoEo2N/j+a9EDSAuchaNlRuZ4Er9yPXQ+iPn1evV+B6BJt8Fq4swDyL6c3tt99ZlfzpE+HZOHmu0OmmZKuSXcUlhm5leklBktdjjfHwcFQfYyP+Mmn3Z2yVEvt67TI74DkE+InRajeFNYX+GcAed7sUUB7JZanVfE0lxrr5yZ2ItKd+wynIh4SizIgWS9I+lYg==\n" +
                "MDEyMzQ1Njc4OTk4NzY1NDMyMTAxMjM0NTY3ODkwMTJFk1m1NVaI/o1GY50Brd/g+e0W3Axsu5ctr0MzphSonygfh1DDLY9QHnkhm8YEYOlLROr2WEyZ7G2MOQI0S4jPIKsDswBOYJumzoXzBNxQm8cmnP7WxixyzmghvW4F7EU=\n" +
                "dY3ovmcYMPGpGjXRUPvNHQOMYkBf3nGIyqTeqSlddVr7DndxKHicF5iiKd1LQWqosBTF2N6ePxfDhxkkwWI8cA4C7pRT5gy7vCD10N3JYmvsjsyQHEg9MJZ1ImbqeKe8cFA70NCBC0S8ERIOV5CTokKbteAOg7dNE0c1gvJPJnb1NDc9ZKI51WL1sOBAu0yUtfRmMVX7BGTH1wqPokra71Vto2WjyT4dyDULWdmICJefpD6lAS0M2tEIGlaEBk70NM7xc7KiUa8SCYF0t3XdqPSa4g2Bd5vIxSQz8DlWUcO5r9iopbvL7b9FqOG";*/


        String metadata_text = "NjI0\n" +
                "dmlnbmVzaA==\n" +
                "nPJ3nEzy/gMCVcjWjU+Bo6RPg6SrF+xMUoADoiv7g3IfVuLRVrzeaQiIY8wkYCYsw7tMCxrKELi1aE3N4eEeLGS+e8rB0IY6Tvsv2wYfRBryYK635uhF/mu3GNfi6/PJNtlLfwb4eBjvT8eOW6TrFyeRzmA9iY5wwOKJW/XgKPw=\n" +
                "2b7e151628aed2a6abf715892b7e151628aed2a6abf71589opegxndtaMv1QXjPypYKgkk3sRAGu5JlSsVuIOmn5yLtOLKJeGFRaGDy4qN3dz9IgwAH8Utvq6uYIlKSEj1Xj8jLqWx87Q1teR7XldMif65VWMHw0Io4l4bBUEuUuYOh9c0eG2COv6yCgbhgR32I3EICyZQIg8hBSUerz6aX07WhbIatzhmXyyPLUad3pQnejYjRlBf6A1bV7mEL2tC5zY23DmSnjmvc\n" +
                "MEhpcFRlVk1NeDJxMEJhRHRzUnhXTFpHajFSWmRTTENySUdXTGZKNkhiZjlqMEFMZ3V5aXVObmNJUldocW9XSkM1UWRJRVM0a2tXaU94SFpyWjFRRjgzTzkybFZ1ZHBCYXdNWWFJRERyTll3SGNGek0wRWdUY0thd09iTFVjOXQ=";


//        Test.save_to_file(metadata_text.getBytes(), new File(fileName, "0"));
        System.out.println("Score : " + efs.getScore(fileName));
        System.out.println("Length of text : " + text.length());
        /*efs.write(fileName, 0,
                text.getBytes(StandardCharsets.UTF_8), password);*/

        String readFile = "/Users/vigneshthirunavukkarasu/Info-sec/text.txt";
        int readPos = 5;
        int readLength = 30;

        // wkGzcan0NzzqKZoa53tP9B2jWoBWo9VFFdfTkphr9RMldAmNRrWd5qTo4twEva8asF9HFBEzjIyUN6Mq6c76JzaMRVNthBCo9TXLyJKjtE5TzqmHzBrd88AKW9juc4LJZcpyHNvSymT8n0M8W5AzyS1hDfVcBMKPaJDYN9fCRuDZT24HED84gAsBbEeEthmGATBm7SsPeOjRP8Y5A2nw8lfyIfdNfjQzWOYqCTIiGl0nQvZeF2d1i5uuJhRQKba5MNfpeA2Is8IfNO3nePLytpqssAv8Hk0j98e1wnHvAV751pAjLJHGg19JNcIW5O3Gk8eHIB8OY8ZplpVVf0UPhsbNUgSV5Mxy04z6JueFxFOS0Lq3GEf4W900KxMuInCHgswlIVqy3FDTQzzKlasts6t5YqmL42e74cCheTHaHdFK8jJbeRzbctMGSph9IUAK6k06FJ1FajcrZFbavWtDxLH0tRyFkI0GKKD0tJVgLo7BPm5ixXwxx9xJO7ZgPk7efBWmQYBF7yCb4Z1P4C7aVMunpS6JVn3mSBYPK63lfPYaom4S5lJCj0C6NdXAV8BsuZzfEmg0JS8J8cpHjtOjJDMBXgpr2QmWKr7U8iImDAZdpFw6K9c1jRbW9WTyAg8al05dYItbAzCSecBBx6qKewQMBzo0zSoYtcdrK4cRcrgWKpclRuOmS52fijrYWZHgX2kb0yDIOCwSawPfE6kkovEPS3ainYXPXG1KkTe93ua09EsGuojMaIRNretWMNggdgF9TBa79S7WkJVSLmKJMgQ1bYUQ3UJusPhmGNXoOo2xn0YGbNwmF59kNtEosJrP0vZMQfUl4xg1i0GdeUbF9d5MBKRuYN3iFZrieMZHTMtKp83ZG0zMYXl4EvPI8dXLssUbhFGBHlrBSczhOK4NX5XfLuyuyeNSGMJuxOxpxjBYltQne4REpkURz5rHm2HdVe8CY9583NvEyAjOOIFNt1QABIwuvb40i6eQUaY28SAlMgFKjlqY1n7TduYt9cwi
        byte[] contents = efs.read(readFile, readPos, readLength, password);
        String out = new String(contents);
        System.out.println("Final Output : " + out);
        System.out.println("Output length: " + out.length());

        efs.write(fileName, 36, "this is a test".getBytes(), password);

        /*String f2 = "/Users/vigneshthirunavukkarasu/Info-sec/text2.txt";
        Sample s = new Sample(null);
        System.out.println("Final Output : " + new String(s.read(f2, readPos, readLength, password)));
        System.out.println(s.read(f2, readPos, readLength, password).length);*/
    }

}
