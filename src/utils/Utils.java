package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import obj.Board; // Pastikan Board constructor: Board(char[][] boardData_0idx, int exitR_1idx, int exitC_1idx) throws Exception

public class Utils {

    public Board inputFileHandler() {
        System.out.println("Welcome to Rush Hour Game Solver!");
        Scanner userInputScanner = new Scanner(System.in);

        while (true) {
            Scanner fileScanner = null;
            char[][] boardData_0idx = null;
            int declaredRows = 0;
            int declaredCols = 0;
            int declaredPieceCount = 0; 
            
            int exitRow_1idx = -1;    
            int exitCol_1idx = -1;    
            boolean kSymbolFound = false;
            int kLineIndexInList = -1;  
            int kCharPosOnItsLine = -1; // Posisi char 'K' (0-idx) pada barisnya SETELAH TRIM AKHIR (whitespace awal masih ada)
            boolean kLineIsSoloType = false; 

            try {
                System.out.print("Masukkan nama file input (dalam folder test, ex: tes1.txt): ");
                String filename = userInputScanner.nextLine();
                String filePath = "test/" + filename; 
                File file = new File(filePath);

                if (!file.exists()) {
                    throw new FileNotFoundException("File pada path '" + filePath + "' tidak ditemukan.");
                }
                
                fileScanner = new Scanner(file);
                
                // 1. Baca header
                if (fileScanner.hasNextInt()) declaredRows = fileScanner.nextInt();
                else throw new NoSuchElementException("Format file salah: nilai baris (row) tidak ditemukan.");
                if (fileScanner.hasNextInt()) declaredCols = fileScanner.nextInt();
                else throw new NoSuchElementException("Format file salah: nilai kolom (col) tidak ditemukan.");
                if (declaredRows < 1 || declaredCols < 1) { // Papan data harus punya dimensi minimal 1x1
                    throw new IllegalArgumentException("Dimensi papan (row/col) harus bernilai >= 1.");
                }
                if (fileScanner.hasNextInt()) declaredPieceCount = fileScanner.nextInt();
                else throw new NoSuchElementException("Format file salah: deklarasi jumlah piece tidak ditemukan.");
                if (fileScanner.hasNextLine()) fileScanner.nextLine(); 

                // 2. Baca semua baris sisa ke List
                List<String> allLines = new ArrayList<>();
                while(fileScanner.hasNextLine()){
                    allLines.add(fileScanner.nextLine());
                }

                // 3. Cari 'K', tentukan jenis baris 'K'
                for (int i = 0; i < allLines.size(); i++) {
                    String originalLine = allLines.get(i);
                    // Trim whitespace AKHIR saja untuk pencarian K dan penentuan kCharPosOnItsLine
                    String lineForKSearch = originalLine.replaceAll("\\s+$", ""); 

                    int kIdx = lineForKSearch.indexOf('K');
                    if (kIdx == -1) kIdx = lineForKSearch.indexOf('k');

                    if (kIdx != -1) {
                        if (kSymbolFound) throw new Exception("Simbol 'K' ditemukan lebih dari satu kali.");
                        kSymbolFound = true;
                        kLineIndexInList = i;
                        kCharPosOnItsLine = kIdx; // Posisi K pada string yang sudah di-trim akhirnya

                        kLineIsSoloType = true;
                        for (int charPos = 0; charPos < lineForKSearch.length(); charPos++) {
                            if (charPos == kIdx) continue; 
                            if (!Character.isWhitespace(lineForKSearch.charAt(charPos))) {
                                kLineIsSoloType = false; 
                                break;
                            }
                        }
                    }
                }

                if (!kSymbolFound) throw new Exception("Simbol 'K' (pintu keluar) tidak ditemukan.");

                // 4. Ekstrak data papan dan finalisasi posisi exit
                boardData_0idx = new char[declaredRows][declaredCols];
                int actualDataRowsProcessed = 0;
                int firstBoardDataLineInAllLines = -1; 

                for (int i = 0; i < allLines.size(); i++) {
                    String originalLine = allLines.get(i);
                    
                    if (i == kLineIndexInList && kLineIsSoloType) {
                        if (firstBoardDataLineInAllLines == -1) firstBoardDataLineInAllLines = i + 1; 
                        continue; 
                    }

                    // Untuk baris yang bukan K-solo, trim whitespace AKHIR untuk diproses lebih lanjut
                    String lineToProcess = originalLine.replaceAll("\\s+$", "");

                    // Abaikan baris yang menjadi kosong SETELAH trim akhir (dan bukan K-solo)
                    if (lineToProcess.trim().isEmpty()) { // .trim() di sini untuk mengecek apakah baris itu substansial kosong
                        if (firstBoardDataLineInAllLines == -1 && kLineIndexInList != -1 && kLineIndexInList < i && kLineIsSoloType) {
                             firstBoardDataLineInAllLines = i + 1;
                        }
                        continue; 
                    }
                    
                    if (actualDataRowsProcessed >= declaredRows) {
                        if (!(i == kLineIndexInList && kLineIsSoloType)) { 
                             throw new Exception("Ditemukan lebih dari " + declaredRows + " baris data papan yang tidak kosong. Baris bermasalah: '" + lineToProcess + "'");
                        }
                        continue;
                    }

                    if (firstBoardDataLineInAllLines == -1) firstBoardDataLineInAllLines = i;
                    
                    String boardRowContentCandidate; // Ini akan berisi string yang AKAN menjadi data papan

                    if (i == kLineIndexInList) { // K ada di baris ini, dan BUKAN K solo
                        // lineToProcess sudah di-trim akhirnya, masih mengandung K dan whitespace awal
                        if (kCharPosOnItsLine == 0 && lineToProcess.length() >= declaredCols + 1) { // K di kiri
                            // Ambil bagian data, lalu trim whitespace awal & akhir dari bagian data ini
                            boardRowContentCandidate = lineToProcess.substring(1).trim();
                            exitRow_1idx = actualDataRowsProcessed + 1;
                            exitCol_1idx = 0; 
                        } else if (kCharPosOnItsLine >= declaredCols && lineToProcess.length() >= declaredCols + 1 && kCharPosOnItsLine == lineToProcess.trim().indexOf('K', declaredCols-1)) { 
                            // K di kanan. Ambil bagian data, lalu trim whitespace awal & akhir
                            // kCharPosOnItsLine harus == declaredCols jika data nya pas declaredCols.
                            // Atau K bisa setelah beberapa spasi setelah data.
                            // Yang penting, substring(0, kCharPosOnItsLine) adalah kandidat data.
                            boardRowContentCandidate = lineToProcess.substring(0, kCharPosOnItsLine).trim();
                            exitRow_1idx = actualDataRowsProcessed + 1;
                            exitCol_1idx = declaredCols + 1;
                        } else {
                            throw new Exception("Posisi 'K' pada baris data ke-" + (actualDataRowsProcessed + 1) +
                                                " (isi: '" + lineToProcess + "') tidak valid. Untuk pintu samping, 'K' harus di awal atau akhir, " +
                                                "dan bagian data (setelah trim total) harus sepanjang " + declaredCols + " karakter.");
                        }
                    } else { // Baris data murni tanpa 'K'
                        // Trim whitespace awal DAN akhir untuk mendapatkan konten data murni
                        boardRowContentCandidate = lineToProcess.trim();
                    }

                    // Validasi panjang string data papan (setelah semua trim yang relevan untuk data)
                    if (boardRowContentCandidate.length() != declaredCols) {
                        throw new Exception("Baris data ke-" + (actualDataRowsProcessed + 1) +
                                            " (setelah diproses menjadi: '" + boardRowContentCandidate + "') memiliki panjang " + boardRowContentCandidate.length() +
                                            ". Diharapkan " + declaredCols + " karakter.");
                    }

                    // Isi boardData_0idx
                    for (int j = 0; j < declaredCols; j++) {
                        boardData_0idx[actualDataRowsProcessed][j] = boardRowContentCandidate.charAt(j);
                    }
                    actualDataRowsProcessed++;
                }

                if (actualDataRowsProcessed < declaredRows) {
                    throw new Exception("Data papan tidak cukup. Diharapkan " + declaredRows +
                                        " baris data valid, hanya ditemukan " + actualDataRowsProcessed + ".");
                }

                // Jika exit belum ditentukan (berarti K ada di baris solo atas/bawah)
                if (exitRow_1idx == -1) {
                    if (firstBoardDataLineInAllLines == -1 || kLineIndexInList < firstBoardDataLineInAllLines) { 
                        exitRow_1idx = 0; 
                    } else { 
                        exitRow_1idx = declaredRows + 1; 
                    }
                    // kCharPosOnItsLine adalah posisi 0-indexed 'K' pada barisnya (termasuk leading whitespace, setelah trim akhir)
                    exitCol_1idx = kCharPosOnItsLine + 1; 
                    
                    if (exitCol_1idx < 1 || exitCol_1idx > declaredCols) {
                        throw new Exception("Posisi 'K' (kolom " + exitCol_1idx +
                                            ") untuk pintu keluar atas/bawah di luar rentang kolom papan [1-" + declaredCols + "]. Isi baris K: '" + allLines.get(kLineIndexInList).replaceAll("\\s+$", "") + "'");
                    }
                }
                
                Board board = new Board(boardData_0idx, exitRow_1idx, exitCol_1idx);
                
                if (declaredPieceCount != board.getPieceCounter()) {
                    throw new Exception("Jumlah piece tidak sesuai deklarasi. Dideklarasikan: " +
                                        declaredPieceCount + ", Dihitung dari papan (unik, tanpa P): " + board.getPieceCounter());
                }
                
                System.out.println("Papan berhasil dibuat dari file: " + filename);
                board.printBoardState(); 

                return board;

            } catch (FileNotFoundException e) {
                System.out.println("Kesalahan: " + e.getMessage() + "\nSilakan coba lagi.");
            } catch (NoSuchElementException | IllegalArgumentException e) { 
                System.out.println("Kesalahan format atau data file: " + e.getMessage() + "\nSilakan coba lagi.");
            } catch (Exception e) { 
                System.out.println("Terjadi kesalahan saat memproses file: " + e.getMessage() + "\nSilakan coba lagi.");
                // e.printStackTrace(); 
            } finally {
                if (fileScanner != null) fileScanner.close();
            }
        }
    }
}