import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MessagingPage extends JFrame {
    private JList < String > userList;
    private JButton sendButton;
    private JButton imagebutton;
    private JPanel messagePanel;
    private JTextField messageField;
    private String currentUser;
    private Connection conn;
    private JFileChooser fileChooser;
    private JButton backButton;
    private JScrollPane messageScrollPane;
    private String shareto;
    private String path;
    private String postedby;



    public MessagingPage(String currentUser, String selectedUser, String imagePath, String poster) {
        shareto = selectedUser;
        path = imagePath;
        postedby = poster;
        // Connect to the database
        try {
            String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.currentUser = currentUser;
        setSize(500, 800);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);


        // Initialize the components
        userList = new JList < String > ();
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setFixedCellHeight(50);
        userList.setFixedCellWidth(80);
        userList.setPreferredSize(new Dimension(80, 500));
        sendButton = new JButton("Send");
        imagebutton = new JButton("Image");
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS)); // Use a BoxLayout to stack the posts vertically
        messagePanel.setBackground(Color.white); // Set the background color of the feed panel to white
        messageScrollPane = new JScrollPane(messagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messageScrollPane.setPreferredSize(new Dimension(400, 600)); // Set the size of the scroll pane
        messageField = new JTextField(30);
        userListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        backButton = new JButton("Back");


        // Add the components to the frame
        add(userList, BorderLayout.LINE_START);
        add(messageScrollPane, BorderLayout.CENTER);
        add(messageField, BorderLayout.SOUTH);
        add(userListScrollPane);
        add(sendButton, BorderLayout.EAST);
        add(imagebutton);
        add(backButton);
        loadUsers();
        if (shareto != "" && path != "" && postedby != "") {
            shareImage(selectedUser, imagePath, poster);
            if (this.messagePanel == null) {
                loadMessages(selectedUser);
            }


        } else if (shareto != "" && path == "" && postedby == "") {
            loadMessages(shareto);
        }

        userList.setSelectedIndex(0);
        loadMessages(userList.getSelectedValue());


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HomePage homePage = new HomePage();
                homePage.setVisible(true);
                dispose();
            }
        });
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });


        // Load the list of users from the database
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadMessages((String) userList.getSelectedValue());
                }
            }
        });
        fileChooser = new JFileChooser();
        imagebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = "user1";
                selectedUser = (String) userList.getSelectedValue();
                int returnVal = fileChooser.showOpenDialog(MessagingPage.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //get the selected file
                    File file = fileChooser.getSelectedFile();
                    //get the path of the file
                    String imagePath = file.getAbsolutePath();
                    //get the selected user
                    //insert the info into the database
                    String sql = "INSERT INTO messages (sentby, sentto, path, postedby, message) VALUES ('" + currentUser + "', '" + selectedUser + "', '" + imagePath + "', '" + currentUser + "', null)";
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate(sql);
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                loadMessages(selectedUser);
            }
        });

        // Add an action listener to the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected user and the message text
                String selectedUser = "user1";
                selectedUser = (String) userList.getSelectedValue();
                String message = messageField.getText();

                // Insert the message into the database
                String sql = "INSERT INTO messages (sentby, sentto, path, postedby, message) VALUES ('" + currentUser + "', '" + selectedUser + "', null, null, '" + message + "')";



                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                // Clear the message field

                messageField.setText("");
                // load the message
                loadMessages(selectedUser);
            }
        });
    }
    private void shareImage(String shareto, String path, String postedby) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db");
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO messages (sentby, sentto, path, postedby, message) VALUES ('" + currentUser + "', '" + shareto + "', '" + path + "', '" + postedby + "', null)";
            stmt.executeUpdate(sql);
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
    ListSelectionListener listSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            loadMessages(userList.getSelectedValue());
        }
    };
    private void loadMessages(String selectedUser) {
        if (messagePanel != null) {
            // Clear the message panel
            messagePanel.removeAll();
            // Query the database for messages between the current user and the selected user
            String sql = "SELECT * FROM messages WHERE (sentby='" + currentUser + "'AND (sentto='" + selectedUser + "' OR sentto IS NULL)) OR (sentby='" + selectedUser + "'AND (sentto='" + currentUser + "' OR sentto IS NULL))";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String sender = rs.getString("sentby");
                    String message = rs.getString("message");
                    String image = rs.getString("path");
                    String postedby = rs.getString("postedby");
                    // Create a label for the message
                    if (rs.getString("path") != null) {
                        JLabel postLabel;
                        if (postedby != null) {
                            postLabel = new JLabel("<html><h1><b>" + "" + postedby + "</b></h1></html>");
                            postLabel.setFont(new Font("Serif", Font.PLAIN, 30));
                            postLabel.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    ProfilePage profilePage = new ProfilePage(postedby);
                                    profilePage.setVisible(true);
                                    MessagingPage.this.dispose();
                                }
                            });
                            messagePanel.add(postLabel);
                        }
                        // Create a label for the image
                        JLabel imageLabel = new JLabel();
                        // Set the size of the label to be a square
                        imageLabel.setPreferredSize(new Dimension(150, 150));
                        // Read the image file and set it as the icon for the label
                        ImageIcon imageIcon = new ImageIcon(image);
                        Image img = imageIcon.getImage();
                        Image newimg = img.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(newimg);
                        imageLabel.setIcon(imageIcon);
                        // Add the label to the panel
                        messagePanel.add(imageLabel);
                    } else if (rs.getString("message") != null) {
                        JLabel postLabel = new JLabel("<html><b>" + sender + "</b>: \n" + message + "</html>");
                        postLabel.setForeground(Color.black);
                        postLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                ProfilePage profilePage = new ProfilePage(sender);
                                profilePage.setVisible(true);
                                MessagingPage.this.dispose();
                            }
                        });
                        messagePanel.add(postLabel);
                    } else if (rs.getString("sentby") != null && rs.getString("path") == null && rs.getString("message") == null) {
                        JLabel usernameLabel = new JLabel(sender);
                        usernameLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                ProfilePage profilePage = new ProfilePage(sender);
                                profilePage.setVisible(true);
                                MessagingPage.this.dispose();
                            }
                        });
                        messagePanel.add(usernameLabel);
                    }

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            // Repaint the message panel
            messagePanel.repaint();
            // Scroll to the bottom of the message panel for most recent message
            messagePanel.scrollRectToVisible(new Rectangle(0, messagePanel.getHeight() - 1, 1, 1));
        }
    }

}