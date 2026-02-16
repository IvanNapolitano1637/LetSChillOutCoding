import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.List;

public class ModInverseGame extends JFrame{
    private static final long serialVersionUID = 5273103038785747040L;
    private final int MOD;
    private final int MAX_NUMBER_OF_ERRORS;
    private final static Color ACTIVE_COLOR = Color.YELLOW;
    private final static Color NEXT_BASE_COLOR = Color.CYAN;
    private final static Color TO_DO_SOON_COLOR = Color.ORANGE;
    private final static Color DONE_COLOR = Color.GREEN;
    private final static Color TO_DO_BY_OPPOSITE_COLOR = Color.MAGENTA;
    private final String NAME_FONT = "Arial";
    private final int STYLE_FONT = Font.BOLD;
    private final int SIZE_FONT = 16;
    private final int NUMBERS_STYLE = Font.PLAIN;
    private final int NUMBERS_FONT = 14;
    private final int WIDTH = 1200;
    private final int HEIGHT = 750;
    private final int NUMBER_OF_COLUMNS = 12;
    private final int VERTICAL_SCROLL_BAR_UNIT_INCREMENT = 50;
    private final String GAME_TITLE = "Inverse modulus ";
    private final String SCORE_LABEL_NAME = "Couples found: ";
    private final String HIGHLIGHT_BUTTON_NAME = "Highlight";
    private final String CHECK_OPPOSITE_FLAG_NAME = "Check opposite";
    private final String CONFIGURE_BUTTON_NAME = "Chosen bases:";
    private final String ERROR_LABEL_NAME = "Errors: ";
    private final String RESET_BUTTON_NAME = "Reset";
    private final String MESSAGE_TO_THE_WINNER = "You have completed the game! :-D All pairs found! :-D";
    private final String NAME_OF_THE_MESSAGE_DIALOG_FOR_YHE_WINNER = "Compliments!";
    private final String MESSAGE_TO_THE_LOSER = "You loose!";
    private final String NAME_OF_THE_MESSAGE_DIALOG_FOR_YHE_LOSER = "Oh shit!";
    private final String NAME_OF_THE_CHANGING_BASES_INPUT_DIALOG = "Bases configuration";
    private final String MESSAGE_FOR_THE_CHANGER_OF_BASES = "Select bases to use for Highlight:";
    private final String NAME_OF_SAVE_BUTTON = "Save";
    private final String NAME_OF_LOAD_BUTTON = "Load";
    private final String MESSAGE_FOR_SUCCESSFUL_SAVING_GAME = "Game successfully saved!";
    private final String MESSAGE_FOR_FAILED_SAVING_GAME = "Error saving game: ";
    private final String MESSAGE_FOR_SUCCESSFUL_LOADING_GAME = "Game successfully loaded!";
    private final String MESSAGE_FOR_FAILED_LOADING_GAME = "Error loading game: ";
    private final String SAVING_GAME_FILE_NAME = "savegame.dat";
    private JButton[] buttons;
    private JButton firstSelected = null;
    private JButton highlightButton;
    private JButton resetButton;
    private JButton configureButton;
    private JButton saveButton;
    private JButton loadButton;
    private JLabel scoreLabel;
    private JLabel errorsLabel;
    private JCheckBox checkOppositeNumbers;
    private Map<Integer, Integer> inverses = new HashMap<>();
    private int firstValue = -1;
    private int score = 0;
    private int totalPairs;
    private boolean highlightActive = false;
    private Color defaultColor;
    private int[] BASES = {2,3};
    private int errors = 0;
    private boolean skipConfigureOnInit = false;
    private static final boolean USE_FILE_CHOOSER = false;

    public ModInverseGame(int mod, int maxErrors){
        this.MOD = mod;
        this.MAX_NUMBER_OF_ERRORS = maxErrors;
        this.buttons = new JButton[MOD - 1];
        this.skipConfigureOnInit = false;
        initUI();
    }

    public ModInverseGame(int mod, int maxErrors, int[] loadedBases){
        this.MOD = mod;
        this.MAX_NUMBER_OF_ERRORS = maxErrors;
        this.BASES = (loadedBases != null) ? loadedBases.clone() : this.BASES;
        this.buttons = new JButton[MOD - 1];
        this.skipConfigureOnInit = true;
        initUI();
    }

    private void initUI(){
        setTitle(GAME_TITLE + MOD);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        computeInverses();
        computeTotalPairs();
        scoreLabel = new JLabel(SCORE_LABEL_NAME + "0/" + totalPairs);
        scoreLabel.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        errorsLabel = new JLabel(ERROR_LABEL_NAME + "0");
        errorsLabel.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        highlightButton = new JButton(HIGHLIGHT_BUTTON_NAME);
        highlightButton.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        highlightButton.addActionListener(_ -> toggleHighlight());
        resetButton = new JButton(RESET_BUTTON_NAME);
        resetButton.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        resetButton.addActionListener(_ -> resetGame());
        configureButton = new JButton(CONFIGURE_BUTTON_NAME);
        configureButton.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        configureButton.addActionListener(_ -> configureBases());
        saveButton = new JButton(NAME_OF_SAVE_BUTTON);
        saveButton.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        saveButton.addActionListener(_ -> save());
        loadButton = new JButton(NAME_OF_LOAD_BUTTON);
        loadButton.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        loadButton.addActionListener(_ -> load());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        add(topPanel, BorderLayout.NORTH);
        int rows = (int) Math.ceil((double) (MOD - 1)/NUMBER_OF_COLUMNS);
        JPanel grid = new JPanel(new GridLayout(rows, NUMBER_OF_COLUMNS, 5, 5));
        for(int i = 1; i < MOD; i++){
            JButton btn = new JButton(String.valueOf(i));
            btn.setMinimumSize(new Dimension(50, 40));
            btn.setPreferredSize(new Dimension(60, 45));
            btn.setFont(new Font(NAME_FONT, NUMBERS_STYLE, Math.max(NUMBERS_FONT, 12)));
            btn.putClientProperty("value", i);
            btn.setBackground(defaultColor);
            btn.setFocusPainted(false);
            btn.setFont(new Font(NAME_FONT, NUMBERS_STYLE, NUMBERS_FONT));
            int value = i;
            btn.addActionListener(_ -> handleClick(btn, value));
            buttons[i - 1] = btn;
            grid.add(btn);
        }
        defaultColor = buttons[0].getBackground();
        JScrollPane scrollPane = new JScrollPane(grid);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_BAR_UNIT_INCREMENT);
        if(!skipConfigureOnInit){
            configureBases();
        }else{
            updateLegend();
        }
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        checkOppositeNumbers = new JCheckBox(CHECK_OPPOSITE_FLAG_NAME);
        checkOppositeNumbers.setFont(new Font(NAME_FONT, STYLE_FONT, SIZE_FONT));
        checkOppositeNumbers.addActionListener(_ -> checkOppositeNumbers());
        topPanel.add(scoreLabel);
        topPanel.add(highlightButton);
        topPanel.add(checkOppositeNumbers);
        topPanel.add(configureButton);
        topPanel.add(errorsLabel);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        topPanel.add(resetButton);
    }

    private String getStateFromColor(Color c){
        if(c.equals(DONE_COLOR)){
            return "DONE";
        }
        if(c.equals(ACTIVE_COLOR)){
            return "ACTIVE";
        }
        if(c.equals(NEXT_BASE_COLOR)){
            return "NEXT_BASE";
        }
        if(c.equals(TO_DO_SOON_COLOR)){
            return "TO_DO_SOON";
        }
        if(c.equals(TO_DO_BY_OPPOSITE_COLOR)){
            return "TO_DO_BY_OPPOSITE";
        }
        return "DEFAULT";
    }

    private Color getColorFromState(String state){
        switch (state){
            case "DONE": return DONE_COLOR;
            case "ACTIVE": return ACTIVE_COLOR;
            case "NEXT_BASE": return NEXT_BASE_COLOR;
            case "TO_DO_SOON": return TO_DO_SOON_COLOR;
            case "TO_DO_BY_OPPOSITE": return TO_DO_BY_OPPOSITE_COLOR;
            default: return defaultColor;
        }
    }

    private void checkOppositeNumbers(){
        if(checkOppositeNumbers == null){
            return;
        }
        if(checkOppositeNumbers.isSelected()){
            for(JButton firstButton : buttons){
                int val_1 = (int) firstButton.getClientProperty("value");
                for(JButton latterButton : buttons){
                    int val_2 = (int) latterButton.getClientProperty("value");
                    if(val_1 + val_2 == MOD){
                        if(firstButton.getBackground().equals(DONE_COLOR)){
                            if(latterButton.getBackground().equals(defaultColor)){
                                latterButton.setBackground(TO_DO_BY_OPPOSITE_COLOR);
                            }
                        }
                    }
                }
            }
        }else{
            for(JButton btn : buttons){
                if(btn.getBackground().equals(TO_DO_BY_OPPOSITE_COLOR)){
                    btn.setBackground(defaultColor);
                }
            }
        }
    }

    private void computeInverses(){
        for(int a = 1; a < MOD; a++){
            for(int b = 1; b < MOD; b++){
                if((a*b)%MOD == 1){
                    inverses.put(a, b);
                    break;
                }
            }
        }
    }

    private void computeTotalPairs(){
        Set<Integer> visited = new HashSet<>();
        int pairs = 0;
        for(int a = 1; a < MOD; a++){
            if(!visited.contains(a)){
                int b = inverses.get(a);
                pairs++;
                visited.add(a);
                visited.add(b);
            }
        }
        totalPairs = pairs;
    }

    private void handleClick(JButton btn, int value){
        if(firstSelected == null){
            if(btn.getBackground().equals(DONE_COLOR)){
                return;
            }
            firstSelected = btn;
            firstValue = value;
            btn.setBackground(ACTIVE_COLOR);
            return;
        }
        int inverse = inverses.get(firstValue);
        if((value == inverse && btn != firstSelected) || (firstSelected == btn && firstValue == inverse)){
            btn.setBackground(DONE_COLOR);
            firstSelected.setBackground(DONE_COLOR);
            btn.setText(value + "×" + firstValue);
            firstSelected.setText(firstValue + "×" + value);
            score++;
            scoreLabel.setText(SCORE_LABEL_NAME + score + "/" + totalPairs);
            if(score == totalPairs){
                JOptionPane.showMessageDialog(this, MESSAGE_TO_THE_WINNER, NAME_OF_THE_MESSAGE_DIALOG_FOR_YHE_WINNER, JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            if(!btn.getBackground().equals(DONE_COLOR)){
                btn.setBackground(defaultColor);
            }
            if(!firstSelected.getBackground().equals(DONE_COLOR)){
                firstSelected.setBackground(defaultColor);
            }
            errorsLabel.setText(ERROR_LABEL_NAME + ++errors);
            if(errors > MAX_NUMBER_OF_ERRORS){
                JOptionPane.showMessageDialog(this, MESSAGE_TO_THE_LOSER, NAME_OF_THE_MESSAGE_DIALOG_FOR_YHE_LOSER, JOptionPane.INFORMATION_MESSAGE);
                resetGame();
            }
        }
        updateHighlightColors();
        checkOppositeNumbers();
        firstSelected = null;
        firstValue = -1;
    }

    private void toggleHighlight(){
        highlightActive = !highlightActive;
        highlightButton.setBackground(highlightActive ? ACTIVE_COLOR : defaultColor);
        updateHighlightColors();
        checkOppositeNumbers();
    }

    private void updateHighlightColors(){
        if(!highlightActive){
            for(JButton btn : buttons){
                Color c = btn.getBackground();
                if(!c.equals(DONE_COLOR)){
                    btn.setBackground(defaultColor);
                }
            }
            return;
        }
        Set<Integer> greenValues = new HashSet<>();
        for(JButton btn : buttons){
            if(btn.getBackground().equals(DONE_COLOR)){
                greenValues.add((int) btn.getClientProperty("value"));
            }
        }
        for(JButton btn : buttons){
            int val = (int) btn.getClientProperty("value");
            if(btn.getBackground().equals(DONE_COLOR)){
                continue;
            }
            boolean marked = false;
            int temp = val;
            while(temp > 1 && temp%2 == 0){
                temp /= 2;
                if(greenValues.contains(temp)){
                    btn.setBackground(NEXT_BASE_COLOR);
                    marked = true;
                    break;
                }
            }
            if(marked){
                continue;
            }
            List<int[]> exponents = generateExponentCombinations(BASES, val);
            for(int[] powers : exponents){
                int divisor = 1;
                for(int i = 0; i < BASES.length; i++){
                    divisor *= Math.pow(BASES[i], powers[i]);
                }
                if(divisor <= 1 || val%divisor != 0){
                    continue;
                }
                int candidate = val/divisor;
                if(greenValues.contains(candidate)){
                    btn.setBackground(TO_DO_SOON_COLOR);
                    marked = true;
                    break;
                }
            }
            if(!marked){
                btn.setBackground(defaultColor);
            }
        }
    }

    private List<int[]> generateExponentCombinations(int[] bases, int maxVal){
        List<int[]> result = new ArrayList<>();
        generateExponentCombinationsRecursive(bases, maxVal, 0, new int[bases.length], result);
        return result;
    }

    private void generateExponentCombinationsRecursive(int[] bases, int maxVal, int index, int[] current, List<int[]> result){
        if(index == bases.length){
            int product = 1;
            for(int i = 0; i < bases.length; i++){
                product *= Math.pow(bases[i], current[i]);
            }
            if(product <= maxVal && product > 1){
                result.add(current.clone());
            }
            return;
        }
        int maxExp = 0;
        int base = bases[index];
        int productSoFar = 1;
        for(int i = 0; i < index; i++){
            productSoFar *= Math.pow(bases[i], current[i]);
        }
        while(productSoFar*Math.pow(base, maxExp) <= maxVal){
            current[index] = maxExp;
            generateExponentCombinationsRecursive(bases, maxVal, index + 1, current, result);
            maxExp++;
        }
    }

    private void resetGame(){
        for(int i = 0; i < buttons.length; i++){
            buttons[i].setBackground(defaultColor);
            buttons[i].setText(String.valueOf(i + 1));
            buttons[i].putClientProperty("value", i + 1);
        }
        firstSelected = null;
        firstValue = -1;
        score = 0;
        errors = 0;
        scoreLabel.setText(SCORE_LABEL_NAME + "0/" + totalPairs);
        errorsLabel.setText(ERROR_LABEL_NAME + "0");
        highlightActive = false;
        highlightButton.setBackground(defaultColor);
        updateLegend();
        checkOppositeNumbers.setSelected(false);
    }

    private void configureBases(){
        String[] options ={"2", "2,3", "2,3,5", "2,3,5,7"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                MESSAGE_FOR_THE_CHANGER_OF_BASES,
                NAME_OF_THE_CHANGING_BASES_INPUT_DIALOG,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if(choice != null){
            List<Integer> chosen = new ArrayList<>();
            String[] tokens = choice.split(",");
            for(String t : tokens){
                chosen.add(Integer.parseInt(t.trim()));
            }
            BASES = chosen.stream().mapToInt(Integer::intValue).toArray();
            configureButton.setText(CONFIGURE_BUTTON_NAME + choice);
        }
        updateLegend();
        toggleHighlight();
        toggleHighlight();
    }

    private void updateLegend(){
        StringBuilder sb = new StringBuilder();
        for(int b : BASES){
            sb.append(b).append(" ");
        }
    }

    private void save(){
        if(USE_FILE_CHOOSER){
            JFileChooser fileChooser = new JFileChooser();
            if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                try(ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(fileChooser.getSelectedFile()))){
                    writeGameState(out);
                    JOptionPane.showMessageDialog(this, MESSAGE_FOR_SUCCESSFUL_SAVING_GAME);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this, MESSAGE_FOR_FAILED_SAVING_GAME + ex.getMessage());
                }
            }
        }else{
            try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVING_GAME_FILE_NAME))){
                writeGameState(out);
                JOptionPane.showMessageDialog(this, MESSAGE_FOR_SUCCESSFUL_SAVING_GAME);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, MESSAGE_FOR_FAILED_SAVING_GAME + ex.getMessage());
            }
        }
    }

    private void writeGameState(ObjectOutputStream out) throws Exception{
        out.writeObject(MOD);
        out.writeObject(score);
        out.writeObject(errors);
        out.writeObject(BASES);
        out.writeObject(highlightActive);
        out.writeObject(checkOppositeNumbers.isSelected());
        String[] texts = new String[buttons.length];
        String[] states = new String[buttons.length];
        for(int i = 0; i < buttons.length; i++){
            texts[i] = buttons[i].getText();
            states[i] = getStateFromColor(buttons[i].getBackground());
        }
        out.writeObject(texts);
        out.writeObject(states);
    }

    private void load(){
        if(USE_FILE_CHOOSER){
            JFileChooser fileChooser = new JFileChooser();
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                try(ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream(fileChooser.getSelectedFile()))){
                    readGameState(in);
                    JOptionPane.showMessageDialog(this, MESSAGE_FOR_SUCCESSFUL_LOADING_GAME);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this, MESSAGE_FOR_FAILED_LOADING_GAME + ex.getMessage());
                }
            }
        }else{
            try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVING_GAME_FILE_NAME))){
                readGameState(in);
                JOptionPane.showMessageDialog(this, MESSAGE_FOR_SUCCESSFUL_LOADING_GAME);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, MESSAGE_FOR_FAILED_LOADING_GAME + ex.getMessage());
            }
        }
    }

    private void readGameState(ObjectInputStream in) throws Exception{
        int loadedMod = (Integer) in.readObject();
        int loadedScore = (Integer) in.readObject();
        int loadedErrors = (Integer) in.readObject();
        int[] loadedBases = (int[]) in.readObject();
        boolean loadedHighlight = (Boolean) in.readObject();
        boolean loadedCheckOpposite = (Boolean) in.readObject();
        String[] texts = (String[]) in.readObject();
        String[] states = (String[]) in.readObject();
        if(loadedMod != MOD){
            SwingUtilities.invokeLater(() ->{
                ModInverseGame newGame = new ModInverseGame(loadedMod, MAX_NUMBER_OF_ERRORS, loadedBases);
                newGame.applyLoadedState(loadedScore, loadedErrors, loadedHighlight, loadedCheckOpposite, texts, states, loadedBases);
            });
            dispose();
        }else{
            applyLoadedState(loadedScore, loadedErrors, loadedHighlight, loadedCheckOpposite, texts, states, loadedBases);
        }
    }

    private void applyLoadedState(int loadedScore, int loadedErrors, boolean loadedHighlight, boolean loadedCheckOpposite,
            String[] texts, String[] states, int[] loadedBases){
    	score = loadedScore;
    	errors = loadedErrors;
    	highlightActive = loadedHighlight;
    	checkOppositeNumbers.setSelected(loadedCheckOpposite);
    	if(loadedBases != null){
    		BASES = loadedBases.clone();
    	}
    	if(texts != null && states != null){
    		int n = Math.min(buttons.length, texts.length);
    		for(int i = 0; i < n; i++){
    			buttons[i].setText(texts[i]);
    			buttons[i].setBackground(getColorFromState(states[i]));
    		}
    	}
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < BASES.length; i++){
    		if(i > 0){
                sb.append(",");
            }
    		sb.append(BASES[i]);
    	}
    	configureButton.setText(CONFIGURE_BUTTON_NAME + sb.toString());
    	scoreLabel.setText(SCORE_LABEL_NAME + score + "/" + totalPairs);
    	errorsLabel.setText(ERROR_LABEL_NAME + errors);
    	highlightButton.setBackground(highlightActive ? ACTIVE_COLOR : defaultColor);
    	updateHighlightColors();
    	checkOppositeNumbers();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new ModInverseGame(97, 10000));
    }
}