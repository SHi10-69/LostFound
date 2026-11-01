package com.smartlostfound.ui;

import com.smartlostfound.model.Item;
import com.smartlostfound.service.ItemService;

import javax.swing.*;
import java.awt.*;

public class EditItemDialog extends JDialog {

    private final Item item;
    private final ItemService itemService;

    private JTextField nameField;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> typeComboBox;
    private JTextField locationField;
    private JTextField ownerField;
    private JTextField contactField;
    private JSpinner quantitySpinner;
    private JTextArea descriptionArea;
    private JSlider urgencySlider;
    private JComboBox<String> statusComboBox;

    public EditItemDialog(
            Window parent,
            Item item,
            ItemService itemService) {

        super(
                parent,
                "Edit Item",
                ModalityType.APPLICATION_MODAL
        );

        this.item = item;
        this.itemService = itemService;

        createGUI();

        loadItemData();

        setSize(700, 650);

        setLocationRelativeTo(parent);
    }


    private void createGUI() {

        setLayout(
                new BorderLayout(10, 10)
        );


        JLabel title =
                new JLabel(
                        "EDIT ITEM",
                        SwingConstants.CENTER
                );


        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );


        add(
                title,
                BorderLayout.NORTH
        );


        JPanel form =
                new JPanel(
                        new GridBagLayout()
                );


        GridBagConstraints gbc =
                new GridBagConstraints();


        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );


        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        // NAME

        addField(
                form,
                gbc,
                0,
                "Item Name:",
                nameField =
                        new JTextField()
        );


        // CATEGORY

        categoryComboBox =
                new JComboBox<>(
                        new String[]{
                                "Electronics",
                                "Documents",
                                "Wallet",
                                "Keys",
                                "Clothing",
                                "Jewellery",
                                "Books",
                                "Bags",
                                "Other"
                        }
                );


        addField(
                form,
                gbc,
                1,
                "Category:",
                categoryComboBox
        );


        // TYPE

        typeComboBox =
                new JComboBox<>(
                        new String[]{
                                "Lost",
                                "Found"
                        }
                );


        addField(
                form,
                gbc,
                2,
                "Type:",
                typeComboBox
        );


        // LOCATION

        addField(
                form,
                gbc,
                3,
                "Location:",
                locationField =
                        new JTextField()
        );


        // OWNER

        addField(
                form,
                gbc,
                4,
                "Owner:",
                ownerField =
                        new JTextField()
        );


        // CONTACT

        addField(
                form,
                gbc,
                5,
                "Contact:",
                contactField =
                        new JTextField()
        );


        // QUANTITY

        quantitySpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                1,
                                1,
                                100,
                                1
                        )
                );


        addField(
                form,
                gbc,
                6,
                "Quantity:",
                quantitySpinner
        );


        // STATUS

        statusComboBox =
                new JComboBox<>(
                        new String[]{
                                "Reported",
                                "Claimed",
                                "Resolved"
                        }
                );


        addField(
                form,
                gbc,
                7,
                "Status:",
                statusComboBox
        );


        // URGENCY

        urgencySlider =
                new JSlider(
                        0,
                        100,
                        50
                );


        urgencySlider.setMajorTickSpacing(25);

        urgencySlider.setPaintTicks(true);

        urgencySlider.setPaintLabels(true);


        addField(
                form,
                gbc,
                8,
                "Urgency:",
                urgencySlider
        );


        // DESCRIPTION

        descriptionArea =
                new JTextArea(
                        5,
                        30
                );


        descriptionArea.setLineWrap(true);

        descriptionArea.setWrapStyleWord(true);


        JScrollPane descriptionScrollPane =
                new JScrollPane(
                        descriptionArea
                );


        addField(
                form,
                gbc,
                9,
                "Description:",
                descriptionScrollPane
        );


        add(
                new JScrollPane(form),
                BorderLayout.CENTER
        );


        // BUTTONS

        JPanel buttons =
                new JPanel(
                        new FlowLayout()
                );


        JButton saveButton =
                new JButton("SAVE");


        JButton cancelButton =
                new JButton("CANCEL");


        buttons.add(saveButton);

        buttons.add(cancelButton);


        add(
                buttons,
                BorderLayout.SOUTH
        );


        saveButton.addActionListener(
                e -> saveChanges()
        );


        cancelButton.addActionListener(
                e -> dispose()
        );
    }


    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;

        panel.add(
                new JLabel(label),
                gbc
        );


        gbc.gridx = 1;
        gbc.weightx = 0.8;

        panel.add(
                component,
                gbc
        );
    }


    private void loadItemData() {

        nameField.setText(
                item.getItemName()
        );


        categoryComboBox.setSelectedItem(
                item.getCategory()
        );


        typeComboBox.setSelectedItem(
                item.getItemType()
        );


        locationField.setText(
                item.getLocation()
        );


        ownerField.setText(
                item.getOwnerName()
        );


        contactField.setText(
                item.getContact()
        );


        quantitySpinner.setValue(
                item.getQuantity()
        );


        statusComboBox.setSelectedItem(
                item.getStatus()
        );


        urgencySlider.setValue(
                item.getUrgency()
        );


        descriptionArea.setText(
                item.getDescription()
        );
    }


    private void saveChanges() {

        if (
                nameField
                        .getText()
                        .trim()
                        .isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Item name cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (
                locationField
                        .getText()
                        .trim()
                        .isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Location cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        item.setItemName(
                nameField
                        .getText()
                        .trim()
        );


        item.setCategory(
                (String)
                        categoryComboBox
                                .getSelectedItem()
        );


        item.setItemType(
                (String)
                        typeComboBox
                                .getSelectedItem()
        );


        item.setLocation(
                locationField
                        .getText()
                        .trim()
        );


        item.setOwnerName(
                ownerField
                        .getText()
                        .trim()
        );


        item.setContact(
                contactField
                        .getText()
                        .trim()
        );


        item.setQuantity(
                (Integer)
                        quantitySpinner
                                .getValue()
        );


        item.setStatus(
                (String)
                        statusComboBox
                                .getSelectedItem()
        );


        item.setUrgency(
                urgencySlider.getValue()
        );


        item.setDescription(
                descriptionArea
                        .getText()
                        .trim()
        );


        itemService.updateItem(item);


        JOptionPane.showMessageDialog(
                this,
                "Item updated successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );


        dispose();
    }
}