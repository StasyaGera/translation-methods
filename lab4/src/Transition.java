import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Transition {
    List<Element> elements;

    Transition(Element... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
    }

    Element first() {
        return elements.get(0);
    }

    void add(Element e) {
        elements.add(e);
    }

    void addAll(Transition other) {
        elements.addAll(other.elements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            sb.append(element);
            sb.append(" ");
        }
        return sb.toString();
    }
}
