import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BeverageAddition extends MenuItem{
	@JsonProperty("beverage")
	protected MenuItem beverage;
	
	@JsonProperty("applied")
	protected boolean applied = false;
	
	public BeverageAddition(MenuItem beverage, String name, String category, double basePrice) {
		super(name, category, basePrice);
		this.beverage = beverage;
		applied = true;
	}
	
	public void apply() {
		applied = true;
	}
	
	public void remove() {
		applied = false;
	}
	
	@Override
	public abstract double getTotal();
	
	@Override
	public abstract String getDescription();
	
	@Override
	public String getDisplayType() {
		// TODO Auto-generated method stub
		return "Bevarage Addition";
	}
	
	public abstract void setCharge(double charge);

}
