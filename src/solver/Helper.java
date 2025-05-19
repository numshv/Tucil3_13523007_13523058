package solver;

import java.util.ArrayList;

import obj.Board;
import obj.Piece;

public class Helper {
    private ArrayList<Board> boardMoves;
    
    public static final String MOVE_UP = "UP";
    public static final String MOVE_DOWN = "DOWN";
    public static final String MOVE_LEFT = "LEFT";
    public static final String MOVE_RIGHT = "RIGHT";
    
    public Helper() {
        boardMoves = new ArrayList<>();
    }
    
    public ArrayList<Board> getBoardMoves() {
        return boardMoves;
    }
    
    public void generateAllMoves(Board board, Piece p, String lastMove) {
        boardMoves = new ArrayList<>();
        char pieceType = p.getPieceType();
        
        if(p.isHorizontal()){
            // Move left and right
            generateHorizontalMoves(board, p, pieceType, lastMove);
        }else{
            // Move up and down
            generateVerticalMoves(board, p, pieceType, lastMove);
        }
    }
    
    private void generateHorizontalMoves(Board board, Piece p, char pieceType, String lastMove) {
        int maxDist = board.getBoardCol();
        
        // Move left
        if(lastMove == null || !lastMove.equals(MOVE_RIGHT)){
            for(int dist = 1; dist <= maxDist; dist++){
                Board newBoard = cloneBoard(board);
                if(newBoard == null) continue;
                
                newBoard.moveLeftPiece(pieceType, dist);
                
                if(!isBoardSame(board, newBoard)){
                    boardMoves.add(newBoard);
                    if(newBoard.isFinished()){
                        break;
                    }
                }else{
                    break;
                }
            }
        }
        
        // Move right
        if(lastMove == null || !lastMove.equals(MOVE_LEFT)){
            for(int dist=1; dist<=maxDist; dist++){
                Board newBoard = cloneBoard(board);
                if(newBoard == null) continue; 
                
                newBoard.moveRightPiece(pieceType, dist);
                
                if(!isBoardSame(board, newBoard)){
                    boardMoves.add(newBoard);
                    if(newBoard.isFinished()){
                        break;
                    }
                }else{
                    break;
                }
            }
        }
    }
    
    private void generateVerticalMoves(Board board, Piece p, char pieceType, String lastMove) {
        int maxDist = board.getBoardRow();
        
        // Move up
        if(lastMove == null || !lastMove.equals(MOVE_DOWN)){
            for(int dist=1; dist<=maxDist; dist++){
                Board newBoard = cloneBoard(board);
                if(newBoard == null) continue; 
                
                newBoard.moveUpPiece(pieceType, dist);
                
                if(!isBoardSame(board, newBoard)){
                    boardMoves.add(newBoard);
                    if (newBoard.isFinished()) {
                        break;
                    }
                }else{
                    break;
                }
            }
        }
        
        // Move down
        if(lastMove == null || !lastMove.equals(MOVE_UP)){
            for(int dist = 1; dist <= maxDist; dist++){
                Board newBoard = cloneBoard(board);
                if(newBoard == null) continue;
                
                newBoard.moveDownPiece(pieceType, dist);
                
                if(!isBoardSame(board, newBoard)){
                    boardMoves.add(newBoard);
                    if (newBoard.isFinished()) {
                        break;
                    }
                }else{
                    break;
                }
            }
        }
    }
    
    private boolean isBoardSame(Board board1, Board board2) {
        if(board1 == null || board2 == null) return true;
        
        char[][] state1 = board1.getBoardState();
        char[][] state2 = board2.getBoardState();
        
        int rows = board1.getBoardRow();
        int cols = board1.getBoardCol();
        
        for(int i = 1; i <= rows; i++){
            for(int j = 1; j <= cols; j++){
                if (state1[i][j] != state2[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private Board cloneBoard(Board b) {
        try {
            char[][] bState = b.getBoardState();
            int rows = b.getBoardRow();
            int cols = b.getBoardCol();
            
            char[][] boardLayout = new char[rows][cols];
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    boardLayout[i][j] = bState[i+1][j+1];
                }
            }
            
            Board newBoard = new Board(boardLayout, b.getExitRow(), b.getExitCol());
            
            return newBoard;
        } catch (Exception e) {
            System.err.println("Error cloning board: " + e.getMessage());
            return null;
        }
    }
    
    public String getLastMove(Board previousBoard, Board currentBoard, Piece p) {
        int prevRow = previousBoard.getStartRowPiece(p);
        int prevCol = previousBoard.getStartColPiece(p);
        int currRow = currentBoard.getStartRowPiece(p);
        int currCol = currentBoard.getStartColPiece(p);
        
        if(p.isHorizontal()){
            if(prevCol < currCol){
                return MOVE_RIGHT;
            }else if(prevCol > currCol){
                return MOVE_LEFT;
            }
        }else{
            if(prevRow < currRow){
                return MOVE_DOWN;
            }else if (prevRow > currRow){
                return MOVE_UP;
            }
        }
        
        return null;
    }
}