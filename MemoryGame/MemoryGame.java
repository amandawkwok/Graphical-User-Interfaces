//
//      Name:       Kwok, Amanda
//      Homework:   #1
//      Due:        October 29, 2018
//      Course:     CS-2450-01-F18
//      Description: An improvement on the previous MemoryGame with an added menu bar
//                  containing different options. 'Game Timer' controls the timer and is
//                  only available when the timer is running. 'Reveal' shows all the game
//                  and is unabilable in debug mode. 'View Help' displays a dialog on how
//                  to play the game; the timer is suspended until the next tile is clicked.
//

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Timer;

public class MemoryGame implements ItemListener, ActionListener{
    
    private static int argsLength;
    private static String debugMode;
    private final JToggleButton[] tiles;
    private JFrame frame;
    private JMenuItem pause, resume;
    private JToggleButton tb1, tb2;
    private JLabel display;
    private Timer displayTimer, flipTimer, debugTimer;
    private int tilesSelected;
    private long elapsed;
    private boolean aboutClicked;
    private Instant start;
    
    public MemoryGame()
    {
        frame = new JFrame("Memory Game");
        JPanel panel = new JPanel(new GridLayout(3,4,2,2));
        JMenuBar jmb = new JMenuBar();
        
        //Creating the action menu
        JMenu action = new JMenu("Action");
        JMenu gameTimer = new JMenu("Game Timer");
        pause = new JMenuItem("Pause", 'P');
        resume = new JMenuItem("Resume", 'R');
        JMenuItem reveal = new JMenuItem("Reveal", 'R');
        JMenuItem exit = new JMenuItem("Exit", 'X');
        
        //Creating the help menu
        JMenu help = new JMenu("Help");
        JMenuItem viewHelp = new JMenuItem("View Help...",'H');
        JMenuItem about = new JMenuItem("About",'A');
        
        //Adding specifications to components
        action.setMnemonic('A');
        gameTimer.setMnemonic('T');
        pause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        resume.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        help.setMnemonic('H');
        pause.setEnabled(false);
        resume.setEnabled(false);

        //Adding contents to proper submenus
        gameTimer.add(pause);
        gameTimer.add(resume);
        action.add(gameTimer);
        action.add(reveal);
        action.addSeparator();
        action.add(exit);
        help.add(viewHelp);
        help.addSeparator();
        help.add(about);
        jmb.add(action);
        jmb.add(help);
        frame.setJMenuBar(jmb);
        
        // Adding action listeners for the menu items
        pause.addActionListener(this);
        resume.addActionListener(this);
        if (debugMode.equals("debug")) // This feature is disabled in debug mode
            reveal.setEnabled(false);
        else
            reveal.addActionListener(this);
        exit.addActionListener(this);
        viewHelp.addActionListener(this);
        about.addActionListener(this);
        
        // Setting up the frame.
        frame.setSize(700,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("MemoryGame.png").getImage());
        frame.setLocationRelativeTo(null);
        
        // Adding the initial time display.
        display = new JLabel("00:00:00", SwingConstants.CENTER);
        frame.add(display, BorderLayout.NORTH);

        // Array containing icons.
        ImageIcon[] pictures = new ImageIcon[]{new ImageIcon(("1.png")), 
            new ImageIcon(("2.png")),
            new ImageIcon(("3.png")),
            new ImageIcon(("4.png")),
            new ImageIcon(("5.png")),
            new ImageIcon(("6.png")),
            new ImageIcon(("MemoryGame.png"))};
        
        // Array containing identifiers for each tile
        String[] commandID = {"1.png", "2,png","3,png", "4,png", "5.png", "6,png"};
        
        // Adding JToggleButtons to array list and shuffling
        ArrayList<JToggleButton> arrlist = new ArrayList<>(12); 
        tiles = new JToggleButton[12];
        int commandIDindex = 0, picturesIndex=0;    
     
        for (int i = 0; i<12; commandIDindex++, picturesIndex++, i+=2)
        {   
            // Setting icons for each pair of tiles.
            tiles[i] = new JToggleButton(pictures[6]);
            tiles[i].setSelectedIcon(pictures[picturesIndex]);
            tiles[i].setActionCommand(commandID[commandIDindex]);
            tiles[i+1] = new JToggleButton(pictures[6]);
            tiles[i+1].setSelectedIcon(pictures[picturesIndex]);
            tiles[i+1].setActionCommand(commandID[commandIDindex]);
            
            if (debugMode.equals("debug"))
            {
                tiles[i].setDisabledIcon(pictures[6]);
                tiles[i+1].setDisabledIcon(pictures[6]);
            }
            else
            {
                tiles[i].setDisabledIcon(pictures[picturesIndex]);
                tiles[i+1].setDisabledIcon(pictures[picturesIndex]);   
            }
            arrlist.add(tiles[i]);
            arrlist.add(tiles[i+1]);
        }
        Collections.shuffle(arrlist);

        // Adding the shuffled list to the display and array.
        for (int i = 0; i < 12; i++)
        {
            tiles[i]=arrlist.get(i);
            if (debugMode.equals("debug"))
                tiles[i].setSelected(true);
            tiles[i].addItemListener(this);
            panel.add(tiles[i]);
        }
        
        // Creating a timer to flip unmatched tile pairs to the game tile 
        // after 1.5 seconds.
        flipTimer = new Timer (1500, ae -> {
            tb1.setSelected(false);
            tb2.setSelected(false);
            flipTimer.stop();
        });    
        
        // Creating a timer to flip unmatched tile pairs to the original pictures
        // under debug mode.
        debugTimer = new Timer (1500, ae -> {
            tb1.setSelected(true);
            tb2.setSelected(true);
            debugTimer.stop();
        });    
        
        // Creating a timer to display the elapsed time
        start = Instant.now();
        displayTimer = new Timer(1000, ae -> {  
            elapsed = Duration.between(start, Instant.now()).getSeconds();
            display.setText(String.format("%02d:%02d:%02d", elapsed/3600,
                (elapsed%3600)/60, elapsed%60));  
        });
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);       
    }
    
    public void actionPerformed(ActionEvent ae) 
    {
        String comStr = ae.getActionCommand();
        
        if (comStr.equals("Pause"))
        {   
            pause.setEnabled(false);
            displayTimer.stop();
            resume.setEnabled(true);
        }
        else if (comStr.equals("Resume"))
        {
            restartTimer();
        }
        else if (comStr.equals("Reveal"))
        {
            if (displayTimer.isRunning())
            {
                pause.setEnabled(false);
                resume.setEnabled(false);
                displayTimer.stop();
            }
            for (int i = 0; i < 12; i++)
                tiles[i].setEnabled(false);
        }
        else if (comStr.equals("Exit"))
            System.exit(0);
        else if (comStr.equals("View Help..."))
        {
            if (displayTimer.isRunning())
            {
                // Disable 'pause' and 'resume' when timer is not running
                pause.setEnabled(false);
                resume.setEnabled(false);
                displayTimer.stop();
            }
            
            // The timer will be clicked
            aboutClicked = true;
            JOptionPane.showMessageDialog(frame,
                "When a game image is clicked, it will turn into the \n"
                + "actual image of the tile. Once a second tile is \n"
                + "clicked, both tiles remain revealed if the first \n"
                + "tile matches the second; otherwise, they will be \n"
                + "flipped. In debug mode indicated by \"java MemoryGame\n"
                + "debug\", the program will perform in the opposite \n"
                + "manner where the game image will be displayed if \n"
                + "tiles are matched.",
                "Help", JOptionPane.PLAIN_MESSAGE);
        }
        else if (comStr.equals("About"))
        {
            if (displayTimer.isRunning())
                displayTimer.stop();
            
            JOptionPane.showMessageDialog(frame, "Match Pairs is a memory game "+
            "where the user \nmatches pairs of tiles. In debug mode indicated \nby " +
            "\"java MemoryGame debug\", the program will \nperform in the " +
            "opposite manner where the game \nimage will be displayed if " +
            "tiles are matched.\n (c) Amanda Kwok", "About", JOptionPane.INFORMATION_MESSAGE, 
            new ImageIcon("MemoryGame.png"));
            
            if (tilesSelected!=0 && tilesSelected!=12)
                restartTimer();       
        }
    }
    public void restartTimer()
    {
        start = Instant.now();
        long pastElapsed = elapsed;
        // Display the duration from (when the timer is restarted -> current time)+the previous stored time 
        displayTimer = new Timer(1000, t -> {
            long newElapsed=Duration.between(start, Instant.now()).getSeconds();
            display.setText(String.format("%02d:%02d:%02d", (newElapsed+pastElapsed)/3600,
                ((newElapsed+pastElapsed)%3600)/60, (newElapsed+pastElapsed)%60));  
            // Update 'elapsed' to include the new elapsedTime
            elapsed = newElapsed+pastElapsed;
        });
        // Enable 'pause' and 'resume' when timer is running
        displayTimer.start();
        pause.setEnabled(true);
        resume.setEnabled(false);
        aboutClicked=false;
    }
    
    @Override
    public void itemStateChanged(ItemEvent ie) {
        // Prevents user from selecting tiles while the flipTimer is running. 
        if (flipTimer.isRunning())
        {
            ((JToggleButton)ie.getItem()).setSelected(false);
            return;
        }
        // Prevents user from selecting tiles while the debugTimer is running.
        else if (debugTimer.isRunning())
        {
            ((JToggleButton)ie.getItem()).setSelected(true);
            return;
        }

        boolean allFlipped=true;
        tilesSelected++;
        if(aboutClicked)
        {
            aboutClicked=false;
            restartTimer();
        }
        // Starts the display timer when the first tile is clicked
        if (tilesSelected==1)
        {
            start = Instant.now();
            displayTimer.start();
            pause.setEnabled(true);
            resume.setEnabled(false);
        }
     
        if (debugMode.equals("debug"))
        {
            allFlipped=true;
            if (tilesSelected%2!=0)
                tb1 = (JToggleButton)ie.getItem();
            else
            {          
                tb2 = (JToggleButton)ie.getItem();
                if (!(tb1.getActionCommand()).equals(tb2.getActionCommand()))
                   debugTimer.start();
                else // Any clicks on already paired-up tiles will be ignored
                {
                    tb1.setEnabled(false);
                    tb2.setEnabled(false);
                }
            }
            for (int i=0; i<12; i++)
            {
                // If there are any tiles that are any tiles not showing the game icon,
                // then allFlipped=false
                if (tiles[i].isSelected())
                    allFlipped = false;
            }
            if (allFlipped)
            {
                displayTimer.stop();
                // Disable 'pause' and 'resume' when timer is not running
                pause.setEnabled(false);
                resume.setEnabled(false);
            }
        }
        else 
        {
            if (tilesSelected%2!=0)
                tb1 = (JToggleButton)ie.getItem();
            else
            {          
                tb2 = (JToggleButton)ie.getItem();
                if (!(tb1.getActionCommand()).equals(tb2.getActionCommand()))
                    flipTimer.start();
                else // Disable the tiles if they match.
                {               
                    tb1.setSelected(false);                                 
                    tb2.setSelected(false);        
                    tb1.setEnabled(false);         
                    tb2.setEnabled(false);    
                }
            }

            for (int i=0; i<12; i++)
            {
                // If there is at least one tile enabled, the tiles are not all flipped
                if (tiles[i].isEnabled()) 
                    allFlipped=false;
            }       
            
            if (allFlipped)
            {
                displayTimer.stop();
                // Disable 'pause' and 'resume' when timer is not running
                pause.setEnabled(false);
                resume.setEnabled(false);
            }
        }
    }
    
    public static void main(String[] args) 
    {
        argsLength = args.length;
        if (argsLength==1)
            debugMode = args[0];
        else
            debugMode = "";
        SwingUtilities.invokeLater(()-> new MemoryGame());
    }
}

