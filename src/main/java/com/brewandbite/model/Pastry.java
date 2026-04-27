

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pastry extends MenuItem {

    @JsonProperty("pastryType")
    private String pastryType;   // e.g. "Croissant", "Muffin", "Cookie"

    @JsonProperty("variation")
    private String variation;    // e.g. "Butter", "Blueberry"

    public Pastry() {}

    public Pastry(String name, String pastryType, String variation, double basePrice) {
        super(name, "Pastry", basePrice);
        this.pastryType = pastryType;
        this.variation  = variation;
    }

    public String getPastryType() { return pastryType; }
    public void setPastryType(String v){ this.pastryType = v; }

    public String getVariation() { return variation; }
    public void setVariation(String v) { this.variation = v; }

    @Override
    public String getDisplayType() { return pastryType != null ? pastryType : "Pastry"; }

    @Override
    public String toString() { return getName(); }
}
