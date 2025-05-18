import utils.*;

import java.util.Scanner;

import obj.*;

public class Main {
    public static void main(String[] args) {
        Utils utils = new Utils();
        Board board = utils.inputFileHandler();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Algoritma pathfinding (UCS / GBFS / A*): ");
        String algoritma = scanner.nextLine();
        
    }
}