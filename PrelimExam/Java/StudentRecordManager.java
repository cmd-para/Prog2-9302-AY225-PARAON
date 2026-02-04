
//Josh Heidric C. Paraon
//230013121
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class StudentRecordManager extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    // Text fields for the 8 data points
    private JTextField txtID, txtFirst, txtLast, txtLab1, txtLab2, txtLab3, txtPrelim, txtAttendance;
    private JButton btnAdd, btnDelete, btnSave, btnOpen;
    private File selectedFile;

    public StudentRecordManager() {
        // Initial Title
        setTitle("Student Records Manager by: Josh Heidric C. Paraon Student ID: 230013121, Managing: (No File)");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Table Setup
        String[] columns = {
                "Student ID", "First Name", "Last Name",
                "Lab 1", "Lab 2", "Lab 3",
                "Prelim Exam", "Attendance"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnOpen = new JButton("Open Student Record File");
        topPanel.add(btnOpen);
        add(topPanel, BorderLayout.NORTH);

        // 3. Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 8, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Student Record"));

        inputPanel.add(new JLabel("ID"));
        inputPanel.add(new JLabel("First Name"));
        inputPanel.add(new JLabel("Last Name"));
        inputPanel.add(new JLabel("Lab 1"));
        inputPanel.add(new JLabel("Lab 2"));
        inputPanel.add(new JLabel("Lab 3"));
        inputPanel.add(new JLabel("Prelim Exam"));
        inputPanel.add(new JLabel("Attendance"));

        txtID = new JTextField();
        txtFirst = new JTextField();
        txtLast = new JTextField();
        txtLab1 = new JTextField();
        txtLab2 = new JTextField();
        txtLab3 = new JTextField();
        txtPrelim = new JTextField();
        txtAttendance = new JTextField();

        inputPanel.add(txtID);
        inputPanel.add(txtFirst);
        inputPanel.add(txtLast);
        inputPanel.add(txtLab1);
        inputPanel.add(txtLab2);
        inputPanel.add(txtLab3);
        inputPanel.add(txtPrelim);
        inputPanel.add(txtAttendance);

        // 4. Control Panel
        JPanel controlPanel = new JPanel();
        btnAdd = new JButton("Add Record");
        btnDelete = new JButton("Delete Selected");
        btnSave = new JButton("Save Changes to CSV");

        controlPanel.add(btnAdd);
        controlPanel.add(btnDelete);
        controlPanel.add(btnSave);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(controlPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // --- FIXED STARTUP LOGIC ---
        try {
            // Looking for the file in the root project directory
            File autoFile = new File("MOCK_DATA.csv");
            if (autoFile.exists()) {
                selectedFile = autoFile; // Ensure this is set so Save button works
                loadData(selectedFile);
            } else {
                System.out.println("MOCK_DATA.csv not found in the root directory.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Auto-load Error: " + ex.getMessage());
        }

        // --- Event Listeners ---
        btnOpen.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                loadData(selectedFile);
            }
        });

        btnAdd.addActionListener(e -> {
            if (validateFields()) {
                tableModel.addRow(new Object[] {
                        txtID.getText(), txtFirst.getText(), txtLast.getText(),
                        txtLab1.getText(), txtLab2.getText(), txtLab3.getText(),
                        txtPrelim.getText(), txtAttendance.getText()
                });
                clearFields();
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                tableModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        btnSave.addActionListener(e -> {
            if (selectedFile != null) {
                saveData(selectedFile);
            } else {
                JOptionPane.showMessageDialog(this, "No file is currently open to save to.");
            }
        });
    }

    private void loadData(File file) {
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip the header row
                }
                String[] d = line.split(",");
                if (d.length >= 8) {
                    tableModel.addRow(new Object[] { d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7] });
                }
            }
            setTitle("Student Records Manager by: Josh Heidric C. Paraon Student ID: 230013121, Managing: ("
                    + file.getName() + ")");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
        }
    }

    private void saveData(File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Write the specific CSV header back
            bw.write("StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE GRADE");
            bw.newLine();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < 8; j++) {
                    row.append(tableModel.getValueAt(i, j));
                    if (j < 7)
                        row.append(",");
                }
                bw.write(row.toString());
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Data saved successfully to " + file.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save Error: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (txtID.getText().trim().isEmpty() || txtFirst.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and First Name are required.");
            return false;
        }

        JTextField[] gradeFields = { txtLab1, txtLab2, txtLab3, txtPrelim, txtAttendance };
        String[] fieldNames = { "Lab 1", "Lab 2", "Lab 3", "Prelim Exam", "Attendance" };

        for (int i = 0; i < gradeFields.length; i++) {
            String val = gradeFields[i].getText().trim();
            if (val.isEmpty())
                continue;

            try {
                int score = Integer.parseInt(val);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, fieldNames[i] + " must be between 0 and 100.");
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, fieldNames[i] + " must be a valid number.");
                return false;
            }
        }
        return true;
    }

    private void clearFields() {
        txtID.setText("");
        txtFirst.setText("");
        txtLast.setText("");
        txtLab1.setText("");
        txtLab2.setText("");
        txtLab3.setText("");
        txtPrelim.setText("");
        txtAttendance.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRecordManager().setVisible(true));
    }
}