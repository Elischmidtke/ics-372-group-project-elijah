

import com.fasterxml.jackson.annotation.JsonProperty;
//not needed 
public class Customization {

    @JsonProperty("name")
    private String name;

    @JsonProperty("extraCost")
    private double extraCost;

    public Customization() {}

    public Customization(String name, double extraCost) {
        this.name = name;
        this.extraCost = extraCost;
    }

    public String getName() { return name; }
    public void   setName(String v) { this.name = v; }

    public double getExtraCost() { return extraCost; }
    public void   setExtraCost(double v){ this.extraCost = v; }

    @Override
    public String toString() {
        return name + " (+USD " + String.format("%.0f", extraCost) + ")";
    }
}
