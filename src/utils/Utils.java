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


public class Utils {

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
                String filePath = "test/" + filename; 
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

    public void runMainApp(){
        Scanner userInput = new Scanner(System.in);
        String pickedAlgo, pickedHeuristic;
        while(true){
            try{
                System.out.print("Pilih algoritma (Masukkan angka pilihanmu): ");
                System.out.println("1. GBFS");
                System.out.println("2. A*");
                System.out.println("3. IDS");
                pickedAlgo = userInput.nextLine();
                pickedAlgo.toLowerCase();
                if(!(pickedAlgo.equals("1") || pickedAlgo.equals("2") ||pickedAlgo.equals("3"))) throw new Exception("Pilihan algoritma tidak valid, ulangi\n");
                break;
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        while(true){
            try{
                System.out.print("Pilih heuristic (Masukkan angka pilihanmu): ");
                System.out.print("1. Jumlah blok menghalangi");
                System.out.print("2. Jarak blok primer ke pintu keluar");
                pickedHeuristic = userInput.nextLine();
                pickedHeuristic.toLowerCase();
                if(!(pickedHeuristic.equals("1") || pickedHeuristic.equals("2") )) throw new Exception("Pilihan heuristik tidak valid, ulangi\n");
                break;
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        long startTime = System.nanoTime();
        if(pickedAlgo.equals("1")){
            if(pickedHeuristic.equals("1")){}//TODO: TAMBAHIN RUN ALGONYA
            else{}//TODO: TAMBAHIN RUN ALGONYA
        }
        else if(pickedAlgo.equals("2")){
            if(pickedHeuristic.equals("1")){}//TODO: TAMBAHIN RUN ALGONYA
            else{}//TODO: TAMBAHIN RUN ALGONYA
        }
        else{
            if(pickedHeuristic.equals("1")){}//TODO: TAMBAHIN RUN ALGONYA
            else{}//TODO: TAMBAHIN RUN ALGONYA
        }

        long endTime = System.nanoTime();
        long durationInMillis = (endTime - startTime) / 1_000_000;

        System.out.println("Time taken: " + durationInMillis + " ms");
        // TODO tambahin print jumlah node dikunjungi
    }

    public List<Board> generateAllPossibleMoves(Board inpBoard, Board prevTwoBoardState){
        Board initBoard = new Board(inpBoard);
        initBoard.printBoardState();
        List<Board> results = new ArrayList<Board>();
        Map<Character, Piece> pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : initBoard.getAllPieces().entrySet()) {
            pieces.put(entry.getKey(), new Piece(entry.getValue())); 
        }

        for (Piece curPiece : pieces.values()){
            System.out.println(curPiece.getPieceType());
            if(curPiece.isHorizontal()){
                // cek kiri kosong
                int emptyLeft = 0;
                for(int i = initBoard.getStartColPiece(curPiece)-1; i >= 1;i-- ) {
                    if(initBoard.getCharAt(initBoard.getStartRowPiece(curPiece), i) == '.'){
                        initBoard.moveLeftPiece(curPiece.getPieceType(), 1);
                        if(!initBoard.isEqual(prevTwoBoardState)) results.add(new Board(initBoard));
                        emptyLeft++;
                    }
                    else break;
                }


                if(emptyLeft != 0) initBoard.moveRightPiece(curPiece.getPieceType(), emptyLeft);

                // cek kanan kosong
                int emptyRight = 0;
                for(int i = initBoard.getEndColPiece(curPiece)+1; i <= initBoard.getBoardCol();i++ ) {
                    if(initBoard.getCharAt(initBoard.getEndRowPiece(curPiece), i) == '.'){
                        initBoard.moveRightPiece(curPiece.getPieceType(), 1);
                        results.add(new Board(initBoard));
                        emptyRight++;
                    }
                    else break;
                }

                if(emptyRight != 0) initBoard.moveLeftPiece(curPiece.getPieceType(), emptyRight);

            }else{
                // cek atas kosong
                int emptyTop = 0;
                for(int i = initBoard.getStartRowPiece(curPiece)-1; i >= 1;i-- ) {
                    if(initBoard.getCharAt(i, initBoard.getStartColPiece(curPiece)) == '.'){
                        initBoard.moveUpPiece(curPiece.getPieceType(), 1);
                        results.add(new Board(initBoard));
                        emptyTop++;
                    }
                    else break;
                }

                if(emptyTop != 0) initBoard.moveDownPiece(curPiece.getPieceType(), emptyTop);

                // cek kanan kosong
                int emptyBottom = 0;
                for(int i = initBoard.getEndRowPiece(curPiece)+1; i <= initBoard.getBoardRow();i++ ) {
                    if(initBoard.getCharAt(i, initBoard.getEndColPiece(curPiece)) == '.'){
                        initBoard.moveDownPiece(curPiece.getPieceType(), 1);
                        results.add(new Board(initBoard));
                        emptyBottom++;
                    }
                    else break;
                }

                if(emptyBottom != 0) initBoard.moveUpPiece(curPiece.getPieceType(), emptyBottom);
            }
        }

        return results;   
    }
}