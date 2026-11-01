
package com.smartlostfound.ui;

import com.smartlostfound.model.Item;
import com.smartlostfound.model.User;
import com.smartlostfound.service.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class BrowseItemsPanel extends JPanel {

    private User loggedInUser;
    private ItemService itemService;

    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> typeFilter;

    private JTable itemTable;
    private DefaultTableModel tableModel;

    private List<Item> allItems;

    // Constructor
    public BrowseItemsPanel(User loggedInUser) {

        this.loggedInUser = loggedInUser;

        itemService = new ItemService();

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        createSearchPanel();
        createTable();
        createButtonPanel();

        loadItems();
    }

    // Search and filter panel
    private void createSearchPanel() {

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        searchField = new JTextField(18);

        categoryFilter = new JComboBox<>(new String[]{
                "All Categories",
                "Electronics",
                "Books",
                "ID Card",
                "Wallet",
                "Keys",
                "Clothing",
                "Bags",
                "Other"
        });

        typeFilter = new JComboBox<>(new String[]{
                "All Types",
                "LOST",
                "FOUND"
        });

        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);

        searchPanel.add(new JLabel("Type:"));
        searchPanel.add(typeFilter);

        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        searchButton.addActionListener(e -> applyFilters());

        resetButton.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            typeFilter.setSelectedIndex(0);
            applyFilters();
        });

        searchField.addActionListener(e -> applyFilters());

        categoryFilter.addActionListener(e -> applyFilters());
        typeFilter.addActionListener(e -> applyFilters());

        add(searchPanel, BorderLayout.NORTH);
    }

    // Create items table
    private void createTable() {

        String[] columns = {
                "ID",
                "Item Name",
                "Category",
                "Type",
                "Location",
                "Contact",
                "Status",
                "Urgency"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemTable = new JTable(tableModel);

        itemTable.setRowHeight(25);

        itemTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        itemTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(itemTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    // Bottom buttons
    private void createButtonPanel() {

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton viewButton = new JButton("View Details");
        JButton editButton = new JButton("Edit My Item");
        JButton deleteButton = new JButton("Delete Item");
        JButton statusButton = new JButton("Update Status");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);

        // Admin-only management buttons
        if (loggedInUser != null &&
                "ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {

            buttonPanel.add(deleteButton);
            buttonPanel.add(statusButton);
        }

        buttonPanel.add(refreshButton);

        viewButton.addActionListener(e -> viewSelectedItem());
        editButton.addActionListener(e -> editSelectedItem());
        deleteButton.addActionListener(e -> deleteSelectedItem());
        statusButton.addActionListener(e -> updateSelectedStatus());
        refreshButton.addActionListener(e -> loadItems());

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Load items from items.dat
    private void loadItems() {

        allItems = itemService.getAllItems();

        if (allItems == null) {
            allItems = new ArrayList<>();
        }

        applyFilters();
    }

    // Search and filter records
    private void applyFilters() {

        if (allItems == null) {
            return;
        }

        tableModel.setRowCount(0);

        String keyword = searchField.getText()
                .trim()
                .toLowerCase();

        String selectedCategory =
                (String) categoryFilter.getSelectedItem();

        String selectedType =
                (String) typeFilter.getSelectedItem();

        for (Item item : allItems) {

            String name = safe(item.getItemName());
            String category = safe(item.getCategory());
            String location = safe(item.getLocation());
            String description = safe(item.getDescription());
            String type = safe(item.getItemType());

            // Keyword search
            boolean matchesKeyword =
                    keyword.isEmpty()
                            || name.toLowerCase().contains(keyword)
                            || category.toLowerCase().contains(keyword)
                            || location.toLowerCase().contains(keyword)
                            || description.toLowerCase().contains(keyword);

            // Category filter
            boolean matchesCategory =
                    "All Categories".equals(selectedCategory)
                            || selectedCategory.equalsIgnoreCase(category);

            // Lost / Found filter
            boolean matchesType =
                    "All Types".equals(selectedType)
                            || selectedType.equalsIgnoreCase(type);

            if (matchesKeyword && matchesCategory && matchesType) {

                tableModel.addRow(new Object[]{
                        item.getId(),
                        name,
                        category,
                        type,
                        location,
                        safe(item.getContact()),
                        safe(item.getStatus()),
                        getUrgencyText(item.getUrgency())
                });
            }
        }
    }

    // View complete item details
    private void viewSelectedItem() {

        Item selectedItem = getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        String details =
                "Item ID: " + selectedItem.getId() +
                        "\nItem Name: " + safe(selectedItem.getItemName()) +
                        "\nCategory: " + safe(selectedItem.getCategory()) +
                        "\nType: " + safe(selectedItem.getItemType()) +
                        "\nLocation: " + safe(selectedItem.getLocation()) +
                        "\nOwner Name: " + safe(selectedItem.getOwnerName()) +
                        "\nContact: " + safe(selectedItem.getContact()) +
                        "\nDescription: " + safe(selectedItem.getDescription()) +
                        "\nAccessories: " + safe(selectedItem.getAccessories()) +
                        "\nQuantity: " + selectedItem.getQuantity() +
                        "\nUrgency: " + getUrgencyText(selectedItem.getUrgency()) +
                        "\nStatus: " + safe(selectedItem.getStatus()) +
                        "\nDate: " + selectedItem.getItemDate();

        JTextArea detailsArea = new JTextArea(details);

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(detailsArea);

        scrollPane.setPreferredSize(new Dimension(400, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Item Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Convert urgency number into readable text
    private String getUrgencyText(int urgency) {

        switch (urgency) {
            case 1:
                return "LOW";

            case 2:
                return "MEDIUM";

            case 3:
                return "HIGH";

            default:
                return String.valueOf(urgency);
        }
    }

    // Get selected item from table
    private Item getSelectedItem() {

        int selectedRow = itemTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item first."
            );

            return null;
        }

        int modelRow = itemTable.convertRowIndexToModel(selectedRow);

        int selectedId = Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString()
        );

        for (Item item : allItems) {

            if (item.getId() == selectedId) {
                return item;
            }
        }

        return null;
    }

    // Delete selected item (Admin)
    private void deleteSelectedItem() {

        Item item = getSelectedItem();

        if (item == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            itemService.deleteItem(item.getId());

            JOptionPane.showMessageDialog(
                    this,
                    "Item deleted successfully!"
            );

            loadItems();
        }
    }

    // Update item status (Admin)
    private void updateSelectedStatus() {

        Item item = getSelectedItem();

        if (item == null) {
            return;
        }

        String[] statuses = {
                "PENDING",
                "APPROVED",
                "REJECTED",
                "RESOLVED"
        };

        String newStatus = (String) JOptionPane.showInputDialog(
                this,
                "Select new status:",
                "Update Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                item.getStatus()
        );

        if (newStatus != null) {

            boolean updated = itemService.updateItemStatus(
                    item.getId(),
                    newStatus
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Status updated successfully!"
                );

                loadItems();
            }
        }
    }

    // Edit own reported item (temporary version)

    private void editSelectedItem() {

        Item item = getSelectedItem();

        if (item == null) {
            return;
        }

        // Check item ownership
        if (loggedInUser == null ||
                item.getReportedByUsername() == null ||
                !item.getReportedByUsername().equals(
                        loggedInUser.getUsername())) {

            JOptionPane.showMessageDialog(
                    this,
                    "You can edit only the items reported by you."
            );
            return;
        }

        // Create editing fields
        JTextField nameField =
                new JTextField(safe(item.getItemName()));

        JComboBox<String> categoryBox = new JComboBox<>(
                new String[]{
                        "Electronics", "Books", "ID Card",
                        "Wallet", "Keys", "Clothing",
                        "Bags", "Other"
                }
        );

        categoryBox.setEditable(true);
        categoryBox.setSelectedItem(item.getCategory());

        JComboBox<String> typeBox = new JComboBox<>(
                new String[]{"LOST", "FOUND"}
        );

        typeBox.setSelectedItem(item.getItemType());

        JTextField locationField =
                new JTextField(safe(item.getLocation()));

        JTextField ownerField =
                new JTextField(safe(item.getOwnerName()));

        JTextField contactField =
                new JTextField(safe(item.getContact()));

        JTextArea descriptionArea =
                new JTextArea(safe(item.getDescription()), 4, 20);

        JTextField accessoriesField =
                new JTextField(safe(item.getAccessories()));

        JSpinner quantitySpinner =
                new JSpinner(new SpinnerNumberModel(
                        Math.max(1, item.getQuantity()),
                        1, 100, 1
                ));

        JComboBox<String> urgencyBox = new JComboBox<>(
                new String[]{"LOW", "MEDIUM", "HIGH"}
        );

        urgencyBox.setSelectedItem(
                getUrgencyText(item.getUrgency())
        );

        // Create form
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Item Name:"));
        form.add(nameField);

        form.add(new JLabel("Category:"));
        form.add(categoryBox);

        form.add(new JLabel("Item Type:"));
        form.add(typeBox);

        form.add(new JLabel("Location:"));
        form.add(locationField);

        form.add(new JLabel("Owner Name:"));
        form.add(ownerField);

        form.add(new JLabel("Contact:"));
        form.add(contactField);

        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(descriptionArea));

        form.add(new JLabel("Accessories:"));
        form.add(accessoriesField);

        form.add(new JLabel("Quantity:"));
        form.add(quantitySpinner);

        form.add(new JLabel("Urgency:"));
        form.add(urgencyBox);

        JScrollPane formScrollPane = new JScrollPane(form);

        formScrollPane.setPreferredSize(new Dimension(450, 400));
        formScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        formScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                formScrollPane,
                "Edit My Item",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Validate input
        String itemName = nameField.getText().trim();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();

        if (itemName.isEmpty() ||
                location.isEmpty() ||
                contact.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Item name, location and contact are required."
            );
            return;
        }

        // Update existing item
        item.setItemName(itemName);
        item.setCategory((String) categoryBox.getSelectedItem());
        item.setItemType((String) typeBox.getSelectedItem());
        item.setLocation(location);
        item.setOwnerName(ownerField.getText().trim());
        item.setContact(contact);
        item.setDescription(descriptionArea.getText().trim());
        item.setAccessories(accessoriesField.getText().trim());
        item.setQuantity((Integer) quantitySpinner.getValue());

        String urgency = (String) urgencyBox.getSelectedItem();

        switch (urgency) {
            case "LOW":
                item.setUrgency(1);
                break;
            case "MEDIUM":
                item.setUrgency(2);
                break;
            case "HIGH":
                item.setUrgency(3);
                break;
        }

        // Save changes
        itemService.updateItem(item);

        JOptionPane.showMessageDialog(
                this,
                "Item updated successfully!"
        );

        loadItems();
    }


    // Prevent null values from causing errors
    private String safe(String value) {

        return value == null ? "" : value;
    }
}
