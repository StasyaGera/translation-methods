import java.util.List;
import java.util.ArrayList;

public class Rule {
    List<Unit> units = new ArrayList<>();

    Rule(Element... elements) {
        for (Element element : elements) {
            units.add(new Unit(element));
        }
    }

    Element head() {
        return units.get(0).element;
    }

    @Deprecated
    void add(Element e) {
        units.add(new Unit(e));
    }

    void add(Element e, List<String> args, String code) {
        units.add(new Unit(e, code, args));
    }

    void addAll(Rule other) {
        units.addAll(other.units);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Unit unit : units) {
            sb.append(unit.element);
            sb.append(" ");
        }
        return sb.toString();
    }

    class Unit {
        Element element;
        String code = "";
        List<String> args = new ArrayList<>();

        Unit(Element element) {
            this.element = element;
        }

        Unit(Element element, String code, List<String> args) {
            this.element = element;
            if (code != null)
                this.code = code/*.substring(1, code.length() - 1)*/;
            if (args != null)
                this.args = args;
        }
    }
}
