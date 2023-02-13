import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestCase{
    public static void main(String[] args) throws Exception {

        EFS efs = new EFS(null);
        String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/test.txt";
        String password = "password";
        boolean passwordCheck = efs.verifyPassword(password, fileName);
        System.out.println("Password check : " + passwordCheck);
        System.out.println("Username check : " + (Objects.equals(efs.findUser(fileName), "vignesh")));
        System.out.println("Length check : " + (efs.length(fileName, password) == 7));

        String text = "Agreed joy vanity regret met may ladies oppose who. Mile fail as left as hard eyes. Meet made call in mean four year it to. Prospect so branched wondered sensible of up. For gay consisted resolving pronounce sportsman saw discovery not. Northward or household as conveying we earnestly believing. No in up contrasted discretion inhabiting excellence. Entreaties we collecting unpleasant at everything conviction. Perpetual sincerity out suspected necessary one but provision satisfied. Respect nothing use set waiting pursuit nay you looking. If on prevailed concluded ye abilities. Address say you new but minuter greater. Do denied agreed in innate. Can and middletons thoroughly themselves him. Tolerably sportsmen belonging in september no am immediate newspaper. Theirs expect dinner it pretty indeed having no of. Principle september she conveying did eat may extensive. On then sake home is am leaf. Of suspicion do departure at extremely he believing. Do know said mind do rent they oh hope of. General enquire picture letters garrets on offices of no on. Say one hearing between excited evening all inhabit thought you. Style begin mr heard by in music tried do. To unreserved projection no introduced invitation." +
                "Apartments simplicity or understood do it we. Song such eyes had and off. Removed winding ask explain delight out few behaved lasting. Letters old hastily ham sending not sex chamber because present. Oh is indeed twenty entire figure. Occasional diminution announcing new now literature terminated. Really regard excuse off ten pulled. Lady am room head so lady four or eyes an. He do of consulted sometimes concluded mr. An household behaviour if pretended.Performed suspicion in certainty so frankness by attention pretended. Newspaper or in tolerably education enjoyment. Extremity excellent certainty discourse sincerity no he so resembled. Joy house worse arise total boy but. Elderly up chicken do at feeling is. Like seen drew no make fond at on rent. Behaviour extremely her explained situation yet september gentleman are who. Is thought or pointed hearing he. Contented get distrusts certainty nay are frankness concealed ham. On unaffected resolution on considered of. No thought me husband or colonel forming effects. End sitting shewing who saw besides son musical adapted. Contrasted interested eat alteration pianoforte sympathize was. He families believed if no elegance interest surprise an. It abode wrong miles an so delay plate. She relation own put outlived may disposed. Difficulty on insensible reasonable in. From as went he they. Preference themselves me as thoroughly partiality considered on in estimating. Middletons acceptance discovered projecting so is so or. In or attachment inquietude remarkably comparison at an. Is surrounded prosperous stimulated am me discretion expression. But truth being state can she china widow. Occasional preference fat remarkably now projecting uncommonly dissimilar. Sentiments projection particular companions interested do at my delightful. Listening newspaper in advantage frankness to concluded unwilling." +
                "Brother set had private his letters observe outward resolve. Shutters ye marriage to throwing we as. Effect in if agreed he wished wanted admire expect. Or shortly visitor is comfort placing to cheered do. Few hills tears are weeks saw. Partiality insensible celebrated is in. Am offended as wandered thoughts greatest an friendly. Evening covered in he exposed fertile to. Horses seeing at played plenty nature to expect we. Young say led stood hills own thing get. Any delicate you how kindness horrible outlived servants. You high bed wish help call draw side. Girl quit if case mr sing as no have. At none neat am do over will. Agreeable promotion eagerness as we resources household to distrusts. Polite do object at passed it is. Small for ask shade water manor think men begin. Repulsive questions contented him few extensive supported. Of remarkably thoroughly he appearance in. Supposing tolerably applauded or of be. Suffering unfeeling so objection agreeable allowance me of. Ask within entire season sex common far who family. As be valley warmth assure on. Park girl they rich hour new well way you. Face ye be me been room we sons fond.";

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
    }

}
