import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegForm extends JFrame {
    private JTextField txtName;
    private JTextField txtMobile;
    private JRadioButton rbFemale;
    private JRadioButton rbMale;
    private JTextField txtDOB;
    private JTextArea txtAddress;
    private JCheckBox chkTerms;
    private JTextArea txtDisplay;
    private ButtonGroup genderGroup;

    public static final String DB_URL = "jdbc:mysql://localhost:3306/reg_form";
    public static final String USER = "admin";
    public static final String PASS = "1234";


    public RegForm() {
        setTitle("Registration Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create Form Panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField(20);
        txtMobile = new JTextField(20);
        rbFemale = new JRadioButton("F");
        rbMale = new JRadioButton("M");
        genderGroup = new ButtonGroup();
        genderGroup.add(rbFemale);
        genderGroup.add(rbMale);

        txtDOB = new JTextField(20); // Date of Birth Text Field

        txtAddress = new JTextArea(3, 20);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        chkTerms = new JCheckBox("Accept Terms and Conditions");

        // Adding components to the formPanel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mobile"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtMobile, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Gender"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(rbFemale);
        genderPanel.add(rbMale);
        formPanel.add(genderPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("DOB (yyyy-MM-dd)"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtDOB, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Address"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(scrollAddress, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(chkTerms, gbc);

        // Create Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSubmit = new JButton("Submit");
        JButton btnReset = new JButton("Reset");
        buttonsPanel.add(btnSubmit);
        buttonsPanel.add(btnReset);

        // Create TextArea for displaying data
        txtDisplay = new JTextArea(10, 50);
        txtDisplay.setEditable(false);
        JScrollPane scrollDisplay = new JScrollPane(txtDisplay);

        // Add panels to the frame
        add(formPanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(scrollDisplay, BorderLayout.SOUTH);

        // Load Data
        loadData();

        // Button Actions
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
    }

    private void loadData() {
        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Employees")) {

            // Clear existing data
            txtDisplay.setText("");

            while (rs.next()) {
                txtDisplay.append("ID: " + rs.getInt("ID") + ", ");
                txtDisplay.append("Name: " + rs.getString("Name") + ", ");
                txtDisplay.append("Mobile: " + rs.getString("Mobile") + ", ");
                txtDisplay.append("Gender: " + rs.getString("Gender") + ", ");
                txtDisplay.append("DOB: " + rs.getDate("DOB") + ", ");
                txtDisplay.append("Address: " + rs.getString("Address") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitForm() {
        if (chkTerms.isSelected()) {
            try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String gender = rbFemale.isSelected() ? "F" : "M";
                String dobStr = txtDOB.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date parsedDate = sdf.parse(dobStr);
                java.sql.Date dob = new java.sql.Date(parsedDate.getTime());

                String query = "INSERT INTO Employees (Name, Mobile, Gender, DOB, Address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, txtName.getText());
                pst.setString(2, txtMobile.getText());
                pst.setString(3, gender);
                pst.setDate(4, dob);
                pst.setString(5, txtAddress.getText());

                pst.executeUpdate();
                loadData();
                JOptionPane.showMessageDialog(this, "Record added successfully!");
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please accept the terms and conditions.");
        }
    }

    private void resetForm() {
        txtName.setText("");
        txtMobile.setText("");
        genderGroup.clearSelection();
        txtDOB.setText("");
        txtAddress.setText("");
        chkTerms.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegForm().setVisible(true);
            }
        });
    }
}
