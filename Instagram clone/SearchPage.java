import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SearchPage extends JFrame {
    private JTextField searchField;
    private JList<String> userList;
    private JScrollPane userListScrollPane;
    private BTree btree;
    private JButton backButton;

    public SearchPage() {
        setTitle("Search Page");
        setSize(500, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Initialize the components
        searchField = new JTextField(20);
        userList = new JList<>();
        userListScrollPane = new JScrollPane(userList);
        btree = new BTree();
        backButton = new JButton("Back");



        //populate the btree with users from the database
        populateBTree();
        // Add the components to the frame
        
        add(searchField, BorderLayout.NORTH);
        add(userListScrollPane, BorderLayout.EAST);
        add(userList, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
         backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HomePage homePage = new HomePage();
                homePage.setVisible(true);
                dispose();
            }
        });
        // Add an key listener to the search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                
                String prefix = searchField.getText();
                    userList.setVisible(true);
                //search the btree for users with the prefix
                List<String> users = btree.search(prefix);
                userList.setListData(users.toArray(new String[0]));
                userList.repaint();
                
                
            }
        });
        //add an action listener to the list to open the profile page when a user is clicked
        userList.addListSelectionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                // Open the profile page
                ProfilePage profilePage = new ProfilePage(selectedUser);
                profilePage.setVisible(true);
                dispose();
            }
        });

        
    }
    private void populateBTree(){
    //Connect to the database and retrieve the list of users
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:/Users/inan/Downloads/pong 5-3/users.db";
            conn = DriverManager.getConnection(url);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT accounts FROM accounts");
            while (rs.next()) {
                String username = rs.getString("accounts");
                btree.insert(username);
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
