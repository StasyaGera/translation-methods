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
                for (Transition gamma : A.transitions) {
                    Element g0 = gamma.elements.get(0);
                    Set<String> toAdd = new HashSet<>(first.get(g0.name));
                    if (g0.isNonTerminal() && gamma.elements.size() > 1 && toAdd.contains(EPS.name)) {
                        toAdd.remove(EPS.name);
                        toAdd.addAll(first.get(gamma.elements.get(1).name));
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
                for (Transition alpha : A.transitions) {
                    for (int i = alpha.elements.size() - 1; i >= 0; i--) {
                        Element B = alpha.elements.get(i);
                        if (B.isNonTerminal()) {
                            if (i + 1 < alpha.elements.size()) {
                                Element eta = alpha.elements.get(i + 1);
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
            for (int i = 0; i < curr.transitions.size(); i++) {
                for (int j = 0; j < curr.transitions.size(); j++) {
                    if (i == j) continue;
                    Transition alpha = curr.transitions.get(i), beta = curr.transitions.get(j);
                    Set<String> tmp = new HashSet<>(first.get(alpha.first().name));
                    tmp.retainAll(first.get(beta.first().name));
                    if (!tmp.isEmpty()) return false;

                    if (first.get(alpha.first().name).contains(EPS.name)) {
                        tmp = new HashSet<>(follow.get(curr.name));
                        tmp.retainAll(first.get(beta.first().name));
                        if (!tmp.isEmpty()) return false;
                    }
                }
            }
        }
        return true;
    }

    Grammar transformToLL1() {
        if (isLL1()) return this;
        Grammar LL1 = new Grammar(this);
        Map<String, NonTerminal> newToNT;
        do {
            newToNT = new HashMap<>();

            for (Map.Entry<String, NonTerminal> entry : LL1.nonTerminals.entrySet()) {
                NonTerminal A = entry.getValue();
                NonTerminal A_ = new NonTerminal(A.name + "_");

                List<Transition> recursive = A.transitions.stream().filter(t -> t.first() == A).collect(Collectors.toList());
                List<Transition> nonRecursive = A.transitions.stream().filter(t -> t.first() != A).collect(Collectors.toList());

                if (!recursive.isEmpty()) {
                    newToNT.put(A_.name, A_);
                    A.transitions.removeAll(recursive);
                    for (Transition nr : nonRecursive) {
                        Transition n = new Transition();
                        n.elements.addAll(nr.elements);
                        n.add(A_);
                        A.transitions.add(n);
                    }

                    A.transitions.removeAll(nonRecursive);
                    for (Transition r : recursive) {
                        Transition n = new Transition();
                        for (int i = 1; i < r.elements.size(); i++) {
                            n.add(r.elements.get(i));
                        }
                        n.add(A_);
                        A_.transitions.add(n);
                    }

                    A_.transitions.add(new Transition(EPS));
                }
            }

            for (Map.Entry<String, NonTerminal> entry : LL1.nonTerminals.entrySet()) {
                NonTerminal A = entry.getValue();
                NonTerminal A_ = new NonTerminal(A.name + "_");

                for (int i = 0; i < A.transitions.size(); i++) {
                    for (int j = i + 1; j < A.transitions.size(); j++) {
                        Transition beta = A.transitions.get(i);
                        Transition gamma = A.transitions.get(j);
                        if (beta.first() == gamma.first()) {
                            newToNT.put(A_.name, A_);

                            A.transitions.remove(beta);
                            A.transitions.remove(gamma);
                            A.transitions.add(new Transition(beta.first(), A_));

                            Transition n = new Transition();
                            for (int l = 1; l < beta.elements.size(); l++) {
                                n.elements.add(beta.elements.get(l));
                            }
                            A_.add(n);
                            n = new Transition();
                            for (int l = 1; l < gamma.elements.size(); l++) {
                                n.elements.add(gamma.elements.get(l));
                            }
                            A_.add(n);
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

    public Map<NonTerminal, Map<Terminal, Transition>> getTable() {
        Map<NonTerminal, Map<Terminal, Transition>> table = new HashMap<>();
        for (Map.Entry<String, NonTerminal> nt_entry : nonTerminals.entrySet()) {
            NonTerminal curr_nt = nt_entry.getValue();
            Map<Terminal, Transition> row = new HashMap<>();
            for (Map.Entry<String, Terminal> t_entry : terminals.entrySet()) {
                Terminal curr_t = t_entry.getValue();
                for (Transition transit : curr_nt.transitions) {
                    if (first.get(transit.first().name).contains(curr_t.name) ||
                            (first.get(transit.first().name).contains(EPS.name) &&
                                    follow.get(curr_nt.name).contains(curr_t.name))) {
                        row.put(curr_t, transit);
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
            if (!elem.transitions.isEmpty()) {
                sb.append("\t: ").append(elem.transitions.get(0)).append('\n');
            }
            for (int i = 1; i < elem.transitions.size(); i++) {
                sb.append("\t| ").append(elem.transitions.get(i)).append('\n');
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
