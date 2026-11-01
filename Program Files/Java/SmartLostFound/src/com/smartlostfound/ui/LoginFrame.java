package com.smartlostfound.ui;

import com.smartlostfound.model.User;
import com.smartlostfound.service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;

    private UserService userService;

    public LoginFrame() {

        userService = new UserService();

        setTitle("Smart Lost & Found Management System");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createGUI();

        setVisible(true);
    }

    private void createGUI() {

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        // Heading
        JLabel titleLabel = new JLabel(
                "SMART LOST & FOUND SYSTEM",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));

        // Username
        JLabel usernameLabel = new JLabel("Username:");

        usernameField = new JTextField();

        // Password
        JLabel passwordLabel = new JLabel("Password:");

        passwordField = new JPasswordField();

        // Show password
        showPasswordCheckBox = new JCheckBox("Show Password");

        showPasswordCheckBox.addActionListener(e -> {

            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }

        });

        // Buttons
        loginButton = new JButton("Login");

        registerButton = new JButton("Register");

        exitButton = new JButton("Exit");

        // Add components
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);

        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        formPanel.add(showPasswordCheckBox);
        formPanel.add(new JLabel(""));

        formPanel.add(loginButton);
        formPanel.add(registerButton);

        formPanel.add(exitButton);
        formPanel.add(new JLabel(""));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Login button action
        loginButton.addActionListener(e -> loginUser());

        // Register button action
        registerButton.addActionListener(e -> {
            new RegisterFrame();
        });

        // Exit button action
        exitButton.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }

        });
    }

    private void loginUser() {

        String username = usernameField.getText().trim();

        String password = new String(
                passwordField.getPassword()
        );

        // Check empty fields
        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Check login
        User user = userService.login(username, password);

        if (user != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + user.getFullName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Open dashboard
            new DashboardFrame(user);

            // Close login window
            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password, or account is blocked.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
        }
    }
}