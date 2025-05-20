package solver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import utils.Utils;
import java.util.Scanner;
import obj.*;

public class GBFS {
    private List<Board> solutionPath;
    private Board currentBoard;
    private int nodeCount; 
    private Set<Board> visitedBoards; 
    private Scanner scanner;
    private TreeHeuristic th;
    private DistHeuristic dh;
    
    public GBFS(){
        solutionPath = new ArrayList<Board>();
        nodeCount = 0;
        visitedBoards = new HashSet<Board>();
        scanner = new Scanner(System.in);
        th = new TreeHeuristic();
        dh = new DistHeuristic();
    }

    public void solve(Board initBoard, boolean isTreeHeuristic){
        Utils utils = new Utils();
        solutionPath.add(initBoard);
        currentBoard = new Board(initBoard);
        visitedBoards.add(currentBoard); 

        int heuristicValue;

        while (!currentBoard.isFinished()) {
            List<Board> currentPossibleBoards = new ArrayList<Board>(utils.generateAllPossibleMoves(currentBoard, currentBoard.getLastMoves(), currentBoard.getLastDist(), currentBoard.getLastPiece()));
            
            Board bestBoard = null;
            int minHeuristicValue = Integer.MAX_VALUE;
            
            for (Board nextBoard : currentPossibleBoards) {
                nodeCount++;
                
                if (nextBoard.isFinished()) {
                    bestBoard = nextBoard;
                    break;
                }
                
                if (isContain(nextBoard)) {
                    continue; 
                }
                
                if(isTreeHeuristic) heuristicValue = th.evaluate(nextBoard);
                else heuristicValue = dh.evaluate(nextBoard);
                
                if (heuristicValue < minHeuristicValue) {
                    minHeuristicValue = heuristicValue;
                    bestBoard = nextBoard;
                }
            }
            
            if (bestBoard == null) {
                break;
            }
            
            currentBoard = bestBoard;
            visitedBoards.add(currentBoard);
            solutionPath.add(currentBoard);
            
            if (currentBoard.isFinished()) {
                scanner.nextLine();
                break;
            }
        }
        System.out.println("node: " + this.getNodeCount());
        scanner.nextLine();
    }
    
    public List<Board> getSolutionPath() {
        return solutionPath;
    }
    
    public boolean isSolutionFound() {
        return currentBoard.isFinished();
    }
    
    public int getSolutionSteps() {
        return solutionPath.size() - 1; 
    }
    
    public int getNodeCount() {
        return nodeCount;
    }

    public boolean isContain(Board inpBoard){
        for(Board b : visitedBoards){
            if(inpBoard.isEqual(b)) return true;
        }  
        return false;
    }

    public void printSolutionPath(){
        if (currentBoard.isFinished()) {
            System.out.println("Solusi ditemukan!");
            System.out.print("Tekan enter untuk lanjut ...");
            scanner.nextLine();
            for (int i = 0; i < solutionPath.size(); i++) {
                System.out.println("\nLangkah Ke-" + i );
                solutionPath.get(i).printBoardState();
            }
            System.out.println("Jumlah Node dieksplor: " + nodeCount );
        } else {
            System.out.println("Tidak ada solusi yang ditemukan " );
        }
    }

    public void writeSolution(String inputFileName) {
        if (currentBoard.isFinished()) {
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "Solution.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                writer.write("=====================================================\n");
                writer.write("         SOLUSI RUSH HOUR MENGGUNAKAN GBFS           \n");
                writer.write("=====================================================\n\n");
                writer.write("Total langkah: " + (solutionPath.size() - 1) + "\n");
                writer.write("Total node dieksplorasi: " + nodeCount + "\n\n");
                
                for (int i = 0; i < solutionPath.size(); i++) {
                    writer.write("Langkah Ke-" + i + "\n");
                    Board board = solutionPath.get(i);
                    
                    // Menulis board state ke file
                    char[][] boardState = board.getBoardState();
                    int boardRow = board.getBoardRow();
                    int boardCol = board.getBoardCol();
                    int exitRow = board.getExitRow();
                    int exitCol = board.getExitCol();
                    
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