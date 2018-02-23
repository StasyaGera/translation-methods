import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

class NumericLexer {
	private InputStream input;
	private int curChar, curPos;
	private String curString;
	private NumericToken curToken;

	NumericLexer(InputStream input) throws ParseException {
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
		if (curChar == -1) {
			curToken = NumericToken.END;
			return;
		}

		curString = "";
		curToken = NumericToken.END;
		NumericToken prev = NumericToken.END;
		while (curToken == NumericToken.END) {
			curString = curString.concat(Character.toString((char)curChar));
			switch (curString) {
				case "+":
					nextChar();
					curToken = NumericToken.ADD;
					break;
				case "(":
					nextChar();
					curToken = NumericToken.TERM0;
					break;
				case "*":
					nextChar();
					curToken = NumericToken.MUL;
					break;
				case ")":
					nextChar();
					curToken = NumericToken.TERM1;
					break;
				case "EPS":
					nextChar();
					curToken = NumericToken.EPS;
					break;
				case "^^":
					nextChar();
					curToken = NumericToken.POW;
					break;
				case "END":
					nextChar();
					curToken = NumericToken.END;
					break;
				default:
					if (curString.matches("[1-9][0-9]*|0")) {
						nextChar();
						curToken = NumericToken.NUM;
					} else if ((curChar == -1 || isBlank(curChar)) && prev == NumericToken.END) {
						throw new ParseException("Illegal character '" + curString.charAt(0) + "' at position ", curPos - curString.length());
					}
			}
			if (curToken == NumericToken.END) {
				if (prev != NumericToken.END) {
					curString = curString.substring(0, curString.length() - 1);
					curToken = prev;
				} else {
					nextChar();
				}
			} else {
				prev = curToken;
				curToken = NumericToken.END;
			}
		}
	}

	NumericToken getCurToken() {
		return curToken;
	}

	int getCurPos() {
		return curPos;
	}

	String getCurString() {
		return curString;
	}
}
