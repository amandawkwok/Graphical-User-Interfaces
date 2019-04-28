//
// Name:        Kwok, Amanda
// Project:     #5
// Due:         December 6, 2018
// Course:      CS-2450-01-F18
//
// Description: A program to create the Windows version of Notepad. The user
// is able to designate a file to open as a command line argument.
//
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class JNotepad implements ActionListener{
    private JFrame frame;
    private JTextArea jta;
    private JMenuItem statusBar;
    private JLabel statusBarText;
    private String searchWord, filePath;
    private boolean wrapOn, textAreaChanged, hasCurrentFile, statusBarOn;
    private int lineNum, columnNum, findIndex;
    private Highlighter h;
    private JFileChooser fc;
    private static boolean hasCommandLineFile;
    private static String inputFileName;
    
    public JNotepad(){
        statusBarText = new JLabel("Ln 1, Col 1       ", SwingConstants.RIGHT); 
        // Creating the frame
        frame = new JFrame("Untitled - JNotepad");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("JNotepad.png").getImage());
        frame.setVisible(true);
        
        // Creating the text area
        jta = new JTextArea();
        h = jta.getHighlighter();
        jta.setFont(new Font("Courier New", Font.PLAIN, 12));
        jta.addCaretListener(cl -> {
            try {
                int position = jta.getCaretPosition();
                lineNum = jta.getLineOfOffset(position);
                columnNum = position - jta.getLineStartOffset(lineNum);
                lineNum += 1;
                columnNum += 1;
                statusBarText.setText("Ln " + lineNum +", Col " + columnNum + "       ");
            }
            catch (Exception e) {
                e.getMessage();
            }
        });
        jta.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e){
                h.removeAllHighlights();
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        jta.getDocument().addDocumentListener(new DocListener());
        JScrollPane sp = new JScrollPane(jta);
        sp.setPreferredSize(new Dimension(550,350));
        frame.add(sp);
        
        // Creating the popup menu for cut, copy, and paste
        JPopupMenu popup = new JPopupMenu();
        JMenuItem cutPopup = new JMenuItem("Cut", 'T');
        JMenuItem pastePopup = new JMenuItem("Paste", 'P');
        JMenuItem copyPopup = new JMenuItem ("Copy", 'C');
        popup.add(cutPopup);
        popup.add(pastePopup);
        popup.add(copyPopup);
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
        cutPopup.addActionListener(this);
        pastePopup.addActionListener(this);
        copyPopup.addActionListener(this);
        
        // Creating the menu bar and menu items
        JMenuBar jmb = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        JMenuItem newFile = new JMenuItem ("New", 'N');
        newFile.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
        JMenuItem open = new JMenuItem ("Open...", 'O');
        open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
        JMenuItem save = new JMenuItem ("Save", 'S');
        save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
        JMenuItem saveAs = new JMenuItem ("Save As...", 'A');
        saveAs.setDisplayedMnemonicIndex(5);
        JMenuItem pageSetup = new JMenuItem("Page Setup...", 'U');
        pageSetup.setEnabled(false);
        JMenuItem print = new JMenuItem("Print...", 'P');
        print.setEnabled(false);
        print.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.CTRL_MASK));
        JMenuItem exit = new JMenuItem("Exit", 'X');
        file.add(newFile);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.addSeparator();
        file.add(pageSetup);
        file.add(print);
        file.addSeparator();
        file.add(exit);
        jmb.add(file);
        
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic('E');
        JMenuItem undo = new JMenuItem("Undo", 'U');
        undo.setEnabled(false);
        JMenuItem cut = new JMenuItem ("Cut", 'T');
        cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        JMenuItem copy = new JMenuItem ("Copy", 'C');
        copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        JMenuItem paste = new JMenuItem ("Paste", 'P');
        paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
        JMenuItem delete = new JMenuItem ("Delete", 'L');
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        JMenuItem find = new JMenuItem("Find...", 'F');
        find.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
        JMenuItem findNext = new JMenuItem("Find Next", 'N');
        findNext.setDisplayedMnemonicIndex(5);
        JMenuItem replace = new JMenuItem("Replace...", 'R');
        replace.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_MASK));
        replace.setEnabled(false);
        JMenuItem goTo = new JMenuItem("Go To...", 'G');
        goTo.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.CTRL_MASK));
        goTo.setEnabled(false);
        JMenuItem selectAll = new JMenuItem("Select All", 'A');
        selectAll.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
        JMenuItem timeDate = new JMenuItem("Time/Date", 'D');
        timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        edit.add(undo);
        edit.addSeparator();
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(delete);
        edit.addSeparator();
        edit.add(find);
        edit.add(findNext);
        edit.add(replace);
        edit.add(goTo);
        edit.addSeparator();
        edit.add(selectAll);
        edit.add(timeDate);
        edit.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent arg0) {
                if (jta.getSelectedText() != null) {
                    cut.setEnabled(true);
                    copy.setEnabled(true);
                    delete.setEnabled(true);
                }
                else {
                    cut.setEnabled(false);
                    copy.setEnabled(false);
                    delete.setEnabled(false);
                }
            }
            public void menuDeselected(MenuEvent arg0) {
            }

            public void menuCanceled(MenuEvent arg0) {
            }
        });
        jmb.add(edit);
        
        JMenu format = new JMenu("Format");
        format.setMnemonic('O');
        JMenuItem wordWrap = new JMenuItem("Word Wrap", 'W');
        JMenuItem font = new JMenuItem("Font...", 'F');
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        format.add(wordWrap);
        wrapOn = true;
        format.add(font);
        jmb.add(format);
        
        JMenu view = new JMenu("View");
        view.setMnemonic('V');
        statusBar = new JMenuItem("Status Bar", 'S');
        statusBar.setEnabled(false);
        view.add(statusBar);
        jmb.add(view);
        
        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        JMenuItem viewHelp = new JMenuItem ("View Help", 'H');
        JMenuItem about = new JMenuItem ("About JNotepad", 'A');
        help.add(viewHelp);
        help.addSeparator();
        help.add(about);
        jmb.add(help);
        
        newFile.addActionListener(this);
        open.addActionListener(this);
        save.addActionListener(this);
        saveAs.addActionListener(this);
        exit.addActionListener(this);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        delete.addActionListener(this);
        find.addActionListener(this);
        findNext.addActionListener(this);
        selectAll.addActionListener(this);
        timeDate.addActionListener(this);
        wordWrap.addActionListener(this);
        font.addActionListener(this);
        statusBar.addActionListener(this);
        viewHelp.addActionListener(this);
        about.addActionListener(this);
        frame.setJMenuBar(jmb);
        
        if (hasCommandLineFile) {
            try {
                File inputFile = new File(inputFileName);
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                jta.read(br, null);
                br.close();
                frame.setTitle(inputFileName + " - JNotepad");
                filePath = inputFile.getPath();
                textAreaChanged = false;
                hasCurrentFile = true;
                jta.getDocument().addDocumentListener(new DocListener());
            }
            catch (IOException e) {
                int choice = JOptionPane.showConfirmDialog(frame,"Error reading file. "
                        + "Would you like to create a new file with that name?", "Error", 
                        JOptionPane.YES_NO_OPTION );
                if (choice == JOptionPane.YES_OPTION) {
                    File f = new File(inputFileName);
                    frame.setTitle(inputFileName + " - JNotepad");
                    
                    filePath = f.getPath();
                    BufferedWriter out;
                    try {
                        out = new BufferedWriter(new FileWriter(filePath));
                                            out.write(jta.getText()); 
                    out.close();
                    textAreaChanged = false;
                    hasCurrentFile = true;
                    jta.getDocument().addDocumentListener(new DocListener());
                    } catch (IOException ex) {
                        Logger.getLogger(JNotepad.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    } 
    
    public void actionPerformed(ActionEvent ae) {
        String comStr = ae.getActionCommand();
        
        JDialog dlg = new JDialog(frame, "JNotepad", true);
        dlg.setSize(350,120);
        dlg.setLocationRelativeTo(frame);
        JPanel south = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(al -> {
            saveAs();
        });
        JButton dontSave = new JButton("Don't Save");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(al -> {
            dlg.setVisible(false);
        });
        south.add(saveButton);
        south.add(dontSave);
        south.add(cancel);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.add(new JLabel(("Do you want to save changes to " +  frame.getTitle()
                + "?"), SwingConstants.CENTER));
                
        if (comStr.equals("New")) {
            if (textAreaChanged) {
                dontSave.addActionListener(al -> {
                    jta.setText("");
                    frame.setTitle("Untitled - JNotepad");
                    hasCurrentFile = false;
                    textAreaChanged = false;
                    dlg.setVisible(false);
                });
                dlg.setVisible(true);
            }
            else {
                jta.setText("");
                frame.setTitle("Untitled - JNotepad");
                textAreaChanged = false;
            }     
        }
        else if (comStr.equals("Open...")) {
            if (textAreaChanged) {
                dontSave.addActionListener(al -> {
                    openFile();
                    dlg.setVisible(false);
                });
                dlg.setVisible(true);
            }    
            else {
               openFile();
               jta.getDocument().addDocumentListener(new DocListener());
            }
        }
        else if (comStr.equals("Save")) { 
            if (hasCurrentFile) {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
                    out.write(jta.getText()); 
                    out.close();
                    textAreaChanged = false;
                }
                catch (Exception e) {
                    System.out.println("Error opening or writing to file.");
                }
            }
            else
                saveAs();
        }
        else if (comStr.equals("Save As...")) {
            saveAs();
        }
        else if (comStr.equals("Exit")) {
            if (textAreaChanged) {
                JOptionPane message = new JOptionPane();
                int choice = message.showConfirmDialog(frame, "Do you want to save chang"
                        + "es to " + frame.getTitle().substring(0, frame.getTitle().length() - 11)
                        + "?", "Notepad", message.YES_NO_CANCEL_OPTION);
                if (choice == message.YES_OPTION) {
                    if (hasCurrentFile) {
                        try {
                            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
                            out.write(jta.getText()); 
                            out.close();
                            textAreaChanged = false;
                        }
                        catch (Exception e) {
                            System.out.println("Error opening or writing to file.");
                        }
                    }
                    else 
                        saveAs();
                }
                else if (choice == message.NO_OPTION) {
                    System.exit(0);
                }
            }
            else
                System.exit(0);
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
        else if (comStr.equals("Delete")) {
            jta.replaceSelection("");
        }
        else if (comStr.equals("Find...")) {
            findIndex = 0;
            JDialog findDialog = new JDialog (frame, "Find", false);
            findDialog.setSize(400,120);
            findDialog.setLocationRelativeTo(null);
            findDialog.setVisible(true);
            
            findDialog.add(new JLabel("     Find what:     "), BorderLayout.WEST);
            JTextField searchField = new JTextField(20);
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                  findIndex = 0;
                }
                public void removeUpdate(DocumentEvent e) {
                  findIndex = 0;
                }
                public void insertUpdate(DocumentEvent e) {
                  findIndex = 0;
                }
            });
            findDialog.add(searchField, BorderLayout.CENTER);
            
            JPanel bottom = new JPanel(new FlowLayout());
            JButton findNextButton = new JButton("Find Next");
            findNextButton.addActionListener(al -> {
                searchWord = searchField.getText();
                findWord(searchWord);
                // set the word to a variable and restart index
            });
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener( al -> {
                h.removeAllHighlights();
                findDialog.setVisible(false);
            });
            bottom.add(findNextButton);
            bottom.add(cancelButton);
            findDialog.add(new JLabel("   "), BorderLayout.NORTH);
            findDialog.add(new JLabel("   "), BorderLayout.EAST);
            findDialog.add(bottom, BorderLayout.SOUTH);
        }
        else if (comStr.equals("Find Next")) {
            if (searchWord != null)
                findWord(searchWord);
        }
        else if (comStr.equals("Select All")) {
            jta.selectAll();
        }
        else if (comStr.equals("Time/Date")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a MM/dd/yyyy");  
            LocalDateTime now = LocalDateTime.now();  
            jta.insert(dtf.format(now), jta.getCaretPosition());
        }
        else if (comStr.equals("Word Wrap")) {
            wrapOn = !wrapOn;
            if (wrapOn) {
                statusBar.setEnabled(false);
                jta.setLineWrap(true);
                frame.remove(statusBarText);
                frame.revalidate();
                frame.repaint();
            }
            else {       
                statusBar.setEnabled(true);
                jta.setLineWrap(false);
                if (statusBarOn) {
                    frame.add(statusBarText, BorderLayout.SOUTH);
                    frame.revalidate();
                    frame.repaint();
                }
            }
        }
        else if (comStr.equals("Font...")) {
            Font fontSel = JFontChooser.showDialog(frame, jta.getFont());
            if (fontSel != null)
                jta.setFont(fontSel);
        }
        else if (comStr.equals("Status Bar")) {
            if (!statusBarOn) {
                frame.add(statusBarText, BorderLayout.SOUTH);
                frame.revalidate();
                frame.repaint();
            }
            else {
                frame.remove(statusBarText);
                frame.revalidate();
                frame.repaint();
            }
            statusBarOn = !statusBarOn;
        }
        else if (comStr.equals("View Help")) {
            try{ 
                String url = "https://support.microsoft.com/en-us/help/4466414/w"
                        + "indows-help-in-notepad"; 
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url)); 
            } 
            catch (java.io.IOException e) { 
                System.out.println(e.getMessage()); 
            } 
        }
        else if (comStr.equals("About JNotepad")) {
            JOptionPane.showMessageDialog(frame, "Program Name: JNotepad.java\n\n"
                    + "JNotepad is a program designed to read .txt and \n.java "
                    + "files and display it in the text area.\n (c) Amanda Kwok", 
                    "About JNotepad", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                            new ImageIcon("JNotepad.png").getImage()));
        }
    }
    
    private void saveAs() {                       
        fc = new JFileChooser(); 
        if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(fc.getSelectedFile().getPath()));
                out.write(jta.getText()); 
                out.close(); 
                frame.setTitle(fc.getSelectedFile().getName() + " - JNotepad");
                filePath = fc.getSelectedFile().getPath();
		jta.getDocument().addDocumentListener(new DocListener());
                hasCurrentFile = true;
            } 
            catch (Exception e) {
                 System.out.println(e.getMessage());
            }
        }
    }

    private void openFile() {
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter ("Java File", "java"));
        fc.setFileFilter(new FileNameExtensionFilter("Text File", "txt") );
        
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            try {
                BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                jta.read(br, null);
                br.close();
                frame.setTitle(selectedFile.getName() + " - JNotepad");
                filePath = fc.getSelectedFile().getPath();
                textAreaChanged = false;
                hasCurrentFile = true;
                jta.getDocument().addDocumentListener(new DocListener());
            }
            catch (IOException e) {
                jta.setText("Error opening or reading file.");
                return;
            }
        }
    }
    
    private void findWord (String word) {
        try {
            word = word.toUpperCase();
            int wordLength = word.length();
            String paragraph = jta.getText().toUpperCase();
            h = jta.getHighlighter();
            h.removeAllHighlights();
            int index = paragraph.indexOf(word, findIndex);
            findIndex = index + wordLength;

            if (index == -1) {
                JOptionPane.showMessageDialog (frame, "End of file reached." , "Find", JOptionPane.INFORMATION_MESSAGE);
            }
            if (index >= 0) {
                h.addHighlight(index, index + wordLength, DefaultHighlighter.DefaultPainter);
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(JNotepad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        if (args.length == 1) {
            hasCommandLineFile = true;
            inputFileName = args[0];
        }
        SwingUtilities.invokeLater(() -> new JNotepad());
    }  
    
    private class DocListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            textAreaChanged = true;
            h.removeAllHighlights();
        }
        public void insertUpdate(DocumentEvent e) {
            textAreaChanged = true;
        }
        public void removeUpdate(DocumentEvent e) {
            textAreaChanged = true;
        }
    }
}


