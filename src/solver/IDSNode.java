package solver;

import java.util.ArrayList;
import java.util.List;
import obj.*;

public class IDSNode {
    private Board currentBoard;
    private List<Board> pathOfBoards;
    private int depth;

    public IDSNode(Board inpBoard, List<Board> inpBoards, int depth){
        this.currentBoard = inpBoard;
        this.pathOfBoards = new ArrayList<Board>(inpBoards);
        this.depth = depth;
    }

    IDSNode(Board inpBoard){
        this.currentBoard = inpBoard;
        this.pathOfBoards = new ArrayList<Board>();
        pathOfBoards.add(inpBoard);
        this.depth = 0;
    }

    IDSNode(IDSNode prevNode){
        this.currentBoard = prevNode.currentBoard;
        this.pathOfBoards = new ArrayList<Board>(prevNode.pathOfBoards);
        this.depth = prevNode.depth;
    }

    IDSNode (Board appendBoard, IDSNode prevNode){
        this.pathOfBoards = new ArrayList<Board>(prevNode.getPathOfBoards());
        this.pathOfBoards.add(appendBoard);
        this.currentBoard = appendBoard;
        this.depth = prevNode.getDepth()+1; 
    }

    public Board getCurrentBoard(){
        return currentBoard;
    }

    public List<Board> getPathOfBoards(){
        return pathOfBoards;
    }

    public int getDepth(){
        return depth;
    }
}
