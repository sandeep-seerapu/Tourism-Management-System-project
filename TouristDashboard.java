import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class TouristDashboard extends JFrame {
    private JLabel welcomeLabel;
    private JComboBox<String> packageComboBox;
    private JTextArea packageInfoTextArea;
    private JButton bookPackageButton;
    private JButton viewBookingsButton;
    private JButton viewPaymentsButton;
    private JButton logoutButton;
    private String username;
    private List<Integer> packageIds;
    public TouristDashboard(String username) {
        this.username = username;
        setTitle("Tourist Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon backgroundImage = new ImageIcon("src/scene4.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        setContentPane(backgroundLabel);
        setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (Tourist)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        packageComboBox = new JComboBox<>();
        packageInfoTextArea = new JTextArea(5, 20);
        packageInfoTextArea.setEditable(false);
        packageInfoTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(packageInfoTextArea);
        bookPackageButton = new JButton("Book Package");
        viewBookingsButton = new JButton("View Bookings");
        viewPaymentsButton = new JButton("View Payments");
        logoutButton = new JButton("Logout");
        JLabel availpack = new JLabel("Available packages:");
        availpack.setFont(new Font("Arial",Font.BOLD,16));
        centerPanel.add(availpack, gbc);
        gbc.gridx++;
        centerPanel.add(packageComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel packinfo = new JLabel("package information:");
        packinfo.setFont(new Font("Arial",Font.BOLD,16));
        centerPanel.add(packinfo, gbc);
        gbc.gridx++;
        centerPanel.add(new JPanel(), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        centerPanel.add(scrollPane, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        centerPanel.add(bookPackageButton, gbc);
        gbc.gridx++;
        centerPanel.add(viewBookingsButton, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        centerPanel.add(viewPaymentsButton, gbc);
        gbc.gridx++;
        centerPanel.add(logoutButton, gbc);
        gbc.gridy++;
        panel.add(centerPanel, BorderLayout.CENTER);
        add(panel);
        setVisible(true);
        initializePackageComboBox();
        addListeners();
    }
    private void initializePackageComboBox() {
        packageIds = new ArrayList<>();
        packageComboBox.removeAllItems();
        packageInfoTextArea.setText("");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT package_id, package_name FROM tour_packages")) {
            while (rs.next()) {
                int packageId = rs.getInt("package_id");
                String packageName = rs.getString("package_name");
                packageComboBox.addItem(packageName);
                packageIds.add(packageId);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch packages: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void addListeners() {
        packageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String packageName = (String) packageComboBox.getSelectedItem();
                if (packageName != null) {
                    int index = packageComboBox.getSelectedIndex();
                    int packageId = packageIds.get(index);
                    fetchPackageDetails(packageId);
                }
            }
        });
        bookPackageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookPackage();
            }
        });
        viewBookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBookings();
            }
        });
        viewPaymentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPayments();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginScreen(); // Assuming you have a login screen to go back to
            }
        });
    }
    private void fetchPackageDetails(int packageId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tour_packages WHERE package_id = ?")) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String packageName = rs.getString("package_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int availableSlots = rs.getInt("available_slots");
                String packageInfo = "Package Name: " + packageName + "\n"
                        + "Description: " + description + "\n"
                        + "Price: $" + price + "\n"
                        + "Available Slots: " + availableSlots;
                packageInfoTextArea.setText(packageInfo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch package details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void bookPackage() {
        String packageName = (String) packageComboBox.getSelectedItem();
        if (packageName != null) {
            int index = packageComboBox.getSelectedIndex();
            int packageId = packageIds.get(index);
            double amount = fetchPackagePrice(packageId);
            int confirmPayment = JOptionPane.showConfirmDialog(this, "Do you want to make payment of $" + amount + " for the selected package?");
            if (confirmPayment == JOptionPane.YES_OPTION) {
                int bookingId = bookPackageInDB(packageId);
                if (bookingId > 0) {
                    makePayment(bookingId, packageId, amount);
                }
            }
        }
    }
    private double fetchPackagePrice(int packageId) {
        double price = 0.0;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep");
             PreparedStatement stmt = conn.prepareStatement("SELECT price FROM tour_packages WHERE package_id = ?")) {
            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                price = rs.getDouble("price");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch package price: " + e.getMessage());
            e.printStackTrace();
        }
        return price;
    }
    private void makePayment(int bookingId, int packageId, double amount) {
        String[] paymentMethods = {"Card", "Cash"};
        String paymentType = (String) JOptionPane.showInputDialog(this, "Select Payment Method:", "Payment", JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);
        if (paymentType != null) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep")) {
                int userId = getUserId(username);
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO payments (user_id, package_id, amount, date, payment_type, booking_id) VALUES (?, ?, ?, ?, ?, ?)");
                stmt.setInt(1, userId);
                stmt.setInt(2, packageId);
                stmt.setDouble(3, amount);
                stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                stmt.setString(5, paymentType);
                stmt.setInt(6, bookingId);
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    updateBookingStatus(bookingId, "Booked");
                    JOptionPane.showMessageDialog(this, "Payment successful!");
                    String feedback = JOptionPane.showInputDialog(this, "Please provide your feedback:");
                    String ratingStr = JOptionPane.showInputDialog(this, "Please rate the package (1 to 5):");
                    int rating = Integer.parseInt(ratingStr);
                    insertFeedback(userId, packageId, feedback, rating);
                } else {
                    JOptionPane.showMessageDialog(this, "Payment failed. Please try again.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Payment failed. Please try again.");
                e.printStackTrace();
            }
        }
    }
    private void insertFeedback(int userId, int packageId, String feedback, int rating) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep")) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO feedback (user_id, package_id, feedback, rating, date) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, packageId);
            stmt.setString(3, feedback);
            stmt.setInt(4, rating);
            stmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Thank you for your feedback!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit feedback. Please try again.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to submit feedback. Please try again.");
            e.printStackTrace();
        }
    }
    private int bookPackageInDB(int packageId) {
        int bookingId = 0;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep")) {
            int userId = getUserId(username);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO bookings (user_id, package_id, booking_date, status) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setInt(2, packageId);
            stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(4, "Pending");
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bookingId = generatedKeys.getInt(1);
                }
                JOptionPane.showMessageDialog(this, "Booking initiated. Please proceed to payment.");
            } else {
                JOptionPane.showMessageDialog(this, "Booking initiation failed. Please try again.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Booking initiation failed. Please try again.");
            e.printStackTrace();
        }
        return bookingId;
    }
    private void updateBookingStatus(int bookingId, String status) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep")) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE bookings SET status = ? WHERE booking_id = ?");
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update booking status. Please try again.");
            e.printStackTrace();
        }
    }
    private void viewBookings() {
        String[] columnNames = {"Booking ID", "Package Name", "Booking Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep")) {
            int userId = getUserId(username);
            PreparedStatement stmt = conn.prepareStatement("SELECT b.booking_id, t.package_name, b.booking_date, b.status FROM bookings b JOIN tour_packages t ON b.package_id = t.package_id WHERE b.user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String packageName = rs.getString("package_name");
                Date bookingDate = rs.getDate("booking_date");
                String status = rs.getString("status");

                model.addRow(new Object[]{bookingId, packageName, bookingDate, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch bookings: " + e.getMessage());
            e.printStackTrace();
        }
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("Your Bookings");
        frame.setSize(600, 400);
        frame.add(scrollPane);
        frame.setVisible(true);
    }
    private void viewPayments() {
        String[] columnNames = {"Payment ID", "Package Name", "Amount", "Date", "Payment Type", "Booking ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep")) {
            int userId = getUserId(username);
            PreparedStatement stmt = conn.prepareStatement("SELECT p.payment_id, t.package_name, p.amount, p.date, p.payment_type, p.booking_id FROM payments p JOIN tour_packages t ON p.package_id = t.package_id WHERE p.user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                String packageName = rs.getString("package_name");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("date");
                String paymentType = rs.getString("payment_type");
                int bookingId = rs.getInt("booking_id");

                model.addRow(new Object[]{paymentId, packageName, amount, date, paymentType, bookingId});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch payments: " + e.getMessage());
            e.printStackTrace();
        }
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("Your Payments");
        frame.setSize(600, 400);
        frame.add(scrollPane);
        frame.setVisible(true);
    }
    private int getUserId(String username) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tourism", "root", "sandeep");
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        throw new SQLException("User ID not found for username: " + username);
    }
}