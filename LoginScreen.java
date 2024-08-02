import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    public LoginScreen() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("src/darj.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Make the panel transparent
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10); // Padding around components
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE); // Set title color
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, constraints);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(Color.WHITE); // Set label color
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(usernameLabel, constraints);
        usernameField = new JTextField(20);
        usernameField.setFont(fieldFont);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(usernameField, constraints);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE); // Set label color
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordLabel, constraints);
        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(passwordField, constraints);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(labelFont);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        backgroundPanel.add(panel, BorderLayout.CENTER);
        add(backgroundPanel);
        setVisible(true);
    }
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = UserAuthentication.authenticateUser(username, password);
        if (role != null) {
            JOptionPane.showMessageDialog(this, "Login successful as " + role);
            dispose();
            if ("admin".equals(role)) {
                new AdminDashboard(username);
            } else if ("tourist".equals(role)) {
                new TouristDashboard(username);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
        }
    }
    public static class UserAuthentication {
        public static String authenticateUser(String username, String password) {
            String role = null;
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep");
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    role = rs.getString("role");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return role;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen();
            }
        });
    }
}
