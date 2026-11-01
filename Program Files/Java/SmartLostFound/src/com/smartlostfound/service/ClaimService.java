
package com.smartlostfound.service;

import com.smartlostfound.model.Claim;
import com.smartlostfound.storage.FileStorage;
import com.smartlostfound.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ClaimService {

    private static final String FILE_NAME = "claims.dat";
    private final ItemService itemService = new ItemService();

    // Get all submitted claims
    public List<Claim> getAllClaims() {
        return FileStorage.loadList(FILE_NAME);
    }

    // Submit a new claim
    public void addClaim(Claim claim) {

        List<Claim> claims = getAllClaims();

        claim.setId(generateNextId(claims));

        claims.add(claim);

        saveClaims(claims);
    }

    // Get claims for a particular item
    public List<Claim> getClaimsForItem(int itemId) {

        List<Claim> results = new ArrayList<>();

        for (Claim claim : getAllClaims()) {

            if (claim.getItemId() == itemId) {
                results.add(claim);
            }
        }

        return results;
    }



    public List<Claim> getClaimsByUsername(String username) {

        List<Claim> results = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            return results;
        }

        for (Claim claim : getAllClaims()) {

            if (claim.getClaimantUsername() != null
                    && claim.getClaimantUsername().trim()
                    .equalsIgnoreCase(username.trim())) {

                results.add(claim);
            }
        }

        return results;
    }

    // Get claims with a particular status
    public List<Claim> getClaimsByStatus(String status) {

        List<Claim> results = new ArrayList<>();

        for (Claim claim : getAllClaims()) {

            if (claim.getClaimStatus() != null
                    && claim.getClaimStatus().equalsIgnoreCase(status)) {

                results.add(claim);
            }
        }

        return results;
    }

    // Approve or reject a claim


    public boolean updateClaimStatus(int claimId, String newStatus) {

        List<Claim> claims = getAllClaims();

        Claim selectedClaim = null;

        // Find the selected claim
        for (Claim claim : claims) {
            if (claim.getId() == claimId) {
                selectedClaim = claim;
                break;
            }
        }

        if (selectedClaim == null) {
            return false;
        }

        // Only pending claims can be updated
        if (!"PENDING".equalsIgnoreCase(selectedClaim.getClaimStatus())) {
            return false;
        }

        // Validate requested status
        if (!"APPROVED".equalsIgnoreCase(newStatus)
                && !"REJECTED".equalsIgnoreCase(newStatus)) {
            return false;
        }

        // Handle approval
        if ("APPROVED".equalsIgnoreCase(newStatus)) {

            Item foundItem = null;
            Item lostItem = null;

            List<Item> items = itemService.getAllItems();

            // Find the exact FOUND item
            for (Item item : items) {
                if (item.getId() == selectedClaim.getItemId()) {
                    foundItem = item;
                    break;
                }
            }

            // Validate FOUND item
            if (foundItem == null
                    || !"FOUND".equalsIgnoreCase(foundItem.getItemType())
                    || "RESOLVED".equalsIgnoreCase(foundItem.getStatus())
                    || "CLOSED".equalsIgnoreCase(foundItem.getStatus())) {
                return false;
            }

            // Find the exact linked LOST item, if one was selected
            if (selectedClaim.getLostItemId() > 0) {

                for (Item item : items) {
                    if (item.getId() == selectedClaim.getLostItemId()) {
                        lostItem = item;
                        break;
                    }
                }

                // Validate linked LOST item
                if (lostItem == null
                        || !"LOST".equalsIgnoreCase(lostItem.getItemType())
                        || "RESOLVED".equalsIgnoreCase(lostItem.getStatus())
                        || "CLOSED".equalsIgnoreCase(lostItem.getStatus())) {
                    return false;
                }
            }

            // Resolve FOUND item
            if (!itemService.updateItemStatus(foundItem.getId(), "RESOLVED")) {
                return false;
            }

            // Resolve the exact linked LOST item
            if (lostItem != null) {

                if (!itemService.updateItemStatus(lostItem.getId(), "RESOLVED")) {

                    // Roll back FOUND item if LOST item update fails
                    itemService.updateItemStatus(foundItem.getId(), "PENDING");

                    return false;
                }
            }

            // Approve selected claim and reject other pending claims
            for (Claim claim : claims) {

                if (claim.getItemId() == selectedClaim.getItemId()
                        && "PENDING".equalsIgnoreCase(claim.getClaimStatus())) {

                    if (claim.getId() == selectedClaim.getId()) {
                        claim.setClaimStatus("APPROVED");
                    } else {
                        claim.setClaimStatus("REJECTED");
                    }
                }
            }

        } else {

            // Reject only the selected claim
            selectedClaim.setClaimStatus("REJECTED");
        }

        saveClaims(claims);

        return true;
    }

    // Generate the next claim ID

    // Count pending claims
    public int countPendingClaims() {
        int count = 0;

        for (Claim claim : getAllClaims()) {
            if ("PENDING".equalsIgnoreCase(claim.getClaimStatus())) {
                count++;
            }
        }

        return count;
    }

    // Count approved claims
    public int countApprovedClaims() {
        int count = 0;

        for (Claim claim : getAllClaims()) {
            if ("APPROVED".equalsIgnoreCase(claim.getClaimStatus())) {
                count++;
            }
        }

        return count;
    }

    private int generateNextId(List<Claim> claims) {

        int maxId = 0;

        for (Claim claim : claims) {

            if (claim.getId() > maxId) {
                maxId = claim.getId();
            }
        }

        return maxId + 1;
    }

    // Save claims to claims.dat
    private void saveClaims(List<Claim> claims) {

        try {

            FileStorage.saveList(FILE_NAME, claims);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
