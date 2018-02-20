import java.util.ArrayList;
import java.util.List;

public class NonTerminal extends Element {
    String init = "";
    List<Rule> rules = new ArrayList<>();

    NonTerminal(String name) {
        super(name);
    }

    void addRule(Rule t) {
        rules.add(t);
    }

    void setInit(String init) {
        this.init = init;
    }
}
