<div align="center">
<h1>Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding</h1>
<h2>(Tugas Kecil 3 IF2211 Strategi Algoritma)</h2>
</div>

## Table of Contents
- [About](#about)
- [Get Started](#get-started)
- [How To Use It](#how-to-use-it)
- [Author](#author)


## About
IQ Puzzler Pro adalah permainan puzzle di mana pemain harus mengisi papan dengan balok-balok beragam bentuk hingga tidak ada ruang kosong tersisa. Dalam permainan nyata, pemain mendapatkan satu set balok dengan bentuk unik dan harus menyusunnya di papan sesuai aturan. Beberapa versi permainan juga memungkinkan rotasi dan pencerminan balok untuk menemukan solusi yang tepat.

<div align="center">
  
![IQ Puzzler Pro](https://i.imgur.com/dPzRfMA.png)

(Sumber:  https://www.thinkfun.com/en-US/products/educational-games/rush-hour-76582)

</div>

Program ini meniru konsep permainan tersebut dengan menerima input berupa susunan papan yang ingin diselesaikan.

Program akan memberikan beberapa seleksi algoritma path-finding menemukan satu solusi yang memenuhi aturan permainan. Jika solusi ditemukan, program akan menampilkan langkah-langkah penyelesaian. Jika tidak ada solusi yang mungkin, program akan memberi tahu pengguna bahwa tidak ada solusi dari susunan papan tersebut.

## Get Started
- Pastikan Java Development Kit (JDK) telah terinstal. Jika belum, anda dapat menginstall JDK pada link berikut
  https://www.oracle.com/id/java/technologies/downloads/

- Download file .zip pada release terbaru atau clone repository ini secara keseluruhan

## How To Use It

### 1. Menjalankan Program
- Buka CLI yang tersedia pada perangkat anda (command prompt, shell, etc.)
- Arahkan ke direktori utama folder project
- Jalankan file JAR dengan perintah:
  ```bash
  java -jar app.jar
  ```

### 2. Input File
Program membutuhkan file input dengan ekstensi `.txt` yang harus ditempatkan dalam folder `test`. 

#### Format Input
Program menerima input dengan format berikut:

```
A B
N
konfigurasi_papan
```

Keterangan:
- `A`: Jumlah baris papan
- `B`: Jumlah kolom papan
- `N`: Jumlah piece
- Berikut adalah karakter-karakter yang valid dan representasinya:
  - Karakter 'A - Z' kecuali 'P' dan 'K': Pieces
  - Karakter '.': Space kosong
  - Karakter 'P': Piece utama (primary)
  - Karakter 'K': Jalan keluar
 
Berikut contoh susunan papan yang valid:
```
6 6
12
AAB..F
..BCDF
GPPCDFK
GH.III
GHJ...
LLJMM.
```

### 3. Output dan Penyimpanan Hasil
- Jika program menemukan solusi, akan ditanyakan apakah hasil ingin disimpan ke file `.txt`
- Jika memilih untuk menyimpan:
  - Program akan meminta input nama file
  - Tidak perlu menambahkan ekstensi `.txt` pada nama file
  - File hasil akan tersimpan dalam folder `Solutions` yang berada di dalam folder `test`

## Author
Noumisyifa Nabila Nareswari - 13523058 - K01
Ranashahira Reztaputri - 13523007 - K01
Teknik Informatika ITB 
