import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    final Terminal EPS = new Terminal("EPS");
    final Terminal END = new Terminal("END");

    Element start;
    Map<String, Terminal> terminals;
    Map<String, NonTerminal> nonTerminals;
    Map<String, Set<String>> first, follow;

    Grammar(Map<String, Terminal> terminals, Map<String, NonTerminal> nonTerminals, Element start) {
        EPS.addStr(EPS.name);
        END.addStr(END.name);

        this.start = start;
        this.terminals = terminals;
        this.terminals.put(EPS.name, EPS);
        this.terminals.put(END.name, END);
        this.nonTerminals = nonTerminals;
        countFirst();
        countFollow();
    }

    private Grammar(Grammar other) {
        this.start = other.start;
        this.nonTerminals = other.nonTerminals;
        this.terminals = other.terminals;
    }

    private void countFirst() {
        first = new HashMap<>();
        for (String name : terminals.keySet()) {
            Set<String> s = new HashSet<>();
            s.add(name);
            first.put(name, s);
        }
        for (String nt : nonTerminals.keySet()) {
            first.put(nt, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, NonTerminal> entry : nonTerminals.entrySet()) {
                NonTerminal A = entry.getValue();
                for (Rule gamma : A.rules) {
                    Element g0 = gamma.units.get(0).element;
                    Set<String> toAdd = new HashSet<>(first.get(g0.name));
                    if (g0 instanceof NonTerminal && gamma.units.size() > 1 && toAdd.contains(EPS.name)) {
                        toAdd.remove(EPS.name);
                        toAdd.addAll(first.get(gamma.units.get(1).element.name));
                    }
                    changed |= first.get(A.name).addAll(toAdd);
                }
            }
        } while (changed);
    }

    private void countFollow() {
        follow = new HashMap<>();
        for (String nonTerm : nonTerminals.keySet()) {
            follow.put(nonTerm, new HashSet<>());
        }

        follow.get(start.name).add(END.name);
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, NonTerminal> entry : nonTerminals.entrySet()) {
                NonTerminal A = entry.getValue();
                for (Rule alpha : A.rules) {
                    for (int i = alpha.units.size() - 1; i >= 0; i--) {
                        Element B = alpha.units.get(i).element;
                        if (B instanceof NonTerminal) {
                            if (i + 1 < alpha.units.size()) {
                                Element eta = alpha.units.get(i + 1).element;
                                Set<String> toAdd = new HashSet<>(first.get(eta.name));
                                if (toAdd.remove(EPS.name))
                                    changed |= follow.get(B.name).addAll(follow.get(A.name));
                                changed |= follow.get(B.name).addAll(toAdd);
                            } else {
                                changed |= follow.get(B.name).addAll(follow.get(A.name));
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    private boolean isLL1() {
        for (Map.Entry<String, NonTerminal> entry : nonTerminals.entrySet()) {
            NonTerminal curr = entry.getValue();
            for (int i = 0; i < curr.rules.size(); i++) {
                for (int j = 0; j < curr.rules.size(); j++) {
                    if (i == j) continue;
                    Rule alpha = curr.rules.get(i), beta = curr.rules.get(j);
                    Set<String> tmp = new HashSet<>(first.get(alpha.head().name));
                    tmp.retainAll(first.get(beta.head().name));
                    if (!tmp.isEmpty()) return false;

                    if (first.get(alpha.head().name).contains(EPS.name)) {
                        tmp = new HashSet<>(follow.get(curr.name));
                        tmp.retainAll(first.get(beta.head().name));
                        if (!tmp.isEmpty()) return false;
                    }
                }
            }
        }
        return true;
    }

    // this will not save your attributes
    // for grammars without attributes works fine
    @Deprecated
    Grammar transformToLL1() {
        if (isLL1()) return this;
        Grammar LL1 = new Grammar(this);
        Map<String, NonTerminal> newToNT;
        do {
            newToNT = new HashMap<>();

            for (Map.Entry<String, NonTerminal> entry : LL1.nonTerminals.entrySet()) {
                NonTerminal A = entry.getValue();
                NonTerminal A_ = new NonTerminal(A.name + "_");

                List<Rule> recursive = A.rules.stream().filter(t -> t.head() == A).collect(Collectors.toList());
                List<Rule> nonRecursive = A.rules.stream().filter(t -> t.head() != A).collect(Collectors.toList());

                if (!recursive.isEmpty()) {
                    newToNT.put(A_.name, A_);
                    A.rules.removeAll(recursive);
                    for (Rule nr : nonRecursive) {
                        Rule n = new Rule();
                        n.addAll(nr);
                        n.add(A_);
                        A.rules.add(n);
                    }

                    A.rules.removeAll(nonRecursive);
                    for (Rule r : recursive) {
                        Rule n = new Rule();
                        for (int i = 1; i < r.units.size(); i++) {
                            n.add(r.units.get(i).element);
                        }
                        n.add(A_);
                        A_.rules.add(n);
                    }

                    A_.rules.add(new Rule(EPS));
                }
            }

            for (Map.Entry<String, NonTerminal> entry : LL1.nonTerminals.entrySet()) {
                NonTerminal A = entry.getValue();
                NonTerminal A_ = new NonTerminal(A.name + "_");

                for (int i = 0; i < A.rules.size(); i++) {
                    for (int j = i + 1; j < A.rules.size(); j++) {
                        Rule beta = A.rules.get(i);
                        Rule gamma = A.rules.get(j);
                        if (beta.head() == gamma.head()) {
                            newToNT.put(A_.name, A_);

                            A.rules.remove(beta);
                            A.rules.remove(gamma);
                            A.rules.add(new Rule(beta.head(), A_));

                            Rule n = new Rule();
                            for (int l = 1; l < beta.units.size(); l++) {
                                n.units.add(beta.units.get(l));
                            }
                            A_.addRule(n);
                            n = new Rule();
                            for (int l = 1; l < gamma.units.size(); l++) {
                                n.units.add(gamma.units.get(l));
                            }
                            A_.addRule(n);
                        }
                    }
                }
            }
            LL1.nonTerminals.putAll(newToNT);
        } while (!newToNT.isEmpty());

        LL1.countFirst();
        LL1.countFollow();
        assert LL1.isLL1();
        return LL1;
    }

    public Map<NonTerminal, Map<Terminal, Rule>> getTable() {
        Map<NonTerminal, Map<Terminal, Rule>> table = new HashMap<>();
        for (Map.Entry<String, NonTerminal> nt_entry : nonTerminals.entrySet()) {
            NonTerminal curr_nt = nt_entry.getValue();
            Map<Terminal, Rule> row = new HashMap<>();
            for (Map.Entry<String, Terminal> t_entry : terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (Rule rule : curr_nt.rules) {
                    if (first.get(rule.head().name).contains(curr_t.name) ||
                            (first.get(rule.head().name).contains(EPS.name) &&
                                    follow.get(curr_nt.name).contains(curr_t.name))) {
                        row.put(curr_t, rule);
                    }
                }
            }
            table.put(curr_nt, row);
        }

        return table;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Start: ").append(start.name).append('\n');
        sb.append("\nNonTerms:\n");
        nonTerminals.forEach((name, elem) -> {
            sb.append(name).append('\n');
            if (!elem.rules.isEmpty()) {
                sb.append("\t: ").append(elem.rules.get(0)).append('\n');
            }
            for (int i = 1; i < elem.rules.size(); i++) {
                sb.append("\t| ").append(elem.rules.get(i)).append('\n');
            }
            sb.append(";\n");
        });
        sb.append("\nTerms:\n");
        terminals.forEach((name, elem) -> {
            sb.append(name).append('\n');
            if (!elem.str.isEmpty()) {
                sb.append("\t: ").append(elem.str.get(0));
            }
            for (int i = 1; i < elem.str.size(); i++) {
                sb.append(" | ").append(elem.str.get(i));
            }
            if (!elem.regex.isEmpty()) {
                if (elem.str.isEmpty()) {
                    sb.append("\t: ").append(elem.regex.get(0));
                } else {
                    sb.append("\n\t| ").append(elem.regex.get(0));
                }
            }
            for (int i = 1; i < elem.regex.size(); i++) {
                sb.append(" | ").append(elem.regex.get(i));
            }
            sb.append(";\n");
        });
        sb.append("\nFirst:\n");
        first.forEach((k, v) -> {
            if (nonTerminals.containsKey(k)) {
                sb.append(k).append(" : ").append(v).append('\n');
            }
        });
        sb.append("\nFollow:\n");
        follow.forEach((k, v) -> sb.append(k).append(" : ").append(v).append('\n'));
        sb.append("\nis").append(isLL1() ? "" : "not").append("LL1\n");
        return sb.toString();
    }
}
