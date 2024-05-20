import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.*;
public class ProfilePage extends JFrame {
    private JPanel imagePanel;
    private JScrollPane imageScrollPane;
    private GridLayout gridLayout;
    private JLabel usernameLabel;
    private JTable classTable;
    private JButton backButton;
    private JButton messagingButton;


    
    public ProfilePage(String user) {
        String username=user;
        setTitle("Profile Page - " + username);
        setSize(700, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Initialize the components
        imagePanel = new JPanel();
        gridLayout = new GridLayout(0,3);  // 3 columns and as many rows as needed
        imagePanel.setLayout(gridLayout);
        imagePanel.setBackground(Color.white);
        imageScrollPane = new JScrollPane(imagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        loadImages(username);
        
        //initialize the username label
        usernameLabel = new JLabel("Username: " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        backButton = new JButton("Back");
        messagingButton = new JButton("Message");


        
        //initialize the table for classes (everyone has the same classes and marks)
        String[] columnNames = {"Class", "Marks"};
        Object[][] data = {
            {"Math", "A+"},
            {"Science", "A"},
            {"English", "A-"},
            {"History", "B+"}
        };
        classTable = new JTable(data, columnNames);
        classTable.setEnabled(false);
       
        // Add the components to the frame
        
        add(usernameLabel,BorderLayout.NORTH);
        add(backButton,BorderLayout.BEFORE_LINE_BEGINS);
        add(imageScrollPane, BorderLayout.CENTER);
        add(classTable, BorderLayout.SOUTH);
        add(messagingButton, BorderLayout.AFTER_LINE_ENDS);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HomePage homePage = new HomePage();
                homePage.setVisible(true);
                ProfilePage.this.dispose();
            }
        });

        messagingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the messaging page with the messages with that user is loaded
                MessagingPage messagingPage = new MessagingPage(LoginScreen.currentUser, username, "", "");
                messagingPage.setVisible(true);
                dispose();
            }
        });
        

        setVisible(true);
    }
   
    private void loadImages(String username) {
        // Connect to the database and retrieve the images for the current user
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
            conn = DriverManager.getConnection(url);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT image FROM posts WHERE username = '" + username + "'");

            while (rs.next()) {
                String imagePath = rs.getString("image");
                // Create a label for each image
                JLabel imageLabel = new JLabel();
                // Set the size of the label to be a square
                imageLabel.setPreferredSize(new Dimension(150,150));
                // Read the image file and set it as the icon for the label
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image image = imageIcon.getImage();
                Image newimg = image.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(newimg);
                imageLabel.setIcon(imageIcon);
                // Add the label to the panel
                imagePanel.add(imageLabel);
            }
            stmt.close();
            conn.close();
            } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }     
}




