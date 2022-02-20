package TreeStructure;

import lombok.*;

import java.awt.*;

@Data
public class Node {
    private String name;
    private Node parent;
    private Node left;
    private Node right;
    private boolean isLeaf;
    private int value;
    private Point bounds;

    public Node(){

    }

    @Override
    public String toString() {
        return name+ " value: " + value;
    }
}
