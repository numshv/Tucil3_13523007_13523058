package solver;

import java.util.HashMap;

import obj.Board;
import obj.Piece;

// Heuristic: Jumlah piece yang menghalangi primary piece ke exit
public class BaseHeuristic extends Heuristic {   
    
    @Override
    public int evaluate(Board b){
        HashMap<Character, Boolean> evaluatedPiece = new HashMap<>();
        
        int count = 0;
        Piece primaryPiece = b.getPiece('P');
        if(primaryPiece.checkHorizontal()){
            if(b.getExitCol() < b.getStartColPiece(primaryPiece)){ // exit is left
                for(int j=1; j<b.getStartColPiece(primaryPiece); j++){
                    if(!evaluatedPiece.get(b.getBoardState()[b.getExitRow()][j])){
                        count++;
                        evaluatedPiece.put(b.getBoardState()[b.getExitRow()][j], true);
                    }
                }
            }else{ // exit is right
                for(int j=b.getStartColPiece(primaryPiece)+primaryPiece.getLen(); j<b.getExitCol(); j++){
                    if(!evaluatedPiece.get(b.getBoardState()[b.getExitRow()][j])){
                        count++;
                        evaluatedPiece.put(b.getBoardState()[b.getExitRow()][j], true);
                    }
                }
            }
        }else{
            if(b.getExitRow() < b.getStartRowPiece(primaryPiece)){ // exit is up
                for(int i=1; i<b.getStartRowPiece(primaryPiece); i++){
                    if(!evaluatedPiece.get(b.getBoardState()[i][b.getExitCol()])){
                        count++;
                        evaluatedPiece.put(b.getBoardState()[i][b.getExitCol()], true);
                    }
                }
            }else{ // exit is down
                for(int i=b.getStartRowPiece(primaryPiece)+primaryPiece.getLen(); i<b.getExitRow(); i++){
                    if(!evaluatedPiece.get(b.getBoardState()[i][b.getExitCol()])){
                        count++;
                        evaluatedPiece.put(b.getBoardState()[i][b.getExitCol()], true);
                    }
                }
            }
        }
        return count;
    }
}
