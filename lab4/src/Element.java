import java.util.ArrayList;
import java.util.List;

public class Element {
    final String name;
    Attribute synth;
    List<Attribute> inh = new ArrayList<>();

    protected Element(String name) {
        this.name = name;
        this.synth = new Attribute();
    }

    public void setSynth(String type, String name) {
        this.synth.type = type;
        this.synth.name = name;
    }

    public void addInh(String type, String name) {
        this.inh.add(new Attribute(type, name));
    }

    public String returnType() {
        return synth.type;
    }

    class Attribute {
        String type, name;

        private Attribute() {
            type = "void";
            name = "";
        }

        Attribute(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
