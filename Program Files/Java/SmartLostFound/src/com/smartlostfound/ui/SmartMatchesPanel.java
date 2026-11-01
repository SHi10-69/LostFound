package com.smartlostfound.ui;

import com.smartlostfound.model.Item;
import com.smartlostfound.model.User;
import com.smartlostfound.service.ItemService;
import com.smartlostfound.service.SmartMatchingService;
import com.smartlostfound.service.SmartMatchingService.MatchResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmartMatchesPanel extends JPanel {

    private final User loggedInUser;
    private final ItemService itemService;
    private final SmartMatchingService matchingService;

    private JTable matchTable;
    private DefaultTableModel tableModel;

    public SmartMatchesPanel(User loggedInUser) {

        this.loggedInUser = loggedInUser;
        itemService = new ItemService();
        matchingService = new SmartMatchingService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        createHeading();
        createTable();
        createButtons();

        loadMatches();
    }

    private void createHeading() {

        JLabel heading = new JLabel("Matches");
        heading.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel subtitle = new JLabel(
                "Possible matches for your reported lost and found items"
        );

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
    }

    private void createTable() {

        String[] columns = {
                "Lost ID",
                "Lost Item",
                "Found ID",
                "Found Item",
                "Category",
                "Lost Location",
                "Found Location",
                "Match %"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        matchTable = new JTable(tableModel);
        matchTable.setRowHeight(28);
        matchTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(matchTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createButtons() {

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton viewButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh Matches");

        viewButton.addActionListener(e -> viewSelectedMatch());
        refreshButton.addActionListener(e -> loadMatches());

        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMatches() {

        tableModel.setRowCount(0);

        List<Item> allItems = itemService.getAllItems();

        Set<String> displayedPairs = new HashSet<>();

        for (Item item : allItems) {

            // Admin can see matches across all users.
            // Normal users can see matches involving their own items.
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {

                if (item.getReportedByUsername() == null
                        || !item.getReportedByUsername().equalsIgnoreCase(
                        loggedInUser.getUsername())) {
                    continue;
                }
            }

            List<MatchResult> results =
                    matchingService.findMatches(item);

            for (MatchResult result : results) {

                Item lost = result.getLostItem();
                Item found = result.getFoundItem();

                String pairKey = lost.getId() + "-" + found.getId();

                // Prevent duplicate match rows
                if (!displayedPairs.add(pairKey)) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        lost.getId(),
                        lost.getItemName(),
                        found.getId(),
                        found.getItemName(),
                        lost.getCategory(),
                        lost.getLocation(),
                        found.getLocation(),
                        String.format("%.2f%%", result.getPercentage())
                });
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No possible matches found for your reported items.",
                    "Smart Matches",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void viewSelectedMatch() {

        int selectedRow = matchTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a match first."
            );
            return;
        }

        int modelRow = matchTable.convertRowIndexToModel(selectedRow);

        int lostId = (int) tableModel.getValueAt(modelRow, 0);
        int foundId = (int) tableModel.getValueAt(modelRow, 2);

        List<Item> items = itemService.getAllItems();

        Item lostItem = null;
        Item foundItem = null;

        for (Item item : items) {

            if (item.getId() == lostId) {
                lostItem = item;
            }

            if (item.getId() == foundId) {
                foundItem = item;
            }
        }

        if (lostItem == null || foundItem == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Item details could not be found."
            );
            return;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd-MM-yyyy");

        String lostDate = lostItem.getItemDate() == null
                ? "Not available"
                : dateFormat.format(lostItem.getItemDate());

        String foundDate = foundItem.getItemDate() == null
                ? "Not available"
                : dateFormat.format(foundItem.getItemDate());

        String details =
                "POSSIBLE MATCH DETAILS\n\n"

                        + "LOST ITEM\n"
                        + "ID: " + lostItem.getId() + "\n"
                        + "Name: " + lostItem.getItemName() + "\n"
                        + "Category: " + lostItem.getCategory() + "\n"
                        + "Location: " + lostItem.getLocation() + "\n"
                        + "Description: " + lostItem.getDescription() + "\n"
                        + "Date: " + lostDate + "\n\n"

                        + "FOUND ITEM\n"
                        + "ID: " + foundItem.getId() + "\n"
                        + "Name: " + foundItem.getItemName() + "\n"
                        + "Category: " + foundItem.getCategory() + "\n"
                        + "Location: " + foundItem.getLocation() + "\n"
                        + "Description: " + foundItem.getDescription() + "\n"
                        + "Date: " + foundDate + "\n\n"

                        + "Match Percentage: "
                        + tableModel.getValueAt(modelRow, 7);

        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Match Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}