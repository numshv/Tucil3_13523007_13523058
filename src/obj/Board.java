package obj;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Board {
    private int boardRow; // nilai row [1...boardRow]
    private int boardCol;// nilai col [1...boardCol]
    private int exitRow;
    private int exitCol;
    private char[][] boardState;
    private boolean boardFinished;
    private int pieceCounter;
    private Map<Character, Piece> pieces;

    // Default board state = empty board
    public Board(int boardRow, int boardCol, int exitRow, int exitCol){
        boardState = new char[boardRow+1][boardCol+1];
        for(int i=1; i<=boardRow; i++){
            for(int j=1; j<=boardCol; j++){
                this.boardState[i][j] = '.';
            }
        }
        this.exitRow = exitRow;
        this.exitCol = exitCol;
        this.boardRow = boardRow;
        this.boardCol = boardCol;
        this.boardFinished = false;
        this.pieceCounter = 0;
        this.pieces = new HashMap<Character, Piece>();
    }

    public Board(char[][] boardState, int exitRow, int exitCol) throws Exception{
        this.boardRow = boardState.length;
        this.boardCol = boardState[0].length;
        this.boardState = new char[this.boardRow+1][this.boardCol+1];
        Set<Character> uniquePieces = new HashSet<>();
        this.pieces = new HashMap<Character, Piece>();
        this.pieceCounter = 0;

        // Copy boardState to this.boardState (with offset +1) and validate characters
        for(int i=0; i<this.boardRow; i++){
            for(int j=0; j<this.boardCol; j++){
                char currentChar = boardState[i][j];
                
                // Validate: only allow alphabets and '.'
                if(currentChar != '.' && !Character.isLetter(currentChar)){
                    throw new Exception("Board hanya boleh berisi karakter alfabet atau '.'");
                }
                
                // Convert to uppercase if it's a letter
                if(Character.isLetter(currentChar)){
                    currentChar = Character.toUpperCase(currentChar);
                }
                
                this.boardState[i + 1][j + 1] = currentChar;
                
                // Update original boardState to uppercase for consistent detection
                boardState[i][j] = currentChar;
            }
        }

        // Detect pieces on the board
        for(int i=0; i<this.boardRow; i++){
            for(int j=0; j<this.boardCol; j++){
                char currentPiece = boardState[i][j];
                
                // Skip empty cells and already processed pieces
                if(currentPiece == '.' || uniquePieces.contains(currentPiece)){
                    continue;
                }
                
                // Check horizontal piece
                boolean isHorizontal = false;
                int pieceLength = 1;
                
                if(i+1 < this.boardRow && boardState[i+1][j] == currentPiece && j+1 < this.boardCol && boardState[i][j+1] == currentPiece) throw new Exception("Piece hanya boleh horizontal atau vertikal");

                // Check horizontal direction
                else if(j+1 < this.boardCol && boardState[i][j+1] == currentPiece){
                    isHorizontal = true;
                    pieceLength = 1; // Reset and count from the beginning
                    
                    // Count length horizontally
                    while(j+pieceLength < this.boardCol && boardState[i][j+pieceLength] == currentPiece){
                        pieceLength++;
                    }
                    
                    Piece newPiece = new Piece(currentPiece, pieceLength, isHorizontal);
                    pieces.put(currentPiece, newPiece);
                    uniquePieces.add(currentPiece);
                    this.pieceCounter++;
                }
                // Check vertical direction

                else if(i+1 < this.boardRow && boardState[i+1][j] == currentPiece){
                    isHorizontal = false;
                    pieceLength = 1; // Reset and count from the beginning
                    
                    // Count length vertically
                    while(i+pieceLength < this.boardRow && boardState[i+pieceLength][j] == currentPiece){
                        pieceLength++;
                    }
                    
                    Piece newPiece = new Piece(currentPiece, pieceLength, isHorizontal);
                    pieces.put(currentPiece, newPiece);
                    uniquePieces.add(currentPiece);
                    this.pieceCounter++;
                }
                else{
                    // Single character piece, minimum length should be 2
                    throw new Exception("Panjang piece setidaknya 2");
                }
            }
        }
        
        // Check if primary piece 'P' exists
        if(!uniquePieces.contains('P')){
            throw new Exception("Papan harus memiliki piece primer 'P'");
        }

        this.exitRow = exitRow;
        this.exitCol = exitCol;
        this.boardFinished = false;

        if(!isExitValid()) throw new Exception("Posisi exit tidak valid");
        
        // Kurangi pieceCounter untuk mengecualikan piece primer 'P'
        this.pieceCounter--;
    }

    // Tambahkan getter untuk mendapatkan piece berdasarkan karakter
    public Piece getPiece(char pieceChar) {
        return pieces.get(pieceChar);
    }
    
    // Tambahkan getter untuk mendapatkan semua pieces
    public Map<Character, Piece> getAllPieces() {
        return pieces;
    }

    public int getBoardRow(){
        return boardRow;
    }

    public int getBoardCol(){
        return boardCol;
    }

    public int getExitRow(){
        return exitRow;
    }

    public int getExitCol(){
        return exitCol;
    }

    public void printBoardState(){
        System.out.println("============= Susunan papan saat ini =============\n");
        if(exitRow == 0){
            for(int i = 0 ; i < boardCol+1;i++){
                if(i == exitCol){
                    System.out.print('K');
                }
                else{
                    System.out.print(' ');
                }
            }
            System.err.println();
        }
        for(int i=1; i<=boardRow; i++){
            if(i != exitRow) System.out.print(" ");
            else{
                if(exitCol == 0) System.out.print("K");
                else System.out.print(" ");
            }
            for(int j=1; j<=boardCol; j++){
                System.out.print(boardState[i][j]);
            }
            if(i != exitRow) System.out.print(" ");
            else{
                if(exitCol == boardCol+1) System.out.print("K");
                else System.out.print(" ");
            }
            System.out.print("\n");
        }
        if(exitRow == boardRow+1){
            for(int i = 0 ; i < boardCol+1;i++){
                if(i == exitCol){
                    System.out.print('K');
                }
                else{
                    System.out.print(' ');
                }
            }
            System.err.println();
        }
        System.out.println("\n\n");
    }

    // Cek secara vertikal apakah kosong
    public boolean rowSpaceEmpty(int row, int col, int len){
        for(int i=row; i<row+len; i++){
            if(boardState[i][col] != '.') return false;
        }
        return true;
    }

    // Cek secara horizontal apakah kosong
    public boolean colSpaceEmpty(int row, int col, int len){
        for(int j=col; j<col+len; j++){
            if(boardState[row][j] != '.') return false;
        }
        return true;
    }

    public int getStartRowPiece(Piece p){
        char type = p.getPieceType();
        for(int i=1; i<=boardRow; i++){
            for(int j=1; j<=boardCol; j++){
                if(boardState[i][j] == type){
                    return i;
                }
            }
        }
        return -1;
    }

    public int getStartRowPiece(char type){
        for(int i=1; i<=boardRow; i++){
            for(int j=1; j<=boardCol; j++){
                if(boardState[i][j] == type){
                    return i;
                }
            }
        }
        return -1;
    }

    public int getStartColPiece(Piece p){
        char type = p.getPieceType();
        for(int i=1; i<=boardRow; i++){
            for(int j=1; j<=boardCol; j++){
                if(boardState[i][j] == type){
                    return j;
                } 
            }
        }
        return -1;
    }

    public int getStartColPiece(char type){
        for(int i=1; i<=boardRow; i++){
            for(int j=1; j<=boardCol; j++){
                if(boardState[i][j] == type){
                    return j;
                } 
            }
        }
        return -1;
    }

    public int getPieceCounter(){
        return this.pieceCounter;
    }

    public void addPiece(Piece p, int row, int col){
        if(p.checkHorizontal()){ // Horizontal
            if(colSpaceEmpty(row, col, p.getLen())){
                for(int j=col; j<col+p.getLen(); j++){
                    boardState[row][j] = p.getPieceType();
                }
                this.pieceCounter++;
            }
        }else{ // Vertikal
            if(rowSpaceEmpty(row, col, p.getLen())){
                for(int i=row; i<row+p.getLen(); i++){
                    boardState[i][col] = p.getPieceType();
                }
                this.pieceCounter++;
            }
        }
    }

    // Row itu topmost piece position dan col itu leftmost piece position
    public void removePiece(Piece p, int row, int col){
        if(p.checkHorizontal()){ // Horizontal
            for(int j=col; j<col+p.getLen(); j++){
                boardState[row][j] = '.';
            }
            this.pieceCounter--;
        }else{ // Vertikal
            for(int i=row; i<row+p.getLen(); i++){
                boardState[i][col] = '.';
            }
            this.pieceCounter--;
        }
    }

    // Row itu topmost piece position dan col itu leftmost piece position
    public void removePiece(char type, int row, int col){
        Piece p = pieces.get(type);
        if(p.checkHorizontal()){ // Horizontal
            for(int j=col; j<col+p.getLen(); j++){
                boardState[row][j] = '.';
            }
            this.pieceCounter--;
        }else{ // Vertikal
            for(int i=row; i<row+p.getLen(); i++){
                boardState[i][col] = '.';
            }
            this.pieceCounter--;
        }
    }

    public void moveUpPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(!p.checkHorizontal()){
            if(p.checkPrimary()){
                if(row-dist <= exitRow && col == exitCol){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            
            int newRow = row-dist;
            // Kalo pergeseran melebihi lebar board
            if(newRow <= 0) newRow = 1;
            if(rowSpaceEmpty(newRow, col, dist)){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveUpPiece(char type, int dist){
        Piece p = pieces.get(type);
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(!p.checkHorizontal()){
            if(p.checkPrimary()){
                if(row-dist <= exitRow && col == exitCol){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            
            int newRow = row-dist;
            // Kalo pergeseran melebihi lebar board
            if(newRow <= 0) newRow = 1;
            if(rowSpaceEmpty(newRow, col, dist)){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveDownPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(!p.checkHorizontal()){
            if(p.checkPrimary()){
                if(row+p.getLen()+dist-1 >= exitRow && col == exitCol){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            int newRow = row+dist;
            // Kalo pergeseran melebihi lebar board
            if(newRow+p.getLen()-1 > boardRow) newRow = boardRow-p.getLen()+1;
            if(rowSpaceEmpty(newRow, col, dist)){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveDownPiece(char type, int dist){
        Piece p = pieces.get(type);
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(!p.checkHorizontal()){
            if(p.checkPrimary()){
                if(row+p.getLen()+dist-1 >= exitRow && col == exitCol){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            int newRow = row+dist;
            // Kalo pergeseran melebihi lebar board
            if(newRow+p.getLen()-1 > boardRow) newRow = boardRow-p.getLen()+1;
            if(rowSpaceEmpty(newRow, col, dist)){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveLeftPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(p.checkHorizontal()){
            if(p.checkPrimary()){
                if(col-dist <= exitCol && row == exitRow){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            int newCol = col-dist;
            // Kalo pergeseran melebihi lebar board
            if(newCol <= 0) newCol = 1;
            if(colSpaceEmpty(row, newCol, dist)){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public void moveLeftPiece(char type, int dist){
        Piece p = pieces.get(type);
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(p.checkHorizontal()){
            if(p.checkPrimary()){
                if(col-dist <= exitCol && row == exitRow){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            int newCol = col-dist;
            // Kalo pergeseran melebihi lebar board
            if(newCol <= 0) newCol = 1;
            if(colSpaceEmpty(row, newCol, dist)){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public void moveRightPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(p.checkHorizontal()){
            if(p.checkPrimary()){
                if(col+p.getLen()+dist-1 >= exitCol && row == exitRow){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            int newCol = col+dist;
            // Kalo pergeseran melebihi lebar board
            if(newCol+p.getLen()-1 > boardCol) newCol = boardCol-p.getLen()+1;
            if(colSpaceEmpty(row, newCol, dist)){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public void moveRightPiece(char type, int dist){
        Piece p = pieces.get(type);
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(p.checkHorizontal()){
            if(p.checkPrimary()){
                if(col+p.getLen()+dist-1 >= exitCol && row == exitRow){
                    removePiece(p, row, col);
                    boardFinished = true;
                    return;
                } 
            }
            int newCol = col+dist;
            // Kalo pergeseran melebihi lebar board
            if(newCol+p.getLen()-1 > boardCol) newCol = boardCol-p.getLen()+1;
            if(colSpaceEmpty(row, newCol, dist)){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public boolean isFinished(){
        return boardFinished;
    }

    public boolean isExitValid(){
        if(pieces.get('P').checkHorizontal() == true) return getStartRowPiece(pieces.get('P')) == exitRow;
        return getStartColPiece(pieces.get('P')) == exitCol;
    }
}