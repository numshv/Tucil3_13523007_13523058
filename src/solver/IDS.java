package solver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Scanner;

import utils.*;

import obj.Board;

public class IDS {
    private Stack<IDSNode> treeStack; 
    private int curMaxDepth;         
    private IDSNode solution;      
    private int exploredNodes;    
    private boolean isFinished;

    public IDS(Board initBoard){
        this.solution = null;         // Menyimpan node solusi jika ditemukan
        this.exploredNodes = 0;       // Menghitung node yang dieksplorasi/digenerate
        this.curMaxDepth = 0;         // Batas kedalaman dimulai dari 0 untuk Iterative Deepening
        this.isFinished = false;

        Utils utils = new Utils();

        IDSNode rootNode = new IDSNode(initBoard); // Membuat node awal (kedalaman 0  
        

        while (this.solution == null) {
            this.treeStack = new Stack<IDSNode>(); 
            this.treeStack.push(rootNode); 
            
            while (!this.treeStack.isEmpty()) {
                IDSNode currentNode = this.treeStack.pop(); 
                Board currentBoardState = currentNode.getCurrentBoard();

                if (currentBoardState.isFinished()) {
                    this.solution = currentNode; 
                    this.isFinished = true;
                    break; 
                }

                if (currentNode.getDepth() < this.curMaxDepth) {
                    if (currentNode.getDepth() > 0) {
                        List<Board> path = currentNode.getPathOfBoards();
                    }
                    
                    List<Board> nextPossibleBoards;
                    nextPossibleBoards = utils.generateAllPossibleMoves(currentBoardState, currentBoardState.getLastMoves(), currentBoardState.getLastDist(), currentBoardState.getLastPiece());


                    for (int i = nextPossibleBoards.size() - 1; i >= 0; i--) {
                        Board nextBoard = nextPossibleBoards.get(i);
                        IDSNode childNode = new IDSNode(nextBoard, currentNode); 
                        this.treeStack.push(childNode);
                        this.exploredNodes++; 
                    }
                }
            } 

            if (this.solution != null) {
                break;
            }

            this.curMaxDepth++;

            if (this.curMaxDepth > 30) { 
                System.out.println("IDS mencapai batas kedalaman praktis (" + this.curMaxDepth + ") tanpa menemukan solusi.");
                break;
            }
        }

    }

    public IDSNode getSolution() {
        return this.solution;
    }

    public int getExploredNodesCount() {
        return this.exploredNodes;
    }

    public int getMaxDepthReached() {
        return this.curMaxDepth; 
    }

    public void printSolutionPath() {
        if (this.solution != null) {
            List<Board> path = solution.getPathOfBoards();
            for (int i = 0; i < path.size(); i++) {
                System.out.println("\nLangkah Ke-" + i + " (Papan ke-" + (i+1) + "):");
                path.get(i).printBoardState();
            }
            System.out.println("Kedalaman: " + this.solution.getDepth() );
            System.out.println("Jumlah Node: " + exploredNodes);
        } else {
            System.out.println("Tidak ada solusi yang ditemukan hingga kedalaman maksimum yang dijelajahi: " + this.curMaxDepth);
        }
    }

    public void writeSolution(String inputFileName) {
        if (isFinished) {
            List<Board> solutionPath = new ArrayList<>(solution.getPathOfBoards());
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "Solution.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                writer.write("=====================================================\n");
                writer.write("         SOLUSI RUSH HOUR MENGGUNAKAN IDS            \n");
                writer.write("=====================================================\n\n");
                writer.write("Total langkah: " + (solutionPath.size() - 1) + "\n");
                writer.write("Total node dieksplorasi: " + exploredNodes + "\n\n");
                
                for (int i = 0; i < solutionPath.size(); i++) {
                    writer.write("Langkah Ke-" + i + "\n");
                    Board board = solutionPath.get(i);
                    
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