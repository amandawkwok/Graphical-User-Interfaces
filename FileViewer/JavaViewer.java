//
// Name:        Kwok, Amanda
// Homework:    #3
// Due:         December 3, 2018
// Course:      CS-2450-01-F18
//
// Description: A program designed to read a java source file and diplay its 
// contents in the text area component. Options to cut, copy, and paste are 
// available by right clicking in the pane.
//
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JavaViewer implements ActionListener{
    private JFrame frame;
    private JTextArea jta;
    
    public JavaViewer() {
        
        // Creating the frame
        frame = new JFrame("Java Viewer");
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("JavaViewer.png").getImage());
        frame.setVisible(true);
        
        // Creating the menu bar and menu items
        JMenuBar jmb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu help = new JMenu("Help");
        JMenuItem open = new JMenuItem("Open...", 'O');
        JMenuItem exit = new JMenuItem("Exit", 'X');
        JMenuItem about = new JMenuItem("About", 'A');
        file.setMnemonic('F');
        help.setMnemonic('H');
        open.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
        
        file.add(open);
        file.addSeparator();
        file.add(exit);
        help.add(about);
        jmb.add(file);
        jmb.add(help);
        frame.setJMenuBar(jmb);
        
        open.addActionListener(this);
        exit.addActionListener(this);
        about.addActionListener(this);
        
        // Creating the text area
        jta = new JTextArea();
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jta.setBackground(Color.blue);
        jta.setFont(new Font("Courier New", Font.PLAIN, 12));
        jta.setForeground(Color.white);
        
        JScrollPane sp = new JScrollPane(jta);
        sp.setPreferredSize(new Dimension(550,350));
        frame.add(sp);
        
        // Creating the popup menu for cut, copy, and paste
        JPopupMenu popup = new JPopupMenu();
        JMenuItem cut = new JMenuItem("Cut", 'T');
        JMenuItem paste = new JMenuItem("Paste", 'P');
        JMenuItem copy = new JMenuItem ("Copy", 'C');
        popup.add(cut);
        popup.add(paste);
        popup.add(copy);
        jta.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.isPopupTrigger())
                    popup.show(me.getComponent(), me.getX(), me.getY());
            }
            public void mouseReleased (MouseEvent me) {
                if (me.isPopupTrigger())
                    popup.show(me.getComponent(), me.getX(), me.getY());
            }
        });
        cut.addActionListener(this);
        paste.addActionListener(this);
        copy.addActionListener(this);
        
    }
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();
        if (comStr.equals("Open...")) {
            JTreeFileChooser jtfc = new JTreeFileChooser();

            if (jtfc.showDialog(frame, System.getProperty("user.dir"))) {
                File selection = jtfc.getSelectedFile();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(selection));
                    jta.read(br, null);
                    br.close();
                }
                catch (IOException e) {
                    jta.setText("Error opening or reading file.");
                    return;
                }
           } 
        }
        else if (comStr.equals("Exit")) {
            if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", 
                    "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.exit(0);
        }
        else if (comStr.equals("About")) {
            JOptionPane.showMessageDialog(frame, "Program Name: JavaViewer\n\n"
                    + "Java Viewer is a program designed to read a \njava source "
                    + "file and display it in the text area. \nUsers should be able"
                    + " to cut, copy, and paste.\n\n(c) Amanda Kwok", "About",
                    JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                            new ImageIcon("JavaViewer.png").getImage()));
        }
        else if (comStr.equals("Cut")) {
            jta.cut();
        }
        else if (comStr.equals("Paste")) {
            jta.paste();
        }
        else if (comStr.equals("Copy")) {
            jta.copy();
        }
    }
    public static void main (String[] args) {
        SwingUtilities.invokeLater(() -> new JavaViewer());
    }
}

