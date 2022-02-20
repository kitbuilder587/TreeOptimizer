package UI;

import TreeStructure.LogicTree;
import TreeStructure.Node;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.function.Function;

@Getter
@Setter
public class Viewport extends JPanel implements MouseMotionListener, MouseWheelListener, MouseListener {

    public static int NODE_WIDTH = 200;
    public static int NODE_HEIGHT = 120;
    public static int NODE_RADIUS = 10;
    private Point cameraPosition = new Point(300,100);
    private double scaleIndex = 0.25;
    private Point mousePoint = new Point(-1,-1);
    private LogicTree tree;
    private int treeLayers = 6;

    public Point convertGlobalCoordinates(Point v){
        double x = v.x * scaleIndex + cameraPosition.x;
        double y = v.y * scaleIndex + cameraPosition.y;
        return new Point ((int) x,(int)y);
    }

    public double dist(Point a,Point b){
        double x1 = a.x, y1= a.y;
        double x2 = b.x, y2= b.y;
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    public float calculateFontSize(int width, String text, Font font,Graphics g){
        float res = 1;
        int minAbs = Integer.MAX_VALUE;
        for(int i=1;i<1000;i++){
            g.setFont(font.deriveFont((float)i/ (10.0f)));
            int widthR = g.getFontMetrics().stringWidth(text);
            int abs = Math.abs(widthR - width);
            if( abs < minAbs){
                minAbs = abs;
                res = i / (10.0f);
            }
        }
        return res;
    }


    public void drawNode(Graphics g,int x,int y,String name,boolean isALeaf){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.decode("#f0e199"));
        Point a = new Point(x,y);
        Point b = new Point(x + NODE_WIDTH, y + NODE_HEIGHT);
        if(isALeaf) b = new Point(x + NODE_HEIGHT, y + NODE_HEIGHT);
        Point center = new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
        Point leftCorner = convertGlobalCoordinates(a);
        Point rightCorner = convertGlobalCoordinates(b);
        int width = (int)(scaleIndex * (NODE_WIDTH));
        if(isALeaf) width = (int)(scaleIndex * (NODE_HEIGHT));
        int height = (int)(scaleIndex * NODE_HEIGHT);

        RoundRectangle2D roundRectangle2D;
        if(isALeaf){
            roundRectangle2D =new RoundRectangle2D.Double(leftCorner.x,leftCorner.y,width,height,0,0);
        }else{
            roundRectangle2D =new RoundRectangle2D.Double(leftCorner.x,leftCorner.y,width,height,NODE_RADIUS,NODE_RADIUS);
        }
        if(isALeaf) g2.setColor(Color.decode("#c9c9c9"));
        g2.fill(roundRectangle2D);
        g2.setColor(Color.decode("#e0b979"));
        if(isALeaf) g2.setColor(Color.decode("#9c9c9c"));
        g2.setStroke(new BasicStroke(1.6f));
        g2.draw(roundRectangle2D);
        g2.setStroke(new BasicStroke(1));
        Font curFont = new Font("TimesNewRoman", Font.PLAIN, 40);
        g2.setFont(curFont);
        int lettersize = 20;
        if(name.length()> 7) lettersize = 14;
        float fontSize = calculateFontSize((int)(lettersize * scaleIndex * name.length()),name,curFont,g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.setFont(curFont.deriveFont(fontSize));
        FontMetrics metrics = g2.getFontMetrics();
        Point stringCoords = new Point(center.x,center.y);
        Point stringCoordsConverted = convertGlobalCoordinates(stringCoords);
        g2.drawString(name, stringCoordsConverted.x - metrics.stringWidth(name) /2,stringCoordsConverted.y + metrics.getDescent());
    }


    public void paintTree(Graphics g,Node node,int x,int y,int marginX,int marginY){
        drawNode(g,x,y,node.getName(),node.isLeaf());
        if(node.isLeaf()){
            return ;
        }
        g.setColor(Color.BLACK);
        boolean isNextLeaf;
        if(node.getLeft() == null && node.getRight() != null) isNextLeaf = node.getRight().isLeaf();
        else isNextLeaf = node.getLeft().isLeaf();
        int width = NODE_WIDTH;
        if(isNextLeaf) width = NODE_HEIGHT;
        Point a = convertGlobalCoordinates(new Point(x  ,y+NODE_HEIGHT/2));
        Point aShifted = convertGlobalCoordinates(new Point(x +NODE_WIDTH ,y+NODE_HEIGHT/2));
        Point b = convertGlobalCoordinates(new Point(x  + width/2 + marginX,y+marginY+NODE_HEIGHT));
        Point c = convertGlobalCoordinates(new Point(x + NODE_WIDTH/2  + width/2 -marginX,y+marginY+NODE_HEIGHT));
        g.drawLine(aShifted.x,a.y,b.x,a.y);
        g.drawLine(b.x,a.y,b.x,b.y);
        g.drawLine(a.x,a.y,c.x,a.y);
        g.drawLine(c.x,a.y,c.x,c.y);
        paintTree(g,node.getRight(),x  + marginX,y+marginY+NODE_HEIGHT,marginX / 2, (int)(marginY / 1.5));
        paintTree(g,node.getLeft(),x + NODE_WIDTH/2 -marginX,y+marginY+NODE_HEIGHT,marginX / 2, (int)(marginY / 1.5));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,getWidth(),getHeight());
        paintTree(g,tree.getRoot(),0,0,(int)(NODE_WIDTH * Math.pow(2,tree.getLayers()-1)),500);
    }



    public Viewport(){
        super();
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        tree = new LogicTree(new Function<ArrayList<Boolean>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<Boolean> booleans) {
                boolean res = true;
                for(Boolean el : booleans){
                    res &= el;
                }
                return res;
            }
        },treeLayers);
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();

        if(notches > 0){
            scaleIndex -= 0.05;
        }else{
            scaleIndex += 0.05;
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("here");

        if (mousePoint.x != -1) {
            cameraPosition = new Point(cameraPosition.x + (e.getX() - mousePoint.x),cameraPosition.y + (e.getY() - mousePoint.y));
        }
        mousePoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
