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
            char[][] boardData_0idx = null; // Array 0-indexed untuk data papan aktual
            int declaredRows = 0;
            int declaredCols = 0;
            int declaredPieceCount = 0;
            
            int exitRow_1idx = -1;      // Final 1-indexed exit row
            int exitCol_1idx = -1;      // Final 1-indexed exit col
            boolean kSymbolFound = false; // Penanda apakah 'K' sudah ditemukan

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
                if (declaredRows < 1 || declaredCols < 1) {
                    throw new IllegalArgumentException("Dimensi papan (row/col) harus bernilai >= 1.");
                }
                if (fileScanner.hasNextInt()) declaredPieceCount = fileScanner.nextInt();
                else throw new NoSuchElementException("Format file salah: deklarasi jumlah piece tidak ditemukan.");
                if (fileScanner.hasNextLine()) fileScanner.nextLine(); // Konsumsi sisa baris header

                // 2. Baca semua baris sisa ke List
                List<String> allLines = new ArrayList<>();
                while(fileScanner.hasNextLine()){
                    allLines.add(fileScanner.nextLine());
                }

                // 3. Cari 'K', tentukan jenis baris 'K' (solo atau dengan data)
                int kLineIndexInList = -1;    // Index baris 'K' di allLines
                int kCharPosOnItsLine = -1;   // Posisi karakter 'K' (0-idx) pada barisnya (setelah trim akhir)
                boolean kLineIsSoloType = false; // True jika baris K hanya berisi K dan whitespace

                for (int i = 0; i < allLines.size(); i++) {
                    String originalLine = allLines.get(i);
                    String lineTrailingTrimmed = originalLine.replaceAll("\\s+$", ""); // Trim akhir, pertahankan awal

                    int kIdx = lineTrailingTrimmed.indexOf('K');
                    if (kIdx == -1) kIdx = lineTrailingTrimmed.indexOf('k');

                    if (kIdx != -1) {
                        if (kSymbolFound) throw new Exception("Simbol 'K' ditemukan lebih dari satu kali.");
                        kSymbolFound = true;
                        kLineIndexInList = i;
                        kCharPosOnItsLine = kIdx;

                        // Cek apakah baris K adalah "solo" (hanya K dan whitespace)
                        kLineIsSoloType = true;
                        for (int charPos = 0; charPos < lineTrailingTrimmed.length(); charPos++) {
                            if (charPos == kIdx) continue; // Lewati karakter K itu sendiri
                            if (!Character.isWhitespace(lineTrailingTrimmed.charAt(charPos))) {
                                kLineIsSoloType = false; // Ada karakter non-whitespace lain selain K
                                break;
                            }
                        }
                    }
                }

                if (!kSymbolFound) throw new Exception("Simbol 'K' (pintu keluar) tidak ditemukan.");

                // 4. Ekstrak data papan dan finalisasi posisi exit
                boardData_0idx = new char[declaredRows][declaredCols];
                int actualDataRowsProcessed = 0;
                int firstBoardDataLineInList = -1; // Untuk referensi K atas/bawah

                for (int i = 0; i < allLines.size(); i++) {
                    if (i == kLineIndexInList && kLineIsSoloType) {
                        // Jika ini baris K solo, catat posisinya relatif jika belum ada data papan
                        if (firstBoardDataLineInList == -1) firstBoardDataLineInList = i + 1; // Data papan akan mulai setelah ini
                        continue; // Lewati baris K solo dari pemrosesan data papan
                    }

                    String originalLine = allLines.get(i);
                    String lineTrailingTrimmed = originalLine.replaceAll("\\s+$", "");

                    if (lineTrailingTrimmed.isEmpty()) {
                        // Jika baris K solo belum ditemukan dan baris data pertama belum ditemukan,
                        // baris kosong ini bisa jadi pemisah antara K atas dan data
                        if (!kLineIsSoloType && kLineIndexInList > i && firstBoardDataLineInList == -1) {
                             firstBoardDataLineInList = i + 1;
                        }
                        continue; // Abaikan baris yang sepenuhnya kosong
                    }
                    
                    // Jika sudah cukup baris data diproses, baris non-kosong berikutnya harusnya baris K solo (jika K di bawah)
                    if (actualDataRowsProcessed >= declaredRows) {
                        if (!(i == kLineIndexInList && kLineIsSoloType)) { // Jika bukan baris K solo yang memang diharapkan di bawah
                             throw new Exception("Ditemukan lebih dari " + declaredRows + " baris data papan yang tidak kosong.");
                        }
                        continue; // Sudah selesai ambil data, ini mungkin K bawah atau sisa file
                    }

                    // Catat indeks baris data pertama
                    if (firstBoardDataLineInList == -1) firstBoardDataLineInList = i;
                    
                    String boardRowString;

                    if (i == kLineIndexInList) { // K ada di baris ini, dan baris ini juga mengandung data papan
                        if (kLineIsSoloType) throw new Exception("Logika error: Baris K solo tidak seharusnya diproses sebagai data."); // Pengaman

                        if (kCharPosOnItsLine == 0 && lineTrailingTrimmed.length() == declaredCols + 1) { // K di kiri
                            boardRowString = lineTrailingTrimmed.substring(1);
                            exitRow_1idx = actualDataRowsProcessed + 1;
                            exitCol_1idx = 0; // Konvensi untuk keluar kiri
                        } else if (kCharPosOnItsLine == declaredCols && lineTrailingTrimmed.length() == declaredCols + 1) { // K di kanan
                            boardRowString = lineTrailingTrimmed.substring(0, declaredCols);
                            exitRow_1idx = actualDataRowsProcessed + 1;
                            exitCol_1idx = declaredCols + 1;
                        } else {
                            throw new Exception("Posisi 'K' di baris data ke-" + (actualDataRowsProcessed + 1) +
                                                " (isi: '" + lineTrailingTrimmed + "') tidak valid. 'K' harus di awal atau akhir, " +
                                                "dan sisa baris harus " + declaredCols + " karakter.");
                        }
                    } else { // Baris data murni tanpa 'K'
                        boardRowString = lineTrailingTrimmed;
                    }

                    if (boardRowString.length() != declaredCols) {
                        throw new Exception("Baris data ke-" + (actualDataRowsProcessed + 1) +
                                            " (isi: '" + boardRowString + "') memiliki panjang " + boardRowString.length() +
                                            ". Diharapkan " + declaredCols + " karakter.");
                    }

                    for (int j = 0; j < declaredCols; j++) {
                        boardData_0idx[actualDataRowsProcessed][j] = boardRowString.charAt(j);
                    }
                    actualDataRowsProcessed++;
                }

                if (actualDataRowsProcessed < declaredRows) {
                    throw new Exception("Data papan tidak cukup. Diharapkan " + declaredRows +
                                        " baris data valid, hanya ditemukan " + actualDataRowsProcessed + ".");
                }

                // Jika exit belum ditentukan (berarti K ada di baris solo atas/bawah)
                if (exitRow_1idx == -1) {
                    if (kLineIndexInList < firstBoardDataLineInList || firstBoardDataLineInList == -1) { // K di atas, atau tidak ada data board sama sekali (kasus K solo dan declaredRows = 0, sudah dicegah)
                        exitRow_1idx = 0; // Konvensi keluar atas
                    } else { // K di bawah (kLineIndexInList harusnya > index baris data terakhir)
                        exitRow_1idx = declaredRows + 1; // Konvensi keluar bawah
                    }
                    exitCol_1idx = kCharPosOnItsLine + 1; // Kolom K (1-indexed) pada barisnya (termasuk leading whitespace)
                    
                    // Validasi kolom K untuk exit atas/bawah agar sejajar dengan papan
                    if (exitCol_1idx < 1 || exitCol_1idx > declaredCols) {
                        throw new Exception("Posisi 'K' (kolom " + exitCol_1idx +
                                            ") untuk pintu keluar atas/bawah di luar rentang kolom papan [1-" + declaredCols + "].");
                    }
                }
                
                // 5. Buat objek Board
                Board board = new Board(boardData_0idx, exitRow_1idx, exitCol_1idx);
                
                // 6. Validasi pieceCounter
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
                if (fileScanner != null) {
                    fileScanner.close();
                }
            }
        }
        // return null; 
    }
}