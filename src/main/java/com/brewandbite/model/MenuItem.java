
import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;



@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Beverage.class, name = "beverage"),
   @JsonSubTypes.Type(value = Pastry.class,   name = "pastry")
})
public abstract class MenuItem {
	@JsonProperty("idGen")
	private static int idGen = 0;
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("basePrice")
    private double basePrice;

    @JsonProperty("available")
    private boolean available = true;

   @JsonProperty("ingredientRequirements")
    private List<IngredientRequirement> ingredientRequirements = new ArrayList<>();

    public MenuItem() {
    	this.id = "MI" + idGen++;
    }

    public MenuItem(String name, String category, double basePrice) {
    	this.id = "MI" + idGen++;
        this.name = name;
        this.category = category;
       this.basePrice = basePrice;
    }

    public String getId() { return id; }
    public void setId(String v) { this.id = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }

    public double  getBasePrice() { return basePrice; }
    public void setBasePrice(double v) { this.basePrice = v; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean v) { this.available = v; }
    
    //addition for decorator
    public double getTotal() {
    	return basePrice;
    }
    
    public String getDescription() {
    	return name;
    }

    public List<IngredientRequirement> getIngredientRequirements() { return ingredientRequirements; }
    public void setIngredientRequirements(List<IngredientRequirement> v) { this.ingredientRequirements = v; }

    /** Short label used in UI cards and type columns. */
    public abstract String getDisplayType();
}
