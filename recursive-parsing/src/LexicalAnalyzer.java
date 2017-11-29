import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

class LexicalAnalyzer {
    private InputStream input;
    private int curChar, curPos, letter;
    private Token curToken;

    LexicalAnalyzer(InputStream input) throws ParseException {
        this.input = input;
        curPos = 0;
        nextChar();
    }

    private boolean isBlank(int c) {
        return Character.isWhitespace(c);
    }

    private void nextChar() throws ParseException {
        curPos++;
        try {
            curChar = input.read();
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), curPos);
        }
    }

    void nextToken() throws ParseException {
        while (isBlank(curChar)) {
            nextChar();
        }
        switch (curChar) {
            case '(':
                nextChar();
                curToken = Token.LPAREN;
                break;
            case ')':
                nextChar();
                curToken = Token.RPAREN;
                break;
            case '|':
                nextChar();
                curToken = Token.VBAR;
                break;
            case '*':
                nextChar();
                curToken = Token.ASTERISK;
                break;
            case '+':
                nextChar();
                curToken = Token.PLUS;
                break;
            case '?':
                nextChar();
                curToken = Token.QUESTION;
                break;
            case -1:
                curToken = Token.END;
                break;
            default:
                if (Character.isAlphabetic(curChar)) {
                    letter = curChar;
                    nextChar();
                    curToken = Token.LETTER;
                } else {
                    throw new ParseException("Illegal character " + (char)curChar + " at position ", curPos);
                }
        }
    }

    Token getCurToken() {
        return curToken;
    }

    int getCurPos() {
        return curPos;
    }

    char getLetter() {
        return (char)letter;
    }
}