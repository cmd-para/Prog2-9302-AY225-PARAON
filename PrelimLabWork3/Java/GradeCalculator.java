import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class GradeCalculator extends JFrame {

    private JTextField attendanceField, excusedAbsencesField, lab1Field, lab2Field, lab3Field;
    private JTextArea resultsArea;
    private boolean isUpdating = false;

    public GradeCalculator() {
        setTitle("Programming 2 - Grade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 800);
        setLocationRelativeTo(null);

        setLookAndFeel();

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(240, 242, 245));
        add(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        root.add(card, gbc);

        JLabel title = new JLabel("Programming 2 Grade Calculator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Prelims Term");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(25));

        card.add(createInputPanel());

        card.add(Box.createVerticalStrut(25));

        resultsArea = new JTextArea(18, 60);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultsArea.setBackground(new Color(245, 247, 250));
        resultsArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        resultsArea.setText("Please enter all values to see results.");

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.add(scrollPane);

        addDocumentListener(attendanceField);
        addDocumentListener(excusedAbsencesField);
        addDocumentListener(lab1Field);
        addDocumentListener(lab2Field);
        addDocumentListener(lab3Field);

        calculateGrades();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 12, 12));
        panel.setBackground(Color.WHITE);

        attendanceField = createField("0 - 5");
        excusedAbsencesField = createField("0 - 5 (Optional)");
        lab1Field = createField("0 - 100");
        lab2Field = createField("0 - 100");
        lab3Field = createField("0 - 100");

        panel.add(createLabel("Attendance Count (0 - 5)"));
        panel.add(attendanceField);

        panel.add(createLabel("Excused Absences (e.g., late enrollee, sick days)"));
        panel.add(excusedAbsencesField);

        panel.add(createLabel("Lab Work 1"));
        panel.add(lab1Field);

        panel.add(createLabel("Lab Work 2"));
        panel.add(lab2Field);

        panel.add(createLabel("Lab Work 3"));
        panel.add(lab3Field);

        return panel;
    }

    private JTextField createField(String tooltip) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setToolTipText(tooltip);
        return field;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private void addDocumentListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateGrades(); }
            public void removeUpdate(DocumentEvent e) { calculateGrades(); }
            public void changedUpdate(DocumentEvent e) { calculateGrades(); }
        });
    }

    private void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void calculateGrades() {
        if (isUpdating) {
            return;
        }
        
        try {
            // Get input values
            String attendanceText = attendanceField.getText().trim();
            String excusedAbsencesText = excusedAbsencesField.getText().trim();
            String lab1Text = lab1Field.getText().trim();
            String lab2Text = lab2Field.getText().trim();
            String lab3Text = lab3Field.getText().trim();
            
            // Check if all required inputs have values (excused absences is optional)
            if (attendanceText.isEmpty() || lab1Text.isEmpty() || 
                lab2Text.isEmpty() || lab3Text.isEmpty()) {
                resultsArea.setText("Please enter all values to see results.");
                return;
            }
            
            double attendanceCount = Double.parseDouble(attendanceText);
            double excusedAbsences = excusedAbsencesText.isEmpty() ? 0 : Double.parseDouble(excusedAbsencesText);
            double lab1 = Double.parseDouble(lab1Text);
            double lab2 = Double.parseDouble(lab2Text);
            double lab3 = Double.parseDouble(lab3Text);
            
            // Enforce attendance limit (0-5)
            if (attendanceCount < 0) {
                attendanceCount = 0;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    attendanceField.setText("0");
                    isUpdating = false;
                });
            } else if (attendanceCount > 5) {
                attendanceCount = 5;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    attendanceField.setText("5");
                    isUpdating = false;
                });
            }
            
            // Enforce excused absences limit (0-5)
            if (excusedAbsences < 0) {
                excusedAbsences = 0;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    excusedAbsencesField.setText("0");
                    isUpdating = false;
                });
            } else if (excusedAbsences > 5) {
                excusedAbsences = 5;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    excusedAbsencesField.setText("5");
                    isUpdating = false;
                });
            }
            
            // Hard limit: attendance + excused absences cannot exceed 5
            if (attendanceCount + excusedAbsences > 5) {
                double attendanceMax = 5 - excusedAbsences;
                attendanceCount = Math.max(0, attendanceMax);
                double finalAttendance = attendanceCount;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    attendanceField.setText(String.valueOf((int)finalAttendance));
                    isUpdating = false;
                });
            }
            
            // Calculate total effective attendance
            double totalAttendance = attendanceCount + excusedAbsences;
            
            // Enforce lab work grade limits (0-100)
            if (lab1 < 0) {
                lab1 = 0;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    lab1Field.setText("0");
                    isUpdating = false;
                });
            } else if (lab1 > 100) {
                lab1 = 100;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    lab1Field.setText("100");
                    isUpdating = false;
                });
            }
            
            if (lab2 < 0) {
                lab2 = 0;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    lab2Field.setText("0");
                    isUpdating = false;
                });
            } else if (lab2 > 100) {
                lab2 = 100;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    lab2Field.setText("100");
                    isUpdating = false;
                });
            }
            
            if (lab3 < 0) {
                lab3 = 0;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    lab3Field.setText("0");
                    isUpdating = false;
                });
            } else if (lab3 > 100) {
                lab3 = 100;
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    lab3Field.setText("100");
                    isUpdating = false;
                });
            }
            
            // Validate ranges
            if (attendanceCount < 0 || attendanceCount > 5) {
                resultsArea.setText("⚠️ Attendance must be between 0 and 5.");
                return;
            }
            
            if (excusedAbsences < 0 || excusedAbsences > 5) {
                resultsArea.setText("⚠️ Excused absences must be between 0 and 5.");
                return;
            }
            
            if (totalAttendance > 5) {
                resultsArea.setText("⚠️ Total attendance (physical + excused) cannot exceed 5.");
                return;
            }
            
            if (lab1 < 0 || lab1 > 100 || lab2 < 0 || lab2 > 100 || lab3 < 0 || lab3 > 100) {
                resultsArea.setText("⚠️ Lab work grades must be between 0 and 100.");
                return;
            }
            
            // Calculate unexcused absences
            double unexcusedAbsences = 5 - totalAttendance;
            
            // Check if student has 4 or more unexcused absences (failed due to absences)
            if (unexcusedAbsences >= 4) {
                StringBuilder results = new StringBuilder();
                results.append("========================================\n");
                results.append("RESULTS\n");
                results.append("========================================\n\n");
                results.append("--- Course Status ---\n");
                results.append("❌ FAILED DUE TO EXCESSIVE ABSENCES\n\n");
                results.append(String.format("Physical Attendances: %.0f out of 5\n", attendanceCount));
                if (excusedAbsences > 0) {
                    results.append(String.format("Excused Absences: %.0f\n", excusedAbsences));
                }
                results.append(String.format("Unexcused Absences: %.0f out of 5\n\n", unexcusedAbsences));
                results.append("Students with 4 or more unexcused absences\n");
                results.append("automatically fail the course, regardless of grades.\n");
                
                resultsArea.setText(results.toString());
                return;
            }
            
            // Calculate Lab Work Average
            double labWorkAverage = (lab1 + lab2 + lab3) / 3;
            
            // Calculate Attendance Score (5 attendances = 100%)
            double maxAttendances = 5;
            double attendanceScore = (totalAttendance / maxAttendances) * 100;
            
            // Calculate Class Standing
            double classStanding = (attendanceScore * 0.40) + (labWorkAverage * 0.60);
            
            // Calculate Required Prelim Exam Scores
            double passingGrade = 75;
            double excellentGrade = 100;
            
            double requiredForPassing = (passingGrade - (classStanding * 0.30)) / 0.70;
            double requiredForExcellent = (excellentGrade - (classStanding * 0.30)) / 0.70;
            
            // Determine student's standing
            String standing;
            if (classStanding >= 90) {
                standing = "Excellent! You have a very strong class standing.";
            } else if (classStanding >= 75) {
                standing = "Good! You have a passing class standing.";
            } else {
                standing = "You need to improve your class standing.";
            }
            
            // Build results text
            StringBuilder results = new StringBuilder();
            results.append("========================================\n");
            results.append("RESULTS\n");
            results.append("========================================\n\n");
            
            results.append("--- Computed Values ---\n");
            results.append(String.format("Physical Attendances: %.0f out of 5\n", attendanceCount));
            if (excusedAbsences > 0) {
                results.append(String.format("Excused Absences: %.0f\n", excusedAbsences));
            }
            results.append(String.format("Unexcused Absences: %.0f out of 5\n", unexcusedAbsences));
            results.append(String.format("Total Effective Attendance: %.0f out of 5\n", totalAttendance));
            results.append(String.format("Attendance Score: %.2f%%\n", attendanceScore));
            results.append(String.format("Lab Work 1: %.2f\n", lab1));
            results.append(String.format("Lab Work 2: %.2f\n", lab2));
            results.append(String.format("Lab Work 3: %.2f\n", lab3));
            results.append(String.format("Lab Work Average: %.2f\n", labWorkAverage));
            results.append(String.format("Class Standing: %.2f\n\n", classStanding));
            
            results.append("--- Required Prelim Exam Scores ---\n");
            
            if (requiredForPassing <= 0) {
                results.append(String.format("To Pass (75): You've already secured a passing grade!\n   (Required: %.2f)\n", 
                    requiredForPassing));
            } else if (requiredForPassing > 100) {
                results.append(String.format("To Pass (75): %.2f\n   Unfortunately, passing is mathematically impossible\n   with your current class standing.\n", 
                    requiredForPassing));
            } else {
                results.append(String.format("To Pass (75): %.2f\n", requiredForPassing));
            }
            
            if (requiredForExcellent <= 0) {
                results.append(String.format("For Excellent (100): You've already secured an excellent grade!\n   (Required: %.2f)\n", 
                    requiredForExcellent));
            } else if (requiredForExcellent > 100) {
                results.append(String.format("For Excellent (100): %.2f\n   This would require more than 100%% on the exam.\n", 
                    requiredForExcellent));
            } else {
                results.append(String.format("For Excellent (100): %.2f\n", requiredForExcellent));
            }
            
            results.append("\n--- Student Standing ---\n");
            results.append(standing + "\n");
            
            resultsArea.setText(results.toString());
            
        } catch (NumberFormatException e) {
            resultsArea.setText("Please enter valid numeric values.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradeCalculator().setVisible(true));
    }
}