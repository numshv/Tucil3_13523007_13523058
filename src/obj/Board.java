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
    private String lastMove;
    private int lastDist;
    private Piece lastPiece;
    private static final Map<Character, String> COLOR_MAP = new HashMap<Character, String>() {{
        put('A', "\u001B[31m"); // Red
        put('B', "\u001B[32m"); // Green
        put('C', "\u001B[33m"); // Yellow
        put('D', "\u001B[34m"); // Blue
        put('E', "\u001B[35m"); // Purple
        put('F', "\u001B[36m"); // Cyan
        put('G', "\u001B[91m"); // Bright Red
        put('H', "\u001B[92m"); // Bright Green
        put('I', "\u001B[93m"); // Bright Yellow
        put('J', "\u001B[94m"); // Bright Blue
        put('K', "\u001B[95m"); // Bright Purple
        put('L', "\u001B[96m"); // Bright Cyan
        put('M', "\u001B[37m"); // White
        put('N', "\u001B[90m"); // Bright Black/Gray
        put('O', "\u001B[97m"); // Bright White
        put('P', "\u001B[44m\u001B[97m"); // Blue background, bright white text
        put('Q', "\u001B[31m\u001B[1m"); // Bold Red
        put('R', "\u001B[32m\u001B[1m"); // Bold Green
        put('S', "\u001B[33m\u001B[1m"); // Bold Yellow
        put('T', "\u001B[34m\u001B[1m"); // Bold Blue
        put('U', "\u001B[35m\u001B[1m"); // Bold Purple
        put('V', "\u001B[36m\u001B[1m"); // Bold Cyan
        put('W', "\u001B[37m\u001B[1m"); // Bold White
        put('X', "\u001B[91m\u001B[1m"); // Bold Bright Red
        put('Y', "\u001B[92m\u001B[1m"); // Bold Bright Green
        put('Z', "\u001B[93m\u001B[1m"); // Bold Bright Yellow
    }};
    private static final String RESET = "\u001B[0m";
    private static final String EXIT_COLOR = "\u001B[42m\u001B[30m"; 

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

    public Board(Board other) {
        this.boardRow = other.boardRow;
        this.boardCol = other.boardCol;
        this.exitRow = other.exitRow;
        this.exitCol = other.exitCol;
        this.boardFinished = other.boardFinished;
        this.pieceCounter = other.pieceCounter;

        this.boardState = new char[boardRow + 1][boardCol + 1];
        for (int i = 1; i <= boardRow; i++) {
            for (int j = 1; j <= boardCol; j++) {
                this.boardState[i][j] = other.getCharAt(i, j);
            }
        }

        this.pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : other.pieces.entrySet()) {
            this.pieces.put(entry.getKey(), new Piece(entry.getValue()));
        }
    }


    public Board(char[][] boardState, int exitRow, int exitCol) throws Exception{
        this.boardRow = boardState.length;
        this.boardCol = boardState[0].length;
        this.boardState = new char[this.boardRow+1][this.boardCol+1];
        Set<Character> uniquePieces = new HashSet<>();
        this.pieces = new HashMap<Character, Piece>();
        this.pieceCounter = 0;

        for(int i=0; i<this.boardRow; i++){
            for(int j=0; j<this.boardCol; j++){
                char currentChar = boardState[i][j];
                
                if(currentChar != '.' && !Character.isLetter(currentChar)){
                    throw new Exception("Board hanya boleh berisi karakter alfabet atau '.'");
                }
                
                if(Character.isLetter(currentChar)){
                    currentChar = Character.toUpperCase(currentChar);
                }
                this.boardState[i + 1][j + 1] = currentChar;
                boardState[i][j] = currentChar;
            }
        }

        for(int i=0; i<this.boardRow; i++){
            for(int j=0; j<this.boardCol; j++){
                char currentPiece = boardState[i][j];
                
                if(currentPiece == '.' || uniquePieces.contains(currentPiece)){
                    continue;
                }
                
                boolean isHorizontal = false;
                int pieceLength = 1;
                
                if(i+1 < this.boardRow && boardState[i+1][j] == currentPiece && j+1 < this.boardCol && boardState[i][j+1] == currentPiece) throw new Exception("Piece hanya boleh horizontal atau vertikal");

                else if(j+1 < this.boardCol && boardState[i][j+1] == currentPiece){
                    isHorizontal = true;
                    pieceLength = 1;

                    while(j+pieceLength < this.boardCol && boardState[i][j+pieceLength] == currentPiece){
                        pieceLength++;
                    }
                    
                    Piece newPiece = new Piece(currentPiece, pieceLength, isHorizontal);
                    pieces.put(currentPiece, newPiece);
                    uniquePieces.add(currentPiece);
                    this.pieceCounter++;
                }

                else if(i+1 < this.boardRow && boardState[i+1][j] == currentPiece){
                    isHorizontal = false;
                    pieceLength = 1; 
                    
                    while(i+pieceLength < this.boardRow && boardState[i+pieceLength][j] == currentPiece){
                        pieceLength++;
                    }
                    
                    Piece newPiece = new Piece(currentPiece, pieceLength, isHorizontal);
                    pieces.put(currentPiece, newPiece);
                    uniquePieces.add(currentPiece);
                    this.pieceCounter++;
                }
                else{
                    throw new Exception("Panjang piece setidaknya 2");
                }
            }
        }
        
        if(!uniquePieces.contains('P')){
            throw new Exception("Papan harus memiliki piece primer 'P'");
        }

        if(isNotSolvable()) throw new Exception("Papan tidak ada solvable, ada piece yang menghalangi P dan searah exit");

        this.exitRow = exitRow;
        this.exitCol = exitCol;
        this.boardFinished = false;

        if(!isExitValid()) throw new Exception("Posisi exit tidak valid");
        
        this.pieceCounter--;
    }

    public boolean isEqual(Board other){
        if(
            boardRow != other.boardRow ||
            boardCol != other.boardCol ||
            exitRow != other.exitRow ||
            exitCol != other.exitCol ||
            pieceCounter != other.pieceCounter
        ) return false;
        for(int i=1;i<=boardRow;i++){
            for(int j = 1;j<=boardCol;j++){
                if(boardState[i][j] != other.boardState[i][j]) return false;
            }
        }
        if(other.exitCol != exitCol || other.exitRow != exitRow) return false;
        return true;
    }

    public boolean isNotSolvable() {
        if (getPiece('P').isHorizontal() && exitCol == 0) {
            int row = getStartRowPiece('P');  
            for (int i = 1; i < getStartColPiece('P'); i++) {
                if (boardState[row][i] != '.' && getPiece(boardState[row][i]).isHorizontal()) 
                    return true;
            }
            return false;
        }
        else if (getPiece('P').isHorizontal() && exitCol == boardCol + 1) {
            int row = getStartRowPiece('P');  
            for (int i = boardCol; i > getEndColPiece('P'); i--) {
                if (boardState[row][i] != '.' && getPiece(boardState[row][i]).isHorizontal()) 
                    return true;
            }
            return false;
        }
        else if (!getPiece('P').isHorizontal() && exitRow == 0) {
            int col = getStartColPiece('P');  
            for (int i = 1; i < getStartRowPiece('P'); i++) {
                if (boardState[i][col] != '.' && !getPiece(boardState[i][col]).isHorizontal()) 
                    return true;
            }
            return false;
        } 
        else {  
            int col = getStartColPiece('P');  
            for (int i = boardRow; i > getEndRowPiece('P'); i--) {
                if (boardState[i][col] != '.' && !getPiece(boardState[i][col]).isHorizontal()) 
                    return true;
            }
            return false;
        }
    }

    public Piece getPiece(char pieceChar) {
        return pieces.get(pieceChar);
    }

    public Map<Character, Piece> getAllPieces() {
        return pieces;
    }

    public String getLastMoves(){
        return lastMove;
    }

    public void setLastMove(String lastMove){
        this.lastMove = lastMove;
    }

    public int getLastDist(){
        return lastDist;
    }

    public void setLastDist(int lastDist){
        this.lastDist = lastDist;
    }

    public Piece getLastPiece(){
        return lastPiece;
    }
    
    public void setLastPiece(Piece lastPiece){
        this.lastPiece = lastPiece;
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

    public char[][] getBoardState(){
        return boardState;
    }

    public void printBoardState(){
        System.out.println("============= Susunan papan saat ini =============\n");
        if(exitRow == 0){
            for(int i = 0; i < boardCol+1; i++){
                if(i == exitCol){
                    System.out.print(EXIT_COLOR + "K" + RESET);
                }
                else{
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        for(int i=1; i<=boardRow; i++){
            if(i != exitRow) System.out.print(" ");
            else{
                if(exitCol == 0) System.out.print(EXIT_COLOR + "K" + RESET);
                else System.out.print(" ");
            }
            for(int j=1; j<=boardCol; j++){
                char currentChar = boardState[i][j];
                if(currentChar != '.') {
                    String color = COLOR_MAP.getOrDefault(currentChar, "\u001B[37m"); // Default to white
                    System.out.print(color + currentChar + RESET);
                } else {
                    System.out.print(currentChar);
                }
            }
            if(i != exitRow) System.out.print(" ");
            else{
                if(exitCol == boardCol+1) System.out.print(EXIT_COLOR + "K" + RESET);
                else System.out.print(" ");
            }
            System.out.print("\n");
        }
        if(exitRow == boardRow+1){
            for(int i = 0; i < boardCol+1; i++){
                if(i == exitCol){
                    System.out.print(EXIT_COLOR + "K" + RESET);
                }
                else{
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println("\n\n");
    }

    // Cek secara vertikal apakah kosong
    public boolean rowSpaceEmpty(int row, int col, int len){
        for(int i=row; i<row+len; i++){
            if(i < 1 || i > boardRow || boardState[i][col] != '.') return false;
        }
        return true;
    }

    // Cek secara horizontal apakah kosong
    public boolean colSpaceEmpty(int row, int col, int len){
        for(int j=col; j<col+len; j++){
            if(j < 1 || j > boardCol || boardState[row][j] != '.') return false;
        }
        return true;
    }

    // Cek secara vertikal dari start ke end apakah kosong (inclusive)
    public boolean verticalRangeEmpty(int startRow, int endRow, int col) {
        // Make sure start <= end
        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }
        
        for (int i = startRow; i <= endRow; i++) {
            if (i < 1 || i > boardRow || (boardState[i][col] != '.' && boardState[i][col] != boardState[startRow][col])) {
                return false;
            }
        }
        return true;
    }

    // Cek secara horizontal dari start ke end apakah kosong (inclusive)
    public boolean horizontalRangeEmpty(int row, int startCol, int endCol) {
        // Make sure start <= end
        if (startCol > endCol) {
            int temp = startCol;
            startCol = endCol;
            endCol = temp;
        }
        
        for (int j = startCol; j <= endCol; j++) {
            if (j < 1 || j > boardCol || (boardState[row][j] != '.' && boardState[row][j] != boardState[row][startCol])) {
                return false;
            }
        }
        return true;
    }

    public Character getCharAt(int row, int col){
        return boardState[row][col];
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

    public int getEndColPiece(Piece p){
        if(p.isHorizontal()) return (getStartColPiece(p) + p.getLen() - 1);
        return getStartColPiece(p);
    }
    public int getEndColPiece(char type){
        Piece p = getPiece(type);
        if(p.isHorizontal()) return (getStartColPiece(p) + p.getLen() - 1);
        return getStartColPiece(p);
    }

    public int getEndRowPiece(Piece p){
        if(!p.isHorizontal()) return (getStartRowPiece(p) + p.getLen() - 1);
        return getStartRowPiece(p);
    }
    public int getEndRowPiece(char type){
        Piece p = getPiece(type);
        if(!p.isHorizontal()) return (getStartRowPiece(p) + p.getLen() - 1);
        return getStartRowPiece(p);
    }


    public int getPieceCounter(){
        return this.pieceCounter;
    }

    public void addPiece(Piece p, int row, int col){
        if(p.isHorizontal()){ // Horizontal
            if(colSpaceEmpty(row, col, p.getLen())){
                for(int j=col; j<col+p.getLen(); j++){
                    boardState[row][j] = p.getPieceType();
                }
                pieces.put(p.getPieceType(), p);
                this.pieceCounter++;
            }
        }else{ // Vertikal
            if(rowSpaceEmpty(row, col, p.getLen())){
                for(int i=row; i<row+p.getLen(); i++){
                    boardState[i][col] = p.getPieceType();
                }
                pieces.put(p.getPieceType(), p);
                this.pieceCounter++;
            }
        }
    }

    // Row itu topmost piece position dan col itu leftmost piece position
    public void removePiece(Piece p, int row, int col){
        if(p.isHorizontal()){ // Horizontal
            for(int j=col; j<col+p.getLen(); j++){
                boardState[row][j] = '.';
            }
            pieces.remove(p.getPieceType(), p);
            this.pieceCounter--;
        }else{ // Vertikal
            for(int i=row; i<row+p.getLen(); i++){
                boardState[i][col] = '.';
            }
            pieces.remove(p.getPieceType(), p);
            this.pieceCounter--;
        }
    }

    // Row itu topmost piece position dan col itu leftmost piece position
    public void removePiece(char type, int row, int col){
        Piece p = pieces.get(type);
        if(p.isHorizontal()){ // Horizontal
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
        if(!p.isHorizontal()){
            int newRow = row-dist;
            if(newRow < 1) newRow = 1;
            
            if(verticalRangeEmpty(newRow, row-1, col)){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveUpPiece(char type, int dist){
        Piece p = pieces.get(type);
        moveUpPiece(p, dist);
    }

    public void moveDownPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(!p.isHorizontal()){
            int pieceEndRow = row + p.getLen() - 1;
            
            int newRow = row+dist;
            int newEndRow = newRow + p.getLen() - 1;
            if(newEndRow > boardRow) newRow = boardRow - p.getLen() + 1;
            
            newEndRow = newRow + p.getLen() - 1;
            
            if(verticalRangeEmpty(pieceEndRow+1, newEndRow, col)){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveDownPiece(char type, int dist){
        Piece p = pieces.get(type);
        moveDownPiece(p, dist);
    }

    public void moveLeftPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(p.isHorizontal()){
            int newCol = col-dist;
            if(newCol < 1) newCol = 1;
            
            if(horizontalRangeEmpty(row, newCol, col-1)){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public void moveLeftPiece(char type, int dist){
        Piece p = pieces.get(type);
        moveLeftPiece(p, dist);
    }

    public void moveRightPiece(Piece p, int dist){
        int row = getStartRowPiece(p);
        int col = getStartColPiece(p);
        if(p.isHorizontal()){
            int pieceEndCol = col + p.getLen() - 1;
            
            int newCol = col+dist;
            int newEndCol = newCol + p.getLen() - 1;
            if(newEndCol > boardCol) newCol = boardCol - p.getLen() + 1;
            
            newEndCol = newCol + p.getLen() - 1;
            
            if(horizontalRangeEmpty(row, pieceEndCol+1, newEndCol)){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public void moveRightPiece(char type, int dist){
        Piece p = pieces.get(type);
        moveRightPiece(p, dist);
    }

    public boolean isFinished(){
        if(exitCol == 0){
            if(boardState[exitRow][1] == 'P') return true;
            else return false;
        }
        else if(exitCol == boardCol+1){
            if(boardState[exitRow][boardCol] == 'P') return true;
            else return false;
        }
        else if(exitRow == 0){
            if(boardState[1][exitCol] == 'P') return true;
            else return false;
        }
        else if(exitRow == boardRow+1){
            if(boardState[boardRow][exitCol] == 'P') return true;
            else return false;
        }
        else return false;
    }

    public boolean isExitValid(){
        Piece p = pieces.get('P');
        if(p == null) return false;
        
        if(p.isHorizontal()) {
            return getStartRowPiece(p) == exitRow;
        } else {
            return getStartColPiece(p) == exitCol;
        }
    }
}