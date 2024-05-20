import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class LoginScreen extends JFrame {

    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    public static String currentUser;



    public LoginScreen() {
        setLayout(new FlowLayout());
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Initialize the components
        usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(20);
        passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");

        // Add the components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);


        // Add an action listener to the login button
        loginButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the username and password from the text fields
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                // Validate the username and password
                if (validateCredentials(username, password)) {
                    // If the credentials are valid, display a message and close the login screen
                    HomePage homePage = new HomePage();
                    homePage.setVisible(true);
                    currentUser=username;
                    dispose();
                } else {
                    // If the credentials are invalid, display an error message
                    JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        usernameField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    loginButton.doClick();
            }
            }
        });
        passwordField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    loginButton.doClick();
            }
            }
        });
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SignUpScreen signUpScreen = new SignUpScreen();
                signUpScreen.setVisible(true);
                dispose();
            }
        });
        
    }
    //do everything in the opposite order of the encryption
    public static boolean decryptPassword(String encryptedPassword,char[] password) {
        try {
        // Decode the base64 encoded string to get the salt and encrypted password
        byte[] combined = Base64.getDecoder().decode(encryptedPassword);
        byte[] salt = Arrays.copyOfRange(combined, 0, 16);
        byte[] encPass = Arrays.copyOfRange(combined, 16, combined.length);
            // Derive the key from the password and salt
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

    // Decrypt the password
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secret);
    byte[] decryptedPassword = cipher.doFinal(encPass);
    return new String(decryptedPassword).equals(new String(password));
} catch (NoSuchAlgorithmException | InvalidKeySpecException |
NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
    return false;
}
    }
    // This method would typically validate the username and password against a database or some other data store
    static boolean validateCredentials(String username, char[] password) {
        // Connect to the database
        Connection conn = null;
        try {
        String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
        conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
        System.out.println(e.getMessage());
        return false;
        }
        // Query the database for the username and password
        String sql = "SELECT passwords FROM accounts WHERE accounts = '" + username + "'";
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
        
            // If the query returns a result, check the password
            if (rs.next()) {
                String encryptedPassword = rs.getString("passwords");
                return decryptPassword(encryptedPassword,password);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        
        // If the query doesn't return a result, the credentials are invalid
        return false;
        }

    
}