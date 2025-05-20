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
    private int nodeCount; // untuk menghitung jumlah node yang diperiksa
    private Set<Board> visitedBoards; // untuk menghindari siklus - langsung menyimpan objek Board
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
        // Tambahkan board awal ke path solusi
        solutionPath.add(initBoard);
        currentBoard = new Board(initBoard);
        visitedBoards.add(currentBoard); // Langsung menambahkan objek Board

        int heuristicValue;

        // Loop sampai menemukan solusi atau tidak ada langkah valid yang tersisa
        while (!currentBoard.isFinished()) {
            // Generate semua kemungkinan move dari node saat ini
            // currentBoard.printBoardState();
            // scanner.nextLine();
            List<Board> currentPossibleBoards = new ArrayList<Board>(utils.generateAllPossibleMoves(currentBoard, currentBoard.getLastMoves(), currentBoard.getLastDist(), currentBoard.getLastPiece()));
            
            // Cari board dengan nilai heuristik terendah
            Board bestBoard = null;
            int minHeuristicValue = Integer.MAX_VALUE;
            
            for (Board nextBoard : currentPossibleBoards) {
                nodeCount++;
                
                // Jika menemukan solusi, langsung pilih board ini
                if (nextBoard.isFinished()) {
                    bestBoard = nextBoard;
                    break;
                }
                
                // Periksa apakah board sudah pernah dikunjungi untuk menghindari siklus
                if (isContain(nextBoard)) {
                    // System.out.println("CONTAINED");
                    // scanner.nextLine();
                    continue; // Lewati board yang sudah pernah dikunjungi
                }
                
                // Evaluasi board menggunakan heuristik
                if(isTreeHeuristic) heuristicValue = th.evaluate(nextBoard);
                else heuristicValue = dh.evaluate(nextBoard);
                
                // Update board terbaik jika nilai heuristik lebih kecil
                if (heuristicValue < minHeuristicValue) {
                    minHeuristicValue = heuristicValue;
                    bestBoard = nextBoard;
                }
            }
            
            // Jika tidak ada board yang valid untuk dipilih
            if (bestBoard == null) {
                // Tidak ada solusi yang dapat ditemukan
                break;
            }
            
            // Pindah ke board terbaik berdasarkan heuristik
            currentBoard = bestBoard;
            visitedBoards.add(currentBoard);
            solutionPath.add(currentBoard);
            
            // Jika solusi ditemukan, keluar dari loop
            if (currentBoard.isFinished()) {
                //System.out.println("here finish");
                scanner.nextLine();
                break;
            }
        }
        System.out.println("node: " + this.getNodeCount());
        scanner.nextLine();
    }
    
    // Method untuk mendapatkan path solusi
    public List<Board> getSolutionPath() {
        return solutionPath;
    }
    
    // Method untuk mengecek apakah solusi ditemukan
    public boolean isSolutionFound() {
        return currentBoard.isFinished();
    }
    
    // Method untuk mendapatkan jumlah langkah dalam solusi
    public int getSolutionSteps() {
        return solutionPath.size() - 1; // Kurangi 1 karena board awal tidak dihitung sebagai langkah
    }
    
    // Method untuk mendapatkan jumlah node yang diperiksa
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
                // System.out.print("Enter anything to continue ...");
                // scanner.nextLine();
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