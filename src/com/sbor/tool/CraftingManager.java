package com.sbor.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class CraftingManager {

    public Map<String, Item> allItems = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    // All category files inside resources/CraftingData/
    private final String[] categories = {
            "1H.json",
            "2H.json",
            "Rapier.json",
            "Dagger.json",
            "Melee.json",
            "Shields.json",
            "Ores.json",
            "Crafted_Materials.json",
            "Armor.json",
            "Lower_Headwear.json",
            "Upper_Headwear.json"
    };

    public CraftingManager() {
        loadAllItems();
    }

    // ------------------------------------------------
    // LOAD ALL JSON FILES FROM RESOURCES
    // ------------------------------------------------

    public void loadAllItems() {

        for (String fileName : categories) {

            System.out.println("Trying to load: /CraftingData/" + fileName);


            try (InputStream is =
                         getClass().getResourceAsStream("/CraftingData/" + fileName)) {

                File file = new File("CraftingData/" + fileName);

                if (!file.exists()) {
                    System.out.println("File NOT found: " + file.getAbsolutePath());
                    continue;
                }

                System.out.println("Loaded from disk: " + file.getAbsolutePath());

                List<Item> items =
                        mapper.readValue(file, new TypeReference<List<Item>>() {});

                for (Item item : items) {
                    allItems.put(item.name.toLowerCase(), item);
                }

            } catch (Exception e) {
                System.out.println("⚠ Error loading: " + fileName);
                e.printStackTrace();
            }
        }

        System.out.println("Total items loaded: " + allItems.size());

        System.out.println("Loaded item names:");
        for (String key : allItems.keySet()) {
            System.out.println("- " + key);
        }

    }

    // ------------------------------------------------
    // GET ITEM BY NAME (case insensitive)
    // ------------------------------------------------

    public Item getItem(String name) {
        if (name == null) return null;
        return allItems.get(name.trim().toLowerCase());
    }

    // ------------------------------------------------
    // GET BASE MATERIALS RECURSIVELY
    // ------------------------------------------------

    public Map<String, Integer> getBaseMaterials(String itemName, int quantity) {

        Map<String, Integer> result = new HashMap<>();

        Item item = getItem(itemName);
        if (item == null) return result;

        calculateMaterials(item, quantity, result);

        return result;
    }

    private void calculateMaterials(Item item,
                                    int quantity,
                                    Map<String, Integer> result) {

        // If no ingredients -> base material
        if (item.ingredients == null || item.ingredients.isEmpty()) {
            result.put(item.name,
                    result.getOrDefault(item.name, 0) + quantity);
            return;
        }

        for (Ingredient ingredient : item.ingredients) {

            Item subItem = getItem(ingredient.name);

            int totalQty = ingredient.quantity * quantity;

            if (subItem == null ||
                    subItem.ingredients == null ||
                    subItem.ingredients.isEmpty()) {

                result.put(ingredient.name,
                        result.getOrDefault(ingredient.name, 0) + totalQty);

            } else {

                calculateMaterials(subItem, totalQty, result);
            }
        }
    }
}
