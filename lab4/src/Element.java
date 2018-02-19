import java.util.List;
import java.util.ArrayList;

public class Element {
    private boolean terminal;
    final String name;
//    List<Transition> transitions = new ArrayList<>();

    protected Element(String name, boolean terminal) {
        this.name = name;
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return name;
    }

    boolean isNonTerminal() { return !terminal; }
    boolean isTerminal() { return terminal; }

//    void add(Transition t) {
//        transitions.add(t);
//    }
}
