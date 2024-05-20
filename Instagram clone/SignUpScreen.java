import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class SignUpScreen extends JFrame {

    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton signUpButton;
    private JButton backButton;



    public SignUpScreen() {
        setLayout(new FlowLayout());
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Initialize the components
        usernameLabel = new JLabel("Username: ");
        usernameField = new JTextField(20);
        passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(20);
        signUpButton = new JButton("Sign Up");
        backButton = new JButton("Back");


        // Add the components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(signUpButton);
        add(backButton);


        // Add an action listener to the sign up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the username and password from the text fields
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                // Validate the username and password
                if (!LoginScreen.validateCredentials(username, password)) {
                    // If the credentials are valid, add the new user to the database and display a message
                    addUserToDatabase(username, password);
                    LoginScreen loginScreen = new LoginScreen();
                    loginScreen.setVisible(true);
                    dispose();
                } else {
                    // If the credentials are invalid, display an error message
                    JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Add an action listener to the back button
    backButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Close the sign up screen and open the login screen
            dispose();
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        }
    });
}
    


    // This method would typically add the new user to a database
    private void addUserToDatabase(String username, char[] password) {
        // Connect to the database
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
    
        // Encrypt the password
        String encryptedPassword = encryptPassword(password);
    
        // Insert the new user into the database
        String sql = "INSERT INTO accounts (accounts, passwords) VALUES ('" + username + "', '" + encryptedPassword + "')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private String encryptPassword(char[] password) {
        try {
            // Generate a random salt for password encryption
            byte[] salt = new byte[16];
            // SecureRandom is used to generate random numbers in a cryptographically secure way
            SecureRandom.getInstanceStrong().nextBytes(salt);
    
            // Derive the key from the password and salt using the PBKDF2WithHmacSHA256 algorithm
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
    
            // Encrypt the password using AES encryption algorithm and ECB mode
            // https://www.baeldung.com/java-cipher-class#:~:text=The%20transformation%20AES%2FECB%2FPKCS5Padding,operation%20and%20PKCS5%20padding%20scheme.&text=In%20this%20case%2C%20Java%20will,the%20mode%20and%20padding%20scheme.
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] encryptedPassword = cipher.doFinal(new String(password).getBytes());
    
            // Combine the salt and encrypted password and encode them in base64
            //https://www.tabnine.com/code/java/methods/com.sun.jersey.core.util.Base64/encode
            byte[] combined = new byte[salt.length + encryptedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(encryptedPassword, 0, combined, salt.length, encryptedPassword.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException |
        NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("Error encrypting password: " + e.getMessage());
            return null;
        }
    }
    
}
    
    
