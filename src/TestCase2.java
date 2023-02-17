
public class TestCase2 {

    public static void main(String[] args) throws Exception {
        EFS efs = new EFS(null);
        String userName = "vignesh";
        String password = "pass";

        String fileName = "/Users/vigneshthirunavukkarasu/Info-sec/hmac.txt";

        System.out.println(efs.getScore(fileName));
        String originalMessage = "wkGzcan0NzzqKZoa53tP9B2jWoBWo9VFFdfTkphr9RMldAmNRrWd5qTo4twEva8asF9HFBEzjIyUN6Mq6c76JzaMRVNthBCo9TXLyJKjtE5TzqmHzBrd88AKW9juc4LJZcpyHNvSymT8n0M8W5AzyS1hDfVcBMKPaJDYN9fCRuDZT24HED84gAsBbEeEthmGATBm7SsPeOjRP8Y5A2nw8lfyIfdNfjQzWOYqCTIiGl0nQvZeF2d1i5uuJhRQKba5MNfpeA2Is8IfNO3nePLytpqssAv8Hk0j98e1wnHvAV751pAjLJHGg19JNcIW5O3Gk8eHIB8OY8ZplpVVf0UPhsbNUgSV5Mxy04z6JueFxFOS0Lq3GEf4W900KxMuInCHgswlIVqy3FDTQzzKlasts6t5YqmL42e74cCheTHaHdFK8jJbeRzbctMGSph9IUAK6k06FJ1FajcrZFbavWtDxLH0tRyFkI0GKKD0tJVgLo7BPm5ixXwxx9xJO7ZgPk7efBWmQYBF7yCb4Z1P4C7aVMunpS6JVn3mSBYPK63lfPYaom4S5lJCj0C6NdXAV8BsuZzfEmg0JS8J8cpHjtOjJDMBXgpr2QmWKr7U8iImDAZdpFw6K9c1jRbW9WTyAg8al05dYItbAzCSecBBx6qKewQMBzo0zSoYtcdrK4cRcrgWKpclRuOmS52fijrYWZHgX2kb0yDIOCwSawPfE6kkovEPS3ainYXPXG1KkTe93ua09EsGuojMaIRNretWMNggdgF9TBa79S7WkJVSLmKJMgQ1bYUQ3UJusPhmGNXoOo2xn0YGbNwmF59kNtEosJrP0vZMQfUl4xg1i0GdeUbF9d5MBKRuYN3iFZrieMZHTMtKp83ZG0zMYXl4EvPI8dXLssUbhFGBHlrBSczhOK4NX5XfLuyuyeNSGMJuxOxpxjBYltQne4REpkURz5rHm2HdVe8CY9583NvEyAjOOIFNt1QABIwuvb40i6eQUaY28SAlMgFKjlqY1n7TduYt9cwi";
//        efs.create(fileName, userName, password);


        byte[] toWrite = "test writing".getBytes();
//        efs.write(fileName, 3, toWrite, password);

//        efs.cut(fileName, 100, password);

        byte[] readOut = efs.read(fileName, 0, 39, password);
        System.out.println(new String(readOut));

        System.out.println("Integrity : " + efs.check_integrity(fileName, password));
    }
}
