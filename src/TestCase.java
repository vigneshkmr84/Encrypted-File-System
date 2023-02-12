public class TestCase{
    public static void main(String[] args) throws Exception {

        EFS efs = new EFS(null);

        boolean passwordCheck = efs.verifyPassword("password", "/Users/vigneshthirunavukkarasu/Info-sec/test.txt");
        System.out.println(passwordCheck);
    }
}
