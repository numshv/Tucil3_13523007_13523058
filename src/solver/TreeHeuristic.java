package solver;

import java.util.ArrayList;
import java.util.List;
import obj.Board;
import utils.Utils;

public class TreeHeuristic extends Heuristic {
    private final int MAX_DEPTH = 3;
    private final BaseHeuristic baseHeuristic = new BaseHeuristic();
    
    @Override
    public int evaluate(Board b) {
        return evaluateMinimum(b, 0, Integer.MAX_VALUE);
    }
    
    private int evaluateMinimum(Board board, int depth, int currentBestScore) {
        if (depth >= MAX_DEPTH || board.isFinished()) {
            return baseHeuristic.evaluate(board);
        }
        
        Utils utils = new Utils();
        List<Board> boards = utils.generateAllPossibleMoves(board);
        
        if (boards.isEmpty()) {
            return baseHeuristic.evaluate(board);
        }
        
        int bestScore = currentBestScore;
        
        for (Board b : boards) {
            if (b.isFinished()) {
                return 0;
            }
            
            int score = evaluateMinimum(b, depth + 1, bestScore);
            
            if (score < bestScore) {
                bestScore = score;
            }
        }
        
        return bestScore;
    }
}