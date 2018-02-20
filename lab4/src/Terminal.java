import java.util.ArrayList;
import java.util.List;

public class Terminal extends Element {
    List<String> str = new ArrayList<>();
    List<String> regex = new ArrayList<>();

    Terminal(String name) {
        super(name);
    }

    void addStr(String s) {
        str.add(s);
    }

    void addAllStr(List<String> ls) {
        str.addAll(ls);
    }

    void addRegex(String s) {
        regex.add(s);
    }

    void addAllRegex(List<String> ls) {
        regex.addAll(ls);
    }
}
