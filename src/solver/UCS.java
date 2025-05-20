package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import obj.Board;
// import obj.Piece;
import utils.Utils;

public class UCS {
    private Utils utils;
    private int exploredNodes;  
    private Scanner scanner;
    private Node solution;
    private Set<Board> visitedBoards;

    public UCS(Board initialBoard) {
        utils = new Utils();
        exploredNodes = 0;
        scanner = new Scanner(System.in);
        visitedBoards = new HashSet<Board>();
        solution = null;
        solve(initialBoard);
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
   
    private void solve(Board initialBoard) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(initialBoard, 0, null));
       
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
           
            List<Board> nextBoards = utils.generateAllPossibleMoves(currentBoard, currentBoard.getLastMoves(), currentBoard.getLastDist(), currentBoard.getLastPiece());
            
            for(Board nextBoard : nextBoards){
                if(!visitedBoards.contains(nextBoard)){
                    Node nextNode = new Node(nextBoard, current.getCost() + 1, current);
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
            }
            System.out.println("Kedalaman: " + (path.size() - 1) );
            System.out.println("Jumlah Node: " + exploredNodes );
        }else{
            System.out.println("Tidak ada solusi yang ditemukan");
        }
    }

    public void writeSolution(String inputFileName) {
        if (solution != null) {
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "Solution.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                writer.write("=====================================================\n");
                writer.write("         SOLUSI RUSH HOUR MENGGUNAKAN UCS            \n");
                writer.write("=====================================================\n\n");
                writer.write("Total langkah: " + (solution.getPathOfBoards().size() - 1) + "\n");
                writer.write("Total node dieksplorasi: " + exploredNodes + "\n\n");
                
                for (int i = 0; i < solution.getPathOfBoards().size(); i++) {
                    writer.write("Langkah Ke-" + i + "\n");
                    Board board = solution.getPathOfBoards().get(i);
                    
                    // Menulis board state ke file
                    char[][] boardState = board.getBoardState();
                    int boardRow = board.getBoardRow();
                    int boardCol = board.getBoardCol();
                    int exitRow = board.getExitRow();
                    int exitCol = board.getExitCol();
                    
                    // Menulis exit bagian atas jika ada
                    if (exitRow == 0) {
                        writer.write(" ");
                        for (int j = 0; j < boardCol + 1; j++) {
                            if (j == exitCol) {
                                writer.write("K");
                            } else {
                                writer.write(" ");
                            }
                        }
                        writer.write("\n");
                    }
                    
                    // Menulis board
                    for (int r = 1; r <= boardRow; r++) {
                        if (r != exitRow) {
                            writer.write(" ");
                        } else {
                            if (exitCol == 0) {
                                writer.write("K");
                            } else {
                                writer.write(" ");
                            }
                        }
                        
                        for (int c = 1; c <= boardCol; c++) {
                            writer.write(boardState[r][c]);
                        }
                        
                        if (r != exitRow) {
                            writer.write(" ");
                        } else {
                            if (exitCol == boardCol + 1) {
                                writer.write("K");
                            } else {
                                writer.write(" ");
                            }
                        }
                        writer.write("\n");
                    }
                    
                    // Menulis exit bagian bawah jika ada
                    if (exitRow == boardRow + 1) {
                        writer.write(" ");
                        for (int j = 0; j < boardCol + 1; j++) {
                            if (j == exitCol) {
                                writer.write("K");
                            } else {
                                writer.write(" ");
                            }
                        }
                        writer.write("\n");
                    }
                    
                    writer.write("\n");
                }
                
                writer.write("=====================================================\n");
                writer.write("                    SOLUSI SELESAI                   \n");
                writer.write("=====================================================\n");
                
                System.out.println("Solusi berhasil disimpan ke file: " + outputFileName);
            } catch (IOException e) {
                System.out.println("Error saat menyimpan solusi ke file: " + e.getMessage());
            }
        } else {
            System.out.println("Tidak ada solusi yang disimpan ke dalam file");
        }
    }
}