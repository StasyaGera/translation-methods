parser grammar LangParser;

options {
    tokenVocab = LangLexer;
}

@header {
	import java.util.Set;
    import java.util.HashSet;
    import java.util.List;
    import java.util.ArrayList;
    import java.io.*;
}

@members {
    StringBuilder sb = new StringBuilder();
    Set<String> vars = new HashSet<>();
    static int tabs = 1;
}

parseLangFile
    @init {
        sb.append("int main() {\n");
    }
    @after {
        sb.append("\treturn 0;\n}");
        try (PrintWriter w = new PrintWriter("output.cpp")) {
            w.print("#include \"stdio.h\"\n\n");
            for (String name : vars) {
                w.print("int " + name + ";\n");
            }
            w.println(sb.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Could not create output file");
            System.exit(1);
        }
    }
    : (statement (NEWLINE+ | EOF))*
;

statement
    : vs = vars ASSIGN es = expressions {
        for (int i = 0; i < $vs.names.size(); i++) {
            for (int t = 0; t < tabs; t++) {
                sb.append('\t');
            }

            String name = $vs.names.get(i), expr = $es.exprs.get(i);
            if (expr.equals("scanf")) {
                sb.append("scanf(\"%d\", &" + name + ");\n");
            } else {
                sb.append(name + " = " + expr + ";\n");
            }
        }
    }
    | PRINT es = expressions {
        for (int i = 0; i < tabs; i++) {
            sb.append('\t');
        }

        int sz = $es.exprs.size();
        sb.append("printf(\"");
        for (int i = 0; i < sz - 1; i++) {
            sb.append("%d, ");
        }
        sb.append("%d\", ");
        for (int i = 0; i < sz - 1; i++) {
            sb.append($es.exprs.get(i) + ", ");
        }
        sb.append($es.exprs.get(sz - 1) + ");\n");
    }
    | IF c = condition COLON NEWLINE? finishIf[$c.cond]
;

finishIf[String cond]
    @init {
        for (int i = 0; i < tabs; i++) {
            sb.append('\t');
        }

        sb.append("if (" + $cond + ") {\n");
        tabs++;
    }
    @after {
        tabs--;
        for (int i = 0; i < tabs; i++) {
            sb.append('\t');
        }
        sb.append("}\n");
    }
    : (statement NEWLINE+)* DOT
    | (statement NEWLINE+)* ELSE finishElse
;

finishElse
    @init {
        for (int i = 0; i < tabs - 1; i++) {
            sb.append('\t');
        }

        sb.append("} else {\n");
    }
    : COLON NEWLINE? (statement NEWLINE+)* DOT
    | IF c = condition COLON NEWLINE? finishIf[$c.cond]
;

condition returns [String cond]
    : l = expression sign = (L | G | LEQ | GEQ | EQ | NE) r = expression {
        $cond = $l.expr + " " + $sign.text + " " + $r.expr;
    }
;

expressions returns [List<String> exprs]
    @init {
        $exprs = new ArrayList<>();
    }
    : vs = vars COMMA es = expressions {
        $exprs.addAll($vs.names);
        $exprs.addAll($es.exprs);
    }
    | e = expression COMMA es = expressions {
        $exprs.add($e.expr);
        $exprs.addAll($es.exprs);
    }
    | e = expression {
        $exprs.add($e.expr);
    }
;

expression returns [String expr]
    : l = expression op = (DIV | MUL | ADD | SUB) r = expression {
        $expr = $l.expr + " " + $op.text + " " + $r.expr;
    }
    | LPAREN e = expression RPAREN {
        $expr = "(" + $e.expr + ")";
    }
    | SUB e = expression {
        $expr = $SUB.text + $e.expr;
    }
    | INT {
        $expr = $INT.text;
    }
    | READ {
        $expr = "scanf";
    }
    | v = var {
        $expr = $v.name;
    }
;

vars returns [List<String> names]
    @init {
        $names = new ArrayList<>();
    }
    : v = var COMMA vs = vars {
        $names.add($v.name);
        $names.addAll($vs.names);
    }
    | v = var {
        $names.add($v.name);
    }
;

var returns [String name]
    : ID {
        $name = $ID.text;
        vars.add($name);
    }
;
