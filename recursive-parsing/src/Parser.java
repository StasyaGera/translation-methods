import java.io.InputStream;
import java.text.ParseException;

public class Parser {
    final static String[] NON_TERMINALS = {"EXPR", "EXPR'", "CONCAT", "CONCAT'", "KLEENE", "KLEENE'"};
    private LexicalAnalyzer analyzer;

    private Tree EXPR() throws ParseException {
        switch (analyzer.getCurToken()) {
            case LPAREN:
            case LETTER:
                return new Tree("EXPR", CONCAT(), EXPR_P());
            default:
                throw new AssertionError();
        }
    }
    private Tree EXPR_P() throws ParseException {
        switch (analyzer.getCurToken()) {
            case RPAREN:
            case END:
                return new Tree("EXPR'");
            case VBAR:
                analyzer.nextToken();
                return new Tree("EXPR'", new Tree("|"), CONCAT(), EXPR_P());
            default:
                throw new AssertionError();
        }
    }
    private Tree CONCAT() throws ParseException {
        switch (analyzer.getCurToken()) {
            case LETTER:
            case LPAREN:
                return new Tree("CONCAT", KLEENE(), CONCAT_P());
            default:
                throw new AssertionError();
        }
    }
    private Tree CONCAT_P() throws ParseException {
        switch (analyzer.getCurToken()) {
            case LETTER:
            case LPAREN:
                return new Tree("CONCAT'", KLEENE(), CONCAT_P());
            case RPAREN:
            case VBAR:
            case END:
                return new Tree("CONCAT'");
            default:
                throw new AssertionError();
        }
    }
    private Tree KLEENE() throws ParseException {
        switch (analyzer.getCurToken()) {
            case LETTER:
                String l = Character.toString(analyzer.getLetter());
                analyzer.nextToken();
                return new Tree("KLEENE", new Tree(l), KLEENE_P());
            case LPAREN:
                analyzer.nextToken();
                Tree e = EXPR();
                if (analyzer.getCurToken() != Token.RPAREN) {
                    throw new ParseException(") expected at position ", analyzer.getCurPos());
                }
                analyzer.nextToken();
                return new Tree("KLEENE", new Tree("("), e, new Tree(")"), KLEENE_P());
            default:
                throw new AssertionError();
        }
    }
    private Tree KLEENE_P() throws ParseException {
        switch (analyzer.getCurToken()) {
            case LETTER:
            case LPAREN:
            case RPAREN:
            case VBAR:
            case END:
                return new Tree("KLEENE'");
            case ASTERISK:
                analyzer.nextToken();
                return new Tree("KLEENE'", new Tree("*"), KLEENE_P());
            case PLUS:
                analyzer.nextToken();
                return new Tree("KLEENE'", new Tree("+"), KLEENE_P());
            case QUESTION:
                analyzer.nextToken();
                return new Tree("KLEENE'", new Tree("?"), KLEENE_P());
            default:
                throw new AssertionError();
        }
    }

    public Tree parse(InputStream input) throws ParseException {
        analyzer = new LexicalAnalyzer(input);
        analyzer.nextToken();
        return EXPR();
    }
}
