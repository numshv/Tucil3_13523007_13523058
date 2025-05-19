package solver;

import java.util.List;
import java.util.Map; // Diperlukan untuk Map<Character, Piece>
import java.util.Stack;
import obj.Board;
import obj.Piece;  // Impor kelas Piece

public class IDS {
    private Stack<IDSNode> treeStack; 
    private int curMaxDepth;         
    private IDSNode solution;      
    private int exploredNodes;    

    public IDS(Board initBoard) {
        this.solution = null;
        this.exploredNodes = 0;
        this.curMaxDepth = 0; 

        Helper moveGenerator = new Helper(); 
        IDSNode rootNode = new IDSNode(initBoard);
        this.exploredNodes++; 

        while (this.solution == null) {
            this.treeStack = new Stack<IDSNode>(); // Stack BARU untuk setiap iterasi DLS

            // Hanya masukkan root ke stack jika kedalamannya (0) <= batas kedalaman saat ini
            if (rootNode.getDepth() <= this.curMaxDepth) {
                this.treeStack.push(rootNode);
            } 

            System.out.println("IDS: Melakukan Depth-Limited Search dengan batas kedalaman = " + this.curMaxDepth);

            // Loop DLS (Depth-Limited Search)
            while (!this.treeStack.isEmpty()) {
                IDSNode currentNode = this.treeStack.pop();
                Board currentBoardState = currentNode.getCurrentBoard();

                // Debugging: (opsional, bisa di-uncomment jika perlu)
                // System.out.println("  Mengunjungi Node (Depth: " + currentNode.getDepth() + ")");
                // currentBoardState.printBoardState();

                // 1. Cek apakah state saat ini adalah solusi (goal test)
                if (currentBoardState.isFinished()) {
                    this.solution = currentNode;
                    System.out.println("SOLUSI DITEMUKAN pada kedalaman: " + currentNode.getDepth() +
                                       " (batas pencarian saat ini: " + this.curMaxDepth + ")");
                    break; // Keluar dari loop DLS karena solusi sudah ditemukan
                }

                // 2. Ekspansi node jika kedalamannya masih DI BAWAH batas kedalaman saat ini
                if (currentNode.getDepth() < this.curMaxDepth) {
                    Map<Character, Piece> piecesOnBoard = currentBoardState.getAllPieces();
                    if (piecesOnBoard != null) {
                        // Iterasi melalui semua piece di papan untuk digerakkan
                        for (Piece pieceToMove : piecesOnBoard.values()) {
                            // Generate semua kemungkinan langkah untuk piece ini
                            // Menggunakan null untuk lastMove untuk kesederhanaan IDS dasar
                            moveGenerator.generateAllMoves(currentBoardState, pieceToMove, null);
                            List<Board> nextPossibleBoards = moveGenerator.getBoardMoves();

                            // DEBUG
                            System.out.println("ALL POSSIBLE MOVE RN");
                            for(Board board : nextPossibleBoards){
                                board.printBoardState();
                            }

                            // Masukkan semua state anak yang valid ke stack
                            // Dimasukkan terbalik agar eksplorasi konsisten (jika urutan generateNextMoves penting)
                            for (int i = nextPossibleBoards.size() - 1; i >= 0; i--) {
                                Board nextBoard = nextPossibleBoards.get(i);
                                IDSNode childNode = new IDSNode(nextBoard, currentNode); // Buat node anak
                                this.treeStack.push(childNode);
                                this.exploredNodes++; // Hitung setiap anak yang digenerate dan dimasukkan stack
                            }
                        }
                    }
                }
            } // Akhir dari loop DLS (while !this.treeStack.isEmpty())

            // Jika solusi sudah ditemukan di iterasi DLS ini, keluar dari loop IDS utama
            if (this.solution != null) {
                break;
            }

            // Naikkan batas kedalaman untuk iterasi IDS berikutnya
            this.curMaxDepth++;

            // Tambahkan kondisi berhenti praktis jika tidak ada solusi atau terlalu dalam
            if (this.curMaxDepth > 30) { // Batas kedalaman praktis, bisa disesuaikan
                System.out.println("IDS mencapai batas kedalaman praktis (" + this.curMaxDepth + ") tanpa menemukan solusi.");
                break;
            }
            // Catatan: IDS akan berhenti secara alami jika tidak ada lagi node yang bisa dieksplorasi
            // (misalnya, semua cabang sudah mencapai cutoff dan tidak ada solusi).
            // Namun, jika tidak ada node yang dieksplorasi pada DLS tertentu (stack kosong dari awal
            // padahal curMaxDepth > rootNode.getDepth()), itu bisa jadi indikasi tidak ada solusi yang lebih dalam.
            // Untuk saat ini, batas kedalaman eksplisit sudah cukup.

        } // Akhir dari loop IDS utama (while this.solution == null)

        // Setelah loop selesai, this.solution akan berisi node solusi atau null
        // this.curMaxDepth akan berisi kedalaman di mana solusi ditemukan atau batas akhir pencarian
        // this.exploredNodes akan berisi jumlah perkiraan node yang digenerate
        System.out.println("Pencarian IDS selesai. Total node (perkiraan) digenerate/dimasukkan stack: " + this.exploredNodes);

    } // Akhir dari konstruktor IDS

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
            }
            System.out.println("--------------------------------");
            System.out.println("--- Kedalaman: " + this.solution.getDepth() + " ---");
            System.out.println("--- Jumlah Node: " + exploredNodes + ") ---");
        } else {
            System.out.println("Tidak ada solusi yang ditemukan hingga kedalaman maksimum yang dijelajahi: " + this.curMaxDepth);
        }
    }
}