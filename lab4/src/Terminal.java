import java.util.ArrayList;
import java.util.List;

public class Terminal extends Element {
    List<String> str = new ArrayList<>();
    List<String> regex = new ArrayList<>();

    Terminal(String name) {
        super(name, true);
    }

    void addStr(String s) {
        str.add(s);
    }

    void addRegex(String s) {
        regex.add(s);
    }
}
