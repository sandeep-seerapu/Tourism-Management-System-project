import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
public class AdminDashboard extends JFrame {
    private JTable dataTable;
    public AdminDashboard(String username) {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new GridLayout(3, 3, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        JButton viewPackagesButton = createStyledButton("View Packages");
        JButton addPackageButton = createStyledButton("Add Package");
        JButton updatePackageButton = createStyledButton("Update Package");
        JButton deletePackageButton = createStyledButton("Delete Package");
        JButton viewBookingsButton = createStyledButton("View Bookings");
        JButton updateBookingButton = createStyledButton("Update Booking");
        JButton viewPaymentsButton = createStyledButton("View Payments");
        JButton updatePaymentButton = createStyledButton("Update Payment");
        JButton viewFeedbackButton = createStyledButton("View Feedback");
        centerPanel.add(viewPackagesButton);
        centerPanel.add(addPackageButton);
        centerPanel.add(updatePackageButton);
        centerPanel.add(deletePackageButton);
        centerPanel.add(viewBookingsButton);
        centerPanel.add(updateBookingButton);
        centerPanel.add(viewPaymentsButton);
        centerPanel.add(updatePaymentButton);
        centerPanel.add(viewFeedbackButton);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        DefaultTableModel tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        getContentPane().setBackground(Color.WHITE);
        add(mainPanel);
        setVisible(true);
        viewPackagesButton.addActionListener(e -> viewPackages());
        addPackageButton.addActionListener(e -> addPackage());
        updatePackageButton.addActionListener(e -> updatePackage());
        deletePackageButton.addActionListener(e -> deletePackage());
        viewBookingsButton.addActionListener(e -> viewBookings());
        updateBookingButton.addActionListener(e -> updateBooking());
        viewPaymentsButton.addActionListener(e -> viewPayments());
        updatePaymentButton.addActionListener(e -> updatePayment());
        viewFeedbackButton.addActionListener(e -> viewFeedback());
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 153, 255));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }
    private void viewPackages() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tour_packages")) {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Package ID", "Package Name", "Description", "Price", "Available Slots"});
            while (rs.next()) {
                int packageId = rs.getInt("package_id");
                String packageName = rs.getString("package_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int availableSlots = rs.getInt("available_slots");
                tableModel.addRow(new Object[]{packageId, packageName, description, price, availableSlots});
            }
            dataTable.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch packages: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void addPackage() {
        JTextField packageNameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField priceField = new JTextField(10);
        JTextField slotsField = new JTextField(5);
        JPanel addPackagePanel = new JPanel(new GridLayout(0, 1));
        addPackagePanel.add(new JLabel("Package Name:"));
        addPackagePanel.add(packageNameField);
        addPackagePanel.add(new JLabel("Description:"));
        addPackagePanel.add(descriptionField);
        addPackagePanel.add(new JLabel("Price:"));
        addPackagePanel.add(priceField);
        addPackagePanel.add(new JLabel("Available Slots:"));
        addPackagePanel.add(slotsField);
        int option = JOptionPane.showConfirmDialog(this, addPackagePanel, "Add New Package", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String packageName = packageNameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int slots = Integer.parseInt(slotsField.getText());
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tour_packages (package_name, description, price, available_slots) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, packageName);
                stmt.setString(2, description);
                stmt.setDouble(3, price);
                stmt.setInt(4, slots);
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Package added successfully!");
                    viewPackages(); // Refresh the displayed packages after adding
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add package. Please try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to add package: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    private void updatePackage() {
        JTextField packageIdField = new JTextField(5);
        JTextField packageNameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField priceField = new JTextField(10);
        JTextField slotsField = new JTextField(5);
        JPanel updatePackagePanel = new JPanel(new GridLayout(0, 1));
        updatePackagePanel.add(new JLabel("Package ID:"));
        updatePackagePanel.add(packageIdField);
        updatePackagePanel.add(new JLabel("Package Name:"));
        updatePackagePanel.add(packageNameField);
        updatePackagePanel.add(new JLabel("Description:"));
        updatePackagePanel.add(descriptionField);
        updatePackagePanel.add(new JLabel("Price:"));
        updatePackagePanel.add(priceField);
        updatePackagePanel.add(new JLabel("Available Slots:"));
        updatePackagePanel.add(slotsField);
        int option = JOptionPane.showConfirmDialog(this, updatePackagePanel, "Update Package", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int packageId = Integer.parseInt(packageIdField.getText());
            double price = Double.parseDouble(priceField.getText());
            int slots = Integer.parseInt(slotsField.getText());
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE tour_packages SET  price = ?, available_slots = ? WHERE package_id = ?")) {
                stmt.setDouble(1, price);
                stmt.setInt(2, slots);
                stmt.setInt(3, packageId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Package updated successfully!");
                    viewPackages();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update package. Please check the package ID and try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update package. Please try again.");
                ex.printStackTrace();
            }
        }
    }
    private void deletePackage() {
        String packageIdString = JOptionPane.showInputDialog(this, "Enter Package ID to delete:");
        if (packageIdString != null && !packageIdString.isEmpty()) {
            int packageId = Integer.parseInt(packageIdString);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tour_packages WHERE package_id = ?")) {
                stmt.setInt(1, packageId);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Package deleted successfully!");
                    viewPackages();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete package. Please check the package ID and try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete package. Please try again.");
                ex.printStackTrace();
            }
        }
    }
    private void viewBookings() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bookings")) {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Booking ID", "user id", "Package ID", "Booking Date"});
            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                int userid = rs.getInt("user_id");
                int packageId = rs.getInt("package_id");
                Date bookingDate = rs.getDate("booking_date");
                tableModel.addRow(new Object[]{bookingId,userid,packageId,bookingDate, });
            }
            dataTable.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch bookings: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void updateBooking() {
        JTextField bookingIdField = new JTextField(5);
        JTextField useridField = new JTextField(5);
        JTextField packageIdField = new JTextField(5);
        JTextField customerNameField = new JTextField(20);
        JTextField bookingDateField = new JTextField(10);
        JTextField statusField = new JTextField(10);
        JPanel updateBookingPanel = new JPanel(new GridLayout(0, 1));
        updateBookingPanel.add(new JLabel("Booking ID:"));
        updateBookingPanel.add(bookingIdField);
        updateBookingPanel.add(new JLabel("user ID:"));
        updateBookingPanel.add(useridField);
        updateBookingPanel.add(new JLabel("Package ID:"));
        updateBookingPanel.add(packageIdField);
        updateBookingPanel.add(new JLabel("Booking Date (YYYY-MM-DD):"));
        updateBookingPanel.add(bookingDateField);
        updateBookingPanel.add(new JLabel("status"));
        updateBookingPanel.add(statusField);
        int option = JOptionPane.showConfirmDialog(this, updateBookingPanel, "Update Booking", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int bookingId = Integer.parseInt(bookingIdField.getText());
            String status = statusField.getText();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE bookings SET status=? WHERE booking_id = ?")) {
                stmt.setString(1,status);
                stmt.setInt(2, bookingId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Booking updated successfully!");
                    viewBookings();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update booking. Please check the booking ID and try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update booking. Please try again.");
                ex.printStackTrace();
            }
        }
    }
    private void viewPayments() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM payments")) {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Payment ID", "userid","packageid","Booking ID","Amount","Date","payment_type","stat"});
            while (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                int userid = rs.getInt("user_id");
                int packageid = rs.getInt("package_id");
                int bookingId = rs.getInt("booking_id");
                double amountPaid = rs.getDouble("amount");
                Date paymentDate = rs.getDate("date");
                String payment_type = rs.getString("payment_type");
                String stat = rs.getString("stat");
                tableModel.addRow(new Object[]{paymentId, userid,packageid,bookingId,amountPaid, paymentDate,payment_type,stat});
            }
            dataTable.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch payments: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void updatePayment() {
        JTextField paymentIdField = new JTextField(5);
        JTextField statField = new JTextField(10);
        JPanel updatePaymentPanel = new JPanel(new GridLayout(0, 1));
        updatePaymentPanel.add(new JLabel("Payment ID:"));
        updatePaymentPanel.add(paymentIdField);
        updatePaymentPanel.add(new JLabel("stat:"));
        updatePaymentPanel.add(statField);
        int option = JOptionPane.showConfirmDialog(this, updatePaymentPanel, "Update Payment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int paymentId = Integer.parseInt(paymentIdField.getText());
            String stat = String.valueOf(statField.getText());
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE payments SET stat=? WHERE payment_id = ?")) {
                stmt.setString(1,stat);
                stmt.setInt(4, paymentId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Payment updated successfully!");
                    viewPayments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update payment. Please check the payment ID and try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update payment. Please try again.");
                ex.printStackTrace();
            }
        }
    }
    private void viewFeedback() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tourism", "root", "sandeep");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM feedback")) {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Feedback ID","user id","package_id","Feedback","rating","Feedback Date "});
            while (rs.next()) {
                int feedbackId = rs.getInt("feed_id");
                int userid = rs.getInt("user_id");
                int packageid=rs.getInt("package_id");
                String feedback = rs.getString("feedback");
                int rating = rs.getInt("rating");
                Date feedbackDate = rs.getDate("date");
                tableModel.addRow(new Object[]{feedbackId,userid,packageid,feedback,rating,feedbackDate});
            }
            dataTable.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch feedback: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
