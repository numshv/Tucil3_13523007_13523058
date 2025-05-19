package solver;

import java.util.ArrayList;
import java.util.Map;

import obj.Board;
import obj.Piece;

// Heuristic: Jumlah piece yang menghalangi primary piece ke exit selama 3 move
public class TreeHeuristic extends Heuristic {
    private final int MAX_DEPTH = 3;
    private final BaseHeuristic baseHeuristic = new BaseHeuristic();
    private final Helper helper = new Helper();
    
    @Override
    public int evaluate(Board b) {
        return evaluateMinimum(b, 0, null);
    }
    
    private int evaluateMinimum(Board board, int depth, String lastMove) {
        if(depth >= MAX_DEPTH || board.isFinished()){
            return baseHeuristic.evaluate(board);
        }
        
        int bestScore = Integer.MAX_VALUE;
        Map<Character, Piece> pieces = board.getAllPieces();
        
        for(char pieceType : pieces.keySet()){
            Piece piece = pieces.get(pieceType);
            
            helper.generateAllMoves(board, piece, lastMove);
            ArrayList<Board> possibleBoards = helper.getBoardMoves();
            
            if(possibleBoards.isEmpty()){
                continue;
            }
            
            for(Board nextBoard : possibleBoards){
                String currentMove = helper.getLastMove(board, nextBoard, piece);
                int score = evaluateMinimum(nextBoard, depth+1, currentMove);
                
                if(score < bestScore){
                    bestScore = score;
                }
                
                if(nextBoard.isFinished()){
                    return 0;
                }
            }
        }
        
        if(bestScore == Integer.MAX_VALUE){
            return baseHeuristic.evaluate(board);
        }
        
        return bestScore;
    }
}
