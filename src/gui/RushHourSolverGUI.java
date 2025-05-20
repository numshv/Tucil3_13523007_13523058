package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        // Create components
        algorithmSelector = new JComboBox<>(new String[]{"Pilih Algoritma ...", "GBFS", "A*", "UCS", "IDS"});
        heuristicSelector = new JComboBox<>(new String[]{"Pilih Heuristik ...", "Jumlah blok menghalangi", "Jarak blok primer ke pintu keluar"});
        fileInput = new JTextField("Masukkan file . . .");
        errorLabel = new JLabel("Error: Input tidak valid");
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setVisible(false);
        
        boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout());
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        animationPanel = new BoardAnimationPanel();
        boardPanel.add(animationPanel, BorderLayout.CENTER);
        
        // Stats panel
        nodesLabel = createStatsLabel("1100", "Explored Nodes", ORANGE_COLOR);
        timeLabel = createStatsLabel("2200", "Execution Time", BLUE_COLOR);
        stepsLabel = createStatsLabel("1100", "Solution Steps", GREEN_COLOR);
        
        solveButton = new JButton("Solve");
        solveButton.setBackground(new Color(255, 236, 66));
        solveButton.setForeground(Color.BLACK);
        solveButton.setFocusPainted(false);
        solveButton.setBorderPainted(false);
        solveButton.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Add action listener to the solve button
        solveButton.addActionListener(e -> {
            if (validateInputs()) {
                solvePuzzle();
            } else {
                errorLabel.setVisible(true);
            }
        });
        
        // Action listener for file input field to clear default text
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
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleStart = new JLabel("Rush Hour Solver", SwingConstants.LEFT);
        titleStart.setFont(new Font("Arial", Font.BOLD, 28));
        titleStart.setForeground(PINK_COLOR);
        
        JLabel titleEnd = new JLabel("Yang Diselesainnya Also Sangat Rushed (kenapa seminggu doang astaga)");
        titleEnd.setFont(new Font("Arial", Font.BOLD, 28));
        
        titlePanel.add(titleStart, BorderLayout.WEST);
        titlePanel.add(titleEnd, BorderLayout.CENTER);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlsPanel.add(algorithmSelector);
        controlsPanel.add(heuristicSelector);
        controlsPanel.add(fileInput);
        controlsPanel.add(solveButton);
        
        // Central panel for board and error message
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(errorLabel, BorderLayout.NORTH);
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.add(nodesLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(stepsLabel);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 10));
        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.EAST);
        
        // Add to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        JLabel authorLabel = new JLabel("Proudly made by Queen Rana and her Slave Noumi", SwingConstants.LEFT);
        JLabel idLabel = new JLabel("13523007 | 13523058 | IF'23", SwingConstants.RIGHT);
        footerPanel.add(authorLabel, BorderLayout.WEST);
        footerPanel.add(idLabel, BorderLayout.EAST);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Set component preferred sizes
        algorithmSelector.setPreferredSize(new Dimension(200, 40));
        heuristicSelector.setPreferredSize(new Dimension(200, 40));
        fileInput.setPreferredSize(new Dimension(400, 40));
        solveButton.setPreferredSize(new Dimension(80, 40));
        statsPanel.setPreferredSize(new Dimension(300, 500));
        
        // Set the content pane
        setContentPane(mainPanel);
    }
    
    private boolean validateInputs() {
        // Validate algorithm selection
        if (algorithmSelector.getSelectedIndex() == 0) {
            return false;
        }
        
        // Validate heuristic selection if applicable
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        if ((selectedAlgorithm.equals("GBFS") || selectedAlgorithm.equals("A*")) 
                && heuristicSelector.getSelectedIndex() == 0) {
            return false;
        }
        
        // Validate file input
        String filePath = fileInput.getText();
        if (filePath.equals("Masukkan file . . .") || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        
        return true;
    }
    
    private void solvePuzzle() {
        // Hide error label if visible
        errorLabel.setVisible(false);
        
        // This is where you would call your solver logic
        // For demonstration, we'll create a dummy list of boards
        List<obj.Board> solutionPath = createDummySolution();
        
        // Update stats
        nodesLabel.setText("<html><div style='text-align: center;'>" +
                "<span style='font-size: 48px; color: " + colorToHex(ORANGE_COLOR) + ";'>1100</span><br>" +
                "<span style='font-size: 20px;'>Explored Nodes</span></div></html>");
                
        timeLabel.setText("<html><div style='text-align: center;'>" +
                "<span style='font-size: 48px; color: " + colorToHex(BLUE_COLOR) + ";'>2200</span><br>" +
                "<span style='font-size: 20px;'>Execution Time</span></div></html>");
                
        stepsLabel.setText("<html><div style='text-align: center;'>" +
                "<span style='font-size: 48px; color: " + colorToHex(GREEN_COLOR) + ";" + solutionPath.size() + "</span><br>" +
                "<span style='font-size: 20px;'>Solution Steps</span></div></html>");
        
        // Start animation
        animationPanel.setSolutionPath(solutionPath);
        animationPanel.startAnimation();
    }
    
    private List<obj.Board> createDummySolution() {
        // This is a placeholder - your actual code would call the solver and return the real solution path
        List<obj.Board> dummySolution = new ArrayList<>();
        
        // For demo purposes only - would be replaced with actual board states
        try {
            // Create a simple 6x6 board with exit at position (3, 6)
            char[][] boardState = new char[6][6];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    boardState[i][j] = '.';
                }
            }
            
            // Add a horizontal primary piece 'P' at position (3, 1) with length 2
            boardState[2][0] = 'P';
            boardState[2][1] = 'P';
            
            // Add a vertical blocking piece 'A' at position (2, 3) with length 3
            boardState[1][2] = 'A';
            boardState[2][2] = 'A';
            boardState[3][2] = 'A';
            
            // Add a horizontal blocking piece 'B' at position (5, 4) with length 2
            boardState[4][3] = 'B';
            boardState[4][4] = 'B';
            
            // Create initial board state
            obj.Board initialBoard = new obj.Board(boardState, 3, 6);
            dummySolution.add(initialBoard);
            
            // In a real implementation, you would add more board states to show the solution
            // This would come from your solver algorithms
            
        } catch (Exception e) {
            System.err.println("Error creating dummy solution: " + e.getMessage());
        }
        
        return dummySolution;
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

// Custom panel for displaying and animating the board
class BoardAnimationPanel extends JPanel {
    private List<obj.Board> solutionPath;
    private int currentStepIndex = 0;
    private ScheduledExecutorService animator;
    private final int ANIMATION_DELAY_MS = 500; // Animation speed

    // Colors for different pieces
    private final Map<Character, Color> pieceColors = new HashMap<>();
    private final Color PRIMARY_PIECE_COLOR = new Color(255, 0, 0); // Red for piece 'P'
    private final Color EXIT_COLOR = new Color(0, 128, 0); // Green for exit
    private final Color GRID_COLOR = new Color(200, 200, 200);

    public BoardAnimationPanel() {
        setBackground(Color.WHITE);
        initializePieceColors();
    }

    private void initializePieceColors() {
        // Initialize with colors for pieces A-Z
        char[] pieces = "ABCDEFGHIJKLMNOUQRSTUVWXYZ".toCharArray();
        Color[] colors = {
            new Color(0, 128, 255),    // Blue
            new Color(255, 165, 0),    // Orange
            new Color(128, 0, 128),    // Purple
            new Color(0, 128, 0),      // Green
            new Color(255, 192, 203),  // Pink
            new Color(165, 42, 42),    // Brown  
            new Color(64, 224, 208),   // Turquoise
            new Color(255, 215, 0),    // Gold
            new Color(192, 192, 192),  // Silver
            new Color(128, 128, 128),  // Gray
            new Color(0, 255, 127),    // Spring Green
            new Color(218, 112, 214),  // Orchid
            new Color(240, 230, 140),  // Khaki
            new Color(255, 99, 71),    // Tomato
            new Color(152, 251, 152),  // Pale Green
            new Color(135, 206, 235),  // Sky Blue
            new Color(219, 112, 147),  // Pale Violet Red
            new Color(244, 164, 96),   // Sandy Brown
            new Color(176, 196, 222),  // Light Steel Blue
            new Color(255, 182, 193),  // Light Pink
            new Color(221, 160, 221),  // Plum
            new Color(173, 216, 230),  // Light Blue
            new Color(144, 238, 144),  // Light Green
            new Color(255, 160, 122)   // Light Salmon
        };

        for (int i = 0; i < pieces.length; i++) {
            pieceColors.put(pieces[i], colors[i % colors.length]);
        }

        // Always set color for primary piece 'P'
        pieceColors.put('P', PRIMARY_PIECE_COLOR);
    }

    public void setSolutionPath(List<obj.Board> solutionPath) {
        this.solutionPath = solutionPath;
        this.currentStepIndex = 0;
        repaint();
    }

    public void startAnimation() {
        // Stop any existing animation
        stopAnimation();
        
        if (solutionPath == null || solutionPath.isEmpty()) {
            return;
        }
        
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
        
        obj.Board currentBoard = solutionPath.get(currentStepIndex);
        drawBoard(g2d, currentBoard);
    }

    private void drawPlaceholder(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2.0f));
        
        // Draw an X
        g2d.drawLine(0, 0, width, height);
        g2d.drawLine(0, height, width, 0);
        
        // Draw border
        g2d.drawRect(0, 0, width - 1, height - 1);
    }

    private void drawBoard(Graphics2D g2d, obj.Board board) {
        if (board == null) return;
        
        int boardRows = board.getBoardRow();
        int boardCols = board.getBoardCol();
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        int width = getWidth();
        int height = getHeight();
        
        int cellWidth = width / boardCols;
        int cellHeight = height / boardRows;
        
        // Draw grid
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));
        
        // Draw horizontal lines
        for (int i = 0; i <= boardRows; i++) {
            int y = i * cellHeight;
            g2d.drawLine(0, y, width, y);
        }
        
        // Draw vertical lines
        for (int j = 0; j <= boardCols; j++) {
            int x = j * cellWidth;
            g2d.drawLine(x, 0, x, height);
        }
        
        // Draw exit point
        g2d.setColor(EXIT_COLOR);
        if (exitRow == 0) { // Top exit
            g2d.fillRect((exitCol - 1) * cellWidth, 0, cellWidth, cellHeight / 4);
        } else if (exitRow > boardRows) { // Bottom exit
            g2d.fillRect((exitCol - 1) * cellWidth, height - cellHeight / 4, cellWidth, cellHeight / 4);
        } else if (exitCol == 0) { // Left exit
            g2d.fillRect(0, (exitRow - 1) * cellHeight, cellWidth / 4, cellHeight);
        } else if (exitCol > boardCols) { // Right exit
            g2d.fillRect(width - cellWidth / 4, (exitRow - 1) * cellHeight, cellWidth / 4, cellHeight);
        }
        
        // Draw pieces
        for (int i = 1; i <= boardRows; i++) {
            for (int j = 1; j <= boardCols; j++) {
                char piece = board.getCharAt(i, j);
                if (piece != '.') {
                    // Check if this is the top-left corner of the piece
                    boolean isTopLeft = (i == 1 || board.getCharAt(i-1, j) != piece) && 
                                       (j == 1 || board.getCharAt(i, j-1) != piece);
                    
                    if (isTopLeft) {
                        drawPiece(g2d, board, piece, i, j, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2d, obj.Board board, char pieceChar, int startRow, int startCol, 
                          int cellWidth, int cellHeight) {
        // Determine piece dimensions
        int pieceWidth = 0;
        int pieceHeight = 0;
        
        // Check horizontally
        int col = startCol;
        while (col <= board.getBoardCol() && board.getCharAt(startRow, col) == pieceChar) {
            pieceWidth++;
            col++;
        }
        
        // Check vertically
        int row = startRow;
        while (row <= board.getBoardRow() && board.getCharAt(row, startCol) == pieceChar) {
            pieceHeight++;
            row++;
        }
        
        // Calculate pixel coordinates and dimensions
        int x = (startCol - 1) * cellWidth;
        int y = (startRow - 1) * cellHeight;
        int width = pieceWidth * cellWidth;
        int height = pieceHeight * cellHeight;
        
        // Draw the piece with rounded corners
        Color pieceColor = pieceColors.getOrDefault(pieceChar, Color.GRAY);
        g2d.setColor(pieceColor);
        
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
            x + 2, y + 2, width - 4, height - 4, 15, 15);
        g2d.fill(roundedRect);
        
        // Draw piece outline
        g2d.setColor(pieceColor.darker());
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.draw(roundedRect);
        
        // Draw piece label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.min(width, height) / 3));
        FontMetrics fm = g2d.getFontMetrics();
        String label = String.valueOf(pieceChar);
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();
        g2d.drawString(label, x + (width - textWidth) / 2, y + height / 2 + textHeight / 3);
    }
}