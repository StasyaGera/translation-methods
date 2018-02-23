import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

class RegexpLexer {
	private InputStream input;
	private int curChar, curPos;
	private String curString;
	private RegexpToken curToken;

	RegexpLexer(InputStream input) throws ParseException {
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
			curToken = RegexpToken.END;
			return;
		}

		curString = "";
		curToken = RegexpToken.END;
		RegexpToken prev = RegexpToken.END;
		while (curToken == RegexpToken.END) {
			curString = curString.concat(Character.toString((char)curChar));
			switch (curString) {
				case "*":
					nextChar();
					curToken = RegexpToken.STAR;
					break;
				case "|":
					nextChar();
					curToken = RegexpToken.GUARD;
					break;
				case "EPS":
					nextChar();
					curToken = RegexpToken.EPS;
					break;
				case "END":
					nextChar();
					curToken = RegexpToken.END;
					break;
				case ")":
					nextChar();
					curToken = RegexpToken.CPAREN;
					break;
				case "(":
					nextChar();
					curToken = RegexpToken.OPAREN;
					break;
				default:
					if (curString.matches("[a-zA-Z]+")) {
						nextChar();
						curToken = RegexpToken.LETTER;
					} else if ((curChar == -1 || isBlank(curChar)) && prev == RegexpToken.END) {
						throw new ParseException("Illegal character '" + curString.charAt(0) + "' at position ", curPos - curString.length());
					}
			}
			if (curToken == RegexpToken.END) {
				if (prev != RegexpToken.END) {
					curString = curString.substring(0, curString.length() - 1);
					curToken = prev;
				} else {
					nextChar();
				}
			} else {
				prev = curToken;
				curToken = RegexpToken.END;
			}
		}
	}

	RegexpToken getCurToken() {
		return curToken;
	}

	int getCurPos() {
		return curPos;
	}

	String getCurString() {
		return curString;
	}
}
