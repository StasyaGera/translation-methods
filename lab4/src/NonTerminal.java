import java.util.ArrayList;
import java.util.List;

public class NonTerminal extends Element {
    List<Transition> transitions = new ArrayList<>();

    NonTerminal(String name) {
        super(name, false);
    }

    void add(Transition t) {
        transitions.add(t);
    }
}
