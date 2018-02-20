import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;

class Main {
    public static void main(String[] args) {
        ReaderLexer lexer;
        try {
            lexer = new ReaderLexer(CharStreams.fromFileName("input.txt"));
            ReaderParser parser = new ReaderParser(new CommonTokenStream(lexer));
            ReaderParser.StartContext ctx = parser.start();

            Grammar gr = new Grammar(parser.terminals, parser.nonTerminals, parser.start)/*.transformToLL1()*/;
            System.out.println(gr);
            Generator gen = new Generator(gr, ctx.title, ctx.header, ctx.members);
            gen.generateAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}