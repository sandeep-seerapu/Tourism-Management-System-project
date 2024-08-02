import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.File;
import javax.imageio.ImageIO;
public class RegistrationForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField contactField;
    private JComboBox<String> roleComboBox;
    private JPanel backgroundPanel;
    public RegistrationForm() {
        setTitle("User Registration");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            Image backgroundImage = ImageIO.read(new File("src/da.jpg"));
            backgroundPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
            backgroundPanel = new JPanel(new BorderLayout());
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        usernameField.setFont(fieldFont);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(labelFont);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(contactLabel, constraints);

        contactField = new JTextField(20);
        contactField.setFont(fieldFont);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(contactField, constraints);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(labelFont);
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(roleLabel, constraints);

        String[] roles = {"admin", "tourist"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(fieldFont);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(roleComboBox, constraints);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(labelFont);
        constraints.gridx = 1;
        constraints.gridy = 4;
        panel.add(registerButton, constraints);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    registerUser();
                } else {
                    JOptionPane.showMessageDialog(RegistrationForm.this, "Please fill out all fields.");
                }
            }
        });

        backgroundPanel.add(panel, BorderLayout.CENTER);
        add(backgroundPanel);
        setVisible(true);
    }
    private boolean validateFields() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String contact = contactField.getText();
        if (username.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            return false;
        }
        return true;
    }
    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String contact = contactField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, contact, role) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, contact);
            stmt.setString(4, role);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
                new LoginScreen();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm();
            }
        });
    }
}
