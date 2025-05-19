package solver;

import java.util.List;
import java.util.Map; // Diperlukan untuk Map<Character, Piece>
import java.util.Stack;
import java.util.Scanner;

import utils.*;

import obj.Board;
import obj.Piece;  // Impor kelas Piece

public class IDS {
    private Stack<IDSNode> treeStack; 
    private int curMaxDepth;         
    private IDSNode solution;      
    private int exploredNodes;    
    private Scanner scanner;

    public IDS(Board initBoard){
        this.solution = null;         // Menyimpan node solusi jika ditemukan
        this.exploredNodes = 0;       // Menghitung node yang dieksplorasi/digenerate
        this.curMaxDepth = 0;         // Batas kedalaman dimulai dari 0 untuk Iterative Deepening
        scanner = new Scanner(System.in);

        Utils utils = new Utils();

        IDSNode rootNode = new IDSNode(initBoard); // Membuat node awal (kedalaman 0  
        
        // scanner.nextLine();

        // Loop utama Iterative Deepening Search
        while (this.solution == null) { // Berlanjut sampai solusi ditemukan (atau kondisi berhenti lain)
            this.treeStack = new Stack<IDSNode>(); // Membuat STACK BARU untuk setiap iterasi Depth-Limited Search (DLS)
            this.treeStack.push(rootNode); // Selalu push root node di awal setiap iterasi
            
            // System.out.println("IDS: Melakukan Depth-Limited Search dengan batas kedalaman = " + this.curMaxDepth);

            // Loop DLS (Depth-Limited Search) untuk batas kedalaman saat ini
            while (!this.treeStack.isEmpty()) {
                IDSNode currentNode = this.treeStack.pop(); // Ambil node dari stack
                Board currentBoardState = currentNode.getCurrentBoard();
                // System.out.println("current node board:");
                // currentBoardState.printBoardState();
                // scanner.nextLine();

                // 1. Cek apakah state saat ini adalah SOLUSI
                if (currentBoardState.isFinished()) {
                    this.solution = currentNode; 
                    // System.out.println("SOLUSI DITEMUKAN pada kedalaman: " + currentNode.getDepth() +
                                    // " (batas pencarian saat ini: " + this.curMaxDepth + ")");
                    break; 
                }

                // 2. Ekspansi node jika kedalamannya MASIH DI BAWAH batas kedalaman saat ini
                if (currentNode.getDepth() < this.curMaxDepth) {
                    // Dapatkan parent board untuk mencegah gerakan mundur
                    // Board parentBoard = null;
                    if (currentNode.getDepth() > 0) {
                        List<Board> path = currentNode.getPathOfBoards();
                        // Pastikan kita mengambil parent board yang benar
                        // if (path.size() >= 2) {
                        //     parentBoard = path.get(path.size() - 2);
                        // }
                    }
                    
                    // Generate semua langkah yang mungkin
                    List<Board> nextPossibleBoards;
                    nextPossibleBoards = utils.generateAllPossibleMoves(currentBoardState);

                    // System.out.println("START OF PRINT ALL POSSIBLE BOARDS");
                    // for(Board b : nextPossibleBoards){
                    //     b.printBoardState();
                    //     scanner.nextLine();
                    // }
                    // System.out.println("END OF PRINT ALL POSSIBLE BOARDS");

                    // Push anak-anak node ke stack dalam urutan terbalik agar pencarian DFS berjalan dengan benar
                    for (int i = nextPossibleBoards.size() - 1; i >= 0; i--) {
                        Board nextBoard = nextPossibleBoards.get(i);
                        IDSNode childNode = new IDSNode(nextBoard, currentNode); 
                        this.treeStack.push(childNode);
                        this.exploredNodes++; // Hitung setiap anak yang digenerate dan dimasukkan stack
                    }
                }
            } 

            if (this.solution != null) {
                break;
            }

            // Naikkan batas kedalaman untuk iterasi IDS berikutnya
            this.curMaxDepth++;

            // Tambahkan kondisi berhenti praktis jika tidak ada solusi atau pencarian terlalu dalam
            if (this.curMaxDepth > 30) { // Batas kedalaman ini bisa Anda sesuaikan
                System.out.println("IDS mencapai batas kedalaman praktis (" + this.curMaxDepth + ") tanpa menemukan solusi.");
                break;
            }
        }

        // System.out.println("Pencarian IDS selesai. Total node (perkiraan) digenerate/dimasukkan stack: " + this.exploredNodes);
    }

    // Getter untuk mengambil hasil (jika diperlukan dari luar setelah objek IDS dibuat)
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
                System.out.print("Enter anything to continue ...");
                scanner.nextLine();
            }
            System.out.println("--------------------------------");
            System.out.println("--- Kedalaman: " + this.solution.getDepth() + " ---");
            System.out.println("--- Jumlah Node: " + exploredNodes + ") ---");
        } else {
            System.out.println("Tidak ada solusi yang ditemukan hingga kedalaman maksimum yang dijelajahi: " + this.curMaxDepth);
        }
    }
}