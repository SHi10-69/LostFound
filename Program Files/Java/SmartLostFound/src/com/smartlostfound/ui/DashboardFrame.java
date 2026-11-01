
package com.smartlostfound.ui;

import com.smartlostfound.model.User;
import com.smartlostfound.service.ItemService;
import com.smartlostfound.service.UserService;
import com.smartlostfound.service.ClaimService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private User loggedInUser;

    private ItemService itemService;
    private UserService userService;
    private ClaimService claimService;

    private JLabel totalItemsLabel;
    private JLabel lostItemsLabel;
    private JLabel foundItemsLabel;
    private JLabel totalUsersLabel;
    private JLabel pendingClaimsLabel;
    private JLabel approvedClaimsLabel;

    private JTabbedPane tabbedPane;

    public DashboardFrame(User user) {

        loggedInUser = user;

        itemService = new ItemService();
        userService = new UserService();
        claimService = new ClaimService();

        setTitle("Smart Lost & Found Management System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMenuBar();
        createDashboard();

        setVisible(true);
    }

    // ---------------- MENU BAR ----------------

    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu accountMenu = new JMenu("Account");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem profileItem = new JMenuItem("My Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenuItem aboutItem = new JMenuItem("About System");

        profileItem.addActionListener(e -> showProfile());

        logoutItem.addActionListener(e -> logout());

        exitItem.addActionListener(e -> System.exit(0));

        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Smart Lost & Found Management System\n" +
                                "Java Swing Desktop Application\n" +
                                "Local File Storage",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        accountMenu.add(profileItem);
        accountMenu.addSeparator();
        accountMenu.add(logoutItem);
        accountMenu.add(exitItem);

        helpMenu.add(aboutItem);

        menuBar.add(accountMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // ---------------- MAIN DASHBOARD ----------------

    private void createDashboard() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Welcome heading
        JLabel welcomeLabel = new JLabel(
                "Welcome, " + loggedInUser.getFullName(),
                SwingConstants.CENTER
        );

        welcomeLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Dashboard tabs
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab(
                "Dashboard",
                createOverviewPanel()
        );

        tabbedPane.addTab(
                "Report Item",
                new AddItemPanel(loggedInUser)
        );

        tabbedPane.addTab(
                "Browse Items",
                createBrowsePanel()
        );

        // NEW: Smart Matches tab
        tabbedPane.addTab(
                "Matches",
                new SmartMatchesPanel(loggedInUser)
        );
        tabbedPane.addTab(
                "Submit Claim",
                new SubmitClaimPanel(loggedInUser)
        );
        tabbedPane.addTab(
                "My Claims",
                new MyClaimsPanel(loggedInUser)
        );

        // Admin-only tabs
        if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {

            tabbedPane.addTab(
                    "Manage Users",
                    createUserManagementPanel()
            );

            tabbedPane.addTab(
                    "Manage Claims",
                    new ManageClaimsPanel(loggedInUser)
            );
        }

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom logout button
        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> logout());

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.add(logoutButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ---------------- OVERVIEW PANEL ----------------

    private JPanel createOverviewPanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));

        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JLabel heading = new JLabel(
                "System Overview",
                SwingConstants.CENTER
        );

        heading.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        panel.add(heading, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(
                new GridLayout(2, 3, 20, 20)
        );

        totalItemsLabel = new JLabel();
        lostItemsLabel = new JLabel();
        foundItemsLabel = new JLabel();
        totalUsersLabel = new JLabel();
        pendingClaimsLabel = new JLabel();
        approvedClaimsLabel = new JLabel();

        cardsPanel.add(
                createStatCard("Total Items", totalItemsLabel)
        );

        cardsPanel.add(
                createStatCard("Lost Items", lostItemsLabel)
        );

        cardsPanel.add(
                createStatCard("Found Items", foundItemsLabel)
        );

        cardsPanel.add(
                createStatCard("Registered Users", totalUsersLabel)
        );
        cardsPanel.add(
                createStatCard("Pending Claims", pendingClaimsLabel)
        );

        cardsPanel.add(
                createStatCard("Approved Claims", approvedClaimsLabel)
        );

        panel.add(cardsPanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Statistics");

        refreshButton.addActionListener(e -> refreshStatistics());

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshStatistics();
        pendingClaimsLabel.setText(
                String.valueOf(claimService.countPendingClaims())
        );

        approvedClaimsLabel.setText(
                String.valueOf(claimService.countApprovedClaims())
        );

        return panel;
    }

    // ---------------- STATISTICS CARD ----------------


    private JPanel createStatCard(String title, JLabel valueLabel) {

        JPanel card = new JPanel(new GridLayout(3, 1, 5, 5));

        // Different background colors for each statistic
        Color cardColor;

        switch (title) {
            case "Total Items":
                cardColor = new Color(69, 96, 234);
                break;

            case "Lost Items":
                cardColor = new Color(230, 126, 34);
                break;

            case "Found Items":
                cardColor = new Color(22, 163, 106);
                break;

            case "Registered Users":
                cardColor = new Color(124, 69, 237);
                break;

            case "Pending Claims":
                cardColor = new Color(202, 12, 6);
                break;

            case "Approved Claims":
                cardColor = new Color(219, 39, 119);
                break;

            default:
                cardColor = new Color(75, 59, 123);
        }

        // Card background
        card.setBackground(cardColor);

        // Rounded-looking border with spacing
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(cardColor.darker(), 2),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        );

        // Statistic title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);

        // Statistic value
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 30));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setOpaque(false);


        String iconText;

        switch (title) {
            case "Total Items":
                iconText = "📦";
                break;
            case "Lost Items":
                iconText = "🔍";
                break;
            case "Found Items":
                iconText = "✅";
                break;
            case "Registered Users":
                iconText = "👥";
                break;
            case "Pending Claims":
                iconText = "⏳";
                break;
            case "Approved Claims":
                iconText = "🏆";
                break;
            default:
                iconText = "📊";
        }

        JLabel iconLabel = new JLabel(iconText, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(Color.WHITE);

        card.add(iconLabel);
        card.add(titleLabel);
        card.add(valueLabel);


        return card;
    }

    // ---------------- REFRESH STATISTICS ----------------

    private void refreshStatistics() {

        totalItemsLabel.setText(
                String.valueOf(itemService.countItems())
        );

        lostItemsLabel.setText(
                String.valueOf(itemService.countLostItems())
        );

        foundItemsLabel.setText(
                String.valueOf(itemService.countFoundItems())
        );

        totalUsersLabel.setText(
                String.valueOf(userService.getAllUsers().size())
        );
    }

    // ---------------- BROWSE ITEMS PANEL ----------------

    private JPanel createBrowsePanel() {

        return new BrowseItemsPanel(loggedInUser);
    }

    // ---------------- USER MANAGEMENT PANEL ----------------

    private JPanel createUserManagementPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Registered User Management",
                SwingConstants.CENTER
        );

        heading.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        panel.add(heading, BorderLayout.NORTH);

        String[] columns = {
                "ID",
                "Full Name",
                "Username",
                "Email",
                "Role",
                "Account Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable userTable = new JTable(model);

        userTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(userTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Users");

        JButton blockButton = new JButton(
                "Block / Unblock Selected User"
        );

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(refreshButton);
        buttonPanel.add(blockButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load users into table
        Runnable loadUsers = () -> {

            model.setRowCount(0);

            List<User> users = userService.getAllUsers();

            for (User user : users) {

                String status = user.isBlocked()
                        ? "Blocked"
                        : "Active";

                model.addRow(new Object[]{
                        user.getId(),
                        user.getFullName(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        status
                });
            }
        };

        loadUsers.run();

        refreshButton.addActionListener(e -> loadUsers.run());

        // Block or unblock user
        blockButton.addActionListener(e -> {

            int selectedRow = userTable.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a user first."
                );

                return;
            }

            int userId = (int) model.getValueAt(selectedRow, 0);

            String role = String.valueOf(
                    model.getValueAt(selectedRow, 4)
            );

            String status = String.valueOf(
                    model.getValueAt(selectedRow, 5)
            );

            if ("ADMIN".equalsIgnoreCase(role)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Admin accounts cannot be blocked."
                );

                return;
            }

            boolean currentlyBlocked = "Blocked".equals(status);

            boolean newBlockedStatus = !currentlyBlocked;

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    newBlockedStatus
                            ? "Are you sure you want to block this user?"
                            : "Are you sure you want to unblock this user?",
                    "Confirm Action",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                userService.setBlocked(
                        userId,
                        newBlockedStatus
                );

                JOptionPane.showMessageDialog(
                        this,
                        newBlockedStatus
                                ? "User blocked successfully."
                                : "User unblocked successfully."
                );

                loadUsers.run();

                refreshStatistics();
            }
        });

        return panel;
    }

    // ---------------- PROFILE ----------------

    private void showProfile() {

        JOptionPane.showMessageDialog(
                this,
                "Full Name: " + loggedInUser.getFullName() +
                        "\nUsername: " + loggedInUser.getUsername() +
                        "\nEmail: " + loggedInUser.getEmail() +
                        "\nRole: " + loggedInUser.getRole(),
                "My Profile",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ---------------- LOGOUT ----------------

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            new LoginFrame();
        }
    }
}
