
import java.io.InputStream;
import java.text.ParseException;

public class RegexpParser {

	private RegexpLexer lex;

	public Tree parse(InputStream input) throws ParseException {
		lex = new RegexpLexer(input);
		lex.nextToken();
		return expr();
	}

	private Tree kleene_() throws ParseException {

		Tree tree = null;
		switch (lex.getCurToken()) {
			case EPS: {
tree = new Tree("kleene_");
				break;
			}
			case STAR: {

				lex.nextToken();
				Tree kleene_ = kleene_();
				tree = new Tree("kleene_", new Tree("*"), kleene_);
				break;
			}
			case CPAREN: {
tree = new Tree("kleene_");
				break;
			}
			case GUARD: {
tree = new Tree("kleene_");
				break;
			}
			case LETTER: {
tree = new Tree("kleene_");
				break;
			}
			case END: {
tree = new Tree("kleene_");
				break;
			}
			case OPAREN: {
tree = new Tree("kleene_");
				break;
			}
			default:
				throw new AssertionError();
		}
		return tree;
	}
	private Tree concat_() throws ParseException {

		Tree tree = null;
		switch (lex.getCurToken()) {
			case EPS: {
tree = new Tree("concat_");
				break;
			}
			case CPAREN: {
tree = new Tree("concat_");
				break;
			}
			case GUARD: {
tree = new Tree("concat_");
				break;
			}
			case LETTER: {
				Tree kleene = kleene();
				
				Tree concat_ = concat_();
				tree = new Tree("concat_", kleene, concat_);
				break;
			}
			case END: {
tree = new Tree("concat_");
				break;
			}
			case OPAREN: {
				Tree kleene = kleene();
				
				Tree concat_ = concat_();
				tree = new Tree("concat_", kleene, concat_);
				break;
			}
			default:
				throw new AssertionError();
		}
		return tree;
	}
	private Tree expr() throws ParseException {

		Tree tree = null;
		switch (lex.getCurToken()) {
			case LETTER: {
				Tree concat = concat();
				
				Tree expr_ = expr_();
				tree = new Tree("expr", concat, expr_);
				break;
			}
			case OPAREN: {
				Tree concat = concat();
				
				Tree expr_ = expr_();
				tree = new Tree("expr", concat, expr_);
				break;
			}
			default:
				throw new AssertionError();
		}
		return tree;
	}
	private Tree concat() throws ParseException {

		Tree tree = null;
		switch (lex.getCurToken()) {
			case LETTER: {
				Tree kleene = kleene();
				
				Tree concat_ = concat_();
				tree = new Tree("concat", kleene, concat_);
				break;
			}
			case OPAREN: {
				Tree kleene = kleene();
				
				Tree concat_ = concat_();
				tree = new Tree("concat", kleene, concat_);
				break;
			}
			default:
				throw new AssertionError();
		}
		return tree;
	}
	private Tree expr_() throws ParseException {

		Tree tree = null;
		switch (lex.getCurToken()) {
			case EPS: {
tree = new Tree("expr_");
				break;
			}
			case CPAREN: {
tree = new Tree("expr_");
				break;
			}
			case GUARD: {

				lex.nextToken();
				Tree concat = concat();
				
				Tree expr_ = expr_();
				tree = new Tree("expr_", new Tree("|"), concat, expr_);
				break;
			}
			case END: {
tree = new Tree("expr_");
				break;
			}
			default:
				throw new AssertionError();
		}
		return tree;
	}
	private Tree kleene() throws ParseException {

		Tree tree = null;
		switch (lex.getCurToken()) {
			case LETTER: {
String l = lex.getCurString();
				lex.nextToken();
				Tree kleene_ = kleene_();
				tree = new Tree("kleene", new Tree(l), kleene_);
				break;
			}
			case OPAREN: {

				lex.nextToken();
				Tree expr = expr();
				

				lex.nextToken();
				Tree kleene_ = kleene_();
				tree = new Tree("kleene", new Tree("("), expr, new Tree(")"), kleene_);
				break;
			}
			default:
				throw new AssertionError();
		}
		return tree;
	}
}
