//A simple Calculator that finds the output of two operands.

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class JCalculator implements ActionListener{

	private JLabel display;
	private String operand1;
	private String operand2;
	private String operator;
	
	public JCalculator()
	{
		operand1="";
		operand2="";
		operator="";
		
		// Create the frame.
		JFrame frame = new JFrame("Calculator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(240, 240);
		frame.setIconImage(new ImageIcon("JCalculator.png").getImage());
		frame.setLocationRelativeTo(null);
		
		// Create the display for the output.
		display = new JLabel("0", JLabel.RIGHT);
		frame.add(display,BorderLayout.NORTH);
		display.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// Creates the keypad.
		JPanel panel = new JPanel(new GridLayout(4,4,4,4));
		JButton[] key = new JButton[16];
		String keypadInput = "789/456x123-0C=+";
		for (int i =0; i<keypadInput.length();i++)
		{
			key[i]=new JButton(Character.toString(keypadInput.charAt(i)));
			key[i].addActionListener(this);
			if ((i>=0 && i<=2) || (i>=4 && i<=6) || (i>=8 && i<=10) || i==12)
				key[i].setActionCommand("operand");
			else if (i==13)
			{
				key[i].setActionCommand("clear");
				key[i].setMnemonic('c');
			}
			else if (i==14)
			{
				key[i].setActionCommand("equals");
				frame.getRootPane().setDefaultButton(key[i]);
			}
			else
				key[i].setActionCommand("operator");	
			panel.add(key[i]);
		}
		frame.add(panel);
		frame.setVisible(true);
	}
	
	// Translates the action performed into an operand or operator. 
	public void actionPerformed(ActionEvent e) 
	{ 
		// Actions for when the user presses the button, [C].
		if((e.getActionCommand()).equals("clear"))
		{
			if ((e.getModifiers() & KeyEvent.CTRL_MASK) > 0 ) 
			{
				display.setText("(c) Amanda Kwok");
				// Sets operand1 and operand2's lengths to greater than 7 to restrict further input.
				operand1 = "resetString";
				operand2 = "resetString";
			}
			else 
			{
				display.setText("0");
				operand1 = "";
				operand2 = "";
				operator = "";
				
			}
		}
		// Determines if the input will be replace or concatenated to the existing number.
		else if (e.getActionCommand().equals("operand"))
		{
			JButton number = (JButton)e.getSource();
			
			if (operand1.length()<8 && operator.equals(""))
			{
				// The input will replace the current operand that contains a leading zero.
				if (operand1.startsWith("0"))
					operand1=number.getText();
				else 
					operand1+=number.getText();
				display.setText(operand1);
					
			}
			else if (operand2.length()<8 && !operator.equals(""))
			{
				if (operand2.startsWith("0"))
					operand2=number.getText();
				else 
					operand2+=number.getText();
				display.setText(operand2);
			}
		} 
		else if (e.getActionCommand().equals("operator"))
		{
			JButton op = (JButton)e.getSource();
			operator = op.getText();
		}
		// Calculates the result of the two operands.
		else if (e.getActionCommand().equals("equals"))
		{
			int op1 = Integer.parseInt(operand1);
			int op2 = Integer.parseInt(operand2);
			int solution = 0;
			
			if (operator.equals("/"))
			{
				if (op2==0)
				{
					display.setText("Div by 0");
					// Restricts any incoming input from replacing the current operands.
					operand1 = "resetString";
					operand2 = "resetString";
				}
				else
				{
					solution=op1/op2;
					display.setText(String.valueOf(solution));
					// Resets the state of the operands.
					operand1 = "";
					operand2 = "";
					operator = "";
				}
			}
			else 
			{
				if (operator.equals("x"))
					solution=op1*op2;
				else if (operator.equals("-"))
					solution=op1-op2;
				else if (operator.equals("+"))
					solution=op1+op2;
				
				// Correct solution will only display if the number of digits is 8 or less.
				if ((solution>=-9999999) && solution<=99999999)
				{
					display.setText(String.valueOf(solution));
					// Resets the state of the operands.
					operand1 = "";
					operand2 = "";
					operator = "";
				}
				else
				{
					display.setText("Overflow");
					// Restricts any incoming input from replacing the current operands.
					operand1 = "resetString";
					operand2 = "resetString";
				}
			}	
		}	
	}
	
	public static void main (String[] args)
	{
		SwingUtilities.invokeLater(()->new JCalculator());
	}
}
