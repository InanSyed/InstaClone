import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.*;
public class HomePage extends JFrame {

    private JButton logoutButton;
    private JTextField postField;
    private JButton postTextButton; // Button to post text
    private JButton postImageButton; // Button to post an image
    private JPanel feedPanel;
    private JScrollPane feedScrollPane;
    private JButton profileButton;
    private JButton messagesButton;
    private Connection conn = null;
    private JButton searchButton;
    private JButton shareButton;
    private String imagePath;
    private JList < String > userList;
    private int counter = 0;
    private boolean share = false;





    public HomePage() {
        setTitle("Home Page");
        setSize(800, 800);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        // Initialize the components
        logoutButton = new JButton("Logout");
        profileButton = new JButton("Profile");
        postField = new JTextField(20);
        postTextButton = new JButton("Post");
        postImageButton = new JButton("Post Image");
        messagesButton = new JButton("Messages");
        searchButton = new JButton("Search");
        feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS)); // Use a BoxLayout to stack the posts vertically
        feedScrollPane = new JScrollPane(feedPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        feedScrollPane.setPreferredSize(new Dimension(400, 600)); // Set the size of the scroll pane
        userList = new JList < String > ();
        try {
            String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            conn = null;
        }


        // Add the components to the frame
        add(logoutButton, BorderLayout.NORTH);
        add(profileButton);
        add(postField);
        add(postTextButton);
        add(postImageButton);
        add(feedScrollPane);
        add(messagesButton);
        add(searchButton);
        loadUsers();
        try {
            loadPosts();
        } catch (SQLException | IOException e2) {
            e2.printStackTrace();
        }

        // Add an action listener to the logout button
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the home page and show the login screen
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
                dispose();

            }
        });


        // Add an action listener to the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the search page
                SearchPage searchPage = new SearchPage();
                searchPage.setVisible(true);
                dispose();
            }
        });
        // Add an action listener to the profile button
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the profile page
                ProfilePage profilePage = new ProfilePage(LoginScreen.currentUser);
                profilePage.setVisible(true);
                dispose();

            }
        });
        messagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessagingPage messagingPage = new MessagingPage(LoginScreen.currentUser, "", "", "");
                messagingPage.setVisible(true);
                dispose();
            }
        });

        // Add an action listener to the post text button
        postTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the text from the post field
                String text = postField.getText();
                String sql = "INSERT INTO posts (username, text, image, post_id) VALUES ('" + LoginScreen.currentUser + "', '" + text + "', null, null)";
                postField.setText("");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    stmt.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                try {
                    loadPosts();
                } catch (SQLException | IOException e1) {
                    e1.printStackTrace();
                }
                postField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            postTextButton.doClick();
                        }
                    }
                });
            }
        });
        // Add an action listener to the post image button
        postImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a file chooser to select an image
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif");
                fileChooser.setFileFilter(filter);
                if (fileChooser.showOpenDialog(HomePage.this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();
                    // Insert the image and user into the database
                    String sql = "INSERT INTO posts (username, text, image, post_id) VALUES ('" + LoginScreen.currentUser + "', null, '" + filePath + "', null)";
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(sql);
                        stmt.close();
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                    try { //Load all the posts including new one
                        loadPosts();

                    } catch (SQLException | IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });





    }
    private void loadUsers() {
        // Query the database for a list of users
        String sql = "SELECT accounts FROM accounts";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            // Create a list model to store the users
            DefaultListModel < String > listModel = new DefaultListModel < > ();
            // Iterate through the results and add each user to the list model
            while (rs.next()) {
                String account = rs.getString("accounts");
                listModel.addElement(account);
            }
            // Set the list model as the model for the user list
            userList.setModel(listModel);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private void loadPosts() throws SQLException, IOException {

        // Retrieve the posts from the database
        String sql = "SELECT * FROM posts";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            // Clear the feed panel
            feedPanel.removeAll();

            // Iterate through the posts
            while (rs.next()) {
                // Retrieve the post data
                counter++;
                String username = rs.getString("username");
                String text = rs.getString("text");
                int id = counter;

                // Create an image icon from the path
                if (rs.getString("image") != null) {
                    shareButton = new JButton("Share");
                    share = true;
                    JLabel postLabel = new JLabel("<html><b>" + username + "</b>: " + "'></html>");
                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(new File(rs.getString("image"))); // read the image from the file
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ImageIcon imageIcon = new ImageIcon(image); // create an ImageIcon from the image
                    Image newimg = imageIcon.getImage();
                    newimg = newimg.getScaledInstance(350, 350, java.awt.Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(newimg);
                    JLabel imageLabel = new JLabel(imageIcon);
                    feedPanel.add(postLabel);
                    feedPanel.add(imageLabel);
                    feedPanel.add(shareButton);

                }

                // Create a label to display the text
                else if (rs.getString("text") != null) {
                    JLabel postLabel = new JLabel("<html><b>" + username + "</b>: \n" + text + "</html>");
                    feedPanel.add(postLabel);
                }
                feedPanel.setVisible(true);
                feedPanel.revalidate(); // Re-layout the feed panel
                feedPanel.repaint(); // Repaint the feed panel
                // Add the post label to the feed panel
                if (share == true) {
                    shareButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Get the id of the post that the share button is next to
                            int postId = id;
                            String poster = "";
                            // Connect to the database and retrieve the image path for the post with the given id
                            Connection conn = null;
                            try {
                                String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
                                conn = DriverManager.getConnection(url);
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT * FROM posts WHERE post_id = '" + postId + "'");
                                imagePath = rs.getString("image");
                                poster = rs.getString("username");
                                stmt.close();
                                conn.close();

                            } catch (SQLException e1) {
                                System.out.println(e1.getMessage());
                            }
                            loadUsers();
                            JOptionPane.showOptionDialog(HomePage.this, userList, "Select a user to share the post with", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[] {}, null);
                            // Get the selected user from the list
                            String selectedUser = userList.getSelectedValue();
                            // Open the messaging page with the selected user
                            if (selectedUser != null) {
                                JOptionPane.getRootFrame().dispose();
                                userList.setVisible(false);
                                MessagingPage messagingPage = new MessagingPage(LoginScreen.currentUser, selectedUser, imagePath, poster);
                                messagingPage.setVisible(true);
                                HomePage.this.dispose();
                            }
                        }
                    });
                }
            }
        }
    }

}