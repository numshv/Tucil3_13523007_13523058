package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
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


// Custom RoundedBorder class
class RoundedBorder implements Border {
    private int radius;
    private int thickness;
    private Color color;
    private Insets insets;

    RoundedBorder(int radius, int thickness, Color color) {
        this.radius = radius;
        this.thickness = thickness;
        this.color = color;
        // Insets should account for the thickness of the border line
        this.insets = new Insets(thickness + radius / 3, thickness + radius / 2, thickness + radius / 3, thickness + radius / 2);
    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public boolean isBorderOpaque() {
        return false; // Border itself is not opaque, it's just lines
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.color);
        g2.setStroke(new BasicStroke(this.thickness));
        // Adjust coordinates and size for stroke thickness to keep border centered
        float offset = this.thickness / 2.0f;
        g2.draw(new RoundRectangle2D.Float(x + offset, y + offset, width - this.thickness, height - this.thickness, radius * 2, radius * 2));
        g2.dispose();
    }
}

// Custom JButton with rounded background
class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false); // We'll paint our own background
        setFocusPainted(false);      // No focus ring
        // Border can be set externally if an outline is desired
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground());
        }
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius * 2, radius * 2));
        
        // Let superclass paint text and icon
        super.paintComponent(g2);
        g2.dispose();
    }
}

// Custom ComboBox UI
class ModernComboBoxUI extends BasicComboBoxUI {
    private int cornerRadius;
    private Color borderColor;
    private Color arrowColor = new Color(100, 100, 100); 
    private Color disabledArrowColor = new Color(180, 180, 180);
    private Color comboBoxBackground = Color.WHITE;
    private Color disabledComboBoxBackground = new Color(235, 235, 235); 
    private Color focusedBorderColor = new Color(100, 149, 237); 
    private Color disabledBorderColor = new Color(210, 210, 210);
    private Color disabledTextColor = new Color(150, 150, 150);


    public ModernComboBoxUI(int cornerRadius, Color borderColor) {
        this.cornerRadius = cornerRadius;
        this.borderColor = borderColor;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JComboBox<?> comboBox = (JComboBox<?>) c;
        comboBox.setOpaque(false);
        comboBox.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10)); 
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int size = 7; 
                int x = (w - size) / 2;
                int y = (h - size / 2) / 2 -1; 

                g2.setColor(comboBox.isEnabled() ? arrowColor : disabledArrowColor); 
                Path2D path = new Path2D.Double();
                path.moveTo(x, y);
                path.lineTo(x + size, y);
                path.lineTo(x + size / 2.0, y + size / 1.5);
                path.closePath();
                g2.fill(path);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, 0); 
            }
        };
        button.setName("ComboBox.arrowButton");
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(false);
        return button;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean isEnabled = c.isEnabled();

        g2.setColor(isEnabled ? comboBoxBackground : disabledComboBoxBackground);
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), cornerRadius * 2, cornerRadius * 2);

        Color currentBorderColorToPaint;
        float currentStrokeWidth;

        if (!isEnabled) {
            currentBorderColorToPaint = disabledBorderColor;
            currentStrokeWidth = 1f;
        } else if (comboBox.hasFocus()) {
            currentBorderColorToPaint = focusedBorderColor; 
            currentStrokeWidth = 1.5f;
        } else {
            currentBorderColorToPaint = borderColor;
            currentStrokeWidth = 1f;
        }
        g2.setColor(currentBorderColorToPaint);
        g2.setStroke(new BasicStroke(currentStrokeWidth));
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, cornerRadius * 2, cornerRadius * 2);
        
        Color originalForeground = c.getForeground();
        Color foregroundToUse = isEnabled ? UIManager.getColor("ComboBox.foreground") : disabledTextColor;

        if (editor != null) {
            editor.setEnabled(isEnabled); 
            if (editor instanceof JComponent) {
                ((JComponent)editor).setForeground(foregroundToUse);
                ((JComponent)editor).setOpaque(false); 
            }
        } else {
            c.setForeground(foregroundToUse);
        }

        Rectangle r = rectangleForCurrentValue();
        paintCurrentValue(g, r, comboBox.hasFocus()); 

        if (editor == null) {
            c.setForeground(originalForeground);
        }
        
        g2.dispose();
    }
    
    @Override
    protected ListCellRenderer<Object> createRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 

                Color fgColor;
                if (!comboBox.isEnabled()) { 
                    fgColor = disabledTextColor;
                } else if (index == -1 && value != null && value.toString().startsWith("Pilih")) { 
                    fgColor = new Color(120, 120, 120); 
                } else if (index == 0 && value != null && value.toString().startsWith("Pilih") && !isSelected){
                     fgColor = new Color(120, 120, 120); 
                } else if (isSelected) {
                    fgColor = Color.BLACK; 
                } else {
                    fgColor = UIManager.getColor("List.foreground");
                }
                setForeground(fgColor);

                if (isSelected && comboBox.isEnabled()) { 
                    setBackground(new Color(220, 235, 255)); 
                } else {
                    setBackground(Color.WHITE); 
                }
                return this;
            }
        };
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius * 2, cornerRadius * 2);
                
                g2.dispose();
            }
        };

        popup.setOpaque(false); 
        popup.setBorder(new RoundedBorder(this.cornerRadius, 1, this.borderColor)); 

        JList<?> list = popup.getList();
        list.setBackground(Color.WHITE); 
        list.setSelectionBackground(new Color(220, 230, 250)); 
        list.setSelectionForeground(Color.BLACK);
        
        for (Component comp : popup.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.setBorder(null); 
                scrollPane.setOpaque(false); 
                scrollPane.getViewport().setOpaque(false); 
                break;
            }
        }
        return popup;
    }
}


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
    private RoundedButton solveButton; 
    private final Color PINK_COLOR = new Color(240, 79, 120);
    private final Color ERROR_COLOR = Color.RED;
    private final Color ORANGE_COLOR = new Color(251, 107, 29);
    private final Color BLUE_COLOR = new Color(77, 155, 230);
    private final Color GREEN_COLOR = new Color(145, 219, 105);
    private final int CORNER_RADIUS = 8; 
    private final Color BORDER_COLOR = new Color(200, 200, 200); 
    private final Color COMBOBOX_BORDER_COLOR = new Color(200, 200, 200); 
    private final int BUTTON_BORDER_THICKNESS = 1; 
    private final String FILE_INPUT_PLACEHOLDER = "Masukkan nama file..."; 


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
        RoundedBorder fieldRoundedBorder = new RoundedBorder(CORNER_RADIUS, 1, BORDER_COLOR); 
        RoundedBorder buttonRoundedBorder = new RoundedBorder(CORNER_RADIUS, BUTTON_BORDER_THICKNESS, Color.BLACK);


        algorithmSelector = new JComboBox<>(new String[]{"Pilih Algoritma ...", "GBFS", "A*", "UCS", "IDS"});
        heuristicSelector = new JComboBox<>(new String[]{"Pilih Heuristik ...", "Jumlah blok menghalangi", "Jarak blok primer ke pintu keluar"});
        
        ModernComboBoxUI modernAlgorithmUI = new ModernComboBoxUI(CORNER_RADIUS, COMBOBOX_BORDER_COLOR);
        algorithmSelector.setUI(modernAlgorithmUI);
        algorithmSelector.setBackground(Color.WHITE); 
        
        ModernComboBoxUI modernHeuristicUI = new ModernComboBoxUI(CORNER_RADIUS, COMBOBOX_BORDER_COLOR);
        heuristicSelector.setUI(modernHeuristicUI); 
        heuristicSelector.setBackground(Color.WHITE); 


        fileInput = new JTextField(FILE_INPUT_PLACEHOLDER); 
        fileInput.setForeground(Color.GRAY); 
        fileInput.setBorder(BorderFactory.createCompoundBorder(fieldRoundedBorder, 
                                BorderFactory.createEmptyBorder(5, 8, 5, 8))); 

        errorLabel = new JLabel("Error: Input tidak valid");
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setVisible(false);
        
        boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout());
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        boardPanel.setBackground(Color.WHITE); 
        
        animationPanel = new BoardAnimationPanel();
        boardPanel.add(animationPanel, BorderLayout.CENTER);
        
        nodesLabel = createStatsLabel("0", "Explored Nodes", ORANGE_COLOR);
        timeLabel = createStatsLabel("0", "Execution Time (ms)", BLUE_COLOR);
        stepsLabel = createStatsLabel("0", "Solution Steps", GREEN_COLOR);
        
        solveButton = new RoundedButton("Solve", CORNER_RADIUS); 
        solveButton.setBackground(new Color(251, 255, 134));
        solveButton.setForeground(Color.BLACK);
        solveButton.setBorder(buttonRoundedBorder); 
        solveButton.setFont(new Font("Arial", Font.BOLD, 16));
        
        solveButton.addActionListener(e -> {
            if (validateInputs()) {
                solvePuzzle();
            } else {
                errorLabel.setVisible(true);
            }
        });
        
        fileInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (fileInput.getText().equals(FILE_INPUT_PLACEHOLDER) && fileInput.getForeground().equals(Color.GRAY)) {
                    fileInput.setText("");
                    fileInput.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (fileInput.getText().isEmpty()) {
                    fileInput.setText(FILE_INPUT_PLACEHOLDER);
                    fileInput.setForeground(Color.GRAY);
                }
            }
        });

        algorithmSelector.addActionListener(e -> {
            String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
            boolean requiresHeuristic = selectedAlgorithm.equals("GBFS") || selectedAlgorithm.equals("A*");
            heuristicSelector.setEnabled(requiresHeuristic);
            if (!requiresHeuristic) {
                heuristicSelector.setSelectedIndex(0); 
            }
            heuristicSelector.repaint(); 
        });
        heuristicSelector.setEnabled(false); 
    }
    
    private JLabel createStatsLabel(String value, String description, Color valueColor) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                "<span style='font-size: 48px; color: " + colorToHex(valueColor) + ";'><b>" + value + "</b></span><br>" +
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
        mainPanel.setBackground(Color.WHITE); 
        
        JPanel titleContentPanel = new JPanel();
        titleContentPanel.setLayout(new BoxLayout(titleContentPanel, BoxLayout.Y_AXIS));
        titleContentPanel.setOpaque(false);

        JPanel firstTitleRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        firstTitleRowPanel.setOpaque(false);

        JLabel titleMain = new JLabel("Rush Hour Solver");
        titleMain.setFont(new Font("Arial", Font.BOLD, 28));
        titleMain.setForeground(PINK_COLOR);

        JLabel titleSubPart1 = new JLabel(" Yang Diselesainnya Also Sangat"); 
        titleSubPart1.setFont(new Font("Arial", Font.BOLD, 28)); 
        
        firstTitleRowPanel.add(titleMain);
        firstTitleRowPanel.add(titleSubPart1);
        firstTitleRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JLabel titleSubPart2 = new JLabel("Rushed (kenapa seminggu doang astaga)");
        titleSubPart2.setFont(new Font("Arial", Font.BOLD, 28)); 
        titleSubPart2.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titleContentPanel.add(firstTitleRowPanel); 
        titleContentPanel.add(titleSubPart2);      

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(titleContentPanel);

        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlsPanel.setOpaque(false); 
        controlsPanel.add(algorithmSelector);
        controlsPanel.add(heuristicSelector);
        controlsPanel.add(Box.createHorizontalStrut(10)); 
        controlsPanel.add(fileInput);
        controlsPanel.add(solveButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false); 
        centerPanel.add(errorLabel, BorderLayout.NORTH);
        centerPanel.add(boardPanel, BorderLayout.CENTER); 
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        statsPanel.setOpaque(false); 
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.add(nodesLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(stepsLabel);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 10));
        contentPanel.setOpaque(false); 
        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.EAST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false); 
        JLabel authorLabel = new JLabel("Proudly made by Queen Rana and her Slave Noumi", SwingConstants.LEFT);
        JLabel idLabel = new JLabel("13523007 | 13523058 | IF'23", SwingConstants.RIGHT);
        footerPanel.add(authorLabel, BorderLayout.WEST);
        footerPanel.add(idLabel, BorderLayout.EAST);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        algorithmSelector.setPreferredSize(new Dimension(220, 40)); 
        heuristicSelector.setPreferredSize(new Dimension(270, 40)); 
        fileInput.setPreferredSize(new Dimension(350, 40)); 
        solveButton.setPreferredSize(new Dimension(100, 40)); 
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
        
        String fileName = fileInput.getText(); 
        if (fileName.equals(FILE_INPUT_PLACEHOLDER) || fileName.isEmpty()) { 
            errorLabel.setText("Error: Path file belum dimasukkan.");
            return false;
        }
        
        String fullPath = "test/" + fileName; 
        File file = new File(fullPath);
        if (!file.exists() || !file.isFile()) {
            errorLabel.setText("Error: File tidak ditemukan atau bukan file valid di path: " + fullPath);
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
        int exitSymbolPositionInLine = -1; 
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
            
            if (fileReader.hasNextLine()) fileReader.nextLine(); 

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

                if (i == exitLineIndex) { 
                    String lineWithoutK;
                    if (exitSymbolPositionInLine == 0 && processedLineContent.length() >= 1) { 
                        lineWithoutK = processedLineContent.substring(1);
                        exitRowPosition = processedRows + 1; 
                        exitColumnPosition = 0; 
                    } else if (exitSymbolPositionInLine > 0 && exitSymbolPositionInLine == processedLineContent.length() - 1) { 
                        lineWithoutK = processedLineContent.substring(0, exitSymbolPositionInLine);
                         exitRowPosition = processedRows + 1; 
                        exitColumnPosition = columnCount + 1; 
                    } else {
                        throw new IOException("Posisi 'K' pada baris data ke-" + (processedRows + 1) +
                                          " (isi: '" + processedLineContent + "') tidak valid. 'K' harus di awal atau akhir baris data.");
                    }
                    boardRowData = lineWithoutK.trim(); 
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

            if (exitRowPosition == -1) { 
                if (firstDataLineIndex == -1 || exitLineIndex < firstDataLineIndex) { 
                    exitRowPosition = 0; 
                } else { 
                    exitRowPosition = rowCount + 1; 
                }
                exitColumnPosition = exitSymbolPositionInLine + 1; 
                
                if (exitColumnPosition < 1 || exitColumnPosition > columnCount) {
                    throw new IOException("Posisi 'K' (kolom " + exitColumnPosition +
                                        ") untuk pintu keluar atas/bawah di luar rentang kolom papan [1-" + columnCount + "]. Isi baris K: '" + 
                                        fileLines.get(exitLineIndex).replaceAll("\\s+$", "") + "'");
                }
            }
            
            Board board = new Board(boardGrid, exitRowPosition, exitColumnPosition);
            
            if (board.getPieceCounter() != -1 && declaredPieces != board.getPieceCounter()) { 
                 throw new IOException("Jumlah piece tidak sesuai deklarasi. Dideklarasikan: " +
                                     declaredPieces + ", Dihitung dari papan (unik, tanpa P): " + board.getPieceCounter());
            }
            
            System.out.println("Papan berhasil dibuat dari file: " + filePath);

            return board;

        } catch (FileNotFoundException e) {
            throw new IOException("File tidak ditemukan: " + filePath + ". Detail: " + e.getMessage(), e);
        } catch (NoSuchElementException | IllegalArgumentException e) { 
            throw new IOException("Format file tidak valid atau data tidak lengkap. Detail: " + e.getMessage(), e);
        } catch (IOException e) { 
            throw e; 
        } catch (Exception e) { 
            throw new IOException("Terjadi error saat memproses file: " + e.getMessage(), e);
        } finally {
            if (fileReader != null) fileReader.close();
        }
    }
    
    private void solvePuzzle() {
        errorLabel.setVisible(false);
        animationPanel.setCustomPlaceholderMessage(null); // Clear previous custom messages
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        String selectedHeuristicName = (String) heuristicSelector.getSelectedItem();
        String fileName = fileInput.getText(); 

        if (fileName.equals(FILE_INPUT_PLACEHOLDER)) {
            errorLabel.setText("Error: Path file belum dimasukkan.");
            errorLabel.setVisible(true);
            return;
        }
        String fullPath = "test/" + fileName; 

        Board initialBoard;
        try {
            initialBoard = readBoardFromFile(fullPath); 
            if (initialBoard == null) { 
                 errorLabel.setText("Error: Gagal memuat board dari file (null).");
                 errorLabel.setVisible(true);
                 return;
            }
        } catch (IOException e) {
            errorLabel.setText("Error saat membaca file: " + e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace(); 
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
                    gbfsSolver.writeSolution(fullPath + "_gbfs_solution.txt"); 
                    break;
                case "A*":
                    AStar aStarSolver = new AStar(initialBoard, useHeuristic1);
                    if (aStarSolver.getSolution() != null) {
                        solutionPath = aStarSolver.getSolution().getPathOfBoards();
                    }
                    nodesExplored = aStarSolver.getExploredNodesCount();
                    aStarSolver.writeSolution(fullPath + "_astar_solution.txt"); 
                    break;
                case "UCS":
                    UCS ucsSolver = new UCS(initialBoard);
                    if (ucsSolver.getSolution() != null) {
                        solutionPath = ucsSolver.getSolution().getPathOfBoards();
                    }
                    nodesExplored = ucsSolver.getExploredNodesCount();
                    ucsSolver.writeSolution(fullPath + "_ucs_solution.txt"); 
                    break;
                case "IDS":
                    IDS idsSolver = new IDS(initialBoard);
                    if (idsSolver.getSolution() != null) {
                        solutionPath = idsSolver.getSolution().getPathOfBoards();
                    }
                    nodesExplored = idsSolver.getExploredNodesCount();
                    idsSolver.writeSolution(fullPath + "_ids_solution.txt"); 
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

        boolean actualSolutionFoundAndFinished = false;
        if (solutionPath != null && !solutionPath.isEmpty()) {
            Board lastBoard = solutionPath.get(solutionPath.size() - 1);
            if (lastBoard.isFinished()) { // Assuming Board.isFinished() exists and is correct
                actualSolutionFoundAndFinished = true;
            }
        }

        if (actualSolutionFoundAndFinished) {
            animationPanel.setCustomPlaceholderMessage(null); 
            nodesLabel.setText(createStatsLabelHTML(String.valueOf(nodesExplored), "Explored Nodes", ORANGE_COLOR));
            timeLabel.setText(createStatsLabelHTML(String.valueOf(durationInMillis), "Execution Time (ms)", BLUE_COLOR));
            stepsLabel.setText(createStatsLabelHTML(String.valueOf(solutionPath.size()), "Solution Steps", GREEN_COLOR));
            animationPanel.setSolutionPath(solutionPath);
            animationPanel.startAnimation();
        } else {
            animationPanel.setSolutionPath(null); 
            animationPanel.setCustomPlaceholderMessage("Solusi tidak ditemukan"); 

            nodesLabel.setText(createStatsLabelHTML(String.valueOf(nodesExplored), "Explored Nodes", ORANGE_COLOR));
            timeLabel.setText(createStatsLabelHTML(String.valueOf(durationInMillis), "Execution Time (ms)", BLUE_COLOR));
            stepsLabel.setText(createStatsLabelHTML("0", "Solution Steps", GREEN_COLOR));
            
            if (solutionPath != null && !solutionPath.isEmpty()) { 
                // Path found but not finished
                // errorLabel.setText("Algoritma menemukan jalur, namun tidak mencapai solusi akhir.");
                // errorLabel.setVisible(true); 
                // The custom placeholder message is now handling this.
            } else { 
                // No path found by solver
                // errorLabel.setText("Solusi tidak ditemukan oleh algoritma " + selectedAlgorithm + ".");
                // errorLabel.setVisible(true);
                 // The custom placeholder message is now handling this.
            }
        }
    }

    private String createStatsLabelHTML(String value, String description, Color valueColor) {
        return "<html><div style='text-align: center;'>" +
               "<span style='font-size: 48px; color: " + colorToHex(valueColor) + ";'><b>" + value + "</b></span><br>" + 
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
    private final int ANIMATION_DELAY_MS = 300; 
    private boolean isAnimating = false;
    private String customPlaceholderMessage = null; // Added for custom messages


    private final Map<Character, Color> pieceColors = new HashMap<>();
    private final Color PRIMARY_PIECE_COLOR = new Color(255, 0, 0); 
    private final Color EXIT_COLOR = new Color(77, 155, 230); 
    private final Color GRID_COLOR = new Color(200, 200, 200);
    private final Color BOARD_BACKGROUND = new Color(240, 240, 240);
    
    private JButton prevButton;
    private JButton nextButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JLabel stepLabel;
    private JPanel controlPanel;

    public BoardAnimationPanel() {
        setBackground(Color.WHITE);
        initializePieceColors();
        initializeControls();
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.SOUTH);
        updateControlsState();
    }
    
    public void setCustomPlaceholderMessage(String message) {
        this.customPlaceholderMessage = message;
        if (message != null) { // If setting a custom message, ensure no animation is running
            stopAnimation();
            this.solutionPath = null; // Clear solution path to ensure placeholder is shown
        }
        repaint();
    }


    private void initializeControls() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        prevButton = new JButton("<-");
        prevButton.addActionListener(e -> {
            if (currentStepIndex > 0) {
                currentStepIndex--;
                updateStepLabel();
                repaint();
            }
        });
        
        nextButton = new JButton("->");
        nextButton.addActionListener(e -> {
            if (solutionPath != null && currentStepIndex < solutionPath.size() - 1) {
                currentStepIndex++;
                updateStepLabel();
                repaint();
            }
        });
        
        playButton = new JButton(">");
        playButton.addActionListener(e -> startAnimation());
        
        pauseButton = new JButton("||");
        pauseButton.addActionListener(e -> stopAnimation());
        
        resetButton = new JButton("Replay");
        resetButton.addActionListener(e -> {
            currentStepIndex = 0;
            updateStepLabel();
            repaint();
            if(solutionPath != null && !solutionPath.isEmpty()) startAnimation(); // Optionally restart animation
        });
        
        stepLabel = new JLabel("Step: 0/0");
        
        controlPanel.add(resetButton);
        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(stepLabel);
    }
    
    private void updateStepLabel() {
        int total = (solutionPath != null && !solutionPath.isEmpty()) ? solutionPath.size() : 0;
        int currentDisplayStep = (total == 0) ? 0 : currentStepIndex + 1;
        stepLabel.setText("Step: " + currentDisplayStep + "/" + total);
    }
    
    private void updateControlsState() {
        boolean hasSolution = solutionPath != null && !solutionPath.isEmpty();
        
        prevButton.setEnabled(hasSolution && currentStepIndex > 0);
        nextButton.setEnabled(hasSolution && currentStepIndex < (solutionPath != null ? solutionPath.size() - 1 : 0));
        playButton.setEnabled(hasSolution && !isAnimating && (solutionPath != null && currentStepIndex < solutionPath.size() -1));
        pauseButton.setEnabled(isAnimating);
        resetButton.setEnabled(hasSolution); // Reset always enabled if there's a path
        
        updateStepLabel();
    }

    private void initializePieceColors() {
        char[] pieces = "ABCDEFGHIJKLMNOQRSTUVWXYZ".toCharArray();
        Color[] colors = {
            new Color(251, 107, 29),  
            new Color(232, 59, 59),   
            new Color(131, 28, 93),   
            new Color(195, 36, 84),   
            new Color(240, 79, 120),  
            new Color(246, 129, 129), 
            new Color(252, 167, 144), 
            new Color(227, 200, 150), 
            new Color(171, 148, 122), 
            new Color(150, 108, 108), 
            new Color(98, 85, 101),   
            new Color(62, 53, 70),    
            new Color(11, 94, 101),   
            new Color(11, 138, 143),  
            new Color(30, 188, 115),  
            new Color(145, 219, 105), 
            new Color(251, 255, 134), 
            new Color(251, 185, 84),  
            new Color(205, 104, 61),  
            new Color(158, 69, 57),   
            new Color(122, 48, 69),   
            new Color(107, 62, 117),  
            new Color(144, 94, 169),  
            new Color(168, 132, 243), 
            new Color(234, 173, 237), 
            new Color(143, 211, 255)  
        };

        for (int i = 0; i < pieces.length; i++) {
            pieceColors.put(pieces[i], colors[i % colors.length]);
        }
        pieceColors.put('P', PRIMARY_PIECE_COLOR); 
    }

    public void setSolutionPath(List<Board> solutionPath) {
        this.solutionPath = solutionPath;
        this.currentStepIndex = 0;
        if (this.solutionPath != null && !this.solutionPath.isEmpty()) {
             this.customPlaceholderMessage = null; // Clear custom message if a valid path is set
        }
        updateControlsState();
        repaint();
    }

    public void startAnimation() {
        stopAnimation();
        
        if (solutionPath == null || solutionPath.isEmpty() || currentStepIndex >= solutionPath.size() -1) {
             isAnimating = false; // Ensure isAnimating is false if animation cannot start/continue
             updateControlsState();
             repaint(); 
            return;
        }
        
        isAnimating = true;
        updateControlsState();
        
        animator = Executors.newSingleThreadScheduledExecutor();
        animator.scheduleAtFixedRate(() -> {
            if (currentStepIndex < solutionPath.size() - 1) {
                currentStepIndex++;
                SwingUtilities.invokeLater(() -> {
                    updateControlsState();
                    repaint();
                });
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
        isAnimating = false;
        updateControlsState();
    }
    
    public void showStep(int step) {
        if (solutionPath != null && step >= 0 && step < solutionPath.size()) {
            stopAnimation();
            currentStepIndex = step;
            updateControlsState();
            repaint();
        }
    }
    
    public int getCurrentStep() {
        return currentStepIndex;
    }
    
    public int getTotalSteps() {
        return solutionPath != null ? solutionPath.size() : 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (customPlaceholderMessage != null || solutionPath == null || solutionPath.isEmpty() || currentStepIndex >= solutionPath.size()) {
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
        int height = getHeight() - controlPanel.getPreferredSize().height; 
        
        g2d.setColor(Color.WHITE); // Explicitly set background to white
        g2d.fillRect(0, 0, width, height); 

        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        
        String msg = (customPlaceholderMessage != null) ? customPlaceholderMessage : "Pilih file dan algoritma, lalu tekan Solve";
        
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(msg);
        g2d.drawString(msg, (width - msgWidth) / 2, height / 2 + fm.getAscent() / 2); 

        g2d.setColor(Color.BLACK); 
        g2d.setStroke(new BasicStroke(1.0f)); 
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
        int panelHeight = getHeight() - controlPanel.getPreferredSize().height; 
        
        int gridRows = boardRows + 2; 
        int gridCols = boardCols + 2; 
        
        int cellWidth = panelWidth / gridCols;
        int cellHeight = panelHeight / gridRows;

        int offsetX = cellWidth;
        int offsetY = cellHeight;
        
        g2d.setColor(BOARD_BACKGROUND);
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        
        g2d.setColor(BOARD_BACKGROUND);
        g2d.fillRect(offsetX, offsetY, cellWidth * boardCols, cellHeight * boardRows);
        
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));
        
        g2d.drawRect(offsetX, offsetY, cellWidth * boardCols, cellHeight * boardRows);
        
        for (int i = 1; i < boardRows; i++) {
            int y = offsetY + i * cellHeight;
            g2d.drawLine(offsetX, y, offsetX + boardCols * cellWidth, y);
        }
        
        for (int j = 1; j < boardCols; j++) {
            int x = offsetX + j * cellWidth;
            g2d.drawLine(x, offsetY, x, offsetY + boardRows * cellHeight);
        }
        
        g2d.setColor(EXIT_COLOR);

        int exitCellX = 0;
        int exitCellY = 0;
        
        if (exitCol > boardCols) { 
            exitCellX = offsetX + boardCols * cellWidth;
            exitCellY = offsetY + (exitRow - 1) * cellHeight;
        } else if (exitCol == 0) { 
            exitCellX = 0;
            exitCellY = offsetY + (exitRow - 1) * cellHeight;
        } else if (exitRow > boardRows) { 
            exitCellX = offsetX + (exitCol - 1) * cellWidth;
            exitCellY = offsetY + boardRows * cellHeight;
        } else if (exitRow == 0) { 
            exitCellX = offsetX + (exitCol - 1) * cellWidth;
            exitCellY = 0;
        }
        
        g2d.fillRect(exitCellX, exitCellY, cellWidth, cellHeight);
        g2d.setColor(EXIT_COLOR.darker());
        g2d.setStroke(new BasicStroke(2)); 
        g2d.setColor(Color.WHITE);
        int fontSize = Math.min(cellWidth, cellHeight) / 3;
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        String exitLabel = "EXIT";
        int textWidth = fm.stringWidth(exitLabel);
        g2d.drawString(exitLabel, exitCellX + (cellWidth - textWidth) / 2, 
                       exitCellY + (cellHeight - fm.getHeight()) / 2 + fm.getAscent());
        
        java.util.Set<Character> drawnPieces = new java.util.HashSet<>();

        for (int r = 1; r <= boardRows; r++) { 
            for (int c = 1; c <= boardCols; c++) { 
                char pieceChar = board.getCharAt(r, c); 
                if (pieceChar != '.' && !drawnPieces.contains(pieceChar)) {
                    drawPiece(g2d, board, pieceChar, r, c, cellWidth, cellHeight, offsetX, offsetY);
                    drawnPieces.add(pieceChar);
                }
            }
        }
        
        drawStepIndicator(g2d, panelWidth, panelHeight);
    }
    
    private void drawStepIndicator(Graphics2D g2d, int panelWidth, int panelHeight) {
        if (solutionPath == null || solutionPath.isEmpty()) return;
        
        g2d.setColor(new Color(98, 85, 101, 150)); 
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        String stepText = "Langkah " + (currentStepIndex + 1) + " dari " + solutionPath.size();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(stepText);
        
        int padding = 10;
        int rectWidth = textWidth + padding * 2;
        int rectHeight = fm.getHeight() + padding;
        
        int rectX = panelWidth - rectWidth - 10;
        int rectY = 10;
        
        g2d.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 10, 10);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(stepText, rectX + padding, rectY + fm.getAscent() + padding/2);
    }

    private void drawPiece(Graphics2D g2d, Board board, char pieceChar, 
                        int startRow, int startCol, 
                        int cellWidth, int cellHeight,
                        int offsetX, int offsetY) {
        
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


        int x = offsetX + (pieceActualCol - 1) * cellWidth; 
        int y = offsetY + (pieceActualRow - 1) * cellHeight; 
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
        fontSize = Math.max(20, fontSize); 

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String label = String.valueOf(pieceChar);
        int textWidth = fm.stringWidth(label);
        
        g2d.drawString(label, x + (width - textWidth) / 2, y + (height - fm.getHeight()) / 2 + fm.getAscent());
    }
}