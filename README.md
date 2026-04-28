# ☕ Brew & Bite Café System

A JavaFX desktop application simulating a café ordering and management system.

---

## Project Structure

```
brew-and-bite/
├── pom.xml
└── src/
    └── main/
        ├── java/com/brewandbite/
        |   ├── Main.java                         # Application entry point
        │   ├── MainApp.java                      
        │   ├── model/
        │   │   ├── MenuItem.java                 # Abstract base (polymorphic JSON)
        │   │   ├── Beverage.java                 # Coffee / Tea items with sizes
        │   │   ├── Pastry.java                   # Croissant / Muffin / Cookie
        │   │   ├── Customization.java            # Add-on with extra cost
        │   │   ├── IngredientRequirement.java    # Item → ingredient mapping
        │   │   ├── Ingredient.java               # Inventory ingredient
        │   │   ├── Order.java                    # A customer's placed order
        │   │   ├── OrderItem.java                # One line in an order
        │   │   ├── UserRole.java                 # CUSTOMER | BARISTA | MANAGER
        │   │   └── AppData.java                  # Root JSON wrapper
        │   ├── service/
        │   │   ├── AuthService.java              # Hardcoded credential check
        │   │   ├── InventoryService.java         # Stock check & deduction
        │   │   ├── MenuService.java              # Observable menu CRUD
        │   │   ├── OrderService.java             # Place & update orders
        │   │   └── PersistenceService.java       # Load/save JSON via Jackson
        │   ├── controller/
        │   │   ├── LandingController.java        # Role selection screen
        │   │   ├── LoginController.java          # Barista / Manager login
        │   │   ├── CustomerController.java       # Browse, customise, order
        │   │   ├── BaristaController.java        # View & fulfil orders
        │   │   └── ManagerController.java        # Menu, inventory, sales
        │   └── util/
        │       ├── SceneManager.java             # FXML scene switching
        │       └── SessionStore.java             # Singleton app-wide state
        └── resources/com/brewandbite/
            ├── css/style.css                     # Coffee-themed stylesheet
            ├── data/seed_data.json               # Default menu & inventory
            └── view/
                ├── LandingView.fxml
                ├── LoginView.fxml
                ├── CustomerView.fxml
                ├── BaristaView.fxml
                └── ManagerView.fxml
```

---

## User Roles & Credentials

| Role     | Username   | Password     |
|----------|------------|--------------|
| Barista  | barista1   | barista123   |
| Barista  | barista2   | brew456      |
| Manager  | manager1   | manager123   |
| Manager  | admin      | admin2024    |

Customers do **not** need credentials — just enter a name at launch.

---

## Building & Running

### Prerequisites
- Java 21 LTS or newer
- Maven 3.8+

### Run in development
```bash
mvn javafx:run
```

### Build executable JAR
```bash
mvn clean package
java -jar target/brew-and-bite-1.0.0.jar
```

> **Note on JavaFX + fat JARs**: The `maven-shade-plugin` bundles all dependencies.
> On some systems you may need to pass JavaFX VM args explicitly:
> ```bash
> java --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
>      -jar target/brew-and-bite-1.0.0.jar
> ```

---

## Data Persistence

All application state is saved to `~/.brewandbite/appdata.json` on exit and
reloaded on startup. If no save file is found, `seed_data.json` is loaded from
the classpath to populate the initial menu and inventory.

---

## Key Design Decisions

| Concern | Approach |
|---------|----------|
| Polymorphic JSON | `@JsonTypeInfo` + `@JsonSubTypes` on `MenuItem` |
| Real-time UI updates | `ObservableList` in `OrderService` / `MenuService` |
| Inventory guard | `InventoryService.canMakeItem()` checked before adding to cart |
| Scene navigation | `SceneManager.switchTo(fxmlPath)` from any controller |
| Shared state | `SessionStore` singleton holds all services |
| No Canvas drawing | Pure JavaFX layout nodes (VBox, TableView, TabPane, etc.) |


### Sequence Diagrams 
### Diagram A: Customer Places Order
**Sequence Diagram A**
```mermaid
sequenceDiagram
    actor Customer
    participant UI as UserInterface
    participant BFS as Barista Brew System
    participant Menu as Menu
    participant TO as tempOrders
    participant Orders as Orders
    participant IN as Inventory
    %% Add more participants as needed

    %% Your diagram here
    Customer ->>UI: Picks Customer
    UI ->>Customer: input name
    Customer -->>UI: their name
    UI ->>BFS: createOrder(name)
    create participant Order
	BFS ->>Order: create(name)
	BFS ->>TO: addOrder(Order)
	BFS -->>UI:Result
	UI ->>BFS: getMenu()
	BFS ->>Menu: getItems()
	create participant IM as Iterator (MenuItems)
	Menu ->>IM: create()
	Menu -->>BFS: Iterator
	BFS -->>UI: Iterator
	loop until no more items to display
		UI ->>IM: getNext()
		IM -->>UI: MenuItem
		UI ->>Customer: menuItem
	end
	loop until customer is done adding items
		Customer ->>UI: adds item
		UI ->>BFS: addItemToOrder(itemID, orderID)
		create participant MI as MenuItem
		BFS ->>MI: create()
		BFS ->>IN: canMakeItem(menuItem)
		IN ->>BFS: true/false
		alt item can be made
			BFS ->>IN: deductIngredients(menuItem, 1)
			BFS ->>Order: addItem(menuItem)
			BFS -->>UI: result
			UI -->>Customer: confirmation
		else item can't be made
			BFS -->>UI: result
			UI -->>Customer: Item can't be made
		end
	end
	Customer ->>UI: Click place order
	UI ->>BFS: PlaceOrder(orderID)
	BFS ->>TO: getOrder(orderID)
	TO -->>BFS: order1
	BFS ->>Order: isEmpty()
	Order -->>BFS: true/false
	alt if order isn't empty
		BFS ->>TO: removeOrder(orderID)
		BFS ->>Orders: addOrder(order1)
	else order is empty
		BFS ->>TO: removeOrder(orderID)
	end
	BFS -->>UI: result
	UI -->>Customer: result
	
	
    
    
    
```

**Sequence Diagram B**
```mermaid
sequenceDiagram
    actor Customer
    participant UI as UserInterface
    participant BFS as Barista Brew System
    participant IN as Inventory
    participant Menu as Menu
    participant Orders as Orders
    %% Add more participants as needed

    %% Your diagram here
    Customer ->>UI: Picks Customer
    UI ->>Customer: input name
    Customer -->>UI: their name
    UI ->>BFS: getMenu()
	BFS ->>Menu: getItems()
	create participant IM as Iterator (MenuItems)
	Menu ->>IM: create()
	Menu -->>BFS: Iterator
	BFS -->>UI: Iterator
    loop until no more items to display
		UI ->>IM: getNext()
		IM -->>UI: MenuItem
		UI ->>Customer: menuItem
	end
	loop until customer is done adding items
		Customer ->>UI: adds item (gives the itemID)
	end
    Customer ->>UI: clicks place order button
    UI ->>BFS: placeOrder(name, itemList)
    opt item list contains items
	    create participant Order
		BFS ->>Order: create(name)
		loop until no more itemIDs or item can't be made
			create participant MI as MenuItem
			BFS ->>MI: create()
			BFS ->>IN: canMakeItem()
			IN ->>BFS: true/false
			alt item can be made
				BFS ->>IN: deductIngredients(menuItem)
				BFS ->>Order: addItem(menuItem)
			else item can't be made
				BFS -->>UI: result
				UI ->>Customer:result
			end
		end
		BFS ->>Orders: addOrder(Order)
	end
	BFS -->>UI:Result
	UI -->>Customer: confirmation
	
    
    
    
```



### Diagram B: Customer Places Order
**Sequence Diagram A**
```mermaid
sequenceDiagram
    actor Barista
    participant UI as UserInterface
    participant BFS as Barista Brew System
    participant O as Order (order1)
    participant Orders as Orders
    %% Add more participants as needed

    %% Your diagram here
    Barista ->>UI: change order status
    Barista -->>UI: orderID and new status
    UI ->>BFS: updateOrder(orderID, status)
    BFS ->>Orders: getOrder(orderID)
    Orders -->>BFS: order1
    BFS ->>Order: changeStatus(status)
    BFS -->>UI: result
    UI -->>Barista: result
	
	
    
    
    
```


### Diagram C: Manger restocks Ingredient
**Sequence Diagram A**
```mermaid
sequenceDiagram
    actor Manager
    participant UI as UserInterface
    participant BFS as Barista Brew System
    participant ingredient as Ingredient (ingredient1)
    participant IN as Inventory
    %% Add more participants as needed

    %% Your diagram here
    Manager ->>UI: picks Ingredient to restock with amount
    Manager -->>UI: Ingredient name and amount to restock
    UI ->>BFS: restockIngredient(ingredientName, amount)
    BFS ->>IN: restock(ingredientName, amount)
    IN ->>IN: findIngredint(ingredientName)
    IN -->>IN: ingredient
    IN ->>ingredient: getQuantity()
    ingredient ->>IN: quantity
    IN ->>ingredient: setQuantity(quantity + amount)
    BFS -->>UI: result
    UI -->>Manager: confirmation
```

