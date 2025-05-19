package solver;

import java.util.ArrayList;
import java.util.List;
// import java.util.Map;

import obj.Board;
// import obj.Piece;
import utils.Utils;

// Heuristic: Jumlah piece yang menghalangi primary piece ke exit selama 3 move
public class TreeHeuristic extends Heuristic {
    private final int MAX_DEPTH = 3;
    private final BaseHeuristic baseHeuristic = new BaseHeuristic();
    //private final Helper helper = new Helper();
    private int bestScore = Integer.MAX_VALUE;
    
    @Override
    public int evaluate(Board b) {
        return evaluateMinimum(b, 0);
    }
    
    private int evaluateMinimum(Board board, int depth) {
        if(depth >= MAX_DEPTH || board.isFinished()){
            return baseHeuristic.evaluate(board);
        }
        
        Utils utils = new Utils();
        List<Board> boards = new ArrayList<Board>();
        boards = utils.generateAllPossibleMoves(board);
        
        int score;
        for(Board b: boards){
            if(b.isFinished()){
                return 0;
            }
            score = evaluateMinimum(b, depth+1);
            if(score < bestScore){
                bestScore = score;
            }
        }

        if(bestScore == Integer.MAX_VALUE){
            return baseHeuristic.evaluate(board);
        }
        
        return bestScore;
    }
}