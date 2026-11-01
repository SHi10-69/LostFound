
package com.smartlostfound.ui;

import com.smartlostfound.service.UserService;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField usernameField;
    private JTextField emailField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private UserService userService;

    public RegisterFrame() {

        userService = new UserService();

        setTitle("Smart Lost & Found - Registration");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createGUI();

        setVisible(true);
    }

    private void createGUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 35, 25, 35
                )
        );

        JLabel title = new JLabel(
                "CREATE ACCOUNT",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 25)
        );

        mainPanel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(
                new GridLayout(6, 2, 10, 15)
        );

        nameField = new JTextField();
        usernameField = new JTextField();
        emailField = new JTextField();

        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        form.add(new JLabel("Full Name:"));
        form.add(nameField);

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        form.add(new JLabel("Confirm Password:"));
        form.add(confirmPasswordField);

        JButton registerButton =
                new JButton("REGISTER");

        JButton clearButton =
                new JButton("CLEAR");

        form.add(registerButton);
        form.add(clearButton);

        mainPanel.add(form, BorderLayout.CENTER);

        JLabel note = new JLabel(
                "New accounts are registered as USER.",
                SwingConstants.CENTER
        );

        mainPanel.add(note, BorderLayout.SOUTH);

        registerButton.addActionListener(
                e -> register()
        );

        clearButton.addActionListener(e -> clearForm());

        add(mainPanel);
    }

    private void register() {

        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields."
            );

            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid email address."
            );

            return;
        }

        if (password.length() < 6) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password must contain at least 6 characters."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
            );

            return;
        }

        boolean success = userService.registerUser(
                name,
                username,
                email,
                password
        );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful! You can now login."
            );

            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Username or email already exists, or saving failed.",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {

        nameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}
