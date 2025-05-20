package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException; // Ditambahkan
import java.io.IOException;
import java.util.List;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException; // Ditambahkan
import java.util.Scanner; // Ditambahkan
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Asumsikan kelas-kelas solver dan Board ada di paket yang sesuai atau default
// Jika ada di paket 'obj', Anda mungkin perlu import seperti:
import obj.Board;
import solver.*;
// import obj.Solution; // Jika AStar, UCS, IDS mengembalikan objek Solution



public class RushHourSolverGUI extends JFrame {
    private JComboBox<String> algorithmSelector;
    private JComboBox<String> heuristicSelector;
    private JTextField fileInput;
    private JLabel errorLabel;
    private JPanel boardPanel;
    private JLabel nodesLabel;
    private JLabel timeLabel;
    private JLabel stepsLabel;
    private BoardAnimationPanel animationPanel;
    private JButton solveButton;
    private final Color PINK_COLOR = new Color(255, 105, 180);
    private final Color ERROR_COLOR = Color.RED;
    private final Color ORANGE_COLOR = new Color(255, 102, 0);
    private final Color BLUE_COLOR = new Color(66, 135, 245);
    private final Color GREEN_COLOR = new Color(122, 229, 82);

    public RushHourSolverGUI() {
        setTitle("Rush Hour Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        initializeComponents();
        layoutComponents();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        algorithmSelector = new JComboBox<>(new String[]{"Pilih Algoritma ...", "GBFS", "A*", "UCS", "IDS"});
        heuristicSelector = new JComboBox<>(new String[]{"Pilih Heuristik ...", "Jumlah blok menghalangi", "Jarak blok primer ke pintu keluar"});
        fileInput = new JTextField("test/tes1.txt"); // Default file path for easier testing
        errorLabel = new JLabel("Error: Input tidak valid");
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setVisible(false);
        
        boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout());
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        animationPanel = new BoardAnimationPanel();
        boardPanel.add(animationPanel, BorderLayout.CENTER);
        
        nodesLabel = createStatsLabel("0", "Explored Nodes", ORANGE_COLOR);
        timeLabel = createStatsLabel("0", "Execution Time (ms)", BLUE_COLOR);
        stepsLabel = createStatsLabel("0", "Solution Steps", GREEN_COLOR);
        
        solveButton = new JButton("Solve");
        solveButton.setBackground(new Color(255, 236, 66));
        solveButton.setForeground(Color.BLACK);
        solveButton.setFocusPainted(false);
        solveButton.setBorderPainted(false);
        solveButton.setFont(new Font("Arial", Font.BOLD, 16));
        
        solveButton.addActionListener(e -> {
            if (validateInputs()) {
                solvePuzzle();
            } else {
                // errorLabel sudah di-set oleh validateInputs()
                errorLabel.setVisible(true);
            }
        });
        
        fileInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (fileInput.getText().equals("Masukkan file . . .")) {
                    fileInput.setText("");
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (fileInput.getText().isEmpty()) {
                    fileInput.setText("Masukkan file . . .");
                }
            }
        });

        algorithmSelector.addActionListener(e -> {
            String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
            if (selectedAlgorithm.equals("GBFS") || selectedAlgorithm.equals("A*")) {
                heuristicSelector.setEnabled(true);
            } else {
                heuristicSelector.setEnabled(false);
                heuristicSelector.setSelectedIndex(0);
            }
        });
        heuristicSelector.setEnabled(false); // Awalnya nonaktif
    }
    
    private JLabel createStatsLabel(String value, String description, Color valueColor) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                "<span style='font-size: 48px; color: " + colorToHex(valueColor) + ";'>" + value + "</span><br>" +
                "<span style='font-size: 20px;'>" + description + "</span></div></html>", 
                SwingConstants.CENTER);
        return label;
    }
    
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleStart = new JLabel("Rush Hour Solver", SwingConstants.LEFT);
        titleStart.setFont(new Font("Arial", Font.BOLD, 28));
        titleStart.setForeground(PINK_COLOR);
        
        JLabel titleEnd = new JLabel("Yang Diselesainnya Also Sangat Rushed (kenapa seminggu doang astaga)");
        titleEnd.setFont(new Font("Arial", Font.BOLD, 28));
        
        titlePanel.add(titleStart, BorderLayout.WEST);
        titlePanel.add(titleEnd, BorderLayout.CENTER);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlsPanel.add(algorithmSelector);
        controlsPanel.add(heuristicSelector);
        controlsPanel.add(fileInput);
        controlsPanel.add(solveButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(errorLabel, BorderLayout.NORTH);
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.add(nodesLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(stepsLabel);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 10));
        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.EAST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel(new BorderLayout());
        JLabel authorLabel = new JLabel("Proudly made by Queen Rana and her Slave Noumi", SwingConstants.LEFT);
        JLabel idLabel = new JLabel("13523007 | 13523058 | IF'23", SwingConstants.RIGHT);
        footerPanel.add(authorLabel, BorderLayout.WEST);
        footerPanel.add(idLabel, BorderLayout.EAST);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        algorithmSelector.setPreferredSize(new Dimension(200, 40));
        heuristicSelector.setPreferredSize(new Dimension(250, 40)); 
        fileInput.setPreferredSize(new Dimension(350, 40)); 
        solveButton.setPreferredSize(new Dimension(80, 40));
        statsPanel.setPreferredSize(new Dimension(300, 500));
        
        setContentPane(mainPanel);
    }
    
    private boolean validateInputs() {
        if (algorithmSelector.getSelectedIndex() == 0) {
            errorLabel.setText("Error: Algoritma belum dipilih.");
            return false;
        }
        
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        if ((selectedAlgorithm.equals("GBFS") || selectedAlgorithm.equals("A*")) 
                && heuristicSelector.getSelectedIndex() == 0) {
            errorLabel.setText("Error: Heuristik belum dipilih untuk algoritma " + selectedAlgorithm + ".");
            return false;
        }
        
        String filePath = fileInput.getText();
        if (filePath.equals("Masukkan file . . .") || filePath.isEmpty()) {
            errorLabel.setText("Error: Path file belum dimasukkan.");
            return false;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            errorLabel.setText("Error: File tidak ditemukan atau bukan file valid di path: " + filePath);
            return false;
        }
        
        return true;
    }

    private Board readBoardFromFile(String filePath) throws IOException {
        Scanner fileReader = null;
        char[][] boardGrid;
        int rowCount = 0;
        int columnCount = 0;
        int declaredPieces = 0;
        
        int exitRowPosition = -1;
        int exitColumnPosition = -1;
        boolean exitSymbolFound = false;
        int exitLineIndex = -1;
        int exitSymbolPositionInLine = -1; // Renamed for clarity
        boolean isExitOnSeparateLine = false;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("File pada path '" + filePath + "' tidak ditemukan.");
            }
            
            fileReader = new Scanner(file);
            
            if (fileReader.hasNextInt()) rowCount = fileReader.nextInt();
            else throw new IOException("Format file salah: nilai baris (row) tidak ditemukan.");
            
            if (fileReader.hasNextInt()) columnCount = fileReader.nextInt();
            else throw new IOException("Format file salah: nilai kolom (col) tidak ditemukan.");
            
            if (rowCount < 1 || columnCount < 1) {
                throw new IOException("Dimensi papan (row/col) harus bernilai >= 1.");
            }
            
            if (fileReader.hasNextInt()) declaredPieces = fileReader.nextInt();
            else throw new IOException("Format file salah: deklarasi jumlah piece tidak ditemukan.");
            
            if (fileReader.hasNextLine()) fileReader.nextLine(); // Consume the rest of the line after numbers

            List<String> fileLines = new ArrayList<>();
            while(fileReader.hasNextLine()){
                fileLines.add(fileReader.nextLine());
            }

            for (int i = 0; i < fileLines.size(); i++) {
                String currentLine = fileLines.get(i);
                String trimmedLine = currentLine.replaceAll("\\s+$", ""); 

                int kPos = trimmedLine.indexOf('K');
                if (kPos == -1) kPos = trimmedLine.indexOf('k');

                if (kPos != -1) {
                    if (exitSymbolFound) throw new IOException("Simbol 'K' ditemukan lebih dari satu kali.");
                    exitSymbolFound = true;
                    exitLineIndex = i;
                    exitSymbolPositionInLine = kPos;

                    isExitOnSeparateLine = true;
                    for (int charPos = 0; charPos < trimmedLine.length(); charPos++) {
                        if (charPos == exitSymbolPositionInLine) continue; 
                        if (!Character.isWhitespace(trimmedLine.charAt(charPos))) {
                            isExitOnSeparateLine = false; 
                            break;
                        }
                    }
                }
            }

            if (!exitSymbolFound) throw new IOException("Simbol 'K' (pintu keluar) tidak ditemukan.");

            boardGrid = new char[rowCount][columnCount];
            int processedRows = 0;
            int firstDataLineIndex = -1; 

            for (int i = 0; i < fileLines.size(); i++) {
                String currentLine = fileLines.get(i);
                
                if (i == exitLineIndex && isExitOnSeparateLine) {
                    if (firstDataLineIndex == -1) firstDataLineIndex = i + 1; 
                    continue; 
                }

                String processedLineContent = currentLine.replaceAll("\\s+$", "");

                if (processedLineContent.trim().isEmpty()) {
                    if (firstDataLineIndex == -1 && exitLineIndex != -1 && exitLineIndex < i && isExitOnSeparateLine) {
                         firstDataLineIndex = i + 1;
                    }
                    continue; 
                }
                
                if (processedRows >= rowCount) {
                    if (!(i == exitLineIndex && isExitOnSeparateLine)) { 
                         throw new IOException("Ditemukan lebih dari " + rowCount + " baris data papan yang tidak kosong. Baris bermasalah: '" + processedLineContent + "'");
                    }
                    continue;
                }

                if (firstDataLineIndex == -1) firstDataLineIndex = i;
                
                String boardRowData;

                if (i == exitLineIndex) { // K is on this data line
                    // Ensure K is at the very start or very end of the logical board content
                    String lineWithoutK;
                    if (exitSymbolPositionInLine == 0 && processedLineContent.length() >= 1) { // K at start
                        lineWithoutK = processedLineContent.substring(1);
                        exitRowPosition = processedRows + 1; // 1-based
                        exitColumnPosition = 0; // 0 means left of column 1
                    } else if (exitSymbolPositionInLine > 0 && exitSymbolPositionInLine == processedLineContent.length() - 1) { // K at end
                        lineWithoutK = processedLineContent.substring(0, exitSymbolPositionInLine);
                         exitRowPosition = processedRows + 1; // 1-based
                        exitColumnPosition = columnCount + 1; // means right of last column
                    } else {
                        throw new IOException("Posisi 'K' pada baris data ke-" + (processedRows + 1) +
                                          " (isi: '" + processedLineContent + "') tidak valid. 'K' harus di awal atau akhir baris data.");
                    }
                    boardRowData = lineWithoutK.trim(); // Trim after removing K
                } else {
                    boardRowData = processedLineContent.trim();
                }

                if (boardRowData.length() != columnCount) {
                    throw new IOException("Baris data ke-" + (processedRows + 1) +
                                        " (setelah diproses menjadi: '" + boardRowData + "') memiliki panjang " + boardRowData.length() +
                                        ". Diharapkan " + columnCount + " karakter.");
                }

                for (int j = 0; j < columnCount; j++) {
                    boardGrid[processedRows][j] = boardRowData.charAt(j);
                }
                processedRows++;
            }

            if (processedRows < rowCount) {
                throw new IOException("Data papan tidak cukup. Diharapkan " + rowCount +
                                    " baris data valid, hanya ditemukan " + processedRows + ".");
            }

            if (exitRowPosition == -1) { // K was on a separate line
                if (firstDataLineIndex == -1 || exitLineIndex < firstDataLineIndex) { 
                    exitRowPosition = 0; // 0 means above row 1
                } else { 
                    exitRowPosition = rowCount + 1; // means below last row
                }
                exitColumnPosition = exitSymbolPositionInLine + 1; // 1-based column for K
                
                if (exitColumnPosition < 1 || exitColumnPosition > columnCount) {
                    throw new IOException("Posisi 'K' (kolom " + exitColumnPosition +
                                        ") untuk pintu keluar atas/bawah di luar rentang kolom papan [1-" + columnCount + "]. Isi baris K: '" + 
                                        fileLines.get(exitLineIndex).replaceAll("\\s+$", "") + "'");
                }
            }
            
            Board board = new Board(boardGrid, exitRowPosition, exitColumnPosition);
            
            // Asumsi obj.Board memiliki metode getPieceCounter() yang mengembalikan jumlah piece unik selain 'P' dan '.'
            // Jika tidak, Anda perlu menyesuaikan atau menghapus validasi ini.
            if (board.getPieceCounter() != -1 && declaredPieces != board.getPieceCounter()) { // Check if getPieceCounter is implemented
                 throw new IOException("Jumlah piece tidak sesuai deklarasi. Dideklarasikan: " +
                                     declaredPieces + ", Dihitung dari papan (unik, tanpa P): " + board.getPieceCounter());
            }
            
            System.out.println("Papan berhasil dibuat dari file: " + filePath);
            // board.printBoardState(); // Mungkin tidak diperlukan di GUI

            return board;

        } catch (FileNotFoundException e) {
            throw new IOException("File tidak ditemukan: " + filePath + ". Detail: " + e.getMessage(), e);
        } catch (NoSuchElementException | IllegalArgumentException e) { 
            throw new IOException("Format file tidak valid atau data tidak lengkap. Detail: " + e.getMessage(), e);
        } catch (IOException e) { // Menangkap IOException yang sudah dilempar atau yang baru
            throw e; // Melempar ulang IOException yang sudah spesifik
        } catch (Exception e) { // Menangkap exception lain yang mungkin tidak terduga
            throw new IOException("Terjadi error saat memproses file: " + e.getMessage(), e);
        } finally {
            if (fileReader != null) fileReader.close();
        }
    }
    
    private void solvePuzzle() {
        errorLabel.setVisible(false);
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        String selectedHeuristicName = (String) heuristicSelector.getSelectedItem();
        String filePath = fileInput.getText();

        Board initialBoard;
        try {
            initialBoard = readBoardFromFile(filePath);
            if (initialBoard == null) { 
                 errorLabel.setText("Error: Gagal memuat board dari file (null).");
                 errorLabel.setVisible(true);
                 return;
            }
        } catch (IOException e) {
            errorLabel.setText("Error saat membaca file: " + e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace(); // Untuk debugging di console
            return;
        }

        List<Board> solutionPath = new ArrayList<>();
        long startTime = 0, endTime = 0;
        int nodesExplored = 0;

        boolean useHeuristic1 = selectedHeuristicName.equals("Jumlah blok menghalangi");

        startTime = System.nanoTime();

        try {
            switch (selectedAlgorithm) {
                case "GBFS":
                    GBFS gbfsSolver = new GBFS();
                    gbfsSolver.solve(initialBoard, useHeuristic1);
                    solutionPath = gbfsSolver.getSolutionPath();
                    nodesExplored = gbfsSolver.getNodeCount();
                    gbfsSolver.writeSolution(filePath + "_gbfs_solution.txt");
                    break;
                case "A*":
                    AStar aStarSolver = new AStar(initialBoard, useHeuristic1);
                    if (aStarSolver.getSolution() != null) {
                        solutionPath = aStarSolver.getSolution().getPathOfBoards();
                    }
                    nodesExplored = aStarSolver.getExploredNodesCount();
                    aStarSolver.writeSolution(filePath + "_astar_solution.txt");
                    break;
                case "UCS":
                    UCS ucsSolver = new UCS(initialBoard);
                    if (ucsSolver.getSolution() != null) {
                        solutionPath = ucsSolver.getSolution().getPathOfBoards();
                    }
                    nodesExplored = ucsSolver.getExploredNodesCount();
                    ucsSolver.writeSolution(filePath + "_ucs_solution.txt");
                    break;
                case "IDS":
                    IDS idsSolver = new IDS(initialBoard);
                    if (idsSolver.getSolution() != null) {
                        solutionPath = idsSolver.getSolution().getPathOfBoards();
                    }
                    nodesExplored = idsSolver.getExploredNodesCount();
                    idsSolver.writeSolution(filePath + "_ids_solution.txt");
                    break;
                default:
                    errorLabel.setText("Error: Algoritma tidak dikenal.");
                    errorLabel.setVisible(true);
                    return;
            }
        } catch (Exception ex) {
            errorLabel.setText("Error selama proses solving: " + ex.getMessage());
            errorLabel.setVisible(true);
            ex.printStackTrace(); 
            return;
        }


        endTime = System.nanoTime();
        long durationInMillis = (endTime - startTime) / 1_000_000;

        if (solutionPath == null || solutionPath.isEmpty()) {
             errorLabel.setText("Solusi tidak ditemukan oleh algoritma " + selectedAlgorithm + ".");
             errorLabel.setVisible(true);
             nodesLabel.setText(createStatsLabelHTML(String.valueOf(nodesExplored), "Explored Nodes", ORANGE_COLOR));
             timeLabel.setText(createStatsLabelHTML(String.valueOf(durationInMillis), "Execution Time (ms)", BLUE_COLOR));
             stepsLabel.setText(createStatsLabelHTML("0", "Solution Steps", GREEN_COLOR));
        } else {
            nodesLabel.setText(createStatsLabelHTML(String.valueOf(nodesExplored), "Explored Nodes", ORANGE_COLOR));
            timeLabel.setText(createStatsLabelHTML(String.valueOf(durationInMillis), "Execution Time (ms)", BLUE_COLOR));
            stepsLabel.setText(createStatsLabelHTML(String.valueOf(solutionPath.size()), "Solution Steps", GREEN_COLOR));
        }
        
        animationPanel.setSolutionPath(solutionPath); 
        animationPanel.startAnimation(); 
    }

    private String createStatsLabelHTML(String value, String description, Color valueColor) {
        return "<html><div style='text-align: center;'>" +
               "<span style='font-size: 48px; color: " + colorToHex(valueColor) + ";'>" + value + "</span><br>" +
               "<span style='font-size: 20px;'>" + description + "</span></div></html>";
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new RushHourSolverGUI());
    }
}

class BoardAnimationPanel extends JPanel {
    private List<Board> solutionPath;
    private int currentStepIndex = 0;
    private ScheduledExecutorService animator;
    private final int ANIMATION_DELAY_MS = 300; // Diubah dari 500 ke 300

    private final Map<Character, Color> pieceColors = new HashMap<>();
    private final Color PRIMARY_PIECE_COLOR = new Color(255, 0, 0); 
    private final Color EXIT_COLOR = new Color(0, 128, 0); 
    private final Color GRID_COLOR = new Color(200, 200, 200);

    public BoardAnimationPanel() {
        setBackground(Color.WHITE);
        initializePieceColors();
    }

    private void initializePieceColors() {
        char[] pieces = "ABCDEFGHIJKLMNOQRSTUVWXYZ".toCharArray(); 
        Color[] colors = {
            new Color(0, 128, 255), 
            new Color(255, 165, 0),  
            new Color(128, 0, 128),   
            new Color(0, 128, 0),    
            new Color(255, 192, 203), 
            new Color(165, 42, 42),   
            new Color(64, 224, 208),  
            new Color(255, 215, 0),   
            new Color(192, 192, 192), 
            new Color(128, 128, 128), 
            new Color(0, 255, 127),   
            new Color(218, 112, 214), 
            new Color(240, 230, 140), 
            new Color(255, 99, 71),   
            new Color(152, 251, 152), 
            new Color(135, 206, 235), 
            new Color(219, 112, 147), 
            new Color(244, 164, 96),  
            new Color(176, 196, 222), 
            new Color(255, 182, 193), 
            new Color(221, 160, 221), 
            new Color(173, 216, 230), 
            new Color(144, 238, 144), 
            new Color(255, 160, 122)  
        };

        for (int i = 0; i < pieces.length; i++) {
            pieceColors.put(pieces[i], colors[i % colors.length]);
        }
        pieceColors.put('P', PRIMARY_PIECE_COLOR); 
    }

    public void setSolutionPath(List<Board> solutionPath) {
        this.solutionPath = solutionPath;
        this.currentStepIndex = 0;
        if (solutionPath == null || solutionPath.isEmpty()) {
            repaint(); 
        } else {
            repaint();
        }
    }

    public void startAnimation() {
        stopAnimation(); 
        
        if (solutionPath == null || solutionPath.isEmpty()) {
            repaint(); 
            return;
        }
        
        currentStepIndex = 0; 
        repaint(); 

        animator = Executors.newSingleThreadScheduledExecutor();
        animator.scheduleAtFixedRate(() -> {
            if (currentStepIndex < solutionPath.size() - 1) {
                currentStepIndex++;
                SwingUtilities.invokeLater(this::repaint);
            } else {
                stopAnimation();
            }
        }, ANIMATION_DELAY_MS, ANIMATION_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    public void stopAnimation() {
        if (animator != null && !animator.isShutdown()) {
            animator.shutdownNow();
            animator = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (solutionPath == null || solutionPath.isEmpty() || currentStepIndex >= solutionPath.size()) {
            drawPlaceholder(g2d);
            return;
        }
        
        Board currentBoard = solutionPath.get(currentStepIndex);
        if (currentBoard == null) { 
            drawPlaceholder(g2d);
            return;
        }
        drawBoard(g2d, currentBoard);
    }

    private void drawPlaceholder(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height); 

        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2.0f));
        
        g2d.drawLine(width/4, height/4, 3*width/4, 3*height/4);
        g2d.drawLine(width/4, 3*height/4, 3*width/4, height/4);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String msg = "Pilih file dan algoritma, lalu tekan Solve";
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(msg);
        g2d.drawString(msg, (width - msgWidth) / 2, height / 2 + fm.getAscent() + 30);

        g2d.setColor(GRID_COLOR);
        g2d.drawRect(0, 0, width - 1, height - 1);
    }

    private void drawBoard(Graphics2D g2d, Board board) {
        if (board == null) return;
        
        int boardRows = board.getBoardRow();
        int boardCols = board.getBoardCol();
        int exitRow = board.getExitRow(); 
        int exitCol = board.getExitCol(); 
                                          
        
        if (boardRows <= 0 || boardCols <= 0) { 
            drawPlaceholder(g2d);
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int cellWidth = panelWidth / boardCols;
        int cellHeight = panelHeight / boardRows;
        
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));
        
        for (int i = 0; i <= boardRows; i++) {
            int y = i * cellHeight;
            g2d.drawLine(0, y, panelWidth, y);
        }
        
        for (int j = 0; j <= boardCols; j++) {
            int x = j * cellWidth;
            g2d.drawLine(x, 0, x, panelHeight);
        }
        
        g2d.setColor(EXIT_COLOR);
        if (exitCol > boardCols && exitRow >= 1 && exitRow <= boardRows) { // Right exit
             g2d.fillRect(panelWidth - cellWidth / 4, (exitRow - 1) * cellHeight, cellWidth / 4, cellHeight);
        } else if (exitCol == 0 && exitRow >= 1 && exitRow <= boardRows) { // Left exit (exitCol 0 for left)
             g2d.fillRect(0, (exitRow - 1) * cellHeight, cellWidth / 4, cellHeight);
        } else if (exitRow > boardRows && exitCol >=1 && exitCol <= boardCols) { // Bottom exit
             g2d.fillRect((exitCol - 1) * cellWidth, panelHeight - cellHeight / 4, cellWidth, cellHeight / 4);
        } else if (exitRow == 0 && exitCol >=1 && exitCol <= boardCols) { // Top exit (exitRow 0 for top)
             g2d.fillRect((exitCol - 1) * cellWidth, 0, cellWidth, cellHeight / 4);
        }
        
        java.util.Set<Character> drawnPieces = new java.util.HashSet<>();

        for (int r = 1; r <= boardRows; r++) { 
            for (int c = 1; c <= boardCols; c++) { 
                char pieceChar = board.getCharAt(r, c); 
                if (pieceChar != '.' && !drawnPieces.contains(pieceChar)) {
                    drawPiece(g2d, board, pieceChar, r, c, cellWidth, cellHeight);
                    drawnPieces.add(pieceChar);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2d, Board board, char pieceChar, 
                           int startRow, int startCol, 
                           int cellWidth, int cellHeight) {
        
        int pieceActualRow = -1, pieceActualCol = -1;
        int pieceWidthInCells = 0;
        int pieceHeightInCells = 0;

        for(int r = 1; r <= board.getBoardRow(); r++) {
            for(int c = 1; c <= board.getBoardCol(); c++) {
                if (board.getCharAt(r,c) == pieceChar) {
                    if (pieceActualRow == -1) { 
                        pieceActualRow = r;
                        pieceActualCol = c;
                    }
                    int currentWidth = 0;
                    for (int k = c; k <= board.getBoardCol(); k++) {
                        if (board.getCharAt(r,k) == pieceChar) currentWidth++;
                        else break;
                    }
                    if (currentWidth > pieceWidthInCells) pieceWidthInCells = currentWidth;

                     int currentHeight = 0;
                    for (int k = r; k <= board.getBoardRow(); k++) {
                        if (board.getCharAt(k,c) == pieceChar) currentHeight++;
                        else break;
                    }
                    if (currentHeight > pieceHeightInCells) pieceHeightInCells = currentHeight;
                }
            }
        }
        
        if (pieceWidthInCells > 1 && pieceHeightInCells > 1) {
             boolean horizontal = false;
             if (pieceActualCol + 1 <= board.getBoardCol() && board.getCharAt(pieceActualRow, pieceActualCol + 1) == pieceChar) {
                 horizontal = true;
             }

             if (horizontal) {
                 pieceHeightInCells = 1;
                 int width = 0;
                 for(int c = pieceActualCol; c <= board.getBoardCol(); c++){
                     if(board.getCharAt(pieceActualRow, c) == pieceChar) width++; else break;
                 }
                 pieceWidthInCells = width;
             } else { 
                 pieceWidthInCells = 1;
                 int height = 0;
                 for(int r = pieceActualRow; r <= board.getBoardRow(); r++){
                     if(board.getCharAt(r, pieceActualCol) == pieceChar) height++; else break;
                 }
                 pieceHeightInCells = height;
             }
        }


        if (pieceActualRow == -1) return; 


        int x = (pieceActualCol - 1) * cellWidth; 
        int y = (pieceActualRow - 1) * cellHeight; 
        int width = pieceWidthInCells * cellWidth;
        int height = pieceHeightInCells * cellHeight;
        
        Color pieceColor = pieceColors.getOrDefault(pieceChar, Color.GRAY);
        g2d.setColor(pieceColor);
        
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
            x + 2, y + 2, width - 4, height - 4, 15, 15);
        g2d.fill(roundedRect);
        
        g2d.setColor(pieceColor.darker());
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.draw(roundedRect);
        
        g2d.setColor(Color.WHITE);
        int fontSize = Math.min(cellWidth, cellHeight) / 2; 
        if (pieceWidthInCells > 1) fontSize = Math.min(cellHeight / 2 , (cellWidth * pieceWidthInCells) / (String.valueOf(pieceChar).length() +1) );
        if (pieceHeightInCells > 1) fontSize = Math.min(cellWidth / 2, (cellHeight * pieceHeightInCells) / (String.valueOf(pieceChar).length() + 1));
        fontSize = Math.max(10, fontSize); 

        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        String label = String.valueOf(pieceChar);
        int textWidth = fm.stringWidth(label);
        
        g2d.drawString(label, x + (width - textWidth) / 2, y + (height - fm.getHeight()) / 2 + fm.getAscent());
    }
}