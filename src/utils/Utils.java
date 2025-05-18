package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import obj.Board;

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
            boolean exitFound = false;

            try {
                System.out.print("Masukkan nama file input (dalam folder test, ex: tes1.txt): ");
                String filename = userInputScanner.nextLine();
                String filePath = "test/" + filename;
                File file = new File(filePath);

                if (!file.exists()) {
                    throw new FileNotFoundException("File pada path '" + filePath + "' tidak ditemukan.");
                }
                
                fileScanner = new Scanner(file);
                
                // 1. Baca dimensi papan (baris dan kolom) dan jumlah piece
                if (fileScanner.hasNextInt()) declaredRows = fileScanner.nextInt();
                else throw new NoSuchElementException("Format file salah: nilai baris (row) tidak ditemukan.");

                if (fileScanner.hasNextInt()) declaredCols = fileScanner.nextInt();
                else throw new NoSuchElementException("Format file salah: nilai kolom (col) tidak ditemukan.");

                if (declaredRows < 1 || declaredCols < 1) {
                    throw new IllegalArgumentException("Dimensi papan tidak valid: Row dan Col harus bernilai >= 1.");
                }

                if (fileScanner.hasNextInt()) {
                    declaredPieceCount = fileScanner.nextInt();
                } else {
                    throw new NoSuchElementException("Format file salah: deklarasi jumlah piece tidak ditemukan.");
                }
                
                // Konsumsi sisa baris setelah membaca integer terakhir
                if (fileScanner.hasNextLine()) {
                    fileScanner.nextLine();
                }

                // 2. Inisialisasi array boardData_0idx (0-indexed)
                boardData_0idx = new char[declaredRows][declaredCols];
                
                // 3. Baca konfigurasi papan dari file
                int actualDataRowsRead = 0;
                while (actualDataRowsRead < declaredRows) {
                    if (!fileScanner.hasNextLine()) {
                        throw new Exception("Data papan tidak lengkap. Diharapkan " + declaredRows +
                                          " baris data papan, tetapi file berakhir setelah memproses " + actualDataRowsRead + " baris data valid.");
                    }
                    
                    String line = fileScanner.nextLine();
                    String trimmedLine = line.replaceAll("^\\s+", ""); // Hapus whitespace di awal baris
                    
                    if (trimmedLine.isEmpty()) {
                        continue; // Lewati baris kosong
                    }
                    
                    // Cek apakah baris berisi karakter K di luar dimensi deklared board
                    int kIndex = trimmedLine.indexOf('K');
                    if (kIndex != -1) {
                        // Cek apakah K berada tepat di posisi declaredCols (segera setelah papan)
                        if (kIndex == declaredCols) {
                            if (exitFound) {
                                throw new Exception("Simbol 'K' (pintu keluar) ditemukan lebih dari satu kali di papan.");
                            }
                            exitFound = true;
                            exitRow_1idx = actualDataRowsRead + 1; // Convert to 1-indexed
                            exitCol_1idx = declaredCols + 1;       // Convert to 1-indexed
                            
                            // Validasi panjang string minus karakter K harus sesuai dengan declaredCols
                            if (trimmedLine.length() - 1 != declaredCols) {
                                throw new Exception("Baris data ke-" + (actualDataRowsRead + 1) + " (isi: '" + trimmedLine + "') " +
                                                  "memiliki " + (trimmedLine.length() - 1) + " karakter board sebelum K. " +
                                                  "Diharapkan " + declaredCols + " karakter.");
                            }
                            
                            // Copy semua karakter kecuali K ke dalam boardData
                            for (int j_0idx = 0; j_0idx < declaredCols; j_0idx++) {
                                boardData_0idx[actualDataRowsRead][j_0idx] = trimmedLine.charAt(j_0idx);
                            }
                        } else {
                            throw new Exception("Simbol 'K' (pintu keluar) harus berada di posisi segera setelah papan (posisi " + 
                                              declaredCols + "), bukan di posisi " + kIndex + ".");
                        }
                    } else {
                        // Jika tidak ada K, pastikan panjang baris tepat sesuai dengan declaredCols
                        if (trimmedLine.length() != declaredCols) {
                            throw new Exception("Baris data ke-" + (actualDataRowsRead + 1) + " (isi: '" + trimmedLine + "') " +
                                              "memiliki " + trimmedLine.length() + " karakter setelah trim whitespace depan. " +
                                              "Diharapkan " + declaredCols + " karakter.");
                        }
                        
                        // Copy karakter ke dalam boardData
                        for (int j_0idx = 0; j_0idx < declaredCols; j_0idx++) {
                            boardData_0idx[actualDataRowsRead][j_0idx] = trimmedLine.charAt(j_0idx);
                        }
                    }
                    
                    actualDataRowsRead++;
                }
                
                if (!exitFound) {
                    throw new Exception("Simbol 'K' (pintu keluar) tidak ditemukan dalam konfigurasi papan di file.");
                }

                Board board = new Board(boardData_0idx, exitRow_1idx, exitCol_1idx);

                if (declaredPieceCount != board.getPieceCounter()) {
                    throw new Exception("Jumlah piece tidak sesuai dengan deklarasi. Dinyatakan: " + 
                                      declaredPieceCount + ", ditemukan: " + board.getPieceCounter());
                }
                
                System.out.println("Papan berhasil dibuat dari file: " + filename);
                board.printBoardState();

                return board;

            } catch (FileNotFoundException e) {
                System.out.println("Kesalahan: " + e.getMessage());
                System.out.println("Pastikan file ada di dalam folder 'test' dan nama file benar. Silakan coba lagi.");
            } catch (NoSuchElementException | IllegalArgumentException e) {
                System.out.println("Kesalahan format atau data file: " + e.getMessage());
                System.out.println("Pastikan format file (angka untuk dimensi, dll.) dan nilainya sudah benar. Silakan coba lagi.");
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan saat memproses file: " + e.getMessage());
                System.out.println("Silakan coba lagi.");
            } finally {
                if (fileScanner != null) {
                    fileScanner.close();
                }
            }
        }
    }
}