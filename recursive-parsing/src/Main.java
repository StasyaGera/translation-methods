import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Tester tester = new Tester();

        if (args.length != 0) {
            tester.run(args[0]).visualize();
        } else {
            try {
                byte[] encoded = Files.readAllBytes(Paths.get("test.in").toAbsolutePath());
                if (encoded.length != 0) {
                    tester.run(new String(encoded, StandardCharsets.UTF_8.name())).visualize();
                } else {
                    System.out.println("Test not found");
                }
            } catch (IOException e) {
                tester.fail("Could not get test: " + e.getMessage());
            }
        }
    }
}
