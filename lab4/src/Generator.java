import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class Generator {
    final String DIR = "my_gen/";

    final private String NAME;
    private String header, members;
    private Grammar grammar;

    Generator(Grammar grammar, String name, String header, String members) {
        this.grammar = grammar;
        this.NAME = name;
        this.header = header;
        this.members = members;
    }

    void generateAll() throws FileNotFoundException {
        generateMain();
        generateLexer();
        generateParser();
        generateToken();
    }

    void generateToken() throws FileNotFoundException {
        String TokenName = NAME + "Token";
        File file = new File(DIR, TokenName + ".java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("public enum " + TokenName + " {");
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
        String LexerName = NAME + "Lexer";
        File file = new File(DIR, LexerName + ".java");
        try (PrintWriter out = new PrintWriter(file)) {
            // imports
            out.println("import java.io.IOException;");
            out.println("import java.io.InputStream;");
            out.println("import java.text.ParseException;\n");

            out.println("class " + LexerName + " {");
            //members
            out.println("\tprivate InputStream input;");
            out.println("\tprivate int curChar, curPos;");
            out.println("\tprivate String curString;");
            out.println("\tprivate " + NAME + "Token curToken;\n");
            // ctor
            out.println("\t" + LexerName + "(InputStream input) throws ParseException {");
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
            out.println("\t\tif (curChar == -1) {\n\t\t\tcurToken = " + NAME + "Token.END;\n\t\t\treturn;\n\t\t}\n");
            out.println("\t\tcurString = \"\";");
            out.println("\t\tcurToken = " + NAME + "Token.END;");
            out.println("\t\t" + NAME + "Token prev = " + NAME + "Token.END;");
            out.println("\t\twhile (curToken == " + NAME + "Token.END) {");
            out.println("\t\t\tcurString = curString.concat(Character.toString((char)curChar));");
            out.println("\t\t\tswitch (curString) {");
            for (Map.Entry<String, Terminal> t_entry : grammar.terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (String str : curr_t.str) {
                    out.println("\t\t\t\tcase \"" + str + "\":");
                    out.println("\t\t\t\t\tnextChar();");
                    out.println("\t\t\t\t\tcurToken = " + NAME + "Token." + curr_t.name.toUpperCase() + ";");
                    out.println("\t\t\t\t\tbreak;");
                }
            }
            out.println("\t\t\t\tdefault:");
            out.print("\t\t\t\t\t");
            for (Map.Entry<String, Terminal> t_entry : grammar.terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (String regex : curr_t.regex) {
                    out.println("if (curString.matches(\"" + regex.substring(1, regex.length() - 1) + "\")) {");
                    out.println("\t\t\t\t\t\tnextChar();");
                    out.println("\t\t\t\t\t\tcurToken = " + NAME + "Token." + curr_t.name.toUpperCase() + ";");
                    out.print("\t\t\t\t\t} else ");
                }
            }
            out.println("if ((curChar == -1 || isBlank(curChar)) && prev == " + NAME + "Token.END) {");
            out.println("\t\t\t\t\t\tthrow new ParseException(\"Illegal character '\" + curString.charAt(0) + \"' at position \", curPos - curString.length());");
            out.println("\t\t\t\t\t}");
            out.println("\t\t\t}");

            out.println("\t\t\tif (curToken == " + NAME + "Token.END) {");
            out.println("\t\t\t\tif (prev != " + NAME + "Token.END) {");
            out.println("\t\t\t\t\tcurString = curString.substring(0, curString.length() - 1);");
            out.println("\t\t\t\t\tcurToken = prev;");
            out.println("\t\t\t\t} else {");
            out.println("\t\t\t\t\tnextChar();");
            out.println("\t\t\t\t}");

            out.println("\t\t\t} else {");
            out.println("\t\t\t\tprev = curToken;");
            out.println("\t\t\t\tcurToken = " + NAME + "Token.END;");
            out.println("\t\t\t}");
            out.println("\t\t}");
            out.println("\t}");

            out.println("\n\t" + NAME + "Token getCurToken() {");
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

    private String defaultValue(String name) {
        if (Character.isUpperCase(name.charAt(0))) return "null";
        else {
            if (name.equals("boolean"))
                return "false";
            else
                return "0";
        }
    }

    void generateParser() throws FileNotFoundException {
        String ParserName = NAME + "Parser";
        String LexerName = NAME + "Lexer";
        File file = new File(DIR, ParserName + ".java");
        try (PrintWriter out = new PrintWriter(file)) {
            // user-defined imports
            out.println(header);
            // imports
            out.println("import java.io.InputStream;");
            out.println("import java.text.ParseException;\n");

            // class header
            out.println("public class " + ParserName + " {");
            // user-defined members
            out.println(members);
            // members
            out.println("\tprivate " + LexerName + " lex;\n");
            // main func, return type same as starting nonterm
            out.println("\tpublic " + grammar.start.returnType() + " parse(InputStream input) throws ParseException {");
            out.println("\t\tlex = new " + LexerName + "(input);");
            out.println("\t\tlex.nextToken();");
            out.print("\t\t");
            if (!grammar.start.returnType().equals("void"))
                out.print("return ");
            out.println(grammar.start.name + "();");
            out.println("\t}\n");

            Map<NonTerminal, Map<Terminal, Rule>> table = grammar.getTable();
            for (Map.Entry<String, NonTerminal> nt_entry : grammar.nonTerminals.entrySet()) {
                NonTerminal curr_nt = nt_entry.getValue();
                // return type & name for a nonterm
                out.print("\tprivate " + curr_nt.returnType() + " " + nt_entry.getKey() + "(");
                // inherited attrs as func args
                if (curr_nt.inh.size() != 0) {
                    out.print(curr_nt.inh.get(0).type + " " + curr_nt.inh.get(0).name);
                }
                for (int i = 1; i < curr_nt.inh.size(); i++) {
                    out.print(", " + curr_nt.inh.get(i).type + " " + curr_nt.inh.get(i).name);
                }
                out.println(") throws ParseException {");
                out.println(curr_nt.init);
                // declare ret val
                if (!curr_nt.returnType().equals("void")) {
                    out.println("\t\t" + curr_nt.returnType() + " " + curr_nt.synth.name + " = " + defaultValue(curr_nt.returnType()) + ";");
                }

                // here start the rules
                out.println("\t\tswitch (lex.getCurToken()) {");
                for (Map.Entry<Terminal, Rule> entry : table.get(nt_entry.getValue()).entrySet()) {
                    // a case for each possible term
                    out.println("\t\t\tcase " + entry.getKey().name.toUpperCase() + ":\n\t\t\t{");
                    boolean own = !entry.getValue().head().name.equals(grammar.EPS.name);
                    Rule rule = entry.getValue();
                    for (int i = 0; i < rule.units.size(); i++) {
                        Element elem = rule.units.get(i).element;
                        if (elem instanceof Terminal) {
                            out.println(rule.units.get(i).code);
                            if (own) {
                                out.println("\t\t\t\tlex.nextToken();");
                            }
                        } else {
                            // here we going to call nt
                            if (!elem.returnType().equals("void")) {
                                out.print("\t\t\t\t" + elem.returnType() + " " + elem.name + " = " + elem.name + "(");
                                if (rule.units.get(i).args.size() != 0) {
                                    out.print(rule.units.get(i).args.get(0));
                                }
                                for (int j = 1; j < rule.units.get(i).args.size(); j++) {
                                    out.print(", " + rule.units.get(i).args.get(j));
                                }
                                out.println(");");
                            } else {
                                out.println("\t\t\t\t" + elem.name + "();");
                            }
                            out.println(rule.units.get(i).code);
                        }
                    }
                    out.println("\t\t\t\tbreak;\n\t\t\t}");
                }

                out.print(
                        "\t\t\tdefault:\n" +
                                "\t\t\t\tthrow new AssertionError();\n" +
                                "\t\t}\n"
                );
                if (!curr_nt.returnType().equals("void")) {
                    out.println("\t\treturn " + curr_nt.synth.name + ";");
                }
                out.println("\t}");
            }

            out.print("}\n");
        }
    }

    void generateMain() throws FileNotFoundException {
        File file = new File(DIR, "Main.java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("import java.io.*;");
            out.println("import java.text.ParseException;");
            out.println("import java.nio.charset.StandardCharsets;");

            out.println("\npublic class Main {");
            out.println("\tpublic static void main(String[] args) {");
            out.println("\t\t" + NAME + "Parser parser = new " + NAME + "Parser();");
            if (!grammar.start.returnType().equals("void")) {
                out.println("\t\t" + grammar.start.returnType() + " res;");
            }
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
