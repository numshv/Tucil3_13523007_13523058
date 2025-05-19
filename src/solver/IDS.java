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

    public IDS(Board initBoard){
        this.solution = null;         // Menyimpan node solusi jika ditemukan
        this.exploredNodes = 0;       // Menghitung node yang dieksplorasi/digenerate
        this.curMaxDepth = 0;         // Batas kedalaman dimulai dari 0 untuk Iterative Deepening

        Helper moveGenerator = new Helper(); // Objek untuk membantu generate langkah
        IDSNode rootNode = new IDSNode(initBoard); // Membuat node awal (kedalaman 0)
        this.exploredNodes++;                // Menghitung node root

        // Loop utama Iterative Deepening Search
        while (this.solution == null) { // Berlanjut sampai solusi ditemukan (atau kondisi berhenti lain)
            this.treeStack = new Stack<IDSNode>(); // Membuat STACK BARU untuk setiap iterasi Depth-Limited Search (DLS)

            // Hanya masukkan rootNode ke stack jika kedalamannya (0) tidak melebihi batas saat ini
            if (rootNode.getDepth() <= this.curMaxDepth) {
                this.treeStack.push(rootNode);
            } 
            // Jika curMaxDepth < rootNode.getDepth() (misal curMaxDepth negatif, tidak mungkin di sini),
            // maka stack akan kosong dan DLS untuk kedalaman itu tidak akan berjalan, lalu curMaxDepth akan naik.

            System.out.println("IDS: Melakukan Depth-Limited Search dengan batas kedalaman = " + this.curMaxDepth);

            // Loop DLS (Depth-Limited Search) untuk batas kedalaman saat ini
            while (!this.treeStack.isEmpty()) {
                IDSNode currentNode = this.treeStack.pop(); // Ambil node dari stack
                Board currentBoardState = currentNode.getCurrentBoard();

                // (Opsional) Debugging untuk melihat node yang sedang diproses
                // System.out.println("  Mengunjungi Node (Depth: " + currentNode.getDepth() + ")");
                // currentBoardState.printBoardState();

                // 1. Cek apakah state saat ini adalah SOLUSI
                if (currentBoardState.isFinished()) {
                    this.solution = currentNode; // Simpan node solusi
                    System.out.println("SOLUSI DITEMUKAN pada kedalaman: " + currentNode.getDepth() +
                                    " (batas pencarian saat ini: " + this.curMaxDepth + ")");
                    break; // Keluar dari loop DLS (inner while)
                }

                // 2. Ekspansi node jika kedalamannya MASIH DI BAWAH batas kedalaman saat ini
                if (currentNode.getDepth() < this.curMaxDepth) {
                    // Dapatkan semua piece dari papan saat ini
                    Map<Character, Piece> piecesOnBoard = currentBoardState.getAllPieces(); // Asumsi Board punya metode ini
                    if (piecesOnBoard != null) {
                        // Untuk setiap piece, generate semua kemungkinan langkahnya
                        for (Piece pieceToMove : piecesOnBoard.values()) {
                            // Helper akan menghasilkan semua kemungkinan Board baru untuk pieceToMove ini
                            moveGenerator.generateAllMoves(currentBoardState, pieceToMove, null); // lastMove = null untuk kesederhanaan
                            List<Board> nextPossibleBoards = moveGenerator.getBoardMoves();

                            // // DEBUG print block (yang Anda tambahkan)
                            // System.out.println("ALL POSSIBLE MOVE RN (untuk piece '" + pieceToMove.getPieceType() + "'):");
                            // for(Board board : nextPossibleBoards){
                            //     board.printBoardState();
                            //     System.out.println("---");
                            // }

                            // Masukkan semua state anak yang valid ke stack
                            // Dimasukkan terbalik agar eksplorasi konsisten (misal, jika generateNextMoves punya urutan)
                            for (int i = nextPossibleBoards.size() - 1; i >= 0; i--) {
                                Board nextBoard = nextPossibleBoards.get(i);
                                // Buat objek IDSNode baru untuk anak ini
                                IDSNode childNode = new IDSNode(nextBoard, currentNode); 
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

            // Tambahkan kondisi berhenti praktis jika tidak ada solusi atau pencarian terlalu dalam
            if (this.curMaxDepth > 30) { // Batas kedalaman ini bisa Anda sesuaikan
                System.out.println("IDS mencapai batas kedalaman praktis (" + this.curMaxDepth + ") tanpa menemukan solusi.");
                break;
            }
            // Jika stack DLS kosong dan tidak ada solusi ditemukan pada kedalaman tertentu,
            // IDS akan secara otomatis melanjutkan ke kedalaman berikutnya.
            // Kondisi berhenti tambahan yang lebih canggih (misalnya, jika tidak ada node baru yang dieksplorasi) bisa ditambahkan
            // jika grafnya diketahui terbatas dan tidak ada solusi.
        } // Akhir dari loop IDS utama (while this.solution == null)

        System.out.println("Pencarian IDS selesai. Total node (perkiraan) digenerate/dimasukkan stack: " + this.exploredNodes);
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
            }
            System.out.println("--------------------------------");
            System.out.println("--- Kedalaman: " + this.solution.getDepth() + " ---");
            System.out.println("--- Jumlah Node: " + exploredNodes + ") ---");
        } else {
            System.out.println("Tidak ada solusi yang ditemukan hingga kedalaman maksimum yang dijelajahi: " + this.curMaxDepth);
        }
    }
}