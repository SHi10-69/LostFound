
package com.smartlostfound.service;

import com.smartlostfound.model.Item;
import com.smartlostfound.storage.FileStorage;

import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private static final String FILE_NAME = "items.dat";

    // Get all items
    public List<Item> getAllItems() {

        return FileStorage.loadList(FILE_NAME);
    }

    // Add new item
    public void addItem(Item item) {

        List<Item> items = getAllItems();

        item.setId(generateNextId(items));

        items.add(item);

        saveItems(items);
    }

    // Update existing item
    public void updateItem(Item updatedItem) {

        List<Item> items = getAllItems();

        for (int i = 0; i < items.size(); i++) {

            if (items.get(i).getId() == updatedItem.getId()) {

                items.set(i, updatedItem);

                break;
            }
        }

        saveItems(items);
    }

    // Delete item
    public void deleteItem(int id) {

        List<Item> items = getAllItems();

        items.removeIf(item -> item.getId() == id);

        saveItems(items);
    }

    // Search items
    public List<Item> searchItems(String keyword) {

        List<Item> allItems = getAllItems();

        List<Item> results = new ArrayList<>();

        String search = keyword.toLowerCase().trim();

        for (Item item : allItems) {

            if (
                    (item.getItemName() != null &&
                            item.getItemName().toLowerCase().contains(search))

                            ||

                            (item.getCategory() != null &&
                                    item.getCategory().toLowerCase().contains(search))

                            ||

                            (item.getLocation() != null &&
                                    item.getLocation().toLowerCase().contains(search))

                            ||

                            (item.getDescription() != null &&
                                    item.getDescription().toLowerCase().contains(search))
            ) {

                results.add(item);
            }
        }

        return results;
    }

    // Generate next item ID
    private int generateNextId(List<Item> items) {

        int maxId = 0;

        for (Item item : items) {

            if (item.getId() > maxId) {

                maxId = item.getId();
            }
        }

        return maxId + 1;
    }

    // Count all reported items
    public int countItems() {

        return getAllItems().size();
    }

    // Count lost items
    public int countLostItems() {

        int count = 0;

        for (Item item : getAllItems()) {

            if ("LOST".equalsIgnoreCase(item.getItemType())) {

                count++;
            }
        }

        return count;
    }

    // Count found items
    public int countFoundItems() {

        int count = 0;

        for (Item item : getAllItems()) {

            if ("FOUND".equalsIgnoreCase(item.getItemType())) {

                count++;
            }
        }

        return count;
    }

    // Count pending items
    public int countPendingItems() {

        int count = 0;

        for (Item item : getAllItems()) {

            if ("PENDING".equalsIgnoreCase(item.getStatus())) {

                count++;
            }
        }

        return count;
    }

    public boolean updateItemStatus(int itemId, String newStatus) {

        List<Item> items = getAllItems();

        for (Item item : items) {

            if (item.getId() == itemId) {

                item.setStatus(newStatus);

                try {
                    FileStorage.saveList(FILE_NAME, items);

                    // Verify that the status was saved
                    List<Item> savedItems = getAllItems();

                    for (Item savedItem : savedItems) {
                        if (savedItem.getId() == itemId) {
                            return newStatus.equalsIgnoreCase(
                                    savedItem.getStatus()
                            );
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                return false;
            }
        }

        return false;
    }

    // Save items to local file
    private void saveItems(List<Item> items) {

        try {

            FileStorage.saveList(FILE_NAME, items);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
