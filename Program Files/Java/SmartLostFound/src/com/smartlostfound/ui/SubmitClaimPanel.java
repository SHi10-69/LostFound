
package com.smartlostfound.ui;

import com.smartlostfound.model.Claim;
import com.smartlostfound.model.Item;
import com.smartlostfound.model.User;
import com.smartlostfound.service.ClaimService;
import com.smartlostfound.service.ItemService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class SubmitClaimPanel extends JPanel {

    private final User loggedInUser;
    private final ItemService itemService;
    private final ClaimService claimService;

    private JComboBox<Item> itemBox;
    private JComboBox<Item> lostItemBox;

    private JTextField nameField;
    private JTextField contactField;
    private JTextArea proofArea;

    public SubmitClaimPanel(User loggedInUser) {

        this.loggedInUser = loggedInUser;
        itemService = new ItemService();
        claimService = new ClaimService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        createHeading();
        createForm();

        loadFoundItems();
        loadLostItems();
    }

    private void createHeading() {

        JLabel heading = new JLabel("Submit Item Claim");
        heading.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel subtitle = new JLabel(
                "Select a found item and link your lost item if available."
        );

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
    }

    private void createForm() {

        JPanel form = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        itemBox = new JComboBox<>();
        lostItemBox = new JComboBox<>();

        nameField = new JTextField(20);
        nameField.setText(loggedInUser.getFullName());

        contactField = new JTextField(20);

        proofArea = new JTextArea(6, 20);
        proofArea.setLineWrap(true);
        proofArea.setWrapStyleWord(true);

        addFormRow(form, gbc, 0, "Found Item:", itemBox);
        addFormRow(form, gbc, 1, "Your Lost Item:", lostItemBox);
        addFormRow(form, gbc, 2, "Claimant Name:", nameField);
        addFormRow(form, gbc, 3, "Contact Number:", contactField);

        JScrollPane proofScroll = new JScrollPane(proofArea);

        addFormRow(form, gbc, 4, "Ownership Proof:", proofScroll);

        JButton submitButton = new JButton("Submit Claim");
        JButton clearButton = new JButton("Clear Form");

        submitButton.addActionListener(e -> submitClaim());
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(submitButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        form.add(buttons, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(component, gbc);
    }

    private void loadFoundItems() {

        itemBox.removeAllItems();

        List<Item> items = itemService.getAllItems();

        for (Item item : items) {

            if ("FOUND".equalsIgnoreCase(item.getItemType())
                    && !"RESOLVED".equalsIgnoreCase(item.getStatus())
                    && !"CLOSED".equalsIgnoreCase(item.getStatus())) {

                itemBox.addItem(item);
            }
        }

        itemBox.setRenderer(createItemRenderer("Select a found item"));

        itemBox.setEnabled(itemBox.getItemCount() > 0);
    }

    private void loadLostItems() {

        lostItemBox.removeAllItems();

        // First option: no linked LOST item
        lostItemBox.addItem(null);

        List<Item> items = itemService.getAllItems();

        for (Item item : items) {

            if ("LOST".equalsIgnoreCase(item.getItemType())
                    && loggedInUser.getUsername().equalsIgnoreCase(
                    item.getReportedByUsername())
                    && !"RESOLVED".equalsIgnoreCase(item.getStatus())
                    && !"CLOSED".equalsIgnoreCase(item.getStatus())) {

                lostItemBox.addItem(item);
            }
        }

        lostItemBox.setRenderer(createItemRenderer("No linked LOST item"));

        lostItemBox.setSelectedIndex(0);
    }

    private DefaultListCellRenderer createItemRenderer(String emptyText) {

        return new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Item) {

                    Item item = (Item) value;

                    setText(
                            "ID: " + item.getId()
                                    + " - " + item.getItemName()
                                    + " (" + item.getLocation() + ")"
                    );

                } else {
                    setText(emptyText);
                }

                return this;
            }
        };
    }

    private void submitClaim() {

        Item selectedItem = (Item) itemBox.getSelectedItem();
        Item selectedLostItem = (Item) lostItemBox.getSelectedItem();

        String claimantName = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String proof = proofArea.getText().trim();

        if (selectedItem == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No available found items to claim."
            );

            return;
        }

        if (claimantName.isEmpty()
                || contact.isEmpty()
                || proof.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all fields."
            );

            return;
        }

        // Prevent duplicate pending claims
        for (Claim existing : claimService.getClaimsForItem(
                selectedItem.getId())) {

            if (existing.getClaimantUsername() != null
                    && existing.getClaimantUsername().equalsIgnoreCase(
                    loggedInUser.getUsername())
                    && "PENDING".equalsIgnoreCase(
                    existing.getClaimStatus())) {

                JOptionPane.showMessageDialog(
                        this,
                        "You already have a pending claim for this item."
                );

                return;
            }
        }

        Claim claim = new Claim();

        claim.setItemId(selectedItem.getId());

        // Link the exact LOST item selected by this user
        if (selectedLostItem != null) {
            claim.setLostItemId(selectedLostItem.getId());
        } else {
            claim.setLostItemId(0);
        }

        claim.setClaimantName(claimantName);
        claim.setClaimantUsername(loggedInUser.getUsername());

        claim.setContact(contact);
        claim.setProof(proof);
        claim.setClaimStatus("PENDING");
        claim.setClaimDate(new Date());

        claimService.addClaim(claim);

        JOptionPane.showMessageDialog(
                this,
                "Your claim has been submitted successfully.\n"
                        + "It is waiting for admin approval.",
                "Claim Submitted",
                JOptionPane.INFORMATION_MESSAGE
        );

        clearForm();
    }

    private void clearForm() {

        nameField.setText(loggedInUser.getFullName());
        contactField.setText("");
        proofArea.setText("");

        if (itemBox.getItemCount() > 0) {
            itemBox.setSelectedIndex(0);
        }

        lostItemBox.setSelectedIndex(0);
    }
}
