//
// Description: A program to showcase different dialogs. Users will have options
//              to change the display text by its font family, style, size, and 
//              foreground color. There is also an option to change the color of
//              the background. 
//
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MyDialogs implements ActionListener{
    private JLabel displayText;
    private JFrame frame;
    
    public MyDialogs(){
        
        // Creating the frame
        frame = new JFrame ("My Dialogs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,300);
        frame.setLocationRelativeTo(null);
        displayText = new JLabel("<html><br>A. Kwok", SwingConstants.CENTER);
        displayText.setVerticalAlignment(SwingConstants.TOP);
        displayText.setFont(new Font("Courier New", Font.PLAIN, 12));
        frame.add(displayText, BorderLayout.CENTER);
        
        // Creating the menu bar and menus
        JMenuBar jmb = new JMenuBar();
        JMenu file = new JMenu ("File");
        JMenuItem open = new JMenuItem ("Open", 'O');
        JMenuItem exit = new JMenuItem("Exit", 'X');
        JMenu format = new JMenu ("Format");
        JMenuItem font = new JMenuItem ("Font", 'F');
        JMenu color = new JMenu ("Color");
        JMenuItem setForeground = new JMenuItem ("Set Foreground", 'F');
        JMenuItem setBackground = new JMenuItem ("Set Background", 'B');
        JMenu help = new JMenu ("Help");
        JMenuItem about = new JMenuItem("About", 'A');

        // Adding specifications to components
        file.setMnemonic('f');
        format.setMnemonic('o');
        color.setMnemonic('c');
        help.setMnemonic('h');
        open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
        setForeground.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.
                CTRL_MASK + ActionEvent.ALT_MASK));
        
        // Adding components to associated menus
        file.add(open);
        file.addSeparator();
        file.add(exit);
        
        format.add(font);
        format.addSeparator();
        format.add(color);
        color.add(setForeground);
        color.add(setBackground);
   
        help.add(about);
        
        // Setting the menu bar
        jmb.add(file);
        jmb.add(format);
        jmb.add(help);
        frame.setJMenuBar(jmb);
        
        // Adding action listeners to the menu items
        open.addActionListener(this);
        exit.addActionListener(this);
        font.addActionListener(this);
        setForeground.addActionListener(this);
        setBackground.addActionListener(this);
        about.addActionListener(this);
        
        frame.setVisible(true);
    }
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();

        if (comStr.equals("Open")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Java File", "java"));
            
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                displayText.setText(chooser.getSelectedFile().getPath());
            }
        }
        else if (comStr.equals("Exit")) {
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?",
                    "Exit", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION)
                System.exit(0);
        }
        else if (comStr.equals("Font")) {
            Font fontSel = JFontChooser.showDialog(frame, displayText.getFont());
            if (fontSel!=null) {
                displayText.setFont(fontSel);
            }
        }
        else if (comStr.equals("Set Foreground")) {
            Color foregroundColor = JColorChooser.showDialog(frame, "Choose Foreground Color", 
                    displayText.getForeground());
            displayText.setForeground(foregroundColor);
        }
        else if (comStr.equals("Set Background")) {
            Color backgroundColor = JColorChooser.showDialog(frame, "Choose Background Color", 
                    frame.getContentPane().getBackground());
            frame.getContentPane().setBackground(backgroundColor);
        }
        else if (comStr.equals("About")) {
            JOptionPane.showMessageDialog(frame, "Program Name: MyDialogs\n"
                    + "MyDialogs is a program designed to showcase different "
                    +"\ntypes of dialogs. Users may use the menu and menu items to"
                    +" \ndisplay the text in a new font, show the path of a file, "
                    +" \nand change the foreground and background color."
                    +"\n(c) Amanda Kwok", "About", JOptionPane.INFORMATION_MESSAGE, 
            new ImageIcon("MyDialogs.png"));
        }
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(() ->new MyDialogs());
    }
    
}
