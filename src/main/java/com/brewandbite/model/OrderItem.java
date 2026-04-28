

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A single line within a customer order.
 * Stores only scalar values so the object serialises to JSON without
 * circular references back to the MenuItem catalogue.
 */
public class OrderItem {

    @JsonProperty("menuItemId")
    private String menuItemId;

    @JsonProperty("menuItemName")
    private String menuItemName;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("selectedSize")
    private String selectedSize;

    @JsonProperty("selectedCustomizations")
    private List<String> selectedCustomizations = new ArrayList<>();

    @JsonProperty("unitPrice")
    private double unitPrice;

    public OrderItem() {}

    public OrderItem(String menuItemId, String menuItemName, int quantity,
                     String selectedSize, List<String> customizations, double unitPrice) {
        this.menuItemId             = menuItemId;
        this.menuItemName           = menuItemName;
        this.quantity               = quantity;
        this.selectedSize           = selectedSize;
        this.selectedCustomizations = customizations != null ? customizations : new ArrayList<>();
        this.unitPrice              = unitPrice;
    }

    /** Total price for this line = unitPrice × quantity. */
    public double getLineTotal() { return unitPrice * quantity; }

    /** Human-readable description for the order summary panel. */
    public String getDisplayDescription() {
        StringBuilder sb = new StringBuilder(menuItemName);
        if (selectedSize != null && !selectedSize.isBlank())
            sb.append(" (").append(selectedSize).append(")");
        if (!selectedCustomizations.isEmpty())
            sb.append(" + ").append(String.join(", ", selectedCustomizations));
        return sb.toString();
    }

    public String getMenuItemId() { return menuItemId; }
    public void setMenuItemId(String v) { this.menuItemId = v; }

    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String v) { this.menuItemName = v; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { this.quantity = v; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String v) { this.selectedSize = v; }

    public List<String> getSelectedCustomizations() { return selectedCustomizations; }
    public void setSelectedCustomizations(List<String> v) { this.selectedCustomizations = v; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double v) { this.unitPrice = v; }
}
