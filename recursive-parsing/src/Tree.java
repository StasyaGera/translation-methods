import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.List;

class Tree {
    private String node;
    private List<Tree> children;
    private static int e = 0;

    Tree(String node, Tree... children) {
        this.node = node;
        this.children = Arrays.asList(children);
    }

    public void visualize() {
        Layout<Tree, Integer> layout = new TreeLayout<>(convert());
        BasicVisualizationServer<Tree, Integer> visServer = new BasicVisualizationServer<>(layout);

        visServer.getRenderContext().setVertexShapeTransformer(n -> {
            int width = Math.max(n.toString().length() * 10, 25);
            return new Ellipse2D.Double(-(width / 2), -12.5, width, 25);
        });
        visServer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<>());
        visServer.getRenderContext().setVertexFillPaintTransformer(tree -> Color.LIGHT_GRAY);
        visServer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        visServer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        JFrame frame = new JFrame(getFullString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(visServer);
        frame.pack();
        frame.setVisible(true);
    }

    private DelegateTree<Tree, Integer> convert() {
        DelegateTree<Tree, Integer> tree = new DelegateTree<>(new DirectedOrderedSparseMultigraph<>());
        tree.addVertex(this);
        return convert(tree);
    }

    private DelegateTree<Tree, Integer> convert(DelegateTree<Tree, Integer> tree) {
        for (Tree child : children) {
            tree.addChild(e++, this, child);
            tree = child.convert(tree);
        }
        return tree;
    }

    String getFullString() {
        StringBuilder res = new StringBuilder();
        for (Tree t : children) {
            res.append(t.getFullString());
        }
        if (!Arrays.asList(Parser.NON_TERMINALS).contains(node)) {
            res.append(node);
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return node;
    }
}
