import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class Generator {
    private final String DIR = "my_gen/";
    private final String NAME;
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
        generateToken();
        generateLexer();
        generateParser();
    }

    void generateToken() throws FileNotFoundException {
        String TokenName = NAME + "Token";
        File file = new File(DIR, TokenName + ".java");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("public enum " + TokenName + " {");
            // printing all the terminals' names as enum members
            StringBuilder sb = new StringBuilder();
            for (String term : grammar.terminals.keySet()) {
                sb.append("\t").append(term.toUpperCase()).append(", \n");
            }
            // an easy way to remove last comma
            if (sb.length() >= 3)
                out.println(sb.delete(sb.length() - 3, sb.length()).toString());
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
            // members
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
            // isBlank func
            // if your grammar allows to redefine blank characters, change it
            out.println("\tprivate boolean isBlank(int c) {\n\t\treturn Character.isWhitespace(c);\n\t}\n");
            // nextChar func
            out.println("\tprivate void nextChar() throws ParseException {");
            out.println("\t\tcurPos++;");
            out.println("\t\ttry {");
            out.println("\t\t\tcurChar = input.read();");
            out.println("\t\t} catch (IOException e) {");
            out.println("\t\t\tthrow new ParseException(e.getMessage(), curPos);");
            out.println("\t\t}");
            out.println("\t}\n");
            // nextToken func
            out.println("\tvoid nextToken() throws ParseException {");
            // skip blanks
            out.println("\t\twhile (isBlank(curChar)) {\n\t\t\tnextChar();\n\t\t}");
            // if eof happens, return END token
            out.println("\t\tif (curChar == -1) {\n\t\t\tcurToken = " + NAME + "Token.END;\n\t\t\treturn;\n\t\t}\n");
            out.println("\t\tcurString = \"\";");
            out.println("\t\tcurToken = " + NAME + "Token.END;");
            out.println("\t\t" + NAME + "Token prev = " + NAME + "Token.END;");
            // greedily reading forward to be able to match string tokens
            out.println("\t\twhile (curToken == " + NAME + "Token.END) {");
            out.println("\t\t\tcurString = curString.concat(Character.toString((char)curChar));");
            out.println("\t\t\tswitch (curString) {");
            // printing all terminals as switch cases
            // if one matches, read next char and write the corresponding token to curToken
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
            // here we print all the regexp representations of terminals
            for (Map.Entry<String, Terminal> t_entry : grammar.terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (String regex : curr_t.regex) {
                    out.println("if (curString.matches(\"" + regex.substring(1, regex.length() - 1) + "\")) {");
                    out.println("\t\t\t\t\t\tnextChar();");
                    out.println("\t\t\t\t\t\tcurToken = " + NAME + "Token." + curr_t.name.toUpperCase() + ";");
                    out.print("\t\t\t\t\t} else ");
                }
            }
            // we went all the way to the eof and could not determine char sequence
            out.println("if ((curChar == -1 || isBlank(curChar)) && prev == " + NAME + "Token.END) {");
            out.println("\t\t\t\t\t\tthrow new ParseException(\"Illegal character '\" + curString.charAt(0) + \"' at position \", curPos - curString.length());");
            out.println("\t\t\t\t\t}");
            out.println("\t\t\t}");
            // confusing schemes to check if we need to continue reading
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

            // some useless getters
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
            if (!grammar.start.returnType().equals("void")) out.print("return ");
            out.println(grammar.start.name + "();");
            out.println("\t}\n");

            // generate a function for each nonterm
            Map<NonTerminal, Map<Terminal, Rule>> table = grammar.getTable();
            for (Map.Entry<String, NonTerminal> nt_entry : grammar.nonTerminals.entrySet()) {
                NonTerminal curr_nt = nt_entry.getValue();
                // return type & name for a nonterm
                out.print("\tprivate " + curr_nt.returnType() + " " + nt_entry.getKey() + "(");
                // inherited attrs as func args
                StringBuilder inh = new StringBuilder();
                for (Element.Attribute attribute : curr_nt.inh) {
                    inh.append(attribute.type).append(" ").append(attribute.name).append(", ");
                }
                // again removing last comma
                if (inh.length() >= 2)
                    out.print(inh.delete(inh.length() - 2, inh.length()).toString());
                out.println(") throws ParseException {");

                // user-defined init
                out.println(curr_nt.init);
                // declare ret val
                if (!curr_nt.returnType().equals("void"))
                    out.println("\t\t" + curr_nt.returnType() + " " + curr_nt.synth.name + " = " + defaultValue(curr_nt.returnType()) + ";");

                // here start the rules
                out.println("\t\tswitch (lex.getCurToken()) {");
                for (Map.Entry<Terminal, Rule> entry : table.get(nt_entry.getValue()).entrySet()) {
                    // a case for each possible term
                    out.println("\t\t\tcase " + entry.getKey().name.toUpperCase() + ": {");
                    // check whether it was not inferred through EPS rule
                    boolean own = !entry.getValue().head().name.equals(grammar.EPS.name);
                    Rule rule = entry.getValue();
                    for (int i = 0; i < rule.units.size(); i++) {
                        Element elem = rule.units.get(i).element;
                        if (elem instanceof Terminal) {
                            out.println(rule.units.get(i).code);
                            if (own) out.println("\t\t\t\tlex.nextToken();");
                        } else {
                            // here we are going to call nonterms from rule
                            if (!elem.returnType().equals("void")) {
                                out.print("\t\t\t\t" + elem.returnType() + " " + elem.name + " = " + elem.name + "(");
                                StringBuilder args = new StringBuilder();
                                for (String arg : rule.units.get(i).args) {
                                    args.append(arg).append(", ");
                                }
                                if (args.length() >= 2)
                                    out.print(args.delete(args.length() - 2, args.length()).toString());
                                out.println(");");
                            } else {
                                out.println("\t\t\t\t" + elem.name + "();");
                            }
                            out.println("\t\t\t\t" + rule.units.get(i).code);
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
