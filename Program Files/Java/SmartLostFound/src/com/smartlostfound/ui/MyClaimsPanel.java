
package com.smartlostfound.ui;

import com.smartlostfound.model.Claim;
import com.smartlostfound.model.Item;
import com.smartlostfound.model.User;
import com.smartlostfound.service.ClaimService;
import com.smartlostfound.service.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyClaimsPanel extends JPanel {

    private final User loggedInUser;
    private final ClaimService claimService;
    private final ItemService itemService;

    private JTable claimsTable;
    private DefaultTableModel tableModel;

    public MyClaimsPanel(User loggedInUser) {

        this.loggedInUser = loggedInUser;

        claimService = new ClaimService();
        itemService = new ItemService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createHeading();
        createTable();
        createButtons();

        loadClaims();
    }

    private void createHeading() {

        JLabel heading = new JLabel("My Claims");
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitle = new JLabel(
                "View the claims you have submitted and their current status."
        );

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
    }

    private void createTable() {

        String[] columns = {
                "Claim ID",
                "Item ID",
                "Item Name",
                "Claim Date",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        claimsTable = new JTable(tableModel);
        claimsTable.setRowHeight(28);
        claimsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(claimsTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createButtons() {

        JButton viewButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        viewButton.addActionListener(e -> viewClaimDetails());
        refreshButton.addActionListener(e -> loadClaims());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void loadClaims() {

        tableModel.setRowCount(0);

        // Load every saved claim
        List<Claim> allClaims = claimService.getAllClaims();

        // Load claims belonging to the logged-in user
        List<Claim> userClaims = claimService.getClaimsByUsername(
                loggedInUser.getUsername()
        );

        // Display diagnostic information
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Claim claim : userClaims) {

            Item item = null;

            for (Item currentItem : itemService.getAllItems()) {
                if (currentItem.getId() == claim.getItemId()) {
                    item = currentItem;
                    break;
                }
            }

            String itemName = item != null
                    ? item.getItemName()
                    : "Item not found";

            String claimDate = claim.getClaimDate() != null
                    ? dateFormat.format(claim.getClaimDate())
                    : "Not available";

            tableModel.addRow(new Object[]{
                    claim.getId(),
                    claim.getItemId(),
                    itemName,
                    claimDate,
                    claim.getClaimStatus()
            });
        }
    }


    private void viewClaimDetails() {

        int selectedRow = claimsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a claim first."
            );
            return;
        }

        int claimId = (int) tableModel.getValueAt(
                selectedRow, 0
        );

        for (Claim claim : claimService.getClaimsByUsername(
                loggedInUser.getUsername())) {

            if (claim.getId() == claimId) {

                JOptionPane.showMessageDialog(
                        this,
                        "Claim ID: " + claim.getId()
                                + "\nItem ID: " + claim.getItemId()
                                + "\nClaimant: " + claim.getClaimantName()
                                + "\nContact: " + claim.getContact()
                                + "\nProof: " + claim.getProof()
                                + "\nStatus: " + claim.getClaimStatus(),
                        "Claim Details",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }
        }
    }
}
