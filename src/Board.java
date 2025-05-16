public class Board {
    private int boardRow; // nilai row [1...boardRow]
    private int boardCol;// nilai col [1...boardCol]
    private int exitRow;
    private int exitCol;
    private char[][] boardState;
    private boolean boardFinished;

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
    }

    public Board(char[][] boardState, int exitRow, int exitCol){
        this.boardState = new char[boardState.length+1][boardState[1].length+1];
        for(int i=1; i<=boardState.length; i++){
            for(int j=1; j<=boardState[0].length; j++){
                this.boardState[i][j] = boardState[i][j];
            }
        }
        this.exitRow = exitRow;
        this.exitCol = exitCol;
        this.boardRow = boardState.length;
        this.boardCol = boardState[1].length;
        this.boardFinished = false;
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
        for(int i=1; i<=boardRow; i++){
            for(int j=1; j<=boardCol; j++){
                System.out.print(boardState[i][j]);
            }
            System.out.print("\n");
        }
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

    public void addPiece(Piece p, int row, int col){
        if(p.checkHorizontal()){ // Horizontal
            if(colSpaceEmpty(row, col, p.getLen())){
                for(int j=col; j<col+p.getLen(); j++){
                    boardState[row][j] = p.getPieceType();
                }
            }
        }else{ // Vertikal
            if(rowSpaceEmpty(row, col, p.getLen())){
                for(int i=row; i<row+p.getLen(); i++){
                    boardState[i][col] = p.getPieceType();
                }
            }
        }
    }

    // Row itu topmost piece position dan col itu leftmost piece position
    public void removePiece(Piece p, int row, int col){
        if(p.checkHorizontal()){ // Horizontal
            for(int j=col; j<col+p.getLen(); j++){
                boardState[row][j] = '.';
            }
        }else{ // Vertikal
            for(int i=row; i<row+p.getLen(); i++){
                boardState[i][col] = '.';
            }
        }
    }

    public void moveUpPiece(Piece p, int dist, int row, int col){
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
            if(rowSpaceEmpty(newRow, col, p.getLen())){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveDownPiece(Piece p, int dist, int row, int col){
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
            if(rowSpaceEmpty(newRow, col, p.getLen())){
                removePiece(p, row, col);
                addPiece(p, newRow, col);
            }
        }
    }

    public void moveLeftPiece(Piece p, int dist, int row, int col){
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
            if(colSpaceEmpty(row, newCol, p.getLen())){
                removePiece(p, row, col);
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public void moveRightPiece(Piece p, int dist, int row, int col){
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
            if(colSpaceEmpty(row, newCol, p.getLen())){
                removePiece(p, row, col);
                addPiece(p, row, newCol);
            }
        }
    }

    public boolean isFinished(){
        return boardFinished;
    }
}