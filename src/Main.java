import utils.*;

import solver.*;

import java.util.ArrayList;
import java.util.List;

import obj.*;

public class Main {
    public static void main(String[] args) {
        Utils utils = new Utils();
        Board board = utils.inputFileHandler();
        //System.out.println(board.getPiece('I').getLen());
        // Scanner scanner = new Scanner(System.in);
        // System.out.print("Algoritma pathfinding (UCS / GBFS / A*): ");
        // String algoritma = scanner.nextLin``e();

        // List<Board> boards = new ArrayList<Board>();
        // boards = utils.generateAllPossibleMoves(board);
        // int count = 1;
        // for (Board b: boards){
        //     System.out.println(count);
        //     count++;
        //     b.printBoardState();
        // }

        // TreeHeuristic th = new TreeHeuristic();
        // System.out.println(th.evaluate(board));

        TreeHeuristic th = new TreeHeuristic();
        System.out.println(th.evaluate(board));

        // BaseHeuristic bh = new BaseHeuristic();
        // System.out.println(bh.evaluate(board));
    }
}