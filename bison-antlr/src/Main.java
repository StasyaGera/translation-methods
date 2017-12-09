import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;

class Main {
    public static void main(String[] args) {
        LangLexer lexer;
        try {
            lexer = new LangLexer(CharStreams.fromFileName("input.txt"));
            LangParser parser = new LangParser(new CommonTokenStream(lexer));
            parser.parseLangFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}