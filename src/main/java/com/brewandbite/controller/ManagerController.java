package com.brewandbite.controller;

import com.brewandbite.model.*;
import com.brewandbite.service.FactoryService;
import com.brewandbite.service.InventoryService;
import com.brewandbite.service.MenuService;
import com.brewandbite.service.OrderService;
import com.brewandbite.service.PersistenceService;
import com.brewandbite.util.SceneManager;
import com.brewandbite.util.SessionStore;
import com.brewandbite.model.MenuItem;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ManagerController {

    // Menu tab
    @FXML private TableView<MenuItem>           menuTable;
    @FXML private TableColumn<MenuItem, String> colId;
    @FXML private TableColumn<MenuItem, String> colName;
    @FXML private TableColumn<MenuItem, String> colCategory;
    @FXML private TableColumn<MenuItem, Double> colPrice;
    @FXML private TableColumn<MenuItem, Boolean> colAvail;
    @FXML private Label                         menuStatusLabel;

    // Inventory tab
    @FXML private TableView<Ingredient>           inventoryTable;
    @FXML private TableColumn<Ingredient, String> colIngName;
    @FXML private TableColumn<Ingredient, Double> colIngQty;
    @FXML private TableColumn<Ingredient, String> colIngUnit;
    @FXML private TextField                       restockNameField;
    @FXML private TextField                       restockAmountField;
    @FXML private Label                           inventoryStatusLabel;

    // Sales tab
    @FXML private TableView<Order>              salesTable;
    @FXML private TableColumn<Order, String>    colOrdId;
    @FXML private TableColumn<Order, String>    colOrdCustomer;
    @FXML private TableColumn<Order, String>    colOrdStatus;
    @FXML private TableColumn<Order, String>    colOrdTime;
    @FXML private TableColumn<Order, Double>    colOrdTotal;
    @FXML private Label                         totalSalesLabel;

    // Services
    private MenuService        menuService;
    private InventoryService   inventoryService;
    private OrderService       orderService;
    private PersistenceService persistenceService;

    // Lifecycle

    @FXML
    public void initialize() {
        SessionStore s = SessionStore.getInstance();
        menuService        = s.getMenuService();
        inventoryService   = s.getInventoryService();
        orderService       = s.getOrderService();
        persistenceService = s.getPersistenceService();

        setupMenuTable();
        setupInventoryTable();
        setupSalesTable();
    }

    // Table setup

    private void setupMenuTable() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getId()));
        colName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        colCategory.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCategory()));
        colPrice.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getBasePrice()).asObject());
        colAvail.setCellValueFactory(c ->
                new SimpleBooleanProperty(c.getValue().isAvailable()).asObject());
        menuTable.setItems(menuService.getAllItems());
    }

    private void setupInventoryTable() {
        colIngName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        colIngQty.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getQuantity()).asObject());
        colIngUnit.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getUnit()));
        inventoryTable.setItems(inventoryService.getInventory());
    }

    private void setupSalesTable() {
        colOrdId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getOrderId()));
        colOrdCustomer.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomerName()));
        colOrdStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));
        colOrdTime.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTimestamp()));
        colOrdTotal.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getTotalPrice()).asObject());
        salesTable.setItems(orderService.getOrders());

        orderService.getOrders().addListener(
                (javafx.collections.ListChangeListener<Order>) c -> refreshTotalSales());
        refreshTotalSales();
    }

    private void refreshTotalSales() {
        double total = orderService.getOrders().stream()
                .filter(o -> o.getStatus() == Order.Status.FULFILLED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
        totalSalesLabel.setText("Total Fulfilled Sales: USD " + String.format("%.2f", total));
    }

    // Menu CRUD handlers

    @FXML
    private void handleAddMenuItem() {
    	showMenuItemAddDialog();
    }

    @FXML
    private void handleEditMenuItem() {
        MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item to edit."); return; }
        showMenuItemEditDialog(sel);
    }
    
    @FXML
    private void handleEditItemRequirements() {
    	MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item to edit Ingredient Requirements."); return; }
        showMenuItemReqDialog(sel);
    }

    @FXML
    private void handleRemoveMenuItem() {
        MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item to remove."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove \"" + sel.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Remove");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                menuService.removeItem(sel.getId());
                menuStatusLabel.setText("\"" + sel.getName() + "\" removed.");
            }
        });
    }

    @FXML
    private void handleToggleAvailability() {
        MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null) { menuStatusLabel.setText("Select a menu item."); return; }
        sel.setAvailable(!sel.isAvailable());
        menuService.updateItem(sel);
        menuTable.refresh();
        menuStatusLabel.setText(sel.getName() + " availability: " + sel.isAvailable());
    }
    
    @FXML
    private void handleEditCustomizations() {
    	MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null || !sel.getCategory().equals("Beverage")) { menuStatusLabel.setText("Select a Beverage menu item."); return; }
        showEditCustomizationDialog( (Beverage) sel);
    }
    
    @FXML
    private void handleAddCustomization() {
    	MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null || !sel.getCategory().equals("Beverage")) { menuStatusLabel.setText("Select a Beverage menu item."); return; }
        showAddCustomizationDialog( (Beverage) sel);
    }
    
    @FXML
    private void handleRemoveCustomization() {
    	MenuItem sel = menuTable.getSelectionModel().getSelectedItem();
        if (sel == null || !sel.getCategory().equals("Beverage")) { menuStatusLabel.setText("Select a Beverage menu item."); return; }
        showRemoveCustomizationDialog( (Beverage) sel);
    }

    // Inventory handler

    @FXML
    private void handleRestock() {
        String name   = restockNameField.getText().trim();
        String amtStr = restockAmountField.getText().trim();

        if (name.isEmpty() || amtStr.isEmpty()) {
            inventoryStatusLabel.setText("Enter both ingredient name and amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException("non-positive");
        } catch (NumberFormatException ex) {
            inventoryStatusLabel.setText("Amount must be a positive number.");
            return;
        }

        inventoryService.restock(name, amount);
        saveAll();
        inventoryTable.refresh();
        inventoryStatusLabel.setText("Restocked " + name + " by " + amount + ".");
        restockNameField.clear();
        restockAmountField.clear();
    }
     

    // Add dialog
    
    //edit item dialog
    private void showMenuItemEditDialog(MenuItem existing) {
    	//create start of the dialog
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Menu Item");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //text fields
        TextField nameField    = new TextField();
        TextField priceField   = new TextField();
        TextField subTypeField = new TextField();
        TextField smallBevField = new TextField();
        TextField mediumBevField = new TextField();
        TextField largeBevField = new TextField();
        TextField variationPatField = new TextField();
        
        //give smallbev, medium, and large text field default values
        smallBevField.setText("0.0");
        mediumBevField.setText("0.0");
        largeBevField.setText("0.0");
        
        //Labels
        Label nameLabel = new Label("Name:");
        Label priceLabel = new Label("Base Price:");
        Label subTypeLabel = new Label("Sub-type:");
        Label varPatLabel = new Label("Variation");
        Label smallBevLabel = new Label("Small Size Cost: ");
        Label mediumBevLabel = new Label("Medium Size Cost: ");
        Label largeBevLabel = new Label("Large Size Cost: ");
        
        //create grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefSize(600, 600);
        grid.setMaxSize(600, 600);
        

        //add to grid
        grid.add(nameLabel,     0, 0); grid.add(nameField,  1, 0);
        grid.add(priceLabel , 0, 1); grid.add(priceField, 1, 1);
        grid.add(subTypeLabel, 0, 2); grid.add(subTypeField,1, 2);
        
        //set up the text fields
        nameField.setText(existing.getName());
        priceField.setText(String.valueOf(existing.getBasePrice()));
        
        if (existing instanceof Beverage b) {
        	subTypeField.setText(b.getBeverageType());
        	Map<String, Double> sizePrices = b.getSizePrices();
        	smallBevField.setText(String.valueOf(sizePrices.get("Small"))); 
        	mediumBevField.setText(String.valueOf(sizePrices.get("Medium"))); 
        	largeBevField.setText(String.valueOf(sizePrices.get("Large"))); 
        	grid.add(smallBevLabel, 0, 3);  grid.add(smallBevField, 1, 3);
        	grid.add(mediumBevLabel, 0, 4); grid.add(mediumBevField, 1, 4);
        	grid.add(largeBevLabel, 0, 5);  grid.add(largeBevField, 1, 5);
        }
        else if (existing instanceof Pastry p) {
        	subTypeField.setText(p.getPastryType());
        	variationPatField.setText(p.getVariation());
        	grid.add(varPatLabel, 0, 3);  grid.add(variationPatField, 1, 3);
        }
        
        dialog.getDialogPane().setContent(grid);
        
        //when a button is pressed
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null; //if it isn't ok return null
            
            //process input into usable input
            String name = nameField.getText().trim();
            if (name.isEmpty()) return null;
            double price;
            try { price = Double.parseDouble(priceField.getText().trim()); }
            catch (NumberFormatException e) { return null; }
            String subType = subTypeField.getText().trim();
            

            //get the variation
            String variation = variationPatField.getText().trim();
            
            //get size prices
            Map<String, Double> sizes = new LinkedHashMap<>();
            double sizePrice = 0.0;
            try { sizePrice = Double.parseDouble(smallBevField.getText().trim()); }
            catch (NumberFormatException e) { return null; }
            sizes.put("Small", sizePrice);
            sizes.put("Medium", sizePrice);
            try { sizePrice = Double.parseDouble(mediumBevField.getText().trim()); }
            catch (NumberFormatException e) { return null;}
            sizes.put("Large", sizePrice);
            try { sizePrice = Double.parseDouble(largeBevField.getText().trim()); }
            catch (NumberFormatException e) { return null;} 
          
            //existing.setBasePrice(price);
            MenuItemRequest request = new MenuItemRequest();
            request.setName(name);
            request.setBasePrice(price);
            request.setSubType(subType);
            request.setVariation(variation);
            request.setSizePrices(sizes);
            
            //edit the item
            existing.editItem(request);
            return existing;
        });
        
        dialog.showAndWait().ifPresent(item -> {
            menuService.updateItem(existing);
            menuStatusLabel.setText("Updated: " + item.getName());
            menuTable.refresh();
        });
    }

    private void showMenuItemAddDialog() {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Add Menu Item");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //text fields
        List<TextField> ingredientCostsTF = new LinkedList<>(); //linked list to hold the ingredient cost text fields
        TextField nameField    = new TextField();
        TextField priceField   = new TextField();
        TextField subTypeField = new TextField();
        TextField smallBevField = new TextField();
        TextField mediumBevField = new TextField();
        TextField largeBevField = new TextField();
        TextField variationPatField = new TextField();
        
        //give smallbev, medium, and large text field default values
        smallBevField.setText("0.0");
        mediumBevField.setText("0.0");
        largeBevField.setText("0.0");
        
        //Labels
        Label typeComboLabel = new Label("Type:");
        Label nameLabel = new Label("Name:");
        Label priceLabel = new Label("Base Price:");
        Label subTypeLabel = new Label("Sub-type:");
        Label varPatLabel = new Label("Variation");
        Label smallBevLabel = new Label("Small Size Cost: ");
        Label mediumBevLabel = new Label("Medium Size Cost: ");
        Label largeBevLabel = new Label("Large Size Cost: ");
        
        //create grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPrefSize(600, 600);
        grid.setMaxSize(600, 600);

        ComboBox<String> typeCombo  = new ComboBox<>();
        typeCombo.getItems().addAll("Beverage", "Pastry");
        grid.add(typeComboLabel,     0, 0); grid.add(typeCombo,  1, 0);
        grid.add(nameLabel,     0, 1); grid.add(nameField,  1, 1);
        grid.add(priceLabel ,0,2); grid.add(priceField, 1, 2);
        grid.add(subTypeLabel, 0, 3); grid.add(subTypeField,1,3);

        //set up ingredients
        List<Ingredient> ingredients = inventoryService.getInventoryAsList();
        List<IngredientRequirement> ingredientReqs = new LinkedList<>(); 
        
        //set up the x to add to grid
        int x = 5;
        
        //add ingredients to grid
        for(Ingredient ingredient: ingredients) {
        	grid.add(new Label(ingredient.getName() + " Cost: "),     0, x);
        	TextField temp = new TextField();
        	temp.setText("0.0"); //default value
        	
        	//add ingredient requirement
        	ingredientReqs.add(new IngredientRequirement(ingredient.getName(), 0.0));
        	
        	//add ingredient cost to TF
        	ingredientCostsTF.add(temp);
        	
        	//add text field to grid
        	grid.add(temp, 1, x);
        	
        	//increase x
        	x++;
        }
        
        
        typeCombo.setOnAction(e -> {
        	//get children
        	ObservableList<Node> children = grid.getChildren();
        	int rowCount;
            switch (typeCombo.getValue()) {
              case "Beverage":
            	//remove variation and TextField
            	children.remove(varPatLabel);
            	children.remove(variationPatField);
            	//children.remo
            	//remove variation and text
            	
            	//add beverage options
            	rowCount = grid.getRowCount();
            	grid.add(smallBevLabel, 0, rowCount); grid.add(smallBevField, 1, rowCount);
            	grid.add(mediumBevLabel, 0, rowCount + 1); grid.add(mediumBevField, 1, rowCount + 1);
            	grid.add(largeBevLabel, 0, rowCount + 2); grid.add(largeBevField, 1, rowCount + 2);
                break;
              case "Pastry":
            	//remove sizes
              	children.remove(smallBevLabel); children.remove(smallBevField);
              	children.remove(mediumBevLabel); children.remove(mediumBevField);
              	children.remove(largeBevLabel); children.remove(largeBevField);
              	
              	//get rowCount
              	rowCount = grid.getRowCount();
              	
              	//add pastry options
              	grid.add(varPatLabel, 0, rowCount); grid.add(variationPatField, 1, rowCount);
                break;
            }
          });
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
        	//process inputs
            if (btn != ButtonType.OK) return null;
            String name = nameField.getText().trim();
            if (name.isEmpty()) return null;
            double price;
            try { price = Double.parseDouble(priceField.getText().trim()); }
            catch (NumberFormatException e) { return null; }
            String subType = subTypeField.getText().trim();
            
            //get the variation
            String variation = variationPatField.getText().trim();
            
            //get size prices
            Map<String, Double> sizes = new LinkedHashMap<>();
            double sizePrice = 0.0;
            try { sizePrice = Double.parseDouble(smallBevField.getText().trim()); }
            catch (NumberFormatException e) { return null; }
            sizes.put("Small", sizePrice);
            sizes.put("Medium", sizePrice);
            try { sizePrice = Double.parseDouble(mediumBevField.getText().trim()); }
            catch (NumberFormatException e) { return null;}
            sizes.put("Large", sizePrice);
            try { sizePrice = Double.parseDouble(largeBevField.getText().trim()); }
            catch (NumberFormatException e) { return null;} 
          
            //get ingredient requirements
            Iterator<IngredientRequirement> inReqIterator = ingredientReqs.iterator();
        	Iterator<TextField> newInReqCostTFIterator = ingredientCostsTF.iterator();
        	
            //iterate through the two iterators
            while(inReqIterator.hasNext() && newInReqCostTFIterator.hasNext()) {
            	double ingredientCost; //the new ingredient cost amount
            	TextField costTextField = newInReqCostTFIterator.next();
            	IngredientRequirement ir = inReqIterator.next();
            	
            	//try to get the value
            	try { ingredientCost = Double.parseDouble(costTextField.getText().trim()); }
                catch (NumberFormatException e) { return null; }
            	
            	//update the ingredient costs
            	ir.setAmount(ingredientCost);
            }
            
            MenuItemRequest request = new MenuItemRequest();
            request.setType(typeCombo.getValue());
            request.setName(name);
            request.setBasePrice(price);
            request.setSubType(subType);
            request.setVariation(variation);
            request.setSizePrices(sizes);
            request.setIngredientRequirement(ingredientReqs);
            
            FactoryService fs = FactoryService.getInstance();
            return fs.createItem(request);


        });
        
        dialog.showAndWait().ifPresent(item -> {
            menuService.addItem(item);
            menuStatusLabel.setText("Added: " + item.getName());

            menuTable.refresh();
        });
    }
  
    //handles how the requirement edit dialog
    private void showMenuItemReqDialog(MenuItem existing) {
    	Dialog<MenuItem> dialog = new Dialog<>();
    	dialog.setTitle("Edit " + existing.getName() + " Ingredient Cost");
    	List<TextField> newIngredientCostsTF = new LinkedList<>(); //linked list to hold the ingredient cost text fields
    	dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    	 
    	//set up the pane
    	GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
         
        
        //get the current ingredient requirement
        List<IngredientRequirement> ingredientRequirements = existing.getIngredientRequirements();
        
        //add the labels
        //add ingredients to grid
        int x  = 0;
        for(IngredientRequirement ingredient: ingredientRequirements) {
        	grid.add(new Label(ingredient.getIngredientName() + " Amount: "),     0, x);
        	
        	//create text field
        	TextField tf = new TextField(String.valueOf(ingredient.getAmount()));
        	
        	//add to grid
        	grid.add(tf, 1, x);
        	
        	//add to list
        	newIngredientCostsTF.add(tf);
        	
        	x++;
        }
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null; //if the button isn't ok just return null
            
            Iterator<IngredientRequirement> inReqIterator = ingredientRequirements.iterator();
        	Iterator<TextField> newInReqCostTFIterator = newIngredientCostsTF.iterator();
        	
            //iterate through the two iterators
            while(inReqIterator.hasNext() && newInReqCostTFIterator.hasNext()) {
            	double ingredientCost; //the new ingredient cost amount
            	TextField costTextField = newInReqCostTFIterator.next();
            	IngredientRequirement ir = inReqIterator.next();
            	
            	//try to get the value
            	try { ingredientCost = Double.parseDouble(costTextField.getText().trim()); }
                catch (NumberFormatException e) { return null; }
            	
            	//update the ingredient
            	existing.editRequirement(ir.getIngredientName(), ingredientCost);
            }
            return existing;

        });
        
        dialog.showAndWait().ifPresent(item -> {
            menuService.updateItem(item);
            menuStatusLabel.setText("Updated: " + existing.getName());
            menuTable.refresh();
        });
         
    }
    
    //Customizations
    //handles the editing of customizations dialog
    private void showEditCustomizationDialog(Beverage existing) {
    	//create start of the dialog
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Customizations");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //set up the Grid pane
    	GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
         
        //text field of the customizations
        List<TextField> custsPriceTF = new LinkedList<>();
        
        //get current customizations to display
        List<Customization> custs = existing.getAvailableCustomizations();
        
        //add textFields
        int x = 0;
        for(Customization cust: custs) {
        	
        	//add label
        	grid.add(new Label(cust.getName()), 0, x);
        	
        	//create temp text field for price of customization and add to lists
        	TextField temp = new TextField(Double.toString(cust.getExtraCost()));
        	custsPriceTF.add(temp);
        	grid.add(temp, 1, x);
        	
        	//increase x
        	x++;
        }
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null; //if the button isn't ok just return null
            
            Iterator<Customization> custsIterator = custs.iterator();
        	Iterator<TextField> custsPriceTFIterator = custsPriceTF.iterator();
        	
            //iterate through the two iterators
            while(custsIterator.hasNext() && custsPriceTFIterator.hasNext()) {
            	double newCost; //the new ingredient cost amount
            	TextField costTextField = custsPriceTFIterator.next();
            	Customization cust = custsIterator.next();
            	
            	//try to get the value
            	try { newCost = Double.parseDouble(costTextField.getText().trim()); }
                catch (NumberFormatException e) { return null; }
            	
            	//update the cost
            	existing.editCustomizationPrice(cust.getName(), newCost);
            }
            return existing;

        });
        
        dialog.showAndWait().ifPresent(item -> {
            menuService.updateItem(item);
            menuStatusLabel.setText("Updated: " + existing.getName() + " Customizations");
            menuTable.refresh();
        });

        
        
    }
    
    private void showAddCustomizationDialog(Beverage existing) {
    	//create start of the dialog
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Add Customization");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        //text field
        TextField nameTF = new TextField();
        TextField costTF = new TextField();
        
       
        //create grid
        GridPane grid = new GridPane();
        
        //add to grid
        grid.add(new Label("Customization Name: "), 0, 0);		grid.add(nameTF, 1, 0);
        grid.add(new Label("Customization Cost: "), 0, 1);		grid.add(costTF, 1, 1);
        
        //add to dialog
        dialog.getDialogPane().setContent(grid);
        
        //
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null; //if the button isn't ok just return null
            
            //process input into usable input
            String name = nameTF.getText().trim();
            if (name.isEmpty()) return null;
            double price;
            try { price = Double.parseDouble(costTF.getText().trim()); }
            catch (NumberFormatException e) { return null; }
            
            Customization customization = new Customization(name, price);
            existing.addCustomization(customization);
            
            
            return existing;

        });
        
        dialog.showAndWait().ifPresent(item -> {
            menuService.updateItem(item);
            menuStatusLabel.setText("Updated: " + existing.getName() + " Customizations");
            menuTable.refresh();
        });
    }
    
    private void showRemoveCustomizationDialog(Beverage existing) {
    	//create start of the dialog
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Removal of Customization", ButtonType.YES, ButtonType.NO);
    	confirm.setTitle("Remove Customization");
    	confirm.setHeaderText("Please choose an option from the list:");
    
        
        //comboBox
        ComboBox<Customization> customizations = new ComboBox<>();
        
        //create grid
        GridPane grid = new GridPane();
        
        if(existing.getAvailableCustomizations().isEmpty()) {
        	grid.add(new Label("ITEM HAS NO CUSTOMIZATIONS"), 0, 0);
        } else {
        	List<Customization> custs = existing.getAvailableCustomizations();
        	for(Customization temp: custs) {
        		customizations.getItems().add(temp);
        		grid.add(new Label("Customization: "),     0, 0); grid.add(customizations,  1, 0);
        	}
        }
        
        
        
        confirm.getDialogPane().setContent(grid);

        //handle alert
        confirm.showAndWait().ifPresent(btn -> {
        	Customization cust = customizations.getValue();
            if (btn == ButtonType.YES) {
                existing.removeCustomization(cust.getName());
                menuService.updateItem(existing);
                menuStatusLabel.setText("\"" + cust.getName() + "\" removed from " + existing.getName() + ".");
            }
        });
       
        
        
    }
    
    // Logout

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("/com/brewandbite/view/LandingView.fxml", "Brew & Bite");
    }

    // Helpers

    private void saveAll() {
        AppData data = new AppData();
        data.setMenuItems(new ArrayList<>(menuService.getAllItems()));
        data.setIngredients(inventoryService.getInventoryAsList());
        data.setOrders(new ArrayList<>(orderService.getOrders()));
        persistenceService.saveData(data);
    }
}
