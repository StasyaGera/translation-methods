import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Random;

public class Tester {
    private final char[] symbols = {'(', ')', '*', '|', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
    private final Random random = new Random(System.currentTimeMillis());
    private final Parser parser = new Parser();

    public static void main(String[] args) {
        Tester instance = new Tester();

        instance.test(100, 100);
        instance.test(100, 10_000);
    }

    void fail(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    Tree run(String test) {
        Tree res = null;
        try {
            res = parser.parse(new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8.name())));
        } catch (ParseException e) {
            fail("Parser failed on string: " + test + "\nCause: " + e.getMessage() + e.getErrorOffset());
        } catch (AssertionError e) {
            fail("Assertion error from parser on test: " + test);
        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }

        assert res != null && (test.equals(res.getFullString()));
        return res;
    }

    private void test(int amount, int length) {
        for (int i = amount; i > 0; i--) {
            run(genString(length));
        }
        System.out.println("PASSED " + amount + " tests on strings of length " + length);
    }

    private String genString(int l) {
        StringBuilder str = new StringBuilder();
        int parens = 0;
        char prev = '\0';
        for (int i = l; i > 0; i--) {
            char next = symbols[random.nextInt(symbols.length - 1)];
            boolean append = true;
            switch (next) {
                case '(':
                    parens++;
                    break;
                case ')':
                    if (parens > 0 && prev != '(' && prev != '|') {
                        parens--;
                    } else {
                        append = false;
                    }
                    break;
                case '*':
                    if (prev == '\0' || prev == '*' || prev == '|' || prev == '(') {
                        append = false;
                    }
                    break;
                case '|':
                    if (prev == '\0' || prev == '|' || prev == '(') {
                        append = false;
                    }
                    break;
                default:
                    break;
            }
            if (append) {
                str.append(next);
                prev = next;
            } else {
                i++;
            }
        }
        if (prev == '(' || prev == '|') {
            str.append('a');
        }
        for (int i = parens; i > 0; i--) {
            str.append(')');
        }
        return str.toString();
    }
}
