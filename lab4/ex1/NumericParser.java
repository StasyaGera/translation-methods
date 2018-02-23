
import java.io.InputStream;
import java.text.ParseException;

public class NumericParser {

    private NumericLexer lex;

    public int parse(InputStream input) throws ParseException {
        lex = new NumericLexer(input);
        lex.nextToken();
        return add();
    }

    private int add() throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case TERM0: {
                int mul = mul();

                int add_ = add_("h", mul);
                val = add_;
                break;
            }
            case NUM: {
                int mul = mul();

                int add_ = add_("h", mul);
                val = add_;
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }

    private int add_(String hello, int acc) throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case END: {
                val = acc;
                break;
            }
            case TERM1: {
                val = acc;
                break;
            }
            case ADD: {

                lex.nextToken();
                int mul = mul();
                int res = acc + mul;
                int add_ = add_(hello, res);
                val = add_;
                break;
            }
            case EPS: {
                val = acc;
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }

    private int pow_() throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case END: {
                val = 1;
                break;
            }
            case MUL: {
                val = 1;
                break;
            }
            case TERM1: {
                val = 1;
                break;
            }
            case ADD: {
                val = 1;
                break;
            }
            case EPS: {
                val = 1;
                break;
            }
            case POW: {

                lex.nextToken();
                int term = term();

                int pow_ = pow_();
                val = (int) Math.pow(term, pow_);
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }

    private int mul() throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case TERM0: {
                int pow = pow();

                int mul_ = mul_(pow);
                val = mul_;
                break;
            }
            case NUM: {
                int pow = pow();

                int mul_ = mul_(pow);
                val = mul_;
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }

    private int mul_(int acc) throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case END: {
                val = acc;
                break;
            }
            case MUL: {

                lex.nextToken();
                int pow = pow();
                int res = acc * pow;
                int mul_ = mul_(res);
                val = mul_;
                break;
            }
            case TERM1: {
                val = acc;
                break;
            }
            case ADD: {
                val = acc;
                break;
            }
            case EPS: {
                val = acc;
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }

    private int pow() throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case TERM0: {
                int term = term();

                int pow_ = pow_();
                val = (int) Math.pow(term, pow_);
                break;
            }
            case NUM: {
                int term = term();

                int pow_ = pow_();
                val = (int) Math.pow(term, pow_);
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }

    private int term() throws ParseException {

        int val = 0;
        switch (lex.getCurToken()) {
            case TERM0: {

                lex.nextToken();
                int add = add();

                val = add;
                lex.nextToken();
                break;
            }
            case NUM: {
                val = Integer.parseInt(lex.getCurString());
                lex.nextToken();
                break;
            }
            default:
                throw new AssertionError();
        }
        return val;
    }
}
