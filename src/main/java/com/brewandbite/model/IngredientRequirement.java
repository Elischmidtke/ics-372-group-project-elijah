

//import com.fasterxml.jackson.annotation.JsonProperty;

public class IngredientRequirement {

    //@JsonProperty("ingredientName")
    private String ingredientName;

    //@JsonProperty("amount")
    private double amount;

    public IngredientRequirement() {}

    public IngredientRequirement(String ingredientName, double amount) {
        this.ingredientName = ingredientName;
        this.amount = amount;
    }

    public String getIngredientName() { return ingredientName; }
    public void   setIngredientName(String v){ this.ingredientName = v; }

    public double getAmount() { return amount; }
    public void setAmount(double v) { this.amount = v; }
}
