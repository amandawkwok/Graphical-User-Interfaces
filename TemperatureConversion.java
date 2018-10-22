class JTemperature{
    
    private JTextField userInput;
    private JLabel name;
 
    JTemperature() {

        // Create a new JFrame container. 
        JFrame jfrm = new JFrame("Temperature Converter");
        // Give the frame an initial size. 
        jfrm.setSize(240, 120);
        // Terminate the program when the user closes the application. 
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Adding the name.        
        name = new JLabel("(c) A. Kwok", SwingConstants.CENTER);
        jfrm.add(name, BorderLayout.NORTH);
       
        // Labeling instructions for user input. 
        JLabel enter = new JLabel("  Enter: ", SwingConstants.RIGHT);
        jfrm.add(enter, BorderLayout.WEST);
        
        // Creating a text field for user input.
        userInput = new JTextField("",1);
        jfrm.add(userInput,BorderLayout.CENTER);
        
        // Labeling Farenheit units.
        JLabel degreesF = new JLabel(" degrees F  ", SwingConstants.LEFT);
        jfrm.add(degreesF, BorderLayout.EAST);
        
        // Labeling Celsius units.
        JLabel degreesC = new JLabel(" degrees C  ", SwingConstants.RIGHT);
        jfrm.add(degreesC, BorderLayout.SOUTH);
 
        //Creating an action listener to detect when the user hits the Enter key.
        userInput.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent ae){
            try {
                double temp = Double.parseDouble(userInput.getText());
                degreesC.setText("= " + Math.round(convert(temp)*10)/10.0 + " degrees C ");
            }
            catch (Exception except)
            {
                degreesC.setText("Invalid input ");
            }
        }
        });
       
        // Display the frame. 
        jfrm.setVisible(true);
    }
  
    public static double convert(double fahrenheit)
    {
        return ((fahrenheit - 32)*5)/9.0;
    }
    public static void main(String args[]) {

        // Create the frame on the event dispatching thread. 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JTemperature();
            }
        });

    }
}