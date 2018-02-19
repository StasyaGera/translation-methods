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
}

parseInput
    : nt = read_nt_name COLON read_cases[$nt.elem] SEMICOLON parseInput
    | t = read_t_name COLON read_t_descr[$t.elem] SEMICOLON parseInput
    | EOF
;

read_nt_name returns [NonTerminal elem]
    : r = RULE {
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
    : t = TOKEN {
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
    : es = ANY (VBAR ds = read_t_descr[$elem])? {
        $elem.addStr($es.text.substring(1, $es.text.length() - 1));
    }
    | es = REGEXP (VBAR ds = read_t_descr[$elem])? {
        $elem.addRegex($es.text);
    }
    | es = TOKEN (VBAR ds = read_t_descr[$elem])? {
        if (terminals.containsKey($es.text)) {
            Terminal t = terminals.get($es.text);
            $elem.str.addAll(t.str);
            $elem.regex.addAll(t.regex);
        }
    }
;

read_cases[NonTerminal elem]
    : expressions[$elem, new Transition()] (VBAR read_cases[$elem])?
;

expressions[NonTerminal elem, Transition transit]
    : expression[transit] expressions[$elem, $transit]
    | expression[transit] {
        $elem.add(transit);
    }
;

expression[Transition transit]
    : nt = read_nt_name {
        $transit.add($nt.elem);
    }
    | t = read_t {
        $transit.add($t.elem);
    }
;

read_t returns [Terminal elem]
    : name = read_t_name {
        $elem = $name.elem;
    }
    | t = REGEXP {
        String name = termName.concat(Integer.toString(cnt++));
        $elem = new Terminal(name);
        $elem.addRegex($t.text);
        terminals.put(name, $elem);
    }
    | t = ANY {
        String name = termName.concat(Integer.toString(cnt++));
        $elem = new Terminal(name);
        $elem.addStr($t.text.substring(1, $t.text.length() - 1));
        terminals.put(name, $elem);
    }
;
