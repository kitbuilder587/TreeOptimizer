package TreeStructure;

import ProgrammLogic.Bits;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class LogicTree {
    private Node root;
    private Function<ArrayList<Boolean>, Boolean> r;
    private int layers;
    private ArrayList<Boolean> leaves;
    public LogicTree(Function<ArrayList<Boolean>, Boolean> r, int layers){
        this.r = r;
        this.layers = layers;
        leaves = new ArrayList<>();
        root = new Node();
        buildLogicTree();
        optimizeTree();
    }

    public void buildLogicTree(){
        for(long s =0;s<=Math.pow(2,layers);s++){
            boolean[] bits = Bits.getBits(s,layers);
            Node currentNode=root;
            root.setName("1_{a_"+ 1 +"}(x_" + 1 + ")");
            for(int i=0;i<layers;i++){
                Node newNode = new Node();
                newNode.setName("1_{a_"+ (i+2) +"}(x_" + (i+2) + ")");
                newNode.setParent(currentNode);
                if(bits[i]){
                    if(currentNode.getRight() == null){
                        currentNode.setRight(newNode);
                        currentNode = newNode;
                    }else{
                        currentNode = currentNode.getRight();
                    }
                }else{
                    if(currentNode.getLeft() == null){
                        currentNode.setLeft(newNode);
                        currentNode = newNode;
                    }else{
                        currentNode = currentNode.getLeft();
                    }
                }
                if(layers -1 == i){
                    newNode.setLeaf(true);
                    List<Boolean> ar = Arrays.asList(ArrayUtils.toObject(bits));

                    boolean value = r.apply(new ArrayList<Boolean>(ar));
                    leaves.add(value);
                    if(value) {
                        newNode.setValue(1);
                        newNode.setName("1");
                    } else {
                        newNode.setValue(0);
                        newNode.setName("0");
                    }

                }
            }
        }
    }


    public void optimizeTreeRecursive(Node curNode,int l,int r){
        int s = (l+r) / 2;
        if(areArraysEqual(leaves,l,s,s+1,r)){
            //if(curNode == null) System.out.println(l + " " + r);
            Node parent = curNode.getParent();
            if(parent == null) {
                root = curNode.getLeft();
                root.setParent(null);
            } else if(parent.getLeft() == curNode){
                parent.setLeft(curNode.getLeft());
            }else{
                parent.setRight(curNode.getLeft());
            }
            curNode = curNode.getLeft();
            curNode.setParent(parent);
            if(curNode ==null || curNode.isLeaf()) return;
            optimizeTreeRecursive(curNode,s+1,r);
        }else{
            if(curNode ==null || curNode.isLeaf()) return;
            optimizeTreeRecursive(curNode.getLeft(),l,s);
            optimizeTreeRecursive(curNode.getRight(),s+1,r);
        }

    }

    public void optimizeTree(){
        optimizeTreeRecursive(root,0,(int)Math.pow(2,layers)-1);
    }

    boolean areArraysEqual(ArrayList a,int l,int r, int l2, int r2){
        if(r2 - l2 != r - l) return false;
        int diff = r - l;
        for(int i=0;i<=diff;i++){
            if(a.get(l+i) != a.get(l2+i)) return false;
        }
        return true;
    }
}
