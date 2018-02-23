import java.io.*;
import java.text.ParseException;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) {
		RegexpParser parser = new RegexpParser();
		Tree res;
		try {
			if (args.length != 0) {
				try {
					res = parser.parse(new ByteArrayInputStream(args[0].getBytes(StandardCharsets.UTF_8.name())));
				} catch (UnsupportedEncodingException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			} else {
				try {
					InputStream input = new FileInputStream(new File("input.txt"));
					res = parser.parse(input);
				} catch (FileNotFoundException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		} catch (ParseException e) {
			System.err.println("Parser failed: \nCause: " + e.getMessage() + e.getErrorOffset());
			System.exit(1);
		}
	}
}