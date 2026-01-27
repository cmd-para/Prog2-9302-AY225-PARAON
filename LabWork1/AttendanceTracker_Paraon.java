package LabWork1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AttendanceTracker_Paraon {
    public static void main(String[] args) {
        // Use the event dispatch thread for Swing components
        SwingUtilities.invokeLater(AttendanceTracker_Paraon::createAndShowUI);
    }

    private static void createAndShowUI() {
        // 1. Setup Whitelist for validation
        Set<String> allowedCourses = new HashSet<>();
        String[] courses = {"BSCS 1", "BSCS 2", "BSCS 3", "BSCS 4", "BSIT 1", "BSIT 2", "BSIT 3", "BSIT 4"};
        for (String c : courses) allowedCourses.add(c);

        // 2. Window Configuration
        JFrame frame = new JFrame("CCS Attendance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 3. Layout and UI Components
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField courseField = new JTextField(20);
        JTextField timeInField = new JTextField(20);
        JTextField signatureField = new JTextField(20);

        // Set Time In: Automatically formatted
        timeInField.setEditable(false);
        timeInField.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")));
        
        // Visual styling for read-only fields
        signatureField.setEditable(false);
        signatureField.setBackground(new Color(245, 245, 245));

        // 4. Build the Form UI
        addComponent(panel, new JLabel("Student Name:"), 0, 0, gbc);
        addComponent(panel, nameField, 1, 0, gbc);
        
        addComponent(panel, new JLabel("Course & Year:"), 0, 1, gbc);
        addComponent(panel, courseField, 1, 1, gbc);
        
        addComponent(panel, new JLabel("Arrival Time:"), 0, 2, gbc);
        addComponent(panel, timeInField, 1, 2, gbc);
        
        addComponent(panel, new JLabel("E-Signature:"), 0, 3, gbc);
        addComponent(panel, signatureField, 1, 3, gbc);

        // 5. Submit Button Logic
        JButton submitButton = new JButton("Submit Attendance");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(submitButton, gbc);

        submitButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String course = courseField.getText().trim().toUpperCase();

            // Validation logic
            if (name.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Error: Name and Course are required.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!allowedCourses.contains(course)) {
                JOptionPane.showMessageDialog(frame, "Invalid Course.\nAccepted: BSIT 1-4, BSCS 1-4", "Validation Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate unique short signature from UUID
            String eSignature = UUID.randomUUID().toString().split("-")[0].toUpperCase();
            signatureField.setText(eSignature);

            // Console logging for verification
            System.out.println("LOGGED: [" + course + "] " + name + " | Sig: " + eSignature);
            
            JOptionPane.showMessageDialog(frame, "Attendance logged successfully for " + name + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        // 6. Finalize Display
        frame.add(panel);
        frame.pack(); // Automatically sizes window to fit components
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Helper to manage GridBagLayout positioning
     */
    private static void addComponent(Container container, Component component, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        container.add(component, gbc);
    }
}