package editprofile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class EditProfileGUI {

    public EditProfileGUI() {
        JFrame frame = new JFrame("Edit Profile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JLabel lblName = new JLabel("Name: ");
        JTextField txtName = new JTextField();

        JLabel lblRole = new JLabel("Role: ");
        JTextField txtRole = new JTextField();

        JLabel lblUsername = new JLabel("Username: ");
        JTextField txtUsername = new JTextField();
        
        JLabel lblPassword = new JLabel("Password: ");
        JPasswordField txtPassword = new JPasswordField();

        JButton btnSubmit = new JButton("Submit");
        JButton btnReset = new JButton("Reset");

        panel.add(lblName);
        panel.add(txtName);

        panel.add(lblRole);
        panel.add(txtRole);

        panel.add(lblUsername);
        panel.add(txtUsername);
        
        panel.add(lblPassword);
        panel.add(txtPassword);

        panel.add(btnSubmit);
        panel.add(btnReset);

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // action for submit button
                try {
                    // get data from fields
                    String name = txtName.getText();
                    String role = txtRole.getText();
                    String username = txtUsername.getText();
                    String password = new String(txtPassword.getPassword());

                    // create a database connection
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tubes_pbo", "username", "password");

                    // create a PreparedStatement to update the database
                    String query = "UPDATE your_table SET name = ?, role = ?, username = ?, password = ? WHERE your_condition";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, name);
                    pstmt.setString(2, role);
                    pstmt.setString(3, username);
                    pstmt.setString(4, password);

                    // execute the PreparedStatement
                    pstmt.executeUpdate();

                    // close the connection
                    conn.close();

                    JOptionPane.showMessageDialog(null, "Profile updated successfully!");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating profile!");
                }
            }
        });

        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // action for reset button
                txtName.setText("");
                txtRole.setText("");
                txtUsername.setText("");
                txtPassword.setText("");
            }
        });

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}