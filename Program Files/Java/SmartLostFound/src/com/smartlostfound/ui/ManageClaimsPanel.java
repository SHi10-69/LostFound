
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

public class ManageClaimsPanel extends JPanel {

    private final User loggedInUser;
    private final ClaimService claimService;
    private final ItemService itemService;

    private JTable claimsTable;
    private DefaultTableModel tableModel;

    public ManageClaimsPanel(User loggedInUser) {

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

        JLabel heading = new JLabel("Manage Claims");
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitle = new JLabel(
                "Review ownership claims and approve or reject them."
        );

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
    }

    private void createTable() {

        String[] columns = {
                "Claim ID",
                "FOUND ID",
                "FOUND Item Name",
                "LOST ID",
                "LOST Item Name",
                "Claimant",
                "Contact",
                "Date",
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

        claimsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        add(new JScrollPane(claimsTable), BorderLayout.CENTER);
    }

    private void createButtons() {

        JButton viewButton = new JButton("View Proof");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JButton refreshButton = new JButton("Refresh");

        viewButton.addActionListener(e -> viewProof());

        approveButton.addActionListener(
                e -> updateSelectedClaim("APPROVED")
        );

        rejectButton.addActionListener(
                e -> updateSelectedClaim("REJECTED")
        );

        refreshButton.addActionListener(e -> loadClaims());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        buttonPanel.add(viewButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadClaims() {

        tableModel.setRowCount(0);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd-MM-yyyy");

        for (Claim claim : claimService.getAllClaims()) {

            Item foundItem = findItem(claim.getItemId());

            Item lostItem = claim.getLostItemId() > 0
                    ? findItem(claim.getLostItemId())
                    : null;

            String foundName = foundItem != null
                    ? foundItem.getItemName()
                    : "Item not found";

            String lostName = lostItem != null
                    ? lostItem.getItemName()
                    : "Not linked";

            String lostId = claim.getLostItemId() > 0
                    ? String.valueOf(claim.getLostItemId())
                    : "-";

            String date = claim.getClaimDate() != null
                    ? dateFormat.format(claim.getClaimDate())
                    : "Not available";

            tableModel.addRow(new Object[]{
                    claim.getId(),
                    claim.getItemId(),
                    foundName,
                    lostId,
                    lostName,
                    claim.getClaimantName(),
                    claim.getContact(),
                    date,
                    claim.getClaimStatus()
            });
        }

        // Adjust column widths
        for (int i = 0; i < claimsTable.getColumnCount(); i++) {
            claimsTable.getColumnModel()
                    .getColumn(i).setPreferredWidth(120);
        }

        claimsTable.getColumnModel()
                .getColumn(2).setPreferredWidth(160);

        claimsTable.getColumnModel()
                .getColumn(4).setPreferredWidth(160);
    }

    private Item findItem(int itemId) {

        for (Item item : itemService.getAllItems()) {

            if (item.getId() == itemId) {
                return item;
            }
        }

        return null;
    }

    private void viewProof() {

        Claim claim = getSelectedClaim();

        if (claim == null) {
            return;
        }

        Item foundItem = findItem(claim.getItemId());

        Item lostItem = claim.getLostItemId() > 0
                ? findItem(claim.getLostItemId())
                : null;

        String foundName = foundItem != null
                ? foundItem.getItemName()
                : "Item not found";

        String lostName = lostItem != null
                ? lostItem.getItemName()
                : "Not linked";

        JOptionPane.showMessageDialog(
                this,
                "Claimant: " + claim.getClaimantName()
                        + "\nContact: " + claim.getContact()
                        + "\nFOUND Item: " + foundName
                        + "\nLOST Item: " + lostName
                        + "\n\nOwnership Proof:\n" + claim.getProof()
                        + "\n\nStatus: " + claim.getClaimStatus(),
                "Claim Ownership Proof",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateSelectedClaim(String newStatus) {

        Claim claim = getSelectedClaim();

        if (claim == null) {
            return;
        }

        if (!"PENDING".equalsIgnoreCase(claim.getClaimStatus())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only pending claims can be approved or rejected."
            );

            return;
        }

        String message = "Are you sure you want to "
                + newStatus.toLowerCase() + " this claim?";

        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Claim Decision",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        boolean updated = claimService.updateClaimStatus(
                claim.getId(),
                newStatus
        );

        if (updated) {

            JOptionPane.showMessageDialog(
                    this,
                    "Claim " + newStatus.toLowerCase()
                            + " successfully."
            );

            loadClaims();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update the claim. Check the linked items "
                            + "and their current statuses."
            );
        }
    }

    private Claim getSelectedClaim() {

        int selectedRow = claimsTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a claim first."
            );

            return null;
        }

        int claimId = (int) tableModel.getValueAt(selectedRow, 0);

        for (Claim claim : claimService.getAllClaims()) {

            if (claim.getId() == claimId) {
                return claim;
            }
        }

        return null;
    }
}
