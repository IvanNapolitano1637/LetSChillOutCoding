import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Queens extends JFrame {
	private static final long serialVersionUID = -4984822436001989024L;
    private static final Color COLOR_1 = Color.YELLOW;
    private static final Color COLOR_2 = new Color(139, 69, 19);
    private static final Color SELECTED = Color.RED;
    private static final Color BLACK = Color.BLACK;
    private static final Color RED_SEMAPHORE_COLOR = Color.RED;
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 204);
    private static final Color BACKGROUND_WIN_COLOR = Color.GREEN;
    private static final Color[] queensBoxBackgroundColors = {new Color(230, 230, 230), new Color(255, 240, 200), new Color(245, 235, 220), new Color(255, 255, 180), new Color(230, 210, 255), new Color(200, 220, 255)};
    private static final Color queensBoxBackgroundColor = queensBoxBackgroundColors[5];
    private static final int FRAME_PADDING = 20;
    private static final int MIN_SIDE = 30;
    private static final int MAX_SIDE = 80;
    private static final int CHESSBOARD_DIMENSION = 600;
    private static final double BOX_WIDTH_FACTOR = 1.2;
    private static final int BOX_MARGIN = 15;
    private static final boolean HIDE_QUEENS_BOX = false;
    private static final String SEMAPHORE_BUTTON_LABEL = "Semaphore: ";
    private static final String SEMAPHORE_ACTIVE_VALUE = "On";
    private static final String SEMAPHORE_INACTIVE_VALUE = "Off";
    private static final String DIFFICULTY_BUTTON_LABEL = "Difficulty: ";
    private static final String DIFFICULTY_EASY_VALUE = "Easy";
    private static final String DIFFICULTY_HARD_VALUE = "Hard";
    private static final String COUNTING_QUEENS_BUTTON_LABEL = "Queens: ";
    private static final String BIGGER_BUTTON_LABEL = "Bigger";
    private static final String SMALLER_BUTTON_LABEL = "Smaller";
    private static final String RESTART_BUTTON_LABEL = "Start again!";
    private static final String MESSAGE_TO_THE_WINNER = "You win! :-D";
    private static final String SLASH = "/";
    private int order = 8;
    private int side = 60;
    private boolean semaphoreFlag = true;
    private boolean easy = false;
    private boolean wan = false;
    private boolean limited = false;
    private int numberOfQueens = 0;
    private int maxNumberOfTheQueens = order * order;
    private JPanel boardPanel, boardWrapper, controlsPanel, queensBoxPanel, centerPanel;
    private JButton btnSemaphore, btnDifficulty, btnQueensNumber, btnBigger, btnSmaller, btnRefresh;
    private Cell[][] grid;

    private static class Cell {
        boolean occupied = false;
        boolean underAttack = false;
    }

    private static class QueenIcon extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color outerColor;
        private final Color innerColor;

        QueenIcon(Color outerColor, Color innerColor) {
            this.outerColor = outerColor;
            this.innerColor = innerColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h);
            int cx = w / 2;
            int cy = h / 2;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(outerColor);
            g2.fillOval(cx - size / 3, cy - size / 3, 2 * size / 3, 2 * size / 3);
            g2.setColor(innerColor);
            g2.fillOval(cx - size / 6, cy - size / 6, size / 3, size / 3);
        }
    }

    public Queens() {
        super("Eight Queens");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initGrid();
        boardPanel = new JPanel() {
            private static final long serialVersionUID = -5054399651289891074L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawAllCells(g2);
                g2.dispose();
            }
        };
        boardPanel.setOpaque(true);
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectCell(e.getX(), e.getY());
            }
        });
        boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(BACKGROUND_COLOR);
        boardWrapper.setBorder(null);
        boardWrapper.add(boardPanel, BorderLayout.CENTER);
        queensBoxPanel = new JPanel();
        queensBoxPanel.setBackground(queensBoxBackgroundColor);
        queensBoxPanel.setPreferredSize(new Dimension(150, order * side));
        queensBoxPanel.setVisible(false);
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(true);
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(FRAME_PADDING, FRAME_PADDING, FRAME_PADDING, FRAME_PADDING));
        centerPanel.add(boardWrapper, BorderLayout.CENTER);
        centerPanel.add(queensBoxPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
        controlsPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        btnSemaphore = new JButton(SEMAPHORE_BUTTON_LABEL + SEMAPHORE_ACTIVE_VALUE);
        btnSemaphore.addActionListener(_ -> changeFlagValue());
        controlsPanel.add(btnSemaphore);
        btnDifficulty = new JButton(DIFFICULTY_BUTTON_LABEL + DIFFICULTY_HARD_VALUE);
        btnDifficulty.addActionListener(_ -> changeDifficulty());
        controlsPanel.add(btnDifficulty);
        btnQueensNumber = new JButton();
        btnQueensNumber.addActionListener(_ -> changeTheMaxNumberOfTheQueens());
        controlsPanel.add(btnQueensNumber);
        btnBigger = new JButton(BIGGER_BUTTON_LABEL);
        btnBigger.addActionListener(_ -> bigger());
        controlsPanel.add(btnBigger);
        btnSmaller = new JButton(SMALLER_BUTTON_LABEL);
        btnSmaller.addActionListener(_ -> smaller());
        controlsPanel.add(btnSmaller);
        btnRefresh = new JButton(RESTART_BUTTON_LABEL);
        btnRefresh.addActionListener(_ -> refresh());
        controlsPanel.add(btnRefresh);
        add(controlsPanel, BorderLayout.SOUTH);
        setMaxNumberOfTheQueens();
        setQueensLimitButtonLabel();
        recomputeSide();
        int queensBoxWidth = (int) (side * BOX_WIDTH_FACTOR);
        queensBoxPanel.setPreferredSize(new Dimension(queensBoxWidth, order * side));
        queensBoxPanel.setVisible(limited && !HIDE_QUEENS_BOX);
        revalidate();
        repaint();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initGrid() {
        maxNumberOfTheQueens = limited ? order : order * order;
        grid = new Cell[order][order];
        for (int r = 0; r < order; r++) {
            for (int c = 0; c < order; c++) {
                grid[r][c] = new Cell();
            }
        }
        numberOfQueens = 0;
    }

    private void setMaxNumberOfTheQueens() {
        maxNumberOfTheQueens = limited ? order : order * order;
    }

    private void setQueensLimitButtonLabel() {
        if (btnQueensNumber != null) {
            btnQueensNumber.setText(COUNTING_QUEENS_BUTTON_LABEL + numberOfQueens + SLASH + maxNumberOfTheQueens);
        }
    }

    private void recomputeSide() {
        side = Math.max(MIN_SIDE, Math.min(MAX_SIDE, CHESSBOARD_DIMENSION / order));
        boardPanel.setPreferredSize(new Dimension(order * side, order * side));
        boardPanel.setMinimumSize(new Dimension(order * side, order * side));
        boardPanel.setMaximumSize(new Dimension(order * side, order * side));
        boardPanel.setSize(order * side, order * side);
        boardPanel.revalidate();
    }

    private void setBackgroundColor(Color color) {
        boardWrapper.setBackground(color);
        if (centerPanel != null) centerPanel.setBackground(color);
        boardPanel.setBackground(color);
        getContentPane().setBackground(color);
        repaint();
    }

    private void drawAllCells(Graphics g) {
        for (int r = 0; r < order; r++) {
            for (int c = 0; c < order; c++) {
                drawCell(g, r, c);
            }
        }
    }

    private void drawCell(Graphics g, int row, int col) {
        g.setColor(((row + col) % 2 == 0) ? COLOR_1 : COLOR_2);
        g.fillRect(col * side, row * side, side, side);
        Cell cell = grid[row][col];
        if (cell.occupied) {
        	drawQueen(g, row, col);
        }else if (easy && cell.underAttack) {
        	drawUnderAttackCell(g, row, col);
        }
    }

    private void drawQueen(Graphics g, int row, int col) {
        int cx = col * side + side / 2;
        int cy = row * side + side / 2;
        g.setColor(SELECTED);
        g.fillOval(cx - side / 3, cy - side / 3, 2 * side / 3, 2 * side / 3);
        g.setColor(BLACK);
        g.fillOval(cx - side / 6, cy - side / 6, side / 3, side / 3);
    }

    private void drawUnderAttackCell(Graphics g, int row, int col) {
        int cx = col * side + side / 2;
        int cy = row * side + side / 2;
        g.setColor(BLACK);
        g.fillOval(cx - side / 8, cy - side / 8, side / 4, side / 4);
    }

    private void selectCell(int x, int y) {
        if (wan) return;
        int col = x / side;
        int row = y / side;
        if (row < 0 || col < 0 || row >= order || col >= order) return;
        if ((numberOfQueens < maxNumberOfTheQueens) || (grid[row][col].occupied && numberOfQueens == maxNumberOfTheQueens)) {
            numberOfQueens += grid[row][col].occupied ? -1 : 1;
            grid[row][col].occupied = !grid[row][col].occupied;
            setUnderAttackValues();
            boardPanel.repaint();
            setQueensLimitButtonLabel();
            boolean valid = checkQueensPositions();
            setBackgroundColor((!valid && semaphoreFlag) ? RED_SEMAPHORE_COLOR : BACKGROUND_COLOR);
            if (numberOfQueens == order && valid) {
                setBackgroundColor(BACKGROUND_WIN_COLOR);
                wan = true;
                JOptionPane.showMessageDialog(this, MESSAGE_TO_THE_WINNER);
            }
        }
        updateQueensBox();
    }

    private void refresh() {
        wan = false;
        numberOfQueens = 0;
        initGrid();
        setMaxNumberOfTheQueens();
        setQueensLimitButtonLabel();
        setBackgroundColor(BACKGROUND_COLOR);
        boardPanel.repaint();
        updateQueensBox();
    }

    private void setUnderAttackValues() {
        for (int r = 0; r < order; r++) for (int c = 0; c < order; c++) grid[r][c].underAttack = false;
        for (int r1 = 0; r1 < order; r1++) {
            for (int c1 = 0; c1 < order; c1++) {
                if (!grid[r1][c1].occupied) continue;
                for (int r2 = 0; r2 < order; r2++) {
                    for (int c2 = 0; c2 < order; c2++) {
                        if (r1 == r2 && c1 == c2) continue;
                        if (r1 == r2 || c1 == c2 || Math.abs(r1 - r2) == Math.abs(c1 - c2)) {
                            grid[r2][c2].underAttack = true;
                        }
                    }
                }
            }
        }
    }

    private boolean checkQueensPositions() {
        for (int r = 0; r < order; r++) {
            for (int c = 0; c < order; c++) {
                if (grid[r][c].occupied && grid[r][c].underAttack) return false;
            }
        }
        return true;
    }

    private void changeFlagValue() {
        semaphoreFlag = !semaphoreFlag;
        btnSemaphore.setText(SEMAPHORE_BUTTON_LABEL + (semaphoreFlag ? SEMAPHORE_ACTIVE_VALUE : SEMAPHORE_INACTIVE_VALUE));
        setBackgroundColor((!checkQueensPositions() && semaphoreFlag) ? RED_SEMAPHORE_COLOR : BACKGROUND_COLOR);
        boardPanel.repaint();
    }

    private void changeDifficulty() {
        easy = !easy;
        btnDifficulty.setText(DIFFICULTY_BUTTON_LABEL + (easy ? DIFFICULTY_EASY_VALUE : DIFFICULTY_HARD_VALUE));
        boardPanel.repaint();
    }

    private void changeTheMaxNumberOfTheQueens() {
        if (numberOfQueens <= order) {
            limited = !limited;
            setMaxNumberOfTheQueens();
            setQueensLimitButtonLabel();
            if(!HIDE_QUEENS_BOX) {
                queensBoxPanel.setPreferredSize(new Dimension((int)(side * BOX_WIDTH_FACTOR), order * side));
                queensBoxPanel.setVisible(limited);
	            updateQueensBox();
	            resizeWindow();
            } else {
                queensBoxPanel.setVisible(false);
            }
            revalidate();
            repaint();
        }
    }

    private void bigger() {
        order++;
        recomputeSide();
        int queensBoxWidth = (int) (side * BOX_WIDTH_FACTOR);
        queensBoxPanel.setPreferredSize(new Dimension(queensBoxWidth, order * side));
        refresh();
        updateQueensBox();
        resizeWindow();
        revalidate();
        repaint();
    }

    private void smaller() {
        if (order > 1) {
            order--;
            recomputeSide();
            int queensBoxWidth = (int) (side * BOX_WIDTH_FACTOR);
            queensBoxPanel.setPreferredSize(new Dimension(queensBoxWidth, order * side));
            refresh();
            updateQueensBox();
            resizeWindow();
            revalidate();
            repaint();
        }
    }

    private void resizeWindow() {
        int boardSize = order * side;
        int queensBoxWidth = (int) (side * BOX_WIDTH_FACTOR);
        int limitedExtraWidth = (limited && !HIDE_QUEENS_BOX) ? (queensBoxWidth + BOX_MARGIN) : 0;
        int width = boardSize + FRAME_PADDING * 2 + limitedExtraWidth;
        int height = boardSize + controlsPanel.getPreferredSize().height + FRAME_PADDING * 2;
        Insets insets = getInsets();
        width += insets.left + insets.right;
        height += insets.top + insets.bottom;
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    private void updateQueensBox() {
        if (!limited) {
            queensBoxPanel.removeAll();
            queensBoxPanel.revalidate();
            queensBoxPanel.repaint();
            return;
        }
        queensBoxPanel.removeAll();
        queensBoxPanel.setLayout(new GridLayout(order, 1, 5, 5));
        int remaining = maxNumberOfTheQueens - numberOfQueens;
        for (int i = 0; i < remaining; i++) {
            QueenIcon icon = new QueenIcon(SELECTED, BLACK);
            icon.setPreferredSize(new Dimension(side, side));
            queensBoxPanel.add(icon);
        }
        queensBoxPanel.revalidate();
        queensBoxPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Queens::new);
    }
}