

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {

   @JsonProperty("name")
    private String name;

   @JsonProperty("quantity")
    private double quantity;

    @JsonProperty("unit")
    private String unit;

    public Ingredient() {}

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public void   setName(String v) { this.name = v; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double v) { this.quantity = v; }

    public String getUnit() { return unit; }
    public void   setUnit(String v) { this.unit = v; }

    @Override
    public String toString() {
        return String.format("%s (%.1f %s)", name, quantity, unit);
    }
}
