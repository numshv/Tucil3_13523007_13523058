package solver;

import java.util.*;
import obj.Board;
// import obj.Piece;
import utils.Utils;

public class AStar {
    private Utils utils;
    private int exploredNodes;  
    private Scanner scanner;
    private Node solution;
    private Set<Board> visitedBoards;
    private boolean isTreeHeuristic;
    private TreeHeuristic th;
    private DistHeuristic dh;
    private int heuristicValue;

    public AStar(Board initialBoard, boolean isTreeHeuristic) {
        utils = new Utils();
        exploredNodes = 0;
        scanner = new Scanner(System.in);
        visitedBoards = new HashSet<Board>();
        solution = null;
        th = new TreeHeuristic();
        dh = new DistHeuristic();
        solve(initialBoard, isTreeHeuristic);
        
    }
    
    public class Node implements Comparable<Node> {
        private Board state;
        private int cost;
        private Node parent;
       
        public Node(Board state, int cost, Node parent) {
            this.state = state;
            this.cost = cost;
            this.parent = parent;
        }
       
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
        
        public int getCost() {
            return cost;
        }
        
        public Board getState() {
            return state;
        }
        
        public Node getParent() {
            return parent;
        }
        
        public List<Board> getPathOfBoards() {
            List<Board> path = new ArrayList<>();
            Node current = this;
            
            while(current != null){
                path.add(current.getState());
                current = current.getParent();
            }
            
            Collections.reverse(path);
            return path;
        }
    }
   
    private void solve(Board initialBoard, boolean isTreeHeuristic) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        if(isTreeHeuristic) heuristicValue = th.evaluate(initialBoard);
        else heuristicValue = dh.evaluate(initialBoard);
        pq.add(new Node(initialBoard, heuristicValue, null));
       
        while(!pq.isEmpty()){
            Node current = pq.poll();
            Board currentBoard = current.getState();
            
            if(visitedBoards.contains(currentBoard)){
                continue;
            }
            
            exploredNodes++;
            visitedBoards.add(currentBoard);
           
            if(currentBoard.isFinished()){
                solution = current;
                break;
            }
           
            List<Board> nextBoards = utils.generateAllPossibleMoves(currentBoard);
            
            for(Board nextBoard : nextBoards){
                if(!visitedBoards.contains(nextBoard)){
                    if(isTreeHeuristic) heuristicValue = th.evaluate(nextBoard);
                    else heuristicValue = dh.evaluate(nextBoard);
                    Node nextNode = new Node(nextBoard, current.getCost()+1+heuristicValue, current);
                    pq.add(nextNode);
                }
            }
        }
    }
    
    public Node getSolution() {
        return solution;
    }
    
    public int getExploredNodesCount() {
        return exploredNodes;
    }
    
    public void printSolutionPath() {
        if(solution != null){
            List<Board> path = solution.getPathOfBoards();
            for(int i = 0; i < path.size(); i++){
                System.out.println("\nLangkah Ke-" + i + " (Papan ke-" + (i+1) + "):");
                path.get(i).printBoardState();
                System.out.print("Enter anything to continue ...");
                scanner.nextLine();
            }
            System.out.println("--------------------------------");
            System.out.println("--- Kedalaman: " + (path.size() - 1) + " ---");
            System.out.println("--- Jumlah Node: " + exploredNodes + " ---");
        }else{
            System.out.println("Tidak ada solusi yang ditemukan");
        }
    }
}