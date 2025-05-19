package solver;

import obj.Board;
import obj.Piece;

// Heuristic: Jarak primary piece ke exit
public class DistHeuristic extends Heuristic {

    @Override
    public int evaluate(Board b){
        Piece primaryPiece = b.getPiece('P');

        if(primaryPiece.isHorizontal()){
            if(b.getExitCol() < b.getStartColPiece(primaryPiece)){ // exit is left
                return b.getStartColPiece(primaryPiece)-1;
            }else{ // exit is right
                return b.getExitCol()-(b.getStartColPiece(primaryPiece)+primaryPiece.getLen());
            }
        }else{
            if(b.getExitRow() < b.getStartRowPiece(primaryPiece)){ // exit is up
                return b.getStartRowPiece(primaryPiece)-1;
            }else{ // exit is down
                return b.getExitRow()-(b.getStartRowPiece(primaryPiece)+primaryPiece.getLen());
            }
        }
    }
}
