package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import obj.Board;
import obj.Piece;
import solver.*;



public class Utils {

    String inpFileName;

    public Board inputFileHandler() {
        System.out.println("\n\nWelcome to Rush Hour Game Solver!\n\n");
        Scanner userInput = new Scanner(System.in);

        while (true) {
            Scanner fileReader = null;
            char[][] boardGrid = null;
            int rowCount = 0;
            int columnCount = 0;
            int declaredPieces = 0; 
            
            int exitRowPosition = -1;    
            int exitColumnPosition = -1;    
            boolean exitSymbolFound = false;
            int exitLineIndex = -1;  
            int exitSymbolPosition = -1; 
            boolean isExitOnSeparateLine = false; 

            try {
                System.out.print("Masukkan nama file input (dalam folder test, ex: tes1.txt): ");
                String filename = userInput.nextLine();
                String filePath = "../test/" + filename; 
                inpFileName = filePath;
                File file = new File(filePath);

                if (!file.exists()) {
                    throw new FileNotFoundException("File pada path '" + filePath + "' tidak ditemukan.");
                }
                
                fileReader = new Scanner(file);
                
                if (fileReader.hasNextInt()) rowCount = fileReader.nextInt();
                else throw new NoSuchElementException("Format file salah: nilai baris (row) tidak ditemukan.");
                
                if (fileReader.hasNextInt()) columnCount = fileReader.nextInt();
                else throw new NoSuchElementException("Format file salah: nilai kolom (col) tidak ditemukan.");
                
                if (rowCount < 1 || columnCount < 1) {
                    throw new IllegalArgumentException("Dimensi papan (row/col) harus bernilai >= 1.");
                }
                
                if (fileReader.hasNextInt()) declaredPieces = fileReader.nextInt();
                else throw new NoSuchElementException("Format file salah: deklarasi jumlah piece tidak ditemukan.");
                
                if (fileReader.hasNextLine()) fileReader.nextLine(); 

                List<String> fileLines = new ArrayList<>();
                while(fileReader.hasNextLine()){
                    fileLines.add(fileReader.nextLine());
                }

                for (int i = 0; i < fileLines.size(); i++) {
                    String currentLine = fileLines.get(i);
                    String trimmedLine = currentLine.replaceAll("\\s+$", ""); 

                    int exitPosition = trimmedLine.indexOf('K');
                    if (exitPosition == -1) exitPosition = trimmedLine.indexOf('k');

                    if (exitPosition != -1) {
                        if (exitSymbolFound) throw new Exception("Simbol 'K' ditemukan lebih dari satu kali.");
                        exitSymbolFound = true;
                        exitLineIndex = i;
                        exitSymbolPosition = exitPosition;

                        isExitOnSeparateLine = true;
                        for (int charPos = 0; charPos < trimmedLine.length(); charPos++) {
                            if (charPos == exitPosition) continue; 
                            if (!Character.isWhitespace(trimmedLine.charAt(charPos))) {
                                isExitOnSeparateLine = false; 
                                break;
                            }
                        }
                    }
                }

                if (!exitSymbolFound) throw new Exception("Simbol 'K' (pintu keluar) tidak ditemukan.");

                boardGrid = new char[rowCount][columnCount];
                int processedRows = 0;
                int firstDataLineIndex = -1; 

                for (int i = 0; i < fileLines.size(); i++) {
                    String currentLine = fileLines.get(i);
                    
                    if (i == exitLineIndex && isExitOnSeparateLine) {
                        if (firstDataLineIndex == -1) firstDataLineIndex = i + 1; 
                        continue; 
                    }

                    String processedLine = currentLine.replaceAll("\\s+$", "");

                    if (processedLine.trim().isEmpty()) {
                        if (firstDataLineIndex == -1 && exitLineIndex != -1 && exitLineIndex < i && isExitOnSeparateLine) {
                             firstDataLineIndex = i + 1;
                        }
                        continue; 
                    }
                    
                    if (processedRows >= rowCount) {
                        if (!(i == exitLineIndex && isExitOnSeparateLine)) { 
                             throw new Exception("Ditemukan lebih dari " + rowCount + " baris data papan yang tidak kosong. Baris bermasalah: '" + processedLine + "'");
                        }
                        continue;
                    }

                    if (firstDataLineIndex == -1) firstDataLineIndex = i;
                    
                    String boardRowContent;

                    if (i == exitLineIndex) {
                        if (exitSymbolPosition == 0 && processedLine.length() >= columnCount + 1) {
                            boardRowContent = processedLine.substring(1).trim();
                            exitRowPosition = processedRows + 1;
                            exitColumnPosition = 0; 
                        } else if (exitSymbolPosition >= columnCount && processedLine.length() >= columnCount + 1 && 
                                  exitSymbolPosition == processedLine.trim().indexOf('K', columnCount-1)) {
                            boardRowContent = processedLine.substring(0, exitSymbolPosition).trim();
                            exitRowPosition = processedRows + 1;
                            exitColumnPosition = columnCount + 1;
                        } else {
                            throw new Exception("Posisi 'K' pada baris data ke-" + (processedRows + 1) +
                                                " (isi: '" + processedLine + "') tidak valid. Untuk pintu samping, 'K' harus di awal atau akhir, " +
                                                "dan bagian data (setelah trim total) harus sepanjang " + columnCount + " karakter.");
                        }
                    } else {
                        boardRowContent = processedLine.trim();
                    }

                    if (boardRowContent.length() != columnCount) {
                        throw new Exception("Baris data ke-" + (processedRows + 1) +
                                            " (setelah diproses menjadi: '" + boardRowContent + "') memiliki panjang " + boardRowContent.length() +
                                            ". Diharapkan " + columnCount + " karakter.");
                    }

                    for (int j = 0; j < columnCount; j++) {
                        boardGrid[processedRows][j] = boardRowContent.charAt(j);
                    }
                    processedRows++;
                }

                if (processedRows < rowCount) {
                    throw new Exception("Data papan tidak cukup. Diharapkan " + rowCount +
                                        " baris data valid, hanya ditemukan " + processedRows + ".");
                }

                if (exitRowPosition == -1) {
                    if (firstDataLineIndex == -1 || exitLineIndex < firstDataLineIndex) { 
                        exitRowPosition = 0; 
                    } else { 
                        exitRowPosition = rowCount + 1; 
                    }
                    exitColumnPosition = exitSymbolPosition + 1; 
                    
                    if (exitColumnPosition < 1 || exitColumnPosition > columnCount) {
                        throw new Exception("Posisi 'K' (kolom " + exitColumnPosition +
                                            ") untuk pintu keluar atas/bawah di luar rentang kolom papan [1-" + columnCount + "]. Isi baris K: '" + 
                                            fileLines.get(exitLineIndex).replaceAll("\\s+$", "") + "'");
                    }
                }
                
                Board board = new Board(boardGrid, exitRowPosition, exitColumnPosition);
                
                if (declaredPieces != board.getPieceCounter()) {
                    throw new Exception("Jumlah piece tidak sesuai deklarasi. Dideklarasikan: " +
                                        declaredPieces + ", Dihitung dari papan (unik, tanpa P): " + board.getPieceCounter());
                }
                
                System.out.println("Papan berhasil dibuat dari file: " + filename + "\n");
                board.printBoardState(); 

                return board;

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage() + "\nSilakan coba lagi...\n");
            } catch (NoSuchElementException | IllegalArgumentException e) { 
                System.out.println(e.getMessage() + "\nSilakan coba lagi...\n");
            } catch (Exception e) { 
                System.out.println(e.getMessage() + "\nSilakan coba lagi...\n");
            } finally {
                if (fileReader != null) fileReader.close();
            }
        }
    }

    public void runMainApp(Board board){
        Scanner userInput = new Scanner(System.in);
        String pickedAlgo, pickedHeuristic;
        while(true){
            try{
                System.out.println("Pilih algoritma! ");
                System.out.println("1. GBFS");
                System.out.println("2. A*");
                System.out.println("3. UCS");
                System.out.println("4. IDS\n");
                System.out.print("Masukkan angka pilihan: ");
                pickedAlgo = userInput.nextLine();
                System.out.println();
                pickedAlgo.toLowerCase();
                if(!(pickedAlgo.equals("1") || pickedAlgo.equals("2") ||pickedAlgo.equals("3") ||pickedAlgo.equals("4"))) throw new Exception("Pilihan algoritma tidak valid, ulangi\n");
                break;
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        while(true){
            try{
                if(pickedAlgo.equals("1") || pickedAlgo.equals("2")){
                    System.out.println("Pilih heuristic (Masukkan angka pilihanmu): ");
                    System.out.println("1. Jumlah blok menghalangi");
                    System.out.println("2. Jarak blok primer ke pintu keluar\n");
                    System.out.print("Masukkan angka pilihan: ");
                    pickedHeuristic = userInput.nextLine();
                    System.out.println();
                    pickedHeuristic.toLowerCase();
                    if(!(pickedHeuristic.equals("1") || pickedHeuristic.equals("2") )) throw new Exception("Pilihan heuristik tidak valid, ulangi\n");
                }
                pickedHeuristic = "NaN";
                break;
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        long startTime = System.nanoTime();
        long endTime;
        if(pickedAlgo.equals("1")){
            GBFS gbfsResult = new GBFS();
            if(pickedHeuristic.equals("1")) gbfsResult.solve(board, true);
            else gbfsResult.solve(board, false);
            endTime = System.nanoTime();
            gbfsResult.printSolutionPath();
            gbfsResult.writeSolution(inpFileName);
        }
        else if(pickedAlgo.equals("2")){
            if(pickedHeuristic.equals("1")){
                AStar aStarResult = new AStar(board, true);
                endTime = System.nanoTime();
                aStarResult.printSolutionPath();
                aStarResult.writeSolution(inpFileName);
            }
            else{
                AStar aStarResult = new AStar(board, true);
                endTime = System.nanoTime();
                aStarResult.printSolutionPath();
                aStarResult.writeSolution(inpFileName);
            }
        }
        else if(pickedAlgo.equals("3")){
            UCS ucsResult = new UCS(board);
            endTime = System.nanoTime();
            ucsResult.printSolutionPath();
            ucsResult.writeSolution(inpFileName);
        }
        else{
            IDS idsResult = new IDS(board);
            endTime = System.nanoTime();
            idsResult.printSolutionPath();
            idsResult.writeSolution(inpFileName);
        }
        long durationInMillis = (endTime - startTime) / 1_000_000;

        System.out.println("Time taken: " + durationInMillis + " ms");
    }


    public List<Board> generateAllPossibleMoves(Board inpBoard) {
        if (inpBoard == null) {
            return new ArrayList<>();
        }
        
        Board initBoard = new Board(inpBoard);
        List<Board> results = new ArrayList<Board>();
        Map<Character, Piece> pieces = new HashMap<>();
        Map<Character, Piece> allPieces = initBoard.getAllPieces();
        if (allPieces == null) {
            return results;
        }
        
        for (Map.Entry<Character, Piece> entry : allPieces.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                pieces.put(entry.getKey(), new Piece(entry.getValue()));
            }
        }
        
        for (Piece curPiece : pieces.values()) {
            if (curPiece == null) continue;
            
            try {
            } catch (Exception e) {
                continue; 
            }
            
            try {
                if (curPiece.isHorizontal()) {
                    // Check left empty spaces
                    int emptyLeft = 0;
                    int startRow = initBoard.getStartRowPiece(curPiece);
                    int startCol = initBoard.getStartColPiece(curPiece);
                    
                    if (startRow <= 0 || startCol <= 0) continue;
                    
                    for (int i = startCol - 1; i >= 1; i--) {
                        if (initBoard.getCharAt(startRow, i) == '.') {
                            initBoard.moveLeftPiece(curPiece.getPieceType(), 1);
                            results.add(new Board(initBoard));
                            emptyLeft++;
                        } else break;
                    }
                    if (emptyLeft != 0) initBoard.moveRightPiece(curPiece.getPieceType(), emptyLeft);
                    
                    // Check right empty spaces
                    int emptyRight = 0;
                    int endCol = initBoard.getEndColPiece(curPiece);
                    int endRow = initBoard.getEndRowPiece(curPiece);
                    
                    if (endRow <= 0 || endCol <= 0 || endCol >= initBoard.getBoardCol()) continue;
                    
                    for (int i = endCol + 1; i <= initBoard.getBoardCol(); i++) {
                        if (initBoard.getCharAt(endRow, i) == '.') {
                            initBoard.moveRightPiece(curPiece.getPieceType(), 1);
                            results.add(new Board(initBoard));
                            emptyRight++;
                        } else break;
                    }
                    if (emptyRight != 0) initBoard.moveLeftPiece(curPiece.getPieceType(), emptyRight);
                } else {
                    // Check top empty spaces
                    int emptyTop = 0;
                    int startRow = initBoard.getStartRowPiece(curPiece);
                    int startCol = initBoard.getStartColPiece(curPiece);
                    
                    if (startRow <= 0 || startCol <= 0) continue;
                    
                    for (int i = startRow - 1; i >= 1; i--) {
                        if (initBoard.getCharAt(i, startCol) == '.') {
                            initBoard.moveUpPiece(curPiece.getPieceType(), 1);
                            results.add(new Board(initBoard));
                            emptyTop++;
                        } else break;
                    }
                    if (emptyTop != 0) initBoard.moveDownPiece(curPiece.getPieceType(), emptyTop);
                    
                    // Check bottom empty spaces
                    int emptyBottom = 0;
                    int endRow = initBoard.getEndRowPiece(curPiece);
                    int endCol = initBoard.getEndColPiece(curPiece);
                    
                    if (endRow <= 0 || endCol <= 0 || endRow >= initBoard.getBoardRow()) continue;
                    
                    for (int i = endRow + 1; i <= initBoard.getBoardRow(); i++) {
                        if (initBoard.getCharAt(i, endCol) == '.') {
                            initBoard.moveDownPiece(curPiece.getPieceType(), 1);
                            results.add(new Board(initBoard));
                            emptyBottom++;
                        } else break;
                    }
                    if (emptyBottom != 0) initBoard.moveUpPiece(curPiece.getPieceType(), emptyBottom);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return results;
    }

    public List<Board> generateAllPossibleMoves(Board inpBoard, String lastMove, int lastDist, Piece lastPiece) {
        if (inpBoard == null) {
            return new ArrayList<>();
        }
        
        List<Board> results = new ArrayList<>();

        Map<Character, Piece> allPieces = inpBoard.getAllPieces();
        if (allPieces == null) {
            return results;
        }
        
        for (Map.Entry<Character, Piece> entry : allPieces.entrySet()) {
            Character pieceKey = entry.getKey();
            Piece curPiece = entry.getValue();

            if (curPiece == null || pieceKey == null) continue;
            
            char pieceType = curPiece.getPieceType();
            Board workingBoard = new Board(inpBoard);
            
            try {
                if (curPiece.isHorizontal()) {
                    processHorizontalMoves(workingBoard, results, pieceType, lastMove, lastDist, lastPiece);
                } else {
                    processVerticalMoves(workingBoard, results, pieceType, lastMove, lastDist, lastPiece);
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return results;
    }
    
    private void processHorizontalMoves(Board workingBoard, List<Board> results, char pieceType, 
                                       String lastMove, int lastDist, Piece lastPiece) {
        Piece curPiece = workingBoard.getPiece(pieceType);
        if (curPiece == null) return;
        int startRow = workingBoard.getStartRowPiece(curPiece);
        int startCol = workingBoard.getStartColPiece(curPiece);

        if (startRow > 0 && startCol > 0) {
            Board leftBoard = new Board(workingBoard);
            int leftCount = 0;
            
            for (int i = startCol - 1; i >= 1; i--) {
                if (leftBoard.getCharAt(startRow, i) == '.') {
                    leftCount++;
                    boolean undoesLastMove = "RIGHT".equals(lastMove) && 
                                            lastDist == leftCount && 
                                            lastPiece != null && 
                                            lastPiece.getPieceType() == pieceType;
                    
                    leftBoard.moveLeftPiece(pieceType, 1);
                    
                    if (!undoesLastMove) {
                        Board newBoard = new Board(leftBoard);
                        newBoard.setLastMove("LEFT");
                        newBoard.setLastDist(leftCount);
                        newBoard.setLastPiece(curPiece);
                        results.add(newBoard);
                    }
                } else {
                    break;
                }
            }
        }
        
        Piece originalPiece = workingBoard.getPiece(pieceType);
        if (originalPiece == null) return;
        
        int endRow = workingBoard.getEndRowPiece(originalPiece);
        int endCol = workingBoard.getEndColPiece(originalPiece);
        
        if (endRow > 0 && endCol > 0 && endCol < workingBoard.getBoardCol()) {
            Board rightBoard = new Board(workingBoard);
            int rightCount = 0;
            for (int i = endCol + 1; i <= rightBoard.getBoardCol(); i++) {
                if (rightBoard.getCharAt(endRow, i) == '.') {
                    rightCount++;
                    
                    boolean undoesLastMove = "LEFT".equals(lastMove) && 
                                            lastDist == rightCount && 
                                            lastPiece != null && 
                                            lastPiece.getPieceType() == pieceType;

                    rightBoard.moveRightPiece(pieceType, 1);
                    
                    if (!undoesLastMove) {
                        Board newBoard = new Board(rightBoard);
                        newBoard.setLastMove("RIGHT");
                        newBoard.setLastDist(rightCount);
                        newBoard.setLastPiece(originalPiece);
                        results.add(newBoard);
                    }
                } else {
                    break;
                }
            }
        }
    }
    
    private void processVerticalMoves(Board workingBoard, List<Board> results, char pieceType, 
                                     String lastMove, int lastDist, Piece lastPiece) {
        Piece curPiece = workingBoard.getPiece(pieceType);
        if (curPiece == null) return;
        int startRow = workingBoard.getStartRowPiece(curPiece);
        int startCol = workingBoard.getStartColPiece(curPiece);
        
        if (startRow > 0 && startCol > 0) {
            Board upBoard = new Board(workingBoard);
            int upCount = 0;
            
            for (int i = startRow - 1; i >= 1; i--) {
                if (upBoard.getCharAt(i, startCol) == '.') {
                    upCount++;
                    
                    boolean undoesLastMove = "DOWN".equals(lastMove) && 
                                            lastDist == upCount && 
                                            lastPiece != null && 
                                            lastPiece.getPieceType() == pieceType;
                    
                    upBoard.moveUpPiece(pieceType, 1);
                    
                    if (!undoesLastMove) {
                        Board newBoard = new Board(upBoard);
                        newBoard.setLastMove("UP");
                        newBoard.setLastDist(upCount);
                        newBoard.setLastPiece(curPiece);
                        results.add(newBoard);
                    }
                } else {
                    break;
                }
            }
        }
        
        Piece originalPiece = workingBoard.getPiece(pieceType);
        if (originalPiece == null) return;
        
        int endRow = workingBoard.getEndRowPiece(originalPiece);
        int endCol = workingBoard.getEndColPiece(originalPiece);
        
        if (endRow > 0 && endCol > 0 && endRow < workingBoard.getBoardRow()) {
            Board downBoard = new Board(workingBoard);
            int downCount = 0;
            
            for (int i = endRow + 1; i <= downBoard.getBoardRow(); i++) {
                if (downBoard.getCharAt(i, endCol) == '.') {
                    downCount++;
                    
                    boolean undoesLastMove = "UP".equals(lastMove) && 
                                            lastDist == downCount && 
                                            lastPiece != null && 
                                            lastPiece.getPieceType() == pieceType;
                    
                    downBoard.moveDownPiece(pieceType, 1);
                    
                    if (!undoesLastMove) {
                        Board newBoard = new Board(downBoard);
                        newBoard.setLastMove("DOWN");
                        newBoard.setLastDist(downCount);
                        newBoard.setLastPiece(originalPiece);
                        results.add(newBoard);
                    }
                } else {
                    break;
                }
            }
        }
    }
}