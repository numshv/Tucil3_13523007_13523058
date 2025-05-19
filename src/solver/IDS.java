package solver;
import java.util.Stack;
import java.util.List;

import obj.*;

public class IDS {
    private List<IDSNode> treeStack;
    private int curMaxDepth;
    private IDSNode solution;
    private int exploredNodes;

    public IDS(Board initBoard){
        treeStack = new Stack<IDSNode>();
        curMaxDepth = 1;

        treeStack.add(new IDSNode(initBoard));

        while(true){

        }
    }
}


