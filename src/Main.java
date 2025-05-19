import utils.*;

import solver.*;

import java.util.ArrayList;
import java.util.List;

import obj.*;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Utils utils = new Utils();
        Board board = utils.inputFileHandler();
        //System.out.println(board.getPiece('I').getLen());
        // System.out.println(board.getPiece('I').getLen());
        // Scanner scanner = new Scanner(System.in);
        // System.out.print("Algoritma pathfinding (UCS / GBFS / A*): ");
        // String algoritma = scanner.nextLin``e();


        // Piece p = new Piece(board.getPiece('I'));
        // System.out.println("I.row: " + board.getStartRowPiece(p) + " - " + board.getEndRowPiece(p) + "I.col: " + board.getStartColPiece(p) + " - " + board.getEndColPiece(p));
        // board.moveLeftPiece(p, 1);
        // board.printBoardState();
        // System.out.println("I.row: " + board.getStartRowPiece(p) + " - " + board.getEndRowPiece(p) + "I.col: " + board.getStartColPiece(p) + " - " + board.getEndColPiece(p));

        List<Board> boards = new ArrayList<Board>();
        Board emptyBoard = new Board(board.getBoardRow(),board.getBoardCol(), board.getExitRow(), board.getExitCol());
        boards = utils.generateAllPossibleMoves(board, emptyBoard);

        System.out.println("starts\n\n");
        for(Board b : boards){
            b.printBoardState();
        }

        // IDS idsResult = new IDS(board);
        // idsResult.printSolutionPath();
        
    }
}