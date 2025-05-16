public class Piece {
    private char piece;
    private int len; // piece length
    private boolean isHorizontal; // true if horizontal, false if vertical
    private boolean isPrimary;

    public Piece(char piece, int len, boolean isHorizontal){
        this.piece = piece;
        this.len = len;
        this.isHorizontal = isHorizontal;

        if(this.piece == 'P') isPrimary = true;
        else isPrimary = false;
    }

    public int getLen(){
        return len;
    }

    public boolean checkHorizontal(){
        return isHorizontal;
    }

    public boolean checkPrimary(){
        return isPrimary;
    }

    public void printPiece(){
        if(isHorizontal){
            for(int i=0; i<len; i++){
                System.out.print(piece);
            }
        }else{
            for(int i=0; i<len; i++){
                System.out.println(piece);
            }
        }
    }

    public char getPieceType(){
        return piece;
    }
}
