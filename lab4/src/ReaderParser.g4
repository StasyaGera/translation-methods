parser grammar ReaderParser;

options { tokenVocab = ReaderLexer; }

@header {
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
}

@members {
int cnt = 0;
String termName = "TERM";
NonTerminal start;
Map<String, Terminal> terminals = new HashMap<>();
Map<String, NonTerminal> nonTerminals = new HashMap<>();

String removeBraces(String input) {
    return input.substring(1, input.length() - 1).trim();
}
}

start returns [String title, String header, String members]
    : (t=(RULE|TOKEN))? (HEADER h=CODE)? (MEMBERS m=CODE)? parseInput {
        $title = ($t.text == null) ? "" : $t.text;
        $header = ($h.text == null) ? "" : removeBraces($h.text);
        $members = ($m.text == null) ? "" : removeBraces($m.text);
    }
;

parseInput
    : nt=read_nt_name (inh[$nt.elem])? (RETURNS synth[$nt.elem])? (init[$nt.elem])?
        COLON read_cases[$nt.elem] SEMICOLON parseInput
    | t=read_t_name COLON read_t_descr[$t.elem] SEMICOLON parseInput
    | EOF
;

inh[Element elem]
    : OBRACK
        v=read_var_descr { $elem.addInh($v.type, $v.name); }
        (COMMA v=read_var_descr{ $elem.addInh($v.type, $v.name); })*? CBRACK
;

synth[Element elem]
    : OBRACK v=read_var_descr { $elem.setSynth($v.type, $v.name); } CBRACK
;

read_var_descr returns [String type, String name]
    : t=(TOKEN|RULE) n=(TOKEN|RULE) {
        $type = $t.text;
        $name = $n.text;
    }
;

init[NonTerminal nt]
    : INIT i=CODE {
        $nt.setInit(($i.text == null) ? "" : removeBraces($i.text));
    }
;

read_nt_name returns [NonTerminal elem]
    : r=RULE {
        String name = $r.text;
        if (nonTerminals.containsKey(name)) {
            $elem = nonTerminals.get(name);
        } else {
            $elem = new NonTerminal(name);
            if (nonTerminals.isEmpty()) {
                start = $elem;
            }
            nonTerminals.put(name, $elem);
        }
    }
;

read_t_name returns [Terminal elem]
    : t=TOKEN {
        String name = $t.text;
        if (terminals.containsKey(name)) {
            $elem = terminals.get(name);
        } else {
            $elem = new Terminal(name);
            terminals.put(name, $elem);
        }
    }
;

read_t_descr[Terminal elem]
    : es=ANY (VBAR ds=read_t_descr[$elem])? {
        $elem.addStr(removeBraces($es.text));
    }
    | es=REGEXP (VBAR ds=read_t_descr[$elem])? {
        $elem.addRegex($es.text);
    }
    | es=TOKEN (VBAR ds=read_t_descr[$elem])? {
        if (terminals.containsKey($es.text)) {
            Terminal t = terminals.get($es.text);
            $elem.addAllStr(t.str);
            $elem.addAllRegex(t.regex);
        }
    }
;

read_cases[NonTerminal elem]
    : expressions[$elem, new Rule()] (VBAR read_cases[$elem])?
;

expressions[NonTerminal elem, Rule rule]
    : expression[rule] expressions[$elem, $rule]
    | expression[rule] {
        $elem.addRule(rule);
    }
;

expression[Rule rule]
    : nt=read_nt_name OBRACK vs=var_names CBRACK (c=CODE)? {
        $rule.add($nt.elem, $vs.args, $c.text == null ? null : removeBraces($c.text));
    }
    | nt=read_nt_name (c=CODE)? {
        $rule.add($nt.elem, null, $c.text == null ? null : removeBraces($c.text));
    }
    | t=read_t (c=CODE)? {
        $rule.add($t.elem, null, $c.text == null ? null : removeBraces($c.text));
    }
;

var_names returns [List<String> args]
    @init {
        $args = new ArrayList<>();
    }
    : v=var_name COMMA vs=var_names {
        $args.add($v.name);
        $args.addAll($vs.args);
    }
    | v=var_name {
        $args.add($v.name);
    }
;

var_name returns [String name]
    : n=(TOKEN|RULE) {
        $name=$n.text;
    }
    | n=CODE {
        $name = removeBraces($n.text);
    }
;

read_t returns [Terminal elem]
    : name=read_t_name {
        $elem = $name.elem;
    }
    | t=REGEXP {
        String name = termName.concat(Integer.toString(cnt++));
        $elem = new Terminal(name);
        $elem.addRegex($t.text);
        terminals.put(name, $elem);
    }
    | t=ANY {
        String name = termName.concat(Integer.toString(cnt++));
        $elem = new Terminal(name);
        $elem.addStr(removeBraces($t.text));
        terminals.put(name, $elem);
    }
;
