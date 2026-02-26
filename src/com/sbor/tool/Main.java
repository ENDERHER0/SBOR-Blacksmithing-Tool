package com.sbor.tool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class Main extends Application {

    private CraftingManager manager;
    private VBox materialList;
    private TextField searchField;
    private Spinner<Integer> qtySpinner;

    private Map<String, Integer> counters = new HashMap<>();
    private File counterFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("SBOR-Blacksmithing-Tool");

        stage.getIcons().add(
                new Image("file:resources/icon.png")
        );

        manager = new CraftingManager();

        // Counter file in user home
        File dataFolder = new File(System.getProperty("user.home"), "SBOR-Blacksmithing-Tool");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        counterFile = new File(dataFolder, "counters.json");
        System.out.println("Counter file path: " + counterFile.getAbsolutePath());


        loadCounters();

        // ---------------------------
        // TOP SEARCH + QTY (UNCHANGED)
        // ---------------------------

        searchField = new TextField();
        searchField.setPromptText("Search Item");

        qtySpinner = new Spinner<>(1, 999, 1);

        HBox topBar = new HBox(10, searchField, qtySpinner);
        topBar.setPadding(new Insets(10, 10, 15, 10));

        // ---------------------------
        // MATERIAL LIST
        // ---------------------------

        materialList = new VBox(5);

        ScrollPane scrollPane = new ScrollPane(materialList);
        scrollPane.setFitToWidth(true);

        scrollPane.setMinHeight(0);
        scrollPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        VBox root = new VBox(topBar, scrollPane);

        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.show();

        // Initial load
        updateSearch();

        // Search on Enter
        searchField.setOnAction(e -> updateSearch());

        // Qty change triggers refresh
        qtySpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateSearch());

        stage.setOnCloseRequest(e -> saveCounters());
    }

    // ---------------------------
    // SEARCH LOGIC
    // ---------------------------

    private void updateSearch() {

        materialList.getChildren().clear();


        String search = searchField.getText().trim();
        int qty = qtySpinner.getValue();

        if (search.isEmpty()) return;

        Item item = manager.getItem(search.trim());

        if (item == null) {
            materialList.getChildren().add(new Label("No Item Found"));
            return;
        }

        Map<String, Integer> materials =
                manager.getBaseMaterials(item.name, qty);

        for (Map.Entry<String, Integer> entry : materials.entrySet()) {

            String materialName = entry.getKey();
            int requiredAmount = entry.getValue();

            materialList.getChildren().add(
                    createMaterialRow(materialName, requiredAmount)
            );
        }
    }

    // ---------------------------
    // MATERIAL ROW (UNCHANGED LAYOUT)
    // Left: Name
    // Center: Current
    // Right: + -
    // ---------------------------

    private HBox createMaterialRow(String name, int required) {
        // Outer row container
        HBox row = new HBox();
        row.setPadding(new Insets(5));
        row.setSpacing(10);
        row.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        row.setAlignment(Pos.CENTER_LEFT);

        // Left: Material name + required quantity
        Label nameLabel = new Label(name + " x" + required);

        // Spacer pushes right-side controls to far right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right group container
        HBox rightGroup = new HBox(5); // spacing between current / + / -
        rightGroup.setAlignment(Pos.CENTER_RIGHT);

        // Current count label
        int currentCount = counters.getOrDefault(name, 0);
        Label currentLabel = new Label("Current: " + currentCount);
        currentLabel.setMinWidth(130);         // keeps width consistent
        currentLabel.setAlignment(Pos.CENTER); // centers text inside label

        // Plus / minus buttons
        Button plusBtn = new Button("+");
        Button minusBtn = new Button("-");

        plusBtn.setOnAction(e -> {
            counters.put(name, counters.getOrDefault(name, 0) + 1);
            currentLabel.setText("Current: " + counters.get(name));
        });

        minusBtn.setOnAction(e -> {
            counters.put(name, Math.max(0, counters.getOrDefault(name, 0) - 1));
            currentLabel.setText("Current: " + counters.get(name));
        });

        // Clear button
        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            counters.put(name, 0);                 // reset counter
            currentLabel.setText("Current: 0");    // update label
        });

        // Add controls to right group
        rightGroup.getChildren().addAll(currentLabel, plusBtn, minusBtn, clearBtn);

        // Add all to row: name | spacer | rightGroup
        row.getChildren().addAll(nameLabel, spacer, rightGroup);

        return row;
    }


    // ---------------------------
    // COUNTER SAVE / LOAD
    // ---------------------------

    private void loadCounters() {
        try {
            if (!counterFile.exists()) {
                counterFile.createNewFile();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            counters = mapper.readValue(counterFile, HashMap.class);

        } catch (Exception e) {
            counters = new HashMap<>();
        }
    }

    private void saveCounters() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(counterFile, counters);
        } catch (Exception ignored) {}
    }
}
