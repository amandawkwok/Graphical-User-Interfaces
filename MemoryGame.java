//
//      Name:        Kwok, Amanda
//      Project:     3
//      Due:         October 17, 2018
//      Course:      CS-2450-01-F18
//
//      Description: 
//                   Match Pairs is a memory game where the user matches pairs 
//                   of tiles. When a game image is clicked, it will turn into 
//                   the actual image of the tile. Once a second tile is clicked, 
//                   both tiles remain revealed if the first tile matches the second;
//                   otherwise, they will be flipped. In debug mode indicated by 
//                   "java MemoryGame debug", the program will perform in the 
//                   opposite manner where the game image will be displayed if 
//                   tiles are matched.
//                  

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Timer;

public class MemoryGame implements ItemListener{
    
    private static int argsLength;
    private static String debugMode;
    private final JToggleButton[] tiles;
    private JToggleButton tb1;
    private JToggleButton tb2;
    private JLabel display;
    private Timer displayTimer;
    private Timer flipTimer;
    private Timer debugTimer;
    private int tilesSelected;

    
    public MemoryGame()
    {
        JFrame frame = new JFrame("Memory Game");
        JPanel panel = new JPanel(new GridLayout(3,4,2,2));
        
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
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);       
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
        
        // Starts the display timer when the first tile is clicked
        if (tilesSelected==1)
        {
            Instant start = Instant.now();
            displayTimer = new Timer(1000, ae -> {  
                long elapsed = Duration.between(start, Instant.now()).getSeconds();
                display.setText(String.format("%02d:%02d:%02d", elapsed/3600,
                    (elapsed%3600)/60, elapsed%60));  
            });
            displayTimer.start();  
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
                displayTimer.stop();
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
                displayTimer.stop();
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
