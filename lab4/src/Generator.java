import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class Generator {
    final String dir = "my_gen/";
    private Grammar grammar;

    Generator(Grammar grammar) {
        this.grammar = grammar;
    }

    void generateAll() throws FileNotFoundException {
        generateMain();
        generateLexer();
        generateParser();
        generateToken();
    }

    void generateToken() throws FileNotFoundException {
        File file = new File(dir, "Token.java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("public enum Token {");
            StringBuilder sb = new StringBuilder();
            for (String term : grammar.terminals.keySet()) {
                sb.append("\t").append(term.toUpperCase()).append(", \n");
            }
            sb.delete(sb.length() - 3, sb.length());
            out.println(sb.toString());
            out.println("}");
        }
    }

    void generateLexer() throws FileNotFoundException {
        final String LEXER_NAME = "Lexer";
        File file = new File(dir, LEXER_NAME + ".java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("import java.io.IOException;");
            out.println("import java.io.InputStream;");
            out.println("import java.text.ParseException;\n");

            out.println("class " + LEXER_NAME + " {");
            out.println("\tprivate InputStream input;");
            out.println("\tprivate int curChar, curPos;");
            out.println("\tprivate String curString;");
            out.println("\tprivate Token curToken;\n");

            out.println("\t" + LEXER_NAME + "(InputStream input) throws ParseException {");
            out.println("\t\tthis.input = input;");
            out.println("\t\tcurPos = 0;");
            out.println("\t\tnextChar();");
            out.println("\t}\n");

            out.println("\tprivate boolean isBlank(int c) {\n\t\treturn Character.isWhitespace(c);\n\t}\n");

            out.println("\tprivate void nextChar() throws ParseException {");
            out.println("\t\tcurPos++;");
            out.println("\t\ttry {");
            out.println("\t\t\tcurChar = input.read();");
            out.println("\t\t} catch (IOException e) {");
            out.println("\t\t\tthrow new ParseException(e.getMessage(), curPos);");
            out.println("\t\t}");
            out.println("\t}\n");

            out.println("\tvoid nextToken() throws ParseException {");
            out.println("\t\twhile (isBlank(curChar)) {\n\t\t\tnextChar();\n\t\t}");
            out.println("\t\tif (curChar == -1) {\n\t\t\tcurToken = Token.END;\n\t\t\treturn;\n\t\t}\n");
            out.println("\t\tcurString = \"\";");
            out.println("\t\tcurToken = Token.END;");
            out.println("\t\twhile (curToken == Token.END) {");
            out.println("\t\t\tcurString = curString.concat(Character.toString((char)curChar));");
            out.println("\t\t\tswitch (curString) {");
            for (Map.Entry<String, Terminal> t_entry : grammar.terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (String str : curr_t.str) {
                    out.println("\t\t\t\tcase \"" + str + "\":");
                    out.println("\t\t\t\t\tnextChar();");
                    out.println("\t\t\t\t\tcurToken = Token." + curr_t.name.toUpperCase() + ";");
                    out.println("\t\t\t\t\tbreak;");
                }
            }
            out.println("\t\t\t\tdefault:");
            out.println("\t\t\t\t\tnextChar();");
            out.print("\t\t\t\t\t");
            for (Map.Entry<String, Terminal> t_entry : grammar.terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (String regex : curr_t.regex) {
                    out.println("if (curString.matches(\"" + regex + "\")) {");
                    out.println("\t\t\t\t\t\tcurToken = Token." + curr_t.name.toUpperCase() + ";");
                    out.print("\t\t\t\t\t} else ");
                }
            }
            out.println("if (curChar == -1 || isBlank(curChar)) {");
            out.println("\t\t\t\t\t\tthrow new ParseException(\"Illegal character '\" + curString.charAt(0) + \"' at position \", curPos - curString.length());");
            out.println("\t\t\t\t\t}");
            out.println("\t\t\t}");
            out.println("\t\t}");
            out.println("\t}");

            out.println("\n\tToken getCurToken() {");
            out.println("\t\treturn curToken;");
            out.println("\t}");

            out.println("\n\tint getCurPos() {");
            out.println("\t\treturn curPos;");
            out.println("\t}");

            out.println("\n\tString getCurString() {");
            out.println("\t\treturn curString;");
            out.println("\t}");

            out.println("}");
        }
    }

    void generateParser() throws FileNotFoundException {
        final String PARSER_NAME = "Parser";
        final String LEXER_NAME = "Lexer";
        File file = new File(dir, PARSER_NAME + ".java");
        try(PrintWriter out = new PrintWriter(file)) {
            out.println("import java.io.InputStream;");
            out.println("import java.text.ParseException;\n");

            out.println("public class " + PARSER_NAME + " {");
            out.println("\tprivate " + LEXER_NAME + " lex;\n");

            out.println("\tpublic Tree parse(InputStream input) throws ParseException {");
            out.println("\t\tlex = new " + LEXER_NAME + "(input);");
            out.println("\t\tlex.nextToken();");
            out.println("\t\treturn " + grammar.start.name + "();");
            out.println("\t}\n");

            Map<NonTerminal, Map<Terminal, Transition>> table = grammar.getTable();
            for (Map.Entry<String, NonTerminal> nt_entry : grammar.nonTerminals.entrySet()) {
                out.println("\tprivate Tree " + nt_entry.getKey() + "() throws ParseException {");
                out.println("\t\tswitch (lex.getCurToken()) {");

                int cnt = 0;
                for (Map.Entry<Terminal, Transition> entry : table.get(nt_entry.getValue()).entrySet()) {
                    out.println("\t\t\tcase " + entry.getKey().name.toUpperCase() + ":");
                    StringBuilder call = new StringBuilder("\t\t\t\treturn new Tree(\"" + nt_entry.getKey() + "\", ");
                    boolean own = !entry.getValue().first().name.equals(grammar.EPS.name);
                    for (Element elem : entry.getValue().elements) {
                        if (elem.isTerminal()) {
                            if (own) {
                                out.println("\t\t\t\tString name" + Integer.toString(cnt) + " = lex.getCurString();");
                                out.println("\t\t\t\tlex.nextToken();");
                                call.append("new Tree(name").append(Integer.toString(cnt++)).append("), ");
                            }
                        } else {
                            out.println("\t\t\t\tTree tree" + Integer.toString(cnt) + " = " + elem.name + "();");
                            call.append("tree").append(Integer.toString(cnt++)).append(", ");
                        }
                    }
                    call.delete(call.length() - 2, call.length());
                    call.append(");");
                    out.println(call.toString());
                }

                out.print(
                        "\t\t\tdefault:\n" +
                                "\t\t\t\tthrow new AssertionError();\n" +
                                "\t\t}\n" +
                                "\t}\n"
                );
            }

            out.print("}\n");
        }
    }

    void generateMain() throws FileNotFoundException {
        File file = new File(dir, "Main.java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("import java.io.*;");
            out.println("import java.text.ParseException;");
            out.println("import java.nio.charset.StandardCharsets;");

            out.println("\npublic class Main {");
            out.println("\tpublic static void main(String[] args) {");
            out.println("\t\tParser parser = new Parser();");
            out.println("\t\tTree res;");
            out.println("\t\ttry {");
            out.println("\t\t\tif (args.length != 0) {");
            out.println("\t\t\t\ttry {");
            out.println("\t\t\t\t\tres = parser.parse(new ByteArrayInputStream(args[0].getBytes(StandardCharsets.UTF_8.name())));");
            out.println("\t\t\t\t} catch (UnsupportedEncodingException e) {");
            out.println("\t\t\t\t\tSystem.err.println(e.getMessage());");
            out.println("\t\t\t\t\tSystem.exit(1);");
            out.println("\t\t\t\t}");
            out.println("\t\t\t} else {");
            out.println("\t\t\t\ttry {");
            out.println("\t\t\t\t\tInputStream input = new FileInputStream(new File(\"input.txt\"));");
            out.println("\t\t\t\t\tres = parser.parse(input);");
            out.println("\t\t\t\t} catch (FileNotFoundException e) {");
            out.println("\t\t\t\t\tSystem.err.println(e.getMessage());");
            out.println("\t\t\t\t\tSystem.exit(1);");
            out.println("\t\t\t\t}");
            out.println("\t\t\t}");
            out.println("\t\t} catch (ParseException e) {");
            out.println("\t\t\tSystem.err.println(\"Parser failed: \\nCause: \" + e.getMessage() + e.getErrorOffset());");
            out.println("\t\t\tSystem.exit(1);");
            out.println("\t\t}");
            out.println("\t}");
            out.print("}");
        }
    }
}
