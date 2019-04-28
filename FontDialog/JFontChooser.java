//
// Name:        Kwok, Amanda
// Project:     #4
// Due:         November 9, 2018
// Course:      CS-2450-01-F18
//
// Description: A program to create a dialog that allows the user to choose a font. 
//              Within the dialog, the user will be able to change the font family, 
//              style, and size, and see a sample of their selections.
//
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class JFontChooser {
    private static Font updatedFont;
    private static JLabel displayText; 
    private static JTextField currentFont, currentStyle, inputSize;
    private static JList<String> fontList, styleList, sizeList;
    private static JScrollPane fontListSP, styleListSP, sizeListSP;
    private static String[] fontNames, fontStyles, fontSizes;
    
    public static Font showDialog(JFrame parent, Font def) {
           
        JDialog dlg = new JDialog(parent, "Font Chooser", true);
        dlg.setLayout(new BorderLayout(10,10));
        dlg.setSize(500, 400);
        
        loadFonts();
        loadStyles();
        loadSizes();
        
        displayText = new JLabel("Sample: The quick brown fox jumps over "
                + "the lazy dog.", SwingConstants.CENTER);
        displayText.setFont(def);
        displayText.setVerticalAlignment(SwingConstants.CENTER);
        
        currentFont = new JTextField();
        currentFont.setSize(150,10);
        currentFont.setText((def.getFamily(Locale.getDefault())));
        currentFont.setEditable(false);
        
        currentStyle = new JTextField();
        currentStyle.setSize(100,10);
        currentStyle.setText(fontStyles[def.getStyle()]);
        currentStyle.setEditable(false);
        
        inputSize = new JTextField();
        inputSize.setSize(50, 10);
        inputSize.setText(String.valueOf(def.getSize2D()));
        inputSize.addActionListener(ae -> {
            try {
                isProperInputSize(parent);
            }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Please enter a valid number "
                        + "when \ntrying to change the size.", "Error", 
                        JOptionPane.INFORMATION_MESSAGE, new ImageIcon("MyDialogs.png"));
            }
        });
        
        JButton ok = new JButton("Ok");
        ok.addActionListener(ae -> {
            try {
                if (isProperInputSize(parent))
                    dlg.setVisible(false);
            } 
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Please enter a valid number "
                        + "when \ntrying to change the size.", "Error", 
                        JOptionPane.INFORMATION_MESSAGE, new ImageIcon("MyDialogs.png"));
            }
        });
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(ae -> {
            updatedFont = null;
            dlg.setVisible(false);
        });
     
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("<html>&nbsp<br>Font Family:",SwingConstants.LEFT), BorderLayout.NORTH);
        left.add(currentFont, BorderLayout.CENTER);
        left.add(fontListSP, BorderLayout.SOUTH);
        
        JPanel middle = new JPanel(new BorderLayout());
        middle.add(new JLabel("<html>&nbsp<br>Font Style:",SwingConstants.LEFT), BorderLayout.NORTH);
        middle.add(currentStyle, BorderLayout.CENTER);
        middle.add(styleListSP, BorderLayout.SOUTH);
        
        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("<html>&nbsp<br>Size:",SwingConstants.LEFT), BorderLayout.NORTH);
        right.add(inputSize,BorderLayout.CENTER);
        right.add(sizeListSP, BorderLayout.SOUTH);
        
        JPanel threeLists = new JPanel();
        threeLists.add(left, BorderLayout.WEST);
        threeLists.add(middle, BorderLayout.CENTER);
        threeLists.add(right, BorderLayout.EAST);
        
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        south.add(ok);
        south.add(cancel);
        
        dlg.add(threeLists, BorderLayout.NORTH);
        dlg.add(new JLabel("<html> &nbsp"), BorderLayout.WEST);
        dlg.add(new JLabel("<html> &nbsp"), BorderLayout.EAST);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.add(displayText,BorderLayout.CENTER);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
             System.out.println("TEST");
        return updatedFont; 
    }
    
    private static void loadFonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNames = e.getAvailableFontFamilyNames();
        
        fontList = new JList<>(fontNames);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontListSP = new JScrollPane(fontList);
        fontListSP.setPreferredSize(new Dimension(150,100));
        fontList.addListSelectionListener(ls -> {
            displayText.setFont(new Font(fontNames[fontList.getSelectedIndex()], 
                    (displayText.getFont()).getStyle(),
                    (displayText.getFont()).getSize()));
            currentFont.setText(fontNames[fontList.getSelectedIndex()]);
        });
    }
    
    private static void loadStyles() {
        fontStyles = new String[] {"Normal", "Bold", "Italic", "Bold-Italic"};
        
        styleList = new JList<>(fontStyles);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleListSP = new JScrollPane(styleList);
        styleListSP.setPreferredSize(new Dimension (100,100));
        styleList.addListSelectionListener(ls -> {
            displayText.setFont(displayText.getFont().deriveFont(styleList.getSelectedIndex()));
            currentStyle.setText(fontStyles[styleList.getSelectedIndex()]);
        });
    }
    
    private static void loadSizes() {
        fontSizes = new String[34];
        for (int i = 0,j=6; i<34; i++,j=j+2)
            fontSizes[i]=String.valueOf(j);
        
        sizeList = new JList<>(fontSizes);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeListSP = new JScrollPane(sizeList);
        sizeListSP.setPreferredSize(new Dimension(50,100));
        sizeList.addListSelectionListener(ls -> {
            int indx = sizeList.getSelectedIndex();
            displayText.setFont(displayText.getFont().deriveFont(Float.parseFloat(fontSizes[indx])));
            inputSize.setText(fontSizes[indx]);
        });
    }

    private static boolean isProperInputSize(JFrame parent) {
        float chosenSize = (float)(Math.round(Float.parseFloat(inputSize.getText()) * 2)/2.0);

        if (chosenSize > 0 ) {
            updatedFont = displayText.getFont().deriveFont(chosenSize);
            displayText.setFont(displayText.getFont().deriveFont(chosenSize));
            inputSize.setText(String.valueOf(chosenSize));
            return true;
        }
        else {
            JOptionPane.showMessageDialog(parent, "Invalid size.", 
                    "About", JOptionPane.INFORMATION_MESSAGE, 
                    new ImageIcon("MyDialogs.png"));
            return false;
        }
    }
}
