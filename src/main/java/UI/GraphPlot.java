package UI;

import javax.swing.*;
import java.awt.*;

public class GraphPlot extends JFrame{


    public static void main(String[] args) {
        new GraphPlot();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);

    }

    public GraphPlot() {
        super("LogicTrees");
        setSize(1300,800);
        setLayout(null);
        Viewport vp = new Viewport();
        vp.setBounds(0,10,1300,790);
        add(vp);
        setVisible(true);
    }
}
