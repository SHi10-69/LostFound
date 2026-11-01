
package com.smartlostfound.ui;

import com.smartlostfound.model.Item;
import com.smartlostfound.service.ItemService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import com.smartlostfound.model.User;
public class AddItemPanel extends JPanel {
    private User loggedInUser;
    private JTextField itemNameField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> typeBox;
    private JTextField locationField;
    private JTextField ownerField;
    private JTextField contactField;
    private JTextArea descriptionArea;
    private JTextField accessoriesField;
    private JSpinner quantitySpinner;
    private JComboBox<String> urgencyBox;

    private ItemService itemService;

    public AddItemPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;

        itemService = new ItemService();

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Report Lost or Found Item",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 23));

        add(heading, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        itemNameField = new JTextField(20);

        categoryBox = new JComboBox<>(new String[]{
                "Electronics",
                "Books",
                "ID Card",
                "Wallet",
                "Keys",
                "Clothing",
                "Bags",
                "Other"
        });

        typeBox = new JComboBox<>(new String[]{
                "LOST",
                "FOUND"
        });

        locationField = new JTextField(20);

        ownerField = new JTextField(20);

        contactField = new JTextField(20);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        accessoriesField = new JTextField(20);

        quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, 100, 1)
        );

        urgencyBox = new JComboBox<>(new String[]{
                "LOW",
                "MEDIUM",
                "HIGH"
        });

        int row = 0;

        addRow(formPanel, gbc, row++, "Item Name:", itemNameField);
        addRow(formPanel, gbc, row++, "Category:", categoryBox);
        addRow(formPanel, gbc, row++, "Item Type:", typeBox);
        addRow(formPanel, gbc, row++, "Location:", locationField);
        addRow(formPanel, gbc, row++, "Your Name:", ownerField);
        addRow(formPanel, gbc, row++, "Contact Number:", contactField);

        addRow(
                formPanel,
                gbc,
                row++,
                "Description:",
                new JScrollPane(descriptionArea)
        );

        addRow(
                formPanel,
                gbc,
                row++,
                "Accessories:",
                accessoriesField
        );

        addRow(
                formPanel,
                gbc,
                row++,
                "Quantity:",
                quantitySpinner
        );

        addRow(formPanel, gbc, row++, "Urgency:", urgencyBox);

        JButton submitButton = new JButton("Submit Report");
        JButton clearButton = new JButton("Clear Form");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        submitButton.addActionListener(e -> submitItem());

        clearButton.addActionListener(e -> clearForm());

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void addRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent component
    ) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        panel.add(component, gbc);
    }

    private void submitItem() {

        String itemName = itemNameField.getText().trim();
        String location = locationField.getText().trim();
        String owner = ownerField.getText().trim();
        String contact = contactField.getText().trim();

        String description = descriptionArea.getText().trim();

        if (itemName.isEmpty()
                || location.isEmpty()
                || owner.isEmpty()
                || contact.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all required fields."
            );

            return;
        }

        if (!contact.matches("\\d{10}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid 10-digit contact number."
            );

            return;
        }

        Item item = new Item();
        item.setReportedByUsername(loggedInUser.getUsername());

        item.setItemName(itemName);

        item.setCategory(
                (String) categoryBox.getSelectedItem()
        );

        item.setItemType(
                (String) typeBox.getSelectedItem()
        );

        item.setLocation(location);

        item.setOwnerName(owner);

        item.setContact(contact);

        item.setDescription(description);

        item.setAccessories(accessoriesField.getText().trim());

        item.setQuantity(
                (Integer) quantitySpinner.getValue()
        );

        String urgency = (String) urgencyBox.getSelectedItem();

        int urgencyValue;

        switch (urgency) {
            case "LOW":
                urgencyValue = 1;
                break;

            case "MEDIUM":
                urgencyValue = 2;
                break;

            case "HIGH":
                urgencyValue = 3;
                break;

            default:
                urgencyValue = 1;
        }

        item.setUrgency(urgencyValue);
        item.setStatus("PENDING");

        item.setItemDate(new Date());

        item.setImagePath("");

        try {

            itemService.addItem(item);

            JOptionPane.showMessageDialog(
                    this,
                    "Item reported successfully!"
            );

            clearForm();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving item: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {

        itemNameField.setText("");
        locationField.setText("");
        ownerField.setText("");
        contactField.setText("");
        descriptionArea.setText("");
        accessoriesField.setText("");

        categoryBox.setSelectedIndex(0);
        typeBox.setSelectedIndex(0);
        urgencyBox.setSelectedIndex(0);

        quantitySpinner.setValue(1);
    }
}
