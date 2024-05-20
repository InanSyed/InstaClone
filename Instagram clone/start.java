import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

// Start class extends JFrame to create a login screen
public class start extends JFrame{
    // This block of code sets the look and feel of the login screen
    // to the FlatLightLaf theme
    static {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    // Creates an instance of the LoginScreen class
    LoginScreen loginScreen = new LoginScreen();

    // The constructor for the start class
    public start(){
        super("LOGIN");
        // Sets the default close operation for the login screen to exit the program
        // Adds the loginScreen to the JFrame
        add(loginScreen);
        // Resizes the frame to fit the loginScreen
        pack();
    }

    public static void main(String[] args) {
        // This block of code sets the look and feel of the login screen
        // to the FlatLightLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Creates an instance of the LoginScreen class
        LoginScreen loginScreen = new LoginScreen();
        // Sets the default close operation for the login screen to exit the program
        loginScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Sets the title of the login screen to "LOGIN"
        loginScreen.setTitle("LOGIN");
        // Centers the loginScreen on the screen
        loginScreen.setLocationRelativeTo(null);
        // Makes the loginScreen visible
        loginScreen.setVisible(true);
    }
}

